package transpiremining.transpiremining.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.Objects.PlayerUtil;
import transpiremining.transpiremining.Objects.Flare;
import transpiremining.transpiremining.TranspireFlares;
import transpiremining.transpiremining.Utilites.Util;

public class FlareCommands implements CommandExecutor {
    TranspireFlares main = TranspireFlares.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("flares")){
        switch (args.length) {
            case 0:

                break;
            case 1:
                if (Util.checkSender(sender)) {
                    Player player = (Player) sender;
                    if (Util.checkPermission(player, "flare.help")) {
                        //do stuff
                    }

                }
                break;
            case 4:
                if ((args[0].equalsIgnoreCase("flare")) && args[1].equalsIgnoreCase("give")){
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target != null){
                        String type = args[3];
                        if (FlareHandler.getFlareTypes().containsKey(type)){
                            Flare flare = FlareHandler.getFlareTypes().get(type);
                            target.getInventory().addItem(flare.getPlaceableItem());
                        }else{
                            //invalid ore type
                        }
                    }else{
                        //invalid player
                    }
                }else{

                }

                break;
            default:
                sender.sendMessage(args.length + "");
                break;
        }



        }

//tm ore give player type amount
//tm booster give player type
//tm level set player level

        return false;
    }
}
