package fr.olympa.pvpkit;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.spigot.command.OlympaCommand;
import fr.olympa.api.common.permission.OlympaSpigotPermission;

public class SuicideCommand extends OlympaCommand {
	
	public SuicideCommand(Plugin plugin) {
		super(plugin, "suicide", "Permet de se suicider et revenir au spawn.", (OlympaSpigotPermission) null);
		setAllowConsole(false);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		player.damage(1000000);
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
	
}
