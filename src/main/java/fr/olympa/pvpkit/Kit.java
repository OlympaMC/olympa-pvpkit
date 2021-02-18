package fr.olympa.pvpkit;

import org.bukkit.inventory.ItemStack;

public class Kit {
	
	public final String name;
	public ItemStack[] items;
	
	public Kit(String name, ItemStack[] items) {
		this.name = name;
		this.items = items;
	}
	
}
