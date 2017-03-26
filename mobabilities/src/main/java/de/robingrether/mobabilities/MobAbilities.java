package de.robingrether.mobabilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bstats.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.mobabilities.io.Configuration;
import de.robingrether.mobabilities.io.UpdateCheck;
import de.robingrether.util.StringUtil;

public class MobAbilities extends JavaPlugin {
	
	public static MobAbilities instance;
	
	Map<Player, Abilities> playerAbilities = new ConcurrentHashMap<Player, Abilities>();
	EventListener listener;
	DisguiseAPI disguiseApi;
	Configuration configuration;
	private Metrics metrics;
	
	public void onEnable() {
		instance = this;
		checkDirectory();
		configuration = new Configuration(this);
		configuration.loadData();
		configuration.saveData();
		listener = new EventListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		disguiseApi = getServer().getServicesManager().getRegistration(DisguiseAPI.class).getProvider();
		getLogger().log(Level.INFO, "Linked with iDisguise.");
		metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SingleLineChart("appliedAbilities") {
			
			public int getValue() {
				return playerAbilities.size();
			}
			
		});
		metrics.addCustomChart(new Metrics.AdvancedPie("abilityTypes") {
			
			public HashMap<String, Integer> getValues(HashMap<String, Integer> valueMap) {
				for(Entry<Player, Abilities> entry : playerAbilities.entrySet()) {
					valueMap.put(entry.getValue().name(), valueMap.containsKey(entry.getValue().name()) ? valueMap.get(entry.getValue().name()) + 1 : 1);
				}
				return valueMap;
			}
			
		});
		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			
			public void run() {
				for(Player player : playerAbilities.keySet()) {
					playerAbilities.get(player).applyPotionEffects(player);
				}
			}
			
		}, 600L, 600L);
		if(configuration.UPDATE_CHECK) {
			getServer().getScheduler().runTaskLaterAsynchronously(this, new UpdateCheck(this, getServer().getConsoleSender(), configuration.UPDATE_DOWNLOAD), 20L);
		}
		getLogger().log(Level.INFO, String.format("%s enabled!", getFullName()));
	}
	
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		for(Player player : playerAbilities.keySet()) {
			disguiseApi.undisguise(player, false);
			playerAbilities.get(player).remove(player);
			player.sendMessage(ChatColor.GOLD + "Removed your abilities.");
		}
		playerAbilities.clear();
		getLogger().log(Level.INFO, String.format("%s disabled!", getFullName()));
	}
	
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		Player player = null;
		if(command.getName().equalsIgnoreCase("mobabilities")) {
			if(sender instanceof Player) {
				player = (Player)sender;
			} else {
				sender.sendMessage(ChatColor.RED + "You cannot use this command.");
				return true;
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
				sender.sendMessage(ChatColor.GOLD + " /" + alias + " <type> - Apply new abilities");
				sender.sendMessage(ChatColor.GOLD + " /" + alias + " remove - Remove your abilities");
				sender.sendMessage(ChatColor.GOLD + " /" + alias + " state - Check your applied abilities");
				sender.sendMessage(ChatColor.GRAY + " Types: " + Abilities.listAbilities());
				return true;
			}
			if(newAbilities == null) {
				if(oldAbilities == null) {
					sender.sendMessage(ChatColor.RED + "Wrong usage.");
				} else {
					playerAbilities.remove(player);
					disguiseApi.undisguise(player, false);
					oldAbilities.remove(player);
					sender.sendMessage(ChatColor.GOLD + "Removed your abilities.");
				}
			} else {
				if(oldAbilities != null && oldAbilities.equals(newAbilities)) {
					sender.sendMessage(ChatColor.RED + "Already applied.");
				} else if(!newAbilities.hasPermission(player)) {
					sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
				} else {
					if(oldAbilities != null) {
						playerAbilities.remove(player);
						disguiseApi.undisguise(player, false);
						oldAbilities.remove(player);
					}
					disguiseApi.disguise(player, newAbilities.getDisguiseType().newInstance(), false);
					newAbilities.apply(player);
					playerAbilities.put(player, newAbilities);
					sender.sendMessage(ChatColor.GOLD + "Applied new abilities.");
				}
			}
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<String>();
		if(command.getName().equalsIgnoreCase("mobabilities")) {
			if(sender instanceof Player) {
				if(args.length < 2) {
					completions.add("state");
					if(playerAbilities.containsKey(sender)) {
						completions.add("remove");
					}
					completions.addAll(Abilities.values());
				}
			}
		}
		if(args.length > 0) {
			for(int i = 0; i < completions.size(); i++) {
				if(!StringUtil.startsWithIgnoreCase(completions.get(i), args[args.length - 1])) {
					completions.remove(i);
					i--;
				}
			}
		}
		return completions;
	}
	
	private void checkDirectory() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
	}
	
	public String getVersion() {
		return getDescription().getVersion();
	}
	
	public String getFullName() {
		return "MobAbilities " + getVersion();
	}
	
	public File getPluginFile() {
		return getFile();
	}
	
}