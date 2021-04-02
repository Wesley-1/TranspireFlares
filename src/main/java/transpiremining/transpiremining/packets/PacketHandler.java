package transpiremining.transpiremining.packets;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.Objects.Flare;
import transpiremining.transpiremining.Objects.Regen;
import transpiremining.transpiremining.TranspireFlares;
import transpiremining.transpiremining.tasks.ProgressTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
© Copyright TranspireDev 2021-2021. All Rights Reserved

This class and all its contents belongs to, and solely to Zlurpy and PlasmaMan916.
The copying of this class is prohibited.
 */

public class PacketHandler extends Thread{

    private Map<BlockPosition,Integer> randomInts = new HashMap<>();
        private Object syncro = new Object();
        @Override
        public void run() {
            super.run();
            synchronized (syncro){
                while(true) {
                    Map<BlockPosition, Double> blockProgress = ProgressTask.getBlockProgress();
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        for (BlockPosition pos : blockProgress.keySet()) {
                            double p = blockProgress.get(pos);
                            int prog = (int)p;
                            if(!randomInts.containsKey(pos)){
                                Random rand = new Random(System.currentTimeMillis());
                                randomInts.put(pos,rand.nextInt(1000));
                            }
                            PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(randomInts.get(pos), pos, (prog / 10));
                            TranspireFlares.getPacketHandler().sendPacket(player, packet);
                        }
                    }
                    try {
                        syncro.wait(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        public void resetBlockAnimations(Player player){
//            for (Regen regen : TranspireFlares.getInstance().getCurrentRegens().keySet()) {
//                Double x = regen.getBlock().getLocation().getX();
//                Double y = regen.getBlock().getLocation().getY();
//                Double z = regen.getBlock().getLocation().getZ();
//
//                BlockPosition pos = new BlockPosition(x,y,z);
//                if(!randomInts.containsKey(pos)){
//                    Random rand = new Random(System.currentTimeMillis());
//                    randomInts.put(pos,rand.nextInt(1000));
//                }
//                System.out.println("Resetting for: "+player.getName());
//                PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(randomInts.get(pos), pos, 0);
//                TranspireFlares.getPacketHandler().sendPacket(player, packet);
//            }
//            for (String flareTypes : FlareHandler.getFlareLocs().keySet()) {
//                for(String d : FlareHandler.getFlareLocs().get(flareTypes)){
//                    String[] data = d.split(",");
//                    Double x = Double.valueOf(data[1]);
//                    Double y = Double.valueOf(data[2]);
//                    Double z = Double.valueOf(data[3]);
//
//                    BlockPosition pos = new BlockPosition(x,y,z);
//                    if(!randomInts.containsKey(pos)){
//                        Random rand = new Random(System.currentTimeMillis());
//                        randomInts.put(pos,rand.nextInt(1000));
//                    }
//                    System.out.println("Resetting for: "+player.getName());
//                    PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(randomInts.get(pos), pos, 0);
//                    TranspireFlares.getPacketHandler().sendPacket(player, packet);
//                }
//            }
        }
        public void resetBlockAnimations(){
            for (Regen regen : TranspireFlares.getInstance().getCurrentRegens().keySet()) {
                regen.regen();
            }
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                resetBlockAnimations(player);
            }
        }
    }