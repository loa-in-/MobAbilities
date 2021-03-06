package de.robingrether.mobabilities;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.robingrether.idisguise.api.DisguiseEvent;
import de.robingrether.idisguise.api.PlayerInteractDisguisedPlayerEvent;
import de.robingrether.idisguise.api.UndisguiseEvent;
import de.robingrether.mobabilities.io.UpdateCheck;

public class EventListener implements Listener {
	
	private MobAbilities plugin;
	
	public EventListener(MobAbilities plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(player.hasPermission("MobAbilities.update") && plugin.configuration.UPDATE_CHECK) {
			plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new UpdateCheck(plugin, player, plugin.configuration.UPDATE_DOWNLOAD), 20L);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(plugin.playerAbilities.containsKey(player)) {
			plugin.playerAbilities.remove(player).remove(player);
			plugin.disguiseApi.undisguise(player, false);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDisguise(DisguiseEvent event) {
		Player player = event.getPlayer();
		if(plugin.playerAbilities.containsKey(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onUndisguise(UndisguiseEvent event) {
		Player player = event.getPlayer();
		if(plugin.playerAbilities.containsKey(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityTarget(EntityTargetEvent event) {
		if(!event.isCancelled() && event.getTarget() != null && event.getTarget().getType().equals(EntityType.PLAYER)) {
			Player target = (Player)event.getTarget();
			if(plugin.playerAbilities.containsKey(target)) {
				event.setCancelled(!plugin.playerAbilities.get(target).allowTargetByEntity(event.getEntityType()));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityShootBow(EntityShootBowEvent event) {
		if(!event.isCancelled() && event.getEntityType().equals(EntityType.PLAYER)) {
			Player player = (Player)event.getEntity();
			if(plugin.playerAbilities.containsKey(player)) {
				plugin.playerAbilities.get(player).handleBowShoot(player, event.getBow(), event.getProjectile());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if(!event.isCancelled() && event.getEntityType().equals(EntityType.PLAYER)) {
			Player player = (Player)event.getEntity();
			if(plugin.playerAbilities.containsKey(player)) {
				event.setDamage(event instanceof EntityDamageByEntityEvent ? plugin.playerAbilities.get(player).handleDamage(player, event.getDamage(), event.getCause(), ((EntityDamageByEntityEvent)event).getDamager()) : plugin.playerAbilities.get(player).handleDamage(player, event.getDamage(), event.getCause(), null));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		if(!event.isCancelled()) {
			Player player = event.getPlayer();
			if(plugin.playerAbilities.containsKey(player)) {
				Location to = event.getFrom().add(plugin.playerAbilities.get(player).handleMove(player, event.getTo().toVector().subtract(event.getFrom().toVector())));
				to.setYaw(event.getTo().getYaw());
				to.setPitch(event.getTo().getPitch());
				event.setTo(to);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(plugin.playerAbilities.containsKey(player)) {
			plugin.playerAbilities.get(player).handleInteract(player, event.getItem());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		if(plugin.playerAbilities.containsKey(player)) {
			plugin.playerAbilities.get(player).handleInteractEntity(player, entity);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractDisguisedPlayer(PlayerInteractDisguisedPlayerEvent event) {
		Player other = event.getPlayer();
		Player player = event.getRightClicked();
		if(plugin.playerAbilities.containsKey(player)) {
			plugin.playerAbilities.get(player).handleRightClickedByPlayer(player, other);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(!event.isCancelled()) {
			Player player = event.getPlayer();
			if(plugin.playerAbilities.containsKey(player)) {
				plugin.playerAbilities.get(player).handleTeleport(player, event.getCause());
			}
		}
	}
	
}