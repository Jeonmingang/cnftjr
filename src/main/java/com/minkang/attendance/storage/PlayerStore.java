
package com.minkang.attendance.storage;

import com.minkang.attendance.AttendancePlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class PlayerStore implements Listener {
    private final AttendancePlugin plugin;
    private final File file;
    private final FileConfiguration data;
    private final Map<UUID, Set<Integer>> claimed = new HashMap<>();
    private final Map<UUID, String> lastClaimDate = new HashMap<>();

    public PlayerStore(AttendancePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.yml");
        if (!file.exists()) { try { file.getParentFile().mkdirs(); file.createNewFile(); } catch (IOException ignored) {} }
        this.data = YamlConfiguration.loadConfiguration(file);
        loadAll();
    }
    private void loadAll() {
        if (!data.isConfigurationSection("players")) return;
        for (String key : data.getConfigurationSection("players").getKeys(false)) {
            UUID id = UUID.fromString(key);
            List<Integer> list = data.getIntegerList("players." + key + ".claimed");
            claimed.put(id, new HashSet<>(list));
            String last = data.getString("players." + key + ".lastClaimDate", "");
            lastClaimDate.put(id, last);
        }
    }
    public void save() {
        for (Map.Entry<UUID, Set<Integer>> e : claimed.entrySet()) data.set("players."+e.getKey()+".claimed", new java.util.ArrayList<>(e.getValue()));
        for (Map.Entry<UUID, String> e : lastClaimDate.entrySet()) data.set("players."+e.getKey()+".lastClaimDate", e.getValue());
        try { data.save(file); } catch (IOException ignored) {}
    }
    @EventHandler public void onQuit(PlayerQuitEvent e) { save(); }
    public boolean hasClaimed(UUID id, int day) { return claimed.getOrDefault(id, Collections.emptySet()).contains(day); }
    public int getProgress(UUID id) { return claimed.getOrDefault(id, Collections.emptySet()).size(); }
    public void markClaimed(UUID id, int day) { claimed.computeIfAbsent(id,k->new HashSet<>()).add(day); lastClaimDate.put(id, LocalDate.now().toString()); save(); }
    public boolean claimedToday(UUID id) { String last = lastClaimDate.get(id); return last != null && LocalDate.now().toString().equals(last); }
    public void resetCycle(UUID id) { claimed.remove(id); lastClaimDate.remove(id); save(); }
}
