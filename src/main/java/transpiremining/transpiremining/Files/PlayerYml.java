package transpiremining.transpiremining.Files;

import org.bukkit.configuration.file.FileConfiguration;
import transpiremining.transpiremining.Objects.AbstractFile;
import transpiremining.transpiremining.TranspireFlares;

public class PlayerYml extends AbstractFile {

    public PlayerYml(TranspireFlares main) {
        super(main, "playerData.yml");
    }

    public FileConfiguration getConfig(){
        return this.config;
    }
}
