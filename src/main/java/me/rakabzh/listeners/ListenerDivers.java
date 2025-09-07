package me.rakabzh.listeners;

import me.rakabzh.Main;
import me.rakabzh.PlayerBedwars;
import me.rakabzh.State;
import me.rakabzh.constants.Messages;
import me.rakabzh.nmsSimple.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class ListenerDivers implements Listener {
    private final Main main;
    HashMap<IronGolem, Player> golemList;

    public ListenerDivers(Main main) {
        this.main = main;
        golemList = new HashMap<>();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
        for (int i = 0; i < event.blockList().size(); i++) {
            Block block = event.blockList().get(i);
            if (main.getBlocksPlaceByPlayer().contains(block) && block.getType() != Material.STAINED_GLASS && block.getType() != Material.OBSIDIAN){
                event.blockList().get(i).setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Snowball){
            Location loc = projectile.getLocation();
            loc.getWorld().spawnEntity(loc, EntityType.SILVERFISH);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();

        if (item == null) return;


        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (item.getType() == Material.FIREBALL) {
                Location eye = event.getPlayer().getEyeLocation();
                Location loc = eye.add(eye.getDirection().multiply(1.2));
                Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
                fireball.setYield(3);
                fireball.setVelocity(loc.getDirection().normalize().multiply(2));
                fireball.setShooter(event.getPlayer());
                fireball.setIsIncendiary(false);

                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.getInventory().setItemInHand(null);
                }
                event.setCancelled(true);
            }
            if (item.getType().equals(Material.MONSTER_EGG)){
                event.setCancelled(true);
                golemList.put((IronGolem) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.IRON_GOLEM), player);
                player.getInventory().getItemInHand().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        if (!main.isState(State.PLAYING)) return;
        PlayerBedwars playerMove = main.getPlayerWithPlayer(player);
        if (playerMove == null) return;
        PlayerBedwars playerDetectBase = playerMove.isInBasePlayer(player.getLocation());
        if (playerDetectBase == null) {
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            player.removePotionEffect(PotionEffectType.REGENERATION);
            return;
        }
        if (!playerDetectBase.equals(playerMove)) {
            if (playerDetectBase.isMiningTrap()){
                player.addPotionEffect(PotionEffectType.SLOW_DIGGING.createEffect(999,1));
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                    player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    playerDetectBase.setMiningTrap(false);
                },10*20);
            }
            if (playerDetectBase.isBaseTrap()){
                player.addPotionEffect(PotionEffectType.SLOW.createEffect(999,1));
                player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(999,1));
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                    player.removePotionEffect(PotionEffectType.SLOW);
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    playerDetectBase.setBaseTrap(false);
                },8*20);
            }
            if (playerDetectBase.isAlarmTrap()){
                playerDetectBase.setAlarmTrap(false);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                playerDetectBase.getPlayer().sendTitle("§4Joueur dans votre Base!", "Tu es mort");
            }
            if (playerDetectBase.isDefenseTrap()){
                playerDetectBase.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(999,1));
                playerDetectBase.getPlayer().addPotionEffect(PotionEffectType.JUMP.createEffect(999,1));
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                    playerDetectBase.getPlayer().removePotionEffect(PotionEffectType.SPEED);
                    playerDetectBase.getPlayer().removePotionEffect(PotionEffectType.JUMP);
                    playerDetectBase.setDefenseTrap(false);
                },15*20);
            }
            player.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
        } else {
            if (playerDetectBase.isHeal()) {
                player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(9999, 999));
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType().equals(Material.POTION)) {
            PotionMeta p = (PotionMeta) event.getItem().getItemMeta();
            if (p.hasCustomEffect(PotionEffectType.INVISIBILITY)) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other.equals(player)) continue;
                    ProtocolManager.hideArmorForPlayer(player);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {;
                        ProtocolManager.showArmorForPlayer(player);
                    }, 30*20L);
                }
            }
        }
        if (event.getItem().getType().equals(Material.MILK_BUCKET)){
            PlayerBedwars base = main.getPlayerWithPlayer(player).isInBasePlayer(player.getLocation());
            if (base == null || base.equals(main.getPlayerWithPlayer(player))){
                player.sendMessage(Messages.MUST_IS_IN_BASE.getMessage());
                event.setCancelled(true);
                return;
            }
            base.setBaseTrap(false);
            base.setMiningTrap(false);
            base.setAlarmTrap(false);
            base.setDefenseTrap(false);
        }

        event.getItem().setType(Material.AIR);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof IronGolem)) return;
        IronGolem golem = (IronGolem) event.getEntity();
        if (golemList.get(golem).equals(golem.getTarget())){
            golem.setTarget(null);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getBlock().getType().equals(Material.CHEST)) return;
        Location location = event.getBlockPlaced().getLocation();
        System.out.println("chest posé à" + location);
        Player player = event.getPlayer();
        World world = player.getWorld();
        int[] direction = getDirectionFromYaw(player.getEyeLocation().getYaw());
        world.getBlockAt(location).setType(Material.AIR);
        for (int hauteur = 0; hauteur < 6; hauteur++) {
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    Location locationBlockPlace = new Location(world, location.getX()+i, location.getY(), location.getZ()+j);
                    if (world.getBlockAt(locationBlockPlace).getType().equals(Material.AIR) && Math.abs(i)!=Math.abs(j) && !(Math.abs(i) == 1 && j==0) && !(Math.abs(j) == 1 && i==0)) {
                        world.getBlockAt(locationBlockPlace).setType(Material.WOOL);
                        world.getBlockAt(locationBlockPlace).setData(main.getPlayerWithPlayer(player).getDataColor());
                        main.getBlocksPlaceByPlayer().add(world.getBlockAt(locationBlockPlace));
                    }
                }
            }
            Location locationAir = null;
            Location locationLadder = null;
            if (direction[0]==-1) {
                locationLadder = new Location(world, location.getX() + direction[1], location.getY(), location.getZ());
                if (hauteur <= 1) {
                    locationAir = new Location(world, location.getX() + (direction[1]) * -2, location.getY(), location.getZ());

                }
            }
            else {
                locationLadder = new Location(world, location.getX(), location.getY(), location.getZ() + direction[1]);
                if (hauteur <= 1) {
                    locationAir = new Location(world, location.getX(), location.getY(), location.getZ() + (direction[1]) * -2);
                }
            }
            world.getBlockAt(locationLadder).setType(Material.LADDER);
            world.getBlockAt(locationLadder).setData(getRealDirectionFromYaw(location.getYaw()));
            main.getBlocksPlaceByPlayer().add(world.getBlockAt(locationLadder));
            if (hauteur <= 1) {
                world.getBlockAt(locationAir).setType(Material.AIR);
                main.getBlocksPlaceByPlayer().remove(world.getBlockAt(locationAir));
            }
            location.setY(location.getY()+1);
        }
        location.setY(location.getY()-1);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                Location locationBlockPlace = new Location(world, location.getX() + i, location.getY(), location.getZ() + j);
                if (world.getBlockAt(locationBlockPlace).getType().equals(Material.AIR) && !(i==0 && j==0) && !(direction[0]==1 && i==0 && j==direction[1]) && !(direction[1]==1 && i==direction[1] && j==0)) {
                    world.getBlockAt(locationBlockPlace).setType(Material.WOOL);
                    world.getBlockAt(locationBlockPlace).setData(main.getPlayerWithPlayer(player).getDataColor());
                    main.getBlocksPlaceByPlayer().add(world.getBlockAt(locationBlockPlace));
                }
            }
        }
        location.setY(location.getY()+1);
        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                Location locationBlockPlace = new Location(world, location.getX()+i, location.getY(), location.getZ()+j);
                if (world.getBlockAt(locationBlockPlace).getType().equals(Material.AIR) && (Math.abs(i)==3 || Math.abs(j) == 3)) {
                    world.getBlockAt(locationBlockPlace).setType(Material.WOOL);
                    world.getBlockAt(locationBlockPlace).setData(main.getPlayerWithPlayer(player).getDataColor());
                    main.getBlocksPlaceByPlayer().add(world.getBlockAt(locationBlockPlace));
                    if((i+3)%2==0&&(j+3)%2==0){
                        locationBlockPlace = new Location(world, location.getX()+i, location.getY()+1, location.getZ()+j);
                        world.getBlockAt(locationBlockPlace).setType(Material.WOOL);
                        world.getBlockAt(locationBlockPlace).setData(main.getPlayerWithPlayer(player).getDataColor());
                        main.getBlocksPlaceByPlayer().add(world.getBlockAt(locationBlockPlace));
                    }
                }
            }
        }
    }

    public static int[] getDirectionFromYaw(float yaw) {
        // Normaliser le yaw entre 0 et 360
        yaw = (yaw + 360) % 360;

        // Déterminer la direction basée sur le yaw
        if (yaw >= 315 || yaw < 45) {
            return new int[]{1,1};  // 0° = Sud
        } else if (yaw >= 45 && yaw < 135) {
            return new int[]{-1,-1};  // 90° = Ouest
        } else if (yaw >= 135 && yaw < 225) {
            return new int[]{1,-1}; // 180° = Nord
        } else { // yaw >= 225 && yaw < 315
            return new int[]{-1,1};  // 270° = Est
        }
    }

    public static byte getRealDirectionFromYaw(float yaw) {
        // Normaliser le yaw entre 0 et 360
        yaw = (yaw + 360) % 360;

        // Déterminer la direction basée sur le yaw
        if (yaw >= 315 || yaw < 45) {
            return 3;  // 0° = Sud
        } else if (yaw >= 45 && yaw < 135) {
            return 4;  // 90° = Ouest
        } else if (yaw >= 135 && yaw < 225) {
            return 2; // 180° = Nord
        } else { // yaw >= 225 && yaw < 315
            return 5;  // 270° = Est
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        event.setCancelled(event.getBlock().getType().equals(Material.LADDER));
    }

}
