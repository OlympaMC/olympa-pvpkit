package fr.olympa.pvpkit.spawning;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.spigot.command.OlympaCommand;
import fr.olympa.api.common.permission.OlympaSpigotPermission;
import fr.olympa.api.utils.Prefix;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;
import fr.olympa.pvpkit.OlympaPvPKit;
import fr.olympa.pvpkit.PvPKitListener;

public class SpawnCommand extends OlympaCommand {
	
	public SpawnCommand(Plugin plugin) {
		super(plugin, "spawn", "Téléporte le joueur au spawn.", (OlympaSpigotPermission) null);
		setAllowConsole(false);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = getPlayer();
		OlympaPvPKit.getInstance().teleportationManager.teleport(player, player.getWorld().getSpawnLocation(), Prefix.DEFAULT_GOOD.formatMessage("Bienvenue au spawn !"), () -> {
			OlympaPlayerPvPKit.get(player).setInPvPZone(null);
			PvPKitListener.giveMenuItem(player);
		});
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
	
}
