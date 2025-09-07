package me.rakabzh.villagers;

import me.rakabzh.Main;
import me.rakabzh.constants.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryUpgrade {
    private final Main main;
    private final ArrayList<ItemShop> listUpgrade;
    Player player;
    Inventory inventory;
    String[] prefix;
    ArrayList<ItemStack> trapList;

    public InventoryUpgrade(Main main, Player player) {
        this.main = main;
        this.player = player;
        this.listUpgrade = new ArrayList<>();
        this.inventory = Bukkit.createInventory(null, 54, "Upgrades and Traps");
        this.prefix = new String[]{"§7","§7","§7","§7","§7","§7","§7","§7","§7","§7"};
        this.trapList = new ArrayList<>();
    }

    public void menu_du_jeu() {
        inventory = Bukkit.createInventory(null, 54, "Upgrades and Traps");
        int cp = 1;
        if (main.getPlayerWithPlayer(player).getProtection() == 4){
            cp = 0;
        }

        List<String> protectionDescription = new ArrayList<>();
        protectionDescription.add(prefix[0] + "Tier 1: Protection I, §b2 Diamonds");
        protectionDescription.add(prefix[1] + "Tier 2: Protection II, §b4 Diamonds");
        protectionDescription.add(prefix[2] + "Tier 3: Protection III, §b8 Diamonds");
        protectionDescription.add(prefix[3] + "Tier 4: Protection IV, §b16 Diamonds");
        List<String> minerDescription = new ArrayList<>();
        minerDescription.add(prefix[4] + "Tier 1: Haste I, §b2 Diamonds");
        minerDescription.add(prefix[5] + "Tier 2: Haste II, §b4 Diamonds");
        List<String> forgeDescription = new ArrayList<>();
        forgeDescription.add(prefix[6] + "Tier 1: +50% Resources, §b2 Diamonds");
        forgeDescription.add(prefix[7] + "Tier 2: +100% Resources, §b4 Diamonds");
        forgeDescription.add(prefix[8] + "Tier 3: Spawn emeralds, §b6 Diamonds");
        forgeDescription.add(prefix[9] + "Tier 4: +200% Resources&7, §b8 Diamonds");

        ItemShop sharpened = new ItemShop(new ItemStack(Material.IRON_SWORD), 4, Material.DIAMOND, "Tranchant I sur toutes les épées !", "Sharpened Swords");
        ItemShop protection = new ItemShop(new ItemStack(Material.IRON_CHESTPLATE), (int) Math.pow(2,main.getPlayerWithPlayer(player).getProtection()+1), Material.DIAMOND, "Protection sur toutes les pièces d'armure !" ,"Armure renforcée", false, protectionDescription);
        ItemShop miner = new ItemShop(new ItemStack(Material.GOLD_PICKAXE), (int) Math.pow(2,main.getPlayerWithPlayer(player).getHaste()+1), Material.DIAMOND, "Tous les joueurs de votre équipe gagnent en permanence de la Haste.","Mineur fou", false, minerDescription);
        ItemShop forge = new ItemShop(new ItemStack(Material.FURNACE), 2*main.getPlayerWithPlayer(player).getForge(), Material.DIAMOND, "Améliorez les ressources qui apparaissent sur votre île.","Forge", false, forgeDescription);
        ItemShop heal = new ItemShop(new ItemStack(Material.BEACON), 1, Material.DIAMOND, "Crée un champ de régénération autour de votre base !", "Champs de guérison");
        ItemShop dragon = new ItemShop(new ItemStack(Material.DRAGON_EGG), 5, Material.DIAMOND, "Votre équipe aura 2 dragons au lieu d'un pendant le match de la mort !", "Dragon");

        ItemShop baseTrap = new ItemShop(new ItemStack(Material.TRIPWIRE_HOOK), (int) Math.pow(2,main.getPlayerWithPlayer(player).getUpgrade().size()), Material.DIAMOND, "Ralenti et aveugle le joueur pendant 8s.", "C'est un piège");
        ItemShop defenseTrap = new ItemShop(new ItemStack(Material.FEATHER),(int) Math.pow(2,main.getPlayerWithPlayer(player).getUpgrade().size()), Material.DIAMOND, "Accorde Vitesse II et Saut II pendant 15 secondes aux joueurs alliés proches de votre base.", "Piège de contre-offensive");
        ItemShop alarmTrap = new ItemShop(new ItemStack(Material.REDSTONE_TORCH_ON),(int) Math.pow(2,main.getPlayerWithPlayer(player).getUpgrade().size()), Material.DIAMOND, "Révèle les joueurs invisibles ainsi que leur nom et leur équipe.", "piège d'alarme");
        ItemShop fatigueTrap = new ItemShop(new ItemStack(Material.IRON_PICKAXE),(int) Math.pow(2,main.getPlayerWithPlayer(player).getUpgrade().size()), Material.DIAMOND, "Inflige de la Mining Fatigue pendant 10 7secondes.", "Piège de fatigue pour les mineurs");



        trapList.add(new ItemStack(Material.GLASS));
        trapList.add(new ItemStack(Material.GLASS));
        trapList.add(new ItemStack(Material.GLASS));
        for (int i = 0; i < main.getPlayerWithPlayer(player).getUpgrade().size(); i++) {
            switch (main.getPlayerWithPlayer(player).getUpgrade().get(i)) {
                case "base":
                    trapList.set(i, baseTrap.getItem());
                    break;
                case "mining":
                    trapList.set(i, fatigueTrap.getItem());
                    break;
                case "defense":
                    trapList.set(i, defenseTrap.getItem());
                    break;
                case "alarm":
                    trapList.set(i, alarmTrap.getItem());
                    break;
            }
        }

        inventory.setItem(10, sharpened.getItem());
        inventory.setItem(11, protection.getItem());
        inventory.setItem(12, miner.getItem());
        inventory.setItem(14, baseTrap.getItem());
        inventory.setItem(15, defenseTrap.getItem());
        inventory.setItem(16, alarmTrap.getItem());
        inventory.setItem(19, forge.getItem());
        inventory.setItem(20, heal.getItem());
        inventory.setItem(21, dragon.getItem());
        inventory.setItem(23, fatigueTrap.getItem());
        inventory.setItem(39, trapList.get(0));
        inventory.setItem(40, trapList.get(1));
        inventory.setItem(41, trapList.get(2));

        listUpgrade.clear();
        listUpgrade.addAll(Arrays.asList(sharpened, protection, miner, forge, heal, dragon, baseTrap, defenseTrap, alarmTrap, fatigueTrap));

        player.openInventory(inventory);
    }

    public void manageMenuShopInventory(ItemStack current) {
        if (current == null || current.getItemMeta() == null) return;
        if (current.getItemMeta().getDisplayName() == null) return;
        int i = -1;
        for (int j = 0; j < listUpgrade.size(); j++) {
            if (listUpgrade.get(j).getItem().getType().equals(current.getType())) {
                i = j;
                break;
            }
        }
        if (i == -1) {
            return;
        }

        Material typeCurrency = listUpgrade.get(i).getCurrency();
        int cout = listUpgrade.get(i).getPrix();

        ArrayList<Integer> list = haveCurrency(player, typeCurrency, cout);
        if (!list.isEmpty()) {


            if (current.getType().equals(Material.IRON_SWORD)) {
                if (!main.getPlayerWithPlayer(player).isSharpened()) {
                    main.getPlayerWithPlayer(player).putSharpened();
                    addGlow(i);
                }
                else {
                    player.sendMessage(Messages.GET_SHARPNESS.getMessage());
                    return;
                }
            }
            else if (current.getType().equals(Material.IRON_CHESTPLATE)) {
                if (main.getPlayerWithPlayer(player).getProtection() < 4) {
                    prefix[main.getPlayerWithPlayer(player).getProtection()] = "§a";
                    main.getPlayerWithPlayer(player).addProtection();
                    if (main.getPlayerWithPlayer(player).getProtection() == 5) {
                        addGlow(i);
                    }
                }
                else {
                    player.sendMessage(Messages.MAXIMAL_PROTECTION.getMessage());
                    return;
                }
            }
            else if (current.getType().equals(Material.GOLD_PICKAXE)) {
                if (main.getPlayerWithPlayer(player).getHaste() <= 1) {
                    prefix[main.getPlayerWithPlayer(player).getHaste()+4] = "§a";
                    main.getPlayerWithPlayer(player).addHaste();
                    if (main.getPlayerWithPlayer(player).getHaste() == 2) {
                        addGlow(i);
                    }
                    player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(99999,main.getPlayerWithPlayer(player).getHaste()));
                }
                else {
                    player.sendMessage(Messages.MAXIMAL_HASTE.getMessage());
                    return;
                }
            }
            else if (current.getType().equals(Material.FURNACE)) {
                if (main.getPlayerWithPlayer(player).getForge() <= 3) {
                    prefix[main.getPlayerWithPlayer(player).getForge()+6] = "§a";
                    main.getPlayerWithPlayer(player).addForge();
                    if (main.getPlayerWithPlayer(player).getForge() == 4) {
                        addGlow(i);
                    }
                }
                else {
                    player.sendMessage(Messages.MAXIMAL_FORGE.getMessage());
                    return;
                }
            }
            else if (current.getType().equals(Material.BEACON)) {
                if (!main.getPlayerWithPlayer(player).isHeal()) {
                    main.getPlayerWithPlayer(player).putHeal();
                    addGlow(i);
                }
                else {
                    player.sendMessage(Messages.GET_REGENERATION.getMessage());
                    return;
                }
            }
            else if (current.getType().equals(Material.DRAGON_EGG)) {
                if (!main.getPlayerWithPlayer(player).isDragon()) {
                    main.getPlayerWithPlayer(player).putDragon();
                    addGlow(i);
                }
                else {
                    player.sendMessage(Messages.GET_BUY.getMessage());
                    return;
                }
            }
            else if (main.getPlayerWithPlayer(player).getUpgrade().size() < 3) {
                if (current.getType().equals(Material.TRIPWIRE_HOOK)) {
                    if (!main.getPlayerWithPlayer(player).isBaseTrap()) {
                        main.getPlayerWithPlayer(player).setBaseTrap(true);
                        addGlow(i);
                    }
                    else {
                        player.sendMessage(Messages.GET_TRAP.getMessage());
                        return;
                    }
                }
                else if (current.getType().equals(Material.FEATHER)) {
                    if (!main.getPlayerWithPlayer(player).isDefenseTrap()) {
                        main.getPlayerWithPlayer(player).setDefenseTrap(true);
                        addGlow(i);
                    }
                    else {
                        player.sendMessage(Messages.GET_TRAP.getMessage());
                        return;
                    }
                }
                else if (current.getType().equals(Material.REDSTONE_TORCH_ON)) {
                    if (!main.getPlayerWithPlayer(player).isAlarmTrap()) {
                        main.getPlayerWithPlayer(player).setAlarmTrap(true);
                        addGlow(i);
                    }
                    else {
                        player.sendMessage(Messages.GET_ALARM.getMessage());
                        return;
                    }
                }
                else if (current.getType().equals(Material.IRON_PICKAXE)) {
                    if (!main.getPlayerWithPlayer(player).isMiningTrap()) {
                        main.getPlayerWithPlayer(player).setMiningTrap(true);
                        addGlow(i);
                    }
                    else {
                        player.sendMessage(Messages.GET_TRAP.getMessage());
                        return;
                    }
                }
                inventory.setItem(38+main.getPlayerWithPlayer(player).getUpgrade().size(), current);
            }
            else {
                System.out.println(main.getPlayerWithPlayer(player).getUpgrade());
                player.sendMessage(Messages.MAXIMAL_TRAP.getMessage());
                return;
            }

            for (Integer slot : list) {

                menu_du_jeu();

                ItemStack item = player.getInventory().getItem(slot);
                if (item.getAmount() > cout) {
                    item.setAmount(item.getAmount() - cout);
                    player.getInventory().setItem(slot, item);
                } else {
                    cout -= item.getAmount();
                    player.getInventory().setItem(slot, null);
                }
            }
            player.updateInventory();
            menu_du_jeu();
        } else {
            player.sendMessage(Messages.CANT_PAY.getMessage());
        }

    }

    private ArrayList<Integer> haveCurrency(Player player, Material material, int nbMaterial) {
        ItemStack[] contents = player.getInventory().getContents();
        ArrayList<Integer> slots = new ArrayList<>();
        int total = 0;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == material) {
                slots.add(i);
                total += item.getAmount();
                if (total >= nbMaterial) break;
            }
        }

        if (total >= nbMaterial) {
            return slots;
        } else {
            return new ArrayList<>();
        }
    }

    private void addGlow(int i){
        ItemMeta meta = listUpgrade.get(i).getItem().getItemMeta();
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        listUpgrade.get(i).getItem().setItemMeta(meta);
    }

    public ArrayList<ItemStack> getTrapList() {
        return trapList;
    }
}