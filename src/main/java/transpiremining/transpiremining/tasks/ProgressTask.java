package transpiremining.transpiremining.tasks;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import transpiremining.transpiremining.CustomEvents.TMiningEvent;
import transpiremining.transpiremining.Files.FlaresYml;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.Listeners.TMining;
import transpiremining.transpiremining.Objects.Flare;
import transpiremining.transpiremining.TranspireFlares;
import transpiremining.transpiremining.Utilites.Util;
import transpiremining.transpiremining.packets.PacketHandler;
import transpiremining.transpiremining.packets.PlayerPacketHandler;
import transpiremining.transpiremining.packets.VoidChannelDuplexHandler;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
© Copyright TranspireDev 2021-2021. All Rights Reserved

This class and all its contents belongs to, and solely to Zlurpy and PlasmaMan916.
The copying of this class is prohibited.
 */

public class ProgressTask extends Thread {

    private static Map<BlockPosition, Hologram> blocks = new ConcurrentHashMap<>();
    private static Map<BlockPosition, Double> blockProgress = new ConcurrentHashMap<>();
    private static Map<BlockPosition, Double> oldBlockProgress = new ConcurrentHashMap<>();

    public static Map<BlockPosition, Double> getBlockProgress() {
        return blockProgress;
    }

    public static void copyOldData(BlockPosition location) {
        if (!blockProgress.containsKey(location)) {
            return;
        }
        if (oldBlockProgress.containsKey(location)) {
            oldBlockProgress.remove(location);
        }
        oldBlockProgress.put(location, blockProgress.get(location));
    }

    TranspireFlares main = TranspireFlares.getInstance();

    int stat = 0;
    private boolean isFlare(Block block) {
        Location loc = block.getLocation();
        String mix = block.getWorld().getName() + "," +block.getLocation().getBlockX() + "," + block.getLocation().getBlockY()+ "," + block.getLocation().getBlockZ();
//        String mix = block.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        try{
            FlareHandler.getLocToFlare().keySet();
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
//        if(stat >= 15) {
//            stat = 0;
//            System.out.println("----------------Begin Check--------------------");
//            for (String m : FlareHandler.getFlareLocs().keySet()) {
//                System.out.println("---------Begin "+m+"----------");
//                for(String s : FlareHandler.getFlareLocs().get(m)){
//                    System.out.println("Contains: " + s);
//                }
//                System.out.println("---------End "+m+"----------");
//            }
//            FlareHandler.getFlaresYml();
//            System.out.println("Looking for: " + mix);
//            System.out.println("----------------End Check--------------------");
//        }
//        stat++;
        if (FlareHandler.getLocToFlare().containsKey(mix)) {
            Flare flare = FlareHandler.getLocToFlare().get(mix);
            if (flare == null) {
//                System.out.println("FLARE IS NULL");
                return false;
            } else {
                return true;
            }
        }else{
            return false;
        }
    }

    private Flare getFlare(Block block) {
        Location loc = block.getLocation();
        String mix = block.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        if (FlareHandler.getLocToFlare().containsKey(mix)) {
            Flare flare = FlareHandler.getLocToFlare().get(mix);
            if (flare == null) {
                return null;
            } else {
                return flare;
            }
        }else{
            return null;
        }
    }

    private PacketHandler packetSender = new PacketHandler();

    public void resetBlockAnimations(){
        packetSender.resetBlockAnimations();
    }

    public void resetBlockAnimations(Player player){
        packetSender.resetBlockAnimations(player);
    }

    private Object sync = new Object();

