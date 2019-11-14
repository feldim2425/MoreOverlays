package at.feldim2425.moreoverlays.lightoverlay;

import at.feldim2425.moreoverlays.api.lightoverlay.LightScannerBase;
import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class LightScannerVanilla extends LightScannerBase {

	private final static AxisAlignedBB TEST_BB = new AxisAlignedBB(0.6D / 2D, 0, 0.6D / 2D, 1D - 0.6D / 2D, 1D, 1D - 0.6D / 2D);

	@Override
	public byte getSpawnModeAt(BlockPos pos, World world) {
		if (world.getLightFor(LightType.BLOCK, pos) >= Config.light_SaveLevel.get())
			return 0;

		BlockState state = world.getBlockState(pos.down());
		if (!state.getBlock().canCreatureSpawn(state, world, pos.down(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, null))
			return 0;

		if (!checkCollision(pos, world))
			return 0;

		if (world.getLightFor(LightType.SKY, pos) >= Config.light_SaveLevel.get())
			return 1;

		return 2;
	}

	private static boolean checkCollision(BlockPos pos, World world) {
		BlockState block1 = world.getBlockState(pos);

		if (block1.isNormalCube(world, pos) || (!Config.light_IgnoreLayer.get() && world.getBlockState(pos.up()).isNormalCube(world, pos.up()))) //Don't check because a check on normal Cubes will/should return false ( 99% collide ).
			return false;
		else if (world.isAirBlock(pos) && (Config.light_IgnoreLayer.get() || world.isAirBlock(pos.up())))  //Don't check because Air has no Collision Box
			return true;

		AxisAlignedBB bb = TEST_BB.offset(pos.getX(), pos.getY(), pos.getZ());
		if (world.getCollisionShapes(null, bb).count() == 0 && !world.containsAnyLiquid(bb)) {
			if (Config.light_IgnoreLayer.get())
				return true;
			else {
				AxisAlignedBB bb2 = bb.offset(0, 1, 0);
				return world.getCollisionShapes(null, bb2).count() == 0 && !world.containsAnyLiquid(bb2);
			}
		}
		return false;
	}
}