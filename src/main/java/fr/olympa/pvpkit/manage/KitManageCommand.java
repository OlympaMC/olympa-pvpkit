package fr.olympa.pvpkit.manage;

import org.bukkit.plugin.Plugin;

import fr.olympa.api.command.complex.Cmd;
import fr.olympa.api.command.complex.CommandContext;
import fr.olympa.api.command.complex.ComplexCommand;
import fr.olympa.pvpkit.PvpKitPermissions;

public class KitManageCommand extends ComplexCommand {
	
	public KitManageCommand(Plugin plugin) {
		super(plugin, "kitmanage", "Permet de modifier les kits", PvpKitPermissions.KIT_MANAGE_COMMAND);
		setAllowConsole(false);
	}
	
	@Cmd
	public void add(CommandContext cmd) {
		
	}
	
}
