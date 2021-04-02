package transpiremining.transpiremining.Objects;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerUtil {

    private UUID uuid;
    private long fragments;
    private HashMap<String, Integer> brokenOres;

    public PlayerUtil(UUID uuid, HashMap<String, Integer> brokenOres, long fragments) {
        this.uuid = uuid;
        this.brokenOres =  brokenOres;
        this.fragments = fragments;
    }

    public Player getAsOnlinePlayer(){
        return Bukkit.getPlayer(uuid);
    }

    public HashMap<String, Integer> getBrokenOres() {
        return brokenOres;
    }

    public void setFragments(long fragments) {
        this.fragments = fragments;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getFragments() {
        return fragments;
    }

    public void setBrokenOres(HashMap<String, Integer> brokenOres) {
        this.brokenOres = brokenOres;
    }
}
