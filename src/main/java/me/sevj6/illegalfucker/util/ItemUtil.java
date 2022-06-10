package me.sevj6.illegalfucker.util;

import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.Items;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class ItemUtil {

    public static final List<Item> illegals = Arrays.asList(
            Item.getById(7), //Bedrock
            Item.getById(166), //Barrier
            Item.getById(120), // End portal frames
            Item.getById(52), //Monster spawner
            Item.getById(255), // Structure block
            Item.getById(217), //Structure void
            Item.getById(383), //Spawn egg
            Item.getById(211), //Chain Command Block
            Item.getById(210), //Repeating Command Block
            Item.getById(137), //Command Block
            Item.getById(422) //Command Block Minecart
    );

    public static boolean isIllegal(ItemStack item) {
        if (item == null) return false;
        return isOverstacked(item) || isUnobtainableItem(item);
    }

    public static boolean isOverstacked(ItemStack item) {
        return item.getCount() > item.getMaxStackSize();
    }

    public static boolean isUnobtainableItem(ItemStack item) {
        return illegals.contains(item.getItem());
    }
}
