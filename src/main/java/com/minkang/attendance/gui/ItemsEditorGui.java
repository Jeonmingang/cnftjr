
package com.minkang.attendance.gui;

import com.minkang.attendance.AttendancePlugin;
import com.minkang.attendance.manager.RewardManager;
import com.minkang.attendance.model.Reward;
import com.minkang.attendance.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class ItemsEditorGui implements Listener {
    private final AttendancePlugin plugin = AttendancePlugin.get();
    private final Player admin; private final int day; private Inventory inv;
    public ItemsEditorGui(Player admin, int day) { this.admin = admin; this.day = day; }
    public void open() {
        String title = (day == -1) ? "§6보너스 아이템 편집 (ESC 저장)" : "§e" + day + "일차 아이템 편집 (ESC 저장)";
        inv = Bukkit.createInventory(null, 54, title); loadExisting(); admin.openInventory(inv); Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    private void loadExisting() {
        RewardManager rm = plugin.rewards(); Reward r = (day == -1) ? rm.getBonusReward() : rm.getReward(day); List<ItemStack> items = r.getItems(); int slot = 0;
        if (items != null) for (ItemStack it : items) { if (it == null) continue; if (slot >= 45) break; inv.setItem(slot++, it.clone()); }
        inv.setItem(49, new ItemBuilder(Material.WRITABLE_BOOK).name("&aESC를 누르면 저장됩니다").lore(java.util.Arrays.asList("&7위 5줄에 아이템 넣기/제거", "&7수량도 그대로 저장됨")).build());
    }
    @EventHandler public void onClose(InventoryCloseEvent e) {
        if (inv == null || !e.getInventory().equals(inv)) return; List<ItemStack> list = new ArrayList<>();
        for (int i=0;i<45;i++) { ItemStack it = inv.getItem(i); if (it != null && it.getType() != Material.AIR) list.add(it.clone()); }
        Reward r = (day == -1) ? plugin.rewards().getBonusReward() : plugin.rewards().getReward(day); r.setItems(list);
        org.bukkit.configuration.ConfigurationSection sec = (day == -1) ? plugin.getConfig().getConfigurationSection("bonus") : plugin.getConfig().getConfigurationSection("rewards."+day);
        if (sec == null) sec = (day == -1) ? plugin.getConfig().createSection("bonus") : plugin.getConfig().createSection("rewards."+day);
        r.saveTo(sec); plugin.saveConfig(); InventoryCloseEvent.getHandlerList().unregister(this);
    }
}
