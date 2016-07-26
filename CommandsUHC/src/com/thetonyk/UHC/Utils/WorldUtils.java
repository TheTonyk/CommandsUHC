package com.thetonyk.UHC.Utils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class WorldUtils {
	
	private static Map<String, Integer> worlds = new HashMap<>();

	public static void loadWorld(String world) {
		
		Environment environment = Environment.NORMAL;
		long seed = -89417720380802761l;
		WorldType type = WorldType.NORMAL;
		Boolean newStone = false;
		
		try {
			
			ResultSet worldDB = DatabaseUtils.sqlQuery("SELECT * FROM uhc_worlds WHERE name = '" + world + "' AND server = '" + GameUtils.getServer() + "';");
			
			if (worldDB.next()) {

				environment = Environment.valueOf(worldDB.getString("environment"));
				seed = worldDB.getLong("seed");
				type = WorldType.valueOf(worldDB.getString("type"));
				newStone = worldDB.getBoolean("newStone");

			}
			
			worldDB.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[WorldUtils] Error to fetch informations of world " + world + " in DB.");
			return;
			
		}
		
		WorldCreator worldCreator = new WorldCreator(world);
		worldCreator.environment(environment);
		worldCreator.generateStructures(true);
		worldCreator.generatorSettings("{\"useMonuments\":false" + (!newStone ? ",\"graniteSize\":1,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":0,\"dioriteSize\":1,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":0,\"andesiteSize\":1,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":0" : "") + "}");
		worldCreator.seed(seed);
		worldCreator.type(type);
		
		World newWorld = worldCreator.createWorld();
		newWorld.save();
		
	}
	
	public static void unloadWorld(String world) {
		
		Bukkit.unloadWorld(world, true);
		
	}
	
	public static void deleteWorld(String world) {

		for (Player player : Bukkit.getWorld(world).getPlayers()) {
			
			player.teleport(Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5));
			
		}
		
		File folder = Bukkit.getWorld(world).getWorldFolder();
		
		WorldUtils.unloadWorld(world);
			
		if (FileUtils.delete(folder)) {
			
			DatabaseUtils.sqlInsert("DELETE FROM uhc_worlds WHERE name = '" + world + "' AND server = '" + GameUtils.getServer() + "';");
			worlds.remove(world);
			return;
			
		}
			
		Bukkit.getLogger().severe("[WorldUtils] The folder of world " + world + " can't be deleted.");
		
	}
	

	public static Boolean exist(String world) {
		
		for (String name : worlds.keySet()) {
			
			if (name.equalsIgnoreCase(world)) return true;
			
		}
		
		return false;
		
	}
	
	public static void loadAllWorlds() {
		
		try {
			
			ResultSet worlds = DatabaseUtils.sqlQuery("SELECT * FROM uhc_worlds WHERE server = '" + GameUtils.getServer() + "';");
			
			while (worlds.next()) {
				
				String name = worlds.getString("name");
				int size = worlds.getInt("size");
				
				WorldUtils.worlds.put(name, size);
				
				loadWorld(name);
				
				if (GameUtils.getWorld() != null && GameUtils.getWorld().equalsIgnoreCase(name)) continue;
				
				World world = Bukkit.getWorld(name);
				world.setPVP(false);
				world.setTime(6000);
				world.setGameRuleValue("doDaylightCycle", "false");
				world.setSpawnFlags(false, false);
				world.setDifficulty(Difficulty.PEACEFUL);
				world.save();
				
			}
			
			worlds.close();
			
		} catch (SQLException exception) {
			
			Bukkit.getLogger().severe("[WorldUtils] Error to fetch all worlds.");
			
		}
		
		World lobby = Bukkit.getWorld("lobby");
		lobby.setPVP(false);
		lobby.setTime(6000);
		lobby.setGameRuleValue("doDaylightCycle", "false");
		lobby.setSpawnFlags(false, false);
		lobby.setDifficulty(Difficulty.PEACEFUL);
		lobby.save();
		
	}
	
	public static World createWorld(String world, Environment environment, long seed, WorldType type, int size, Boolean newStone) {
		
		WorldCreator worldCreator = new WorldCreator(world);
		
		worldCreator.environment(environment);
		worldCreator.generateStructures(true);
		worldCreator.generatorSettings("{\"useMonuments\":false" + (!newStone ? ",\"graniteSize\":1,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":0,\"dioriteSize\":1,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":0,\"andesiteSize\":1,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":0" : "") + "}");
		worldCreator.seed(seed);
		worldCreator.type(type);
		
		World newWorld = worldCreator.createWorld();
		
		newWorld.setDifficulty(Difficulty.PEACEFUL);
		newWorld.setSpawnLocation(0, 200, 0);
		newWorld.getWorldBorder().setSize(size);
		newWorld.getWorldBorder().setCenter(0, 0);
		newWorld.getWorldBorder().setDamageBuffer(0);
		newWorld.getWorldBorder().setDamageAmount(1);
		newWorld.getWorldBorder().setWarningDistance(15);
		newWorld.getWorldBorder().setWarningTime(1);
		newWorld.setTime(6000);
		newWorld.setGameRuleValue("doDaylightCycle", "false");
		newWorld.setSpawnFlags(false, false);
		newWorld.save();
		
		DatabaseUtils.sqlInsert("INSERT INTO uhc_worlds (`name`, `environment`, `seed`, `type`, `size`, `pregenned`, `server`, `newStone`) VALUES ('" + world + "', '" + environment.name() + "', '" + seed + "', '" + type.name() + "', '" + size + "', 0, '" + GameUtils.getServer() + "', " + (newStone ? "1" : "0") + ");");
		worlds.put(world, size);
		
		return newWorld;
		
	}
	
	public static int getHighestY (int x, int z, World world) {
		
		for (int y = 255; y >= 0; y--) {
			
			if (world.getBlockAt(x, y, z).getType() == Material.AIR) continue;
				
			return y;
			
		}
		
		return 255;
		
	}
	
	public static int getSize(String world) {
		
		return worlds.containsKey(world) ? worlds.get(world) : 0;
		
	}
	
	public static void setSize(String world, int size) {
		
		DatabaseUtils.sqlInsert("UPDATE uhc_worlds SET size = '" + size + "' WHERE name = '" + world + "' AND server = '" + GameUtils.getServer() + "';");
		worlds.put(world, size);
		
	}
	
	public static int butcher (World world) {
		
		int count = 0;
		
		for (Entity entity : world.getEntities()) {
			
			switch (entity.getType()) {
			
				case ARMOR_STAND:
				case ARROW:
				case BOAT:
				case CHICKEN:
				case COMPLEX_PART:
				case COW:
				case DROPPED_ITEM:
				case EGG:
				case ENDER_CRYSTAL:
				case ENDER_DRAGON:
				case ENDER_PEARL:
				case ENDER_SIGNAL:
				case EXPERIENCE_ORB:
				case FALLING_BLOCK:
				case FIREWORK:
				case FISHING_HOOK:
				case HORSE:
				case IRON_GOLEM:
				case ITEM_FRAME:
				case LEASH_HITCH:
				case LIGHTNING:
				case MINECART:
				case MINECART_CHEST:
				case MINECART_COMMAND:
				case MINECART_FURNACE:
				case MINECART_HOPPER:
				case MINECART_MOB_SPAWNER:
				case MINECART_TNT:
				case MUSHROOM_COW:
				case OCELOT:
				case PAINTING:
				case PIG:
				case PLAYER:
				case PRIMED_TNT:
				case RABBIT:
				case SHEEP:
				case SNOWBALL:
				case SNOWMAN:
				case SPLASH_POTION:
				case SQUID:
				case THROWN_EXP_BOTTLE:
				case UNKNOWN:
				case VILLAGER:
				case WEATHER:
				case WOLF:
					continue;
				default:
					break;
			
			}
			
			entity.remove();
			count++;
			
		}
		
		return count;
		
	}
	
}
