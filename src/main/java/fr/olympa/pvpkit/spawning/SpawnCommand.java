package fr.olympa.pvpkit.spawning;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.command.OlympaCommand;
import fr.olympa.api.permission.OlympaSpigotPermission;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;
import fr.olympa.pvpkit.PvPKitListener;

public class SpawnCommand extends OlympaCommand {
	
	public SpawnCommand(Plugin plugin) {
		super(plugin, "spawn", "Téléporte le joueur au spawn.", (OlympaSpigotPermission) null);
		setAllowConsole(false);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = getPlayer();
		OlympaPlayerPvPKit oplayer = getOlympaPlayer();
		oplayer.setInPvPZone(null);
		PvPKitListener.giveMenuItem(player);
		player.teleport(player.getWorld().getSpawnLocation());
		sendSuccess("Bienvenue au spawn !");
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
	
}
