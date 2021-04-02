package transpiremining.transpiremining.packets;

import io.netty.channel.*;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import transpiremining.transpiremining.FlareHandler;
import transpiremining.transpiremining.Objects.Flare;
import transpiremining.transpiremining.tasks.ProgressTask;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
© Copyright TranspireDev 2021-2021. All Rights Reserved

This class and all its contents belongs to, and solely to Zlurpy and PlasmaMan916.
The copying of this class is prohibited.
 */

public class VoidChannelDuplexHandler extends ChannelDuplexHandler {
    private Player player;
    private ChannelHandlerContext context = null;
    private ChannelPromise promise = null;
    public ChannelHandlerContext getContext(){
        return context;
    }
    public ChannelPromise getPromise(){
        return promise;
    }
    public VoidChannelDuplexHandler(Player player){
        this.player = player;
    }

    private static Map<BlockPosition,Boolean> blockBreaking = new ConcurrentHashMap<>();

    public static boolean getBlockBreak(BlockPosition pos){
        if(blockBreaking.containsKey(pos)){
            return blockBreaking.get(pos);
        }else{
            return false;
        }
    }

    private boolean isFlare(Block block) {
        Location loc = block.getLocation();
        String mix = block.getWorld().getName() + "," +block.getLocation().getBlockX() + "," + block.getLocation().getBlockY()+ "," + block.getLocation().getBlockZ();
//        String mix = block.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        try{
            FlareHandler.getLocToFlare().keySet();
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
        if (FlareHandler.getLocToFlare().containsKey(mix)) {
            Flare flare = FlareHandler.getLocToFlare().get(mix);
            if (flare == null) {
                System.out.println("FLARE IS NULL");
                return false;
            } else {
                return true;
            }
        }else{
            return false;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PacketPlayInBlockDig){
            PacketPlayInBlockDig packet = (PacketPlayInBlockDig) msg;

            BlockPosition a;
            Field fa = packet.getClass().getDeclaredField("a");
            fa.setAccessible(true);
            a = (BlockPosition) fa.get(packet);

            PacketPlayInBlockDig.EnumPlayerDigType c;
            Field fc = packet.getClass().getDeclaredField("c");
            fc.setAccessible(true);
            c = (PacketPlayInBlockDig.EnumPlayerDigType) fc.get(packet);

            Block block = player.getWorld().getBlockAt(a.getX(),a.getY(),a.getZ());

            if(blockBreaking.containsKey(a)){
                blockBreaking.remove(a);
            }
            if(c == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK){
//                System.out.println("Starting Breaking!");
                blockBreaking.put(a,true);
            }else if(c == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK || c == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK){
//                System.out.println("Stopping Breaking!");
                blockBreaking.put(a,false);
                ProgressTask.copyOldData(a);
            }
            if(isFlare(block) && c == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK){
                return;
            }
        }
        super.channelRead(ctx, msg);
    }

    protected void breakBlock(Block block){

    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        this.context = ctx;
        this.promise = promise;

        if(msg instanceof PacketPlayOutBlockBreak){
            PacketPlayOutBlockBreak packet = (PacketPlayOutBlockBreak) msg;

            BlockPosition c;
            Field ca = packet.getClass().getDeclaredField("c");
            ca.setAccessible(true);
            c = (BlockPosition) ca.get(packet);

            Block block = player.getWorld().getBlockAt(c.getX(),c.getY(),c.getZ());


            IBlockData d;
            Field da = packet.getClass().getDeclaredField("d");
            da.setAccessible(true);
            d = (IBlockData) da.get(packet);

//            WorldServer ws = ((CraftWorld) player.getWorld()).getHandle();
//            ws.getT

            PacketPlayInBlockDig.EnumPlayerDigType a;
            Field fa = packet.getClass().getDeclaredField("a");
            fa.setAccessible(true);
            a = (PacketPlayInBlockDig.EnumPlayerDigType) fa.get(packet);

            boolean e;
            Field ea = packet.getClass().getDeclaredField("e");
            ea.setAccessible(true);
            e = ea.getBoolean(packet);

            if(isFlare(block)){
//                System.out.println(e);
                e = true;
            }
        }
        if(msg instanceof PacketPlayOutBlockBreakAnimation){
            PacketPlayOutBlockBreakAnimation packet = (PacketPlayOutBlockBreakAnimation) msg;

            int a;
            Field fa = packet.getClass().getDeclaredField("a");
            fa.setAccessible(true);
            a = fa.getInt(packet);

            BlockPosition b;
            Field fb = packet.getClass().getDeclaredField("b");
            fb.setAccessible(true);
            b = (BlockPosition) fb.get(packet);

            int c;
            Field fc = packet.getClass().getDeclaredField("c");
            fc.setAccessible(true);
            c = fc.getInt(packet);

//            System.out.println(a+":a or "+c+":c is progress at "+" "+b.getX()+", "+b.getY()+", "+b.getZ());
        }

        super.write(ctx, msg, promise);

    }


    public VoidChannelDuplexHandler startListen(){//Use the reflection for getting the channel I sent you
        ChannelPipeline pipeline = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), this);

        return this;
    }
    public VoidChannelDuplexHandler stopListening(){//Use the reflection for getting the channel I sent you
        Channel channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
        if(channel==null)return this;
        channel.eventLoop().submit(() -> { channel.pipeline().remove(player.getName());
            return null;
        });
        return this;
    }
}
