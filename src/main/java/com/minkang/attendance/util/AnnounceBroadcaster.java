
package com.minkang.attendance.util;

import com.minkang.attendance.AttendancePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class AnnounceBroadcaster {

    public static void broadcast(String rawMessage, String senderName) {
        if (!AttendancePlugin.get().getConfig().getBoolean("announce.enable", true)) return;

        for (Player pl : Bukkit.getOnlinePlayers()) {
            Map<String, String> vars = new HashMap<>();
            vars.put("message", rawMessage);
            vars.put("sender", senderName);

            String header = AttendancePlugin.get().getConfig().getString("announce.chat.header", "&6&l[공지]");
            String body = AttendancePlugin.get().getConfig().getString("announce.chat.message-format", "&f{message}");
            String senderFmt = AttendancePlugin.get().getConfig().getBoolean("announce.chat.show-sender", true)
                    ? AttendancePlugin.get().getConfig().getString("announce.chat.sender-format", "&7From &f{sender}")
                    : null;
            String footer = AttendancePlugin.get().getConfig().getString("announce.chat.footer", "");

            pl.sendMessage(MessageUtil.fmt(header, pl, vars));
            pl.sendMessage(MessageUtil.fmt(body, pl, vars));
            if (senderFmt != null && !senderFmt.isEmpty()) pl.sendMessage(MessageUtil.fmt(senderFmt, pl, vars));
            if (footer != null && !footer.isEmpty()) pl.sendMessage(MessageUtil.fmt(footer, pl, vars));

            if (AttendancePlugin.get().getConfig().getBoolean("announce.title.enable", true)) {
                String t = AttendancePlugin.get().getConfig().getString("announce.title.title", "&e&l공지");
                String st = AttendancePlugin.get().getConfig().getString("announce.title.subtitle", "&f{message}");
                MessageUtil.title(pl, MessageUtil.fmt(t, pl, vars), MessageUtil.fmt(st, pl, vars));
            }
            if (AttendancePlugin.get().getConfig().getBoolean("announce.actionbar.enable", true)) {
                String ab = AttendancePlugin.get().getConfig().getString("announce.actionbar.text", "&6[공지]&f {message}");
                MessageUtil.actionBar(pl, MessageUtil.fmt(ab, pl, vars));
            }
            MessageUtil.sound(pl, "announce.sound");
        }
        Bukkit.getLogger().info("[공지] " + rawMessage + " (by " + senderName + ")");
    }
}
