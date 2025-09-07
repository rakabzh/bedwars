package me.rakabzh;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class YmlManager {
    private ArrayList<File> filesList;
    private ArrayList<YamlConfiguration> ymlList;
    private String[] configs = {"player", "villager_shop", "villager_upgrade", "generateur_color", "generateur_public"};

    private Main main;

    public YmlManager(Main main) {
        this.main = main;
        filesList = new ArrayList<>();
        ymlList = new ArrayList<>();
    }

    public void loadAllYML() {
        if (!main.getDataFolder().exists()) {
            main.getDataFolder().mkdir();
        }

        File configFolder = new File(main.getDataFolder(), "config");
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        for (String configName : configs) {
            File ymlFile = new File(configFolder, configName + ".yml");
            if (!ymlFile.exists()) {
                try {
                    ymlFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            filesList.add(ymlFile);

            if (!configName.equalsIgnoreCase(configs[configs.length-1])) {
                ymlList.add(YamlConfiguration.loadConfiguration(ymlFile));
            } else {
                ymlList.add(null);
            }
        }
    }

    public ArrayList<YamlConfiguration> getYmlList() {
        return ymlList;
    }

    public ArrayList<File> getFilesList() {
        return filesList;
    }

    public String[] getConfigs() {
        return configs;
    }

    public Location parseStringToLoc(String value) {
        String normalized = value.replace(',', ' ').trim().replaceAll("\\s+", " ");
        String[] parts = normalized.split(" ");
        return new Location(main.getServer().getWorld("world"), Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Float.parseFloat(parts[3]), Float.parseFloat(parts[4]));
    }

    public static boolean isValidLocationString(String value) {
        if (value == null) return false;
        String normalized = value.replace(',', ' ').trim().replaceAll("\\s+", " ");
        String[] parts = normalized.split(" ");
        if (parts.length != 5) return false;

        try {
            for (int i = 0; i < 3; i++) {
                Double.parseDouble(parts[i]);
            }

            for (int i = 3; i < 5; i++) {
                String p = parts[i];
                if (p.endsWith("f") || p.endsWith("F")) {
                    p = p.substring(0, p.length() - 1);
                }
                Double.parseDouble(p);
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
