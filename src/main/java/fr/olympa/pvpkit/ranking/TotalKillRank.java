package fr.olympa.pvpkit.ranking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import fr.olympa.api.sql.statement.OlympaStatement;
import fr.olympa.api.utils.spigot.AbstractRank;

public class TotalKillRank extends AbstractRank {
	
	public TotalKillRank(Location location) throws SQLException {
		super("total_kill", location);
	}
	
	@Override
	public String getHologramName() {
		return "§e§lKills totaux";
	}
	
	@Override
	public String getMessageName() {
		return "des kills";
	}
	
	@Override
	protected void fillUpScores(ScoreEntry[] scores) throws SQLException {
		OlympaStatement top10Statement = new OlympaStatement(
				"SELECT pseudo, kills"
				+ " FROM pvpkit_players"
				+ " INNER JOIN commun.players ON pvpkit_players.player_id = commun.players.id"
				+ " ORDER BY kills DESC LIMIT 10");
		try (PreparedStatement statement = top10Statement.createStatement()) {
			ResultSet resultSet = top10Statement.executeQuery(statement);
			int i = 0;
			while (resultSet.next()) {
				int kills = resultSet.getInt("kills");
				if (kills != 0) scores[i].fill(resultSet.getString("pseudo"), kills);
				i++;
			}
			resultSet.close();
		}
	}
	
}
