package transpiremining.transpiremining.Utilites;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.TranspireFlares;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Util {

    TranspireFlares main = TranspireFlares.getPlugin(TranspireFlares.class);

    public ItemStack createItem1(String string){
        System.out.println(string + ".material");//This prints out "flares.Ruby.item.material"
        System.out.println(FlareHandler.getFlaresYml().getString(string + ".particle-effect"));// Prints out 'null'
        Material mat = Material.valueOf(FlareHandler.getFlaresYml().getString(string + ".material"));
        short damage = (short)FlareHandler.getFlaresYml().getInt(string + ".damage");
        boolean glow = FlareHandler.getFlaresYml().getBoolean(string + ".glow");

        ItemStack item = new ItemStack(mat, 1, damage);
        ItemMeta meta = item.getItemMeta();

        String name = Util.toColor(FlareHandler.getFlaresYml().getString(string  + ".name"));
        ArrayList<String> lore = new ArrayList<>();
        FlareHandler.getFlaresYml().getStringList(string +  ".lore").stream().forEach(s -> lore.add(Util.toColor(s)));

        meta.setDisplayName(name);
        meta.setLore(lore);

        if (glow){
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);

        return item;
    }
    public ItemStack createItem(final String string) {
        final Material mat = Material.valueOf(FlareHandler.getFlaresYml().getString("flares." + string + ".material"));
        final short damage = (short)FlareHandler.getFlaresYml().getInt("flares." + string + "damage");
        final boolean glow = FlareHandler.getFlaresYml().getBoolean("flares." + string + ".glow");
        final ItemStack item = new ItemStack(mat, 1, damage);
        final ItemMeta meta = item.getItemMeta();
        final String name = toColor(FlareHandler.getFlaresYml().getString("flares." + string + ".name"));
        final ArrayList<String> lore = new ArrayList<String>();
        FlareHandler.getFlaresYml().getStringList("flares." + string + ".lore").stream().forEach(s -> lore.add(toColor(s)));
        meta.setDisplayName(name);
        meta.setLore((List)lore);
        if (glow) {
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        }
        item.setItemMeta(meta);
        return item;
    }

    public static boolean checkSender(CommandSender sending) {
        boolean correctsender = false;
        if (sending instanceof Player) {
            correctsender = true;
        } else {
            sending.sendMessage("this command can only be used by players");
        }
        return correctsender;
    }

    public static boolean checkPermission(Player player, String permission) {
        boolean has = false;
        if (player.hasPermission(permission)) {
            has = true;
        } else {
            player.sendMessage("no permission");
            has = false;
        }
        return has;
    }
    public static int randomAmount(int Min, int Max)
    {
        return (int) (Math.random()*((Max-Min) + 1)) + Min;
    }
    public static void sendActionbar(Player player, String message) {
        if (player == null || message == null) return;
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

        //1.10 and up
        if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }

        //1.8.x and 1.9.x
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            Object packetPlayOutChat;
            Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String toColor(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
