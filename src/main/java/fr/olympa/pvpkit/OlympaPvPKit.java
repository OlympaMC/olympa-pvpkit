package fr.olympa.pvpkit;

import java.sql.SQLException;

import fr.olympa.api.command.essentials.KitCommand;
import fr.olympa.api.command.essentials.KitCommand.IKit;
import fr.olympa.api.plugin.OlympaAPIPlugin;
import fr.olympa.pvpkit.manage.KitManageCommand;

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
		
		try {
			kits = new KitsManager();
			new KitManageCommand(this).register();
			new KitCommand<OlympaPlayerPvPKit>(this, () -> kits.getKits().stream().map(x -> (IKit<OlympaPlayerPvPKit>) x)).register();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
