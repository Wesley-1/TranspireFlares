package transpiremining.transpiremining.Objects;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class Flare {

    private String name;
    private boolean particleBased;
    private String ParticleEffect;
    private boolean permissionBased;
    private long regenerationTime;
    private String permission;
    private Material indentifier;
    private ItemStack placeableItem;
    private double hardness;
    private ArrayList<String> commands;
    private int commandCount;

    public Flare(String name, Double harndness, ItemStack placeableItem, Material indentifier, ArrayList<String> commands, int commandCount, boolean permissionBased, String permission, long regenerationTime, String ParticleEffect, boolean particleBased) {
        this.name = name;
        this.permissionBased = permissionBased;
        this.permission = permission;
        this.regenerationTime = regenerationTime;
        this.indentifier = indentifier;
        this.placeableItem = placeableItem;
        this.particleBased = particleBased;
        this.ParticleEffect = ParticleEffect;
        this.hardness = harndness;
        this.commands = commands;
        this.commandCount = commandCount;
    }

    public ItemStack getPlaceableItem() {
        return placeableItem;
    }

    public Material getIndentifier() {
        return indentifier;
    }

    public long getRegenerationTime() {
        return regenerationTime;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> pickCommands() {
        ArrayList<String> pickedCommands = new ArrayList<>();
        while(pickedCommands.size() < this.commandCount){
            Random random = new Random();
            random.setSeed(System.currentTimeMillis());
            int num = random.nextInt(this.commands.size());
            String pick = this.commands.get(num);
            if(!pickedCommands.contains(pick)){
                pickedCommands.add(pick);
            }
        }
        return pickedCommands;
    }

    public boolean isPermissionBased() {
        return permissionBased;
    }

    public String getPermission() {
        return permission;
    }

    public String getParticleEffect() {
        return ParticleEffect;
    }
    public boolean isParticlebased() {
        return particleBased;
    }

    public void setHardness(double hardness) {
        this.hardness = hardness;
    }

    public double getHardness() {
        return hardness;
    }
}
