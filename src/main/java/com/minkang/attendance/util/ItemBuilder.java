
package com.minkang.attendance.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private ItemStack item;
    public ItemBuilder(Material mat) { this.item = new ItemStack(mat); }
    public ItemBuilder(ItemStack base) { this.item = base.clone(); }

    public ItemBuilder name(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> result = new ArrayList<>();
            if (lore != null) for (String s : lore) result.add(ChatColor.translateAlternateColorCodes('&', s));
            meta.setLore(result);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (!glow) return this;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemStack build() { return item; }
}
