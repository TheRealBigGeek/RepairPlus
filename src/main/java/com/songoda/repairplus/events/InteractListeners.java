package com.songoda.repairplus.events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by songoda on 2/25/2017.
 */

public class InteractListeners implements Listener {

    private final RepairPlus instance;

    public InteractListeners(RepairPlus instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onAnvilClick(PlayerInteractEvent e) {
        try {
            boolean repair = false;
            boolean anvil = false;
            Player p = e.getPlayer();
            if (e.getClickedBlock() == null) {
                return;
            }
            String loc = Arconix.pl().getApi().serialize().serializeLocation(e.getClickedBlock());
            if (e.getClickedBlock().getType() != Material.ANVIL
                    || !((instance.getConfig().getBoolean("data.anvil." + loc + ".permPlaced")
                    || !instance.getConfig().getBoolean("settings.Perms-Only")))) {
                return;
            }
            if (instance.getConfig().getString("data.anvil." + loc + ".inf") != null) {
                e.getClickedBlock().setType(Material.AIR);
                e.getClickedBlock().setType(Material.ANVIL); //ToDO: This may not work.
            }
            if (!instance.getConfig().getBoolean("settings.Enable-Default-Anvil-Function") && !p.isSneaking())
                e.setCancelled(true);
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK &&
                    !p.isSneaking()) {
                if (instance.getConfig().getBoolean("settings.Swap-Functions"))
                    repair = true;
                else if (!instance.getConfig().getBoolean("settings.Enable-Default-Anvil-Function"))
                    repair = true;
            }
            if (e.getAction() == Action.LEFT_CLICK_BLOCK &&
                    !p.isSneaking()) {
                if (instance.getConfig().getBoolean("settings.Swap-Functions")) {
                    if (instance.getConfig().getBoolean("settings.Enable-Default-Anvil-Function"))
                        anvil = true;
                    else
                        repair = true;
                } else
                    repair = true;
            }
            if (repair) {
                instance.repair.initRepair(p, e.getClickedBlock().getLocation());
                e.setCancelled(true);
            } else if (anvil && instance.getConfig().getBoolean("settings.Enable-Default-Anvil-Function")) {
                Inventory inv = Bukkit.createInventory(null, InventoryType.ANVIL, "Repair & Name");
                p.openInventory(inv);
                e.setCancelled(true);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }
}