package io.github.invvk.mony.listener;

import io.github.invvk.mony.MonyBootstrap;
import io.github.invvk.mony.config.properties.ConfigProperty;
import io.github.invvk.mony.config.properties.bean.MobBean;
import io.github.invvk.mony.config.properties.bean.MonyMob;
import io.github.invvk.mony.hook.VaultHook;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
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

        if (bootstrap.isTestEnvironment())
            bean.getMobMap().put(EntityType.GIANT.name(),
                    MonyMob.builder().price(monyMob.getPrice()).build());

        if (hook == null || monyMob.getPrice() < 0)
            return;

        hook.getEconomy().depositPlayer(killer, monyMob.getPrice());
    }

}
