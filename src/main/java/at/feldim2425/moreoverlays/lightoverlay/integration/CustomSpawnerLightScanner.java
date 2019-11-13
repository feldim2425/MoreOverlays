package at.feldim2425.moreoverlays.lightoverlay.integration;

import at.feldim2425.moreoverlays.lightoverlay.LightScannerVanilla;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class CustomSpawnerLightScanner extends LightScannerVanilla {

	/*
	 * DrZharky Custom Spawner integration disabled until that mod get's updated to 1.14
	 */

	@Override
	public boolean shouldCheck(BlockPos pos, World world) {
		Biome biome = world.getBiome(pos);
		if(biome.getSpawningChance() <= 0){
			return true;
		}
		return false;
		/*
		EnvironmentSettings environment = CMSUtils.getEnvironment(world);
		if (environment == null) {
			return false;
		}
		List<Biome.SpawnListEntry> possibleSpawns = CustomSpawner.instance().getPossibleCustomCreatures(world, environment.entitySpawnTypes.get("MONSTER"), pos.getX(), pos.getY(), pos.getZ());

		return !possibleSpawns.isEmpty();*/
	}
}
