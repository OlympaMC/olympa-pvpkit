package fr.olympa.pvpkit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import fr.olympa.api.item.ItemUtils;
import fr.olympa.api.utils.Prefix;
import fr.olympa.pvpkit.kits.gui.KitListGUI;
import fr.olympa.pvpkit.xp.XPManagement;

public class PvPKitListener implements Listener {
	
	private static final ItemStack MENU_ITEM = ItemUtils.item(Material.NETHER_STAR, "§bSélecteur de Kit", "§8> §7Clique ici pour ouvrir", "  §7le menu des Kits !");
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player dead = e.getEntity();
		Player killer = dead.getKiller();
		
		OlympaPlayerPvPKit deadOP = OlympaPlayerPvPKit.get(dead);
		int deadKS = deadOP.getKillStreak().get();
		
		if (killer != null) {
			OlympaPlayerPvPKit killerOP = OlympaPlayerPvPKit.get(killer);
			killerOP.getKillStreak().increment();
			int killerKS = killerOP.getKillStreak().get();
			
			int xpGain;
			if (deadKS >= 30) {
				xpGain = 5;
			}else if (deadKS >= 20) {
				xpGain = 4;
			}else if (deadKS >= 10) {
				xpGain = 3;
			}else if (deadKS >= 5) {
				xpGain = 2;
			}else xpGain = 1;

			if (killerKS >= 50) {
				xpGain *= 6;
			}else if (killerKS >= 40) {
				xpGain *= 5;
			}else if (killerKS >= 30) {
				xpGain *= 4;
			}else if (killerKS >= 20) {
				xpGain *= 3;
			}else if (killerKS >= 10) {
				xpGain *= 2;
			}
			
			Prefix.DEFAULT_GOOD.sendMessage(killer, "§eTu gagnes §6§l%d xp§e !", xpGain);
			killerOP.setXP(killerOP.getXP() + xpGain);
			killerOP.getGameMoney().give(xpGain);
				
			e.setDeathMessage("§4➤ §c" + dead.getName() + " (" + deadOP.getUsedKit().getId() + ") s'est fait tuer par " + killer.getName() + " (" + killerOP.getUsedKit().getId() + ")");
		}else {
			e.setDeathMessage("§4➤ §c" + dead.getName() + " est mort.");
		}
		
		deadOP.getKillStreak().set(0);
		deadOP.setInPvPZone(null);
		
		e.setDroppedExp(0);
		e.getDrops().clear();
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e) {
		OlympaPlayerPvPKit player = OlympaPlayerPvPKit.get(e.getPlayer());
		e.setFormat(XPManagement.getLevelPrefix(player.getLevel()) + " " + player.getGroupPrefix() + "%s " + player.getGroup().getChatSuffix() + " %s");
	}
	
	@EventHandler
	public void onJoinLocation(PlayerSpawnLocationEvent e) {
		e.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation());
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
		giveMenuItem(e.getPlayer());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().getInventory().clear();
		giveMenuItem(e.getPlayer());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getHand() == EquipmentSlot.HAND) {
			if (MENU_ITEM.equals(e.getItem())) {
				new KitListGUI(OlympaPlayerPvPKit.get(e.getPlayer())).create(e.getPlayer());
			}
		}
	}
	
	public static void giveMenuItem(Player p) {
		p.getInventory().setItem(4, MENU_ITEM);
	}
	
}
