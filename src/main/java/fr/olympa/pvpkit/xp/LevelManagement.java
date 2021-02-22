package fr.olympa.pvpkit.xp;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import fr.olympa.api.utils.Prefix;
import fr.olympa.api.utils.observable.Observable.Observer;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;
import fr.olympa.pvpkit.OlympaPvPKit;

public class LevelManagement implements Observer {
	
	private OlympaPlayerPvPKit player;
	
	public LevelManagement(OlympaPlayerPvPKit player) {
		this.player = player;
	}
	
	@Override
	public void changed() throws Exception {
		Player p = player.getPlayer();
		int newLevel = player.getLevel().get();
		Prefix.DEFAULT_GOOD.sendMessage(p, "Félicitations ! §lTu passes au niveau §2%d§a§l !", newLevel);
		Bukkit.getOnlinePlayers().stream().filter(x -> x != p).forEach(x -> Prefix.DEFAULT_GOOD.sendMessage(x, "§l%s §apasse au niveau %d !", p.getName(), newLevel));
		
		Runnable launchFirework = () -> {
			Firework firework = p.getWorld().spawn(p.getLocation(), Firework.class);
			FireworkMeta meta = firework.getFireworkMeta();
			meta.setPower(0);
			meta.addEffect(FireworkEffect.builder().with(Type.BURST).withColor(Color.LIME).withFade(Color.GREEN).withTrail().build());
			firework.setFireworkMeta(meta);
		};
		if (Bukkit.isPrimaryThread()) {
			launchFirework.run();
		}else Bukkit.getScheduler().runTask(OlympaPvPKit.getInstance(), launchFirework);
	}
	
}
