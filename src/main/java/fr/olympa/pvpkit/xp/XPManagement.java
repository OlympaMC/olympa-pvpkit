package fr.olympa.pvpkit.xp;

import fr.olympa.api.utils.observable.Observable.Observer;
import fr.olympa.pvpkit.OlympaPlayerPvPKit;

public class XPManagement implements Observer {
	
	private static final int[] XP_PER_LEVEL =
			{
					-1,
					20,
					30,
					45,
					70,
					90,
					120,
					160,
					200,
					250,
					300,
					350,
					410,
					470,
					520,
					570,
					620,
					670,
					720,
					770,
					830,
					890,
					950,
					1010,
					1070,
					1130,
					1190,
					1250,
					1310,
					1370,
					1440,
					1510,
					1580,
					1650,
					1720,
					1870,
					1940,
					2010,
					2080,
					2150,
					2230,
					2310,
					2390,
					2470,
					2550,
					2630,
					2710,
					2790,
					2870,
					2950,
					3040,
					3130,
					3220,
					3310,
					3400,
					3490,
					3580,
					3570,
					3660,
					3750,
					3850,
					3950,
					4050,
					4150,
					4250,
					4350,
					4450,
					4550,
					4650,
					4750,
					4860,
					4970,
					5080,
					5190,
					5300,
					5410,
					5520,
					5630,
					5740,
					5850,
					5970,
					6090,
					6110,
					6230,
					6350,
					6370,
					6490,
					6610,
					6730,
					6850,
					6980,
					7710,
					7240,
					7370,
					7500,
					7630,
					7760,
					7890,
					8020,
					8150 };
	
	private OlympaPlayerPvPKit player;
	
	public XPManagement(OlympaPlayerPvPKit player) {
		this.player = player;
	}
	
	@Override
	public void changed() throws Exception {
		int xpToLevelUp = getXPToLevelUp(player.getLevel().get());
		if (player.getXP().get() >= xpToLevelUp) {
			player.getLevel().increment();
			player.getXP().set(Math.max(0, player.getXP().get() - xpToLevelUp));
		}
	}
	
	public static int getXPToLevelUp(int level) {
		return XP_PER_LEVEL[level];
	}
	
}
