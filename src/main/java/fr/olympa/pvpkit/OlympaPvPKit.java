package fr.olympa.pvpkit;

import java.sql.SQLException;

import org.bukkit.Bukkit;

import fr.olympa.api.command.essentials.KitCommand;
import fr.olympa.api.command.essentials.KitCommand.IKit;
import fr.olympa.api.economy.MoneyCommand;
import fr.olympa.api.lines.CyclingLine;
import fr.olympa.api.lines.DynamicLine;
import fr.olympa.api.lines.FixedLine;
import fr.olympa.api.permission.OlympaPermission;
import fr.olympa.api.plugin.OlympaAPIPlugin;
import fr.olympa.api.provider.AccountProvider;
import fr.olympa.api.scoreboard.sign.Scoreboard;
import fr.olympa.api.scoreboard.sign.ScoreboardManager;
import fr.olympa.pvpkit.kits.KitManageCommand;
import fr.olympa.pvpkit.kits.KitsManager;
import fr.olympa.pvpkit.xp.LevelCommand;
import fr.olympa.pvpkit.xp.XPListener;
import fr.olympa.pvpkit.xp.XPManagement;

public class OlympaPvPKit extends OlympaAPIPlugin {
	
	private static OlympaPvPKit instance;
	
	public static OlympaPvPKit getInstance() {
		return (OlympaPvPKit) instance;
	}
	
	public KitsManager kits;
	
	public ScoreboardManager<OlympaPlayerPvPKit> scoreboards;
	public DynamicLine<Scoreboard<OlympaPlayerPvPKit>> lineMoney = new DynamicLine<>(x -> "§7Monnaie: §6" + x.getOlympaPlayer().getGameMoney().getFormatted());
	public DynamicLine<Scoreboard<OlympaPlayerPvPKit>> lineKillStreak = new DynamicLine<>(x -> "§7Killstreak: §6" + x.getOlympaPlayer().getKillStreak().get());
	public DynamicLine<Scoreboard<OlympaPlayerPvPKit>> lineLevel = new DynamicLine<>(x -> "§7Niveau: §6" + x.getOlympaPlayer().getLevel().get() + " §e(" + x.getOlympaPlayer().getXP().get() + "/" + XPManagement.getXPToLevelUp(x.getOlympaPlayer().getLevel().get()) + ")");
	
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
		new LevelCommand(this).register();
		
		scoreboards = new ScoreboardManager<OlympaPlayerPvPKit>(this, "§6Olympa §e§lPvP-Kits").addLines(
				FixedLine.EMPTY_LINE,
				lineMoney,
				FixedLine.EMPTY_LINE,
				lineKillStreak,
				lineLevel)
				.addFooters(
				FixedLine.EMPTY_LINE,
				CyclingLine.olympaAnimation());
		
		Bukkit.getPluginManager().registerEvents(new XPListener(), this);
		
	}
	
}
