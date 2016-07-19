package com.thetonyk.UHC.Features;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

import static net.md_5.bungee.api.ChatColor.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class DisplayTab implements Listener {
	
	public DisplayTab() {
		
		new BukkitRunnable() {
			
			public void run() {
				
				updateTab();
				
			}
			
		}.runTaskTimer(Main.uhc, 5, 5);
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		updateTab();
		
	}
	
	private void updateTab () {
		
		DecimalFormat format = new DecimalFormat("##.##");
		int pvpTime = DisplayTimers.getTimeLeftPVP();
		int meetupTime = DisplayTimers.getTimeLeftMeetup();
		World world = GameUtils.getWorld() == null ? null : Bukkit.getWorld(GameUtils.getWorld());
		
		double rawTps = MinecraftServer.getServer().recentTps[0];
		String tps = format.format(rawTps);
		String pvp = pvpTime > 0 ? DisplayTimers.getOtherFormatedTime(pvpTime) : "ON";
		String meetup = meetupTime > 0 ? DisplayTimers.getOtherFormatedTime(meetupTime) : "Now";
		String border = world != null ? (int) world.getWorldBorder().getSize() + "§7x§a" + (int) world.getWorldBorder().getSize() : "Not ready";
		int players = GameUtils.getPlayersCount();
		
		if (GameUtils.getStatus() == Status.END) {
			
			pvp = "End";
			meetup = "End";
			
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			int ping = ((CraftPlayer)player).getHandle().ping;
			
			ComponentBuilder header = new ComponentBuilder("\n Welcome on the UHC, ").color(GRAY).append(player.getName()).color(GREEN).append(" ! \n ").color(GRAY).append("@CommandsPVP").color(AQUA).append(" ⋯ ").color(GRAY).append("TS: ").color(GREEN).append("commandspvp.com").color(AQUA).append(" \n Players: ").color(GRAY).append(String.valueOf(players)).color(GREEN).append(" ⋯ Ping: ").color(GRAY).append(ping + "ms").color(GREEN).append(" ⋯ TPS: ").color(GRAY).append(tps + " \n").color(GREEN);
			ComponentBuilder footer = new ComponentBuilder("\n PVP: ").color(GRAY).append(pvp).color(GREEN).append(" ⋯ Meetup: ").color(GRAY).append(meetup).color(GREEN).append(" ⋯ Border: ").color(GRAY).append(border + " \n").color(GREEN);
			
			player.setPlayerListHeaderFooter(header.create(), footer.create());
		
		}
		
	}

}
