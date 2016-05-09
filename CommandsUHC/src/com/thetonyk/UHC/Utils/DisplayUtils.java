package com.thetonyk.UHC.Utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils.Status;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class DisplayUtils {
	
	public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {

		IChatBaseComponent jsonTitle = ChatSerializer.a("{'text':'" + title + "'}");
		IChatBaseComponent jsonSubtitle = ChatSerializer.a("{'text':'" + subtitle + "'}");

		PacketPlayOutTitle sendTime = new PacketPlayOutTitle(EnumTitleAction.TIMES, (IChatBaseComponent) null, fadeIn, stay, fadeOut);
		PacketPlayOutTitle sendTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, jsonTitle, fadeIn, stay, fadeOut);
		PacketPlayOutTitle sendSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, jsonSubtitle);

		((CraftPlayer)player).getHandle().playerConnection.sendPacket(sendTime);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(sendSubtitle);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(sendTitle);
		
	}
	
	public static void sendActionBar(Player player, String message){
		
		IChatBaseComponent jsonText = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(jsonText, (byte) 2);
		
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}

	public static void sendTab(Player player, String header, String footer) {
	
		IChatBaseComponent jsonHeader = ChatSerializer.a("{'text':'" + header + "'}");
		IChatBaseComponent jsonFooter = ChatSerializer.a("{'text':'" + footer + "'}");
		
		PacketPlayOutPlayerListHeaderFooter tabHeader = new PacketPlayOutPlayerListHeaderFooter(jsonHeader);
		
		try {
			
			Field sendTab = tabHeader.getClass().getDeclaredField("b");
			sendTab.setAccessible(true);
			sendTab.set(tabHeader, jsonFooter);
			sendTab.setAccessible(!sendTab.isAccessible());
			
		} catch (Exception e) {
			
			Bukkit.getLogger().severe("Error to set tab header & footer to " + player.getName());
			
		}
		
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(tabHeader);
		
	}
	
	public static void redditHearts() {
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Main.uhc, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN) {
					
			@Override
			public void onPacketSending(PacketEvent event) {
				
				if (event.getPacketType() != PacketType.Play.Server.LOGIN) return;
					
				event.getPacket().getBooleans().write(0, true);
				
			}
				
		});
		
	}
	
	public static void playersCount() {
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Main.uhc, ListenerPriority.NORMAL, PacketType.Status.Server.OUT_SERVER_INFO) {
			
			@Override
			public void onPacketSending(PacketEvent event) {
				
				if (event.getPacketType() != PacketType.Status.Server.OUT_SERVER_INFO) return;
					
				int players = (GameUtils.getStatus() == Status.NONE || GameUtils.getStatus() == Status.OPEN || GameUtils.getStatus() == Status.READY) ? Bukkit.getOnlinePlayers().size() : Bukkit.getWhitelistedPlayers().size();
			
				if (GameUtils.getStatus() == Status.NONE || GameUtils.getStatus() == Status.OPEN || GameUtils.getStatus() == Status.READY) {
					
					for (Player player : Bukkit.getOnlinePlayers()) {
						
						if (player.getGameMode() != GameMode.SPECTATOR) continue;
						
						players--;
						
					}
					
				}
				
				WrappedServerPing count = event.getPacket().getServerPings().read(0);
				count.setPlayersOnline(players);
				
				event.getPacket().getServerPings().write(0, count);
				
			}
				
		});
		
	}
	
}
