package me.rakabzh.commands;

import me.rakabzh.Main;
import me.rakabzh.PlayerBedwars;
import me.rakabzh.YmlManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;

public class CommandYML implements CommandExecutor {

    private Main main;

    public CommandYML(Main main) {
        this.main = main;
    }

    public String fileToString(File file) throws Exception {
        return Files.readAllLines(file.toPath())
                .stream()
                .collect(Collectors.joining("\n"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (!label.equalsIgnoreCase("config")) return false;
        if (args.length == 0){
            sender.sendMessage("§cTapez /config read <nom du dossier> §epour voir le contenu du fichier\n" +
                    "§c/config check §epour vérifier si tous les fichiers sont correctement remplie\n" +
                    "§c/config list §epour voir la list des fichiers\n" +
                    "§c/config create <nom du fichier> <color> <x> <y> <z> <yaw> <pitch> §epour ajouter des coordonnées en fonction de la couleur");
            return false;
        }

        if (args[0].equalsIgnoreCase("read")) {
            if (args.length != 2) {
                sender.sendMessage("§eTapez /config read <nom du dossier>");
                return false;
            }
            String asked = args[1];
            File target = null;
            for (File f : main.getYml().getFilesList()) {
                String base = f.getName();
                if (base.toLowerCase().endsWith(".yml")) {
                    base = base.substring(0, base.length() - 4);
                }
                if (base.equalsIgnoreCase(asked)) {
                    target = f;
                    break;
                }
            }


            if (target == null || !target.exists()) {
                sender.sendMessage("§cFichier introuvable : §f" + asked);
                return false;
            }

            try {
                sender.sendMessage("§6=== " + target.getName() + " ===");
                sender.sendMessage("§7" + fileToString(target));
            } catch (Exception e) {
                sender.sendMessage("§cErreur système : " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("list")){
            sender.sendMessage("§bList des fichiers:");
            for (File file : main.getYml().getFilesList()) {
                sender.sendMessage("§6" + file.getName());
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("check")) {
            return runCheck(sender);
        }
        if (!args[0].equalsIgnoreCase("create")){
            sender.sendMessage("§cSuivez les instruction de la commande /config check");
            return false;
        }
        if (args.length != 8){
            sender.sendMessage("§c/config create <nom du fichier> <color> <x> <y> <z> <yaw> <pitch>");
            return false;
        }

        int index = -1;

        for (int i = 0; i < main.getYml().getFilesList().size(); i++) {
            String base = main.getYml().getFilesList().get(i).getName();
            if (base.toLowerCase().endsWith(".yml")) {
                base = base.substring(0, base.length() - 4); // enlève ".yml"
            }
            if (base.equalsIgnoreCase(args[1])) {
                index = i;
                break;
            }
        }

        if (index == -1){
            sender.sendMessage("§eIl n'y a pas de fichier avec le nom §f" + args[1] +
                    "§eVeuillez tapez /config list pour connaitre la list des fichiers");
            return false;
        }

        if (PlayerBedwars.getPrefixColor(args[2]) == null){
            sender.sendMessage("§eLa couleur " + args[2] + " n'est pas une couleur d'un joueur");
            return false;
        }

        main.getYml().getYmlList().get(index).set(args[2], args[3]+" "+args[4]+" "+args[5]+" "+args[6]+" "+args[7]);
        try {
            main.getYml().getYmlList().get(index).save(main.getYml().getFilesList().get(index));
            sender.sendMessage("§aLa localisation du " + PlayerBedwars.getPrefixColor(args[2]) + args[2] + " §aa bien été écrit dans le fichier §f" + args[1]);
        } catch (IOException e) {
            sender.sendMessage("§cErreur lors de la sauvegarde du fichier !");
            throw new RuntimeException(e);
        }

        return true;
    }

    public boolean runCheck(CommandSender sender){
        for (int i = 0; i < main.getYml().getFilesList().size()-1; i++) {
            File file_player = main.getYml().getFilesList().get(i);
            YamlConfiguration config_yml = main.getYml().getYmlList().get(i);

            sender.sendMessage("§6=== " + file_player.getName() + " ===");

            for (String color : main.getColorTeam()) {
                Set<String> keys = config_yml.getKeys(false);

                String foundKey = keys.stream()
                        .filter(k -> k.equalsIgnoreCase(color))
                        .findFirst()
                        .orElse(null);

                if (foundKey != null) {
                    String value = config_yml.getString(foundKey);
                    if (YmlManager.isValidLocationString(value)) {
                        sender.sendMessage("§2" + color + " V");
                    } else {
                        sender.sendMessage(PlayerBedwars.getPrefixColor(color) + color + " §clocalisation mal écrite \nIl faut refaire");
                        return false;
                    }
                } else {
                    sender.sendMessage("§eIl manque la localisation du spawn de la couleur " + PlayerBedwars.getPrefixColor(color) + color);
                    return false;
                }
            }
        }
        try {
            for (String line : Files.readAllLines(main.getYml().getFilesList().get(main.getYml().getFilesList().size() - 1).toPath())) {
                if (!line.equalsIgnoreCase("") && !YmlManager.isValidLocationString(line)){
                    sender.sendMessage("§clocalisation mal écrite dans le fichier");
                    return false;
                }
            }
            sender.sendMessage("§aFichier correct V");
        } catch (IOException e) {
            sender.sendMessage("§eErreur lecture du fichier §f" + main.getYml().getFilesList().get(main.getYml().getFilesList().size()-1).getName());
            throw new RuntimeException(e);
        }

        return true;
    }
}
