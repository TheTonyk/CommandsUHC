package com.thetonyk.UHC.Inventories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.UHC.Utils.DatabaseUtils;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.ItemsUtils;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.TeamsUtils;

public class TeamsInventory implements Listener {
	
	public static Inventory getTeams(int page) {
		
		Inventory inventory = Bukkit.createInventory(null, 54, "§8⫸ §4Teams");
		Inventory inventory1 = Bukkit.createInventory(null, 54, "§8⫸ §4Teams");
		
		ArrayList<String> lore = new ArrayList<String>();
		
		try {
			
			ResultSet teams = DatabaseUtils.sqlQuery("SELECT * FROM uhc_teams WHERE server = '" + GameUtils.getServer() + "';");
			
			int count = 0;
			
			while (teams.next()) {
				
				if (teams.getInt("exist") == 1) {
					
					int data;
					
					switch (teams.getString("prefix").substring(1, 2)) {
					
						case "0":
							data = 0;
							break;
						case "1":
							data = 4;
							break;
						case "2":
							data = 2;
							break;
						case "3":
							data = 6;
							break;
						case "4":
							data = 3;
							break;
						case "5":
							data = 5;
							break;
						case "6":
							data = 14;
							break;
						case "8":
							data = 8;
							break;
						case "9":
							data = 12;
							break;
						case "a":
							data = 10;
							break;
						case "b":
							data = 7;
							break;
						case "c":
							data = 1;
							break;
						case "d":
							data = 9;
							break;
						case "e":
							data = 11;
							break;
						case "f":
							data = 15;
							break;
						default:
							data = 15;
							break;
						
					}
					
					lore.add(" ");
					
					if (teams.getString("members").length() > 0) {
						
						for (String player : teams.getString("members").split(";")) {
							
							UUID uuid = UUID.fromString(player);
							
							lore.add("§8⫸ " + (GameUtils.getDeath(uuid) ? "§c☠ " : "  ") + PlayerUtils.getRank(uuid).getPrefix() + ((TeamsUtils.getTeam(uuid) != null) ? TeamsUtils.getTeamPrefix(uuid) : "§7") + PlayerUtils.getName(PlayerUtils.getId(uuid)));
							
						}
					
					}
					
					lore.add(" ");
					
					ItemStack item = ItemsUtils.createItem(Material.BANNER, "§8⫸ " + teams.getString("prefix") + teams.getString("name") + "§r §8⫷", 1, data, lore);
					
					if (count <= 44) inventory.setItem(count, item);
					else {
						
						int slot = (count + 9) - 54;
						inventory1.setItem(slot, item);
						
					}
					
					lore.clear();
					count++;
					
				}
				
				if (count > 44) {
					
					inventory.setItem(52, ItemsUtils.getSkull("§7Next §8⫸", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJmM2EyZGZjZTBjM2RhYjdlZTEwZGIzODVlNTIyOWYxYTM5NTM0YThiYTI2NDYxNzhlMzdjNGZhOTNiIn19fQ=="));
					inventory1.setItem(52, ItemsUtils.getSkull("§8⫷ §7Previous", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIwZjZlOGFmNDZhYzZmYWY4ODkxNDE5MWFiNjZmMjYxZDY3MjZhNzk5OWM2MzdjZjJlNDE1OWZlMWZjNDc3In19fQ=="));
					
				}
					
				ItemStack item = ItemsUtils.createItem(Material.BARRIER, "§8⫸ §cClose", 1, 0);
				inventory.setItem(53, item);
				inventory1.setItem(53, item);
				
			}
			
			teams.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[TeamsInventory] Error to fetch all teams.");
			
		}
		
		if (page == 0) return inventory;
		if (page == 1) return inventory1;
		
		return null;
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (!event.getInventory().getTitle().equals("§8⫸ §4Teams")) return;
		
		event.setCancelled(true);
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		if (item.getItemMeta().getDisplayName().equals("§7Next §8⫸")) {
			
			player.openInventory(TeamsInventory.getTeams(1));
			return;
			
		}
		
		if (item.getItemMeta().getDisplayName().equals("§8⫷ §7Previous")) {
			
			player.openInventory(TeamsInventory.getTeams(0));
			return;
			
		}
		
		if (item.getItemMeta().getDisplayName().equals("§8⫸ §cClose")) {
			
			player.closeInventory();
			
		}
		
	}

}
