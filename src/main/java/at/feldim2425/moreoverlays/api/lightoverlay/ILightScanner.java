package at.feldim2425.moreoverlays.api.lightoverlay;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ILightScanner {

	void update(EntityPlayer player);

	void clear();

	List<Pair<BlockPos, Byte>> getLightModes();
}
