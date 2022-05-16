package io.github.invvk.mony.internal.listener;

import io.github.invvk.actionbar.api.ActionBarAPI;
import io.github.invvk.mony.api.config.properties.ConfigProperty;
import io.github.invvk.mony.api.config.properties.MessagesProperty;
import io.github.invvk.mony.api.config.properties.bean.MobBean;
import io.github.invvk.mony.api.config.properties.bean.MonyMob;
import io.github.invvk.mony.api.events.PlayerKillMobEvent;
import io.github.invvk.mony.internal.MonyBootstrap;
import io.github.invvk.mony.internal.hook.VaultHook;
import io.github.invvk.mony.internal.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

@RequiredArgsConstructor
public class MobKillListener implements Listener {

    private final MonyBootstrap bootstrap;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKill(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        if (killer == null)
            return;

        final MobBean bean = bootstrap.getConfigManager().getConfig().getProperty(ConfigProperty.MOB_PRICE);
        final MonyMob monyMob = bean.getMob(entity.getType());
        final VaultHook hook = this.bootstrap.getVault();

        if (hook == null || monyMob.getPrice() < 0)
            return;

        double toDeposit = monyMob.getPrice();

        if (bootstrap.isDailyLimitEnabled()) {
            final PlayerKillMobEvent customEvent = new PlayerKillMobEvent(killer, entity, monyMob.getPrice());

            Bukkit.getPluginManager().callEvent(customEvent);

            if (customEvent.isCancelled())
                return;

            toDeposit = customEvent.getAmount();
        }

        hook.getEconomy().depositPlayer(killer, toDeposit);

        if (!this.bootstrap.isTestEnvironment())
            ActionBarAPI.send(killer, Utils.color(bootstrap.getConfigManager()
                .getMessage().getProperty(MessagesProperty.MOB_KILL_ACTIONBAR).replace("{AMOUNT}",
                        String.valueOf(toDeposit)).replace("{MOB}", StringUtils.
                        capitalize(entity.getType().name().toLowerCase().replace("_", " ")))));
    }

}
