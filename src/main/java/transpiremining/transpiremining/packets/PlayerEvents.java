package transpiremining.transpiremining.packets;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import transpiremining.transpiremining.TranspireFlares;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        TranspireFlares.getPacketHandler().addPlayer(event.getPlayer());
        TranspireFlares.togglePlayer(event.getPlayer(), true);
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        TranspireFlares.getPacketHandler().removePlayer(event.getPlayer());
        TranspireFlares.removePlayer(event.getPlayer());
        TranspireFlares.getInstance().resetBlockAnimations(event.getPlayer());
    }
}
