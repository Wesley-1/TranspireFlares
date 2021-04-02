package transpiremining.transpiremining.Listeners;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.IBlockData;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import transpiremining.transpiremining.TranspireFlares;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockDamage implements Listener{

    private Map<Location, Integer> blocks = new ConcurrentHashMap<>();

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event){
//        Location loc = event.getBlock().getLocation();
//        if(blocks.containsKey(loc)){
//            int stat = blocks.get(loc);
//            blocks.remove(loc);
//            blocks.put(loc, stat+1);
//        }else{
//            blocks.put(loc,1);
//        }
//        event.getPlayer().sendMessage(blocks.get(loc)+"");
//        new BukkitRunnable(
//        ) {
//            private Object sync = new Object();
//            @Override
//            public void run() {
//                synchronized (sync){
//                    int stat = blocks.get(loc);
//                    try {
//                        sync.wait(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if(blocks.get(loc) <= stat){
//                        blocks.remove(loc);
//                    }
//                }
//            }
//        }.runTaskAsynchronously(TranspireFlares.getInstance());
    }
}
