package at.feldim2425.moreoverlays.lightoverlay;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public abstract class LightOverlayRenderer {

	private static LightOverlayRenderer INSTANCE;
	public static LightOverlayRenderer instance() {
		if(INSTANCE == null) {
			try {
				Class.forName("drzhark.customspawner.CustomSpawner", false, LightOverlayRenderer.class.getClassLoader());
				INSTANCE = new LightOverlayRendererZharkCustomSpawner();
			}
			catch (ClassNotFoundException e) {
				INSTANCE = new LightOverlayRendererVanilla();
			}
		}
			
		return INSTANCE;
	}
	
	private final ResourceLocation BLANK_TEX = new ResourceLocation(MoreOverlays.MOD_ID, "textures/blank.png");
	private final AxisAlignedBB TEST_BB = new AxisAlignedBB(0.6D / 2D, 0, 0.6D / 2D, 1D - 0.6D / 2D, 1D, 1D - 0.6D / 2D);
	private final List<Pair<BlockPos, Byte>> overlayCache = new LinkedList<>();
	private RenderManager render = Minecraft.getMinecraft().getRenderManager();

	protected abstract boolean checkIfBiomeSpawnlistIsEmpty(World world, int x, int y, int z);
	

	public void renderOverlays() {
		Minecraft.getMinecraft().renderEngine.bindTexture(BLANK_TEX);
		GlStateManager.pushMatrix();
		GL11.glLineWidth(Config.render_spawnLineWidth);
		GlStateManager.translate(-render.viewerPosX, -render.viewerPosY, -render.viewerPosZ);

		float ar = ((float) ((Config.render_spawnAColor >> 16) & 0xFF)) / 255F;
		float ag = ((float) ((Config.render_spawnAColor >> 8) & 0xFF)) / 255F;
		float ab = ((float) (Config.render_spawnAColor & 0xFF)) / 255F;

		float nr = ((float) ((Config.render_spawnNColor >> 16) & 0xFF)) / 255F;
		float ng = ((float) ((Config.render_spawnNColor >> 8) & 0xFF)) / 255F;
		float nb = ((float) (Config.render_spawnNColor & 0xFF)) / 255F;

		synchronized (overlayCache) {
			for (Pair<BlockPos, Byte> entry : overlayCache) {
				Byte mode = entry.getValue();
				if (mode == null || mode == 0)
					continue;
				else if (mode == 1)
					renderCross(entry.getKey(), nr, ng, nb);
				else if (mode == 2)
					renderCross(entry.getKey(), ar, ag, ab);
			}
		}

		GlStateManager.popMatrix();
	}


	public synchronized void refreshCache() {
		if (Minecraft.getMinecraft().player == null)
			return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		int px = (int) Math.floor(player.posX);
		int py = (int) Math.floor(player.posY);
		int pz = (int) Math.floor(player.posZ);

		int y1 = py - Config.light_DownRange;
		int y2 = py + Config.light_UpRange;

		synchronized (overlayCache) {
			overlayCache.clear();
			for (int xo = -Config.light_HRange; xo <= Config.light_HRange; xo++) {
				for (int zo = -Config.light_HRange; zo <= Config.light_HRange; zo++) {
					
					if (checkIfBiomeSpawnlistIsEmpty(player.world, px + xo, py, pz + zo))
						continue;

					for (int y = y1; y <= y2; y++) {
						BlockPos pos = new BlockPos(px + xo, y, pz + zo);
						byte mode = getSpawnModeAt(pos, player.world);
						if (mode != 0)
							overlayCache.add(Pair.of(pos, mode));
					}
				}
			}
		}
	}

	private byte getSpawnModeAt(BlockPos pos, World world) {

		if (world.getLightFor(EnumSkyBlock.BLOCK, pos) >= Config.light_SaveLevel)
			return 0;

		IBlockState state = world.getBlockState(pos.down());
		if (!state.getBlock().canCreatureSpawn(state, world, pos.down(), EntityLiving.SpawnPlacementType.ON_GROUND))
			return 0;

		if (!checkCollision(pos, world))
			return 0;

		if (world.getLightFor(EnumSkyBlock.SKY, pos) >= Config.light_SaveLevel)
			return 1;
		
		return 2;
	}

	private boolean checkCollision(BlockPos pos, World world) {
		IBlockState block1 = world.getBlockState(pos);

		if (block1.isNormalCube() || (!Config.light_IgnoreLayer && world.getBlockState(pos.up()).isNormalCube())) //Don't check because a check on normal Cubes will/should return false ( 99% collide ).
			return false;
		else if (world.isAirBlock(pos) && (Config.light_IgnoreLayer || world.isAirBlock(pos.up())))  //Don't check because Air has no Collision Box
			return true;

		AxisAlignedBB bb = TEST_BB.offset(pos.getX(), pos.getY(), pos.getZ());
		if (world.getCollisionBoxes(null, bb).isEmpty() && !world.containsAnyLiquid(bb)) {
			if (Config.light_IgnoreLayer)
				return true;
			else {
				AxisAlignedBB bb2 = bb.offset(0, 1, 0);
				return world.getCollisionBoxes(null, bb2).isEmpty() && !world.containsAnyLiquid(bb2);
			}
		}
		return false;
	}

	private void renderCross(BlockPos pos, float r, float g, float b) {
		double y = pos.getY() + 0.005D;

		double x0 = pos.getX();
		double x1 = x0 + 1;
		double z0 = pos.getZ();
		double z1 = z0 + 1;

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder renderer = tess.getBuffer();

		renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos(x0, y, z0).color(r, g, b, 1).endVertex();
		renderer.pos(x1, y, z1).color(r, g, b, 1).endVertex();

		renderer.pos(x1, y, z0).color(r, g, b, 1).endVertex();
		renderer.pos(x0, y, z1).color(r, g, b, 1).endVertex();
		tess.draw();
	}

	public void clearCache() {
		overlayCache.clear();
	}
}
