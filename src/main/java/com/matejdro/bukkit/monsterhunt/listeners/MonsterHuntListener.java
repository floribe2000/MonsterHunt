package com.matejdro.bukkit.monsterhunt.listeners;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.Set;

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
import com.matejdro.bukkit.monsterhunt.Setting;
import com.matejdro.bukkit.monsterhunt.Util;

public class MonsterHuntListener implements Listener {
    //HashMap<Integer, Player> lastHits = new HashMap<Integer, Player>();
    //HashMap<Integer, Integer> lastHitCauses = new HashMap<Integer, Integer>();

    @EventHandler()
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            MonsterHuntWorld world = HuntWorldManager.getWorld(player.getWorld().getName());

            if (world == null || world.getWorld() == null) return;
            if (world.worldSettings.getDeathPenalty() == 0) return;

            if (world.state > 1 && world.Score.containsKey(player.getName())) {
                double score = world.Score.get(player.getName()) + 0.00;
                score = score - (score * world.worldSettings.getDeathPenalty() / 100.00);
                world.Score.put(player.getName(), (int) Math.round(score));
                Util.Message(world.worldSettings.getString(Setting.DeathMessage), player);
            }
        }

        if (!HuntZone.isInsideZone(event.getEntity().getLocation())) return;
        if (event.getEntity() == null || !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return;
        MonsterHuntWorld world = HuntWorldManager.getWorld(event.getEntity().getWorld().getName());
        if (world == null || world.getWorld() == null || world.state < 2) return;
        Util.Debug("test");
        kill((LivingEntity) event.getEntity(), world);
    }


    private void kill(LivingEntity monster, MonsterHuntWorld world) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) monster.getLastDamageCause();
        String name;
        Player player = null;

        String cause = "General";
        if (event.getCause() == DamageCause.PROJECTILE && event.getDamager() instanceof Projectile) {
            if (event.getDamager() instanceof Snowball)
                cause = "Snowball";
            else
                cause = "Arrow";
            if (((Projectile) event.getDamager()).getShooter() instanceof LivingEntity) {
                LivingEntity shooter = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
                if (shooter instanceof Player) player = (Player) shooter;
            }
        } else if (event.getDamager() instanceof Wolf && ((Wolf) event.getDamager()).isTamed()) {
            cause = "Wolf";
            player = (Player) ((Wolf) event.getDamager()).getOwner();
        }

        if (player == null) {
            if (!(event.getDamager() instanceof Player)) return;
            player = (Player) event.getDamager();

            if (cause.equals("General")) {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    cause = String.valueOf(0);
                } else {
                    cause = String.valueOf(player.getInventory().getItemInMainHand().getType());
                }
            }
        }


        int points = 0;
        if (monster instanceof Skeleton) {
            points = world.worldSettings.getMonsterValue("Skeleton", cause);
            name = "Skeleton";
        } else if (monster instanceof Spider) {
            points = world.worldSettings.getMonsterValue("Spider", cause);
            name = "Spider";
        } else if (monster instanceof Creeper creeper) {
            if (creeper.isPowered()) {
                points = world.worldSettings.getMonsterValue("ElectrifiedCreeper", cause);
                name = "Electrified Creeper";
            } else {
                points = world.worldSettings.getMonsterValue("Creeper", cause);
                name = "Creeper";
            }
        } else if (monster instanceof Ghast) {
            points = world.worldSettings.getMonsterValue("Ghast", cause);
            name = "Ghast";
        } else if (monster instanceof Slime) {
            points = world.worldSettings.getMonsterValue("Slime", cause);
            name = "Slime";
        } else if (monster instanceof PigZombie) {
            points = world.worldSettings.getMonsterValue("ZombiePigman", cause);
            name = "Zombie Pigman";
        } else if (monster instanceof Giant) {
            points = world.worldSettings.getMonsterValue("Giant", cause);
            name = "Giant";
        } else if (monster instanceof Zombie) {
            points = world.worldSettings.getMonsterValue("Zombie", cause);
            name = "Zombie";
        } else if (monster instanceof Wolf) {
            Wolf wolf = (Wolf) monster;
            if (wolf.isTamed()) {
                points = world.worldSettings.getMonsterValue("TamedWolf", cause);
                name = "Tamed Wolf";
            } else {
                points = world.worldSettings.getMonsterValue("WildWolf", cause);
                name = "Wild Wolf";
            }

        } else if (monster instanceof Player) {
            points = world.worldSettings.getMonsterValue("Player", cause);
            name = "Player";
        } else if (monster instanceof Enderman) {
            points = world.worldSettings.getMonsterValue("Enderman", cause);
            name = "Enderman";
        } else if (monster instanceof Silverfish) {
            points = world.worldSettings.getMonsterValue("Silverfish", cause);
            name = "Silverfish";
        } else if (monster instanceof CaveSpider) {
            points = world.worldSettings.getMonsterValue("CaveSpider", cause);
            name = "CaveSpider";
        } else if (monster instanceof EnderDragon) {
            points = world.worldSettings.getMonsterValue("EnderDragon", cause);
            name = "Ender Dragon";
        } else if (monster instanceof MagmaCube) {
            points = world.worldSettings.getMonsterValue("MagmaCube", cause);
            name = "Magma Cube";
        } else if (monster instanceof MushroomCow) {
            points = world.worldSettings.getMonsterValue("Mooshroom", cause);
            name = "Mooshroom";
        } else if (monster instanceof Chicken) {
            points = world.worldSettings.getMonsterValue("Chicken", cause);
            name = "Chicken";
        } else if (monster instanceof Cow) {
            points = world.worldSettings.getMonsterValue("Cow", cause);
            name = "Cow";
        } else if (monster instanceof Blaze) {
            points = world.worldSettings.getMonsterValue("Blaze", cause);
            name = "Blaze";
        } else if (monster instanceof Pig) {
            points = world.worldSettings.getMonsterValue("Pig", cause);
            name = "Pig";
        } else if (monster instanceof Sheep) {
            points = world.worldSettings.getMonsterValue("Sheep", cause);
            name = "Sheep";
        } else if (monster instanceof Snowman) {
            points = world.worldSettings.getMonsterValue("SnowGolem", cause);
            name = "Snow Golem";
        } else if (monster instanceof Squid) {
            points = world.worldSettings.getMonsterValue("Squid", cause);
            name = "Squid";
        } else if (monster instanceof Villager) {
            points = world.worldSettings.getMonsterValue("Villager", cause);
            name = "Villager";
        } else {
            return;
        }
        if (points < 1) return;

        if (!world.Score.containsKey(player.getName()) && !world.worldSettings.getBoolean(Setting.EnableSignup))
            world.Score.put(player.getName(), 0);
        if (world.Score.containsKey(player.getName())) {
            if (!(world.worldSettings.getBoolean(Setting.OnlyCountMobsSpawnedOutsideBlackList) ^ world.properlySpawned.contains(monster.getEntityId())) && world.worldSettings.getBoolean(Setting.OnlyCountMobsSpawnedOutside)) {
                String message = world.worldSettings.getString(Setting.KillMobSpawnedInsideMessage);
                Util.Message(message, player);
                world.blacklist.add(monster.getEntityId());
                return;

            }
            int newscore = world.Score.get(player.getName()) + points;

            if (world.worldSettings.getBoolean(Setting.AnnounceLead)) {
                Entry<String, Integer> leadpoints = null;
                for (Entry<String, Integer> e : world.Score.entrySet()) {
                    if (leadpoints == null || e.getValue() > leadpoints.getValue() || (e.getValue() == leadpoints.getValue() && leadpoints.getKey().equalsIgnoreCase(player.getName()))) {
                        leadpoints = e;
                    }

                }
                Util.Debug(leadpoints.toString());
                Util.Debug(String.valueOf(newscore));
                Util.Debug(String.valueOf(!leadpoints.getKey().equals(player.getName())));

                if (leadpoints != null && newscore > leadpoints.getValue() && !leadpoints.getKey().equals(player.getName())) {
                    String message = world.worldSettings.getString(Setting.MessageLead);
                    message = message.replace("<Player>", player.getName());
                    message = message.replace("<Points>", String.valueOf(newscore));
                    message = message.replace("<World>", world.name);
                    Util.Broadcast(message);

                }

            }

            world.Score.put(player.getName(), newscore);
            world.blacklist.add(monster.getEntityId());

            world.properlySpawned.remove((Object) monster.getEntityId());

            String message = world.worldSettings.getKillMessage(cause);
            message = message.replace("<MobValue>", String.valueOf(points));
            message = message.replace("<MobName>", name);
            message = message.replace("<Points>", String.valueOf(newscore));
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
            if (!world.worldSettings.getBoolean(Setting.OnlyCountMobsSpawnedOutside)) return;
            Block block = event.getLocation().getBlock();
            int maxHeight = world.getWorld().getMaxHeight();
            int huntHeightLimit = world.worldSettings.getInt(Setting.OnlyCountMobsSpawnedOutsideHeightLimit);
            if (huntHeightLimit > 0) {
                maxHeight = huntHeightLimit;
            }
            while (block.getY() < maxHeight) { //checks all blocks over the mob doc!!!!
                block = block.getRelative(BlockFace.UP);
                boolean isBlockUnderSky = isLeaveOrAir(block.getType());
                if (!isBlockUnderSky && logMaterials.contains(block.getType())) {
                    isBlockUnderSky = isTreeBlock(block);
                }

                if(!isBlockUnderSky && world.worldSettings.getBoolean(Setting.OnlyCountMobsSpawnedOutsideBlackList)) {
                    world.properlySpawned.add(event.getEntity().getEntityId());
                    return;
                }
            }

            if (!world.worldSettings.getBoolean(Setting.OnlyCountMobsSpawnedOutsideBlackList)) {
                world.properlySpawned.add(event.getEntity().getEntityId());
            }
        }
    }

    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getInventory().getItemInMainHand().getType() == Setting.SelectionTool.getMaterial()) {
            if (HuntZoneCreation.players.containsKey(event.getPlayer().getName())) {
                HuntZoneCreation.select(event.getPlayer(), event.getClickedBlock());
                event.setCancelled(true);
            }
        }

    }

}
