package fr.olympa.pvpkit;

import java.lang.reflect.Field;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.spigotmc.SpigotConfig;

import fr.olympa.api.CombatManager;
import fr.olympa.api.command.essentials.KitCommand;
import fr.olympa.api.command.essentials.KitCommand.IKit;
import fr.olympa.api.economy.MoneyCommand;
import fr.olympa.api.lines.CyclingLine;
import fr.olympa.api.lines.DynamicLine;
import fr.olympa.api.lines.FixedLine;
import fr.olympa.api.permission.OlympaPermission;
import fr.olympa.api.plugin.OlympaAPIPlugin;
import fr.olympa.api.provider.AccountProvider;
import fr.olympa.api.region.Region;
import fr.olympa.api.region.tracking.ActionResult;
import fr.olympa.api.region.tracking.flags.DamageFlag;
import fr.olympa.api.region.tracking.flags.DropFlag;
import fr.olympa.api.region.tracking.flags.Flag;
import fr.olympa.api.region.tracking.flags.FrostWalkerFlag;
import fr.olympa.api.region.tracking.flags.GameModeFlag;
import fr.olympa.api.region.tracking.flags.ItemDurabilityFlag;
import fr.olympa.api.region.tracking.flags.PhysicsFlag;
import fr.olympa.api.region.tracking.flags.PlayerBlockInteractFlag;
import fr.olympa.api.region.tracking.flags.PlayerBlocksFlag;
import fr.olympa.api.scoreboard.sign.Scoreboard;
import fr.olympa.api.scoreboard.sign.ScoreboardManager;
import fr.olympa.api.utils.spigot.TeleportationManager;
import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.pvpkit.kits.KitManageCommand;
import fr.olympa.pvpkit.kits.KitsManager;
import fr.olympa.pvpkit.kits.gui.KitListGUI;
import fr.olympa.pvpkit.ranking.BestKillStreakRank;
import fr.olympa.pvpkit.ranking.TotalKillRank;
import fr.olympa.pvpkit.spawning.SpawnCommand;
import fr.olympa.pvpkit.spawning.SpawnPointsManager;
import fr.olympa.pvpkit.xp.LevelCommand;
import fr.olympa.pvpkit.xp.XPManagement;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PlayerList;

public class OlympaPvPKit extends OlympaAPIPlugin {
	
	private static OlympaPvPKit instance;
	
	public static OlympaPvPKit getInstance() {
		return (OlympaPvPKit) instance;
	}
	
	public KitsManager kits;
	private CombatManager combat;
	public SpawnPointsManager spawnPoints;
	public TeleportationManager teleportationManager;
	
	public ScoreboardManager<OlympaPlayerPvPKit> scoreboards;
	public DynamicLine<Scoreboard<OlympaPlayerPvPKit>> lineMoney = new DynamicLine<>(x -> "§7Monnaie: §6" + x.getOlympaPlayer().getGameMoney().getFormatted());
	public DynamicLine<Scoreboard<OlympaPlayerPvPKit>> lineKillStreak = new DynamicLine<>(x -> "§7Killstreak: §6" + x.getOlympaPlayer().getKillStreak().get());
	public DynamicLine<Scoreboard<OlympaPlayerPvPKit>> lineKills = new DynamicLine<>(x -> "§7Kills: §6" + x.getOlympaPlayer().getKills().get());
	public DynamicLine<Scoreboard<OlympaPlayerPvPKit>> lineLevel = new DynamicLine<>(x -> "§7Niveau: §6" + x.getOlympaPlayer().getLevel() + " §e(" + XPManagement.formatExperience(x.getOlympaPlayer().getXP()) + "/" + XPManagement.formatExperience(XPManagement.getXPToLevelUp(x.getOlympaPlayer().getLevel())) + ")");
	public DynamicLine<Scoreboard<OlympaPlayerPvPKit>> lineKit = new DynamicLine<>(x -> "§7Kit: " + (x.getOlympaPlayer().isInPvPZone() ? x.getOlympaPlayer().getUsedKit().getName() : "§8§oaucun"));
	
	public TotalKillRank totalKillRank;
	public BestKillStreakRank bestKSRank;
	
	public Location pvpLocation;
	public Region safeZone;
	
