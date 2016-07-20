package com.thetonyk.UHC.Inventories;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.github.paperspigot.Title;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Commands.HostCommand;
import com.thetonyk.UHC.Features.DisplaySidebar;
import com.thetonyk.UHC.Features.SpecInfo;
import com.thetonyk.UHC.GUI.AnvilGUI;
import com.thetonyk.UHC.GUI.NumberGUI;
import com.thetonyk.UHC.GUI.NumberGUI.NumberCallback;
import com.thetonyk.UHC.GUI.SignGUI;
import com.thetonyk.UHC.GUI.AnvilGUI.AnvilCallback;
import com.thetonyk.UHC.GUI.SignGUI.SignCallback;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.GameType;
import com.thetonyk.UHC.Utils.GameUtils.TeamType;
import com.thetonyk.UHC.Utils.ItemsUtils;
import com.thetonyk.UHC.Utils.MatchesUtils;
import com.thetonyk.UHC.Utils.WorldUtils;
import com.thetonyk.UHC.Utils.MatchesUtils.Match;
import com.thetonyk.UHC.Utils.MatchesUtils.MatchesCallback;

import net.dean.jraw.ApiException;
import twitter4j.TwitterException;

public class GameInventory implements Listener {
	
	private static String TITLE_PREFIX = "§8⫸ §4Host§8: §8";
	private Inventory inventory;
	private String hour;
	private long time;
	private SimpleDateFormat format;
	private Page page;
	private TeamType teamType;
	private int teamSize;
	private int slots;
	private int pvp;
	private int meetup;
	private GameType game;
	private URL twitter;
	private URL reddit;
	private Boolean autoOpen;
	private int size;
	private Boolean newStone;
	private Boolean nether;
	private Boolean end;
	private long seed;
	private String lastSeed;
	
	public GameInventory() {
		
		this.page = Page.BASIC;
		this.inventory = Bukkit.createInventory(null, this.page.getSize(), TITLE_PREFIX + this.page.getName());
		this.hour = null;
		this.time = 0;
		this.format = new SimpleDateFormat("dd/MM HH:mm");
		this.format.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.teamType = null;
		this.teamSize = 1;
		this.slots = 150;
		this.pvp = 20;
		this.meetup = 60;
		this.game = GameType.REDDIT;
		this.twitter = null;
		this.reddit = null;
		this.autoOpen = false;
		this.size = 2000;
		this.newStone = false;
		this.nether = false;
		this.end = false;
		this.seed = 0;
		this.lastSeed = null;
		
		update();
		
		Bukkit.getPluginManager().registerEvents(this, Main.uhc);
		
	}
	
	public Inventory getInventory() {
		
		return inventory;
		
	}
	
	private void update() {
		
		update(null);
		
	}
	