    @Override
    public void run() {
        super.run();
        packetSender.start();
        synchronized (sync) {
            while (true) {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (p != null) {
                        try {
                            Block bl = p.getTargetBlockExact(5);
                            if (bl != null){
                                if(isFlare(bl)) {
                                    Flare flare = getFlare(bl);
                                    double hardnessMulti = 1d / (flare.getHardness() / 100d);
                                    BlockPosition pos = new BlockPosition(bl.getX(), bl.getY(), bl.getZ());
                                    if (VoidChannelDuplexHandler.getBlockBreak(pos)) {
                                        EntityPlayer pl = ((CraftPlayer) p).getHandle();
                                        World world = pl.world;
                                        IBlockData b = world.getType(pos);

                                        int currentTick;
                                        Field currentTickF = null;
                                        currentTickF = pl.playerInteractManager.getClass().getDeclaredField("currentTick");
                                        currentTickF.setAccessible(true);
                                        currentTick = currentTickF.getInt(pl.playerInteractManager);

                                        int lastDigTick;
                                        Field lastDigTickF = null;
                                        lastDigTickF = pl.playerInteractManager.getClass().getDeclaredField("lastDigTick");
                                        lastDigTickF.setAccessible(true);
                                        lastDigTick = lastDigTickF.getInt(pl.playerInteractManager);

                                        int k = currentTick - lastDigTick;

                                        float f = b.getDamage(pl, world, pos) * (float) (k + 1);

                                        Double progress = Double.valueOf(String.valueOf((f * 100F))) * hardnessMulti;

                                        if (blockProgress.containsKey(pos)) {
                                            blockProgress.remove(pos);
                                        }
                                        Double oldP = 0d;
                                        if (oldBlockProgress.containsKey(pos)) {
                                            oldP = oldBlockProgress.get(pos);
                                        }
                                        if(p.getGameMode() != GameMode.CREATIVE) {
                                            blockProgress.put(pos, progress + oldP);
                                        }

                                        Double prog = blockProgress.get(pos);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                double holoProg = Math.round(prog * 100.0) / 100.0;
                                                Location loc = bl.getLocation().add(0.5, 2.6, 0.5);
                                                if (RegenTask.doesHologramExist(loc)) {
//                                                    RegenTask.getHologram(loc).clearLines();
                                                    RegenTask.getHologram(loc).removeLine(3);
                                                    int progressBarProg = (int) (prog/5);
                                                    int progressBarProgRed = 20-progressBarProg;
                                                    Hologram hologram = RegenTask.getHologram(loc);
//                                                    ItemLine itemLine = hologram.appendItemLine(new org.bukkit.inventory.ItemStack(org.bukkit.Material.ENDER_CHEST));
//                                                    TextLine textLine = hologram.appendTextLine("");
//                                                    TextLine textLine1 = hologram.appendTextLine("");
                                                    TextLine progressBar = hologram.appendTextLine("");
//
//                                                    itemLine.setItemStack(new ItemStack(org.bukkit.Material.ENDER_CHEST));
//                                                    textLine.setText(Util.toColor("&BBreak this flare to gain insane rewards!"));
//                                                    textLine1.setText(Util.toColor("&bThis is a &f" + flare.getName() + " &bflare."));
                                                    String txt = "&2";
                                                    for(int i = 0; i < progressBarProg; i++){
                                                        txt=txt+"|";
                                                    }
                                                    txt=txt+"&c";
                                                    for(int i = 0; i < progressBarProgRed; i++){
                                                        txt=txt+"|";
                                                    }
                                                    txt=txt+" &7("+holoProg+"%)";
                                                    progressBar.setText(Util.toColor(txt));
                                                }

                                                if (prog < 100D && prog > -1D) {
//                                                    p.sendMessage(progress + " nice");
                                                } else {
                                                    Location where = bl.getLocation().add(0.5, 2.6, 0.5);
                                                    RegenTask.deleteHologram(where);
                                                    TMiningEvent e = new TMiningEvent(p, bl);
                                                    Bukkit.getServer().getPluginManager().callEvent(e);
                                                    blockProgress.remove(pos);
                                                    if (oldBlockProgress.containsKey(pos)) {
                                                        oldBlockProgress.remove(pos);
                                                    }
                                                }
                                            }
                                        }.runTaskLater(TranspireFlares.getInstance(), 1l);
                                    }
                                }else{
//                                    System.out.println("Not Flare!");
                                }
                            }


                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    sync.wait(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
