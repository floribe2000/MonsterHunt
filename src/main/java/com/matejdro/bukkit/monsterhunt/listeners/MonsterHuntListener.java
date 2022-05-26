package com.matejdro.bukkit.monsterhunt.listeners;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.Set;

import de.geistlande.monsterhunt.Localizer;
import de.geistlande.monsterhunt.Settings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.HuntZone;
import com.matejdro.bukkit.monsterhunt.HuntZoneCreation;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Util;

public class MonsterHuntListener implements Listener {

    @EventHandler()
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            MonsterHuntWorld world = HuntWorldManager.getWorld(player.getWorld().getName());

            if (world == null || world.getWorld() == null) return;
            if (world.worldSettings.getDeathPenalty() == 0) return;

            if (world.state > 1 && world.Score.containsKey(player.getUniqueId())) {
                double score = world.Score.get(player.getUniqueId()) + 0.00;
                score = score - (score * world.worldSettings.getDeathPenalty() / 100.00);
                world.Score.put(player.getUniqueId(), (int) Math.round(score));
                Util.Message(Localizer.INSTANCE.getString("personal.death", world.worldSettings.getDeathPenalty()), player);
            }
        }

        if (!HuntZone.isInsideZone(event.getEntity().getLocation())) return;
        event.getEntity();
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return;
        MonsterHuntWorld world = HuntWorldManager.getWorld(event.getEntity().getWorld().getName());
        if (world == null || world.getWorld() == null || world.state < 2) return;
        kill(event.getEntity(), world);
    }


    private void kill(LivingEntity monster, MonsterHuntWorld world) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) monster.getLastDamageCause();
        String name;
        Player player;

        if (event == null) return;

        String cause;
        if (event.getCause() == DamageCause.PROJECTILE && event.getDamager() instanceof Projectile projectile) {
            cause = projectile instanceof Snowball ? "snowball" : "arrow";
            if (projectile.getShooter() instanceof Player shooter) {
                player = shooter;
            } else {
                return;
            }
        } else if (event.getDamager() instanceof Wolf wolf && wolf.getOwner() instanceof Player owner) {
            cause = "wolf";
            player = owner;
        } else if (event.getDamager() instanceof Player actingPlayer) {
            player = actingPlayer;
            if (player.getInventory().getItemInMainHand().getType().isAir()) {
                cause = String.valueOf(0);
            } else {
                cause = String.valueOf(player.getInventory().getItemInMainHand().getType());
            }
        } else {
            return;
        }

        int points;
        if (monster instanceof Creeper creeper) {
            if (creeper.isPowered()) {
                points = world.worldSettings.findMobPoints("charged_creeper");
            } else {
                points = world.worldSettings.findMobPoints(EntityType.CREEPER.name());
            }
        } else if (monster instanceof Wolf wolf) {
            if (wolf.isTamed()) {
                points = world.worldSettings.findMobPoints("tamed_wolf");
            } else {
                points = world.worldSettings.findMobPoints(EntityType.WOLF.name());
            }
        } else {
            var type = monster.getType();
            points = world.worldSettings.findMobPoints(type.name());
        }
        name = monster.getName();

        if (points < 1) return;

        if (!world.Score.containsKey(player.getUniqueId()) && !world.worldSettings.getEnableSignup())
            world.Score.put(player.getUniqueId(), 0);
        if (world.Score.containsKey(player.getUniqueId())) {
            // TODO: add blacklist again?
            if (world.worldSettings.getMobSettings().getOnlyCountMobsSpawnedOutside() && !world.properlySpawned.contains(monster.getEntityId())) {
                String message = Localizer.INSTANCE.getString("personal.kill.inside");
                Util.Message(message, player);
                return;

            }
            int newScore = world.Score.get(player.getUniqueId()) + points;

            if (world.worldSettings.getAnnounceLead()) {
                var currentLeader = world.Score.entrySet().stream().max(Entry.comparingByValue()).orElse(null);
                if (currentLeader == null || !currentLeader.getKey().equals(player.getUniqueId())) {
                    String message = Localizer.INSTANCE.getString("hunt.leadChanged", player.getName(), newScore, world.name);
                    Util.Broadcast(message);
                }
            }

            world.Score.put(player.getUniqueId(), newScore);
            world.properlySpawned.remove(monster.getEntityId());

            String messageKey = "personal.mobKilled";
            if (!cause.isBlank()) {
                messageKey += "." + cause;
            }
            String message = Localizer.INSTANCE.getString(messageKey, points, name, newScore);
            Util.Message(message, player);
        }
    }

    private boolean isTreeBlock(Block block) {
        return Arrays.stream(BlockFace.values())
            .filter(BlockFace::isCartesian)
            .map(block::getRelative)
            .filter(secondBlock -> secondBlock.getBlockData() instanceof Leaves)
            .map(secondBlock -> (Leaves)secondBlock)
            .anyMatch(leave -> !leave.isPersistent());

    //    return ((leavesAndAir.contains( block.getRelative(BlockFace.NORTH).getType()) || (block.getRelative(BlockFace.EAST).getType() == mat) ||
    //        (block.getRelative(BlockFace.WEST).getType() == mat) || (block.getRelative(BlockFace.SOUTH).getType() == mat) ||
    //        (block.getRelative(BlockFace.UP).getType() == mat) || (block.getRelative(BlockFace.DOWN).getType() == mat));

    }

    private final Set<Material> leaves = EnumSet.of(
        Material.ACACIA_LEAVES,
        Material.AZALEA_LEAVES,
        Material.BIRCH_LEAVES,
        Material.OAK_LEAVES,
        Material.JUNGLE_LEAVES,
        Material.DARK_OAK_LEAVES,
        Material.FLOWERING_AZALEA_LEAVES,
        Material.SPRUCE_LEAVES);

    private final Set<Material> logMaterials = EnumSet.of(
        Material.ACACIA_LOG,
        Material.OAK_LOG,
        Material.BIRCH_LOG,
        Material.JUNGLE_LOG,
        Material.DARK_OAK_LOG,
        Material.SPRUCE_LOG);

    private boolean isLeaveOrAir(Material material) {
        return material.isAir() || leaves.contains(material);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Creature) {
            if (!event.getLocation().isWorldLoaded()) return;
            @SuppressWarnings("ConstantConditions") MonsterHuntWorld world = HuntWorldManager.getWorld(event.getLocation().getWorld().getName());
            if (world == null || world.getWorld() == null) return;
            if (world.state == 0) return;
            if (!world.worldSettings.getMobSettings().getOnlyCountMobsSpawnedOutside()) return;
            Block block = event.getLocation().getBlock();
            int maxHeight = world.getWorld().getMaxHeight();
            int huntHeightLimit = world.worldSettings.getMobSettings().getOutsideHeightCheck();
            if (huntHeightLimit > 0) {
                maxHeight = Math.min(event.getLocation().getBlock().getY() + huntHeightLimit, maxHeight);
            }
            while (block.getY() < maxHeight) { //checks all blocks over the mob doc!!!!
                block = block.getRelative(BlockFace.UP);
                boolean isBlockUnderSky = isLeaveOrAir(block.getType());
                if (!isBlockUnderSky && logMaterials.contains(block.getType())) {
                    isBlockUnderSky = isTreeBlock(block);
                }

                if(!isBlockUnderSky) {
                    world.properlySpawned.add(event.getEntity().getEntityId());
                    return;
                }
            }

            world.properlySpawned.add(event.getEntity().getEntityId());
        }
    }

    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getInventory().getItemInMainHand().getType() == Settings.INSTANCE.getConfig().getSelectionTool()) {
            if (HuntZoneCreation.players.containsKey(event.getPlayer().getName())) {
                HuntZoneCreation.select(event.getPlayer(), event.getClickedBlock());
                event.setCancelled(true);
            }
        }
    }
}
