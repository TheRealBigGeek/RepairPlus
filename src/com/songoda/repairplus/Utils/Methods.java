package com.songoda.repairplus.Utils;

import com.songoda.arconix.Arconix;
import com.songoda.repairplus.RepairPlus;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by songoda on 2/25/2017.
 */
public class Methods {

    public static ItemStack getGlass() {
        try {
            RepairPlus plugin = RepairPlus.pl();
            return Arconix.pl().getGUI().getGlass(plugin.getConfig().getBoolean("settings.Rainbow-Glass"), plugin.getConfig().getInt("settings.Glass-Type-1"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        try {
            RepairPlus plugin = RepairPlus.pl();
            if (type)
                return Arconix.pl().getGUI().getGlass(false, plugin.getConfig().getInt("settings.Glass-Type-2"));
            else
                return Arconix.pl().getGUI().getGlass(false, plugin.getConfig().getInt("settings.Glass-Type-3"));
        } catch (Exception e) {
            Debugger.runReport(e);
        }
        return null;
    }

    public static int getCost(String type, ItemStack item) {
        try {

            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");

            String equationXP = RepairPlus.getInstance().getConfig().getString("settings.XP-Cost-Equation");
            String equationECO = RepairPlus.getInstance().getConfig().getString("settings.ECO-Cost-Equation");
            String equationITEM = RepairPlus.getInstance().getConfig().getString("settings.ITEM-Cost-Equation");

            equationXP = equationXP.replace("{MaxDurability}", Short.toString(item.getType().getMaxDurability()));
            equationXP = equationXP.replace("{Durability}", Short.toString(item.getDurability()));
            int XPCost = (int)Math.round(Double.parseDouble(engine.eval(equationXP).toString()));

            equationECO = equationECO.replace("{MaxDurability}", Short.toString(item.getType().getMaxDurability()));
            equationECO = equationECO.replace("{Durability}", Short.toString(item.getDurability()));
            equationECO = equationECO.replace("{XPCost}", Integer.toString(XPCost));

            int ECOCost = (int)Math.round(Double.parseDouble(engine.eval(equationECO).toString()));

            equationITEM = equationITEM.replace("{MaxDurability}", Short.toString(item.getType().getMaxDurability()));
            equationITEM = equationITEM.replace("{Durability}", Short.toString(item.getDurability()));
            equationITEM = equationITEM.replace("{XPCost}", Integer.toString(XPCost));

            int ITEMCost = (int)Math.round(Double.parseDouble(engine.eval(equationITEM).toString()));

            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasEnchants()) {
                    int multi = RepairPlus.getInstance().getConfig().getInt("settings.Enchanted-Item-Multiplier");
                    XPCost = XPCost * multi;
                    ECOCost = ECOCost * multi;
                    ITEMCost = ITEMCost * multi;
                }
            }

            if (type.equals("XP")) {
                return XPCost;
            } else if (type.equals("ITEM")) {
                return ITEMCost;
            } else {
                return ECOCost;
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return 9999999;
    }

    public static Material getType(ItemStack item) {
        RepairPlus plugin = RepairPlus.pl();
        if (RepairPlus.getInstance().getConfig().getBoolean("settings.Item-Match-Type")) {
            if (item.getType().name().contains("DIAMOND"))
                return Material.DIAMOND;
            if (item.getType().name().contains("IRON"))
                return Material.IRON_INGOT;
            if (item.getType().name().contains("GOLD"))
                return Material.GOLD_INGOT;
            if (item.getType().name().contains("LEATHER"))
                return Material.LEATHER;
            if (item.getType().name().contains("STONE"))
                return Material.STONE;
            if (item.getType().name().contains("WOOD"))
                return Material.WOOD;
        }
        return Material.valueOf(RepairPlus.getInstance().getConfig().getString("settings.ITEM"));
    }
}
