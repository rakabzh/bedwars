package me.rakabzh.villagers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemShop {
    ItemStack item;
    int prix;
    Material currency;
    String description;
    List<String> description2;
    String name;
    boolean putPrix;

    public ItemShop(ItemStack item, int prix, Material currency, String description, String name, boolean putPrix, List<String> description2) {
        this.item = item;
        this.prix = prix;
        this.currency = currency;
        this.description = description;
        this.description2 = description2;
        this.name = name;
        this.putPrix = putPrix;
        setDescription();
    }

    public ItemShop(ItemStack item, int prix, Material currency, String description, String name) {
        this(item, prix, currency, description, name , true, new ArrayList<>());
    }
    public ItemShop(ItemStack item, int prix, Material currency, String description) {
        this(item, prix, currency, description, item.getType().toString().replace("_", " ") , true, new ArrayList<>());
    }

    public void setDescription(){
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;
        String prefix= "";
        String monaiString = "";
        switch (currency){
            case IRON_INGOT:
                prefix = "§f";
                monaiString = " Iron";
                break;
            case GOLD_INGOT:
                prefix = "§6";
                monaiString = " Gold";
                break;
            case EMERALD:
                prefix = "§2";
                monaiString = " Emeralds";
                break;
            case DIAMOND:
                prefix = "§b";
                monaiString = " Diamonds";
                break;
        }

        List<String> lore = new ArrayList<>();
        if (putPrix) {
            lore.add("§7Cost: " + prefix + prix + monaiString);
        }
        lore.add("");
        for (String line : wrapText(description, 35)) {
            lore.add("§7" + line);
        }
        lore.add("");
        for (String s : description2) {
            lore.add(s);
        }
        itemMeta.setDisplayName("§c" + name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    public static List<String> wrapText(String text, int maxLineLength) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxLineLength) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            if (line.length() > 0) {
                line.append(" ");
            }
            line.append(word);
        }

        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getPrix() {
        return prix;
    }

    public Material getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
