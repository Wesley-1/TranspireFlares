package transpiremining.transpiremining.Listeners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.Objects.Flare;
import transpiremining.transpiremining.Objects.Regen;
import transpiremining.transpiremining.TranspireFlares;
import transpiremining.transpiremining.Utilites.Util;
import transpiremining.transpiremining.tasks.ProgressTask;
import transpiremining.transpiremining.tasks.RegenTask;

public class BlockPlace implements Listener {

    TranspireFlares main = TranspireFlares.getPlugin(TranspireFlares.class);

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        System.out.println("AAAAAAA");

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();
        NBTItem nbtItem = new NBTItem(item);

        if (!nbtItem.hasKey("type"))
            return;

        String type = nbtItem.getString("type");

        Flare flare = FlareHandler.getFlareTypes().get(type);
        String mix = event.getBlock().getWorld().getName() + "," + event.getBlock().getLocation().getBlockX() + "," + event.getBlock().getLocation().getBlockY() + "," + event.getBlock().getLocation().getBlockZ();
        Location flareLoc = event.getBlock().getLocation();

        event.getBlock().setType(flare.getIndentifier());
        Plugin plugin = TranspireFlares.getInstance();
        Location loc = event.getBlock().getLocation().add(0.5, 2.6, 0.5);
        Hologram hologram = HologramsAPI.createHologram(plugin, loc);

        ItemLine itemLine = hologram.appendItemLine(new ItemStack(Material.ENDER_CHEST));
        TextLine textLine = hologram.appendTextLine("");
        TextLine textLine1 = hologram.appendTextLine("");
        TextLine progressBar = hologram.appendTextLine("");

        itemLine.setItemStack(new ItemStack(Material.ENDER_CHEST));
        textLine.setText(Util.toColor("&BBreak this flare to gain insane rewards!"));
        textLine1.setText(Util.toColor("&bThis is a &f" + flare.getName() + " &bflare."));
        progressBar.setText(Util.toColor("&c||||||||||||||||||| &7(0%)"));

        System.out.println("FFFFFFFFFFF");
        RegenTask.addHologram(loc, hologram);
        player.sendMessage(toColor(main.getConfig().getString("messages.flare-placed").replaceAll("%flaretype%", type)));
        main.getAllRegenLocs().put(mix + ":" + flare.getName(), flare);
        System.out.println("GGGGGGGGG");
        FlareHandler.addFlare(flareLoc, flare);
        System.out.println("ADDED FLARE");

    }

    public static String toColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
