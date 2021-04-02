package transpiremining.transpiremining.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import transpiremining.transpiremining.Objects.PlayerUtil;
import transpiremining.transpiremining.TranspireFlares;

import java.util.HashMap;
import java.util.UUID;

public class PlayerJoin implements Listener {
    TranspireFlares main = TranspireFlares.getPlugin(TranspireFlares.class);

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (main.getMiningPlayers().containsKey(uuid))return;
        PlayerUtil playerUtil = new PlayerUtil(uuid, new HashMap<String, Integer>(), 0);
        main.getMiningPlayers().put(uuid, playerUtil);
    }
}
