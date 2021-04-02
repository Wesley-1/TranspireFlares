package transpiremining.transpiremining.tasks;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import transpiremining.transpiremining.Objects.Flare;
import transpiremining.transpiremining.Objects.Regen;
import transpiremining.transpiremining.TranspireFlares;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/*
© Copyright TranspireDev 2021-2021. All Rights Reserved

This class and all its contents belongs to, and solely to Zlurpy and PlasmaMan916.
The copying of this class is prohibited.
 */

public class RegenTask extends BukkitRunnable {

    TranspireFlares main = TranspireFlares.getInstance();

    public RegenTask() {
        this.runTaskTimer(main, 0, 5);
    }

    private static Map<Location, Hologram> holograms = new ConcurrentHashMap<>();

    public static void deleteHologram(Location loc) {
        if (holograms.containsKey(loc)) {
            holograms.get(loc).delete();
            holograms.remove(loc);
        }
    }

    public static Boolean doesHologramExist(Location loc) {
        if (holograms.containsKey(loc)) {
            return true;
        } else {
            return false;
        }
    }

    public static Hologram getHologram(Location loc) {
        if (holograms.containsKey(loc)) {
            return holograms.get(loc);
        } else {
            return null;
        }
    }

    public static void addHologram(Location loc, Hologram holo) {
        holograms.put(loc, holo);
    }


//Where do you want the map
    // I want to delete it in the tmining class

    @Override
    public void run() {
        for (Regen regen : main.getCurrentRegens().keySet()) {
            Flare flare = regen.getOre();
            if (System.currentTimeMillis() >= regen.getEnd()) {
                regen.regen();
                Bukkit.broadcastMessage(TranspireFlares.translate("&cA &f" + regen.getOre().getName() + " &Cflare has respawned in the whiteout zone: &aX: " + regen.getBlock().getLocation().getX() + "&f,&aY: " + regen.getBlock().getLocation().getY() + "&f, &aZ: " + regen.getBlock().getLocation().getZ()));
            }
        }
    }
}
