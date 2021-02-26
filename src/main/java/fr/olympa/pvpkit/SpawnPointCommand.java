package fr.olympa.pvpkit;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.command.OlympaCommand;

public class SpawnPointCommand extends OlympaCommand {
	
	public SpawnPointCommand(Plugin plugin) {
		super(plugin, "addspawnpoint", PvPKitPermissions.SPAWNPOINT_COMMAND_MANAGE);
		setAllowConsole(false);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			OlympaPvPKit.getInstance().spawnPoints.addSpawnPoint(getPlayer().getLocation());
			sendSuccess("Un spawnpoint a été ajouté à la liste (contient %d spawnpoints)", OlympaPvPKit.getInstance().spawnPoints.getLocations().size());
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
	
}
