package me.sevj6.illegalfucker.listener;

import me.txmc.paperapiextentions.events.ItemStackCreateEvent;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestItemStackEvent implements Listener {

    @EventHandler
    public void onCreate(ItemStackCreateEvent event) {
        ItemStack item = event.getItemStack();
    }
}
