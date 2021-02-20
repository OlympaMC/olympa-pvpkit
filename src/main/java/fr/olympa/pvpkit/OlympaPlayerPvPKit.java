package fr.olympa.pvpkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.olympa.api.economy.MoneyPlayerInterface;
import fr.olympa.api.economy.OlympaMoney;
import fr.olympa.api.provider.AccountProvider;
import fr.olympa.api.provider.OlympaPlayerObject;
import fr.olympa.api.sql.SQLColumn;
import fr.olympa.api.utils.observable.ObservableInt;
import fr.olympa.pvpkit.xp.LevelManagement;
import fr.olympa.pvpkit.xp.XPManagement;

public class OlympaPlayerPvPKit extends OlympaPlayerObject implements MoneyPlayerInterface {
	
	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_MONEY = new SQLColumn<OlympaPlayerPvPKit>("money", "DOUBLE NULL DEFAULT 0", Types.DOUBLE).setUpdatable();
	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_KILL_STREAK = new SQLColumn<OlympaPlayerPvPKit>("kill_streak", "SMALLINT NULL DEFAULT 0", Types.SMALLINT).setUpdatable();
	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_LEVEL = new SQLColumn<OlympaPlayerPvPKit>("level", "SMALLINT NULL DEFAULT 1", Types.SMALLINT).setUpdatable();
	private static final SQLColumn<OlympaPlayerPvPKit> COLUMN_XP = new SQLColumn<OlympaPlayerPvPKit>("xp", "INTEGER NULL DEFAULT 0", Types.INTEGER).setUpdatable();
	
	public static final List<SQLColumn<OlympaPlayerPvPKit>> COLUMNS = Arrays.asList(COLUMN_MONEY, COLUMN_KILL_STREAK, COLUMN_LEVEL, COLUMN_XP);
	
	private OlympaMoney money = new OlympaMoney(0);
	private ObservableInt killStreak = new ObservableInt(0);
	private ObservableInt level = new ObservableInt(1);
	private ObservableInt xp = new ObservableInt(0);
	
	public OlympaPlayerPvPKit(UUID uuid, String name, String ip) {
		super(uuid, name, ip);
		money.observe("datas", () -> COLUMN_MONEY.updateAsync(this, money.get(), null, null));
		killStreak.observe("datas", () -> COLUMN_KILL_STREAK.updateAsync(this, killStreak.get(), null, null));
		level.observe("datas", () -> COLUMN_LEVEL.updateAsync(this, level.get(), null, null));
		level.observe("levelManagement", new LevelManagement(this));
		xp.observe("datas", () -> COLUMN_XP.updateAsync(this, xp.get(), null, null));
		xp.observe("xpManagement", new XPManagement(this));
	}
	
	@Override
	public OlympaMoney getGameMoney() {
		return money;
	}
	
	public ObservableInt getKillStreak() {
		return killStreak;
	}
	
	public ObservableInt getLevel() {
		return level;
	}
	
	public ObservableInt getXP() {
		return xp;
	}
	
	@Override
	public void loadDatas(ResultSet resultSet) throws SQLException {
		money.set(resultSet.getDouble("money"));
		killStreak.set(resultSet.getInt("kill_streak"));
		level.set(resultSet.getInt("level"));
		xp.set(resultSet.getInt("xp"));
	}
	
	public static OlympaPlayerPvPKit get(Player p) {
		return AccountProvider.get(p.getUniqueId());
	}
	
}
