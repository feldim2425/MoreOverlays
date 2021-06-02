package at.feldim2425.moreoverlays.lightoverlay.integration;

import at.feldim2425.moreoverlays.lightoverlay.LightScannerVanilla;
import drzhark.customspawner.CustomSpawner;
import drzhark.customspawner.environment.EnvironmentSettings;
import drzhark.customspawner.utils.CMSUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class CustomSpawnerLightScanner extends LightScannerVanilla {

	@Override
	public boolean shouldCheck(BlockPos pos, World world) {
		Biome biome = world.getBiome(pos);
		if(biome.getSpawningChance() <= 0)
			return true;

		EnvironmentSettings environment = CMSUtils.getEnvironment(world);
		if (environment == null) {
			return false;
		}
		List<Biome.SpawnListEntry> possibleSpawns = CustomSpawner.instance().getPossibleCustomCreatures(world, environment.entitySpawnTypes.get("MONSTER"), pos.getX(), pos.getY(), pos.getZ());

		return possibleSpawns != null && !possibleSpawns.isEmpty();
	}
}
