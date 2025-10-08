
package com.minkang.attendance.util;

import com.minkang.attendance.AttendancePlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PapiHook extends PlaceholderExpansion {

    @Override
    public String getIdentifier() { return "attendance"; }

    @Override
    public String getAuthor() { return "MinKang"; }

    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null) return "";
        UUID id = p.getUniqueId();
        int progress = AttendancePlugin.get().store().getProgress(id);
        int cycle = AttendancePlugin.get().rewards().getCycleDays();
        int next = Math.min(progress + 1, cycle);
        boolean claimedToday = AttendancePlugin.get().store().claimedToday(id);
        boolean canClaim = (progress + 1) <= cycle && !claimedToday;
        boolean bonusReady = progress >= cycle;

        switch (params.toLowerCase()) {
            case "progress": return String.valueOf(progress);
            case "cycle": return String.valueOf(cycle);
            case "next": return String.valueOf(next);
            case "claimed_today": return String.valueOf(claimedToday);
            case "can_claim": return String.valueOf(canClaim);
            case "bonus_ready": return String.valueOf(bonusReady);
            default: return "";
        }
    }
}
