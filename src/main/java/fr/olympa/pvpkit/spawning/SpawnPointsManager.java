package fr.olympa.pvpkit.spawning;

import java.sql.SQLException;
import java.sql.Types;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
	
	public Location getBestLocation() {
		if (locations.isEmpty()) return null;
		return locations.stream()
				.map(location -> new AbstractMap.SimpleEntry<>(location, getClosestEntityDistance(location))).sorted((o1, o2) -> Double.compare(o1.getValue(), o2.getValue())) // map locations with their closest entity distance
				.reduce((o1, o2) -> o2) // get last object
				.get().getKey();
	}
	
	private double getClosestEntityDistance(Location location) {
		return location.getWorld().getNearbyEntities(location, 25, 25, 25, x -> x instanceof Player).stream().mapToDouble(entity -> entity.getLocation().distanceSquared(location)).sorted().findFirst().orElse(999);
	}
	
	public void addSpawnPoint(Location location) throws SQLException {
		table.insert(SpigotUtils.convertLocationToString(location));
		locations.add(location);
	}
	
}
