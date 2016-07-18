package com.thetonyk.UHC;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Commands.AcceptCommand;
import com.thetonyk.UHC.Commands.BorderCommand;
import com.thetonyk.UHC.Commands.ButcherCommand;
import com.thetonyk.UHC.Commands.ClearCommand;
import com.thetonyk.UHC.Commands.DQCommand;
import com.thetonyk.UHC.Commands.EndgameCommand;
import com.thetonyk.UHC.Commands.FeedCommand;
import com.thetonyk.UHC.Commands.FlyCommand;
import com.thetonyk.UHC.Commands.GamemodeCommand;
import com.thetonyk.UHC.Commands.GiveCommand;
import com.thetonyk.UHC.Commands.HealCommand;
import com.thetonyk.UHC.Commands.HealthCommand;
import com.thetonyk.UHC.Commands.HelpopCommand;
import com.thetonyk.UHC.Commands.HostCommand;
import com.thetonyk.UHC.Commands.InviteCommand;
import com.thetonyk.UHC.Commands.LagCommand;
import com.thetonyk.UHC.Commands.ListCommand;
import com.thetonyk.UHC.Commands.NearCommand;
import com.thetonyk.UHC.Commands.NosoundCommand;
import com.thetonyk.UHC.Commands.PVPCommand;
import com.thetonyk.UHC.Commands.PmCommand;
import com.thetonyk.UHC.Commands.PregenCommand;
import com.thetonyk.UHC.Commands.RankCommand;
import com.thetonyk.UHC.Commands.RespawnCommand;
import com.thetonyk.UHC.Commands.RulesCommand;
import com.thetonyk.UHC.Commands.SpecchatCommand;
import com.thetonyk.UHC.Commands.SpectateCommand;
import com.thetonyk.UHC.Commands.StartCommand;
import com.thetonyk.UHC.Commands.TCommand;
import com.thetonyk.UHC.Commands.TcCommand;
import com.thetonyk.UHC.Commands.TeamCommand;
import com.thetonyk.UHC.Commands.TeleportCommand;
import com.thetonyk.UHC.Commands.TextCommand;
import com.thetonyk.UHC.Commands.TimeleftCommand;
import com.thetonyk.UHC.Commands.TpCommand;
import com.thetonyk.UHC.Commands.WhitelistCommand;
import com.thetonyk.UHC.Commands.WorldCommand;
import com.thetonyk.UHC.Features.ChatCooldown;
import com.thetonyk.UHC.Features.ChatIgnoreSettings;
import com.thetonyk.UHC.Features.ChatSettings;
import com.thetonyk.UHC.Features.DeathMessage;
import com.thetonyk.UHC.Features.DeathRespawn;
import com.thetonyk.UHC.Features.HealthScore;
import com.thetonyk.UHC.Features.HealthShoot;
import com.thetonyk.UHC.Features.TeleportLate;
import com.thetonyk.UHC.Features.LobbyFly;
import com.thetonyk.UHC.Features.LobbyItems;
import com.thetonyk.UHC.Features.LobbyProtection;
import com.thetonyk.UHC.Features.LoginPlayer;
import com.thetonyk.UHC.Features.LoginWhitelist;
import com.thetonyk.UHC.Features.DQLogout;
import com.thetonyk.UHC.Features.DQPunishment;
import com.thetonyk.UHC.Features.LogoutPlayer;
import com.thetonyk.UHC.Features.MeetupEnable;
import com.thetonyk.UHC.Features.MeetupWarning;
import com.thetonyk.UHC.Features.HealthRegeneration;
import com.thetonyk.UHC.Features.PVPEnable;
import com.thetonyk.UHC.Features.PregenStates;
import com.thetonyk.UHC.Features.SpecInfo;
import com.thetonyk.UHC.Features.SpecPlayer;
import com.thetonyk.UHC.Features.TeleportProtection;
import com.thetonyk.UHC.Features.Options.OptionFeature;
import com.thetonyk.UHC.Features.TeamsInvitations;
import com.thetonyk.UHC.Features.DisplayNametags;
import com.thetonyk.UHC.Features.DisplayServerList;
import com.thetonyk.UHC.Features.DisplaySidebar;
import com.thetonyk.UHC.Features.DisplayTab;
import com.thetonyk.UHC.Features.DisplayTimers;
import com.thetonyk.UHC.Features.HealthFood;
import com.thetonyk.UHC.Inventories.InviteInventory;
import com.thetonyk.UHC.Inventories.PlayerInventory;
import com.thetonyk.UHC.Inventories.RulesInventory;
import com.thetonyk.UHC.Inventories.SelectorInventory;
import com.thetonyk.UHC.Inventories.TeamsInventory;
import com.thetonyk.UHC.Utils.BiomesUtils;
import com.thetonyk.UHC.Utils.DisplayUtils;
import com.thetonyk.UHC.Utils.TeamsUtils;
import com.thetonyk.UHC.Utils.WorldUtils;

import static net.md_5.bungee.api.ChatColor.*;

import java.io.File;

import net.md_5.bungee.api.chat.ComponentBuilder;

public class Main extends JavaPlugin {
	
	public static Main uhc;
	
	public static final String NO_PERMS = "§fUnknown command.";
	public static final String PREFIX = "§a§lUHC §8⫸ §7";
	
