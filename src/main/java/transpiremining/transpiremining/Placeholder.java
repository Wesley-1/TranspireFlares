package transpiremining.transpiremining;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import transpiremining.transpiremining.Objects.PlayerUtil;

public class Placeholder extends PlaceholderExpansion {
    TranspireFlares main = TranspireFlares.getPlugin(TranspireFlares.class);

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public boolean persist(){
        return true;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "flare";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DeSerialize + Zlurpy";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){
        //%tm_broken_{type}%
        PlayerUtil playerUtil = main.getMiningPlayers().get(player.getUniqueId());


        if(identifier.startsWith("broken_")){
            String[] split = identifier.split("_");
            String type = split[1];
            String value = String.valueOf(main.getMiningPlayers().get(player.getUniqueId()).getBrokenOres().get(type));
            if (value == "null"){
                return "0";
            }else {
                return value;
            }
        }
        return identifier;
    }
}
