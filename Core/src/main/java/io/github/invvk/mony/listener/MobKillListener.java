package io.github.invvk.mony.listener;

import io.github.invvk.mony.MonyBootstrap;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
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

    }

}
