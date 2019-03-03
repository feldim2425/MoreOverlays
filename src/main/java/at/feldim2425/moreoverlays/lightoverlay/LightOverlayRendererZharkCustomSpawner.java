package at.feldim2425.moreoverlays.lightoverlay;

import java.util.List;

import drzhark.customspawner.CustomSpawner;
import drzhark.customspawner.environment.EnvironmentSettings;
import drzhark.customspawner.utils.CMSUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;

public class LightOverlayRendererZharkCustomSpawner extends LightOverlayRenderer {

	protected boolean checkIfBiomeSpawnlistIsEmpty(World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		Biome biome = world.getBiome(pos);
		if(biome.getSpawningChance() <= 0)
			return true;
		
		EnvironmentSettings environment = CMSUtils.getEnvironment(world);
		if (environment == null) {
		  System.out.println("return");
		}
		List<SpawnListEntry> possibleSpawns = CustomSpawner.instance().getPossibleCustomCreatures(world, environment.entitySpawnTypes.get("MONSTER"), x, y, z);

		return possibleSpawns.isEmpty();
	}
}
