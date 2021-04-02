package transpiremining.transpiremining;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import transpiremining.transpiremining.Commands.*;
import transpiremining.transpiremining.Files.FlaresYml;
import transpiremining.transpiremining.Listeners.*;
import transpiremining.transpiremining.Objects.*;
import transpiremining.transpiremining.Utilites.Util;
import transpiremining.transpiremining.packets.PlayerEvents;
import transpiremining.transpiremining.packets.PlayerPacketHandler;
import transpiremining.transpiremining.tasks.ProgressTask;
import transpiremining.transpiremining.tasks.RegenTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
© Copyright TranspireDev 2021-2021. All Rights Reserved

This class and all its contents belongs to, and solely to Zlurpy and PlasmaMan916.
The copying of this class is prohibited.
 */

public final class TranspireFlares extends JavaPlugin {
    private static TranspireFlares instance;
    private HashMap<UUID, PlayerUtil> miningPlayers = new HashMap<>();
    private FileConfiguration playerData;
    private HashMap<String, FileConfiguration> customConfigs = new HashMap<String, FileConfiguration>();
    private HashMap<String, Flare> allRegenLocs = new HashMap<>();
    private static Util util;
    private HashMap<Regen, Flare> currentRegens = new HashMap<>();

    //VoidPacketStuff Start
    private ProgressTask fph = new ProgressTask();

    public void resetBlockAnimations(Player player){
        fph.resetBlockAnimations(player);
    }

    public void removeRegen(Location loc,String mix){
        for(Regen regen : currentRegens.keySet()){
            if(regen.getBlock().getLocation() == loc){
                currentRegens.remove(regen);
            }
        }
        if(allRegenLocs.containsKey(mix)){
            allRegenLocs.remove(mix);
        }

    }

    private static PlayerPacketHandler packetHandler;
    private static Map<Player, Boolean> toggles = new ConcurrentHashMap<>();

    public static boolean getToggle(Player p) {
        boolean status = false;
        if (toggles.containsKey(p)) {
            status = toggles.get(p);
        }
        return status;
    }

    public static void togglePlayer(Player p) {
        boolean toggled = true;
        if (toggles.containsKey(p)) {
            toggled = toggles.get(p);
            toggles.remove(p);
        }
        if (toggled) {
            toggled = false;
        } else {
            toggled = true;
        }
        toggles.put(p, toggled);
    }

    public static void togglePlayer(Player p, boolean status) {
        if (toggles.containsKey(p)) {
            toggles.remove(p);
        }
        toggles.put(p, status);
    }

    public static void removePlayer(Player p) {
        if (toggles.containsKey(p)) {
            toggles.remove(p);
        }
    }

    public static PlayerPacketHandler getPacketHandler() {
        return packetHandler;
    }
    //VoidPacketStuff Stop

    public static TranspireFlares getInstance() {
        return TranspireFlares.instance;
    }

    // any idea?
    private RegenTask task;
    @Override
    public void onEnable() {
        instance = this;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {

            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            this.util = new Util();
            new DataFile(this, "playerData.yml");
            loadConfig();
            loadCommands();
            loadListeners();
            loadPlayers();
            new Placeholder().register();
            FlareHandler.loadFlares();
            task = new RegenTask();
        } else {
            getLogger().severe("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        fph.start();
        packetHandler = new PlayerPacketHandler();
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new BlockDamage(), this);
        for (Player p : this.getServer().getOnlinePlayers()) {
            packetHandler.addPlayer(p);
            togglePlayer(p);
        }
        packetHandler.start();
    }

    public HashMap<Regen, Flare> getCurrentRegens() {
        return currentRegens;
    }

    public void setPlayerData(FileConfiguration playerData) {
        this.playerData = playerData;
    }

    public static Util getUtil() {
        return util;
    }

    private void loadPlayers() {
        File file = new File(getDataFolder(), "playerData.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.isConfigurationSection("players")) return;
        for (String uid : config.getConfigurationSection("players").getKeys(false)) {
            UUID uuid = UUID.fromString(uid);
            int fragments = config.getInt("players." + uid + ".fragments");
            HashMap<String, Integer> brokenOres = new HashMap<>();
            if (config.isConfigurationSection("players." + uid + ".broken-ores")) {
                for (String bName : config.getConfigurationSection("players." + uid + ".broken-ores").getKeys(false)) {
                    int amount = config.getInt("players." + uid + ".broken-ores." + bName);
                    brokenOres.put(bName, amount);
                }
            }
            PlayerUtil playerUtil = new PlayerUtil(uuid, brokenOres, fragments);
            getMiningPlayers().put(uuid, playerUtil);
            System.out.println("Loaded " + uid);
        }
    }

    private void savePlayers() throws IOException {
        File file = new File(getDataFolder(), "playerData.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (UUID uuid : getMiningPlayers().keySet()) {
            System.out.println(uuid.toString() + " FOUND");
            String uid = uuid.toString();
            PlayerUtil player = getMiningPlayers().get(uuid);
            config.set("players." + uid + ".fragments", player.getFragments());
            for (String bName : player.getBrokenOres().keySet()) {
                config.set("players." + uid + ".broken-ores." + bName, player.getBrokenOres().get(bName));
            }
        }
        config.save(file);
    }

    public HashMap<String, FileConfiguration> getCustomConfigs() {
        return this.customConfigs;
    }

    public HashMap<UUID, PlayerUtil> getMiningPlayers() {
        return miningPlayers;
    }

    @Override
    public void onDisable() {
        fph.resetBlockAnimations();
        try {
            savePlayers();
            FlareHandler.doSave();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Player p : this.getServer().getOnlinePlayers()){
            packetHandler.removePlayer(p);
            removePlayer(p);
        }
        task.cancel();
        fph.stop();
        packetHandler.stop();
        instance = null;
    }


    private void loadConfig() {
        saveDefaultConfig();
    }

    private void loadCommands() {
        this.getCommand("flares").setExecutor(new FlareCommands());
        this.getCommand("flares").setTabCompleter(new FlareTab());
    }

    private void loadListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BlockBreak(), this);
        pluginManager.registerEvents(new BlockPlace(), this);
        pluginManager.registerEvents(new TMining(), this);
        pluginManager.registerEvents(new PlayerJoin(), this);
    }

    public HashMap<String, Flare> getAllRegenLocs() {
        return allRegenLocs;
    }

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}