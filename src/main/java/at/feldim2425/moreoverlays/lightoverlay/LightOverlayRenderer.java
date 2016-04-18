package at.feldim2425.moreoverlays.lightoverlay;

import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class LightOverlayRenderer {

    private final static AxisAlignedBB TEST_BB = new AxisAlignedBB(0.6D/2D, 0, 0.6D/2D, 1D-0.6D/2D, 1D, 1D-0.6D/2D);
    private final static int RANGE = 16;
    private static Map.Entry<BlockPos, Byte>[] overlayCache;
    private static RenderManager render = Minecraft.getMinecraft().getRenderManager();


    public static void renderOverlays(){
        if(overlayCache==null)
            return;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glLineWidth(2.0F);
        GlStateManager.translate(-render.viewerPosX , -render.viewerPosY, -render.viewerPosZ);

        for (int i = 0; i < overlayCache.length; i++) {
            Map.Entry<BlockPos, Byte> entry = overlayCache[i];
            Byte mode = entry.getValue();
            if (mode == null || mode == 0)
                continue;
            else if (mode == 1)
                renderCross(entry.getKey(), 1, 1, 0);
            else if (mode == 2)
                renderCross(entry.getKey(), 1, 0, 0);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void refreshCache(){
        if (Minecraft.getMinecraft().thePlayer == null)
            return;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        int px = (int) Math.floor(player.posX);
        int py = Math.min(Math.max((int) player.posY, 0), player.worldObj.getHeight()-1);
        int pz = (int) Math.floor(player.posZ);

        int y1 = (py - Config.light_DownRange < 0) ? 0 : py-Config.light_DownRange;
        int y2 = (py + Config.light_UpRange > player.worldObj.getHeight()-1) ? player.worldObj.getHeight()-1 : py+Config.light_UpRange;

        HashMap<BlockPos, Byte> newCache = new HashMap<>();
        for (int xo = -Config.light_HRange; xo <= Config.light_HRange; xo++) {
            for (int zo = -Config.light_HRange; zo <= Config.light_HRange; zo++) {
                BlockPos pos1 = new BlockPos(px + xo, py, pz + zo);
                BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(pos1);

                if (biome.getSpawningChance() <= 0 || biome.getSpawnableList(EnumCreatureType.MONSTER).isEmpty())
                    continue;

                Chunk chunk = player.worldObj.getChunkFromBlockCoords(pos1);
                for (int y = y1; y <= y2; y++) {
                    BlockPos pos = new BlockPos(px + xo, y, pz + zo);
                    byte mode = getSpawnModeAt(pos, chunk, player.worldObj);
                    if (mode != 0)
                        newCache.put(pos, mode);
                }
            }
        }

        overlayCache = newCache.entrySet().toArray(new Map.Entry[newCache.size()]);
    }

    private static byte getSpawnModeAt(BlockPos pos, Chunk chunk, World world){
        if(chunk.getLightFor(EnumSkyBlock.BLOCK, pos)>7 || !world.getBlockState(pos.down()).getBlock().canCreatureSpawn(world,pos.down(), EntityLiving.SpawnPlacementType.ON_GROUND))
            return 0;

        if(!checkCollision(pos,world))
            return 0;

        if(chunk.getLightFor(EnumSkyBlock.SKY, pos)>7)
            return 1;

        return 2;
    }

    private static boolean checkCollision(BlockPos pos, World world){
        Block block1 = world.getBlockState(pos).getBlock();

        if(block1.isNormalCube(world,pos) || (!Config.light_IgnoreLayer && world.getBlockState(pos.up()).getBlock().isNormalCube(world,pos.up()))) //Don't check because a check on normal Cubes will/should return false ( 99% collide ).
            return false;
        else if(world.isAirBlock(pos) && (Config.light_IgnoreLayer || world.isAirBlock(pos.up())))  //Don't check because Air has no Collision Box
            return true;

        AxisAlignedBB bb = TEST_BB.offset(pos.getX(),pos.getY(),pos.getZ());
        if(world.func_147461_a(bb).isEmpty() && !world.isAnyLiquid(bb)){
            if(Config.light_IgnoreLayer)
                return true;
            else {
                AxisAlignedBB bb2 = bb.offset(0,1,0);
                return world.func_147461_a(bb2).isEmpty() && !world.isAnyLiquid(bb2);
            }
        }
        return false;
    }

    private static void renderCross(BlockPos pos, float r, float g, float b){
        double y = pos.getY() + 0.005D;

        double x0 = pos.getX();
        double x1 = x0 + 1;
        double z0 = pos.getZ();
        double z1 = z0 + 1;

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer renderer = tess.getWorldRenderer();

        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos(x0, y , z0).color(r,g,b,1).endVertex();
        renderer.pos(x1, y , z1).color(r,g,b,1).endVertex();

        renderer.pos(x1, y , z0).color(r,g,b,1).endVertex();
        renderer.pos(x0, y , z1).color(r,g,b,1).endVertex();
        tess.draw();
    }

    public static void clearCache(){
        overlayCache=null;
    }
}