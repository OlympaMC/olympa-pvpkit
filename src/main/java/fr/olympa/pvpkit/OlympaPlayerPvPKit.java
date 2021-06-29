package fr.olympa.pvpkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.olympa.api.common.observable.ObservableInt;
import fr.olympa.api.common.provider.AccountProviderAPI;
import fr.olympa.api.common.provider.OlympaPlayerObject;
import fr.olympa.api.common.sql.SQLColumn;
import fr.olympa.api.spigot.economy.MoneyPlayerInterface;
import fr.olympa.api.spigot.economy.OlympaMoney;
import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.pvpkit.kits.Kit;
import fr.olympa.pvpkit.xp.LevelManagement;
import fr.olympa.pvpkit.xp.XPManagement;

public class OlympaPlayerPvPKit extends OlympaPlayerObject implements MoneyPlayerInterface {

	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_MONEY = new SQLColumn<OlympaPlayerPvPKit>("money", "DOUBLE NULL DEFAULT 0", Types.DOUBLE).setUpdatable();
	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_KILL_STREAK = new SQLColumn<OlympaPlayerPvPKit>("kill_streak", "SMALLINT UNSIGNED NULL DEFAULT 0", Types.SMALLINT).setUpdatable();
	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_KILL_STREAK_MAX = new SQLColumn<OlympaPlayerPvPKit>("kill_streak_max", "SMALLINT UNSIGNED NULL DEFAULT 0", Types.SMALLINT).setUpdatable();
	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_LEVEL = new SQLColumn<OlympaPlayerPvPKit>("level", "SMALLINT UNSIGNED NULL DEFAULT 1", Types.SMALLINT).setUpdatable();
	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_XP = new SQLColumn<OlympaPlayerPvPKit>("xp", "INTEGER UNSIGNED NULL DEFAULT 0", Types.INTEGER).setUpdatable();
	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_KILLS = new SQLColumn<OlympaPlayerPvPKit>("kills", "INTEGER UNSIGNED NULL DEFAULT 0", Types.INTEGER).setUpdatable();

	public static final List<SQLColumn<OlympaPlayerPvPKit>> COLUMNS = Arrays.asList(COLUMN_MONEY, COLUMN_KILL_STREAK, COLUMN_KILL_STREAK_MAX, COLUMN_LEVEL, COLUMN_XP, COLUMN_KILLS);

	private OlympaMoney money = new OlympaMoney(0);
	private ObservableInt killStreak = new ObservableInt(0);
	private ObservableInt killStreakMax = new ObservableInt(0);
	private ObservableInt level = new ObservableInt(1);
	private ObservableInt xp = new ObservableInt(0);
	private ObservableInt kills = new ObservableInt(0);

	private Kit usedKit = null;

	public OlympaPlayerPvPKit(UUID uuid, String name, String ip) {
		super(uuid, name, ip);
	}

	@Override
	public void loaded() {
		money.observe("datas", () -> COLUMN_MONEY.updateAsync(this, money.get(), null, null));
		money.observe("scoreboard_update", () -> OlympaPvPKit.getInstance().lineMoney.updateHolder(OlympaPvPKit.getInstance().scoreboards.getPlayerScoreboard(this)));
		killStreak.observe("datas", () -> COLUMN_KILL_STREAK.updateAsync(this, killStreak.get(), null, null));
		killStreak.observe("scoreboard_update", () -> OlympaPvPKit.getInstance().lineKillStreak.updateHolder(OlympaPvPKit.getInstance().scoreboards.getPlayerScoreboard(this)));
		killStreak.observe("updateMax", () -> {
			if (killStreak.get() > killStreakMax.get())
				killStreakMax.set(killStreak.get());
		});
		killStreakMax.observe("datas", () -> COLUMN_KILL_STREAK_MAX.updateAsync(this, killStreakMax.get(), null, null));
		killStreakMax.observe("ranking", () -> OlympaPvPKit.getInstance().bestKSRank.handleNewScore(getName(), (Player) getPlayer(), killStreakMax.get()));
		level.observe("datas", () -> COLUMN_LEVEL.updateAsync(this, level.get(), null, null));
		level.observe("levelManagement", new LevelManagement(this));
		level.observe("scoreboard_update", () -> OlympaPvPKit.getInstance().lineLevel.updateHolder(OlympaPvPKit.getInstance().scoreboards.getPlayerScoreboard(this)));
		level.observe("tab_update", () -> OlympaCore.getInstance().getNameTagApi().callNametagUpdate(this));
		level.observe("xp_bar", this::updateXPBar);
		xp.observe("datas", () -> COLUMN_XP.updateAsync(this, xp.get(), null, null));
		xp.observe("xpManagement", new XPManagement(this));
		xp.observe("scoreboard_update", () -> OlympaPvPKit.getInstance().lineLevel.updateHolder(OlympaPvPKit.getInstance().scoreboards.getPlayerScoreboard(this)));
		xp.observe("xp_bar", this::updateXPBar);
		kills.observe("datas", () -> COLUMN_KILLS.updateAsync(this, kills.get(), null, null));
		kills.observe("ranking", () -> OlympaPvPKit.getInstance().totalKillRank.handleNewScore(getName(), (Player) getPlayer(), kills.get()));
		kills.observe("scoreboard_update", () -> OlympaPvPKit.getInstance().lineKills.updateHolder(OlympaPvPKit.getInstance().scoreboards.getPlayerScoreboard(this)));
	}

	@Override
	public OlympaMoney getGameMoney() {
		return money;
	}

	public ObservableInt getKillStreak() {
		return killStreak;
	}

	public int getKillStreakMax() {
		return killStreakMax.get();
	}

	public int getLevel() {
		return level.get();
	}

	public void setLevel(int level) {
		this.level.set(Math.min(Math.max(level, 1), XPManagement.XP_PER_LEVEL.length - 1));
	}

	public int getXP() {
		return xp.get();
	}

	public void setXP(int xp) {
		this.xp.set(Math.min(Math.max(xp, 0), Short.MAX_VALUE));
	}

	public void updateXPBar() {
		Player p = (Player) getPlayer();
		p.setLevel(level.get());
		float xpRatio = (float) xp.get() / (float) XPManagement.getXPToLevelUp(level.get());
		if (xpRatio <= 1)
			p.setExp(xpRatio);
	}

	public ObservableInt getKills() {
		return kills;
	}

	public boolean isInPvPZone() {
		return usedKit != null;
	}

	public Kit getUsedKit() {
		return usedKit;
	}

	public void setInPvPZone(Kit usedKit) {
		this.usedKit = usedKit;
		OlympaPvPKit.getInstance().lineKit.updateHolder(OlympaPvPKit.getInstance().scoreboards.getPlayerScoreboard(this));
	}

	@Override
	public void loadDatas(ResultSet resultSet) throws SQLException {
		money.set(resultSet.getDouble("money"));
		killStreak.set(resultSet.getInt("kill_streak"));
		killStreakMax.set(resultSet.getInt("kill_streak_max"));
		level.set(resultSet.getInt("level"));
		xp.set(resultSet.getInt("xp"));
		kills.set(resultSet.getInt("kills"));
		updateXPBar();
	}

	public static OlympaPlayerPvPKit get(Player p) {
		return AccountProviderAPI.getter().get(p.getUniqueId());
	}

}
