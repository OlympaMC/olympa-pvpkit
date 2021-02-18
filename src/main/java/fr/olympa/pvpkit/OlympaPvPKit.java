package fr.olympa.pvpkit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.plugin.OlympaAPIPlugin;

public class OlympaPvPKit extends OlympaAPIPlugin {
	
	public Map<String, Kit> kits = new HashMap<>();
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		ConfigurationSection kitsSection = getConfig().getConfigurationSection("kits");
		for (String name : kitsSection.getKeys(false)) {
			ConfigurationSection kitSection = kitsSection.getConfigurationSection(name);
			kits.put(name, new Kit(name, kitSection.getList("items").toArray(ItemStack[]::new)));
		}
	}
	
}
