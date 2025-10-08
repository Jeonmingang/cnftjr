
package com.minkang.attendance.manager;

import com.minkang.attendance.AttendancePlugin;
import com.minkang.attendance.gui.DailyGui;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CitizensLinkManager implements Listener {
    private final AttendancePlugin plugin;
    private final Set<Integer> linked = new HashSet<>();

    public CitizensLinkManager(AttendancePlugin plugin) { this.plugin = plugin; reload(); }
    public void reload() {
        linked.clear();
        List<Integer> ids = plugin.getConfig().getIntegerList("citizens-links");
        if (ids != null) linked.addAll(ids);
    }
    public void link(int id) { linked.add(id); save(); }
    public void unlink(int id) { linked.remove(id); save(); }
    private void save() {
        plugin.getConfig().set("citizens-links", new java.util.ArrayList<>(linked));
        plugin.saveConfig();
    }
    @EventHandler public void onRightClick(NPCRightClickEvent e) {
        int id = e.getNPC().getId();
        if (!linked.contains(id)) return;
        Player p = e.getClicker();
        new DailyGui(p).open();
    }
}
