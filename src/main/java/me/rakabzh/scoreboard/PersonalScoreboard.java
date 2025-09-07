package me.rakabzh.scoreboard;

import me.rakabzh.Main;
import me.rakabzh.PlayerBedwars;
import me.rakabzh.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
 * This file is part of SamaGamesAPI.
 *
 * SamaGamesAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PersonalScoreboard {
    private Main main;
    private Player player;
    private final UUID uuid;
    private final ObjectiveSign objectiveSign;

    PersonalScoreboard(Player player, Main main){
        this.player = player;
        uuid = player.getUniqueId();
        objectiveSign = new ObjectiveSign("sidebar", "DevPlugin");

        reloadData();
        objectiveSign.addReceiver(player);
        this.main = main;
    }

    public void reloadData(){}

    public void setLines(String ip){
        if(main.getINSTANCE().isState(State.WAITING)) {
            objectiveSign.setDisplayName("§eServeur");

            objectiveSign.setLine(0, "§1");
            objectiveSign.setLine(1, "§6Joueurs: §a" + Bukkit.getOnlinePlayers().size() + "/20");
            objectiveSign.setLine(2, "§6Pseudo: §b" + player.getName());
            objectiveSign.setLine(3, "§2");
            objectiveSign.setLine(4, ip);

            objectiveSign.updateLines();
        }
        else if (main.getINSTANCE().isState(State.PLAYING)) {
            objectiveSign.setDisplayName("§eServeur");
            objectiveSign.setLine(0, "Temps de jeux : " + main.getTimer() + "s");
            String string = "";
            if (main.getTimer() < 5*60){
                string = "Diamants II à 5min";
            } else if (main.getTimer() < 11*60){
                string = "Emeraudes II à 11min";
            }else if (main.getTimer() < 17*60){
                string = "Diamants III à 17min";
            }else if (main.getTimer() < 23*60){
                string = "Emeraudes III à 23min";
            }else if (main.getTimer() < 29*60){
                string = "Destructions des lits à 29min";
            }else if (main.getTimer() < 49*60){
                string = "Fin de la partie à 49min";
            }
            objectiveSign.setLine(1, "Prochaine étapes : " + string);
            int lineScoreBoard = 2;
            for (PlayerBedwars playerBedwars : main.getPlayerList()) {
                String state = "";
                String prefix = "";
                if (Color.RED.equals(playerBedwars.getVraiColor())) {
                    prefix = "§c";
                }
                else if (Color.FUCHSIA.equals(playerBedwars.getVraiColor())) {
                    prefix = "§d";
                }
                else if (Color.GRAY.equals(playerBedwars.getVraiColor())) {
                    prefix = "§7";
                }
                else if (Color.BLUE.equals(playerBedwars.getVraiColor())) {
                    prefix = "§9";
                }
                else if (Color.GREEN.equals(playerBedwars.getVraiColor())) {
                    prefix = "§a";
                }
                else if (Color.YELLOW.equals(playerBedwars.getVraiColor())) {
                    prefix = "§e";
                }
                else if (Color.AQUA.equals(playerBedwars.getVraiColor())) {
                    prefix = "§b";
                }
                else {
                    prefix = "§f";
                }
                switch (playerBedwars.getState()){
                    case 0:
                        state = "§aV";
                        break;
                    case 1:
                        state = "§a1";
                        break;
                    case 2:
                        state = "§c❌";
                        break;
                }
                objectiveSign.setLine(lineScoreBoard,prefix + playerBedwars.getColor().charAt(0) + " §f"+playerBedwars.getColor() + ": " + state);
                lineScoreBoard++;
            }
            objectiveSign.setLine(8, ip);

            objectiveSign.updateLines();
        }
    }

    public void onLogout(){
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
    }
}