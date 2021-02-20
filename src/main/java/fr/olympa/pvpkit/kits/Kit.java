package fr.olympa.pvpkit.kits;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.command.essentials.KitCommand.IKit;
import fr.olympa.api.item.ItemUtils;
import fr.olympa.api.player.OlympaPlayer;
import fr.olympa.api.utils.Prefix;
import fr.olympa.api.utils.spigot.SpigotUtils;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;
import fr.olympa.pvpkit.OlympaPvPKit;

public class Kit implements IKit<OlympaPlayerPvPKit> {
	
	private String id, name;
	private ItemStack[] items;
	
	protected Kit(String id, String name, ItemStack[] items) {
		this.id = id;
		this.name = name;
		this.items = items;
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
	}
	
	@Override
	public boolean canTake(OlympaPlayer player) {
		return true;
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
	
	@Override
	public void give(OlympaPlayerPvPKit olympaPlayer, Player p) {
		SpigotUtils.giveItems(p, items);
		Prefix.DEFAULT_GOOD.sendMessage(p, "Tu as reçu le kit %s !", id);
	}
	
}
