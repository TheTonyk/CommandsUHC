package com.thetonyk.UHC.Features.Options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.thetonyk.UHC.Main;

public class OptionFeature {
	
	private static List<Option> options = new ArrayList<Option>();
	
	public static void setup() {
		
		Set<Class<?>> classes = null;
			 
		try {
			
			classes = getClasses("com.thetonyk.UHC.Features.Options");
			
		} catch (ClassNotFoundException | IOException exception) {
			
			Bukkit.getLogger().severe("[OptionFeature] Unable to setup options.");
			return;
			
		}
		 
		Bukkit.broadcastMessage(String.valueOf(classes.size()));
		 
		for (Class<? extends Object> option : classes) {
			
			Bukkit.broadcastMessage(option.getName());
			Object instance = null;
			
			try {
			
				instance = option.newInstance();
			
			} catch (InstantiationException | IllegalAccessException exception) {
				
				continue;
				
			}
			
			if (instance instanceof Listener) {
				
				Bukkit.getPluginManager().registerEvents((Listener) instance, Main.uhc);
				
			}
			
			if (instance instanceof Option) {
				
				options.add((Option) instance);
				
			}
			 
		}
		
	}
	
	private static Set<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
		
		Set<Class<?>> classes = new HashSet<Class<?>>();
		JarFile jar = new JarFile(Main.uhc.getJarFile());
		Enumeration<JarEntry> entries = jar.entries();
		
		while (entries.hasMoreElements()) {
 
			JarEntry entry = entries.nextElement();
			String name = entry.getName().replace("/", ".");
			
			if (!name.startsWith(packageName) || !name.endsWith(".class") || name.contains("$")) continue;
			
			classes.add(Class.forName(name.substring(0, name.length() - 6)));
			
		}
		
		jar.close();
		
		return classes;
		
	}
	
	public static class Option {
		
		private String name;
		private ItemStack icon;
		private Boolean state;
		
		public Option(String name, ItemStack icon, Boolean defaultState) {
			
			this.name = name;
			this.icon = icon;
			this.state = defaultState;
			
		}
		
		public ItemStack getIcon() {
			
			ItemStack item = icon.clone();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§8⫸ §6" + name + " §8⫷");
			item.setItemMeta(meta);
			return item;
			
		}
		
		public void enable() {
			
			if (isEnabled()) return;
			
			this.state = true;
			onEnable();
			
		}
		
		public void disable() {
			
			if (!isEnabled()) return;
			
			this.state = false;
			onDisable();
			
		}
		
		public Boolean isEnabled() {
			
			return this.state;
			
		}
		
		public void toggle() {
			
			if (isEnabled()) disable();
			else enable(); 
			
		}
		
		public void onEnable() {};
		public void onDisable() {};
		
	}

}
