package fr.olympa.pvpkit.kits.gui;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.gui.templates.PagedGUI;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;
import fr.olympa.pvpkit.OlympaPvPKit;
import fr.olympa.pvpkit.kits.Kit;

public class KitListGUI extends PagedGUI<Kit> {
	
	public KitListGUI() {
		super("Kits", DyeColor.RED, OlympaPvPKit.getInstance().kits.getKits(), 3);
	}
	
	@Override
	public ItemStack getItemStack(Kit kit) {
		return kit.getIconGUI();
	}

	@Override
	public void click(Kit kit, Player p, ClickType click) {
		if (click.isLeftClick()) {
			OlympaPlayerPvPKit olympaPlayer = OlympaPlayerPvPKit.get(p);
			if (kit.canTake(olympaPlayer)) {
				kit.give(olympaPlayer, p);
			}else {
				kit.sendImpossibleToTake(olympaPlayer);
			}
		}else if (click.isRightClick()) {
			new KitViewGUI(kit).create(p);
		}
	}
	
}
