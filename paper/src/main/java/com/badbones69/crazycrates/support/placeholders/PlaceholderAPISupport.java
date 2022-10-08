package com.badbones69.crazycrates.support.placeholders;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.CrateType;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.support.libs.PluginSupport;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player.isOnline()) {
            Player playerOnline = (Player) player;

            for (Crate crate : crazyManager.getCrates()) {
                if (crate.getCrateType() != CrateType.MENU) {
                    if (identifier.equalsIgnoreCase(crate.getName())) {
                        return NumberFormat.getNumberInstance().format(crazyManager.getVirtualKeys(playerOnline, crate));
                    } else if (identifier.equalsIgnoreCase(crate.getName() + "_physical")) {
                        return NumberFormat.getNumberInstance().format(crazyManager.getPhysicalKeys(playerOnline, crate));
                    } else if (identifier.equalsIgnoreCase(crate.getName() + "_total")) {
                        return NumberFormat.getNumberInstance().format(crazyManager.getTotalKeys(playerOnline, crate));
                    }
                }
            }
        }

        return "";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "crazycrates";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "BadBones69";
    }
    
    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public static ItemStack buildItemWithPlaceholders(Player player, Prize prize) {

        ItemStack item = prize.getDisplayItem();
        if (!PluginSupport.PLACEHOLDERAPI.isPluginLoaded()) return item;


        List<String> newLore = new ArrayList<>();
        ItemMeta newMeta = item.getItemMeta();

        newMeta.setDisplayName(PlaceholderAPI.setPlaceholders(player, item.getItemMeta().getDisplayName()));

        if (item.getItemMeta().hasLore()) {
            for (String s : item.getLore()) {
                newLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }
        newMeta.setLore(newLore);
        item.setItemMeta(newMeta);

        return item;
    }


}

