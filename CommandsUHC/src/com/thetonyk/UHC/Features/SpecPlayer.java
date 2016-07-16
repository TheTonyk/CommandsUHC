package com.thetonyk.UHC.Features;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Inventories.PlayerInventory;
import com.thetonyk.UHC.Inventories.SelectorInventory;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.ItemsUtils;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.PlayerUtils.Rank;
import com.thetonyk.UHC.Utils.TeamsUtils;
import com.thetonyk.UHC.Utils.WorldUtils;

public class SpecPlayer implements Listener {
	
	public static void enable(UUID uuid) {
		
		if (!GameUtils.getPlayers().containsKey(uuid)) GameUtils.addPlayer(uuid);
		
		GameUtils.setSpectate(uuid, true);
		
		if (TeamsUtils.invitations.containsKey(uuid)) TeamsUtils.invitations.remove(uuid);
		
		if (TeamsUtils.getTeam(uuid) != null) TeamsUtils.leaveTeam(uuid);
		
		if (Bukkit.getPlayer(uuid) == null) return;
		
		Player spectator = Bukkit.getPlayer(uuid);
		
		LoginPlayer.updateVisibility();
		
		PlayerUtils.clearInventory(spectator);
		PlayerUtils.clearEffects(spectator);
		PlayerUtils.clearXp(spectator);
		spectator.setGameMode(GameMode.SPECTATOR);
		setItems(spectator);
		DisplayNametags.updateNametag(spectator, GameUtils.getIDs());
		
	}
	
	public static void disable(UUID uuid) {
		
		GameUtils.setSpectate(uuid, false);
		
		if (Bukkit.getPlayer(uuid) == null) return;
		
		Player spectator = Bukkit.getPlayer(uuid);
		
		LoginPlayer.updateVisibility();
		
		spectator.teleport(Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5));
		PlayerUtils.clearInventory(spectator);
		PlayerUtils.clearEffects(spectator);
		spectator.setGameMode(GameMode.ADVENTURE);
		spectator.setPlayerListName(null);
		DisplayNametags.updateNametag(spectator, GameUtils.getIDs());
		
	}
	
	private static void setItems(Player spectator) {
		
		ItemStack teleportMiddle = new ItemStack(Material.NETHER_STAR);
		ItemMeta teleportMiddleMeta = teleportMiddle.getItemMeta();
		teleportMiddleMeta.setDisplayName("§6Teleport to the middle §7(Click on it)");
		teleportMiddle.setItemMeta(teleportMiddleMeta);
		
		spectator.getInventory().setItem(2, teleportMiddle);
		
		ItemStack openSelector = new ItemStack(Material.COMPASS);
		ItemMeta openSelectorMeta = openSelector.getItemMeta();
		openSelectorMeta.setDisplayName("§6Open the selector §7(Click on it)");
		openSelector.setItemMeta(openSelectorMeta);
		
		spectator.getInventory().setItem(4, openSelector);
		
		ItemStack nightVision = new ItemStack(Material.EYE_OF_ENDER);
		ItemMeta nightVisionMeta = nightVision.getItemMeta();
		nightVisionMeta.setDisplayName("§6Night Vision §7(Click on it)");
		nightVision.setItemMeta(nightVisionMeta);
		if (spectator.hasPotionEffect(PotionEffectType.NIGHT_VISION)) nightVision = ItemsUtils.addGlow(nightVision);
		
		spectator.getInventory().setItem(6, nightVision);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		
		Player spectator = event.getPlayer();
		
		if (!GameUtils.getSpectate(spectator.getUniqueId())) return;
		
		LoginPlayer.updateVisibility();
		
		PlayerUtils.clearInventory(spectator);
		PlayerUtils.clearEffects(spectator);
		PlayerUtils.clearXp(spectator);
		spectator.setGameMode(GameMode.SPECTATOR);
		setItems(spectator);
		
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Rank rank = PlayerUtils.getRank(uuid);
		
		if (!GameUtils.getSpectate(uuid)) return;
		
		if (rank == Rank.ADMIN || rank == Rank.HOST || rank == Rank.STAFF) return;
		
		if (event.getMessage().equalsIgnoreCase("gg")) return;
		
		event.setCancelled(true);
		player.performCommand("sc " + event.getMessage());
		
	}
	
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Action action = event.getAction();
		
		if (!GameUtils.getSpectate(uuid)) return;
		
		if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return;
		
		List<UUID> alives = GameUtils.getAlives();
		
		if (alives.isEmpty()) {
			
			event.getPlayer().sendMessage(Main.PREFIX + "There are no players alive.");
			return;
			
		}
		
		Player teleport = null;
		int i = 0;
		
		while (teleport == null) {
			
			if (i > alives.size()) return;
			
			int random = new Random().nextInt(alives.size());
			teleport = Bukkit.getPlayer(alives.get(random));
			i++;
			
		}
		
		player.teleport(teleport);
		
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Action action = event.getAction();
		
		if (!GameUtils.getSpectate(uuid)) return;
		
		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
		
		if (action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock() instanceof InventoryHolder) return;
		
		player.openInventory(SelectorInventory.getSelector(uuid));
		
	}
	
	@EventHandler
	public void onRightClickEntity(PlayerInteractEntityEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (!(event.getRightClicked() instanceof Player)) return;
		
		Player clicked = (Player) event.getRightClicked();
		
		if (!GameUtils.getSpectate(uuid) || GameUtils.getSpectate(clicked.getUniqueId())) return;
		
		player.openInventory(PlayerInventory.getInventory(clicked.getUniqueId()));	
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		ItemMeta meta = item.getItemMeta();
		
		if (meta.getDisplayName().equals("§6Night Vision §7(Click on it)")) {
			
			event.setCancelled(true);
			
			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			else player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
			
			return;
			
		}
		
		if (meta.getDisplayName().equals("§6Open the selector §7(Click on it)")) {
			
			event.setCancelled(true);
			
			player.openInventory(SelectorInventory.getSelector(player.getUniqueId()));	
			return;
			
		}
		
		if (meta.getDisplayName().equals("§6Teleport to the middle §7(Click on it)")) {
			
			event.setCancelled(true);
			
			World world = Bukkit.getWorld(GameUtils.getWorld());
			
			if (world == null) return;
			
			Location center = world.getWorldBorder().getCenter();
			center.setY(WorldUtils.getHighestY((int) center.getX(), (int) center.getZ(), world) + 5);
			player.teleport(center);
			
			return;
			
		}
		
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Location location = event.getTo();
		World world = player.getWorld();
		double radius = world.getWorldBorder().getSize() / 2;
		
		if (!GameUtils.getSpectate(uuid)) return;
		
		if (location.getY() <= 1) {
		
			event.setCancelled(true);
			return;
			
		}
		
		if (location.getX() < radius && location.getX() > -radius && location.getZ() < radius && location.getZ() > -radius) return;
		
		Location teleport = player.getLocation();
		
		if (location.getX() > radius - 5 || location.getX() < -radius + 5) teleport.setX(location.getX()  > 0 ? radius - 5 : radius + 5);
		if (location.getZ() > radius - 5 || location.getZ() < -radius + 5) teleport.setZ(location.getZ()  > 0 ? radius - 5 : radius + 5);
		
		player.teleport(teleport);
		
	}

}
