package io.github.thebusybiscuit.slimefun4.testing.tests.profiles;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.testing.TestUtilities;

public class TestPlayerBackpacks {

    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        MockBukkit.load(SlimefunPlugin.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    public void testCreateBackpack() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        Assertions.assertFalse(profile.isDirty());

        PlayerBackpack backpack = profile.createBackpack(18);

        Assertions.assertNotNull(backpack);

        // Creating a backpack should mark profiles as dirty
        Assertions.assertTrue(profile.isDirty());

        Assertions.assertEquals(profile, backpack.getOwner());
        Assertions.assertEquals(18, backpack.getSize());
        Assertions.assertEquals(18, backpack.getInventory().getSize());
    }

    @Test
    public void testChangeSize() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        PlayerBackpack backpack = profile.createBackpack(9);

        Assertions.assertThrows(IllegalArgumentException.class, () -> profile.createBackpack(-9));
        Assertions.assertThrows(IllegalArgumentException.class, () -> profile.createBackpack(12));
        Assertions.assertThrows(IllegalArgumentException.class, () -> profile.createBackpack(3000));

        Assertions.assertThrows(IllegalArgumentException.class, () -> backpack.setSize(-9));
        Assertions.assertThrows(IllegalArgumentException.class, () -> backpack.setSize(12));
        Assertions.assertThrows(IllegalArgumentException.class, () -> backpack.setSize(3000));

        backpack.setSize(27);

        Assertions.assertEquals(27, backpack.getSize());
        Assertions.assertTrue(profile.isDirty());
    }

    @Test
    public void testGetBackpackById() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        PlayerBackpack backpack = profile.createBackpack(9);
        int id = backpack.getId();

        Assertions.assertThrows(IllegalArgumentException.class, () -> profile.getBackpack(-20));

        Optional<PlayerBackpack> optional = profile.getBackpack(id);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(backpack, optional.get());

        Assertions.assertFalse(profile.getBackpack(500).isPresent());
    }

    @Test
    public void testLoadBackpackFromFile() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        profile.getConfig().setValue("backpacks.50.size", 27);

        for (int i = 0; i < 27; i++) {
            profile.getConfig().setValue("backpacks.50.contents." + i, new ItemStack(Material.DIAMOND));
        }

        Optional<PlayerBackpack> optional = profile.getBackpack(50);
        Assertions.assertTrue(optional.isPresent());

        PlayerBackpack backpack = optional.get();
        Assertions.assertEquals(50, backpack.getId());
        Assertions.assertEquals(27, backpack.getSize());
        Assertions.assertEquals(-1, backpack.getInventory().firstEmpty());

        backpack.getInventory().setItem(1, new ItemStack(Material.NETHER_STAR));

        Assertions.assertEquals(new ItemStack(Material.DIAMOND), profile.getConfig().getItem("backpacks.50.contents.1"));

        // Saving should write it to the Config file
        backpack.save();
        Assertions.assertEquals(new ItemStack(Material.NETHER_STAR), profile.getConfig().getItem("backpacks.50.contents.1"));
    }
}
