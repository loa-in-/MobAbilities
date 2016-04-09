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
		if(cmd.getName().equalsIgnoreCase("ma")) {
			if(sender instanceof Player) {
				player = (Player)sender;
			} else {
				sender.sendMessage(ChatColor.RED + "You cannot use this command.");
			}
			Abilities oldAbilities = playerAbilities.get(player), newAbilities = null;
			String argument = args.length > 0 ? args[0].toLowerCase(Locale.ENGLISH) : "";
			switch(argument) {
				case "blaze":
					newAbilities = Abilities.BLAZE;
					break;
				case "chicken":
					newAbilities = Abilities.CHICKEN;
					break;
				case "creeper":
					newAbilities = Abilities.CREEPER;
					break;
				case "enderman":
					newAbilities = Abilities.ENDERMAN;
					break;
				case "ghast":
					newAbilities = Abilities.GHAST;
					break;
				case "horse":
					newAbilities = Abilities.HORSE;
					break;
				case "pig_zombie":
					newAbilities = Abilities.PIG_ZOMBIE;
					break;
				case "skeleton":
					newAbilities = Abilities.SKELETON;
					break;
				case "spider":
					newAbilities = Abilities.SPIDER;
					break;
				case "squid":
					newAbilities = Abilities.SQUID;
					break;
				case "remove":
					newAbilities = null;
					break;
				case "state":
					//TODO
				default:
					sender.sendMessage(ChatColor.GREEN + getFullName() + " - Help");
					sender.sendMessage(ChatColor.GOLD + " /ma <type> - Apply new abilities");
					sender.sendMessage(ChatColor.GOLD + " /ma remove - Remove your abilities");
					sender.sendMessage(ChatColor.GRAY + " Types: blaze, chicken, creeper, enderman, ghast, horse, pig_zombie, skeleton, spider, squid");
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