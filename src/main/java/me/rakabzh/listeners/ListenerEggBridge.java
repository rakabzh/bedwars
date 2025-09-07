package me.rakabzh.listeners;

import me.rakabzh.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class ListenerEggBridge implements Listener {
    private final Main main;

    int timer = 0;
    BukkitTask timerthread;

    public ListenerEggBridge(Main main) {
        this.main = main;
    }
    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Egg)) return;

        Egg egg = (Egg) event.getEntity();
        if (!(egg.getShooter() instanceof Player)) return;

        Player player = (Player) egg.getShooter();

        // Ne pas annuler l'événement ici, laisser l'œuf voler naturellement

        // Suivre l'œuf avec un timer individuel pour chaque œuf
        BukkitTask eggTask = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
            private Location lastLocation = egg.getLocation().clone();
            private int ticks = 0;

            @Override
            public void run() {
                // Vérifier si l'œuf existe encore
                if (egg.isDead() || !egg.isValid() || ticks > 100) { // Max 5 secondes
                    this.cancel();
                    return;
                }

                Location currentLocation = egg.getLocation();

                // Créer des blocs entre la dernière position et la position actuelle
                createBridgePath(lastLocation, currentLocation, player);

                // Mettre à jour la dernière position
                lastLocation = currentLocation.clone();
                ticks++;
            }

            private void cancel() {
                // Arrêter cette tâche spécifique
                Bukkit.getScheduler().cancelTask(this.hashCode());
            }

        }, 0L, 1L); // Exécuter chaque tick pour plus de fluidité
    }

    // Méthode pour créer le chemin de blocs
    private void createBridgePath(Location fromLoc, Location toLoc, Player player) {
        // Calculer la direction et la distance
        Vector direction = toLoc.toVector().subtract(fromLoc.toVector());
        double distance = direction.length();

        // Si la distance est trop petite, ne rien faire
        if (distance < 0.3) return;

        // Normaliser la direction
        direction.normalize();

        // Créer des blocs le long du chemin
        for (double d = 0; d <= distance; d += 0.4) { // Tous les 0.4 blocs pour éviter les trous
            Location bridgePos = fromLoc.clone().add(direction.clone().multiply(d));

            // Placer le bloc principal 2 blocs sous l'œuf
            Location mainBlockLoc = new Location(
                    bridgePos.getWorld(),
                    Math.floor(bridgePos.getX()),
                    Math.floor(bridgePos.getY()) - 2,
                    Math.floor(bridgePos.getZ())
            );

            placeBridgeBlock(mainBlockLoc, player);

            // Gérer les mouvements diagonaux
            handleDiagonalMovement(fromLoc, toLoc, mainBlockLoc, player);
        }
    }

    // Méthode pour gérer les mouvements diagonaux
    private void handleDiagonalMovement(Location from, Location to, Location mainBlock, Player player) {
        double deltaX = to.getX() - from.getX();
        double deltaZ = to.getZ() - from.getZ();

        // Vérifier si c'est un mouvement diagonal significatif
        if (Math.abs(deltaX) > 0.2 && Math.abs(deltaZ) > 0.2) {
            // Mouvement diagonal détecté, placer des blocs supplémentaires

            // Déterminer la direction principale
            if (Math.abs(deltaX) > Math.abs(deltaZ)) {
                // Mouvement principal en X
                int offsetX = deltaX > 0 ? 1 : -1;
                Location extraBlock = mainBlock.clone().add(offsetX, 0, 0);
                placeBridgeBlock(extraBlock, player);
            } else {
                // Mouvement principal en Z
                int offsetZ = deltaZ > 0 ? 1 : -1;
                Location extraBlock = mainBlock.clone().add(0, 0, offsetZ);
                placeBridgeBlock(extraBlock, player);
            }

            // Placer un bloc aux quatre coins pour les grandes diagonales
            if (Math.abs(deltaX) > 0.5 && Math.abs(deltaZ) > 0.5) {
                int offsetX = deltaX > 0 ? 1 : -1;
                int offsetZ = deltaZ > 0 ? 1 : -1;

                Location cornerBlock1 = mainBlock.clone().add(offsetX, 0, 0);
                Location cornerBlock2 = mainBlock.clone().add(0, 0, offsetZ);
                Location cornerBlock3 = mainBlock.clone().add(offsetX, 0, offsetZ);

                placeBridgeBlock(cornerBlock1, player);
                placeBridgeBlock(cornerBlock2, player);
                placeBridgeBlock(cornerBlock3, player);
            }
        }
    }

    // Méthode pour placer un bloc de bridge
    private void placeBridgeBlock(Location location, Player player) {
        // Vérifier que la position est valide
        if (location.getY() < 0 || location.getY() > 255) return;

        Block block = location.getBlock();

        // Ne placer que si c'est de l'air
        if (block.getType() != Material.AIR) return;

        // Placer le bloc
        block.setType(Material.WOOL);

        // Définir la couleur selon le joueur
        try {
            byte colorData = main.getPlayerWithPlayer(player).getDataColor();
            block.setData(colorData);
        } catch (Exception e) {
            // Couleur par défaut si erreur
            block.setData((byte) 0);
        }

        // Ajouter à la liste des blocs placés
        main.getBlocksPlaceByPlayer().add(block);
    }

    // Gestionnaire pour quand l'œuf touche quelque chose
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg)) return;

        Egg egg = (Egg) event.getEntity();
        if (!(egg.getShooter() instanceof Player)) return;

        Player player = (Player) egg.getShooter();

        // Placer un bloc final à l'endroit de l'impact
        Location hitLocation = egg.getLocation();
        Location finalBlockLoc = new Location(
                hitLocation.getWorld(),
                Math.floor(hitLocation.getX()),
                Math.floor(hitLocation.getY()) - 1,
                Math.floor(hitLocation.getZ())
        );

        placeBridgeBlock(finalBlockLoc, player);

        // Optionnel : placer quelques blocs autour pour une petite plateforme
        placeBridgeBlock(finalBlockLoc.clone().add(1, 0, 0), player);
        placeBridgeBlock(finalBlockLoc.clone().add(-1, 0, 0), player);
        placeBridgeBlock(finalBlockLoc.clone().add(0, 0, 1), player);
        placeBridgeBlock(finalBlockLoc.clone().add(0, 0, -1), player);
    }

}
