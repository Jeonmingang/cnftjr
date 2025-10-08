
package com.minkang.attendance.model;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Reward {
    private ItemStack icon;
    private List<String> commands;

    public Reward(ItemStack icon, List<String> cmds) {
        this.icon = icon;
        this.commands = cmds == null ? new ArrayList<>() : new ArrayList<>(cmds);
    }

    public ItemStack getIcon() { return icon; }
    public List<String> getCommands() { return commands; }

    public void setIcon(ItemStack icon) { this.icon = icon; }
    public void setCommands(List<String> commands) { this.commands = commands; }

    public static Reward fromConfig(ConfigurationSection sec) {
        if (sec == null) return new Reward(new ItemStack(Material.PAPER), new ArrayList<>());
        // Reconstruct simple item from config (material, name, lore)
        String matName = sec.getString("icon.material", "PAPER");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.PAPER;
        org.bukkit.inventory.ItemStack icon = new org.bukkit.inventory.ItemStack(mat);
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
        return new Reward(icon, cmds);
    }

    public void saveTo(ConfigurationSection sec) {
        if (sec == null) return;
        sec.set("icon.material", icon.getType().name());
        if (icon.hasItemMeta()) {
            org.bukkit.inventory.meta.ItemMeta meta = icon.getItemMeta();
            sec.set("icon.name", meta.hasDisplayName() ? meta.getDisplayName() : "&a보상");
            sec.set("icon.lore", meta.hasLore() ? meta.getLore() : new java.util.ArrayList<>());
        } else {
            sec.set("icon.name", "&a보상");
            sec.set("icon.lore", new java.util.ArrayList<>());
        }
        sec.set("commands", commands);
    }
}
