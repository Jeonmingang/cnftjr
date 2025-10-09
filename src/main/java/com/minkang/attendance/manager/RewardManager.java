
package com.minkang.attendance.manager;

import com.minkang.attendance.AttendancePlugin;
import com.minkang.attendance.model.Reward;
import com.minkang.attendance.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RewardManager implements Listener {
    private final AttendancePlugin plugin;
    private final int cycleDays;
    private final Map<Integer, Reward> rewards = new HashMap<>();
    private Reward bonusReward;

    public RewardManager(AttendancePlugin plugin) {
        this.plugin = plugin;
        this.cycleDays = plugin.getConfig().getInt("cycle-days", 28);
        reload();
    }

    public void reload() {
        rewards.clear();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("rewards");
        if (sec != null) for (String key : sec.getKeys(false)) try {
            int day = Integer.parseInt(key);
            rewards.put(day, Reward.fromConfig(sec.getConfigurationSection(key)));
        } catch (NumberFormatException ignored) {}
        bonusReward = Reward.fromConfig(plugin.getConfig().getConfigurationSection("bonus"));
    }

    public int getCycleDays() { return cycleDays; }
    public Reward getReward(int day) {
        return rewards.getOrDefault(day, new Reward(new ItemStack(Material.PAPER), Collections.singletonList("give {player} paper 1")));
    }
    public Reward getBonusReward() { return bonusReward; }

    public List<Integer> daySlots() {
        List<Integer> slots = new ArrayList<>();
        int start = 10;
        for (int r=0; r<4; r++) for (int c=0; c<7; c++) slots.add(start + r*9 + c);
        return slots;
    }

    public ItemStack buildDayItem(int day, boolean claimed, boolean claimableNext, boolean locked) {
        Reward rw = getReward(day);
        java.util.List<String> lore = new java.util.ArrayList<>();
        if (claimableNext && !claimed && !locked) {
            lore.add(org.bukkit.ChatColor.GREEN + "보상 수령 가능!");
            lore.add(org.bukkit.ChatColor.GRAY + "좌클릭: 보상 수령하기");
            lore.add(org.bukkit.ChatColor.GRAY + "우클릭: 보상 미리보기");
        } else if (claimed) {
            lore.add(org.bukkit.ChatColor.AQUA + "이미 수령했습니다.");
        } else if (locked) {
            lore.add(org.bukkit.ChatColor.DARK_GRAY + "잠금됨");
        }
        if (rw.getIcon().hasItemMeta()) {
            ItemStack copy = rw.getIcon().clone();
            if (copy.getItemMeta() != null) {
                org.bukkit.inventory.meta.ItemMeta meta = copy.getItemMeta();
                java.util.List<String> baseLore = meta.getLore() == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(meta.getLore());
                baseLore.addAll(lore);
                meta.setLore(baseLore);
                meta.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', meta.hasDisplayName() ? meta.getDisplayName() : "&a" + day + "일차 보상"));
                copy.setItemMeta(meta);
            }
            return new ItemBuilder(copy).glow(claimed).build();
        }
        return new ItemBuilder(Material.PAPER).name("&a" + day + "일차 보상").lore(lore).glow(claimed).build();
    }

    public void giveReward(Player p, int day) {
        Reward rw = getReward(day);
        java.util.List<org.bukkit.inventory.ItemStack> items = rw.getItems();
        if (items != null) for (org.bukkit.inventory.ItemStack it : items) {
            // 미리보기용 종이(실행: )은 지급하지 않음
            try {
                if (it.getType() == org.bukkit.Material.PAPER) {
                    org.bukkit.inventory.meta.ItemMeta m = it.getItemMeta();
                    if (m != null && m.hasDisplayName()) {
                        String dn = org.bukkit.ChatColor.stripColor(m.getDisplayName());
                        if (dn != null && dn.startsWith("실행:")) continue;
                    }
                }
            } catch (Throwable ignore) {}

            if (it == null) continue;
            java.util.Map<Integer, org.bukkit.inventory.ItemStack> left = p.getInventory().addItem(it.clone());
            for (org.bukkit.inventory.ItemStack leftover : left.values()) p.getWorld().dropItemNaturally(p.getLocation(), leftover);
        }
        for (String cmd : rw.getCommands()) {
            if (cmd == null || cmd.trim().isEmpty()) continue;
            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), cmd.replace("{player}", p.getName()));
        }
    }

    public void giveBonus(Player p) {
        if (bonusReward == null) return;
        java.util.List<org.bukkit.inventory.ItemStack> items = bonusReward.getItems();
        if (items != null) for (org.bukkit.inventory.ItemStack it : items) {
            // 미리보기용 종이(실행: )은 지급하지 않음
            try {
                if (it.getType() == org.bukkit.Material.PAPER) {
                    org.bukkit.inventory.meta.ItemMeta m = it.getItemMeta();
                    if (m != null && m.hasDisplayName()) {
                        String dn = org.bukkit.ChatColor.stripColor(m.getDisplayName());
                        if (dn != null && dn.startsWith("실행:")) continue;
                    }
                }
            } catch (Throwable ignore) {}

            if (it == null) continue;
            java.util.Map<Integer, org.bukkit.inventory.ItemStack> left = p.getInventory().addItem(it.clone());
            for (org.bukkit.inventory.ItemStack leftover : left.values()) p.getWorld().dropItemNaturally(p.getLocation(), leftover);
        }
        for (String cmd : bonusReward.getCommands()) {
            if (cmd == null || cmd.trim().isEmpty()) continue;
            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), cmd.replace("{player}", p.getName()));
        }
    }
}
