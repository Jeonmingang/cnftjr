
package com.minkang.attendance.gui;

import com.minkang.attendance.AttendancePlugin;
import com.minkang.attendance.manager.RewardManager;
import com.minkang.attendance.model.Reward;
import com.minkang.attendance.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingsGui implements Listener {
    private final AttendancePlugin plugin = AttendancePlugin.get();
    private final RewardManager rewards = plugin.rewards();
    private final Player admin;
    private Inventory inv;

    public SettingsGui(Player admin) { this.admin = admin; }

    public void open() {
        inv = Bukkit.createInventory(null, 54, "§6출석체크 설정");
        draw();
        admin.openInventory(inv);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void draw() {
        inv.clear();
        List<Integer> daySlots = rewards.daySlots();
        for (int i = 1; i <= Math.min(rewards.getCycleDays(), daySlots.size()); i++) {
            Reward rw = rewards.getReward(i);
            ItemStack icon = rw.getIcon().clone();
            if (icon.getItemMeta() != null) {
                List<String> lore = icon.getItemMeta().getLore() == null ? new ArrayList<>() : new ArrayList<>(icon.getItemMeta().getLore());
                lore.add("§7좌클릭: 아이콘을 손에 든 아이템으로 설정");
                lore.add("§7우클릭: 이 날의 명령어 추가");
                org.bukkit.inventory.meta.ItemMeta meta = icon.getItemMeta();
                meta.setLore(lore);
                meta.setDisplayName("§e" + i + "일차 설정");
                icon.setItemMeta(meta);
            }
            inv.setItem(daySlots.get(i-1), icon);
        }
        // Bonus editor
        ItemStack b = new ItemBuilder(Material.CHEST).name("&6보너스 보상 설정")
                .lore(java.util.Arrays.asList("§7좌클릭: 아이콘을 손에 든 아이템으로 설정", "§7우클릭: 명령어 추가")).build();
        inv.setItem(plugin.getConfig().getInt("gui.bonus-button-slot", 49), b);

        // Save/Reload buttons
        inv.setItem(45, new ItemBuilder(Material.WRITABLE_BOOK).name("&a설정 저장").lore(java.util.Arrays.asList("&7config.yml에 저장")).build());
        inv.setItem(53, new ItemBuilder(Material.CLOCK).name("&e리로드").lore(java.util.Arrays.asList("&7config.yml을 다시 읽기")).build());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (inv == null || !e.getInventory().equals(inv)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        // Save
        if (e.getRawSlot() == 45) {
            plugin.saveConfig();
            p.sendMessage("§a저장되었습니다.");
            return;
        }
        // Reload
        if (e.getRawSlot() == 53) {
            plugin.reloadConfig();
            plugin.rewards().reload();
            draw();
            p.sendMessage("§e리로드 완료.");
            return;
        }

        List<Integer> daySlots = rewards.daySlots();
        for (int i = 0; i < Math.min(rewards.getCycleDays(), daySlots.size()); i++) {
            if (e.getRawSlot() == daySlots.get(i)) {
                int day = i+1;
                if (e.isLeftClick()) {
                    // Set icon from item in hand
                    ItemStack hand = p.getInventory().getItemInMainHand();
                    if (hand == null || hand.getType() == Material.AIR) {
                        p.sendMessage("§c손에 아이템을 들고 설정하세요.");
                        return;
                    }
                    Reward rw = rewards.getReward(day);
                    rw.setIcon(hand);
                    ConfigurationSection sec = plugin.getConfig().getConfigurationSection("rewards."+day);
                    if (sec == null) sec = plugin.getConfig().createSection("rewards."+day);
                    rw.saveTo(sec);
                    draw();
                    p.sendMessage("§a아이콘이 설정되었습니다.");
                } else if (e.isRightClick()) {
                    plugin.rewards().beginAddCommand(p, day);
                }
                return;
            }
        }

        // Bonus
        int bslot = plugin.getConfig().getInt("gui.bonus-button-slot", 49);
        if (e.getRawSlot() == bslot) {
            if (e.isLeftClick()) {
                ItemStack hand = p.getInventory().getItemInMainHand();
                if (hand == null || hand.getType() == Material.AIR) {
                    p.sendMessage("§c손에 아이템을 들고 설정하세요.");
                    return;
                }
                plugin.rewards().getBonusReward().setIcon(hand);
                ConfigurationSection bsec = plugin.getConfig().getConfigurationSection("bonus");
                if (bsec == null) bsec = plugin.getConfig().createSection("bonus");
                plugin.rewards().getBonusReward().saveTo(bsec);
                draw();
                p.sendMessage("§a보너스 아이콘이 설정되었습니다.");
            } else if (e.isRightClick()) {
                plugin.rewards().beginAddCommand(p, -1);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (inv == null || !e.getInventory().equals(inv)) return;
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }
}
