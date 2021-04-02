package transpiremining.transpiremining;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import transpiremining.transpiremining.Files.FlaresYml;
import transpiremining.transpiremining.Objects.DataFile;
import transpiremining.transpiremining.Objects.Flare;
import transpiremining.transpiremining.Objects.Regen;
import transpiremining.transpiremining.TranspireFlares;
import transpiremining.transpiremining.Utilites.Util;
import transpiremining.transpiremining.tasks.RegenTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
© Copyright TranspireDev 2021-2021. All Rights Reserved

This class and all its contents belongs to, and solely to Zlurpy and PlasmaMan916.
The copying of this class is prohibited.
 */

public class FlareHandler extends Thread {
    private static FlaresYml flaresYml;
    private static Map<String, Flare> flareTypes = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ArrayList<String>> flareLocs = new ConcurrentHashMap<>();
    private static Map<String, Flare> locToFlare = new ConcurrentHashMap<>();//This is somehow null

    public static void loadFlaresYml() {
    }

    public static FileConfiguration getFlaresYml() {
        return flaresYml.getConfig();
    }

    public static Map<String, Flare> getLocToFlare() {
        return locToFlare;
    }

    protected static void loadFlares() {
        flaresYml = new FlaresYml(TranspireFlares.getInstance());
        for (String string : getFlaresYml().getConfigurationSection("flares").getKeys(false)) {
            long regenTime = getFlaresYml().getLong("flares." + string + ".regeneration-time");
            boolean permissionBased = getFlaresYml().getBoolean("flares." + string + ".permission-based");
            String permission = getFlaresYml().getString("flares." + string + ".permission");
            int commandAmount = getFlaresYml().getInt("flares." + string + ".amount-given");
            ArrayList<String> commands = (ArrayList<String>) getFlaresYml().getStringList("flares." + string + ".commands");
            double hardness = getFlaresYml().getDouble("flares." + string + ".hardness");
            String particleEffect = getFlaresYml().getString("flares." + string + ".particle-effect");
            boolean particleBased = getFlaresYml().getBoolean("flares." + string + ".particle-based");
            Material indentifier = Material.valueOf(getFlaresYml().getString("flares." + string + ".identifier.material"));
            ItemStack placeableItem = TranspireFlares.getUtil().createItem( string + ".item");
            NBTItem nbtItem = new NBTItem(placeableItem);
            nbtItem.setString("type", string);

            Flare flare = new Flare(string, hardness, nbtItem.getItem(), indentifier, commands, commandAmount, permissionBased, permission, regenTime, particleEffect, particleBased);

            flareTypes.put(string, flare);
//            new DataFile(TranspireFlares.getInstance(), string);

            TranspireFlares tf = TranspireFlares.getInstance();
            File file = new File(tf.getDataFolder(), string + "Locs.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            for(String s : config.getStringList("locs")){
                locToFlare.put(s,flare);

                ArrayList<String> oldData = new ArrayList<>();
                if(flareLocs.containsKey(flare.getName())){
                    oldData = flareLocs.get(flare.getName());
                    flareLocs.remove(flare.getName());
                }
                oldData.add(s);
                flareLocs.put(flare.getName(),oldData);
                TranspireFlares.getInstance().getAllRegenLocs().put(s + ":" + flare.getName(), flare);

                String[] data = s.split(",");
                World w = Bukkit.getWorld(data[0]);
                Double x = Double.valueOf(data[1]);
                Double y = Double.valueOf(data[2]);
                Double z = Double.valueOf(data[3]);

                Location blockLocation = new Location(w,x,y,z);
                blockLocation.getBlock().setType(flare.getIndentifier());

                Location location = new Location(w,x+0.5,y+2.6,z+0.5);
                Hologram hologram = HologramsAPI.createHologram(TranspireFlares.getInstance(), location);

                ItemLine itemLine = hologram.appendItemLine(new ItemStack(Material.ENDER_CHEST));
                TextLine textLine = hologram.appendTextLine("");
                TextLine textLine1 = hologram.appendTextLine("");
                TextLine progressBar = hologram.appendTextLine("");

                itemLine.setItemStack(new ItemStack(Material.ENDER_CHEST));
                textLine.setText(Util.toColor("&BBreak this flare to gain insane rewards!"));
                textLine1.setText(Util.toColor("&bThis is a &f" + flare.getName() + " &bflare."));
                progressBar.setText(Util.toColor("&c||||||||||||||||||| &7(0%)"));

                RegenTask.addHologram(location, hologram);
            }
        }

    }

    protected static void doSave() throws IOException {
        saveRegenLocations();
        flaresYml.save();
    }


    public static void addFlare(Location loc, Flare flare) {
        String mix = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        locToFlare.put(mix, flare);
        ArrayList<String> oldData = new ArrayList<>();
        if(flareLocs.containsKey(flare.getName())) {
            oldData = flareLocs.get(flare.getName());
            flareLocs.remove(flare.getName());
        }
        if(!oldData.contains(mix)) {
            oldData.add(mix);
        }
        flareLocs.put(flare.getName(),oldData);
    }

    private static void saveRegenLocations() throws IOException {
        TranspireFlares tf = TranspireFlares.getInstance();
//        for (Regen r : tf.getCurrentRegens().keySet()) {
//            Flare flare = tf.getCurrentRegens().get(r);
//            r.getBlock().getLocation().getBlock().setType(flare.getIndentifier());
//            System.out.println(r.getBlock().getType() + " : " + flare.getIndentifier().toString());
//        }

        for (Regen regen : TranspireFlares.getInstance().getCurrentRegens().keySet()) {
            regen.regen();
        }
        for (String string : getFlareLocs().keySet()) {
            File file = new File(tf.getDataFolder(), string + "Locs.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            Flare flare = flareTypes.get(string);


            ArrayList<String> locs = getFlareLocs().get(string);
            config.set("locs", locs);
            config.save(file);
        }

    }

    public static void removeFlare(Location loc, String mix){//Issue lies here or
        Location holoLoc = loc.add(0.5, 2.6, 0.5);
        if(RegenTask.doesHologramExist(holoLoc)){
            RegenTask.deleteHologram(holoLoc);
        }
        if(locToFlare.containsKey(mix)){
            locToFlare.remove(mix);
        }
        for(String s : flareLocs.keySet()){
            if(flareLocs.get(s).contains(mix)){
                ArrayList<String> stuff = flareLocs.get(s);
                flareLocs.remove(s);
                stuff.remove(mix);
                flareLocs.put(s,stuff);
            }
        }
        TranspireFlares.getInstance().removeRegen(loc,mix);//Here
    }

    public static Map<String, ArrayList<String>> getFlareLocs() {
        return flareLocs;
    }

    public static Map<String, Flare> getFlareTypes() {
        return flareTypes;
    }

    @Override
    public void run() {
        super.run();
    }
}
