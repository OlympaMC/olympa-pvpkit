package fr.olympa.pvpkit;

import java.io.File;
import java.lang.reflect.Field;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import com.mojang.datafixers.DataFixer;

import fr.olympa.api.common.provider.AccountProvider;
import net.minecraft.server.v1_16_R3.Convertable.ConversionSession;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.WorldNBTStorage;

public class CustomWorldNBTStorage extends WorldNBTStorage {
	
	public static final NamespacedKey PLAYER_KIT = new NamespacedKey(OlympaPvPKit.getInstance(), "pvpkit_kit");
	
	public CustomWorldNBTStorage(ConversionSession convertable_conversionsession, DataFixer datafixer) throws ReflectiveOperationException {
		super(convertable_conversionsession, datafixer);
		File playerDir = new File(OlympaPvPKit.getInstance().getDataFolder(), "playerdata");
		playerDir.mkdirs();
		Field field = WorldNBTStorage.class.getDeclaredField("playerDir");
		field.setAccessible(true);
		field.set(this, playerDir);
	}
	
	@Override
	public void save(EntityHuman entityhuman) {
		OlympaPlayerPvPKit player = AccountProvider.getter().get(entityhuman.getUniqueID());
		if (player == null) return; // appelé après que le joueur soit enlevé du cache
		if (player.isInPvPZone()) {
			entityhuman.getBukkitEntity().getPersistentDataContainer().set(PLAYER_KIT, PersistentDataType.STRING, player.getUsedKit().getId());
			OlympaPvPKit.getInstance().sendMessage("Sauvegarde du joueur %s avec le kit %s dans %s.", entityhuman.getName(), player.getUsedKit().getId(), getPlayerDir().getPath());
			super.save(entityhuman);
		}else {
			File file = new File(getPlayerDir(), entityhuman.getUniqueIDString() + ".dat");
			if (file.exists()) file.delete();
			file = new File(getPlayerDir(), entityhuman.getUniqueIDString() + ".dat_old");
			if (file.exists()) file.delete();
		}
	}
	
}
