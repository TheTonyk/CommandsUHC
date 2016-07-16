package com.thetonyk.UHC.GUI;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.Gson;
import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Packets.PacketHandler;

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateSign;

public class SignGUI {
	
	private EntityPlayer entityPlayer;
	private PacketHandler packetsListener;
	private Listener listener;
	private Gson gson = new Gson();
	
	public SignGUI (Player player, SignCallback<String[]> callback) {
		
		this(player, new String[0], callback);
		
	}
	
	public SignGUI (Player player, String[] text, SignCallback<String[]> callback) {
		
		this.entityPlayer = ((CraftPlayer) player).getHandle();
		
		IBlockData oldBlock = this.entityPlayer.world.getType(new BlockPosition(0, 0, 0));
		IChatBaseComponent[] nmsText = new IChatBaseComponent[4];
		
		for (int i = 0; i < nmsText.length; i++) {
			
			nmsText[i] = IChatBaseComponent.ChatSerializer.a(gson.toJson(i < text.length ? text[i] : ""));
			
		}
		
		PacketPlayOutBlockChange nmsPacket = new PacketPlayOutBlockChange(this.entityPlayer.world, new BlockPosition(0, 0, 0));
		nmsPacket.block = Blocks.WALL_SIGN.getBlockData();
		
		this.entityPlayer.playerConnection.sendPacket(nmsPacket);
		this.entityPlayer.playerConnection.sendPacket(new PacketPlayOutUpdateSign(this.entityPlayer.world, new BlockPosition(0, 0, 0), nmsText));
		this.entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(new BlockPosition(0, 0, 0)));
		
		nmsPacket = new PacketPlayOutBlockChange(this.entityPlayer.world, new BlockPosition(0, 0, 0));
		nmsPacket.block = oldBlock;
		
		this.entityPlayer.playerConnection.sendPacket(nmsPacket);
		
		this.packetsListener = new PacketHandler(Main.uhc) {
			
			@Override
			public Object onPacketInAsync(Player player, Channel channel, Object packet) {
				
				if (!(packet instanceof PacketPlayInUpdateSign)) return super.onPacketInAsync(player, channel, packet);
				
				if (!player.getUniqueId().equals(entityPlayer.getUniqueID())) return super.onPacketInAsync(player, channel, packet);
				
				PacketPlayInUpdateSign nmsPacket = (PacketPlayInUpdateSign) packet;
				
				IChatBaseComponent[] lines = nmsPacket.b();
				
				String[] rawLines = new String[4];
				
				for (int i = 0; i < lines.length; i++) {
					
					rawLines[i] = lines[i].getText();
					
				}
				
				callback.onConfirm(rawLines);
				delete();
				
				return super.onPacketInAsync(player, channel, packet);
				
			}
				
		};
		
		this.listener = new Listener() {
			
			@EventHandler
			public void onQuit(PlayerQuitEvent event) {
				
				callback.onDisconnect();
				delete();
				
			}
			
		};
		
		Bukkit.getPluginManager().registerEvents(this.listener, Main.uhc);
		
	}
	
	public void delete() {
		
		this.packetsListener.close();;
		HandlerList.unregisterAll(this.listener);
		
	}
	
	public interface SignCallback<T> {
		
		void onConfirm(String[] lines);
		void onDisconnect();
		
	}

}
