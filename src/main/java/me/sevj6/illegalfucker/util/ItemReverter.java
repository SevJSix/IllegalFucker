package me.sevj6.illegalfucker.util;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

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
        if (ItemUtil.hasCustomPotionEffects(itemStack)) {
            itemStack.getTag().remove("CustomPotionEffects");
            Utils.log("&3Removed all custom potion effects from %s", Utils.formatItem(itemStack));
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
            if (name.length() > 16) name = name.substring(0, 16);
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
        for (int i = 0; i < enchants.size(); i++) {
            NBTTagCompound compound = enchants.get(i);
            short level = compound.getShort("lvl");
            Enchantment enchantment = Enchantment.c(compound.getShort("id"));
            if (level > enchantment.getMaxLevel()) {
                compound.setShort("lvl", (short) enchantment.getMaxLevel());
                Utils.log("&3Reverted enchant&r&a %s&r&3 from level&r&a %d&r&3 to&r&a %d&r", enchantment.a(), level, enchantment.getMaxLevel());
            }
//            if (!ItemUtil.canEnchant(itemStack, enchantment)) {
//                enchants.remove(i);
//                Utils.log("&3Removing enchant&r&a %s&r&3 because it could not enchant %s", enchantment.a(), Utils.formatItem(itemStack));
//            }
        }
    }

    public static boolean hasConflictingEnchants(org.bukkit.inventory.ItemStack itemStack) {
        if (hasMeta(itemStack)) {
            ItemMeta meta = itemStack.getItemMeta();
            if (!meta.hasEnchants()) return false;
            for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                org.bukkit.enchantments.Enchantment key = entry.getKey();
                if (key.equals(org.bukkit.enchantments.Enchantment.DAMAGE_ALL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_UNDEAD)
                        || key.equals(org.bukkit.enchantments.Enchantment.DAMAGE_ALL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ARTHROPODS)
                        || key.equals(org.bukkit.enchantments.Enchantment.DAMAGE_UNDEAD) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ARTHROPODS)
                        || key.equals(org.bukkit.enchantments.Enchantment.MENDING) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.ARROW_INFINITE)
                        || key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE)
                        || key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_FIRE)
                        || key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS)
                        || key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_FIRE) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS)
                        || key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_FIRE) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE)
                        || key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE)
                        || key.equals(org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH)
                        || isArmor(itemStack) && key.equals(org.bukkit.enchantments.Enchantment.VANISHING_CURSE) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.BINDING_CURSE)
                        || (!isBoots(itemStack) && key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_FALL))
                        || isBoots(itemStack) && key.equals(org.bukkit.enchantments.Enchantment.DEPTH_STRIDER) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.FROST_WALKER))
                    return true;
            }
        }
        return false;
    }

    public static void removeConflicting(org.bukkit.inventory.ItemStack itemStack) {
        for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            org.bukkit.enchantments.Enchantment key = entry.getKey();
            if (key.equals(org.bukkit.enchantments.Enchantment.DAMAGE_ALL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_UNDEAD))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_UNDEAD);
            if (key.equals(org.bukkit.enchantments.Enchantment.DAMAGE_ALL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ARTHROPODS))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ARTHROPODS);
            if (key.equals(org.bukkit.enchantments.Enchantment.DAMAGE_UNDEAD) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ARTHROPODS))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ARTHROPODS);
            if (key.equals(org.bukkit.enchantments.Enchantment.MENDING) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.ARROW_INFINITE))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.MENDING);
            if (key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE);
            if (key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_FIRE))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_FIRE);
            if (key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS);
            if (key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_FIRE) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS);
            if (key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_FIRE) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE);
            if (key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE);
            if (key.equals(org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH);
            if (isArmor(itemStack) && key.equals(org.bukkit.enchantments.Enchantment.VANISHING_CURSE) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.BINDING_CURSE))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.BINDING_CURSE);
            if (isBoots(itemStack) && key.equals(org.bukkit.enchantments.Enchantment.PROTECTION_FALL))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_FALL);
            if (isBoots(itemStack) && key.equals(org.bukkit.enchantments.Enchantment.DEPTH_STRIDER) && itemStack.containsEnchantment(org.bukkit.enchantments.Enchantment.FROST_WALKER))
                itemStack.removeEnchantment(org.bukkit.enchantments.Enchantment.FROST_WALKER);
        }
    }

    public static boolean isArmor(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemElytra;
    }

    public static boolean isBoots(ItemStack itemStack) {
        return itemStack.getItem().getName().contains("_BOOTS");
    }
}
