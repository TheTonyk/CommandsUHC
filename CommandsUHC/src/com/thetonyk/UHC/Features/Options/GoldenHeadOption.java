package com.thetonyk.UHC.Features.Options;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Features.Options.OptionFeature.Option;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.ItemsUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

public class GoldenHeadOption extends Option implements Listener {
	
	private int regen;
	private static BlockFace[] sortedFaces = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
	
	public GoldenHeadOption() {
		
		super("Golden Head", new ItemStack(Material.GOLDEN_APPLE), true);
		
		Bukkit.addRecipe(new ShapedRecipe(ItemsUtils.createItem(Material.GOLDEN_APPLE, "§6Golden Head", 1, 0)).shape("AAA", "ABA", "AAA").setIngredient('A', Material.GOLD_INGOT).setIngredient('B', new ItemStack(Material.SKULL_ITEM, 1, (short) 3).getData()));
		
		this.regen = 8;
		
	}
	
	public int getRegen() {
		
		return this.regen;
		
	}
	
	public void setRegen(int amount) {
		
		this.regen = amount;
		
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		UUID uuid = player.getUniqueId();
		Location location = player.getLocation();
		Status status = GameUtils.getStatus();
		
		if (!isEnabled()) return;
		
		if (status != Status.PLAY || GameUtils.getDeath(uuid) || GameUtils.getSpectate(uuid)) return;
		
		if (location.getWorld().getName().equalsIgnoreCase("lobby")) return;
		
		new BukkitRunnable() {
			
			@SuppressWarnings("deprecation")
			public void run() {
				
				location.getBlock().setType(Material.COBBLE_WALL);
				location.setY(location.getY() + 1);
				location.getBlock().setType(Material.SKULL);
				
				try {
					
					Skull head = (Skull) location.getBlock().getState();
					head.setRotation(sortedFaces[Math.round(location.getYaw() / 45) & 0x7]);
					head.setSkullType(SkullType.PLAYER);
					head.setOwner(player.getName());
					head.update();
					location.getBlock().setData((byte) 0x1, true);
					
				} catch (Exception exception) {}
				
			}
			
		}.runTaskLater(Main.uhc, 1);
		
	}
	
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if (!isEnabled()) return;
		
		if (item.getType() != Material.GOLDEN_APPLE || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Golden Head")) return;
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regen * 25, 1));
		
	}
	
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		
		Recipe recipe = event.getRecipe();
		CraftingInventory inventory = event.getInventory();
		ItemStack item = recipe.getResult();
		
		if (item.getType() != Material.GOLDEN_APPLE || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().equalsIgnoreCase("§6Golden Head")) return;
		
		if (isEnabled()) return;
			
		inventory.setResult(new ItemStack(Material.AIR));
		
	}

}
