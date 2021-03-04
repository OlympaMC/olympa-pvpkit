package fr.olympa.pvpkit;

import fr.olympa.api.groups.OlympaGroup;
import fr.olympa.api.permission.OlympaSpigotPermission;

public class PvPKitPermissions {
	
	private PvPKitPermissions() {}
	
	public static final OlympaSpigotPermission TP_TIME_BYPASS = new OlympaSpigotPermission(OlympaGroup.RESP);
	
	public static final OlympaSpigotPermission KIT_MANAGE_COMMAND = new OlympaSpigotPermission(OlympaGroup.GAMEMASTER);
	
	public static final OlympaSpigotPermission MONEY_COMMAND = new OlympaSpigotPermission(OlympaGroup.PLAYER);
	public static final OlympaSpigotPermission MONEY_COMMAND_OTHER = new OlympaSpigotPermission(OlympaGroup.ASSISTANT);
	public static final OlympaSpigotPermission MONEY_COMMAND_MANAGE = new OlympaSpigotPermission(OlympaGroup.RESP);
	
	public static final OlympaSpigotPermission LEVEL_COMMAND = new OlympaSpigotPermission(OlympaGroup.PLAYER);
	public static final OlympaSpigotPermission LEVEL_COMMAND_OTHER = new OlympaSpigotPermission(OlympaGroup.ASSISTANT);
	public static final OlympaSpigotPermission LEVEL_COMMAND_MANAGE = new OlympaSpigotPermission(OlympaGroup.RESP);
	
	public static final OlympaSpigotPermission SPAWNPOINT_COMMAND_MANAGE = new OlympaSpigotPermission(OlympaGroup.RESP);
	
}
