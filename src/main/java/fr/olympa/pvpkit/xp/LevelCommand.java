package fr.olympa.pvpkit.xp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.command.complex.Cmd;
import fr.olympa.api.command.complex.CommandContext;
import fr.olympa.api.command.complex.ComplexCommand;
import fr.olympa.api.utils.observable.ObservableInt;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;
import fr.olympa.pvpkit.PvPKitPermissions;

public class LevelCommand extends ComplexCommand {
	
	public LevelCommand(Plugin plugin) {
		super(plugin, "level", "Permet de gérer les niveaux d'expérience.", PvPKitPermissions.LEVEL_COMMAND, "xp", "lvl");
	}
	
	@Override
	public boolean noArguments(CommandSender sender) {
		if (!(sender instanceof Player)) return false;
		sendXP(getOlympaPlayer());
		return true;
	}
	
	@Cmd (permissionName = "LEVEL_COMMAND_OTHER", min = 1, args = "PLAYERS", syntax = "<joueur>", description = "Permet de voir l'expérience d'un autre joueur")
	public void get(CommandContext cmd) {
		sendXP(OlympaPlayerPvPKit.get(cmd.getArgument(0)));
	}
	
	@Cmd (permissionName = "LEVEL_COMMAND_MANAGE", min = 3, args = { "PLAYERS", "INTEGER", "xp|level" }, syntax = "<joueur> <quantité> <xp/level>")
	public void give(CommandContext cmd) {
		OlympaPlayerPvPKit player = OlympaPlayerPvPKit.get(cmd.getArgument(0));
		int amount = cmd.getArgument(1);
		boolean xp;
		String type = cmd.getArgument(2);
		if (type.equals("xp")) {
			xp = true;
		}else if (type.equals("level")) {
			xp = false;
		}else {
			sendError("Donnée invalide: %s. Accepté: xp/level", type);
			return;
		}
		ObservableInt data = xp ? player.getXP() : player.getLevel();
		data.add(amount);
		sendSuccess("%s a reçu %d %s.", player.getName(), amount, xp ? "xp" : "niveaux");
	}
	
	@Cmd (permissionName = "LEVEL_COMMAND_MANAGE", min = 3, args = { "PLAYERS", "INTEGER", "xp|level" }, syntax = "<joueur> <quantité> <xp/level>")
	public void set(CommandContext cmd) {
		OlympaPlayerPvPKit player = OlympaPlayerPvPKit.get(cmd.getArgument(0));
		int amount = cmd.getArgument(1);
		boolean xp;
		String type = cmd.getArgument(2);
		if (type.equals("xp")) {
			xp = true;
		}else if (type.equals("level")) {
			xp = false;
		}else {
			sendError("Donnée invalide: %s. Accepté: xp/level", type);
			return;
		}
		ObservableInt data = xp ? player.getXP() : player.getLevel();
		data.set(amount);
		sendSuccess("%s a maintenant %d %s.", player.getName(), amount, xp ? "xp" : "niveaux");
	}
	
	public void sendXP(OlympaPlayerPvPKit player) {
		sendSuccess("Expérience de %s:"
				+ "\n§e➤ Niveau: %d"
				+ "\n§e➤ Expérience: %d/%d ", player.getName(), player.getLevel().get(), player.getXP().get(), XPManagement.getXPToLevelUp(player.getLevel().get()));
	}
	
}