	@Override
	public void onEnable() {
		
		getLogger().info("UHC Plugin has been enabled.");
		getLogger().info("Plugin by TheTonyk for CommandsPVP");
		
		uhc = this;
		
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "CommandsBungee", new DQPunishment());
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "CommandsBungee");
		
		BiomesUtils.removeOceansAndJungles();
		DisplayUtils.redditHearts();
		DisplayUtils.playersCount();
		TeamsUtils.reload();
		WorldUtils.loadAllWorlds();
		
		getConfig().options().copyDefaults(false);
		saveConfig();
		
		this.getCommand("gamemode").setExecutor(new GamemodeCommand());
		this.getCommand("rank").setExecutor(new RankCommand());
		this.getCommand("world").setExecutor(new WorldCommand());
		this.getCommand("pregen").setExecutor(new PregenCommand());
		this.getCommand("border").setExecutor(new BorderCommand());
		this.getCommand("team").setExecutor(new TeamCommand());
		this.getCommand("teleport").setExecutor(new TeleportCommand());
		this.getCommand("invite").setExecutor(new InviteCommand());
		this.getCommand("accept").setExecutor(new AcceptCommand());
		this.getCommand("pm").setExecutor(new PmCommand());
		this.getCommand("pmcoords").setExecutor(new PmCommand());
		this.getCommand("pmores").setExecutor(new PmCommand());
		this.getCommand("t").setExecutor(new TCommand());
		this.getCommand("tc").setExecutor(new TcCommand());
		this.getCommand("whitelist").setExecutor(new WhitelistCommand());
		this.getCommand("butcher").setExecutor(new ButcherCommand());
		this.getCommand("helpop").setExecutor(new HelpopCommand());
		this.getCommand("start").setExecutor(new StartCommand());
		this.getCommand("timeleft").setExecutor(new TimeleftCommand());
		this.getCommand("rules").setExecutor(new RulesCommand());
		this.getCommand("fly").setExecutor(new FlyCommand());
		this.getCommand("clear").setExecutor(new ClearCommand());
		this.getCommand("feed").setExecutor(new FeedCommand());
		this.getCommand("heal").setExecutor(new HealCommand());
		this.getCommand("pvp").setExecutor(new PVPCommand());
		this.getCommand("health").setExecutor(new HealthCommand());
		this.getCommand("nosound").setExecutor(new NosoundCommand());
		this.getCommand("lag").setExecutor(new LagCommand());
		this.getCommand("give").setExecutor(new GiveCommand());
		this.getCommand("spectate").setExecutor(new SpectateCommand());
		this.getCommand("tp").setExecutor(new TpCommand());
		this.getCommand("near").setExecutor(new NearCommand());
		this.getCommand("specchat").setExecutor(new SpecchatCommand());
		this.getCommand("endgame").setExecutor(new EndgameCommand());
		this.getCommand("list").setExecutor(new ListCommand());
		this.getCommand("text").setExecutor(new TextCommand());
		this.getCommand("respawn").setExecutor(new RespawnCommand());
		this.getCommand("dq").setExecutor(new DQCommand());
		this.getCommand("host").setExecutor(new HostCommand());
		
		PluginManager manager = Bukkit.getPluginManager();
		
		//Lowest Priority
		manager.registerEvents(new LoginPlayer(), this);
		
		manager.registerEvents(new ChatCooldown(), this);
		manager.registerEvents(new ChatIgnoreSettings(), this);
		manager.registerEvents(new ChatSettings(), this);
		manager.registerEvents(new DeathMessage(), this);
		manager.registerEvents(new DeathRespawn(), this);
		manager.registerEvents(new DisplayNametags(), this);
		manager.registerEvents(new DisplaySidebar(), this);
		manager.registerEvents(new DisplayServerList(), this);
		manager.registerEvents(new DisplayTab(), this);
		manager.registerEvents(new DisplayTimers(), this);
		manager.registerEvents(new HealthScore(), this);
		manager.registerEvents(new HealthShoot(), this);
		manager.registerEvents(new HealthRegeneration(), this);
		manager.registerEvents(new HealthFood(), this);
		manager.registerEvents(new TeleportLate(), this);
		manager.registerEvents(new LobbyFly(), this);
		manager.registerEvents(new LobbyItems(), this);
		manager.registerEvents(new LobbyProtection(), this);
		manager.registerEvents(new LoginWhitelist(), this);
		manager.registerEvents(new DQLogout(), this);
		manager.registerEvents(new LogoutPlayer(), this);
		manager.registerEvents(new MeetupEnable(), this);
		manager.registerEvents(new MeetupWarning(), this);
		manager.registerEvents(new PregenStates(), this);
		manager.registerEvents(new PVPEnable(), this);
		manager.registerEvents(new SpecInfo(), this);
		manager.registerEvents(new SpecPlayer(), this);
		manager.registerEvents(new TeleportProtection(), this);
		manager.registerEvents(new TeamsInvitations(), this);
		
		manager.registerEvents(new InviteInventory(), this);
		manager.registerEvents(new PlayerInventory(), this);
		manager.registerEvents(new RulesInventory(), this);
		manager.registerEvents(new SelectorInventory(), this);
		manager.registerEvents(new TeamsInventory(), this);
		
		try {
        	
            Class.forName("com.thetonyk.UHC.Packets.PacketHandler");
            
        } catch (Exception exception) {
        	
            Main.uhc.getLogger().severe("[Main] Unable to load the packet hanlder.");
            
        }
		
		new BukkitRunnable() {
			
			public void run() {
				
				OptionFeature.setup();
				
			}
			
		}.runTaskLater(Main.uhc, 20);
		
	}
	
	@Override
	public void onDisable() {
		
		getLogger().info("UHC Plugin has been disabled.");
		
		BiomesUtils.resetBiomes();
		
		uhc = null;
		
	}
	
	public static ComponentBuilder getPrefixComponent() {
		
		return new ComponentBuilder("UHC ").color(GREEN).bold(true).append("⫸ ").color(DARK_GRAY).bold(false);
		
	}
	
	public File getJarFile() {
		
		return this.getFile();
		
	}
	
}
