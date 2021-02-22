package fr.olympa.pvpkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import fr.olympa.api.utils.Prefix;
import fr.olympa.pvpkit.xp.XPManagement;

public class PvPKitListener implements Listener {
	
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
			killerOP.getXP().add(xpGain);
			killerOP.getGameMoney().give(xpGain);
				
			e.setDeathMessage("§4➤ §c" + dead.getName() + "(" + deadOP.getUsedKit().getName() + ") s'est fait tuer par " + killer.getName() + " (" + killerOP.getUsedKit().getName() + ")");
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
		e.setFormat(XPManagement.getLevelPrefix(player.getLevel().get()) + " " + player.getGroupPrefix() + "%s " + player.getGroup().getChatSuffix() + " %s");
	}
	
	@EventHandler
	public void onJoinLocation(PlayerSpawnLocationEvent e) {
		e.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().getInventory().clear();
	}
	
}
