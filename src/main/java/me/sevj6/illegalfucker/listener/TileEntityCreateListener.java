package me.sevj6.illegalfucker.listener;

import me.txmc.paperapiextentions.events.TileEntityCreateEvent;
import me.txmc.paperapiextentions.mixin.mixins.MixinTileEntity;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TileEntityCreateListener implements Listener {

    @EventHandler
    public void onTileEntityCreate(TileEntityCreateEvent event) {
        NBTTagCompound compound = event.getCompound();
        if (!compound.hasKey("CustomName")) return;
        String name = compound.getString("CustomName");
        if (name.length() > 30) name = name.substring(0, 30);
        name = ChatColor.stripColor(name);
        compound.setString("CustomName", name);
    }
}
