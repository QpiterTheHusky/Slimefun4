package io.github.thebusybiscuit.slimefun4.testing.tests.items.implementations.food;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.DietCookie;
import io.github.thebusybiscuit.slimefun4.testing.TestUtilities;
import io.github.thebusybiscuit.slimefun4.testing.interfaces.SlimefunItemTest;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class TestDietCookie implements SlimefunItemTest<DietCookie> {

    private static ServerMock server;
    private static SlimefunPlugin plugin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(SlimefunPlugin.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Override
    public DietCookie registerSlimefunItem(SlimefunPlugin plugin, String id) {
        SlimefunItemStack item = new SlimefunItemStack(id, Material.COOKIE, "&5Test Cookie");
        DietCookie cookie = new DietCookie(TestUtilities.getCategory(plugin, "diet_cookie"), item, RecipeType.NULL, new ItemStack[9]);
        cookie.register(plugin);
        return cookie;
    }

    @Test
    @Disabled("Potion Effects are currently not fully implemented in MockBukkit")
    public void testConsumptionBehaviour() {
        PlayerMock player = server.addPlayer();
        DietCookie cookie = registerSlimefunItem(plugin, "TEST_DIET_COOKIE");

        simulateConsumption(player, cookie);

        player.assertSoundHeard(Sound.ENTITY_GENERIC_EAT);
        Assertions.assertTrue(player.hasPotionEffect(PotionEffectType.LEVITATION));
    }

}
