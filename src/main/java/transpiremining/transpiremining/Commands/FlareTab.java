package transpiremining.transpiremining.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.TranspireFlares;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlareTab implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        List<String> tabComplete = new ArrayList<>();
        if(args.length == 1){
            if("flare".startsWith(args[0].toLowerCase()) || args[0].equals("")) {
                tabComplete.add("flare");
            }
            if (args[0].equals("")) {
                tabComplete.add("flare");
            }
        }else if(args.length == 2){
            if("give".startsWith(args[1].toLowerCase()) || args[1].equals("")) {
                tabComplete.add("give");
            }
        }else if(args.length == 3){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getName().startsWith(args[2]) || args[2].equals("")){
                    tabComplete.add(p.getName());
                }
            }
        }else if(args.length == 4){
            for(String s : FlareHandler.getFlareTypes().keySet()){
                if(s.toLowerCase().startsWith(args[3].toLowerCase()) || args[3].equals("")) {
                    tabComplete.add(s);
                }
            }
        }
        return tabComplete;
    }

}
