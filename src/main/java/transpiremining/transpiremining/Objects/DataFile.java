package transpiremining.transpiremining.Objects;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.configuration.file.YamlConfiguration;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.TranspireFlares;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DataFile {
    
    private TranspireFlares main;
    private File file;
    protected FileConfiguration config;
    private String fileName;
    public DataFile(TranspireFlares main, String name){
            this.main = main;
            if (name.equalsIgnoreCase("playerData.yml")){
                this.file = new File(main.getDataFolder(), "playerData.yml");

            }else {
                this.file = new File(main.getDataFolder(), name + "Locs.yml");
            }
            if (!file.exists()){
                try{
                    file.createNewFile();
                }catch(IOException ev){
                    ev.printStackTrace();
                }

            }
            this.config = YamlConfiguration.loadConfiguration(file);
            if (name.equalsIgnoreCase("playerData.yml")){

                main.setPlayerData(this.config);

            }else{
                FlareHandler.getFlareLocs().put(name, new ArrayList<String>());
                for (String string : this.config.getStringList("locs")){
                    System.out.println("loading " + string);
                    FlareHandler.getFlareLocs().get(name).add(string.split(":")[0]);
                    main.getAllRegenLocs().put(string.split(":")[0], FlareHandler.getFlareTypes().get(string.split(":")[1]));
                    FlareHandler.getLocToFlare().put(string.split(":")[0], FlareHandler.getFlareTypes().get(string.split(":")[1]));

                }
            }
    }
}
