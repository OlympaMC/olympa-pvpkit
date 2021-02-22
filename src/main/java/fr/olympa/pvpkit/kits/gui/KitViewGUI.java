package fr.olympa.pvpkit.kits.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.gui.OlympaGUI;
import fr.olympa.api.item.ItemUtils;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;
import fr.olympa.pvpkit.kits.Kit;

public class KitViewGUI extends OlympaGUI {
	
	private Kit kit;
	
	public KitViewGUI(Kit kit) {
		super("Kit " + kit.getId(), 2);
		this.kit = kit;
		for (int i = 0; i < kit.getItems().length; i++) {
			inv.setItem(i, kit.getItems()[i]);
		}
		inv.setItem(13, ItemUtils.item(Material.OAK_DOOR, "§a← Revenir à la liste"));
		inv.setItem(15, ItemUtils.item(Material.DIAMOND, "§b✦ Prendre ce kit"));
	}
	
	@Override
	public boolean onClick(Player p, ItemStack current, int slot, ClickType click) {
		if (slot == 13) {
			new KitListGUI().create(p);
		}else if (slot == 15) {
			OlympaPlayerPvPKit olympaPlayer = OlympaPlayerPvPKit.get(p);
			if (kit.canTake(olympaPlayer)) {
				kit.give(olympaPlayer, p);
			}else {
				kit.sendImpossibleToTake(olympaPlayer);
			}
		}
		return true;
	}
	
}
