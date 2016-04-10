package de.robingrether.mobabilities;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.util.StringUtil;

public class MobAbilities extends JavaPlugin {
	
	Map<Player, Abilities> playerAbilities = new ConcurrentHashMap<Player, Abilities>();
	EventListener listener;
	DisguiseAPI disguiseApi;
	
	public void onEnable() {
		listener = new EventListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		disguiseApi = getServer().getServicesManager().getRegistration(DisguiseAPI.class).getProvider();
		getLogger().log(Level.INFO, "Linked with iDisguise.");
		getLogger().log(Level.INFO, String.format("%s enabled!", getFullName()));
	}
	
	public void onDisable() {
		for(Player player : playerAbilities.keySet()) {
			disguiseApi.undisguise(player, false);
			playerAbilities.get(player).removePotionEffects(player);
			player.sendMessage(ChatColor.GOLD + "Removed your abilities.");
		}
		playerAbilities.clear();
		getLogger().log(Level.INFO, String.format("%s disabled!", getFullName()));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if(StringUtil.equalsIgnoreCase(cmd.getName(), "mobabilities", "ma")) {
			if(sender instanceof Player) {
				player = (Player)sender;
			} else {
				sender.sendMessage(ChatColor.RED + "You cannot use this command.");
			}
			Abilities oldAbilities = playerAbilities.get(player), newAbilities = null;
			String argument = args.length > 0 ? args[0].toLowerCase(Locale.ENGLISH) : "";
			if(argument.equalsIgnoreCase("remove")) {
			} else if(argument.equalsIgnoreCase("state")) {
				if(playerAbilities.containsKey(player)) {
					sender.sendMessage(ChatColor.GOLD + "Your abilities: " + playerAbilities.get(player).name());
				} else {
					sender.sendMessage(ChatColor.GOLD + "No abilities applied.");
				}
				return true;
			} else if((newAbilities = Abilities.fromName(argument)) == null) {
				sender.sendMessage(ChatColor.GREEN + getFullName() + " - Help");
				sender.sendMessage(ChatColor.GOLD + " /" + cmd.getName() + " <type> - Apply new abilities");
				sender.sendMessage(ChatColor.GOLD + " /" + cmd.getName() + " remove - Remove your abilities");
				sender.sendMessage(ChatColor.GOLD + " /" + cmd.getName() + " state - Check your applied abilities");
				sender.sendMessage(ChatColor.GRAY + " Types: " + Abilities.listAbilities());
				return true;
			}
			if(newAbilities == null) {
				if(oldAbilities == null) {
					sender.sendMessage(ChatColor.RED + "Wrong usage.");
				} else {
					playerAbilities.remove(player);
					disguiseApi.undisguise(player, false);
					oldAbilities.removePotionEffects(player);
					sender.sendMessage(ChatColor.GOLD + "Removed your abilities.");
				}
			} else {
				if(oldAbilities != null && oldAbilities.equals(newAbilities)) {
					sender.sendMessage(ChatColor.RED + "Already applied.");
				} else {
					if(oldAbilities != null) {
						playerAbilities.remove(player);
						disguiseApi.undisguise(player, false);
						oldAbilities.removePotionEffects(player);
					}
					disguiseApi.disguise(player, newAbilities.getDisguiseType().newInstance(), false);
					newAbilities.applyPotionEffects(player);
					playerAbilities.put(player, newAbilities);
					sender.sendMessage(ChatColor.GOLD + "Applied new abilities.");
				}
			}
		}
		return true;
	}
	
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	public String getFullName() {
		return "MobAbilities " + getVersion();
	}
	
}