package me.sevj6.illegalfucker.util;

import net.minecraft.server.v1_12_R1.*;

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
    private static final List<Item> exempt = Arrays.asList(
            Item.getById(0), //Air
            Item.getById(397), //Skull
            Item.getById(351), //Dye
            Item.getById(322), //Golden apple
            Item.getById(355), //Beds
            Item.getById(349), //Fish
            Item.getById(350), //Cooked fish
            Item.getById(425), //Banner
            Item.getById(263), //Coal
            Item.getById(358)
    );

    public static boolean isUnobtainableItem(ItemStack item) {
        return illegals.contains(item.getItem());
    }

    public static boolean isOverstacked(ItemStack itemStack) {
        return itemStack.getCount() > itemStack.getItem().getMaxStackSize();
    }

    public static boolean isHighDura(ItemStack itemStack) {
        if (itemStack.hasTag() && itemStack.getTag().hasKey(""))
        if (exempt.contains(itemStack.getItem())) return false;
        return itemStack.getDamage() < 0 || itemStack.getDamage() > itemStack.getItem().getMaxDurability() && Item.getId(itemStack.getItem()) > 256;
    }

    public static boolean hasAttributes(ItemStack itemStack) {
        if (!hasTag(itemStack)) return false;
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) return false;
        return tag.hasKey("AttributeModifiers");
    }

    public static boolean hasTag(ItemStack itemStack) {
        return itemStack.hasTag();
    }

    public static boolean hasIllegalEnchants(ItemStack itemStack) {
        if (!hasTag(itemStack)) return false;
        if (!itemStack.hasEnchantments()) return false;
        NBTTagList enchants = itemStack.getEnchantments();
        for (NBTTagCompound compound : enchants.list.stream().map(t -> (NBTTagCompound) t).toArray(NBTTagCompound[]::new)) {
            short level = compound.getShort("lvl");
            Enchantment enchantment = Enchantment.c(compound.getShort("id"));
            if (level > enchantment.getMaxLevel()) return true;
            if (!enchantment.canEnchant(itemStack)) return true;
        }
        return false;
    }
}
