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

    public static boolean hasConflictingEnchants(ItemStack itemStack) {
        if (!ItemUtil.hasTag(itemStack)) return false;
        if (!itemStack.hasEnchantments()) return false;
        NBTTagList enchants = itemStack.getTag().getList("ench", 10);
        for (int i = 0; i < enchants.size(); i++) {
            NBTTagCompound enchTag = enchants.get(i);
            Enchantment key = Enchantment.c(enchTag.getShort("id"));
            if (Enchantment.getId(key) == 16 && containsEnchantment(enchants, Enchantment.c(17))
                    || key.equals(Enchantment.DAMAGE_ALL) && itemStack.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS)
                    || key.equals(Enchantment.DAMAGE_UNDEAD) && itemStack.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS)
                    || key.equals(Enchantment.MENDING) && itemStack.containsEnchantment(Enchantment.ARROW_INFINITE)
                    || key.equals(Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(Enchantment.PROTECTION_PROJECTILE)
                    || key.equals(Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(Enchantment.PROTECTION_FIRE)
                    || key.equals(Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(Enchantment.PROTECTION_EXPLOSIONS)
                    || key.equals(Enchantment.PROTECTION_FIRE) && itemStack.containsEnchantment(Enchantment.PROTECTION_EXPLOSIONS)
                    || key.equals(Enchantment.PROTECTION_FIRE) && itemStack.containsEnchantment(Enchantment.PROTECTION_PROJECTILE)
                    || key.equals(Enchantment.PROTECTION_EXPLOSIONS) && itemStack.containsEnchantment(Enchantment.PROTECTION_PROJECTILE)
                    || key.equals(Enchantment.LOOT_BONUS_BLOCKS) && itemStack.containsEnchantment(Enchantment.SILK_TOUCH)
                    || isArmor(itemStack) && key.equals(Enchantment.VANISHING_CURSE) && itemStack.containsEnchantment(Enchantment.BINDING_CURSE)
                    || (!isBoots(itemStack) && key.equals(Enchantment.PROTECTION_FALL))
                    || isBoots(itemStack) && key.equals(Enchantment.DEPTH_STRIDER) && itemStack.containsEnchantment(Enchantment.FROST_WALKER))
                return true;
        }
        return false;
    }

    private static boolean containsEnchantment(NBTTagList ench, Enchantment enchantment) {
        return ench.list.stream().map(t -> (NBTTagCompound)t).anyMatch(c -> Enchantment.getId(enchantment) == c.getShort("id"));
    }

    public static void removeConflicting(org.bukkit.inventory.ItemStack itemStack) {
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            Enchantment key = entry.getKey();
            if (key.equals(Enchantment.DAMAGE_ALL) && itemStack.containsEnchantment(Enchantment.DAMAGE_UNDEAD))
                itemStack.removeEnchantment(Enchantment.DAMAGE_UNDEAD);
            if (key.equals(Enchantment.DAMAGE_ALL) && itemStack.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS))
                itemStack.removeEnchantment(Enchantment.DAMAGE_ARTHROPODS);
            if (key.equals(Enchantment.DAMAGE_UNDEAD) && itemStack.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS))
                itemStack.removeEnchantment(Enchantment.DAMAGE_ARTHROPODS);
            if (key.equals(Enchantment.MENDING) && itemStack.containsEnchantment(Enchantment.ARROW_INFINITE))
                itemStack.removeEnchantment(Enchantment.MENDING);
            if (key.equals(Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(Enchantment.PROTECTION_PROJECTILE))
                itemStack.removeEnchantment(Enchantment.PROTECTION_PROJECTILE);
            if (key.equals(Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(Enchantment.PROTECTION_FIRE))
                itemStack.removeEnchantment(Enchantment.PROTECTION_FIRE);
            if (key.equals(Enchantment.PROTECTION_ENVIRONMENTAL) && itemStack.containsEnchantment(Enchantment.PROTECTION_EXPLOSIONS))
                itemStack.removeEnchantment(Enchantment.PROTECTION_EXPLOSIONS);
            if (key.equals(Enchantment.PROTECTION_FIRE) && itemStack.containsEnchantment(Enchantment.PROTECTION_EXPLOSIONS))
                itemStack.removeEnchantment(Enchantment.PROTECTION_EXPLOSIONS);
            if (key.equals(Enchantment.PROTECTION_FIRE) && itemStack.containsEnchantment(Enchantment.PROTECTION_PROJECTILE))
                itemStack.removeEnchantment(Enchantment.PROTECTION_PROJECTILE);
            if (key.equals(Enchantment.PROTECTION_EXPLOSIONS) && itemStack.containsEnchantment(Enchantment.PROTECTION_PROJECTILE))
                itemStack.removeEnchantment(Enchantment.PROTECTION_PROJECTILE);
            if (key.equals(Enchantment.LOOT_BONUS_BLOCKS) && itemStack.containsEnchantment(Enchantment.SILK_TOUCH))
                itemStack.removeEnchantment(Enchantment.SILK_TOUCH);
            if (isArmor(itemStack) && key.equals(Enchantment.VANISHING_CURSE) && itemStack.containsEnchantment(Enchantment.BINDING_CURSE))
                itemStack.removeEnchantment(Enchantment.BINDING_CURSE);
            if (isBoots(itemStack) && key.equals(Enchantment.PROTECTION_FALL))
                itemStack.removeEnchantment(Enchantment.PROTECTION_FALL);
            if (isBoots(itemStack) && key.equals(Enchantment.DEPTH_STRIDER) && itemStack.containsEnchantment(Enchantment.FROST_WALKER))
                itemStack.removeEnchantment(Enchantment.FROST_WALKER);
        }
    }

    public static boolean isArmor(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemElytra;
    }

    public static boolean isBoots(ItemStack itemStack) {
        return itemStack.getItem().getName().contains("_BOOTS");
    }
}
