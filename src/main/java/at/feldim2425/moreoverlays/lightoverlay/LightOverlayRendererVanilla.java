package at.feldim2425.moreoverlays.lightoverlay;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class LightOverlayRendererVanilla extends LightOverlayRenderer {

	@Override
	protected boolean checkIfBiomeSpawnlistIsEmpty(World world, int x, int y, int z) {
		BlockPos pos1 = new BlockPos(x, y, z);
		Biome biome = world.getBiome(pos1);

		return (biome.getSpawningChance() <= 0 || biome.getSpawnableList(EnumCreatureType.MONSTER).isEmpty());
	}

}
