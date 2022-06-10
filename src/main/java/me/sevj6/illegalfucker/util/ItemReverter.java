package me.sevj6.illegalfucker.util;

import net.minecraft.server.v1_12_R1.Enchantment;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;

/**
 * @author 254n_m
 * @since 6/10/22/ 12:32 AM
 * This file was created as a part of IllegalFucker
 */
public class ItemReverter {
    public static void revert(ItemStack itemStack) {
        if (ItemUtil.isUnobtainableItem(itemStack)) {
            itemStack.setCount(-1);
            Utils.log("&3Deleted a &r&a%s&3 because it was unobtainable", Utils.formatItem(itemStack));
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
        if (ItemUtil.hasIllegalEnchants(itemStack)) {
            NBTTagCompound itemTag = (NBTTagCompound) itemStack.getTag().clone();
            NBTTagList enchants = (NBTTagList) itemTag.map.get("ench");
            for (int i = 0; i < enchants.size(); i++) {
                try {
                    NBTTagCompound compound = enchants.get(i);
                    short level = compound.getShort("lvl");
                    Enchantment enchantment = Enchantment.c(compound.getShort("id"));
                    if (level > enchantment.getMaxLevel()) {
                        compound.setShort("lvl", (short) enchantment.getMaxLevel());
                        Utils.log("&3Reverted enchant&r&a %s&r&3 from level&r&a %d&r&3 to&r&a %d&r", enchantment.a(), level, enchantment.getMaxLevel());
                    }
                    if (!enchantment.canEnchant(itemStack)) {
                        enchants.remove(i);
                        Utils.log("&3Removing enchant&r&a %s&r&3 because it could not enchant&r&a %s&r", enchantment.a(), Utils.formatItem(itemStack));
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            if (enchants.size() > 0) {
                itemTag.set("ench", enchants);
            } else itemTag.remove("ench");
            itemStack.setTag(itemTag);
        }
    }
}
