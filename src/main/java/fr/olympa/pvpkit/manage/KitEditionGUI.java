package fr.olympa.pvpkit.manage;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.gui.OlympaGUI;
import fr.olympa.api.item.ItemUtils;
import fr.olympa.pvpkit.Kit;

public class KitEditionGUI extends OlympaGUI {
	
	private Kit kit;
	
	public KitEditionGUI(Kit kit) {
		super("Modifier le kit", 2);
		this.kit = kit;
		
		int i = 0;
		for (ItemStack item : kit.items) {
			inv.setItem(i++, item);
		}
		
		inv.setItem(16, ItemUtils.item(Material.BARRIER, "§cAnnuler"));
		inv.setItem(17, ItemUtils.item(Material.DIAMOND, "§aValider"));
	}
	
}
