package com.thetonyk.UHC.Features.Options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils;

public class OptionFeature {
	
	private static Map<String, Option> options = new HashMap<String, Option>();
	
	public static void setup() {
		
		List<Class<?>> classes = null;
			 
		try {
			
			classes = getClasses("com.thetonyk.UHC.Features.Options");
			
		} catch (ClassNotFoundException | IOException exception) {
			
			Bukkit.getLogger().severe("[OptionFeature] Unable to setup options.");
			return;
			
		}
		 
		for (Class<? extends Object> file : classes) {
			
			Object instance = null;
			
			try {
			
				instance = file.newInstance();
			
			} catch (InstantiationException | IllegalAccessException exception) {
				
				continue;
				
			}
			
			if (instance instanceof Listener) {
				
				Bukkit.getPluginManager().registerEvents((Listener) instance, Main.uhc);
				
			}
			
			if (instance instanceof Option) {
				
				Option option = (Option) instance;
				
				OptionFeature.options.put(option.getId(), option);
				
				if (option.isEnabled()) option.onEnable();
				else option.onDisable();
				
			}
			 
		}
		
		for (Map.Entry<String, Boolean> id : GameUtils.getOptions().entrySet()) {
			
			Option option = OptionFeature.options.get(id.getKey());
			
			if (option.isEnabled() == id.getValue()) continue;
			
			option.toggle();
			
		}
		
	}
	
	public static Map<String, Option> getFeatures() {
		
		return options;
		
	}
	
	private static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
		
		List<Class<?>> classes = new ArrayList<Class<?>>();
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
		
		public String getName() {
			
			return this.name;
			
		}
		
		public ItemStack getIcon() {
			
			ItemStack item = this.icon.clone();
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
		
		public String getId() {
			
			return this.getName().replaceAll(" ", "_").toLowerCase();
			
		}
		
	}

}
