package com.songoda.repairplus.Events;

import com.songoda.repairplus.Handlers.RepairHandler;
import com.songoda.repairplus.Lang;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.Utils.Debugger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

/**
 * Created by songoda on 2/25/2017.
 */
public class InventoryListeners implements Listener {

    @EventHandler
    public void OnPickup(InventoryPickupItemEvent event) {
        if (event.getItem().hasMetadata("RepairPlus"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        try {
            Player p = (Player) event.getWhoClicked();
            if (RepairPlus.getInstance().repair.player.containsKey(p.getName())) {
                event.setCancelled(true);
                Location loc = RepairPlus.getInstance().repair.anvilLoc.get(p.getName());
                if (event.getSlot() == 11) {
                    RepairPlus.getInstance().repair.finish("yes", p);
                    p.closeInventory();
                } else if (event.getSlot() == 15) {
                    RepairPlus.getInstance().repair.finish("no", p);
                    p.closeInventory();
                }
            } else if (event.getInventory().getTitle().equals(Lang.GUI_TITLE.getConfigValue(null))) {
                event.setCancelled(true);
                Location loc = RepairPlus.getInstance().repair.anvilLoc.get(p.getName());
                if (event.getSlot() == 11) {
                    p.closeInventory();
                    if (p.hasPermission("repairplus.use.ECO")) {
                        RepairPlus.getInstance().repair.preRepair(p, "ECO", loc);
                    }
                } else if (event.getSlot() == 13) {
                    p.closeInventory();
                    if (p.hasPermission("repairplus.use.ITEM")) {
                        RepairPlus.getInstance().repair.preRepair(p, "ITEM", loc);
                    }
                } else if (event.getSlot() == 15) {
                    p.closeInventory();
                    if (p.hasPermission("repairplus.use.XP")) {
                        RepairPlus.getInstance().repair.preRepair(p, "XP", loc);
                    }
                }
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}
