package fr.olympa.pvpkit;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;

import fr.olympa.api.sql.SQLColumn;
import fr.olympa.api.sql.SQLTable;
import fr.olympa.api.utils.spigot.SpigotUtils;

public class SpawnPointsManager {
	
	private SQLTable<Location> table;
	
	private List<Location> locations;
	
	public SpawnPointsManager() throws SQLException {
		table = new SQLTable<>("pvpkit_spawnpoints", Arrays.asList(new SQLColumn<Location>("location", "VARCHAR(100)", Types.VARCHAR).setPrimaryKey(SpigotUtils::convertLocationToString)), resultSet -> SpigotUtils.convertStringToLocation(resultSet.getString("location")));
		table.createOrAlter();
		
		locations = table.selectAll();
	}
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public Location getRandomLocation() {
		if (locations.isEmpty()) return null;
		return locations.get(ThreadLocalRandom.current().nextInt(locations.size()));
	}
	
	public void addSpawnPoint(Location location) throws SQLException {
		table.insert(SpigotUtils.convertLocationToString(location));
		locations.add(location);
	}
	
}
