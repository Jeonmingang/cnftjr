
package com.minkang.attendance.gui;

import com.minkang.attendance.AttendancePlugin;
import com.minkang.attendance.manager.RewardManager;
import com.minkang.attendance.storage.PlayerStore;
import com.minkang.attendance.util.ItemBuilder;
import com.minkang.attendance.util.MessageUtil;
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
import java.util.UUID;

public class DailyGui implements Listener {
    private final AttendancePlugin plugin = AttendancePlugin.get();
    private final Player player;
    private Inventory inv;
    private final RewardManager rewards;
    private final PlayerStore store;

    public DailyGui(Player player) {
        this.player = player;
        this.rewards = plugin.rewards();
        this.store = plugin.store();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    public void open() {
        String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("gui.title", "&6DAILY"));
        inv = Bukkit.createInventory(null, 54, title);
        draw(); player.openInventory(inv);
    }
    private void draw() {
        inv.clear();
        java.util.List<Integer> daySlots = rewards.daySlots();
        UUID id = player.getUniqueId();
        int progress = store.getProgress(id);
        int nextDay = progress + 1;
        
        java.util.Map<String, String> tokens = new java.util.HashMap<>();
        tokens.put("progress", String.valueOf(progress));
        tokens.put("cycle", String.valueOf(rewards.getCycleDays()));
        tokens.put("next", String.valueOf(nextDay));
for (int i = 1; i <= Math.min(rewards.getCycleDays(), daySlots.size()); i++) {
            int slot = daySlots.get(i-1);
            boolean claimed = store.hasClaimed(id, i);
            boolean claimable = (i == nextDay);
            boolean locked = i > nextDay;
            ItemStack item = rewards.buildDayItem(i, claimed, claimable, locked);
            inv.setItem(slot, item);
        }
        int bonusSlot = plugin.getConfig().getInt("gui.bonus-button-slot", 49);
        ItemStack bonus = RewardIcons.bonusIcon();
        if (progress >= rewards.getCycleDays()) bonus = new ItemBuilder(bonus).name("&a보너스 수령 가능!").glow(true).build();
        inv.setItem(bonusSlot, bonus);
    }
    @EventHandler public void onClick(InventoryClickEvent e) {
        if (inv == null || e.getInventory() == null) return;
        if (!e.getInventory().equals(inv)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        MessageUtil.sound(p, "sounds.click");
        UUID id = p.getUniqueId();
        java.util.List<Integer> daySlots = rewards.daySlots();
        int progress = store.getProgress(id);
        int nextDay = progress + 1;
        int bonusSlot = plugin.getConfig().getInt("gui.bonus-button-slot", 49);
        if (e.getRawSlot() == bonusSlot) {
            if (e.isRightClick()) { new PreviewGui(p, "&6보너스 미리보기", plugin.rewards().getBonusReward()).open(); return; }
            if (progress >= rewards.getCycleDays()) {
                plugin.rewards().giveBonus(p); store.resetCycle(id);
                p.sendMessage(MessageUtil.fmt(plugin.getConfig().getString("texts.bonus-claimed", "&b보너스 보상을 받았습니다!"), p, tokens));
                MessageUtil.title(p, plugin.getConfig().getString("titles.bonus.title", "&6보너스!"), plugin.getConfig().getString("titles.bonus.subtitle", ""));
                MessageUtil.sound(p, "sounds.bonus"); draw();
            } else { p.sendMessage(MessageUtil.fmt(plugin.getConfig().getString("texts.bonus-ready", "&c아직 보너스를 받을 수 없습니다."), p, tokens)); MessageUtil.sound(p, "sounds.error"); }
            return;
        }
        int day = -1;
        for (int i = 0; i < Math.min(plugin.rewards().getCycleDays(), daySlots.size()); i++) if (e.getRawSlot() == daySlots.get(i)) { day = i+1; break; }
        if (day == -1) return;
        
        tokens.put("day", String.valueOf(day));
if (e.isRightClick()) { new PreviewGui(p, "&e" + day + "일차 보상 미리보기", plugin.rewards().getReward(day)).open(); return; }
        boolean claimable = (day == nextDay);
        if (!claimable) { p.sendMessage(MessageUtil.fmt(plugin.getConfig().getString("texts.not-available", "&c아직 수령할 수 없는 날입니다."), p, tokens)); MessageUtil.sound(p, "sounds.error"); return; }
        if (plugin.getConfig().getBoolean("allow-claim-once-per-day", true) && store.claimedToday(id)) { p.sendMessage(MessageUtil.fmt(plugin.getConfig().getString("texts.already-claimed-today", "&c오늘은 이미 받았습니다."), p, tokens)); MessageUtil.sound(p, "sounds.error"); return; }
        plugin.rewards().giveReward(p, day); store.markClaimed(id, day);
        MessageUtil.title(p, plugin.getConfig().getString("titles.claim.title", "&a수령!"), plugin.getConfig().getString("titles.claim.subtitle", ""));
        MessageUtil.sound(p, "sounds.claim"); draw();
    }
    @EventHandler public void onClose(InventoryCloseEvent e) {
        if (inv == null) return; if (!e.getInventory().equals(inv)) return;
        InventoryClickEvent.getHandlerList().unregister(this); InventoryCloseEvent.getHandlerList().unregister(this);
    }
    public static class RewardIcons {
        public static ItemStack bonusIcon() {
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(Material.LIME_CONCRETE);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', com.minkang.attendance.AttendancePlugin.get().getConfig().getString("gui.bonus-button.name", "&a&lBONUS!!")));
                java.util.List<String> lore = com.minkang.attendance.AttendancePlugin.get().getConfig().getStringList("gui.bonus-button.lore");
                java.util.List<String> out = new java.util.ArrayList<>(); for (String s : lore) out.add(ChatColor.translateAlternateColorCodes('&', s));
                meta.setLore(out); item.setItemMeta(meta);
            }
            return item;
        }
    }
}
