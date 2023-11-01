package com.badbones69.crazycrates.paper.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.api.users.guis.InventoryManager;
import java.util.HashMap;
import java.util.UUID;

public class PreviewListener implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private static final CrazyHandler crazyHandler = plugin.getCrazyHandler();

    private static final InventoryManager inventoryManager = crazyHandler.getInventoryManager();

    private static final SettingsManager config = plugin.getConfigManager().getConfig();

    private static final HashMap<UUID, Crate> playerCrate = new HashMap<>();
    private static final HashMap<UUID, Boolean> playerInMenu = new HashMap<>();
    private static ItemStack menuButton;
    private static ItemBuilder nextButton;
    private static ItemBuilder backButton;

    public static void loadButtons() {
        menuButton = new ItemBuilder()
                .setMaterial(config.getProperty(Config.menu_button_item))
                .setName(config.getProperty(Config.menu_button_name))
                .setLore(config.getProperty(Config.menu_button_lore))
                .build();

        nextButton = new ItemBuilder()
                .setMaterial(config.getProperty(Config.next_button_item))
                .setName(config.getProperty(Config.next_button_name))
                .setLore(config.getProperty(Config.next_button_lore));
        backButton = new ItemBuilder()
                .setMaterial(config.getProperty(Config.back_button_item))
                .setName(config.getProperty(Config.back_button_name))
                .setLore(config.getProperty(Config.back_button_lore));
    }
    
    public static void openNewPreview(Player player, Crate crate) {
        playerCrate.put(player.getUniqueId(), crate);

        setPage(player, 1);
        player.openInventory(crate.getPreview(player));
    }

    public static void closePreview(Player player, InventoryCloseEvent.Reason reason) {
        playerCrate.remove(player.getUniqueId());
        player.closeInventory(reason);
    }

    public static void closePreview(Player player, Crate crate) {
        if (playerCrate.containsKey(player.getUniqueId())) {
            plugin.getCrazyHandler().getInventoryManager().addViewer(player.getUniqueId());

            crate.getPreview(player).close();

            playerCrate.remove(player.getUniqueId());
        }
    }

    public static boolean inPreview(Player player) {
        return playerCrate.containsKey(player.getUniqueId());
    }
    
    public static void openPreview(Player player, Crate crate) {
        playerCrate.put(player.getUniqueId(), crate);
        player.openInventory(crate.getPreview(player));
    }
    
    public static void setPage(Player player, int pageNumber) {
        int max = playerCrate.get(player.getUniqueId()).getMaxPage();

        if (pageNumber < 1) {
            pageNumber = 1;
        } else if (pageNumber >= max) {
            pageNumber = max;
        }

        playerPage.put(player.getUniqueId(), pageNumber);
    }
    
    public static ItemStack getMenuButton() {
        return menuButton;
    }
    
    public static ItemStack getNextButton(Player player) {
        ItemBuilder button = new ItemBuilder(nextButton);

        if (player != null) button.addLorePlaceholder("%Page%", (inventoryManager.getPage(player.getUniqueId()) + 1) + "");

        return button.build();
    }
    
    public static ItemStack getBackButton(Player player) {
        ItemBuilder button = new ItemBuilder(backButton);

        if (player != null) button.addLorePlaceholder("%Page%", (inventoryManager.getPage(player.getUniqueId()) - 1) + "");

        return button.build();
    }
    
    public static boolean playerInMenu(Player player) {
        return playerInMenu.getOrDefault(player.getUniqueId(), false);
    }
    
    public static void setPlayerInMenu(Player player, boolean inMenu) {
        playerInMenu.put(player.getUniqueId(), inMenu);
    }
    
    @EventHandler
    public void onPlayerClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        UUID uuid = player.getUniqueId();

        if (e.getClickedInventory() != null && playerCrate.get(player.getUniqueId()) != null) {
            Crate crate = playerCrate.get(player.getUniqueId());

            if (crate.isPreview(e.getView())) {
                e.setCancelled(true);

                if (e.getCurrentItem() != null) {
                    if (e.getRawSlot() == crate.getAbsoluteItemPosition(4)) { // Clicked the menu button.

                        if (playerInMenu(player)) MenuListener.openGUI(player);

                    } else if (e.getRawSlot() == crate.getAbsoluteItemPosition(5)) { // Clicked the next button.
                        if (inventoryManager.getPage(uuid) < crate.getMaxPage()) {
                            inventoryManager.nextPage(uuid);
                            openPreview(player, crate);
                        }

                    } else if (e.getRawSlot() == crate.getAbsoluteItemPosition(3)) { // Clicked the back button.
                        if (inventoryManager.getPage(uuid) > 1 && inventoryManager.getPage(uuid) <= crate.getMaxPage()) {
                            inventoryManager.backPage(uuid);
                            openPreview(player, crate);
                        }
                    }
                }
            }
        }
    }
}