package me.rakabzh.scoreboard;

import me.rakabzh.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
public class ScoreboardManager {
    private Main main;
    private final Map<UUID, PersonalScoreboard> scoreboards;
    private final ScheduledFuture glowingTask;
    private final ScheduledFuture reloadingTask;
    private int ipCharIndex;
    private int cooldown;

    public ScoreboardManager(Main main) {
        this.main = main;
        scoreboards = new HashMap<>();
        ipCharIndex = 0;
        cooldown = 0;

        glowingTask = Main.getINSTANCE().getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            String ip = colorIpAt();
            for (PersonalScoreboard scoreboard : scoreboards.values())
                Main.getINSTANCE().getExecutorMonoThread().execute(() -> scoreboard.setLines(ip));
        }, 80, 80, TimeUnit.MILLISECONDS);

        reloadingTask = Main.getINSTANCE().getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            for (PersonalScoreboard scoreboard : scoreboards.values())
                Main.getINSTANCE().getExecutorMonoThread().execute(scoreboard::reloadData);
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void onDisable() {
        scoreboards.values().forEach(PersonalScoreboard::onLogout);
    }

    public void onLogin(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            return;
        }
        scoreboards.put(player.getUniqueId(), new PersonalScoreboard(player,main));
    }

    public void onLogout(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            scoreboards.get(player.getUniqueId()).onLogout();
            scoreboards.remove(player.getUniqueId());
        }
    }

    public void update(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            scoreboards.get(player.getUniqueId()).reloadData();
        }
    }

    private String colorIpAt() {
        String ip = "play.serveur.fr";

        if (cooldown > 0) {
            cooldown--;
            return ChatColor.YELLOW + ip;
        }

        StringBuilder formattedIp = new StringBuilder();

        if (ipCharIndex > 0) {
            formattedIp.append(ip.substring(0, ipCharIndex - 1));
            formattedIp.append(ChatColor.GOLD).append(ip.substring(ipCharIndex - 1, ipCharIndex));
        } else {
            formattedIp.append(ip.substring(0, ipCharIndex));
        }

        formattedIp.append(ChatColor.RED).append(ip.charAt(ipCharIndex));

        if (ipCharIndex + 1 < ip.length()) {
            formattedIp.append(ChatColor.GOLD).append(ip.charAt(ipCharIndex + 1));

            if (ipCharIndex + 2 < ip.length())
                formattedIp.append(ChatColor.YELLOW).append(ip.substring(ipCharIndex + 2));

            ipCharIndex++;
        } else {
            ipCharIndex = 0;
            cooldown = 50;
        }

        return ChatColor.YELLOW + formattedIp.toString();
    }

}