	@SuppressWarnings ("deprecation")
	@Override
	public void onEnable() {
		instance = this;
		super.onEnable();
		
		OlympaPermission.registerPermissions(PvPKitPermissions.class);
		
		AccountProvider.setPlayerProvider(OlympaPlayerPvPKit.class, OlympaPlayerPvPKit::new, "pvpkit", OlympaPlayerPvPKit.COLUMNS);
		
		OlympaCore.getInstance().getRegionManager().awaitWorldTracking("world", e -> e.getRegion().registerFlags(
				new ItemDurabilityFlag(true),
				new PhysicsFlag(true),
				new PlayerBlocksFlag(true),
				new GameModeFlag(GameMode.ADVENTURE),
				new DropFlag(true),
				new FrostWalkerFlag(false),
				new PlayerBlockInteractFlag(false, true, true)));
		
		try {
			kits = new KitsManager();
			new KitManageCommand(this).register();
			new KitCommand<OlympaPlayerPvPKit>(this, () -> kits.getKits().stream().map(IKit.class::cast)) {
				@Override
				protected void noArgument() {
					new KitListGUI(getOlympaPlayer()).create(getPlayer());
				}
			}.register();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		scoreboards = new ScoreboardManager<OlympaPlayerPvPKit>(this, "§6Olympa §e§lPvP-Kits").addLines(
				FixedLine.EMPTY_LINE,
				lineMoney,
				FixedLine.EMPTY_LINE,
				lineKillStreak,
				lineKills,
				FixedLine.EMPTY_LINE,
				lineLevel,
				FixedLine.EMPTY_LINE,
				lineKit)
				.addFooters(
				FixedLine.EMPTY_LINE,
				CyclingLine.olympaAnimation());
		
		Bukkit.getPluginManager().registerEvents(new PvPKitListener(), this);
		Bukkit.getPluginManager().registerEvents(combat = new CombatManager(this, 15), this);
		combat.setSendMessages(false);
		
		pvpLocation = getConfig().getLocation("pvpLocation");
		safeZone = getConfig().getSerializable("safeZone", Region.class);
		OlympaCore.getInstance().getRegionManager().registerRegion(safeZone, "safeZone", EventPriority.HIGH, new DamageFlag(false));
		OlympaCore.getInstance().getRegionManager().registerRegion(getConfig().getSerializable("killbox", Region.class), "killbox", EventPriority.HIGH, new Flag() {
			public fr.olympa.api.region.tracking.ActionResult enters(org.bukkit.entity.Player p, java.util.Set<fr.olympa.api.region.tracking.TrackedRegion> to) {
				getTask().runTask(() -> p.damage(100000));
				return ActionResult.ALLOW;
			};
		});
		
		try {
			spawnPoints = new SpawnPointsManager();
			//new SpawnPointCommand(this).register();
		}catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		new SuicideCommand(this).register();
		
		try {
			totalKillRank = new TotalKillRank(getConfig().getLocation("rankingHolograms.totalKills"));
			bestKSRank = new BestKillStreakRank(getConfig().getLocation("rankingHolograms.bestKS"));
		}catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		teleportationManager = new TeleportationManager(this, PvPKitPermissions.TP_TIME_BYPASS);
		
		new MoneyCommand<OlympaPlayerPvPKit>(this, "money", "Gérer son porte-monnaie.", PvPKitPermissions.MONEY_COMMAND, PvPKitPermissions.MONEY_COMMAND_OTHER, PvPKitPermissions.MONEY_COMMAND_MANAGE, "monnaie").register();
		new LevelCommand(this).register();
		new SpawnCommand(this).register();
		
		OlympaCore.getInstance().getNameTagApi().addNametagHandler(EventPriority.LOWEST, (nametag, player, to) -> nametag.appendPrefix(XPManagement.getLevelPrefix(((OlympaPlayerPvPKit) player).getLevel())));
		
		MinecraftServer server = MinecraftServer.getServer();
		try {
			CustomWorldNBTStorage nbtStorage = new CustomWorldNBTStorage(server.convertable, server.getDataFixer());
			Field field = MinecraftServer.class.getDeclaredField("worldNBTStorage");
			field.setAccessible(true);
			field.set(server, nbtStorage);
			field = PlayerList.class.getDeclaredField("playerFileData");
			field.setAccessible(true);
			field.set(server.getPlayerList(), nbtStorage);
		}catch (ReflectiveOperationException ex) {
			ex.printStackTrace();
		}
		if (server.worldNBTStorage instanceof CustomWorldNBTStorage && server.getPlayerList().playerFileData instanceof CustomWorldNBTStorage) {
			sendMessage("§aLa gestion custom des données joueurs vanilla est implantée.");
		}else {
			sendMessage("§cUn problème est survenu lors de la gestion custom des données joueurs vanilla.");
			SpigotConfig.disablePlayerDataSaving = true;
		}
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		combat.unload();
	}
	
}
