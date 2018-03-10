package com.songoda.repairplus.Events;

import com.songoda.arconix.Arconix;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.Utils.Debugger;
import com.songoda.repairplus.Utils.Methods;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by songoda on 2/25/2017.
 */
public class BlockListeners implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        try {
            if (e.getPlayer().hasPermission("repairplus.permPlace")) {
                if (e.getBlockPlaced().getType().equals(Material.ANVIL)) {
                    String loc = Arconix.pl().serialize().serializeLocation(e.getBlock());
                    RepairPlus.getInstance().getConfig().set("data.anvil." + loc + ".permPlaced", true);
                }
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        try {
            Block b = e.getBlock();
            String loc = Arconix.pl().serialize().serializeLocation(b);
            if (RepairPlus.getInstance().getConfig().contains("data.anvil." + loc)) {
                RepairPlus.getInstance().getConfig().set("data.anvil." + loc + ".holo", null);
                RepairPlus.getInstance().getConfig().set("data.anvil." + loc, null);
                RepairPlus.getInstance().holo.updateHolograms(true);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}
