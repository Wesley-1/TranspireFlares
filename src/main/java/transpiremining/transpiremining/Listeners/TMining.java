package transpiremining.transpiremining.Listeners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import transpiremining.transpiremining.CustomEvents.TMiningEvent;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.Objects.PlayerUtil;
import transpiremining.transpiremining.Objects.Flare;
import transpiremining.transpiremining.Objects.Regen;
import transpiremining.transpiremining.TranspireFlares;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/*
© Copyright TranspireDev 2021-2021. All Rights Reserved

This class and all its contents belongs to, and solely to Zlurpy and PlasmaMan916.
The copying of this class is prohibited.
 */

public class TMining implements Listener {
    TranspireFlares main = TranspireFlares.getPlugin(TranspireFlares.class);

    @EventHandler
    public void onTMining(TMiningEvent event) {
        Player player = event.getPlayer();
        PlayerUtil playerUtil = main.getMiningPlayers().get(player.getUniqueId());
        String mix = event.getBlock().getWorld().getName() + "," + event.getBlock().getLocation().getBlockX() + "," + event.getBlock().getLocation().getBlockY() + "," + event.getBlock().getLocation().getBlockZ();

        if (FlareHandler.getLocToFlare().containsKey(mix)) {
            Flare flare = FlareHandler.getLocToFlare().get(mix);
            if (flare == null) {
                player.sendMessage(toColor(main.getConfig().getString("messages.flare-null")));
            } else {
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    event.getBlock().setType(Material.AIR);
                    FlareHandler.getLocToFlare().remove(mix);
//                    main.getAllRegenLocs().remove(mix);
                    FlareHandler.getFlareLocs().get(flare.getName()).remove(mix);
                    player.sendMessage(toColor(main.getConfig().getString("messages.flare-removed")));
                } else {
                    breakOre(playerUtil, event.getBlock(), flare);
                    ArrayList<String> commands = flare.pickCommands();
                    for(String s : commands){
                        s = s.replaceAll("%player%",event.getPlayer().getName());
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),s);
                        System.out.println(s);
                    }
                    Regen regen = new Regen(event.getBlock(), flare, System.currentTimeMillis(), System.currentTimeMillis() + flare.getRegenerationTime()*1000);
                    main.getCurrentRegens().put(regen, flare);
                }
            }
        }
    }

    public static boolean isValidEffect(final String effect) {
        return Arrays.stream(ParticleEffect.values())
                .map(ParticleEffect::name)
                .collect(Collectors.toSet())
                .contains(effect);
    }

    private void breakOre(PlayerUtil player, Block block, Flare flare) {
        block.setType(Material.AIR);
        if (flare.isParticlebased()) {
            if (!isValidEffect(flare.getParticleEffect())) {
                player.getAsOnlinePlayer().sendMessage("block particle is not valid, dm admin");
            } else {
                ParticleEffect effect = ParticleEffect.valueOf(flare.getParticleEffect());
                effect.display(block.getLocation().add(0, 1.5, 0));

            }
        }
    }

    public static String toColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
