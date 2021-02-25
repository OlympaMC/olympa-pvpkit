package fr.olympa.pvpkit.kits;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import fr.olympa.api.item.ItemUtils;
import fr.olympa.api.sql.SQLColumn;
import fr.olympa.api.sql.SQLTable;
import fr.olympa.api.utils.spigot.SpigotUtils;
import fr.olympa.pvpkit.OlympaPvPKit;

public class KitsManager {
	
	private final SQLTable<Kit> table;
	public final SQLColumn<Kit> columnId = new SQLColumn<Kit>("id", "VARCHAR(45)", Types.VARCHAR).setPrimaryKey(Kit::getId).setUpdatable();
	public final SQLColumn<Kit> columnName = new SQLColumn<Kit>("name", "VARCHAR(45)", Types.VARCHAR).setUpdatable();
	public final SQLColumn<Kit> columnItems = new SQLColumn<Kit>("items", "VARBINARY(8000)", Types.VARBINARY).setUpdatable();
	public final SQLColumn<Kit> columnItemsDescription = new SQLColumn<Kit>("items_description", "TEXT DEFAULT NULL", Types.VARCHAR).setUpdatable();
	public final SQLColumn<Kit> columnIcon = new SQLColumn<Kit>("icon", "VARBINARY(8000)", Types.VARBINARY).setUpdatable();
	public final SQLColumn<Kit> columnLevel = new SQLColumn<Kit>("level", "SMALLINT DEFAULT 1", Types.SMALLINT).setUpdatable().setNotDefault();
	
	private final List<Kit> kits;

	public KitsManager() throws SQLException {
		table = new SQLTable<>("pvpkit_kits", Arrays.asList(columnId, columnName, columnItems, columnItemsDescription, columnIcon, columnLevel), set -> {
			try {
				String itemsDescription = set.getString("items_description");
				return new Kit(set.getString("id"), set.getString("name"), ItemUtils.deserializeItemsArray(set.getBytes("items")), (itemsDescription == null || itemsDescription.isEmpty()) ? new String[0]
						: itemsDescription.split("\\|\\|"), SpigotUtils.deserialize(set.getBytes("icon")), set.getInt("level"));
			}catch (ClassNotFoundException | IOException ex) {
				ex.printStackTrace();
			}
			return null;
		}).createOrAlter();
		
		kits = table.selectAll();
		Collections.sort(kits, (o1, o2) -> Integer.compare(o1.getMinLevel(), o2.getMinLevel()));
		OlympaPvPKit.getInstance().sendMessage("%d kits chargés !", kits.size());
	}
	
	public List<Kit> getKits() {
		return kits;
	}
	
	public Kit addKit(String id, String name, ItemStack[] items, ItemStack icon, int minLevel) throws SQLException, IOException {
		Kit kit = new Kit(id, name, items, new String[0], icon, minLevel);
		table.insert(id, name, ItemUtils.serializeItemsArray(items), SpigotUtils.serialize(icon), minLevel);
		kits.add(kit);
		return kit;
	}
	
	public Kit getKit(String id) {
		return kits.stream().filter(kit -> kit.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
	}
	
	public void removeKit(Kit kit) throws SQLException {
		table.delete(kit);
		kits.remove(kit);
	}
	
}
