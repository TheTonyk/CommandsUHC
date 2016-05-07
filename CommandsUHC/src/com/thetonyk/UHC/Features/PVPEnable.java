package com.thetonyk.UHC.Features;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Events.PVPEvent;
import com.thetonyk.UHC.Utils.DisplayUtils;
import com.thetonyk.UHC.Utils.GameUtils;

public class PVPEnable implements Listener {

	@EventHandler
	public void onPVPEnable(PVPEvent event) {
		
		Bukkit.getWorld(GameUtils.getWorld()).setPVP(true);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			DisplayUtils.sendTitle(player, "", "§aPVP §7enabled", 5, 30, 5);
			player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1, 1);
			
		}
		
		Bukkit.broadcastMessage(Main.PREFIX + "The PVP is now enabled.");
		
	}
	
}
