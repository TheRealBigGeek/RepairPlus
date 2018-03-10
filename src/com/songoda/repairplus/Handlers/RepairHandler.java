package com.songoda.repairplus.Handlers;

import com.songoda.arconix.Arconix;
import com.songoda.repairplus.Lang;
import com.songoda.repairplus.RepairPlus;
import com.songoda.repairplus.Utils.Debugger;
import com.songoda.repairplus.Utils.Methods;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by songoda on 2/25/2017.
 */
public class RepairHandler implements Listener {

    public Map<String, Location> anvilLoc = new HashMap<>();
    public Map<String, Integer> price = new HashMap<>();
    public Map<String, String> type = new HashMap<>();
    public Map<String, Item> item = new HashMap<>();
    public Map<String, ItemStack> player = new HashMap<>();
    public Map<String, Location> locations = new HashMap<>();

    public boolean RepairPlus() {
        return true;
    }

    public void repairType(Player p) {
        try {
            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().format().formatTitle(Lang.GUI_TITLE.getConfigValue()));

            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }

            ItemStack item = new ItemStack(Material.valueOf(RepairPlus.getInstance().getConfig().getString("settings.ECO-Icon")), 1);
            ItemMeta itemmeta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Lang.ECO_LORE.getConfigValue());
            itemmeta.setLore(lore);
            itemmeta.setDisplayName(Lang.ECO.getConfigValue());
            item.setItemMeta(itemmeta);


            Material mat = Methods.getType(p.getItemInHand());

            ItemStack item3 = new ItemStack(mat, 1);
            String name = (mat.name().substring(0, 1).toUpperCase() + mat.name().toLowerCase().substring(1)).replace("_", " ");
            ItemMeta itemmeta3 = item3.getItemMeta();
            ArrayList<String> lore3 = new ArrayList<>();
            lore3.add(Lang.ITEM_LORE.getConfigValue(name));
            itemmeta3.setLore(lore3);
            itemmeta3.setDisplayName(Lang.ITEM.getConfigValue(name));
            item3.setItemMeta(itemmeta3);

            ItemStack item2 = new ItemStack(Material.valueOf(RepairPlus.getInstance().getConfig().getString("settings.XP-Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            ArrayList<String> lore2 = new ArrayList<>();
            lore2.add(Lang.XP_LORE.getConfigValue(item3.toString()));
            itemmeta2.setLore(lore2);
            itemmeta2.setDisplayName(Lang.XP.getConfigValue());
            item2.setItemMeta(itemmeta2);

            if (p.hasPermission("repairplus.use.ECO")) {
                i.setItem(11, item);
            }
            if (p.hasPermission("repairplus.use.ITEM")) {
                i.setItem(13, item3);
            }
            if (p.hasPermission("repairplus.use.XP")) {
                i.setItem(15, item2);
            }

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

            p.openInventory(i);
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public void preRepair(Player p, String typ, Location loc) {
        try {
            if (loc.add(0, 1, 0).getBlock().getTypeId() == 0) {
                if (p.getItemInHand().getDurability() > 0) {
                    if (p.getItemInHand().getMaxStackSize() == 1) {
                        if (!player.containsKey(p.getName())) {

                            Item i = p.getWorld().dropItem(loc.add(0.5, 2, 0.5), p.getItemInHand());
                            i.setMetadata("grabbed", new FixedMetadataValue(RepairPlus.getInstance(), "true"));
                            i.setMetadata("betterdrops_ignore", new FixedMetadataValue(RepairPlus.getInstance(), true));
                            Vector vec = p.getEyeLocation().getDirection();
                            vec.setX(0);
                            vec.setY(0);
                            vec.setZ(0);
                            i.setVelocity(vec);
                            i.setPickupDelay(3600);
                            i.setMetadata("RepairPlus", new FixedMetadataValue(RepairPlus.getInstance(), ""));
                            item.put(p.getName(), i);
                            player.put(p.getName(), p.getItemInHand());
                            locations.put(p.getName(), loc.add(0, -2, 0));

                            yesNo(p, typ, p.getItemInHand());


                            p.setItemInHand(null);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> {
                                if (player.containsKey(p.getName())) {
                                    p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.TIME_OUT.getConfigValue()));
                                    removeItem(p);
                                    p.closeInventory();
                                }
                            }, RepairPlus.getInstance().getConfig().getLong("settings.Timeout"));
                        } else {
                            p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.ALREADY_REPAIRING.getConfigValue()));
                        }
                    } else {
                        p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.CANT_REPAIR.getConfigValue()));
                    }
                } else {
                    p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NOT_DAMAGED.getConfigValue()));
                }
            } else {
                p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NEED_SPACE.getConfigValue()));
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void initRepair(Player p, Location location) {
        int num = 0;
        if (p.hasPermission("repairplus.use.ECO"))
            num ++;
        if (p.hasPermission("repairplus.use.XP"))
            num ++;
        if (p.hasPermission("repairplus.use.ITEM"))
            num ++;

        if (num >= 2 || p.hasPermission("repairplus.use.*")) {
            RepairPlus.getInstance().repair.repairType(p);
            RepairPlus.getInstance().repair.anvilLoc.put(p.getName(), location);
        } else if (p.hasPermission("repairplus.use.eco")) {
            RepairPlus.getInstance().repair.preRepair(p, "ECO", location);
        } else if (p.hasPermission("repairplus.use.XP")) {
            RepairPlus.getInstance().repair.preRepair(p, "XP", location);
        } else if (p.hasPermission("repairplus.use.ITEM")) {
            RepairPlus.getInstance().repair.preRepair(p, "ITEM", location);
        }
    }

    public void finish(String answer, Player p) {
        try {
            if (answer == "yes") {
                boolean economy;
                boolean sold = false;
                if (RepairPlus.getInstance().getServer().getPluginManager().getPlugin("Vault") != null && type.get(p.getName()) == "ECO") {
                    RegisteredServiceProvider<Economy> rsp = RepairPlus.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                    net.milkbowl.vault.economy.Economy econ = rsp.getProvider();
                    if (econ.has(p, price.get(p.getName()))) {
                        econ.withdrawPlayer(p, price.get(p.getName()));
                        sold = true;
                    }
                    economy = true;
                } else {
                    economy = false;
                }

                int cost = Methods.getCost(type.get(p.getName()), player.get(p.getName()));
                ItemStack item2 = new ItemStack(Methods.getType(player.get(p.getName())), cost);
                String name = (item2.getType().name().substring(0, 1).toUpperCase() + item2.getType().name().toLowerCase().substring(1)).replace("_", " ");
                if (type.get(p.getName()) == "ITEM") {
                    if (Arconix.pl().getGUI().inventoryContains(p.getInventory(), item2)) {
                        Arconix.pl().getGUI().removeFromInventory(p.getInventory(), item2);
                        sold = true;
                    }
                }

                if (type.get(p.getName()) == "XP" && p.getLevel() >= price.get(p.getName()) || sold == true || p.getGameMode() == GameMode.CREATIVE) {
                    p.getWorld().playEffect(locations.get(p.getName()), Effect.STEP_SOUND, 152);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> p.getWorld().playEffect(locations.get(p.getName()), Effect.STEP_SOUND, 152), 5L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> {
                        p.getWorld().playEffect(locations.get(p.getName()), Effect.STEP_SOUND, 152);
                        p.getWorld().playEffect(locations.get(p.getName()), Effect.STEP_SOUND, 1);
                        if (RepairPlus.getInstance().v1_8) {
                            Arconix.pl().getPlayer(p).playSound(Sound.valueOf("ANVIL_LAND"));
                        } else {
                            Arconix.pl().getPlayer(p).playSound(Sound.valueOf("BLOCK_ANVIL_LAND"));
                        }
                    }, 10L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> p.getWorld().playEffect(locations.get(p.getName()), Effect.STEP_SOUND, 152), 15L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> p.getWorld().playEffect(locations.get(p.getName()), Effect.STEP_SOUND, 152), 20L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> {
                        if (RepairPlus.getInstance().v1_8 || RepairPlus.getInstance().v1_7) {
                            Arconix.pl().getPlayer(p).playSound(Sound.valueOf("ANVIL_LAND"));
                        } else {
                            Arconix.pl().getPlayer(p).playSound(Sound.valueOf("BLOCK_ANVIL_LAND"));
                        }
                        p.getWorld().playEffect(locations.get(p.getName()), Effect.STEP_SOUND, 152);
                        p.getWorld().playEffect(locations.get(p.getName()), Effect.STEP_SOUND, 145);
                        p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.SUCCESS.getConfigValue()));
                        ItemStack repairedi = player.get(p.getName());
                        repairedi.setDurability((short) 0);
                        Item repaired = p.getWorld().dropItemNaturally(p.getLocation(), repairedi);
                        repaired.remove();
                        p.getInventory().addItem(player.get(p.getName()));
                        item.get(p.getName()).remove();
                        if (p.getGameMode() != GameMode.CREATIVE) {
                            if (type.get(p.getName()) == "XP") {
                                p.setLevel(p.getLevel() - price.get(p.getName()));
                            }
                        }
                        price.remove(p.getName());
                        item.remove(p.getName());
                        player.remove(p.getName());
                        type.remove(p.getName());
                    }, 25L);
                } else {
                    if (type.get(p.getName()).equals("ECO")) {
                        if (!economy) {
                            p.sendMessage("Vault is not installed.");
                        } else {
                            p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NOT_ENOUGH.getConfigValue(Lang.ECO.getConfigValue())));
                        }
                    } else if (type.get(p.getName()).equals("XP")) {
                        p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NOT_ENOUGH.getConfigValue(Lang.XP.getConfigValue())));
                    } else {
                        p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.NOT_ENOUGH.getConfigValue(name)));
                    }
                    removeItem(p);
                }
            } else {
                p.sendMessage(Arconix.pl().format().formatText(RepairPlus.getInstance().references.getPrefix() + Lang.CANCELLED.getConfigValue()));
                removeItem(p);
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }


    public void yesNo(Player p, String typ, ItemStack item) {
        try {
            int price2 = Methods.getCost(typ, item);
            String cost;

            Material mat = new Methods().getType(item);
            String name = (mat.name().substring(0, 1).toUpperCase() + mat.name().toLowerCase().substring(1));

            if (typ == "XP")
                cost = price2 + " XP";
            else if (typ == "ECO")
                cost = Lang.ECO_GUI.getConfigValue(Integer.toString(price2));
            else
                cost = price2 + " " + name;

            Inventory i = Bukkit.createInventory(null, 27, Arconix.pl().format().formatTitle(Lang.GUI_TITLE_YESNO.getConfigValue(cost)));


            int nu = 0;
            while (nu != 27) {
                i.setItem(nu, Methods.getGlass());
                nu++;
            }

            ItemStack item2 = new ItemStack(Material.valueOf(RepairPlus.getInstance().getConfig().getString("Buy-Icon")), 1);
            ItemMeta itemmeta2 = item2.getItemMeta();
            itemmeta2.setDisplayName(Lang.YES_GUI.getConfigValue());
            item2.setItemMeta(itemmeta2);

            ItemStack item3 = new ItemStack(Material.valueOf(RepairPlus.getInstance().getConfig().getString("Exit-Icon")), 1);
            ItemMeta itemmeta3 = item3.getItemMeta();
            itemmeta3.setDisplayName(Lang.NO_GUI.getConfigValue());
            item3.setItemMeta(itemmeta3);

            i.setItem(4, item);
            i.setItem(11, item2);
            i.setItem(15, item3);

            Bukkit.getScheduler().scheduleSyncDelayedTask(RepairPlus.getInstance(), () -> p.openInventory(i), 1);

            type.put(p.getName(), typ);
            price.put(p.getName(), price2);

            i.setItem(0, Methods.getBackgroundGlass(true));
            i.setItem(1, Methods.getBackgroundGlass(true));
            i.setItem(2, Methods.getBackgroundGlass(false));
            i.setItem(6, Methods.getBackgroundGlass(false));
            i.setItem(7, Methods.getBackgroundGlass(true));
            i.setItem(8, Methods.getBackgroundGlass(true));
            i.setItem(9, Methods.getBackgroundGlass(true));
            i.setItem(10, Methods.getBackgroundGlass(false));
            i.setItem(16, Methods.getBackgroundGlass(false));
            i.setItem(17, Methods.getBackgroundGlass(true));
            i.setItem(18, Methods.getBackgroundGlass(true));
            i.setItem(19, Methods.getBackgroundGlass(true));
            i.setItem(20, Methods.getBackgroundGlass(false));
            i.setItem(24, Methods.getBackgroundGlass(false));
            i.setItem(25, Methods.getBackgroundGlass(true));
            i.setItem(26, Methods.getBackgroundGlass(true));

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public void removeItem(Player p) {
        try {
            p.getInventory().addItem(player.get(p.getName()));
            item.get(p.getName()).remove();
            price.remove(p.getName());
            item.remove(p.getName());
            player.remove(p.getName());
            type.remove(p.getName());
            p.closeInventory();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

}
