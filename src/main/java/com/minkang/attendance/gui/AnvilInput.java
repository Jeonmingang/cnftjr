
package com.minkang.attendance.gui;

import com.minkang.attendance.AttendancePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class AnvilInput implements Listener {
    private static final Map<UUID, Consumer<String>> waiting = new HashMap<>();
    private static final AttendancePlugin plugin = AttendancePlugin.get();

    public static void open(Player p, String title, String defaultText, Consumer<String> onDone) {
        waiting.put(p.getUniqueId(), onDone);
        Inventory inv = Bukkit.createInventory(p, InventoryType.ANVIL, title == null ? "입력" : title);
        AnvilInventory anvil = (AnvilInventory) inv;
        ItemStack paper = new ItemStack(Material.PAPER);
        org.bukkit.inventory.meta.ItemMeta meta = paper.getItemMeta();
        if (meta != null) { meta.setDisplayName(defaultText == null ? "" : defaultText); paper.setItemMeta(meta); }
        anvil.setItem(0, paper);
        p.openInventory(anvil);
        Bukkit.getPluginManager().registerEvents(new AnvilInput(), plugin);
    }
    @EventHandler public void onPrepare(PrepareAnvilEvent e) {
        if (!(e.getInventory() instanceof AnvilInventory)) return;
        org.bukkit.inventory.InventoryView view = e.getView();
        if (!(view.getPlayer() instanceof Player)) return;
        Player p = (Player) view.getPlayer();
        if (!waiting.containsKey(p.getUniqueId())) return;
        String txt = ((AnvilInventory) e.getInventory()).getRenameText();
        ItemStack out = new ItemStack(Material.PAPER);
        org.bukkit.inventory.meta.ItemMeta meta = out.getItemMeta();
        if (meta != null) { meta.setDisplayName(txt == null ? "" : txt); out.setItemMeta(meta); }
        e.setResult(out);
    }
    @EventHandler public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return; Player p = (Player) e.getWhoClicked();
        if (!waiting.containsKey(p.getUniqueId())) return; if (e.getInventory().getType() != InventoryType.ANVIL) return;
        if (e.getRawSlot() == 2) { e.setCancelled(true); String txt = ((AnvilInventory) e.getInventory()).getRenameText();
            Consumer<String> cb = waiting.remove(p.getUniqueId()); if (cb != null) cb.accept(txt == null ? "" : txt); p.closeInventory();
            InventoryClickEvent.getHandlerList().unregister(this); PrepareAnvilEvent.getHandlerList().unregister(this);
        }
    }
}
