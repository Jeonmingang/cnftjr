
package com.minkang.attendance.util;

import com.minkang.attendance.AttendancePlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageUtil {

    public static boolean hasPapi() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}


    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s);
    
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}


    public static String fmt(String s, Player p, Map<String, String> tokens) {
        String out = s;
        if (tokens != null) {
            for (Map.Entry<String, String> e : tokens.entrySet()) {
                out = out.replace("{"+e.getKey()+"
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}
", e.getValue());
            
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}

        
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}

        out = out.replace("{player
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}
", p.getName());
        if (hasPapi()) {
            out = PlaceholderAPI.setPlaceholders(p, out);
        
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}

        return color(out);
    
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}


    public static void title(Player p, String title, String subtitle) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("titles.enable", true)) return;
        int fi = c.getInt("titles.fade-in", 10);
        int st = c.getInt("titles.stay", 40);
        int fo = c.getInt("titles.fade-out", 10);
        p.sendTitle(color(title), color(subtitle), fi, st, fo);
    
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}


    public static void sound(Player p, String path) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("sounds.enable", true)) return;
        String name = c.getString(path, "");
        if (name == null || name.isEmpty()) return;
        try {
            Sound s = Sound.valueOf(name);
            p.playSound(p.getLocation(), s, 1.0f, 1.0f);
        
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}
 catch (IllegalArgumentException ignored) {
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}

    
    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}


    public static void actionBar(Player p, String text) {
        FileConfiguration c = AttendancePlugin.get().getConfig();
        if (!c.getBoolean("announce.actionbar.enable", c.getBoolean("actionbar.enable", true))) {
            // allow legacy key actionbar.enable for safety
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(text)));
    }
}

