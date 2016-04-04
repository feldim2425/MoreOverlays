package at.feldim2425.moreoverlays.lightoverlay;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class LightOverlayRenderer {

    //private final static AxisAlignedBB NULL_BB = new AxisAlignedBB(0,0,0,0,0,0);
    //private final static EntityZombie dummy = new EntityZombie(null);
    private final static AxisAlignedBB ZOMBIE_BB = new AxisAlignedBB(0.6D / 2D, 0, 1.95D / 2D, 1D - 0.6D / 2D, 1.95D, 1D - 0.6D / 2D);
    private final static int RANGE = 16;
    private static HashMap<BlockPos, Byte> overlayCache;
    private static RenderManager render = Minecraft.getMinecraft().getRenderManager();


    public static void renderOverlays() {
        if (overlayCache == null || overlayCache.isEmpty())
            return;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glLineWidth(2.0F);
        GlStateManager.translate(-render.viewerPosX, -render.viewerPosY, -render.viewerPosZ);

        Map.Entry<BlockPos, Byte>[] posSet = overlayCache.entrySet().toArray(new Map.Entry[overlayCache.size()]);
        for (int i = 0; i < posSet.length; i++) {
            Map.Entry<BlockPos, Byte> entry = posSet[i];
            byte mode = entry.getValue();
            if (mode == 0)
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


    public synchronized static void refreshCache() {
        if (Minecraft.getMinecraft().thePlayer == null)
            return;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        int px = (int) player.posX;
        int py = Math.min(Math.max((int) player.posY, RANGE), player.worldObj.getHeight() - RANGE);
        int pz = (int) player.posZ;

        int y1 = (py - RANGE < 0) ? 0 : -RANGE;
        int y2 = (py + RANGE > player.worldObj.getHeight()) ? player.worldObj.getHeight() : RANGE;

        HashMap<BlockPos, Byte> newCache = new HashMap<>();
        for (int xo = -RANGE; xo <= RANGE; xo++) {
            for (int zo = -RANGE; zo <= RANGE; zo++) {
                BlockPos pos1 = new BlockPos(px + xo, py, pz + zo);
                BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(pos1);

                if (biome.getSpawningChance() <= 0 || biome.getSpawnableList(EnumCreatureType.MONSTER).isEmpty())
                    continue;

                Chunk chunk = player.worldObj.getChunkFromBlockCoords(pos1);
                for (int yo = y1; yo <= y2; yo++) {
                    BlockPos pos = new BlockPos(px + xo, py + yo, pz + zo);
                    byte mode = getSpawnModeAt(pos, chunk, player.worldObj);
                    if (mode != 0)
                        newCache.put(pos, mode);
                }
            }
        }

        if (overlayCache != null)
            overlayCache.clear();
        overlayCache = newCache;
    }

    private static byte getSpawnModeAt(BlockPos pos, Chunk chunk, World world) {
        IBlockState block = chunk.getBlockState(pos.down());

        if (!checkCollision(pos, world))
            return 0;

        if (!block.getBlock().canCreatureSpawn(block,world, pos.down(), EntityLiving.SpawnPlacementType.ON_GROUND) || chunk.getLightFor(EnumSkyBlock.BLOCK, pos) > 7)
            return 0;

        if (chunk.getLightFor(EnumSkyBlock.SKY, pos) > 7)
            return 1;

        return 2;
    }

    private static boolean checkCollision(BlockPos pos, World world) {
        IBlockState block1 = world.getBlockState(pos);
        IBlockState block2 = world.getBlockState(pos.up());
        if (block1.getBlock().isAir(block1,world, pos) && block2.getBlock().isAir(block2,world, pos))  //Don't check because Air has no Collision Box
            return true;
        else if (block1.getBlock().isNormalCube(block1,world, pos) || block2.getBlock().isNormalCube(block2, world, pos)) //Don't check because a check on normal Cubes will/should return false ( 99% collide ).
            return false;
        AxisAlignedBB bb = ZOMBIE_BB.offset(pos.getX(), pos.getY(), pos.getZ());
        return world.getCollisionBoxes(bb).isEmpty() && !world.isAnyLiquid(bb);
    }

    private static void renderCross(BlockPos pos, float r, float g, float b) {
        double y = pos.getY() + 0.005D;

        double x0 = pos.getX();
        double x1 = x0 + 1;
        double z0 = pos.getZ();
        double z1 = z0 + 1;

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer renderer = tess.getBuffer();

        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos(x0, y, z0).color(r, g, b, 1).endVertex();
        renderer.pos(x1, y, z1).color(r, g, b, 1).endVertex();

        renderer.pos(x1, y, z0).color(r, g, b, 1).endVertex();
        renderer.pos(x0, y, z1).color(r, g, b, 1).endVertex();
        tess.draw();
    }

    public static void clearCache() {
        if (overlayCache != null)
            overlayCache.clear();
    }
}
