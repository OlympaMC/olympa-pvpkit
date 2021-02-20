package fr.olympa.pvpkit;

import java.sql.SQLException;

import org.bukkit.Bukkit;

import fr.olympa.api.command.essentials.KitCommand;
import fr.olympa.api.command.essentials.KitCommand.IKit;
import fr.olympa.api.economy.MoneyCommand;
import fr.olympa.api.permission.OlympaPermission;
import fr.olympa.api.plugin.OlympaAPIPlugin;
import fr.olympa.api.provider.AccountProvider;
import fr.olympa.pvpkit.kits.KitManageCommand;
import fr.olympa.pvpkit.kits.KitsManager;
import fr.olympa.pvpkit.xp.XPListener;

public class OlympaPvPKit extends OlympaAPIPlugin {
	
	private static OlympaPvPKit instance;
	
	public static OlympaPvPKit getInstance() {
		return (OlympaPvPKit) instance;
	}
	
	public KitsManager kits;
	
	@Override
	public void onEnable() {
		instance = this;
		super.onEnable();
		
		OlympaPermission.registerPermissions(PvPKitPermissions.class);
		
		AccountProvider.setPlayerProvider(OlympaPlayerPvPKit.class, OlympaPlayerPvPKit::new, "pvpkit", OlympaPlayerPvPKit.COLUMNS);
		
		try {
			kits = new KitsManager();
			new KitManageCommand(this).register();
			new KitCommand<OlympaPlayerPvPKit>(this, () -> kits.getKits().stream().map(x -> (IKit<OlympaPlayerPvPKit>) x)).register();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		new MoneyCommand<OlympaPlayerPvPKit>(this, "money", "Gérer son porte-monnaie.", PvPKitPermissions.MONEY_COMMAND, PvPKitPermissions.MONEY_COMMAND_OTHER, PvPKitPermissions.MONEY_COMMAND_MANAGE, "monnaie").register();
		
		Bukkit.getPluginManager().registerEvents(new XPListener(), this);
		
	}
	
}
