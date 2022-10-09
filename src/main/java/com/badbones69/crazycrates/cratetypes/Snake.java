package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Random;

public class Snake implements Listener {
    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    private static final CrazyManager crazyManager = plugin.getCrazyManager();

    static BukkitTask task;
    public static void openSnake(Player player, Crate crate, KeyType keyType, boolean checkHand) {
        if (!crazyManager.takeKeys(1, player, crate, keyType, checkHand)) {
            Methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        final Inventory inv = plugin.getServer().createInventory(null, 45, crate.getCrateInventoryName());

        player.openInventory(inv);

        crazyManager.addCrateTask(player,task = new BukkitRunnable() {
            int round = 0;
            final Prize prize = crate.pickPrize(player);

            final ArrayList<Integer> path = getPath();


            @Override
            public void run() {

                if (round < 45) {
                    inv.setItem(path.get(round), randomPrize(crate));
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                if (round == 47) {
                    for (int i = 0; i < 45; i++) inv.setItem(i, new ItemStack(Material.RED_STAINED_GLASS_PANE, 1));
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                }

                if (round == 55) {
                    inv.setItem(22, prize.getDisplayItem());
                    crazyManager.givePrize(player, prize);
                    player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
                }

                if(round % 5 == 0) player.openInventory(inv);
                round++;

                if (round >= 90) {
                    task.cancel();
                    crazyManager.endCrate(player);
                    player.closeInventory();
                    crazyManager.givePrize(player, prize);

                    if (prize.useFireworks()) Methods.firework(player.getLocation().add(0, 1, 0));

                    plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
                    crazyManager.removePlayerFromOpeningList(player);

                }
            }
        }.runTaskTimer(plugin, 1, 1));

    }
    private static ItemStack randomPrize(Crate crate) {
        return crate.getPrizes().get(new Random().nextInt(crate.getPrizes().size())).getDisplayItem();
    }
    private static ArrayList<Integer> getPath() {
        ArrayList<Integer> slots = new ArrayList<>();

        //0-8  17-9  18-26 35-27 36-44
        // 0 8 17 26 35 44

        for(int i = 0; i <= 8; i++) slots.add(i);
        for(int i = 17; i >= 9; i--) slots.add(i);
        for(int i = 18; i <= 26; i++) slots.add(i);
        for(int i = 35; i >= 27; i--) slots.add(i);
        for(int i = 36; i <= 44; i++) slots.add(i);

        return slots;
    }
}
