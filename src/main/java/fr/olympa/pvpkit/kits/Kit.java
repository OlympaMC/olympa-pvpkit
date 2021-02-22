package fr.olympa.pvpkit.kits;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.command.essentials.KitCommand.IKit;
import fr.olympa.api.item.ItemUtils;
import fr.olympa.api.utils.Prefix;
import fr.olympa.api.utils.spigot.SpigotUtils;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;
import fr.olympa.pvpkit.OlympaPvPKit;

public class Kit implements IKit<OlympaPlayerPvPKit> {
	
	private String id, name;
	private ItemStack[] items;
	private ItemStack icon, iconGUI;
	private int minLevel;
	
	protected Kit(String id, String name, ItemStack[] items, ItemStack icon, int minLevel) {
		this.id = id;
		this.name = name;
		this.items = items;
		this.icon = icon;
		this.minLevel = minLevel;
		refreshIconGUI();
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
		OlympaPvPKit.getInstance().kits.columnId.updateAsync(this, id, null, null);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		OlympaPvPKit.getInstance().kits.columnName.updateAsync(this, name, null, null);
		refreshIconGUI();
	}
	
	public ItemStack[] getItems() {
		return items;
	}
	
	public void setItems(ItemStack[] items) {
		this.items = items;
		try {
			OlympaPvPKit.getInstance().kits.columnItems.updateAsync(this, ItemUtils.serializeItemsArray(items), null, null);
		}catch (IOException e) {
			e.printStackTrace();
		}
		refreshIconGUI();
	}
	
	public ItemStack getIconGUI() {
		return iconGUI;
	}
	
	private void refreshIconGUI() {
		int i = 0;
		String[] lore = new String[items.length + 4];
		lore[i++] = "";
		for (ItemStack item : items) lore[i++] = "§8● " + ItemUtils.getName(item);
		lore[i++] = "";
		lore[i++] = "§8§lClic droit> §7voir le contenu";
		lore[i++] = "§8§lClic gauche> §7§lsélectionner le kit";
		this.iconGUI = ItemUtils.name(ItemUtils.lore(icon.clone(), lore), name);
	}
	
	public ItemStack getIcon() {
		return icon;
	}
	
	public void setIcon(ItemStack icon) {
		this.icon = icon;
		try {
			OlympaPvPKit.getInstance().kits.columnIcon.updateAsync(this, SpigotUtils.serialize(icon), null, null);
		}catch (IOException e) {
			e.printStackTrace();
		}
		refreshIconGUI();
	}
	
	public int getMinLevel() {
		return minLevel;
	}
	
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
		OlympaPvPKit.getInstance().kits.columnLevel.updateAsync(this, minLevel, null, null);
	}
	
	@Override
	public void give(OlympaPlayerPvPKit olympaPlayer, Player p) {
		p.getInventory().clear();
		SpigotUtils.giveItems(p, items);
		olympaPlayer.setInPvPZone(this);
		p.closeInventory();
		p.teleport(OlympaPvPKit.getInstance().pvpLocation);
		Prefix.DEFAULT_GOOD.sendMessage(p, "Tu as reçu le kit %s ! Bon combat !", id);
	}
	
	@Override
	public boolean canTake(OlympaPlayerPvPKit player) {
		return !player.isInPvPZone() && player.getLevel().get() >= minLevel;
	}
	
	@Override
	public void sendImpossibleToTake(OlympaPlayerPvPKit player) {
		Prefix.DEFAULT_BAD.sendMessage(player.getPlayer(), "Tu ne peux pas prendre de kit si tu es déjà en combat !");
	}
	
	@Override
	public long getTimeBetween() {
		return 0;
	}
	
	@Override
	public long getLastTake(OlympaPlayerPvPKit player) {
		return 0;
	}
	
	@Override
	public void setLastTake(OlympaPlayerPvPKit player, long time) {}
	
}
