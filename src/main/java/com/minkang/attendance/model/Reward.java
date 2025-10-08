
package com.minkang.attendance.model;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class Reward {
    private ItemStack icon;
    private List<String> commands;
    private List<ItemStack> items;

    public Reward(ItemStack icon, List<String> cmds) {
        this.icon = icon;
        this.commands = cmds == null ? new ArrayList<>() : new ArrayList<>(cmds);
        this.items = new ArrayList<>();
    }

    public ItemStack getIcon() { return icon; }
    public List<String> getCommands() { return commands; }
    public List<ItemStack> getItems() { return items; }

    public void setIcon(ItemStack icon) { this.icon = icon; }
    public void setCommands(List<String> commands) { this.commands = commands; }
    public void setItems(List<ItemStack> items) { this.items = (items == null) ? new ArrayList<>() : new ArrayList<>(items); }

    public static Reward fromConfig(ConfigurationSection sec) {
        if (sec == null) return new Reward(new ItemStack(Material.PAPER), new ArrayList<>());
        String matName = sec.getString("icon.material", "PAPER");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.PAPER;
        ItemStack icon = new ItemStack(mat);
        org.bukkit.inventory.meta.ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', sec.getString("icon.name", "&a보상")));
            java.util.List<String> lore = sec.getStringList("icon.lore");
            java.util.List<String> colored = new java.util.ArrayList<>();
            for (String s : lore) colored.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', s));
            meta.setLore(colored);
            icon.setItemMeta(meta);
        }
        java.util.List<String> cmds = sec.getStringList("commands");
        Reward r = new Reward(icon, cmds);
        java.util.List<?> raw = sec.getList("items");
        if (raw != null) for (Object o : raw) if (o instanceof ItemStack) r.getItems().add(((ItemStack) o).clone());
        return r;
    }

    public void saveTo(ConfigurationSection sec) {
        if (sec == null) return;
        sec.set("icon.material", icon.getType().name());
        if (icon.hasItemMeta()) {
            org.bukkit.inventory.meta.ItemMeta meta = icon.getItemMeta();
            sec.set("icon.name", meta.hasDisplayName() ? meta.getDisplayName() : "&a보상");
            sec.set("icon.lore", meta.hasLore() ? meta.getLore() : new ArrayList<>());
        } else {
            sec.set("icon.name", "&a보상");
            sec.set("icon.lore", new ArrayList<>());
        }
        sec.set("commands", commands);
        sec.set("items", items);
    }
}
