package transpiremining.transpiremining.Objects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import transpiremining.transpiremining.TranspireFlares;

import java.io.File;

public class AbstractFile {

    private TranspireFlares main;
    private File file;
    protected FileConfiguration config;
    private String fileName;


    public AbstractFile(TranspireFlares main, String fileName){
        this.main = main;
        this.fileName = fileName;
        this.file = new File(main.getDataFolder(), fileName);
        if (!file.exists()){
            main.saveResource(fileName, false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        main.getCustomConfigs().put(fileName, this.config);
    }

    public void save(){
        main.saveResource(fileName, false);

    }
    
    //now to go booster.yml
}
//
