package fr.olympa.pvpkit.ranking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import fr.olympa.api.sql.statement.OlympaStatement;

public class BestKillStreakRank extends AbstractRank {
	
	public BestKillStreakRank(Location location) throws SQLException {
		super("total_kill", location);
	}
	
	@Override
	public String getHologramName() {
		return "§c§lMeilleur kill streak";
	}
	
	@Override
	public String getMessageName() {
		return "du meilleur kill streak";
	}
	
	@Override
	protected void fillUpScores(ScoreEntry[] scores) throws SQLException {
		OlympaStatement top10Statement = new OlympaStatement(
				"SELECT pseudo, kill_streak_max"
				+ " FROM pvpkit_players"
				+ " INNER JOIN commun.players ON pvpkit_players.player_id = commun.players.id"
						+ " ORDER BY kill_streak_max DESC LIMIT 10");
		try (PreparedStatement statement = top10Statement.createStatement()) {
			ResultSet resultSet = top10Statement.executeQuery(statement);
			int i = 0;
			while (resultSet.next()) {
				int ks = resultSet.getInt("kill_streak_max");
				if (ks != 0) scores[i].fill(resultSet.getString("pseudo"), ks);
				i++;
			}
			resultSet.close();
		}
	}
	
}
