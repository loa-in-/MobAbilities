package de.robingrether.mobabilities;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import de.robingrether.idisguise.disguise.DisguiseType;

public abstract class Abilities {
	
	public boolean allowTargetByEntity(EntityType entityType) { return true; }
	
	public void applyPotionEffects(Player player) {}
	
	public abstract DisguiseType getDisguiseType();
	
	public void handleBowShoot(Player player, ItemStack bow) {}
	
	public double handleDamage(Player player, double damage, DamageCause cause) { return damage; }
	
	public Vector handleMove(Player player, Vector movement) { return movement; }
	
	public void handleRightClick(Player player) {}
	
	public void handleRightClickedByPlayer(Player player, Player other) {}
	
	public void handleTeleport(Player player, TeleportCause cause) {}
	
	public void removePotionEffects(Player player) {}
	
	public static final Abilities BLAZE = new Abilities() {
		
		public boolean allowTargetByEntity(EntityType entityType) {
			return !entityType.equals(EntityType.BLAZE);
		}
		
		public void applyPotionEffects(Player player) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
		}
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.BLAZE;
		}
		
		public void handleRightClick(Player player) {
			player.launchProjectile(SmallFireball.class);
		}
		
		public void removePotionEffects(Player player) {
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
		}
		
	};
	
	public static final Abilities CHICKEN = new Abilities() {
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.CHICKEN;
		}
		
		public double handleDamage(Player player, double damage, DamageCause cause) {
			if(cause.equals(DamageCause.FALL)) {
				return 0.0;
			}
			return damage;
		}
		
		public Vector handleMove(Player player, Vector movement) {
			return movement.setY(movement.getY() * 0.1);
		}
		
	};
	
	public static final Abilities CREEPER = new Abilities() {
		
		public boolean allowTargetByEntity(EntityType entityType) {
			return !entityType.equals(EntityType.CREEPER);
		}
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.CREEPER;
		}
		
		public void handleRightClick(Player player) {
			if(player.isSneaking() && player.getLocation().getPitch() == 90.0F) {
				player.getWorld().createExplosion(player.getLocation(), 1.0F);
			}
		}
		
	};
	
	public static final Abilities ENDERMAN = new Abilities() {
		
		public boolean allowTargetByEntity(EntityType entityType) {
			return !entityType.equals(EntityType.ENDERMAN);
		}
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.ENDERMAN;
		}
		
		public void handleTeleport(Player player, TeleportCause cause) {
			if(cause.equals(TeleportCause.ENDER_PEARL)) {
				if(player.getInventory().contains(Material.ENDER_PEARL)) {
					ItemStack itemStack = player.getInventory().getItem(player.getInventory().first(Material.ENDER_PEARL));
					itemStack.setAmount(itemStack.getAmount() + 1);
				} else {
					player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
				}
				player.updateInventory();
			}
		}
		
	};
	
	public static final Abilities GHAST = new Abilities() {
		
		public void applyPotionEffects(Player player) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
			player.setAllowFlight(true);
		}
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.GHAST;
		}
		
		public void handleRightClick(Player player) {
			player.launchProjectile(LargeFireball.class);
		}
		
		public void removePotionEffects(Player player) {
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
			player.setAllowFlight(false);
		}
		
	};
	
	public static final Abilities HORSE = new Abilities() {
		
		public void applyPotionEffects(Player player) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
		}
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.HORSE;
		}
		
		public void handleRightClickedByPlayer(Player player, Player other) {
			player.setPassenger(other);
		}
		
		public void removePotionEffects(Player player) {
			player.removePotionEffect(PotionEffectType.JUMP);
			player.removePotionEffect(PotionEffectType.SPEED);
		}
		
	};
	
	public static final Abilities PIG_ZOMBIE = new Abilities() {
		
		public boolean allowTargetByEntity(EntityType entityType) {
			return !entityType.equals(EntityType.PIG_ZOMBIE);
		}
		
		public void applyPotionEffects(Player player) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
		}
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.PIG_ZOMBIE;
		}
		
		public void removePotionEffects(Player player) {
			player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
		}
		
	};
	
	public static final Abilities SKELETON = new Abilities() {
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.SKELETON;
		}
		
		public void handleBowShoot(Player player, ItemStack bow) {
			if(!bow.containsEnchantment(Enchantment.ARROW_INFINITE)) {
				if(player.getInventory().contains(Material.ARROW)) {
					ItemStack itemStack = player.getInventory().getItem(player.getInventory().first(Material.ARROW));
					itemStack.setAmount(itemStack.getAmount() + 1);
				} else {
					player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
				}
				player.updateInventory();
			}
		}
		
	};
	
	public static final Abilities SPIDER = new Abilities() {
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.SPIDER;
		}
		
		public Vector handleMove(Player player, Vector movement) {
			Block block = player.getLocation().getBlock();
			if(!block.getRelative(BlockFace.EAST).isEmpty() || !block.getRelative(BlockFace.NORTH).isEmpty() || !block.getRelative(BlockFace.SOUTH).isEmpty() || !block.getRelative(BlockFace.WEST).isEmpty()) {
				player.sendBlockChange(block.getLocation(), Material.VINE, (byte)0);
			}
			return movement;
		}
		
	};
	
	public static final Abilities SQUID = new Abilities() {
		
		public void applyPotionEffects(Player player) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
		}
		
		public DisguiseType getDisguiseType() {
			return DisguiseType.SQUID;
		}
		
		public void removePotionEffects(Player player) {
			player.removePotionEffect(PotionEffectType.WATER_BREATHING);
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
		
	};
	
}