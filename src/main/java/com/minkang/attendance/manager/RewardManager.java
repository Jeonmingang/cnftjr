
package com.minkang.attendance.manager;

import com.minkang.attendance.AttendancePlugin;
import com.minkang.attendance.model.Reward;
import com.minkang.attendance.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.*;

public class RewardManager implements Listener {
    private final AttendancePlugin plugin;
    private final int cycleDays;

    private final Map<Integer, Reward> rewards = new HashMap<>();
    private Reward bonusReward;

    // Editing state for admin chat input
    private final Map<UUID, Integer> awaitingCommandInput = new HashMap<>(); // day index

    public RewardManager(AttendancePlugin plugin) {
        this.plugin = plugin;
        this.cycleDays = plugin.getConfig().getInt("cycle-days", 28);
        reload();
    }

    public void reload() {
        rewards.clear();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("rewards");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                try {
                    int day = Integer.parseInt(key);
                    rewards.put(day, Reward.fromConfig(sec.getConfigurationSection(key)));
                } catch (NumberFormatException ignored) {}
            }
        }
        bonusReward = Reward.fromConfig(plugin.getConfig().getConfigurationSection("bonus"));
    }

    public int getCycleDays() { return cycleDays; }

    public Reward getReward(int day) {
        return rewards.getOrDefault(day, new Reward(new ItemStack(Material.PAPER), Collections.singletonList("give {player} paper 1")));
    }

    public Reward getBonusReward() { return bonusReward; }

    public List<Integer> daySlots() {
        // 4 rows x 7 columns layout starting from slot 10
        List<Integer> slots = new ArrayList<>();
        int start = 10;
        for (int r=0; r<4; r++) {
            for (int c=0; c<7; c++) {
                slots.add(start + r*9 + c);
            }
        }
        return slots;
    }

    public ItemStack buildDayItem(int day, boolean claimed, boolean claimableNext, boolean locked) {
        Reward rw = getReward(day);
        List<String> lore = new ArrayList<>();
        if (claimableNext && !claimed && !locked) {
            lore.add(ChatColor.GREEN + "보상 수령 가능!");
            lore.add(ChatColor.GRAY + "좌클릭: 보상 수령하기");
            lore.add(ChatColor.GRAY + "우클릭: 보상 미리보기");
        } else if (claimed) {
            lore.add(ChatColor.AQUA + "이미 수령했습니다.");
        } else if (locked) {
            lore.add(ChatColor.DARK_GRAY + "잠금됨");
        }
        if (rw.getIcon().hasItemMeta()) {
            ItemStack copy = rw.getIcon().clone();
            if (copy.getItemMeta() != null) {
                org.bukkit.inventory.meta.ItemMeta meta = copy.getItemMeta();
                List<String> baseLore = meta.getLore() == null ? new ArrayList<>() : new ArrayList<>(meta.getLore());
                baseLore.addAll(lore);
                meta.setLore(baseLore);
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                        meta.hasDisplayName() ? meta.getDisplayName() : "&a" + day + "일차 보상"));
                copy.setItemMeta(meta);
            }
            return new ItemBuilder(copy).glow(claimed).build();
        }
        return new ItemBuilder(Material.PAPER)
                .name("&a" + day + "일차 보상")
                .lore(lore)
                .glow(claimed).build();
    }

    public void giveReward(Player p, int day) {
        Reward rw = getReward(day);
        for (String cmd : rw.getCommands()) {
            if (cmd == null || cmd.trim().isEmpty()) continue;
            String parsed = cmd.replace("{player}", p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        }
    }

    public void giveBonus(Player p) {
        if (bonusReward == null) return;
        for (String cmd : bonusReward.getCommands()) {
            if (cmd == null || cmd.trim().isEmpty()) continue;
            String parsed = cmd.replace("{player}", p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        }
    }

    public void beginAddCommand(Player admin, int day) {
        awaitingCommandInput.put(admin.getUniqueId(), day);
        admin.closeInventory();
        admin.sendMessage("§e추가할 명령어를 채팅으로 입력하세요. {player} 사용 가능. §7(예: give {player} diamond 3)");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        if (!awaitingCommandInput.containsKey(id)) return;
        e.setCancelled(true);
        int day = awaitingCommandInput.remove(id);
        Bukkit.getScheduler().runTask(plugin, () -> {
            String msg = e.getMessage();
            Reward rw;
            if (day == -1) {
                rw = bonusReward;
            } else {
                rw = getReward(day);
            }
            List<String> cmds = new ArrayList<>(rw.getCommands());
            cmds.add(msg);
            rw.setCommands(cmds);

            // Save to config
            if (day == -1) {
                ConfigurationSection bsec = plugin.getConfig().getConfigurationSection("bonus");
                if (bsec == null) bsec = plugin.getConfig().createSection("bonus");
                rw.saveTo(bsec);
            } else {
                ConfigurationSection sec = plugin.getConfig().getConfigurationSection("rewards." + day);
                if (sec == null) sec = plugin.getConfig().createSection("rewards." + day);
                rw.saveTo(sec);
            }
            plugin.saveConfig();
            e.getPlayer().sendMessage("§a명령어가 추가되었습니다.");
        });
    }
}
