package com.thetonyk.UHC.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Features.DisplayNametags;

public class PlayerUtils {
	
	public static void clearInventory(Player player) {
		
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);
        player.setItemOnCursor(new ItemStack(Material.AIR));

        InventoryView openedInventory = player.getOpenInventory();
        
        if (openedInventory.getType() == InventoryType.CRAFTING)  openedInventory.getTopInventory().clear();
        
    }
	
	public static void clearXp(Player player) {
	
		player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0F);
		
	}
	
	public static void feed(Player player) {
		
        player.setSaturation(5.0F);
        player.setExhaustion(0F);
        player.setFoodLevel(20);
        
    }
	
	public static void heal(Player player) {
		
		player.setHealth(player.getMaxHealth());
		
	}
	
	public static void clearEffects(Player player) {
		
		Collection<PotionEffect> activeEffects = player.getActivePotionEffects();

        for (PotionEffect activeEffect : activeEffects) {
        	
            player.removePotionEffect(activeEffect.getType());
            
        }
		
	}
	
	public enum Rank {
		
		PLAYER("", "§7Player"), WINNER("§6Winner §8| ", "§6Winner"), FAMOUS("§bFamous §8| ", "§bFamous"), BUILDER("§2Build §8| ", "§2Builder"),STAFF("§cStaff §8| ", "§cStaff"), MOD("§9Mod §8| ", "§9Moderator"), ADMIN("§4Admin §8| ", "§4Admin"), FRIEND("§3Friend §8| ", "§3Friend"), HOST("§cHost §8| ", "§cHost"), ACTIVE_BUILDER("§2Build §8| ", "§2Builder");
		
		String prefix;
		String name;
		
		private Rank(String prefix, String name) {
			
			this.prefix = prefix;
			this.name = name;
			
		}
		
		public String getPrefix() {
			
			return prefix;
			
		}
		
		public String getName() {
			
			return name;
			
		}
		
	}
	
	public static void setRank(UUID player, Rank rank) {
		
		DatabaseUtils.sqlInsert("UPDATE users SET rank = '" + rank + "' WHERE uuid = '" + player + "';");
		
		if (Bukkit.getPlayer(player) == null) return;
			
		new BukkitRunnable() {
			
			public void run() {
				
				PermissionsUtils.clearPermissions(Bukkit.getPlayer(player));
				PermissionsUtils.setPermissions(Bukkit.getPlayer(player));
				PermissionsUtils.updateBungeePermissions(Bukkit.getPlayer(player));
				
				DisplayNametags.updateNametag(Bukkit.getPlayer(player), GameUtils.getIDs());
				
			}
			
		}.runTaskLater(Main.uhc, 2);
		
	}
	
	public static Rank getRank(UUID player) {
		
		Rank rank = Rank.PLAYER;
		
		try {
			
			ResultSet req = DatabaseUtils.sqlQuery("SELECT rank FROM users WHERE uuid = '" + player + "';");
			
			if (req.next()) rank = Rank.valueOf(req.getString("rank"));
			
			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get the rank of player with UUID " + player + ".");
			
		}
		
		return rank;
		
	}
	
	public static String getRanks() {
		
		String list = "Availables ranks: §aplayer";
		
		for (Rank rank : Rank.values()) {
			
			if (!rank.name().toLowerCase().equalsIgnoreCase("player")) list = list + " §7| §a" + rank.name().toLowerCase();
			
		}
		
		list = list + "§7.";
		
		return list;
		
	}
	
	public static int getId (String name) {
		
		int id = 0;
		
		try {
			
			ResultSet req = DatabaseUtils.sqlQuery("SELECT id FROM users WHERE name ='" + name + "';");
			
			if (req.next()) id = req.getInt("id");
			
			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get id of player " + name + ".");
			
		}
		
		return id;
		
	}
	
	public static int getId (UUID uuid) {
		
		int id = 0;
		
		try {
			
			ResultSet req = DatabaseUtils.sqlQuery("SELECT id FROM users WHERE uuid ='" + uuid + "';");
			
			if (req.next()) id = req.getInt("id");
			
			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get id of player with UUID " + uuid + ".");
			
		}
		
		return id;
		
	}
	
	public static String getName (int id) {
		
		String name = null;
		
		try {
			
			ResultSet req = DatabaseUtils.sqlQuery("SELECT * FROM users WHERE id = '" + id + "';");
			
			if (req.next()) name = req.getString("name");
				
			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get name of player with id " + id + ".");

		}
		
		return name;
		
	}
	
	public static UUID getUUID (String name) {
		
		UUID uuid = null;
		
		try {
			
			ResultSet req = DatabaseUtils.sqlQuery("SELECT * FROM users WHERE name = '" + name + "';");
			
			if (req.next()) uuid = UUID.fromString(req.getString("uuid"));

			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get UUID of player " + name + ".");
			
		}
		
		return uuid;
		
	}
	
	public static int getChatVisibility (Player player) {
		
		int chatVisibility = 1;
		
		try {
			
			ResultSet req = DatabaseUtils.sqlQuery("SELECT chat FROM settings WHERE id = " + PlayerUtils.getId(player.getUniqueId()) + ";");
			
			if (req.next()) chatVisibility = req.getInt("chat");
			
			req.close();

		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get chat setting of player " + player.getName() + ".");
			
		}
		
		return chatVisibility;
		
	}
	
	public static int getMentionsState (Player player) {
		
		int mentionsState = 1;
		
		try {
			
			ResultSet req = DatabaseUtils.sqlQuery("SELECT mentions FROM settings WHERE id = " + PlayerUtils.getId(player.getUniqueId()) + ";");
			
			if (req.next()) mentionsState = req.getInt("mentions");
			
			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get mentions setting of player " + player.getName() + ".");
			
		}
		
		return mentionsState;
		
	}
	
	public static int getNosoundState (Player player) {
		
		int nosound = 0;
		
		try {
			
			ResultSet req = DatabaseUtils.sqlQuery("SELECT nosound FROM settings WHERE id = " + PlayerUtils.getId(player.getUniqueId()) + ";");
			
			if (req.next()) nosound = req.getInt("nosound");
			
			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get nosound settings of player " + player.getName() + ".");
			
		}
		
		return nosound;
		
	}
	
	public static void setNosoundState (Player player) {
		
		int state = PlayerUtils.getNosoundState(player) == 1 ? 0 : 1;
		
		DatabaseUtils.sqlInsert("UPDATE settings SET nosound = " + state + " WHERE id = " + PlayerUtils.getId(player.getUniqueId()) + ";");
	
	}
	
	public static List<UUID> getIgnoredPlayers (UUID player) {
		
		String ignoredPlayers = "";
		
		try {
			
			ResultSet req = DatabaseUtils.sqlQuery("SELECT ignored FROM settings WHERE id = " + PlayerUtils.getId(player) + ";");
			
			if (req.next()) ignoredPlayers = req.getString("ignored");
			
			req.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[PlayerUtils] Error to get ignored players of player with UUID " + player + ".");
			
		}
		
		List<UUID> ignored = ignoredPlayers == "" ? new ArrayList<UUID>() : new Gson().fromJson(ignoredPlayers, new TypeToken<List<UUID>>(){}.getType());
		return ignored;
		
	}

}
