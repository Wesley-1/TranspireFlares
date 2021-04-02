package transpiremining.transpiremining.Files;


import org.bukkit.configuration.file.FileConfiguration;
import transpiremining.transpiremining.Objects.AbstractFile;
import transpiremining.transpiremining.TranspireFlares;

public class FlaresYml extends AbstractFile {
    public FlaresYml(TranspireFlares main) {
        super(main, "flares.yml");
    }

    public FileConfiguration getConfig(){
        return this.config;
    }
}
