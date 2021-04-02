package transpiremining.transpiremining.Objects;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.TranspireFlares;
import transpiremining.transpiremining.Utilites.Util;
import transpiremining.transpiremining.tasks.RegenTask;

public class Regen {

    private long end;
    private long start;
    private Block block;
    private Location loc;
    private Flare flare;
    private boolean regenerated = false;

    public Regen(Block block, Flare flare, long start, long end) {

        System.out.println("Created Regen!");
        this.start = start;
        this.end = end;
        this.block = block;
        this.loc = block.getLocation();
        this.flare = flare;
    }

    public Flare getOre() {
        return flare;
    }

    public long getStart() {
        return start;
    }
    public long getEnd() {
        return end;
    }
    public Block getBlock() {
        return block;
    }

    public Location getLoc() {
        return loc;
    }


    public void regen(){
        if(regenerated){
            return;
        }
        regenerated = true;
        Location loc = this.getBlock().getLocation();
        this.getBlock().getWorld().strikeLightningEffect(loc);
        this.block.setType(this.flare.getIndentifier());
        FlareHandler.addFlare(this.block.getLocation(),this.flare);
        String mix = this.block.getWorld().getName() + "," + this.block.getLocation().getBlockX() + "," + this.block.getLocation().getBlockY() + "," + this.block.getLocation().getBlockZ();
        Location flareLoc = this.getBlock().getLocation();

        Plugin plugin = TranspireFlares.getInstance();
        Location holoLoc = this.block.getLocation().add(0.5, 2.6, 0.5);
        Hologram hologram = HologramsAPI.createHologram(plugin, holoLoc);

        ItemLine itemLine = hologram.appendItemLine(new ItemStack(Material.ENDER_CHEST));
        TextLine textLine = hologram.appendTextLine("");
        TextLine textLine1 = hologram.appendTextLine("");
        TextLine progressBar = hologram.appendTextLine("");

        itemLine.setItemStack(new ItemStack(Material.ENDER_CHEST));
        textLine.setText(Util.toColor("&BBreak this flare to gain insane rewards!"));
        textLine1.setText(Util.toColor("&bThis is a &f" + flare.getName() + " &bflare."));
        progressBar.setText(Util.toColor("&c||||||||||||||||||| &7(0%)"));

        RegenTask.addHologram(holoLoc, hologram);
        FlareHandler.addFlare(flareLoc, flare);

        TranspireFlares.getInstance().getCurrentRegens().remove(this);
    }

}
