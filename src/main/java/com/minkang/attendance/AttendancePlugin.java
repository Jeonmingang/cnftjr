
package com.minkang.attendance;

import com.minkang.attendance.gui.DailyGui;
import com.minkang.attendance.gui.SettingsGui;
import com.minkang.attendance.manager.RewardManager;
import com.minkang.attendance.storage.PlayerStore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class AttendancePlugin extends JavaPlugin implements Listener {

    private static AttendancePlugin instance;
    private RewardManager rewardManager;
    private PlayerStore playerStore;

    public static AttendancePlugin get() { return instance; }
    public RewardManager rewards() { return rewardManager; }
    public PlayerStore store() { return playerStore; }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.rewardManager = new RewardManager(this);
        this.playerStore = new PlayerStore(this);
        Bukkit.getPluginManager().registerEvents(rewardManager, this);
        Bukkit.getPluginManager().registerEvents(playerStore, this);
        if (org.bukkit.Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
    try { new com.minkang.attendance.util.PapiHook().register(); getLogger().info("PlaceholderAPI 확장 등록 완료."); } catch (Throwable t) { getLogger().warning("PAPI 등록 실패: " + t.getMessage()); }
}
getLogger().info("AttendanceCheck enabled");
    }

    @Override
    public void onDisable() {
        if (playerStore != null) playerStore.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // NOTICE_CMD
        if (command.getName().equalsIgnoreCase("공지")) {
            if (args.length == 0) {
                sender.sendMessage("§e사용법: /공지 <할말>");
                return true;
            }
            if (sender instanceof org.bukkit.entity.Player) {
                org.bukkit.entity.Player pp = (org.bukkit.entity.Player) sender;
                if (!pp.hasPermission("attendance.notice")) {
                    pp.sendMessage("§c권한이 없습니다.");
                    return true;
                }
            }
            String msg = String.join(" ", args);
            com.minkang.attendance.util.AnnounceBroadcaster.broadcast(msg, sender.getName());
            return true;
        }
    
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }
        Player p = (Player) sender;
        
        if (args.length >= 1 && args[0].equalsIgnoreCase("초기화")) {
            if (!p.hasPermission("attendance.admin")) {
                p.sendMessage("§c권한이 없습니다.");
                return true;
            }
            if (args.length < 2) {
                p.sendMessage("§e사용법: /출석체크 초기화 <플레이어>");
                return true;
            }
            org.bukkit.OfflinePlayer op = org.bukkit.Bukkit.getOfflinePlayer(args[1]);
            java.util.UUID uid = op.getUniqueId();
            this.playerStore.resetCycle(uid);
            p.sendMessage("§a" + args[1] + " 의 출석 데이터를 초기화했습니다.");
            return true;
        }
    
        if (args.length == 0) {
            new DailyGui(p).open();
            return true;
        }
        if (args.length >= 1 && (args[0].equalsIgnoreCase("설정") || args[0].equalsIgnoreCase("setting"))) {
            if (!p.hasPermission("attendance.admin")) {
                p.sendMessage("§c권한이 없습니다.");
                return true;
            }
            new SettingsGui(p).open();
            p.sendMessage(getConfig().getString("texts.open-settings", "설정 GUI를 열었습니다."));
            return true;
        }
        p.sendMessage("§e/출석체크 §7- GUI 열기");
        if (p.hasPermission("attendance.admin")) p.sendMessage("§e/출석체크 설정 §7- 설정 GUI");
        return true;
    }
}