	private void update(List<Match> uhcs) {
		
		ItemStack separator = ItemsUtils.createItem(Material.STAINED_GLASS_PANE, "§7UHC by CommandsPVP", 1, 7);
		
		for (int i = 0; i < inventory.getSize(); i++) {
			
			inventory.setItem(i, separator);
			
		}
		
		List<String> lore = new ArrayList<String>();
		
		switch (this.page) {
			
			case BASIC:
				
				ItemStack team = ItemsUtils.createItem(Material.BANNER, "§8⫸ §7Teams: §6" + (this.teamType == null ? "FFA" : SpecInfo.formatName(this.teamType.toString()) + (this.teamSize > 1 ? " Team of " + this.teamSize : "")), (this.teamSize > 1 ? this.teamSize : 1), this.teamType == null ? 1 : 10);
				ItemStack slots = ItemsUtils.createItem(Material.SKULL_ITEM, "§8⫸ §7Slots: §a" + this.slots, (int) Math.floor(this.slots / 10), 3);
				ItemStack pvp = ItemsUtils.createItem(Material.IRON_SWORD, "§8⫸ §7PVP: §a" + this.pvp + "min", 1, 0);
				pvp = ItemsUtils.hideFlags(pvp);
				ItemStack meetup = ItemsUtils.createItem(Material.IRON_FENCE, "§8⫸ §7Meetup: §a" + this.meetup + "min", 1, 0);
				
				lore.add(" ");
				formatLore(lore, this.game.getDescription());
				lore.add(" ");
				ItemStack game = ItemsUtils.createItem(Material.PAPER, "§8⫸ §7Type: §6" + SpecInfo.formatName(this.game.toString()), 1, 0, lore);
				lore.clear();
				
				Boolean valid = true;
				
				if (this.time < 1 && this.hour != null) {
					
					lore.add(" ");
					lore.add("§8⫸ §7Last entered hour: §c" + this.hour);
					lore.add(" ");
					
					if (!isValid(this.hour)){
						
						lore.add("§8⫸ §7This hour is not valid.");
						lore.add(" ");
						valid = false;
						
					} else {
						
						long parsedTime = getTime(this.hour);
						Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
						int nowDay = calendar.get(Calendar.DAY_OF_MONTH);
						int nowMonth = calendar.get(Calendar.MONTH);
						calendar.setTimeInMillis(parsedTime);
						int minutes = calendar.get(Calendar.MINUTE);
						int day = calendar.get(Calendar.DAY_OF_MONTH);
						int month = calendar.get(Calendar.MONTH);
						
						if (this.game == GameType.REDDIT) {
						
							if (minutes != 0 && minutes != 15 && minutes != 30 && minutes != 45) {
								
								lore.add("§8⫸ §7You can only schedule UHC at these times:");
								lore.add("§8⫸ §6XX:00 §8| §6XX:15 §8| §6XX:30 §8| §6XX:45");
								lore.add(" ");
								valid = false;
								
							}
							
							if (month > nowMonth + 1 || (month > nowMonth && day > nowDay)) {
								
								lore.add("§8⫸ §7UHC must be scheduled at maximum a month.");
								lore.add(" ");
								valid = false;
								
							}
							
							if ((parsedTime - 1800000) < new Date().getTime()) {
								
								lore.add("§8⫸ §7UHC must be scheduled at least 30min before.");
								lore.add(" ");
								valid = false;
								
							}
							
							if (uhcs == null) {
								
								lore.add("§8⫸ §7Checking others UHC on Reddit...");
								lore.add(" ");
								valid = false;
								
							}
							else {
								
								for (Match uhc : uhcs) {
									
									if (parsedTime != uhc.getTime() * 1000) continue;
									
									lore.add("§8⫸ §cAn UHC is already scheduled at this hour.");
									lore.add(" ");
									valid = false;
									break;
									
								}
								
							}
						
						}
						
					}
					
				}
				
				if (valid && this.hour != null) {
					
					this.time = getTime(this.hour);
					lore.clear();
					
				}
				
				ItemStack date = ItemsUtils.createItem(Material.WATCH, "§8⫸ §7Time: §6" + (this.time < 1 ? "§cNone" : this.format.format(new Date(this.time))), 1, 0, lore);
				lore.clear();
				
				lore.add(" ");
				String text = "The whitelist will " + (this.autoOpen ? "" : "Not") + " be automatically open at the hour of the UHC";
				formatLore(lore, text);
				lore.add(" ");
				ItemStack autoOpen = ItemsUtils.createItem(Material.COMMAND, "§8⫸ §7Auto-Open: " + (this.autoOpen ? "§aEnabled" : "§cDisabled"), 1, 0, lore);
				lore.clear();
				
				this.inventory.setItem(10, team);
				this.inventory.setItem(11, slots);
				this.inventory.setItem(12, pvp);
				this.inventory.setItem(13, meetup);
				this.inventory.setItem(14, game);
				this.inventory.setItem(15, date);
				this.inventory.setItem(16, autoOpen);
				break;
			case TEAMS:
				
				ItemStack ffa = ItemsUtils.createItem(Material.STAINED_CLAY, "§8⫸ " + (this.teamType == null ? "§a" : "§c") + "FFA", 1, 4);
				ItemStack auction = ItemsUtils.createItem(Material.STAINED_CLAY, "§8⫸ " + (this.teamType == TeamType.AUCTION ? "§a" : "§c") + "Auction", 1, 4);
				
				if (this.teamType == null) ffa = ItemsUtils.addGlow(ffa);
				if (this.teamType == TeamType.AUCTION) auction = ItemsUtils.addGlow(auction);
				
				this.inventory.setItem(10, ffa);
				this.inventory.setItem(11, auction);
				
				int slot = 12;
				
				for (int i = 2; i <= 20; i++) {
					
					while (slot % 9 == 0 || slot % 9 == 8) {
						
						slot++;
						
					}
					
					ItemStack item = ItemsUtils.createItem(Material.BANNER, "§8⫸ §7Team of " + (this.teamSize == i ? "§a" : "§c") + i, 1, this.teamSize == i ? 10 : 1);
					if (this.teamSize == i) item = ItemsUtils.addGlow(item);
					
					this.inventory.setItem(slot, item);
					slot++;
					
				}
				
				slot = this.inventory.getSize() - 7;
				
				for (TeamType type : TeamType.values()) {
					
					if (type == TeamType.AUCTION) continue;
						
					ItemStack item = ItemsUtils.createItem(Material.STAINED_CLAY, "§8⫸ §7Type: " + (this.teamType == type ? "§a" : "§c") + SpecInfo.formatName(type.toString()), 1, this.teamType == type ? 3 : 11);
					if (this.teamType == type) item = ItemsUtils.addGlow(item);
					
					this.inventory.setItem(slot, item);
					slot++;
				
				}
				
				break;
			case WORLD:
				
				ItemStack size = ItemsUtils.createItem(Material.MAP, "§8⫸ §7Size: §a" + this.size + "x" + this.size, 1, 0);
				size = ItemsUtils.hideFlags(size);
				ItemStack newStone = ItemsUtils.createItem(Material.STONE, "§8⫸ §7New 1.8 stones: " + (this.newStone ? "§aEnabled" : "§cDisabled"), 1, 1);
				ItemStack nether = ItemsUtils.createItem(Material.NETHERRACK, "§8⫸ §7Nether: " + (this.nether ? "§aEnabled" : "§cDisabled"), 1, 0);
				ItemStack end = ItemsUtils.createItem(Material.ENDER_STONE, "§8⫸ §7The End: " + (this.end ? "§aEnabled" : "§cDisabled"), 1, 0);
				
				if (this.seed == 0 && this.lastSeed != null) {
					
					lore.add(" ");
					lore.add("§8⫸ §7Last entered seed: §c" + this.lastSeed);
					lore.add("§8⫸ §7This seed is incorrect.");
					lore.add(" ");
					
				}
				
				ItemStack seed = ItemsUtils.createItem(Material.GRASS, "§8⫸ §7Seed: §a" + (this.seed == 0 ? "§cRandom" : this.seed), 1, 0, lore);
				lore.clear();
				
				this.inventory.setItem(11, size);
				this.inventory.setItem(12, newStone);
				this.inventory.setItem(13, nether);
				this.inventory.setItem(14, end);
				this.inventory.setItem(15, seed);
				break;
			
		}
		
		
		ItemStack cancel = ItemsUtils.createItem(Material.STAINED_CLAY, "§8⫸ §c" + (this.page.ordinal() < 1 ? "Cancel" : "Back"), 1, 14);
		ItemStack valid = ItemsUtils.createItem(Material.STAINED_CLAY, "§8⫸ §a" + (this.page.next().ordinal() <= this.page.ordinal() ? "Confirm" : "Next"), 1, 5);
		
		this.inventory.setItem(this.inventory.getSize() - 9, cancel);
		if (this.page.main() == null) this.inventory.setItem(this.inventory.getSize() - 1, valid);
		
	}
	
