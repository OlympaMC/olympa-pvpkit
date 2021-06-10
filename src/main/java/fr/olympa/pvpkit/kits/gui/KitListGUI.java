package fr.olympa.pvpkit.kits.gui;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.spigot.gui.templates.PagedGUI;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;
import fr.olympa.pvpkit.OlympaPvPKit;
import fr.olympa.pvpkit.kits.Kit;

public class KitListGUI extends PagedGUI<Kit> {
	
	private OlympaPlayerPvPKit player;
	
	public KitListGUI(OlympaPlayerPvPKit player) {
		super("Kits", DyeColor.RED, new ArrayList<>(OlympaPvPKit.getInstance().kits.getKits()), 3);
		this.player = player;
		OlympaPvPKit.getInstance().kits.getKits().stream().filter(kit -> kit.getMinLevel() > player.getLevel()).forEach(kit -> updateObjectItem(kit, kit.getIconGUI(false)));
	}
	
	@Override
	public ItemStack getItemStack(Kit kit) {
		return kit.getIconGUI(player == null || (kit.getMinLevel() <= player.getLevel()));
	}

	@Override
	public void click(Kit kit, Player p, ClickType click) {
		if (click.isLeftClick()) {
			if (kit.canTake(player)) {
				kit.give(player, p);
			}else {
				kit.sendImpossibleToTake(player);
			}
		}else if (click.isRightClick()) {
			new KitViewGUI(kit).create(p);
		}
	}
	
}
