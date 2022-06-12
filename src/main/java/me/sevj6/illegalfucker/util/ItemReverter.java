package me.sevj6.illegalfucker.util;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;

/**
 * @author 254n_m
 * @since 6/10/22/ 12:32 AM
 * This file was created as a part of IllegalFucker
 */
public class ItemReverter {
    public static void revert(ItemStack itemStack) {
        if (ItemUtil.isUnobtainableItem(itemStack)) {
            Utils.log("&3Deleted a &r&a%s&3 because it was unobtainable", Utils.formatItem(itemStack));
            itemStack.setCount(-1);
        }
        if (ItemUtil.isOverstacked(itemStack)) {
            Utils.log("&aReverted a &r&a%s&3 because it was overstacked", Utils.formatItem(itemStack));
            itemStack.setCount(itemStack.getItem().getMaxStackSize());
        }
        if (ItemUtil.isHighDura(itemStack)) {
            Utils.log("&aReverted a &r&a%s&3 because it had&r&a %d/%d&r&3 durability", Utils.formatItem(itemStack), itemStack.getDamage(), itemStack.getItem().getMaxDurability());
            itemStack.setDamage(0); //Damage in minecraft is fucking weird
        }
        if (ItemUtil.hasAttributes(itemStack)) {
            Utils.log("&aRemoving attributes from item %s", Utils.formatItem(itemStack));
            NBTTagCompound tagCompound = itemStack.getTag();
            tagCompound.remove("AttributeModifiers");
            itemStack.setTag(tagCompound);
        }
        if (ItemUtil.cantBeEnchanted(itemStack)) {
            if (itemStack.hasTag()) {
                NBTTagCompound tag = itemStack.getTag();
                if (tag.hasKey("ench")) {
                    tag.remove("ench");
                    Utils.log("&3Removed all enchantments from item %s", Utils.formatItem(itemStack));
                }
            }
        }
        if (ItemUtil.hasIllegalEnchants(itemStack)) {
            revertEnchantLevels(itemStack);
        }
        if (ItemUtil.isIllegalEnchantedBook(itemStack)) {
            revertBookEnchants(itemStack);
        }
        if (ItemUtil.hasCustomPotionEffects(itemStack)) {
            itemStack.getTag().remove("CustomPotionEffects");
            Utils.log("&3Removed all custom potion effects from %s", Utils.formatItem(itemStack));
        }
        if (ItemUtil.hasCustomPotionColor(itemStack)) {
            itemStack.getTag().remove("CustomPotionColor");
            Utils.log("&3Removed custom potion color from %s", Utils.formatItem(itemStack));
        }
        if (ItemUtil.hasMeta(itemStack)) {
            NBTTagCompound display = itemStack.getTag().getCompound("display");
            display.remove("Lore");
            Utils.log("&3Removed lore form %s", Utils.formatItem(itemStack));
        }
        if (ItemUtil.isUnbreakable(itemStack)) {
            itemStack.getTag().remove("Unbreakable");
            Utils.log("&3Removed the Unbreakable tag from item %s", Utils.formatItem(itemStack));
        }
        if (ItemUtil.hasHideFlags(itemStack)) {
            itemStack.getTag().remove("HideFlags");
            Utils.log("&3Removed the HideFlags tag from item %s", Utils.formatItem(itemStack));
        }
        if (ItemUtil.hasInvalidBlockEntityTag(itemStack)) {
            itemStack.getTag().remove("BlockEntityTag");
            Utils.log("&3Removed the BlockEntityTag tag from item %s", Utils.formatItem(itemStack));
        }
        if (ItemUtil.hasInvalidName(itemStack)) {
            NBTTagCompound display = itemStack.getTag().getCompound("display");
            String name = display.getString("Name");
            if (ChatColor.stripColor(name).length() != name.length()) name = ChatColor.stripColor(name);
            if (name.length() > 30) name = name.substring(0, 30);
            display.setString("Name", name);
        }
        if (ItemUtil.hasIllegalFlightDuration(itemStack)) {
            NBTTagCompound compound = itemStack.getTag().getCompound("Fireworks");
            byte duration = compound.getByte("Flight");
            if (duration < 1) compound.setByte("Flight", (byte) 1);
            else if (duration > 3) compound.setByte("Flight", (byte) 3);
            Utils.log("&3Reverted the flight duration of a firework with the duration &a%d", duration);
        }
    }

    private static void revertEnchantLevels(ItemStack itemStack) {
        NBTTagList enchants = itemStack.getEnchantments();
        handleEnchantTagList(enchants);
        if (hasConflictingEnchants(itemStack)) itemStack.getTag().remove("ench");
    }

    private static void revertBookEnchants(ItemStack itemStack) {
        NBTTagList enchants = (NBTTagList) itemStack.getTag().get("StoredEnchantments");
        handleEnchantTagList(enchants);
        if (checkConflicting(itemStack, enchants)) itemStack.getTag().remove("StoredEnchantments");
    }

    private static void handleEnchantTagList(NBTTagList enchants) {
        for (int i = 0; i < enchants.size(); i++) {
            NBTTagCompound compound = enchants.get(i);
            short level = compound.getShort("lvl");
            Enchantment enchantment = Enchantment.c(compound.getShort("id"));
            if (level > enchantment.getMaxLevel()) {
                compound.setShort("lvl", (short) enchantment.getMaxLevel());
                Utils.log("&3Reverted enchant&r&a %s&r&3 from level&r&a %d&r&3 to&r&a %d&r", enchantment.a(), level, enchantment.getMaxLevel());
            }
        }
    }

    public static boolean hasConflictingEnchants(ItemStack itemStack) {
        if (!ItemUtil.hasTag(itemStack)) return false;
        if (!itemStack.hasEnchantments()) return false;
        NBTTagList enchants = itemStack.getTag().getList("ench", 10);
        return checkConflicting(itemStack, enchants);
    }

    public static boolean checkConflicting(ItemStack itemStack, NBTTagList enchants) {
        for (int i = 0; i < enchants.size(); i++) {
            NBTTagCompound enchTag = enchants.get(i);
            Enchantment key = Enchantment.c(enchTag.getShort("id"));
            if (
                    Enchantment.getId(key) == 16 && containsEnchantment(enchants, 17) ||
                            Enchantment.getId(key) == 16 && containsEnchantment(enchants, 18) ||
                            Enchantment.getId(key) == 17 && containsEnchantment(enchants, 18) ||
                            Enchantment.getId(key) == 70 && containsEnchantment(enchants, 51) ||
                            Enchantment.getId(key) == 0 && containsEnchantment(enchants, 4) ||
                            Enchantment.getId(key) == 0 && containsEnchantment(enchants, 1) ||
                            Enchantment.getId(key) == 0 && containsEnchantment(enchants, 3) ||
                            Enchantment.getId(key) == 1 && containsEnchantment(enchants, 3) ||
                            Enchantment.getId(key) == 1 && containsEnchantment(enchants, 4) ||
                            Enchantment.getId(key) == 3 && containsEnchantment(enchants, 4) ||
                            Enchantment.getId(key) == 35 && containsEnchantment(enchants, 33) ||
                            isArmor(itemStack) && Enchantment.getId(key) == 71 && containsEnchantment(enchants, 10))
                return true;
        }
        return false;
    }

    private static boolean containsEnchantment(NBTTagList ench, int id) {
        return ench.list.stream().map(t -> (NBTTagCompound) t).anyMatch(c -> id == c.getShort("id"));
    }

    public static boolean isArmor(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemElytra;
    }
}
