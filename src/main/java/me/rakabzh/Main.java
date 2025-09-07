package me.rakabzh;

import me.rakabzh.commands.CommandYML;
import me.rakabzh.constants.Messages;
import me.rakabzh.listeners.*;
import me.rakabzh.nmsSimple.ProtocolManager;
import me.rakabzh.scoreboard.ScoreboardManager;
import me.rakabzh.tasks.FinishCycle;
import me.rakabzh.tasks.SetDied;
import me.rakabzh.villagers.ItemShop;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main extends JavaPlugin {
    private static Main INSTANCE;

    private final HashMap<Color, Location> locations = new HashMap<>();
    private final String[] colorTeams = {"Blanc","Rose","Gris","Rouge","Bleu","Vert","Jaune","Cyan"};

    private final List<PlayerBedwars> playerList = new ArrayList<>();
    private final List<ItemShop> itemShopList = new ArrayList<>();
    private final HashMap<Color, Location> locationsVillagerShop = new HashMap<>();
    private final HashMap<Color, Location> locationsVillagerUpgrade = new HashMap<>();
    private final ArrayList<Location> locationsGenerateurPublic = new ArrayList<>();
    private final HashMap<Color, Location> locationsSpawnerGenerateur = new HashMap<>();
    private final List<Block> blocksPlaceByPlayer = new ArrayList<>();
    private State state;
    private int timer = 0;
    private ScoreboardManager scoreboardManager;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledExecutorService executorMonoThread;
    private ProtocolManager protocolManager;
    private YmlManager yml;

    @Override
    public void onEnable() {
        setUp();
    }

    private void setUp() {
        INSTANCE = this;
        state = State.WAITING;
        scheduledExecutorService = Executors.newScheduledThreadPool(16);
        executorMonoThread = Executors.newScheduledThreadPool(1);
        scoreboardManager = new ScoreboardManager(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                protocolManager = new ProtocolManager(Main.this);
            }
        }.runTaskLater(this, 20L);

        yml = new YmlManager(this);
        yml.loadAllYML();

        registerLocations();
        registerCommands();
        registerEvents();
        registerPluginChannel();


        Bukkit.broadcastMessage(Messages.STARTING_SERVER.getMessage());
    }
    private void registerPluginChannel() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void registerCommands() {
        getCommand("config").setExecutor(new CommandYML(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Listener(this), this);
        getServer().getPluginManager().registerEvents(new ListenerDivers(this),this);
        getServer().getPluginManager().registerEvents(new ListenerEggBridge(this),this);
        getServer().getPluginManager().registerEvents(new ListenerProtectionMap(this),this);
        getServer().getPluginManager().registerEvents(new ListenerVillagerCustom(this),this);
    }

    private void registerLocations() {
        if (new CommandYML(this).runCheck(Bukkit.getConsoleSender())) {
            setLocationsColors();
            setLocationsVillager();
            setLocationsSpawnerCurrency();
            setLocationsGenerateurPublic();
        }
        else {
            setState(State.CONFIG);
        }
    }

    private void setLocationsColors() {
        for (String color : colorTeams) {
            YamlConfiguration config_yml = getYml().getYmlList().get(0);
            locations.put(getVraiColor(color), yml.parseStringToLoc(config_yml.getString(color)));
        }
    }

    private void setLocationsVillager(){
        for (String color : colorTeams) {
            YamlConfiguration config_yml = getYml().getYmlList().get(1);
            locationsVillagerShop.put(getVraiColor(color), yml.parseStringToLoc(config_yml.getString(color)));
        }
        for (String color : colorTeams) {
            YamlConfiguration config_yml = getYml().getYmlList().get(2);
            locationsVillagerUpgrade.put(getVraiColor(color), yml.parseStringToLoc(config_yml.getString(color)));
        }
    }

    private void setLocationsSpawnerCurrency(){
        for (String color : colorTeams) {
            YamlConfiguration config_yml = getYml().getYmlList().get(3);
            locationsSpawnerGenerateur.put(getVraiColor(color), yml.parseStringToLoc(config_yml.getString(color)));
        }
    }

    private void setLocationsGenerateurPublic(){
        try {
            for (String line : Files.readAllLines(getYml().getFilesList().get(getYml().getFilesList().size() - 1).toPath())) {
                if (!line.equalsIgnoreCase("")) {
                    locationsGenerateurPublic.add(yml.parseStringToLoc(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Color getVraiColor(String color) {
        switch (color) {
            case "Blanc":
                return Color.WHITE;
            case "Rose":
                return Color.FUCHSIA;
            case "Gris":
                return Color.GRAY;
            case "Rouge":
                return Color.RED;
            case "Bleu":
                return Color.BLUE;
            case "Vert":
                return Color.GREEN;
            case "Jaune":
                return Color.YELLOW;
            case "Cyan":
                return Color.AQUA;
        }
        return null;
    }

    @Override
    public void onDisable () {
        scoreboardManager.onDisable();
    }

    public static Main getINSTANCE() {
        return INSTANCE;
    }

    public HashMap<Color, Location> getLocations() {
        return locations;
    }


    public List<PlayerBedwars> getPlayerList() {
        return playerList;
    }
    public void removePlayerList(PlayerBedwars playerBedwars){
        playerList.remove(playerBedwars);
    }

    public List<ItemShop> getItemShopList() {
        return itemShopList;
    }

    public HashMap<Color, Location> getLocationsVillagerShop() {
        return locationsVillagerShop;
    }

    public HashMap<Color, Location> getLocationsVillagerUpgrade() {
        return locationsVillagerUpgrade;
    }

    public ArrayList<Location> getLocationsGenerateurPublic() {
        return locationsGenerateurPublic;
    }

    public HashMap<Color, Location> getLocationsSpawnerGenerateur() {
        return locationsSpawnerGenerateur;
    }

    public void setState(State state) {
        this.state = state;
    }
    public boolean isState(State state){
        return this.state == state;
    }

    public State getState() {
        return state;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
    public void setupdateAllPlayerScoreboard(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboardManager.update(player);
        }
    }

    public ScheduledExecutorService getExecutorMonoThread() {
        return executorMonoThread;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public String[] getColorTeam() {
        return colorTeams;
    }

    public List<Block> getBlocksPlaceByPlayer() {
        return blocksPlaceByPlayer;
    }

    public PlayerBedwars getPlayerWithPlayer(Player player){
        for (PlayerBedwars playerBedwars : playerList) {
            if (playerBedwars.getPlayer().getUniqueId().equals(player.getUniqueId())){
                return playerBedwars;
            }
        }
        return null;
    }

    public void checkWin() {
        if(playerList.size() == 1){
            Player winner = playerList.get(0).getPlayer();
            Bukkit.broadcastMessage(winner.getName()+" a gagné(e) le jeu !");
            FinishCycle finishCycle = new FinishCycle(this);
            finishCycle.runTaskTimer(this,0,20);
        }
    }
    public void setDied(Player player){
        SetDied setDied = new SetDied(this,player);
        setDied.runTaskTimer(this,0,20);
    }

    public int getTimer() {
        return timer;
    }

    public void addTimer(){
        timer++;
    }

    public YmlManager getYml() {
        return yml;
    }
}
