package de.robingrether.mobabilities;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import de.robingrether.idisguise.api.DisguiseAPI;

public class MobAbilities extends JavaPlugin {
	
	private DisguiseAPI disguiseApi;
	
	public void onEnable() {
		disguiseApi = getServer().getServicesManager().getRegistration(DisguiseAPI.class).getProvider();
		getLogger().log(Level.INFO, String.format("%s enabled!", getFullName()));
	}
	
	public void onDisable() {
		
	}
	
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	public String getFullName() {
		return "MobAbilities " + getVersion();
	}
	
}