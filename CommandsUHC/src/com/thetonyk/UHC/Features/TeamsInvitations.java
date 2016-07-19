package com.thetonyk.UHC.Features;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;

import com.thetonyk.UHC.Inventories.InviteInventory;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;
import com.thetonyk.UHC.Utils.GameUtils.TeamType;
import com.thetonyk.UHC.Utils.TeamsUtils;

public class TeamsInvitations implements Listener {

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		
		if (!(event.getRightClicked() instanceof Player)) return;
		
		Status status = GameUtils.getStatus();
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		World world = player.getWorld();
		Player clicked = (Player) event.getRightClicked();
		
		if (status == Status.PLAY || status == Status.TELEPORT || status == Status.END) return;
		
		if (GameUtils.getSpectate(uuid)) return;
		
		if (!world.getName().equals("lobby")) return;
		
		if (GameUtils.getTeamSize() < 2 || GameUtils.getTeamType() != TeamType.CHOSEN) return;
		
		if (TeamsUtils.getTeam(uuid) != null && TeamsUtils.getTeam(clicked.getUniqueId()) != null && TeamsUtils.getTeam(uuid).equalsIgnoreCase(TeamsUtils.getTeam(clicked.getUniqueId()))) return;
				
		Inventory invitation = InviteInventory.getInvite(clicked);
		
		player.openInventory(invitation);
		
	}
	
}
