package io.github.invvk.mony.test.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.invvk.mony.internal.MonyLoader;
import io.github.invvk.mony.internal.config.properties.ConfigProperty;
import io.github.invvk.mony.internal.config.properties.bean.MobBean;
import io.github.invvk.mony.internal.config.properties.bean.MonyMob;
import io.github.invvk.mony.internal.hook.VaultHook;
import io.github.invvk.mony.internal.listener.MobKillListener;
import io.github.invvk.mony.test.TestEconomy;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TestMobKillListener {

    private static ServerMock server;
    private static MonyLoader plugin;

    private static MobKillListener listener;

    @BeforeAll
    static void intiAll() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(MonyLoader.class);
        listener = new MobKillListener(plugin.getBootstrap());
        plugin.getBootstrap().setVault(new VaultHook(new TestEconomy()));
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unload();
    }

    @Test
    public void checkBeanProperty() {
        final MobBean bean = plugin.getBootstrap().getConfigManager().getConfig().getProperty(ConfigProperty.MOB_PRICE);
        assertNotNull(bean, "bean can't be null");

        final MonyMob zombie = bean.getMob(EntityType.ZOMBIE);
        assertNotNull(zombie, "MonyMob can't be null even if the mob doesn't exists in the configuration");

        assertNotEquals(zombie.getPrice(), bean.getDefaultPrice());
        assertNotEquals(zombie.getMultiplier(), bean.getComboMultiplier());

        final MonyMob bat = bean.getMob(EntityType.BAT);
        assertEquals(bat.getPrice(), bean.getDefaultPrice());
        assertEquals(bat.getMultiplier(), bean.getComboMultiplier());
    }

    @Test
    public void checkOnKill() {
        final LivingEntity entity = Mockito.mock(LivingEntity.class);
        final Player player = server.addPlayer();
        final MobBean bean = plugin.getBootstrap().getConfigManager().getConfig().getProperty(ConfigProperty.MOB_PRICE);
        final MonyMob expected = bean.getMob(EntityType.ZOMBIE);
        final EntityDeathEvent deathEvent = new EntityDeathEvent(entity, Collections.emptyList());

        // when the method entity#getType() is called, Mockito will return EntityType.ZOMBIE
        // Because entity is being mocked with no actual data.
        Mockito.when(entity.getType()).thenReturn(EntityType.ZOMBIE);

        // make sure that when entity#getKiller() gets called, it references the newly created player
        Mockito.when(entity.getKiller()).thenReturn(player);
        listener.onKill(deathEvent);

        assertEquals(expected.getPrice(), plugin.getBootstrap().getVault().getEconomy().getBalance(player));
    }

}
