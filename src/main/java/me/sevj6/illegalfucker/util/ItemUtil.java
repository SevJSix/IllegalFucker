package me.sevj6.illegalfucker.util;

import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class ItemUtil {

    public static final List<Material> unobtainable = Arrays.asList(Material.BEDROCK, Material.LONG_GRASS, Material.MOB_SPAWNER, Material.SOIL,
            Material.COMMAND, Material.COMMAND_REPEATING, Material.COMMAND_CHAIN, Material.COMMAND_MINECART, Material.BARRIER, Material.GRASS_PATH,
            Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID, Material.MONSTER_EGGS, Material.MONSTER_EGG, Material.KNOWLEDGE_BOOK);

    public static boolean isOverstacked(ItemStack item) {
        return item.getCount() > item.getMaxStackSize();
    }

    public static boolean isOverEnchanted(ItemStack item) {
        return item.hasEnchantments() && item.asBukkitCopy().getEnchantments().entrySet().stream().anyMatch(entry -> entry.getValue() > entry.getKey().getMaxLevel());
    }

    public static boolean isIllegalDurability(ItemStack item) {
        org.bukkit.inventory.ItemStack itemStack = item.asBukkitCopy();
        return ((itemStack.getType().getMaxDurability() > 50) && (itemStack.getDurability() < 0 || itemStack.getDurability() > itemStack.getType().getMaxDurability()));
    }
}
