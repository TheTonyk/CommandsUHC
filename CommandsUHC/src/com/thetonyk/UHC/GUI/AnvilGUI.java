package com.thetonyk.UHC.GUI;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Packets.PacketHandler;
import com.thetonyk.UHC.Utils.ItemsUtils;

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.ContainerAnvil;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutWindowData;

public class AnvilGUI {
	
	private EntityPlayer entityPlayer;
	private AnvilContainer container;
	private Listener listener;
	private PacketHandler packetsListener;
	private Inventory inventory;

	public AnvilGUI (Player player, String explain, AnvilCallback<String> callback) {
	
		this.entityPlayer = ((CraftPlayer) player).getHandle();
		this.container = new AnvilContainer(this.entityPlayer);
		this.inventory = this.container.getBukkitView().getTopInventory();
		
		this.inventory.setItem(0, ItemsUtils.getSkull(explain, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="));
		
		int c = this.entityPlayer.nextContainerCounter();
		
		this.entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Anvil"), 0));
		this.entityPlayer.playerConnection.sendPacket(new PacketPlayOutWindowData(c, 0, 3));
		
		this.entityPlayer.activeContainer = container;
		this.entityPlayer.activeContainer.windowId = c;
		this.entityPlayer.activeContainer.addSlotListener(this.entityPlayer);
		
		this.listener = new Listener() {
			
			@EventHandler
			public void onClick(InventoryClickEvent event) {
				
				if (!(event.getWhoClicked() instanceof Player)) return;
				
				Player player = (Player) event.getWhoClicked();
				Inventory inv = event.getInventory();
				ItemStack item = event.getCurrentItem();
				
				if (!inv.equals(inventory)) return;
				
				event.setCancelled(true);
				
				if (event.getRawSlot() != 2) return;
				
				player.closeInventory();
				delete();
				
				if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
				
				callback.onConfirm(item.getItemMeta().getDisplayName());
			
			}
			
			@EventHandler
			public void onClose(InventoryCloseEvent event) {
				
				Inventory inv = event.getInventory();
				
				if (!inv.equals(inventory)) return;
				
				inv.clear();
				delete();
				callback.onClose();
				
			}
			
			@EventHandler
			public void onQuit(PlayerQuitEvent event) {
				
				Player player = event.getPlayer();
				
				if (!player.getUniqueId().equals(entityPlayer.getUniqueID())) return;
				
				delete();
				callback.onClose();
				
			}
			
			
		};
		
		Bukkit.getPluginManager().registerEvents(this.listener, Main.uhc);
		
		this.packetsListener = new PacketHandler(Main.uhc) {
			
			@Override
			public Object onPacketInAsync(Player player, Channel channel, Object packet) {
				
				if (!(packet instanceof PacketPlayInCustomPayload)) return super.onPacketInAsync(player, channel, packet);
				
				if (((CraftPlayer) player).getHandle().activeContainer != container) return super.onPacketInAsync(player, channel, packet);
				
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, 0, 0));
				
				return super.onPacketInAsync(player, channel, packet);
				
			}
			
		};
		
	}
		
	private void delete() {
		
		HandlerList.unregisterAll(this.listener);
		this.packetsListener.close();
		
	}
	
	private static class AnvilContainer extends ContainerAnvil {
		
        public AnvilContainer (EntityHuman entity){
        	
            super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
            
        }
 
        @Override
        public boolean a (EntityHuman entityhuman){
        	
            return true;
            
        }
        
    }
	
	public static interface AnvilCallback<T> {
		
		void onConfirm(String text);
		void onClose();
		
	}
	
}
