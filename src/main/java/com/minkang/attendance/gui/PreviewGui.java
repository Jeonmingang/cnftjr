
package com.minkang.attendance.gui;

import com.minkang.attendance.AttendancePlugin;
import com.minkang.attendance.model.Reward;
import com.minkang.attendance.util.ItemBuilder;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 미리보기 GUI
 * - 상단/중단: 실제 지급될 아이템들
 * - 하단(종이): 실행될 명령어 목록(미지급, 안내용)
 * - 우측 하단: 닫기
 */
public class PreviewGui implements Listener {
    private final AttendancePlugin plugin = AttendancePlugin.get();
    private final Player viewer;
    private final Reward reward;
    private final String title;
    private Inventory inv;

    public PreviewGui(Player viewer, String title, Reward reward) {
        this.viewer = viewer;
        this.reward = reward;
        this.title = ChatColor.translateAlternateColorCodes('&', title);
    }

    public void open() {
        inv = Bukkit.createInventory(null, 27, title);
        draw();
        viewer.openInventory(inv);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private ItemStack commandPaper(String cmd) {
        return new ItemBuilder(Material.PAPER).name("§f실행: §7" + cmd).build();
    }

    private void draw() {
        inv.clear();
        // 1) 실제 지급 아이템들 표시
        List<ItemStack> items = reward.getItems();
        int[] itemSlots = new int[]{0,1,2,3,4,5,6,7,8, 9,10,11,12,14,15,16,17}; // 13은 비워둠
        int idx = 0;
        if (items != null) {
            for (ItemStack it : items) {
                if (it == null) continue;
                if (idx >= itemSlots.length) break;
                inv.setItem(itemSlots[idx++], it.clone());
            }
        }
        // 가운데에 보상 아이콘(정보용)
        inv.setItem(13, reward.getIcon());

        // 2) 실행될 명령어 목록(안내용 종이)
        List<String> cmds = reward.getCommands();
        int[] cmdSlots = new int[]{18,19,20,21,22,23,24,25};
        int c = 0;
        if (cmds != null) {
            for (String cmd : cmds) {
                if (cmd == null || cmd.trim().isEmpty()) continue;
                if (c >= cmdSlots.length) break;
                inv.setItem(cmdSlots[c++], commandPaper(cmd));
            }
        }
        // 3) 닫기 버튼
        inv.setItem(26, new ItemBuilder(Material.BARRIER).name("§c닫기").build());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (inv == null || !e.getInventory().equals(inv)) return;
        e.setCancelled(true); // 미리보기는 클릭 방지
        if (e.getRawSlot() == 26) {
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (inv == null || !e.getInventory().equals(inv)) return;
        InventoryCloseEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
    }
}