	@Override
	protected void finalize() {
		
		cancel();
		
	}
	
	private void cancel() {
		
		HandlerList.unregisterAll(this);
		
		for (HumanEntity viewer : new ArrayList<HumanEntity>(inventory.getViewers())) {
			
			viewer.closeInventory();
			
		}
		
		for (Map.Entry<UUID, GameInventory> entry : HostCommand.inventories.entrySet()) {
			
			if (!entry.getValue().equals(this)) continue;
			
			HostCommand.inventories.remove(entry.getKey(), this);
			
		}
		
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		
		if (!event.getInventory().equals(this.inventory)) return;
		
		event.setCancelled(true);
		
		ItemStack item = event.getCurrentItem();
		
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		switch (this.page) {
		
			case BASIC:
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Teams: §6")) {
					
					changePage(Page.TEAMS);
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Slots: §a")) {
					
					NumberGUI gui = new NumberGUI("Slots", this.slots, 10, 1, 150, 200, 5, new NumberCallback<Integer>() {
						
						@Override
						public void onConfirm(int newSlots) {
							
							slots = newSlots;
							update();
							player.openInventory(inventory);
							
						}
						
						@Override
						public void onCancel() {
							
							player.openInventory(inventory);
							
						}
						
						@Override
						public void onDisconnect() {}
						
					});
					
					player.openInventory(gui.getInventory());
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7PVP: §a")) {
					
					NumberGUI gui = new NumberGUI("PVP Time", this.pvp, 10, 1, 20, 180, 1, new NumberCallback<Integer>() {

						@Override
						public void onConfirm(int newPvp) {
							
							pvp = newPvp;
							update();
							player.openInventory(inventory);
							
						}

						@Override
						public void onCancel() {
							
							player.openInventory(inventory);
							
						}

						@Override
						public void onDisconnect() {}
						
					});
					
					player.openInventory(gui.getInventory());
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Meetup: §a")) {
					
					NumberGUI gui = new NumberGUI("Meetup Time", this.meetup, 10, 1, 60, 240, 1, new NumberCallback<Integer>() {

						@Override
						public void onConfirm(int newMeetup) {
							
							meetup = newMeetup;
							update();
							player.openInventory(inventory);
							
						}

						@Override
						public void onCancel() {
							
							player.openInventory(inventory);
							
						}

						@Override
						public void onDisconnect() {}
						
					});
					
					player.openInventory(gui.getInventory());
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Type: §6")) {
					
					this.game = this.game.next();
					
					if (this.game == GameType.REDDIT) {
						
						Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
						calendar.setTimeInMillis(this.time);
						int day = calendar.get(Calendar.DAY_OF_MONTH);
						int month = calendar.get(Calendar.MONTH);
						int hour = calendar.get(Calendar.HOUR_OF_DAY);
						int minutes = calendar.get(Calendar.MINUTE);
						
						this.hour = day + "/" + month + " " + hour + ":" + minutes;
						this.time = 0;
						
					}
					
					update();
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Time: §6")) {
					
					String[] text = new String[1];
					Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
					int hours = calendar.get(Calendar.HOUR_OF_DAY);
					int minutes = calendar.get(Calendar.MINUTE);
					
					text[0] = (this.time < 1 && this.hour != null) ? this.hour : (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
					
					new SignGUI(player, text, new SignCallback<String[]>() {
		
						@Override
						public void onConfirm(String[] lines) {
							
							player.openInventory(inventory);
							
							if (lines[0].length() + lines[1].length() + lines[2].length() + lines[3].length() < 1) return;
							
							String text = null;
							
							for (String line : lines) {
								
								if (line.length() < 1) continue;
								
								text = line;
								break;
								
							}
							
							time = 0;
							hour = text;
							update();
							
							MatchesUtils.getUpcomingMatches(new MatchesCallback<List<Match>>() {
		
								@Override
								public void onSuccess(List<Match> done) {
									
									update(done);
									
								}
		
								@Override
								public void onFailure(Throwable exception) {}
								
							});
							
						}
		
						@Override
						public void onDisconnect() {}
						
					});
					
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Auto-Open: ")) {
					
					this.autoOpen = !this.autoOpen;
					update();
					return;
					
				}
			
				break;
			case TEAMS:
				
				if (item.getItemMeta().getDisplayName().endsWith("FFA")) {
					
					this.teamType = null;
					this.teamSize = 1;
					update();
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().endsWith("Auction")) {
					
					this.teamType = TeamType.AUCTION;
					this.teamSize = 0;
					update();
					return;
					
				}

				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Team of ")) {
					
					int size = 0;
					String[] name = item.getItemMeta().getDisplayName().split(" ");
					
					try {
						
						size = Integer.parseInt(name[name.length - 1].substring(2));
						
					} catch (Exception exception) {
						
						return;
						
					}
					
					if (this.teamType == null || this.teamType == TeamType.AUCTION) this.teamType = TeamType.CHOSEN;
					
					this.teamSize = size;
					update();
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Type: ")) {
					
					TeamType type = null;
					String[] name = item.getItemMeta().getDisplayName().split(" ");
					
					try {
						
						type = TeamType.valueOf(name[name.length - 1].substring(2).toUpperCase());
						
					} catch (Exception exception) {
						
						break;
						
					}
					
					if (this.teamSize < 2) this.teamSize = 2;
					
					this.teamType = type;
					update();
					return;
					
				}
				
				break;
			case WORLD:
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Size: §a")) {
					
					NumberGUI gui = new NumberGUI("Size", this.size, 1000, 100, 2000, 10000, 100, new NumberCallback<Integer>() {

						@Override
						public void onConfirm(int newSize) {
							
							player.openInventory(inventory);
							size = newSize;
							update();
							
						}

						@Override
						public void onCancel() {
							
							player.openInventory(inventory);
							
						}

						@Override
						public void onDisconnect() {}
						
					});
					
					player.openInventory(gui.getInventory());
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7New 1.8 stones: ")) {
					
					this.newStone = !this.newStone;
					update();
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Nether: ")) {
					
					this.nether = !this.nether;
					update();
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7The End: ")) {
					
					this.end = !this.end;
					update();
					return;
					
				}
				
