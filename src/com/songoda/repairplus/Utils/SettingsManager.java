package com.songoda.repairplus.Utils;

import com.songoda.arconix.Arconix;
import com.songoda.repairplus.RepairPlus;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by songo on 6/4/2017.
 */
public class SettingsManager implements Listener {

    private static ConfigWrapper defs;

    public SettingsManager() {
        RepairPlus.getInstance().saveResource("SettingDefinitions.yml", true);
        defs = new ConfigWrapper(RepairPlus.getInstance(), "", "SettingDefinitions.yml");
        defs.createNewFile("Loading data file", "RepairPlus SettingDefinitions file");
        RepairPlus.getInstance().getServer().getPluginManager().registerEvents(this, RepairPlus.getInstance());
    }

    public Map<Player, String> current = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getInventory().getTitle().equals("RepairPlus Settings Editor")) {
                e.setCancelled(true);

                String key = e.getCurrentItem().getItemMeta().getDisplayName().substring(2);

                Player p = (Player) e.getWhoClicked();

                if (RepairPlus.getInstance().getConfig().get("settings." + key).getClass().getName().equals("java.lang.Boolean")) {
                    boolean bool = (Boolean) RepairPlus.getInstance().getConfig().get("settings." + key);
                    if (!bool)
                        RepairPlus.getInstance().getConfig().set("settings." + key, true);
                    else
                        RepairPlus.getInstance().getConfig().set("settings." + key, false);
                    finishEditing(p);
                } else {
                    editObject(p, key);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (current.containsKey(p)) {
            if (RepairPlus.getInstance().getConfig().get("settings." + current.get(p)).getClass().getName().equals("java.lang.Integer")) {
                RepairPlus.getInstance().getConfig().set("settings." + current.get(p), Integer.parseInt(e.getMessage()));
            } else if (RepairPlus.getInstance().getConfig().get("settings." + current.get(p)).getClass().getName().equals("java.lang.Double")) {
                RepairPlus.getInstance().getConfig().set("settings." + current.get(p), Double.parseDouble(e.getMessage()));
            } else if (RepairPlus.getInstance().getConfig().get("settings." + current.get(p)).getClass().getName().equals("java.lang.String")) {
                RepairPlus.getInstance().getConfig().set("settings." + current.get(p), e.getMessage());
            }
            finishEditing(p);
            e.setCancelled(true);
        }
    }

    public void finishEditing(Player p) {
        current.remove(p);
        RepairPlus.getInstance().saveConfig();
        openEditor(p);
    }


    public void editObject(Player p, String current) {
        this.current.put(p, current);
        p.closeInventory();
        p.sendMessage("");
        p.sendMessage(Arconix.pl().format().formatText("&7Please enter a value for &6"+current+"&7."));
        if (RepairPlus.getInstance().getConfig().get("settings." + current).getClass().getName().equals("java.lang.Integer")) {
            p.sendMessage(Arconix.pl().format().formatText("&cUse only numbers."));
        }
        p.sendMessage("");
    }

    public static void openEditor(Player p) {
        RepairPlus plugin = RepairPlus.pl();
        Inventory i = Bukkit.createInventory(null, 54, "RepairPlus Settings Editor");

        int num = 0;
        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("settings");
        for (String key : cs.getKeys(true)) {

            ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Arconix.pl().format().formatText("&6"+key));
            ArrayList<String> lore = new ArrayList<>();
            if (plugin.getConfig().get("settings." + key).getClass().getName().equals("java.lang.Boolean")) {

                item.setType(Material.LEVER);
                boolean bool = (Boolean) plugin.getConfig().get("settings." + key);

                if (!bool)
                    lore.add(Arconix.pl().format().formatText("&c" + bool));
                else
                    lore.add(Arconix.pl().format().formatText("&a" + bool));

            } else if (plugin.getConfig().get("settings." + key).getClass().getName().equals("java.lang.String")) {
                item.setType(Material.PAPER);
                String str = (String) plugin.getConfig().get("settings." + key);
                lore.add(Arconix.pl().format().formatText("&9" + str));
            } else if (plugin.getConfig().get("settings." + key).getClass().getName().equals("java.lang.Integer")) {
                item.setType(Material.WATCH);

                int in = (Integer) plugin.getConfig().get("settings." + key);
                lore.add(Arconix.pl().format().formatText("&5" + in));
            }
            if (defs.getConfig().contains(key)) {
                String text = defs.getConfig().getString(key);
                int index = 0;
                while (index < text.length()) {
                    lore.add(Arconix.pl().format().formatText("&7" + text.substring(index, Math.min(index + 50,text.length()))));
                    index += 50;
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);

            i.setItem(num, item);
            num++;
        }
        p.openInventory(i);
    }
}
