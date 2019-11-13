package at.feldim2425.moreoverlays.api.lightoverlay;

import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class LightScannerBase implements ILightScanner {

	protected List<Pair<BlockPos, Byte>> overlayCache = new ArrayList<>();

	@Override
	public void update(PlayerEntity player) {
		int px = (int) Math.floor(player.posX);
		int py = (int) Math.floor(player.posY);
		int pz = (int) Math.floor(player.posZ);

		int y1 = py - Config.light_DownRange;
		int y2 = py + Config.light_UpRange;

		overlayCache.clear();
		for (int xo = -Config.light_HRange; xo <= Config.light_HRange; xo++) {
			for (int zo = -Config.light_HRange; zo <= Config.light_HRange; zo++) {
				BlockPos pos1 = new BlockPos(px + xo, py, pz + zo);
				if(!shouldCheck(pos1, player.world)){
					continue;
				}
				for (int y = y1; y <= y2; y++) {
					BlockPos pos = new BlockPos(px + xo, y, pz + zo);
					byte mode = getSpawnModeAt(pos, player.world);
					if (mode != 0) {
						overlayCache.add(Pair.of(pos, mode));
					}
				}
			}
		}
	}

	@Override
	public void clear() {
		overlayCache.clear();
	}

	@Override
	public List<Pair<BlockPos, Byte>> getLightModes() {
		return overlayCache;
	}

	public boolean shouldCheck(BlockPos pos, World world){
		if(Config.light_IgnoreSpawn){
			return true;
		}
		Biome biome = world.getBiome(pos);
		return biome.getSpawningChance() > 0 && !biome.getSpawns(EntityClassification.MONSTER).isEmpty();
	}

	public abstract byte getSpawnModeAt(BlockPos pos, World world);
}