				if (item.getItemMeta().getDisplayName().startsWith("§8⫸ §7Seed: §a")) {
					
					new AnvilGUI(player, this.seed == 0 && this.lastSeed == null ? "Seed..." : this.seed == 0 ? this.lastSeed : String.valueOf(this.seed), new AnvilCallback<String>(){

						@Override
						public void onConfirm(String text) {
							
							player.openInventory(inventory);
							
							if (text.length() < 1) return;
							
							long longSeed = 0;
							
							try {
								
								longSeed = Long.parseLong(text);
								
							} catch (Exception exception) {
								
								lastSeed = text;
								seed = 0;
								update();
								return;
								
							}
							
							seed = longSeed;
							lastSeed = null;
							update();
							
						}

						@Override
						public void onClose() {
							
							player.openInventory(inventory);
							
						}
						
					});
					
				}
				
				break;

		}
		
		if (item.getItemMeta().getDisplayName().equals("§8⫸ §cBack")) {
			
			Page page = this.page.main() != null ? this.page.main() : this.page.previous();
			
			changePage(page);
			return;
			
		}
		
		if (item.getItemMeta().getDisplayName().equals("§8⫸ §cCancel")) {
			
			cancel();
			return;
			
		}
		
		if (item.getItemMeta().getDisplayName().equals("§8⫸ §aNext")) {
			
			changePage(this.page.next());
			return;
			
		}
		
		if (item.getItemMeta().getDisplayName().equals("§8⫸ §aConfirm")) {
			
			player.closeInventory();
			
			if (this.game == GameType.REDDIT || this.game == GameType.TWITTER) {
			
				SimpleDateFormat hour = new SimpleDateFormat("HH:mm zzz");
				hour.setTimeZone(TimeZone.getTimeZone("UTC"));
				String tweet = "⫸ " + (GameUtils.getTeamType() == null ? "FFA" : (GameUtils.getTeamSize() < 2 ? SpecInfo.formatName(GameUtils.getTeamType().name()) : GameUtils.getTeamType().getSyntax() + "To" + GameUtils.getTeamSize())) + " | Vanila+\n\n⫸ Open: " + hour.format(new Date(this.time)) + /*"\n⫸ Post: " + this.reddit.toString() + */"\n\n⫸ Host: " + player.getName() + "\n⫸ IP: commandspvp.com";
				
				MatchesUtils.postTweet(Main.uhc.getConfig().getString("TwitterConsumerKey"), Main.uhc.getConfig().getString("TwitterConsumerSecret"), Main.uhc.getConfig().getString("TwitterAccessToken"), Main.uhc.getConfig().getString("TwitterAccessSecret"), tweet, new MatchesCallback<URL>() {
	
					@Override
					public void onSuccess(URL url) {
						
						twitter = url;
						
						if (game == GameType.TWITTER) {
							
							cancel();
							Bukkit.broadcastMessage(Main.PREFIX + "The UHC has been schedule for the " + format.format(time) + ".");
							Bukkit.broadcastMessage(Main.PREFIX + twitter.toString());
							return;
							
						}
						
						SimpleDateFormat hour = new SimpleDateFormat("MMM dd HH:mm zzz");
						hour.setTimeZone(TimeZone.getTimeZone("UTC"));
						String title = hour.format(new Date(time)) + " EU - " + player.getName() + " #65 - " + (GameUtils.getTeamType() == null ? "FFA" : (GameUtils.getTeamSize() < 2 ? SpecInfo.formatName(GameUtils.getTeamType().name()) : GameUtils.getTeamType().getSyntax() + "To" + GameUtils.getTeamSize())) + " - Vanilla+ [Beta Game] [UHC by CommandsPVP]";
						String text = "Match|Informations\n:--|:--\n**WH Open**|" + hour.format(new Date(time)) + "\n**Start**|" + (GameUtils.getTeamSize() > 1 && GameUtils.getTeamType() == TeamType.CHOSEN ? "10" : "5") + " minutes after WH open\n**Team Size**|" + (GameUtils.getTeamType() == null ? "FFA" : SpecInfo.formatName(GameUtils.getTeamType().name())) + (GameUtils.getTeamSize() > 1 ? " To" + GameUtils.getTeamSize() : "") + "\n**Map Size**|2000x2000\n**PVP Time**|" + pvp + ":00 after start\n**Meetup Time**|" + meetup + ":00 after start\n**Gamemode**|Vanilla\n**Slots**|" + slots + " slots\n\nScénarios|Informations\n:--|:--\n**Vanilla**|Just a normal UHC.\n\nServer|Informations\n:--|:--\n**Server**|Dedicated Server (*OVH*)\n**Location**|Paris, *France*\n**RAM**|10 GB\n**Version**|Server: 1.8.8 (Compatible : 1.8.0 - 1.8.9)\n**IP**|commandspvp.com ^Direct ^IP: ^uhc.commandspvp.com\n\nGame|Informations\n:--|:--\n**Nether**|Disabled\n**Golden Head**|Enabled\n**Starter Food**|10 Cooked Beef\n**Mining Rules**|Rollercoaster\n**Absorption**|Enabled\n**Saturation Fix**|Enabled\n\nUse **/rules** in-game for any informations";
						
						new MatchesUtils.Submit(player, title, text, "UHCMatches", new MatchesCallback<URL>() {
							
							@Override
							public void onSuccess(URL url) {
								
								reddit = url;
								cancel();
								Bukkit.broadcastMessage(Main.PREFIX + "The UHC has been schedule for the " + format.format(time) + ".");
								Bukkit.broadcastMessage("§7" + twitter.toString());
								Bukkit.broadcastMessage("§7" + reddit.toString());
								
							}

							@Override
							public void onFailure(Throwable exception) {
								
								player.openInventory(inventory);
								
								if (exception == null) return;
								
								if (exception instanceof ApiException) {
									
									ApiException error = (ApiException) exception;
									
									player.sendMessage(Main.PREFIX + "An error from Reddit has occurred.");
									player.sendMessage(Main.PREFIX + error.getExplanation());
									return;
									
								}
								
								exception.printStackTrace();
								player.sendMessage(Main.PREFIX + "An error has occurred while posting on Reddit.");
								player.sendMessage(Main.PREFIX + exception.getMessage());
								
							}
							
						});
						
					}
	
					@Override
					public void onFailure(Throwable exception) {
						
						player.openInventory(inventory);
						
						if (exception == null) return;
						
						if (exception instanceof TwitterException) {
							
							TwitterException error = (TwitterException) exception;
							
							player.sendMessage(Main.PREFIX + "An error from Twitter has occured. (Code: " + error.getStatusCode() + ")");
							player.sendMessage(Main.PREFIX + error.getErrorMessage());
							return;
							
						}
						
						exception.printStackTrace();
						player.sendMessage(Main.PREFIX + "An error has occurred while tweeting.");
						player.sendMessage(Main.PREFIX + exception.getMessage());
						
					}
					
				});
			
			} else Bukkit.broadcastMessage(Main.PREFIX + "The UHC has been schedule for the " + format.format(time) + ".");
			
			GameUtils.setTeamType(this.teamType);
			GameUtils.setSlots(this.slots);
			GameUtils.setPVP(this.pvp);
			GameUtils.setMeetup(this.meetup);
			GameUtils.setDate(this.time);
			GameUtils.setHost(player.getUniqueId());
			GameUtils.setTeamSize(this.teamSize);
			GameUtils.setAutoOpen(this.autoOpen);
			if (this.autoOpen) GameUtils.scheduleOpening(this.time);
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				
				DisplaySidebar.update(online);
				
			}
			
			if (this.seed == 0) this.seed = new Random().nextLong();
			
			Title title = new Title("§cWorld Creation", "§7The server is currently freezed", 0, 1200, 0);
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				
				online.sendTitle(title);
				
			}
			
			String name = player.getName().toLowerCase();
			int i = 0;
			
			while (WorldUtils.exist(name)) {
				
				if (i > 1000) break;
				
				if (i == 0) {
					
					name += String.valueOf(i);
					i++;
					
				}
				
				name = name.substring(0, name.length() - 2) + String.valueOf(i);
				
			}

			WorldUtils.createWorld(name, Environment.NORMAL, this.seed, WorldType.NORMAL, this.size, this.newStone);
			
			if (this.nether) WorldUtils.createWorld(name + "_nether", Environment.NETHER, this.seed, WorldType.NORMAL, this.size, this.newStone);	
			
			if (this.end) WorldUtils.createWorld(name + "_end", Environment.THE_END, this.seed, WorldType.NORMAL, this.size, this.newStone);
			
			Bukkit.broadcastMessage(Main.PREFIX + "The game world has been created.");
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				
				online.hideTitle();
				
			}
			
			GameUtils.setWorld(name);
			return;
			
		}
		
	}
	
	private Boolean isValid(String time) {
		
		Pattern pattern = Pattern.compile("^([0-9]{1,2}\\/[0-9]{1,2} )?[0-9]{1,2}(:|h|H)[0-9]{0,2}$");
		Matcher matcher = pattern.matcher(time);
		
		return matcher.matches();
		
	}
	
	private long getTime(String hour) {
		
		int day = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH).get(Calendar.DAY_OF_MONTH);
		int month = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH).get(Calendar.MONTH);
		
		if (hour.contains(" ")) {
			
			String date = hour.split(" ")[0];
			hour = hour.split(" ")[1];
			
			Pattern pattern = Pattern.compile("/");
			Matcher matcher = pattern.matcher(date);
			
			matcher.find();
			
			day = Integer.parseInt(date.substring(0, matcher.start()));
			month = Integer.parseInt(date.substring(matcher.start() + 1)) - 1;
			
		}
		
		Pattern pattern = Pattern.compile("(:|h|H)");
		Matcher matcher = pattern.matcher(hour);
		
		int hours = 0;
		int minutes = 0;
		
		matcher.find();
			
		hours = Integer.parseInt(hour.substring(0, matcher.start()));
		if (hour.length() > matcher.start() + 1) minutes = Integer.parseInt(hour.substring(matcher.start() + 1));
		
		SimpleDateFormat format = new SimpleDateFormat("dd MM HH:mm zzz");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		if (!hour.contains(" ") && calendar.getTimeInMillis() < new Date().getTime()) calendar.set(Calendar.DAY_OF_MONTH, day + 1);
		
		if (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) < day + 1) {
			
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, month == 12 ? 1 : month + 1);
			if (month == 12) calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
			
		}
		
		return calendar.getTimeInMillis();
		
	}
	
	private static void formatLore(List<String> lore, String text) {
		
		String line = "§8⫸ §7";
		
		for (String word : text.split(" ")) {
			
			line += word + " ";
			
			if (line.split(" ").length >= 4) {
				
				lore.add(line);
				line = "§8⫸ §7";
				
			}
			
		}
		
		if (line.length() > 7) lore.add(line);
		
	}
	
	private void changePage(Page page) {
		
		List<HumanEntity> viewers = new ArrayList<HumanEntity>(this.inventory.getViewers());
		
		this.inventory = Bukkit.createInventory(null, page.getSize(), TITLE_PREFIX + page.getName());
		this.page = page;
		update();
		
		for (HumanEntity viewer : viewers) {
			
			viewer.openInventory(inventory);
			
		}
		
	}
	
	private enum Page {
		
		BASIC("Config", 27, null), TEAMS("Teams", 54, Page.BASIC), WORLD("World", 27, null);
		
		private static Page[] values = values();
		private String name;
		private int size;
		private Page main;
		
		private Page(String name, int size, Page main) {
			
			this.name = name;
			this.size = size;
			this.main = main;
			
		}
		
		public Page next() {
			
			Page next = this;
			
			do {
				
				next = values[(next.ordinal() + 1) % values.length];
				
			} while (next.main != null);
			
			return next;
			
		}
		
		public Page previous() {
			
			Page next = this;
			
			do {
				
				next = values[(next.ordinal() - 1) % values.length];
				
			} while (next.main != null);
			
			return next;
			
		}
		
		public String getName() {
			
			return this.name;
			
		}
		
		public int getSize() {
			
			return this.size;
			
		}
		
		public Page main() {
			
			return this.main;
			
		}
		
	}

}
