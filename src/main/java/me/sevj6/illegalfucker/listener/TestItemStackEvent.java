package me.sevj6.illegalfucker.listener;

import me.sevj6.illegalfucker.util.ItemUtil;
import me.txmc.paperapiextentions.events.ItemStackCreateEvent;
import me.txmc.paperapiextentions.mixin.mixins.MixinItemStack;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestItemStackEvent implements Listener {

    @EventHandler
    public void onCreate(ItemStackCreateEvent event) {
        ItemStack item = event.getItemStack();
        if (ItemUtil.isIllegal(item)) event.setCancelled(true);
    }
}
