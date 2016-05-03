package com.thetonyk.UHC.Features;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.thetonyk.UHC.Game;
import com.thetonyk.UHC.Game.Status;
import com.thetonyk.UHC.Utils.PermissionsUtils;

public class LoginWhitelist implements Listener {

	@EventHandler
	public void onConnect(PlayerLoginEvent event) {
		
		PermissionsUtils.setPermissions(event.getPlayer());
		
		if (event.getResult() != Result.KICK_WHITELIST) return;
			
		if (event.getPlayer().isOp() || event.getPlayer().hasPermission("global.bypasswhitelist")) {
			
			event.allow();
			return;
			
		}
			
		if (Game.getStatus() == Status.TELEPORT || Game.getStatus() == Status.PLAY || Game.getStatus() == Status.END) {
			
			event.setKickMessage("§8⫸ §7You are not whitelisted §8⫷\n\n§cThe UHC has already begun.\n\n§7The UHC Arena is available at: §acommandspvp.com §7!");
			return;
			
		}
			
		event.setKickMessage("§8⫸ §7You are not whitelisted §8⫷\n\n§cNo scheduled UHC.\n\n§7The UHC Arena is available at: §acommandspvp.com §7!");
		
	}
	
}
