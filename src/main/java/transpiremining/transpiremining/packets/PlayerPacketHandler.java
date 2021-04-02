package transpiremining.transpiremining.packets;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import io.netty.channel.DefaultChannelPromise;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
© Copyright TranspireDev 2021-2021. All Rights Reserved

This class and all its contents belongs to, and solely to Zlurpy and PlasmaMan916.
The copying of this class is prohibited.
 */

public class PlayerPacketHandler extends Thread{
    private Map<Player, VoidChannelDuplexHandler> players = new ConcurrentHashMap<>();

    public void sendPacket(Player player, Object packet){
        for(Player p : players.keySet()) {
            if(p.getUniqueId() == player.getUniqueId()) {
                VoidChannelDuplexHandler handler = players.get(p);
                try {
                    handler.write(handler.getContext(), packet, new DefaultChannelPromise(handler.getPromise().channel()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public VoidChannelDuplexHandler addPlayer(Player player){
        if(!players.containsKey(player)){
            VoidChannelDuplexHandler newHandler = new VoidChannelDuplexHandler(player);
            newHandler.startListen();
            players.put(player,newHandler);
            return newHandler;
        }else{
            return players.get(player);
        }
    }
    public void removePlayer(Player player){
        if(players.containsKey(player)){
            players.get(player).stopListening();
            players.remove(player);

        }
    }

    private Object sync = new Object();

    @Override
    public void run() {
        super.run();
        synchronized (sync) {
            while (true) {
                try {
                    sync.wait(1000);
                }catch (InterruptedException e){}
            }
        }
    }
}
