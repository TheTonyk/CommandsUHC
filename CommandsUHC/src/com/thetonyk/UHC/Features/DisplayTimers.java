package com.thetonyk.UHC.Features;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;
import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Events.FinalHealEvent;
import com.thetonyk.UHC.Events.MeetupEvent;
import com.thetonyk.UHC.Events.PVPEvent;
import com.thetonyk.UHC.Utils.DisplayUtils;
import com.thetonyk.UHC.Utils.GameUtils;

public class DisplayTimers implements Listener {
	
	public static BukkitRunnable timer = null;
	public static int time = GameUtils.getTime();
	public static int pvpTime = GameUtils.getPVP();
	public static int meetupTime = GameUtils.getMeetup();
	
	@EventHandler
	public void onEnable(PluginEnableEvent event) {
		
		if (GameUtils.getStatus() != Status.PLAY) return;
				
		startTimer();
				
	}
	
	public static void startTimer() {
		
		if (timer != null) {
			
			timer.cancel();
			timer = null;
			
		}
		
		time = GameUtils.getTime();
		pvpTime = GameUtils.getPVP();
		meetupTime = GameUtils.getMeetup();
		timer = new BukkitRunnable() {
			
			public void run() {
				
				if (GameUtils.getStatus() == Status.END) {
					
					cancel();
					return;
					
				}
				
				time++;
				
				if (time % 60 == 0) GameUtils.setTime(time);
				
				int finalHeal = 45 - time;
				int mobSpawn = 180 - time;
				int pvp = pvpTime - time;
				int meetup = meetupTime - time;
				String message = "";
				
				if (finalHeal > 0) message += "§7Final heal §8⫸ §a" + getFormatedTime(finalHeal);
				
				if (pvp > 0) message += (message.length() > 0 ? " §8| " : "") + "§7PVP §8⫸ §a" + getFormatedTime(pvp);
				
				if (meetup > 0) message += (message.length() > 0 ? " §8| " : "") + "§7Meetup §8⫸ §a " + getFormatedTime(meetup);
				
				if (message.length() == 0) {
					
					int size = (int) Bukkit.getWorld(GameUtils.getWorld()).getWorldBorder().getSize();
					message = "§8⫸ §6Meetup is now! §8| §7Border size §8⫸ §a" + size + "§7x§a" + size;
					
				}
				
				for (Player player : Bukkit.getOnlinePlayers()) {
						
					if (MeetupWarning.runnables.containsKey(player.getUniqueId())) continue;
						
					DisplayUtils.sendActionBar(player, message);
						
				}
				
				if (finalHeal == 0) {
					
					Bukkit.getPluginManager().callEvent(new FinalHealEvent());
					
					for (Player player : Bukkit.getOnlinePlayers()) {
						
						PlayerUtils.feed(player);
						PlayerUtils.heal(player);
						player.setFireTicks(0);
						
						Title title = new Title("§aFinal heal", "§7Do not ask for others heals.", 5, 30, 5);
						player.sendTitle(title);
						
						if (PlayerUtils.getNosoundState(player) == 1) continue;
						
						player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
						
					}
					
					Bukkit.broadcastMessage(Main.PREFIX + "All players were full healed.");
					
				}
				
				if (mobSpawn == 0) {
					
					Bukkit.getWorld(GameUtils.getWorld()).setSpawnFlags(true, true);
					
				}
				
				if (pvp == 0) Bukkit.getPluginManager().callEvent(new PVPEvent());
				
				if (meetup == 0) Bukkit.getPluginManager().callEvent(new MeetupEvent());
				
			}
			
		};
		
		timer.runTaskTimer(Main.uhc, 0, 20);
		
	}
	
	public static String getFormatedTime (int time) {
		
		int timer = time;
		
		int hours = (int) Math.floor(timer / 3600);
		timer -= hours * 3600;
		
		int minutes = (int) Math.floor(timer / 60);
		timer -= minutes * 60;
		
		StringBuilder stringTime = new StringBuilder();
		
		if (hours > 0) {
			
			if (hours < 10) stringTime.append("0");
			stringTime.append(hours);
			stringTime.append("h");
			
		}
		
		if (minutes > 0) {
			
			if (minutes < 10) stringTime.append("0");
			stringTime.append(minutes);
			stringTime.append("m");
			
		}
		
		if (timer < 10) stringTime.append("0");
		stringTime.append(timer);
		stringTime.append("s");
		
		return stringTime.toString();
		
	}
	
	public static String getOtherFormatedTime (int time) {
		
		int timer = time;
		
		int hours = (int) Math.floor(timer / 3600);
		timer -= hours * 3600;
		
		int minutes = (int) Math.floor(timer / 60);
		timer -= minutes * 60;
		
		StringBuilder stringTime = new StringBuilder();
		
		if (hours > 0) {
			
			if (hours < 10) stringTime.append("0");
			stringTime.append(hours);
			stringTime.append(":");
			
		}
		
		if (minutes > -1) {
			
			if (minutes < 10) stringTime.append("0");
			stringTime.append(minutes);
			stringTime.append(":");
			
		} else {
			
			stringTime.append("00:");
			
		}
		
		if (timer < 10) stringTime.append("0");
		stringTime.append(timer);
		
		return stringTime.toString();
		
	}
	
	public static int getTime() {
		
		return time;
		
	}
	
	public static int getTimeLeftPVP() {
		
		return pvpTime - time;
		
	}
	
	public static int getTimeLeftMeetup() {
		
		return meetupTime - time;
		
	}

}
