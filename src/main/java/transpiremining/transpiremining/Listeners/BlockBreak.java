package transpiremining.transpiremining.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import transpiremining.transpiremining.CustomEvents.TMiningEvent;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.TranspireFlares;
import transpiremining.transpiremining.tasks.RegenTask;


public class BlockBreak  implements Listener  {

    TranspireFlares main = TranspireFlares.getPlugin(TranspireFlares.class);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String mix = event.getBlock().getWorld().getName() + "," + event.getBlock().getLocation().getBlockX() + "," + event.getBlock().getLocation().getBlockY() + "," + event.getBlock().getLocation().getBlockZ();
        if (FlareHandler.getLocToFlare().containsKey(mix) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getBlock().getState().update(true);
        }
        if(FlareHandler.getLocToFlare().containsKey(mix) && event.getPlayer().getGameMode() == GameMode.CREATIVE){
            System.out.println("Removing Flare");
            String m = event.getBlock().getWorld().getName() + "," + event.getBlock().getLocation().getBlockX() + "," + event.getBlock().getLocation().getBlockY() + "," + event.getBlock().getLocation().getBlockZ();
            FlareHandler.removeFlare(event.getBlock().getLocation(),m);
        }
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                Location loc = event.getBlock().getLocation().add(0.5, 2.6, 0.5);
//                if (RegenTask.doesHologramExist(loc)) {
//                    RegenTask.deleteHologram(loc);
//
//                }
//            }
//        }.runTaskLater(TranspireFlares.getInstance(),10l);//0.5 sec, right?
    }
}
