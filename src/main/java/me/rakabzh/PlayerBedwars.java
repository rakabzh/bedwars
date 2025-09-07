package me.rakabzh;

import me.rakabzh.constants.Messages;
import me.rakabzh.generateur.Generator;
import me.rakabzh.villagers.InventoryShop;
import me.rakabzh.villagers.InventoryUpgrade;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerBedwars {

    Main main;
    Player player;
    String color;
    int armor;
    int[] tools;
    int state;
    boolean bed;
    byte dataColor;
    InventoryShop inventoryShop;
    InventoryUpgrade inventoryUpgrade;
    Location locationSpawn;
    Location locationBedHead;
    Location locationBedFoot;
    Location locationGenerateur;
    Generator generator;

    boolean sharpened;
    boolean heal;
    boolean dragon;
    boolean baseTrap;
    boolean miningTrap;
    boolean defenseTrap;
    boolean alarmTrap;
    int protection;
    int haste;
    int forge;
    ArrayList<String> upgrade;

    public PlayerBedwars(Player player, String color, Location locationGenerateur, Main main) {
        this.main = main;
        this.player = player;
        this.color = color;
        this.dataColor = 0;
        this.locationGenerateur = locationGenerateur;
        this.locationSpawn = main.getLocations().get(main.getVraiColor(color));
        this.bed = true;
        this.armor = 0;
        this.tools = new int[]{0, 0, 0};
        this.state = 0;
        this.locationBedFoot = new Location(player.getWorld(), locationSpawn.getX(),66,locationSpawn.getZ());
        this.locationBedHead = new Location(player.getWorld(), locationSpawn.getX(),66,locationSpawn.getZ());
        putLocationBed();
        this.inventoryShop = new InventoryShop(this.main, this.player);
        this.inventoryUpgrade = new InventoryUpgrade(this.main, this.player);
        getVraiColor();

        this.sharpened = false;
        this.heal = false;
        this.dragon = false;
        this.baseTrap = false;
        this.miningTrap = false;
        this.defenseTrap = false;
        this.alarmTrap = false;
        this.protection = 0;
        this.haste = 0;
        this.forge = 0;
        this.upgrade = new ArrayList<>();
    }

    public PlayerBedwars isInBasePlayer(Location location){
        for (PlayerBedwars pB : main.getPlayerList()) {
            Location pL = pB.getLocationBedFoot();
            if ((pL.getX() - 20 <= location.getX() && location.getX() <= pL.getX() + 20) &&
                    (pL.getY() - 20 <= location.getY() && location.getY() <= pL.getY() + 20)) {
                return pB;
            }
        }
        return null;
    }

    public void putAllPermenant(){
        putArmor();
        putTools();
    }

    public void putTools(){
        List<Material> listpickaxe = Arrays.asList(Material.WOOD_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE);
        List<Material> listaxe = Arrays.asList(Material.WOOD_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE);
        int slotP = 0;
        int slotA = 0;
        int slotS = 0;
        for (int i = 1; i < player.getInventory().getSize(); i++) {
            if (player.getInventory().getItem(i) == null){
                if (slotP == 0) {
                    slotP = i;
                }
                if (slotA == 0 && slotP != i){
                    slotA = i;
                }
                if (slotS == 0 && slotA != i && slotP != i){
                    slotS = i;
                }
            }
            else if (listpickaxe.contains(player.getInventory().getItem(i).getType())){
                slotP = i;
            }
            else if (listaxe.contains(player.getInventory().getItem(i).getType())){
                slotA = i;
            }
            else if (player.getInventory().getItem(i).getType().equals(Material.SHEARS)){
                slotS = i;
            }
        }
        if (slotA == 0 || slotP == 0 || slotS == 0){
            player.sendMessage(Messages.NO_PLACE_INVENTORY.getMessage());
            return;
        }
        if (tools[0] > 0){
            player.getInventory().setItem(slotP, new ItemStack(listpickaxe.get(tools[0]-1)));
        }
        if (tools[1] > 0){
            player.getInventory().setItem(slotA, new ItemStack(listaxe.get(tools[1]-1)));
        }
        if (tools[2] == 1){
            player.getInventory().setItem(slotS, new ItemStack(Material.SHEARS));
        }
    }

    public void putArmor() {
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setHelmet(null);
        ItemStack item = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmet = (LeatherArmorMeta) item.getItemMeta();
        helmet.setColor(getVraiColor());
        if (protection > 0) {
            helmet.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, protection, true);
        }
        item.setItemMeta(helmet);
        player.getInventory().setHelmet(item);

        item = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplate = (LeatherArmorMeta) item.getItemMeta();
        chestplate.setColor(getVraiColor());
        if (protection > 0) {
            chestplate.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, protection, true);
        }
        item.setItemMeta(chestplate);
        player.getInventory().setChestplate(item);

        if (armor == 0){
            item = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta leggings = (LeatherArmorMeta) item.getItemMeta();
            leggings.setColor(getVraiColor());
            if (protection > 0) {
                leggings.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, protection, true);
            }
            item.setItemMeta(leggings);
            player.getInventory().setLeggings(item);

            item = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta boots = (LeatherArmorMeta) item.getItemMeta();
            boots.setColor(getVraiColor());
            if (protection > 0) {
                boots.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, protection, true);
            }
            item.setItemMeta(boots);
            player.getInventory().setBoots(item);
        }
        if (armor == 1){
            ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
            ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
            if (protection > 0){
                leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
                boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
            }
            player.getInventory().setLeggings(leggings);
            player.getInventory().setBoots(boots);
        }
        if (armor == 2){
            ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
            ItemStack boots = new ItemStack(Material.IRON_BOOTS);
            if (protection > 0){
                leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
                boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
            }
            player.getInventory().setLeggings(leggings);
            player.getInventory().setBoots(boots);
        }
        if (armor == 3){
            ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
            ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
            if (protection > 0){
                leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
                boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
            }
            player.getInventory().setLeggings(leggings);
            player.getInventory().setBoots(boots);
        }
    }

    public void putSword(ItemStack current){
        ItemStack sword = new ItemStack(current.getType());
        if (isSharpened()){
            sword.addEnchantment(Enchantment.DAMAGE_ALL,1);
        }
        player.getInventory().setItem(0,sword);
        player.updateInventory();
    }

    public void putBed() {
        int direction = 0;

        switch ((int) locationBedFoot.getYaw()){
            case 180:
                direction = 0;//north
                break;
            case -90:
                direction = 1;//west
                break;
            case 0:
                direction = 2;//south
                break;
            case 90:
                direction = 3;//east
                break;
        }

        if (!locationBedFoot.clone().subtract(0, 1, 0).getBlock().getType().isSolid()
                || !locationBedHead.clone().subtract(0, 1, 0).getBlock().getType().isSolid()) {
            System.out.println("Impossible de poser le lit : pas de support solide !");
            return;
        }

        Block foot = player.getWorld().getBlockAt(locationBedFoot);
        Block head = player.getWorld().getBlockAt(locationBedHead);

        foot.setType(Material.BED_BLOCK);
        foot.setData((byte) direction);

        head.setType(Material.BED_BLOCK);
        head.setData((byte) (direction + 8));
    }


    private void putLocationBed(){
        locationBedFoot.setYaw(locationSpawn.getYaw());
        locationBedHead.setYaw(locationSpawn.getYaw());
        switch ((int) locationSpawn.getYaw()){
            case 180:
                locationBedHead.setZ(locationSpawn.getZ()-12);
                locationBedFoot.setZ(locationSpawn.getZ()-13);
                break;
            case -90:
                locationBedHead.setX(locationSpawn.getX()+12);
                locationBedFoot.setX(locationSpawn.getX()+13);
                break;
            case 0:
                locationBedHead.setZ(locationSpawn.getZ()+12);
                locationBedFoot.setZ(locationSpawn.getZ()+13);
                break;
            case 90:
                locationBedHead.setX(locationSpawn.getX()-12);
                locationBedFoot.setX(locationSpawn.getX()-13);
                break;
        }
    }

    public Color getVraiColor(){
        switch (color){
            case "Blanc":
                dataColor = 0;
                return Color.WHITE;
            case "Rose":
                dataColor = 6;
                return Color.FUCHSIA;
            case "Gris":
                dataColor = 8;
                return Color.GRAY;
            case "Rouge":
                dataColor = 14;
                return Color.RED;
            case "Bleu":
                dataColor = 11;
                return Color.BLUE;
            case "Vert":
                dataColor = 13;
                return Color.GREEN;
            case "Jaune":
                dataColor = 4;
                return Color.YELLOW;
            case "Cyan":
                dataColor = 9;
                return Color.AQUA;
        }
        return null;
    }

    public String getPrefixColor(){
        switch (color){
            case "Blanc":
                return "§f";
            case "Rose":
                return "§d";
            case "Gris":
                return "§7";
            case "Rouge":
                return "§c";
            case "Bleu":
                return "§9";
            case "Vert":
                return "§2";
            case "Jaune":
                return "§e";
            case "Cyan":
                return "§b";
        }
        return null;
    }

    public static String getPrefixColor(String color){
        switch (color){
            case "Blanc":
                return "§f";
            case "Rose":
                return "§d";
            case "Gris":
                return "§7";
            case "Rouge":
                return "§c";
            case "Bleu":
                return "§9";
            case "Vert":
                return "§2";
            case "Jaune":
                return "§e";
            case "Cyan":
                return "§b";
        }
        return null;
    }

    public int[] getTools() {
        return tools;
    }

    public void upgradeTools(int tool) {
        tools[tool]++;
        putTools();
    }
    public void downgradeTools() {
        if (tools[0] > 0) {
            tools[0]--;
        }
        if (tools[1] > 0) {
            tools[1]--;
        }
    }


    public void setArmor(int armor) {
        this.armor = armor;
    }

    public Player getPlayer() {
        return player;
    }

    public int getArmor() {
        return armor;
    }

    public Location getLocationSpawn() {
        return locationSpawn;
    }

    public Location getLocationGenerateur() {
        return locationGenerateur;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public InventoryShop getInventoryShop() {
        return inventoryShop;
    }

    public InventoryUpgrade getInventoryUpgrade() {
        return inventoryUpgrade;
    }

    public String getColor() {
        return color;
    }

    public Location getLocationBedHead() {
        return locationBedHead;
    }

    public Location getLocationBedFoot() {
        return locationBedFoot;
    }

    public boolean haveBed() {
        return bed;
    }

    public void bedBreak(){
        bed = false;
        state = 1;
        main.setupdateAllPlayerScoreboard();
        player.sendTitle("§4Vous n'avez plus de lit", "Cheh");
    }

    public int getState() {
        return state;
    }

    public byte getDataColor() {
        return dataColor;
    }

    public boolean isSharpened() {
        return sharpened;
    }

    public void putSharpened() {
        sharpened = true;
        ItemMeta meta = player.getInventory().getItem(0).getItemMeta();
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        player.getInventory().getItem(0).setItemMeta(meta);
    }

    public boolean isHeal() {
        return heal;
    }

    public void putHeal() {
        heal = true;
    }

    public boolean isDragon() {
        return dragon;
    }
    public void putDragon() {
        dragon = true;
    }
    public int getProtection() {
        return protection;
    }
    public void addProtection(){
        protection++;
        putArmor();
    }
    public int getHaste() {
        return haste;
    }
    public void addHaste(){
        haste++;
    }
    public int getForge() {
        return forge;
    }
    public void addForge(){
        forge++;
    }

    public ArrayList<String> getUpgrade() {
        return upgrade;
    }

    public boolean isBaseTrap() {
        return baseTrap;
    }


    public boolean isMiningTrap() {
        return miningTrap;
    }


    public boolean isDefenseTrap() {
        return defenseTrap;
    }


    public boolean isAlarmTrap() {
        return alarmTrap;
    }

    public void setBaseTrap(boolean baseTrap) {
        this.baseTrap = baseTrap;
        if (baseTrap){
            upgrade.add("base");
        }
        else {
            upgrade.remove("base");
            for (int i = 0; i < inventoryUpgrade.getTrapList().size(); i++) {
                if (inventoryUpgrade.getTrapList().get(i).getType().equals(Material.TRIPWIRE_HOOK)){
                    inventoryUpgrade.getTrapList().set(i, new ItemStack(Material.GLASS));
                    break;
                }
            }
        }
    }

    public void setMiningTrap(boolean miningTrap) {
        this.miningTrap = miningTrap;
        if (miningTrap){
            upgrade.add("mining");
        }
        else {
            upgrade.remove("mining");
            for (int i = 0; i < inventoryUpgrade.getTrapList().size(); i++) {
                if (inventoryUpgrade.getTrapList().get(i).getType().equals(Material.IRON_PICKAXE)){
                    inventoryUpgrade.getTrapList().set(i, new ItemStack(Material.GLASS));
                    break;
                }
            }
        }
    }

    public void setDefenseTrap(boolean defenseTrap) {
        this.defenseTrap = defenseTrap;
        if (defenseTrap){
            upgrade.add("defense");
        }
        else {
            upgrade.remove("defense");
            for (int i = 0; i < inventoryUpgrade.getTrapList().size(); i++) {
                if (inventoryUpgrade.getTrapList().get(i).getType().equals(Material.FEATHER)){
                    inventoryUpgrade.getTrapList().set(i, new ItemStack(Material.GLASS));
                    break;
                }
            }
        }
    }

    public void setAlarmTrap(boolean alarmTrap) {
        this.alarmTrap = alarmTrap;
        if (alarmTrap){
            upgrade.add("alarm");
        }
        else {
            upgrade.remove("alarm");
            for (int i = 0; i < inventoryUpgrade.getTrapList().size(); i++) {
                if (inventoryUpgrade.getTrapList().get(i).getType().equals(Material.REDSTONE_TORCH_ON)){
                    inventoryUpgrade.getTrapList().set(i, new ItemStack(Material.GLASS));
                    break;
                }
            }
        }
    }
}
