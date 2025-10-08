
package com.minkang.attendance.gui;

import com.minkang.attendance.AttendancePlugin;
import com.minkang.attendance.model.Reward;
import com.minkang.attendance.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class CommandsEditorGui implements Listener {
    private final AttendancePlugin plugin = AttendancePlugin.get();
    private final Player admin; private final int day; private Inventory inv;
    public CommandsEditorGui(Player admin, int day) { this.admin = admin; this.day = day; }
    public void open() { String title = (day == -1) ? "§6보너스 명령어 편집 (ESC 저장)" : "§e" + day + "일차 명령어 편집 (ESC 저장)";
        inv = Bukkit.createInventory(null, 54, title); draw(); admin.openInventory(inv); Bukkit.getPluginManager().registerEvents(this, plugin); }
    private void draw() {
        inv.clear(); Reward r = (day == -1) ? plugin.rewards().getBonusReward() : plugin.rewards().getReward(day);
        java.util.List<String> cmds = new java.util.ArrayList<>(r.getCommands()); int pos = 0;
        for (String c : cmds) { if (pos >= 45) break; inv.setItem(pos++, new ItemBuilder(Material.PAPER).name("&f" + c).lore(java.util.Arrays.asList("&7좌클릭: 수정", "&7우클릭/버리기키: 제거")).build()); }
        inv.setItem(45, new ItemBuilder(Material.ANVIL).name("&a명령어 추가").lore(java.util.Arrays.asList("&7클릭 후 모루 이름 바꾸기로 입력", "&7{player} 변수 사용 가능")).build());
        inv.setItem(49, new ItemBuilder(Material.WRITABLE_BOOK).name("&aESC를 누르면 저장됩니다").lore(java.util.Arrays.asList("&7위 5줄에 종이 아이템이 명령 리스트")).build());
    }
    @EventHandler public void onClick(InventoryClickEvent e) {
        if (inv == null || !e.getInventory().equals(inv)) return; e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return; Player p = (Player) e.getWhoClicked();
        if (e.getRawSlot() == 45) { AnvilInput.open(p, "명령어 입력", "give {player} diamond 1", txt -> {
                if (txt == null || txt.trim().isEmpty()) return;
                for (int i=0;i<45;i++) if (inv.getItem(i) == null || inv.getItem(i).getType()==Material.AIR) {
                    inv.setItem(i, new ItemBuilder(Material.PAPER).name("&f" + txt).lore(java.util.Arrays.asList("&7좌클릭: 수정", "&7우클릭/버리기키: 제거")).build()); break;
                } });
            return; }
        if (e.getRawSlot() >=0 && e.getRawSlot() < 45) {
            ItemStack it = inv.getItem(e.getRawSlot());
            if (it == null || it.getType() != Material.PAPER || it.getItemMeta() == null) return;
            String current = org.bukkit.ChatColor.stripColor(it.getItemMeta().getDisplayName());
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.DROP) { inv.setItem(e.getRawSlot(), null); return; }
            if (e.getClick().isLeftClick()) {
                final int slot = e.getRawSlot();
                AnvilInput.open(p, "명령어 수정", current, txt -> {
                    if (txt == null || txt.trim().isEmpty()) { inv.setItem(slot, null); return; }
                    inv.setItem(slot, new ItemBuilder(Material.PAPER).name("&f" + txt).lore(java.util.Arrays.asList("&7좌클릭: 수정", "&7우클릭/버리기키: 제거")).build());
                });
            }
        }
    }
    @EventHandler public void onClose(InventoryCloseEvent e) {
        if (inv == null || !e.getInventory().equals(inv)) return;
        java.util.List<String> cmds = new java.util.ArrayList<>(); for (int i=0;i<45;i++) {
            ItemStack it = inv.getItem(i);
            if (it == null || it.getType() != Material.PAPER || it.getItemMeta()==null) continue;
            String c = org.bukkit.ChatColor.stripColor(it.getItemMeta().getDisplayName());
            if (c != null && !c.trim().isEmpty()) cmds.add(c);
        }
        Reward r = (day == -1) ? plugin.rewards().getBonusReward() : plugin.rewards().getReward(day); r.setCommands(cmds);
        org.bukkit.configuration.ConfigurationSection sec = (day == -1) ? plugin.getConfig().getConfigurationSection("bonus") : plugin.getConfig().getConfigurationSection("rewards."+day);
        if (sec == null) sec = (day == -1) ? plugin.getConfig().createSection("bonus") : plugin.getConfig().createSection("rewards."+day);
        r.saveTo(sec); plugin.saveConfig();
        InventoryClickEvent.getHandlerList().unregister(this); InventoryCloseEvent.getHandlerList().unregister(this);
    }
}
