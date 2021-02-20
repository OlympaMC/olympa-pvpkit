package fr.olympa.pvpkit.kits;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.command.complex.ArgumentParser;
import fr.olympa.api.command.complex.Cmd;
import fr.olympa.api.command.complex.CommandContext;
import fr.olympa.api.command.complex.ComplexCommand;
import fr.olympa.api.utils.Prefix;
import fr.olympa.pvpkit.OlympaPvPKit;
import fr.olympa.pvpkit.PvPKitPermissions;

public class KitManageCommand extends ComplexCommand {
	
	public KitManageCommand(Plugin plugin) {
		super(plugin, "kitmanage", "Permet de modifier les kits", PvPKitPermissions.KIT_MANAGE_COMMAND);
		setAllowConsole(false);
		addArgumentParser("KIT", new ArgumentParser<>((sender, arg) -> OlympaPvPKit.getInstance().kits.getKits().stream().map(Kit::getId).collect(Collectors.toList()), x -> OlympaPvPKit.getInstance().kits.getKit(x), x -> "Le kit %s est introuvable", false));
	}
	
	@Cmd (min = 1, syntax = "<id> [nom]", description = "Créer un kit")
	public void add(CommandContext cmd) {
		String id = cmd.getArgument(0);
		if (OlympaPvPKit.getInstance().kits.getKit(id) == null) {
			sendError("Le kit %s existe déjà.", id);
			return;
		}
		Player player = getPlayer();
		new KitEditionGUI(new ItemStack[0], items -> {
			try {
				Kit kit = OlympaPvPKit.getInstance().kits.addKit(id, cmd.getArgumentsLength() == 1 ? id : cmd.getFrom(1), items);
				Prefix.DEFAULT_GOOD.sendMessage(player, "Le kit %s a été créé ! (%d items)", kit.getId(), kit.getItems().length);
			}catch (SQLException | IOException e) {
				e.printStackTrace();
				Prefix.ERROR.sendMessage(player, "Une erreur est survenue lors de la création du kit.");
			}
		}).create(player);
	}
	
	@Cmd (min = 2, args = "KIT", syntax = "<id> <nouveau nom>", description = "Modifier le nom du kit")
	public void changeName(CommandContext cmd) {
		Kit kit = cmd.getArgument(0);
		kit.setName(cmd.getFrom(1));
		sendSuccess("Le kit %s a été renommé.", kit.getId());
	}
	
	@Cmd (min = 1, args = "KIT", syntax = "<id>", description = "Modifier le contenu du kit")
	public void changeItems(CommandContext cmd) {
		Player player = getPlayer();
		Kit kit = cmd.getArgument(0);
		new KitEditionGUI(kit.getItems(), items -> {
			kit.setItems(items);
			Prefix.DEFAULT_GOOD.sendMessage(player, "Le kit %s a été modifié. (%d items)", kit.getId(), kit.getItems().length);
		}).create(player);
	}
	
	@Cmd (min = 1, args = "KIT", syntax = "<id>", description = "Supprimer le kit")
	public void delete(CommandContext cmd) {
		try {
			Kit kit = cmd.getArgument(0);
			OlympaPvPKit.getInstance().kits.removeKit(kit);
			sendSuccess("Le kit %s a été supprimé.", kit.getId());
		}catch (SQLException e) {
			e.printStackTrace();
			sendError(e);
		}
	}
	
}
