
package com.minkang.attendance.gui;

import com.minkang.attendance.AttendancePlugin;
import com.minkang.attendance.model.Reward;
import com.minkang.attendance.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PreviewGui implements Listener {
    private final AttendancePlugin plugin = AttendancePlugin.get();
    private final Player viewer;
    private final Reward reward;
    private final String title;
    private Inventory inv;

    public PreviewGui(Player viewer, String title, Reward reward) {
        this.viewer = viewer;
        this.reward = reward;
        this.title = title;
    }

    public void open() {
        inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', title));
        draw();
        viewer.openInventory(inv);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void draw() {
        inv.clear();
        // Place icon in center
        inv.setItem(13, reward.getIcon());
        // List commands as papers
        List<String> cmds = reward.getCommands();
        int idx = 0;
        for (int slot : new int[]{9,10,11,12,14,15,16,17,18,19,20,21,22,23,24,25}) {
            if (idx >= cmds.size()) break;
            String c = cmds.get(idx++);
            ItemStack paper = new ItemBuilder(Material.PAPER).name("&f실행: &7" + c).build();
            inv.setItem(slot, paper);
        }
        inv.setItem(26, new ItemBuilder(Material.BARRIER).name("&c닫기").build());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (inv == null || !e.getInventory().equals(inv)) return;
        e.setCancelled(true);
        if (e.getRawSlot() == 26) {
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (inv == null || !e.getInventory().equals(inv)) return;
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }
}
