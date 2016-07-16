package com.thetonyk.UHC.Utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftIconCache;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Packets.PacketHandler;

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutLogin;
import net.minecraft.server.v1_8_R3.PacketStatusOutServerInfo;
import net.minecraft.server.v1_8_R3.ServerPing;
import net.minecraft.server.v1_8_R3.ServerPing.ServerData;
import net.minecraft.server.v1_8_R3.ServerPing.ServerPingPlayerSample;

public class DisplayUtils {
	
	public static void sendActionBar(Player player, String message){
		
		EntityPlayer entity = ((CraftPlayer) player).getHandle();
		
		IChatBaseComponent jsonText = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(jsonText, (byte) 2);
		
		entity.playerConnection.sendPacket(packet);
	}
	
	public static void redditHearts() {
		
		new PacketHandler(Main.uhc) {
			
			@Override
			public Object onPacketOutAsync(Player player, Channel channel, Object packet) {
				
				if (!(packet instanceof PacketPlayOutLogin)) return super.onPacketOutAsync(player, channel, packet);
				
				PacketPlayOutLogin nmsPacket = (PacketPlayOutLogin) packet;
				
				try {
					
					Field hardcore = nmsPacket.getClass().getDeclaredField("b");
					hardcore.setAccessible(true);
					hardcore.setBoolean(nmsPacket, true);
					hardcore.setAccessible(false);
					
				} catch (Exception exception) {
					
					exception.printStackTrace();
				
				}
				
				return super.onPacketOutAsync(player, channel, nmsPacket);
				
			}
			
		};
		
	}
	
	public static void playersCount() {
		
		int players = GameUtils.getPlayersCount();
		int max = GameUtils.getSlots();
		
		new PacketHandler(Main.uhc) {
			
			@Override
			public Object onPacketOutAsync(Player player, Channel channel, Object packet) {
				
				if (!(packet instanceof PacketStatusOutServerInfo)) return super.onPacketOutAsync(player, channel, packet);
				
				PacketStatusOutServerInfo nmsPacket = (PacketStatusOutServerInfo) packet;
				ServerPing oldPing = null;
				
				try {
					
					Field serverPing = nmsPacket.getClass().getDeclaredField("b");
					serverPing.setAccessible(true);
					oldPing = (ServerPing) serverPing.get(nmsPacket);
					serverPing.setAccessible(false);
					
				} catch (Exception exception) {
					
					exception.printStackTrace();
				
				}
				
				ServerPing ping = new ServerPing();
				ServerPingPlayerSample counter = new ServerPingPlayerSample(max, players);
				counter.a(new GameProfile[0]);
				
				ping.setFavicon(((CraftIconCache) Bukkit.getServerIcon()).value);
				ping.setMOTD(oldPing.a());
				ping.setPlayerSample(counter);
				ping.setServerInfo(new ServerData(oldPing.c().a(), oldPing.c().b()));
				
				return super.onPacketOutAsync(player, channel, new PacketStatusOutServerInfo(ping));
				
			}
			
		};
		
	}
	
	public static void hideCoord (Player player) {
		
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(((CraftPlayer) player).getHandle(), (byte) 22);
		
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		
	}
	
	public static void showCoord (Player player) {
		
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(((CraftPlayer) player).getHandle(), (byte) 23);
		
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		
	}
	
}
