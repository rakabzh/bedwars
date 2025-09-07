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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryShop {
    private final Main main;
    private final ArrayList<ItemShop> listShop;
    Player player;
    int shopInventory;
    List<String> listNameMenuBar = Arrays.asList("Blocks", "Melee", "Armor", "Tools", "Ranged", "Potions", "Utility");

    public ItemStack getItem(Material material, String customName) {
        if (material == null) return null;
        ItemStack it = new ItemStack(material);
        ItemMeta itM = it.getItemMeta();
        itM.setDisplayName(customName);
        it.setItemMeta(itM);
        return it;
    }

    public InventoryShop(Main main, Player player) {
        this.main = main;
        this.player = player;
        this.listShop = new ArrayList<>();
        this.shopInventory = 1;
    }

    public void start(){
        blocksShop(Bukkit.createInventory(null, 54, "Buy"));
        main.getItemShopList().addAll(listShop);
        meleeShop(Bukkit.createInventory(null, 54, "Buy"));
        main.getItemShopList().addAll(listShop);
        armorShop(Bukkit.createInventory(null, 54, "Buy"));
        main.getItemShopList().addAll(listShop);
        toolsShop(Bukkit.createInventory(null, 54, "Buy"));
        main.getItemShopList().addAll(listShop);
        rangedShop(Bukkit.createInventory(null, 54, "Buy"));
        main.getItemShopList().addAll(listShop);
        potionsShop(Bukkit.createInventory(null, 54, "Buy"));
        main.getItemShopList().addAll(listShop);
        utilityShop(Bukkit.createInventory(null, 54, "Buy"));
        main.getItemShopList().addAll(listShop);
        this.listShop.clear();
    }

    public void menu_du_jeu() {
        Inventory inventoryBarMenu = Bukkit.createInventory(null, 54, "Buy");
        inventoryBarMenu.setItem(1, getItem(Material.HARD_CLAY, listNameMenuBar.get(0)));
        inventoryBarMenu.setItem(2, getItem(Material.GOLD_SWORD, listNameMenuBar.get(1)));
        inventoryBarMenu.setItem(3, getItem(Material.CHAINMAIL_BOOTS, listNameMenuBar.get(2)));
        inventoryBarMenu.setItem(4, getItem(Material.STONE_PICKAXE, listNameMenuBar.get(3)));
        inventoryBarMenu.setItem(5, getItem(Material.BOW, listNameMenuBar.get(4)));
        inventoryBarMenu.setItem(6, getItem(Material.BREWING_STAND_ITEM, listNameMenuBar.get(5)));
        inventoryBarMenu.setItem(7, getItem(Material.TNT, listNameMenuBar.get(6)));

        Inventory inventory;
        switch (shopInventory) {
            default:
                inventory = inventoryBarMenu;
                break;
            case 1:
                inventory = blocksShop(inventoryBarMenu);
                break;
            case 2:
                inventory = meleeShop(inventoryBarMenu);
                break;
            case 3:
                inventory = armorShop(inventoryBarMenu);
                break;
            case 4:
                inventory = toolsShop(inventoryBarMenu);
                break;
            case 5:
                inventory = rangedShop(inventoryBarMenu);
                break;
            case 6:
                inventory = potionsShop(inventoryBarMenu);
                break;
            case 7:
                inventory = utilityShop(inventoryBarMenu);
                break;
        }


        player.openInventory(inventory);
    }

    public void manageMenuShopInventory(ItemStack current) {
        if (current == null || current.getItemMeta() == null) return;
        if (current.getItemMeta().getDisplayName() == null) return;
        int i = -1;
        for (int j = 0; j < listShop.size(); j++) {
            if (listShop.get(j).getItem().getType().equals(current.getType())) {
                i = j;
                break;
            }
        }
        if (listNameMenuBar.contains(current.getItemMeta().getDisplayName())) {
            shopInventory = listNameMenuBar.indexOf(current.getItemMeta().getDisplayName()) + 1;
            menu_du_jeu();
            return;
        }
        if (i == -1) {
            return;
        }

        Material typeCurrency = listShop.get(i).getCurrency();
        int cout = listShop.get(i).getPrix();

        ArrayList<Integer> list = haveCurrency(player, typeCurrency, cout);
        if (!list.isEmpty()) {
            List<Material> swordList = Arrays.asList(Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD);
            List<Material> armorPieces = Arrays.asList(Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS);
            if (armorPieces.contains(current.getType())) {
                if (armorPieces.indexOf(current.getType()) >= main.getPlayerWithPlayer(player).getArmor()) {
                    main.getPlayerWithPlayer(player).setArmor(armorPieces.indexOf(current.getType()) + 1);
                    main.getPlayerWithPlayer(player).putArmor();
                    player.updateInventory();
                    return;
                } else {
                    player.sendMessage(Messages.SAME_ARMOR.getMessage());
                    return;
                }
            } else {
                List<Material> pickaxeList = Arrays.asList(Material.WOOD_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE);
                List<Material> axeList = Arrays.asList(Material.WOOD_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE);
                if (pickaxeList.contains(current.getType())) {
                    for (int slot = axeList.size() - 1; slot >= 0; slot--) {
                        if (player.getInventory().contains(pickaxeList.get(slot))) {
                            if (slot >= pickaxeList.indexOf(current.getType())) {
                                player.sendMessage(Messages.SAME_TOOL.getMessage());
                                return;
                            }
                        }
                    }
                    main.getPlayerWithPlayer(player).upgradeTools(0);
                } else if (axeList.contains(current.getType())) {
                    for (int slot = axeList.size() - 1; slot >= 0; slot--) {
                        if (player.getInventory().contains(axeList.get(slot))) {
                            if (slot >= axeList.indexOf(current.getType())) {
                                player.sendMessage(Messages.SAME_TOOL.getMessage());
                                return;
                            }
                        }
                    }
                    main.getPlayerWithPlayer(player).upgradeTools(1);
                } else if (current.getType().equals(Material.SHEARS)) {
                    if (player.getInventory().contains(Material.SHEARS)){
                        player.sendMessage(Messages.GET_SHEARS.getMessage());
                        return;
                    }
                    main.getPlayerWithPlayer(player).upgradeTools(2);
                } else if (swordList.contains(current.getType())) {
                    if (player.getInventory().getItem(0).getType().equals(Material.WOOD_SWORD) || swordList.indexOf(current.getType()) > swordList.indexOf(player.getInventory().getItem(0).getType())) {
                        main.getPlayerWithPlayer(player).putSword(current);
                        return;
                    } else {
                        player.sendMessage(Messages.SAME_SWORD.getMessage());
                        return;
                    }
                } else {
                    ItemMeta itemMeta = current.getItemMeta();
                    if (current.getType().equals(Material.POTION)){
                        List<String> lore = itemMeta.getLore();
                        lore.remove(0);
                        lore.remove(0);
                    }
                    else {
                        itemMeta.setLore(null);
                        itemMeta.setDisplayName(null);
                    }
                    current.setItemMeta(itemMeta);
                    player.getInventory().addItem(current);
                    player.updateInventory();
                }
                menu_du_jeu();
            }
            for (Integer slot : list) {
                ItemStack item = player.getInventory().getItem(slot);
                if (item.getAmount() > cout) {
                    item.setAmount(item.getAmount() - cout);
                    player.getInventory().setItem(slot, item);
                } else {
                    cout -= item.getAmount();
                    player.getInventory().setItem(slot, null);
                }
                player.updateInventory();
            }
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

    public List<String> getListNameMenuBar() {
        return listNameMenuBar;
    }

    public Inventory blocksShop(Inventory inventory) {

        ItemShop wool = new ItemShop(new ItemStack(Material.WOOL, 16, main.getPlayerWithPlayer(player).getDataColor()), 4, Material.IRON_INGOT, "Idéal pour relier les îles. Prend la couleur de votre équipe.");
        ItemShop hard_clay = new ItemShop(new ItemStack(Material.STAINED_CLAY, 16, main.getPlayerWithPlayer(player).getDataColor()), 12, Material.IRON_INGOT, "Bloc de base pour défendre votre lit.");
        ItemShop glass = new ItemShop(new ItemStack(Material.STAINED_GLASS, 4, main.getPlayerWithPlayer(player).getDataColor()), 12, Material.IRON_INGOT, "Immunisé contre les explosions.");
        ItemShop ender_stone = new ItemShop(new ItemStack(Material.ENDER_STONE, 12), 24, Material.IRON_INGOT, "Bloc solide pour défendre votre lit.");
        ItemShop ladder = new ItemShop(new ItemStack(Material.LADDER, 8), 4, Material.IRON_INGOT, "Utile pour sauver les chats coincés dans les arbres.");
        ItemShop wood = new ItemShop(new ItemStack(Material.WOOD, 16), 4, Material.GOLD_INGOT, "Bon bloc pour défendre votre lit. Résistant aux pioches.");
        ItemShop obsidian = new ItemShop(new ItemStack(Material.OBSIDIAN, 4), 4, Material.EMERALD, "Protection extrême pour votre lit.");


        inventory.setItem(19, wool.getItem());
        inventory.setItem(20, hard_clay.getItem());
        inventory.setItem(21, glass.getItem());
        inventory.setItem(22, ender_stone.getItem());
        inventory.setItem(23, ladder.getItem());
        inventory.setItem(24, wood.getItem());
        inventory.setItem(25, obsidian.getItem());

        listShop.clear();
        listShop.addAll(Arrays.asList(wool, hard_clay, glass, ender_stone, ladder, wood, obsidian));
        return inventory;
    }



    private Inventory meleeShop(Inventory inventory) {
        ItemShop stoneSword = new ItemShop(new ItemStack(Material.STONE_SWORD), 10, Material.IRON_INGOT, "");
        ItemShop ironSword = new ItemShop(new ItemStack(Material.IRON_SWORD), 7, Material.GOLD_INGOT, "");
        ItemShop diamondSword = new ItemShop(new ItemStack(Material.DIAMOND_SWORD), 4, Material.EMERALD, "");
        ItemShop stick = new ItemShop(new ItemStack(Material.STICK), 5, Material.GOLD_INGOT, "");
        ItemStack stickItem = stick.getItem();
        stickItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        inventory.setItem(19, stoneSword.getItem());
        inventory.setItem(20, ironSword.getItem());
        inventory.setItem(21, diamondSword.getItem());
        inventory.setItem(22, stickItem);

        listShop.clear();
        listShop.addAll(Arrays.asList(stoneSword, ironSword, diamondSword, stick));
        return inventory;
    }

    private Inventory armorShop(Inventory inventory) {
        ItemShop chainmailArmor = new ItemShop(new ItemStack(Material.CHAINMAIL_BOOTS), 30, Material.IRON_INGOT, "Des jambières et bottes en cotte de mailles avec lesquels vous apparaîtrez toujours.");
        ItemShop ironArmor = new ItemShop(new ItemStack(Material.IRON_BOOTS), 12, Material.GOLD_INGOT, "Des jambières et bottes en fer avec lesquels vous apparaîtrez toujours.");
        ItemShop diamondArmor = new ItemShop(new ItemStack(Material.DIAMOND_BOOTS), 6, Material.EMERALD, "Des jambières et bottes en diamant avec lesquels vous apparaîtrez toujours.");

        inventory.setItem(19, chainmailArmor.getItem());
        inventory.setItem(20, ironArmor.getItem());
        inventory.setItem(21, diamondArmor.getItem());

        listShop.clear();
        listShop.addAll(Arrays.asList(chainmailArmor, ironArmor, diamondArmor));
        return inventory;
    }

    private Inventory toolsShop(Inventory inventory) {
        ItemShop shears = new ItemShop(new ItemStack(Material.SHEARS), 20, Material.IRON_INGOT, "Idéal pour se débarrasser de la laine. Ces ciseaux vous permettront toujours de frayer.");
        ItemShop pickaxeTier1 = new ItemShop(new ItemStack(Material.WOOD_PICKAXE), 10, Material.IRON_INGOT, "Cet objet peut être amélioré. Il perdra 1 niveau en cas de mort ! Vous réapparaîtrez en permanence avec au moins le niveau le plus bas.");
        ItemShop pickaxeTier2 = new ItemShop(new ItemStack(Material.IRON_PICKAXE), 10, Material.IRON_INGOT, "Cet objet peut être amélioré. Il perdra 1 niveau en cas de mort ! Vous réapparaîtrez en permanence avec au moins le niveau le plus bas.");
        ItemShop pickaxeTier3 = new ItemShop(new ItemStack(Material.GOLD_PICKAXE), 3, Material.GOLD_INGOT, "Cet objet peut être amélioré. Il perdra 1 niveau en cas de mort ! Vous réapparaîtrez en permanence avec au moins le niveau le plus bas.");
        ItemShop pickaxeTier4 = new ItemShop(new ItemStack(Material.DIAMOND_PICKAXE), 6, Material.GOLD_INGOT, "Cet objet peut être amélioré. Il perdra 1 niveau en cas de mort ! Vous réapparaîtrez en permanence avec au moins le niveau le plus bas.");
        ItemShop axeTier1 = new ItemShop(new ItemStack(Material.WOOD_AXE), 10, Material.IRON_INGOT, "Cet objet peut être amélioré. Il perdra 1 niveau en cas de mort ! Vous réapparaîtrez en permanence avec au moins le niveau le plus bas.");
        ItemShop axeTier2 = new ItemShop(new ItemStack(Material.IRON_AXE), 10, Material.IRON_INGOT, "Cet objet peut être amélioré. Il perdra 1 niveau en cas de mort ! Vous réapparaîtrez en permanence avec au moins le niveau le plus bas.");
        ItemShop axeTier3 = new ItemShop(new ItemStack(Material.GOLD_AXE), 3, Material.GOLD_INGOT, "Cet objet peut être amélioré. Il perdra 1 niveau en cas de mort ! Vous réapparaîtrez en permanence avec au moins le niveau le plus bas.");
        ItemShop axeTier4 = new ItemShop(new ItemStack(Material.DIAMOND_AXE), 6, Material.GOLD_INGOT, "Cet objet peut être amélioré. Il perdra 1 niveau en cas de mort ! Vous réapparaîtrez en permanence avec au moins le niveau le plus bas.");

        ItemShop[] pickaxeList = new ItemShop[]{pickaxeTier1, pickaxeTier2, pickaxeTier3, pickaxeTier4};
        ItemShop[] axeList = new ItemShop[]{axeTier1, axeTier2, axeTier3, axeTier4};

        inventory.setItem(19, shears.getItem());
        inventory.setItem(20, pickaxeList[main.getPlayerWithPlayer(player).getTools()[0]].getItem());
        inventory.setItem(21, axeList[main.getPlayerWithPlayer(player).getTools()[1]].getItem());

        listShop.clear();
        listShop.addAll(Arrays.asList(shears, pickaxeTier1, pickaxeTier2, pickaxeTier3, pickaxeTier4, axeTier1, axeTier2, axeTier3, axeTier4));
        return inventory;
    }

    private Inventory rangedShop(Inventory inventory) {
        ItemShop arrow = new ItemShop(new ItemStack(Material.ARROW, 6), 2, Material.GOLD_INGOT, "");
        ItemShop bow = new ItemShop(new ItemStack(Material.BOW), 12, Material.GOLD_INGOT, "");
        ItemShop bowPower = new ItemShop(new ItemStack(Material.BOW), 20, Material.GOLD_INGOT, "");
        ItemStack bowPowerItem = bowPower.getItem();
        bowPowerItem.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
        ItemShop bowPowerPunch = new ItemShop(new ItemStack(Material.BOW), 6, Material.EMERALD, "");
        ItemStack bowPowerPunchItem = bowPowerPunch.getItem();
        bowPowerPunchItem.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
        bowPowerPunchItem.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);

        inventory.setItem(19, arrow.getItem());
        inventory.setItem(20, bow.getItem());
        inventory.setItem(21, bowPowerItem);
        inventory.setItem(22, bowPowerPunchItem);

        listShop.clear();
        listShop.addAll(Arrays.asList(arrow, bow, bowPower, bowPowerPunch));
        return inventory;
    }

    private Inventory potionsShop(Inventory inventory) {
        ItemShop speed = new ItemShop(new ItemStack(Material.POTION), 1, Material.EMERALD, "§9Speed II (0:45).");
        PotionMeta speedMeta = (PotionMeta) speed.getItem().getItemMeta();
        speedMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 45*20, 1), true);
        ItemStack speedItem = speed.getItem();
        speedItem.setItemMeta(speedMeta);

        ItemShop jump = new ItemShop(new ItemStack(Material.POTION), 1, Material.EMERALD, "§9Jump Boost V (0:45).");
        PotionMeta jumpMeta = (PotionMeta) jump.getItem().getItemMeta();
        jumpMeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 45*20, 4), true);
        ItemStack jumpItem = jump.getItem();
        jumpItem.setItemMeta(jumpMeta);

        ItemShop invisibility = new ItemShop(new ItemStack(Material.POTION), 2, Material.EMERALD, "§9Complete Invisibility (0:30).");
        PotionMeta invisibilityMeta = (PotionMeta) invisibility.getItem().getItemMeta();
        invisibilityMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30*20, 5), true);
        ItemStack invisibilityItem = invisibility.getItem();
        invisibilityItem.setItemMeta(invisibilityMeta);

        inventory.setItem(19, speedItem);
        inventory.setItem(20, jumpItem);
        inventory.setItem(21, invisibilityItem);

        listShop.clear();
        listShop.addAll(Arrays.asList(speed, jump, invisibility));
        return inventory;
    }

    public Inventory utilityShop(Inventory inventory) {

        ItemShop goldenApple = new ItemShop(new ItemStack(Material.GOLDEN_APPLE), 3, Material.GOLD_INGOT, "Guérison complète.");
        ItemShop bedBug = new ItemShop(new ItemStack(Material.SNOW_BALL), 30, Material.IRON_INGOT, "Fait apparaître des silverfish à l'endroit où la boule de neige atterrit pour distraire vos ennemis. Dure 15 secondes.");
        ItemShop golem = new ItemShop(new ItemStack(Material.MONSTER_EGG, 1, (short) 99), 120, Material.IRON_INGOT, "Golem de fer pour aider à défendre votre base. Durée de vie de 4 minutes.");
        ItemShop fireBall = new ItemShop(new ItemStack(Material.FIREBALL, 1), 40, Material.IRON_INGOT, "Cliquez avec le bouton droit de la souris pour lancer l'opération ! Idéal pour repousser les ennemis qui marchent sur de minces ponts.");
        ItemShop tnt = new ItemShop(new ItemStack(Material.TNT), 4, Material.GOLD_INGOT, "Il s'enflamme instantanément, ce qui permet de faire exploser les choses !");
        ItemShop ender_pearl = new ItemShop(new ItemStack(Material.ENDER_PEARL), 4, Material.EMERALD, "Le moyen le plus rapide d'envahir les bases ennemies.");
        ItemShop water = new ItemShop(new ItemStack(Material.WATER_BUCKET), 3, Material.GOLD_INGOT, "Excellent pour ralentir les ennemis qui s'approchent. Peut également protéger contre les TNT.");
        ItemShop eggBridge = new ItemShop(new ItemStack(Material.EGG), 1, Material.EMERALD, "Cet oeuf crée un pont dans son parcours après avoir été lancé.");
        ItemShop milk = new ItemShop(new ItemStack(Material.MILK_BUCKET), 4, Material.GOLD_INGOT, "Évitez de déclencher des pièges pendant les 30 secondes qui suivent la consommation.");
        ItemShop sponge = new ItemShop(new ItemStack(Material.SPONGE), 3, Material.GOLD_INGOT, "Idéal pour absorber l'eau.");
        ItemShop chestDefense = new ItemShop(new ItemStack(Material.CHEST), 24, Material.IRON_INGOT, "Placez une défense qui apparaisse !");

        inventory.setItem(19, goldenApple.getItem());
        inventory.setItem(20, bedBug.getItem());
        inventory.setItem(21, golem.getItem());
        inventory.setItem(22, fireBall.getItem());
        inventory.setItem(23, tnt.getItem());
        inventory.setItem(24, ender_pearl.getItem());
        inventory.setItem(25, water.getItem());
        inventory.setItem(28, eggBridge.getItem());
        inventory.setItem(29, milk.getItem());
        inventory.setItem(30, sponge.getItem());
        inventory.setItem(31, chestDefense.getItem());

        listShop.clear();
        listShop.addAll(Arrays.asList(goldenApple, bedBug, golem, fireBall, tnt, ender_pearl, water, eggBridge, milk, sponge, chestDefense));
        return inventory;
    }
}