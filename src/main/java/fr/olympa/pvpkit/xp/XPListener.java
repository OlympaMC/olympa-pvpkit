package fr.olympa.pvpkit.xp;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.olympa.api.utils.Prefix;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;

public class XPListener implements Listener {
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player dead = e.getEntity();
		Player killer = dead.getKiller();
		
		OlympaPlayerPvPKit deadOP = OlympaPlayerPvPKit.get(dead);
		int deadKS = deadOP.getKillStreak().get();
		deadOP.getKillStreak().set(0);
		
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
				
			e.setDeathMessage("§c" + dead.getName() + " s'est fait tuer par " + killer.getName() + ".");
		}else {
			e.setDeathMessage("§c" + dead.getName() + " est mort.");
		}
	}
	
}
