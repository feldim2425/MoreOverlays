package at.feldim2425.moreoverlays.config;

import net.minecraft.init.Items;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import at.feldim2425.moreoverlays.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {

	@Nullable
	private static Config instance;
	public static final String CONFIG_FILENAME = "MoreOverlays.cfg";
	private static final String configKeyPrefix = "config.moreoverlays";
	private static Configuration config;
	public static List<String> categories = new ArrayList<>();

	public static int light_UpRange;
	public static int light_DownRange;
	public static int light_HRange;
	public static boolean light_IgnoreLayer;
	public static int light_SaveLevel;

	public static int chunk_EdgeRadius;
	public static boolean chunk_ShowMiddle;

	public static Set<String> itemsearch_matchNbt;
	//public static boolean itemsearch_ShowItemSearchKey;
	//public static boolean itemsearch_FadeoutText;

	public static int render_chunkEdgeColor;
	public static int render_chunkGridColor;
	public static int render_chunkMiddleColor;
	public static float render_chunkLineWidth;
	public static int render_spawnAColor;
	public static int render_spawnNColor;
	public static float render_spawnLineWidth;
	public static final Logger LOGGER = LogManager.getLogger();

	public Config(File moreOverlaysConfigurationDir) {
		instance = this;
		final File configFile = new File(moreOverlaysConfigurationDir, Config.CONFIG_FILENAME);
		LOGGER.debug(configFile.getAbsolutePath());
		this.config = new Configuration(configFile, "0.4.0");
		Config.getCategories(categories);
		Config.loadValues();
	}

	public static void loadValues() {

		config.setCategoryComment("lightoverlay", "Settings for the light / mobspawn overlay");
		light_UpRange = config.get("lightoverlay", "uprange", 4, "Range of the lightoverlay (positive Y)").getInt();
		light_DownRange = config.get("lightoverlay", "downrange", 16, "Range of the lightoverlay (negative Y)").getInt();
		light_HRange = config.get("lightoverlay", "hrange", 16, "Range of the lightoverlay (Horizontal N,E,S,W)").getInt();
		light_IgnoreLayer = config.get("lightoverlay", "ignoreLayer", false, "Ignore if there in no 2 Block space to spawn. (Less lag if true)").getBoolean();
		light_SaveLevel = config.get("lightoverlay", "saveLevel", 8, "Minimum save light level where no mobs can spawn").getInt();

		config.setCategoryComment("chunkbounds", "Settings for the chunk bounds overlay");
		chunk_EdgeRadius = config.get("chunkbounds", "radius", 1, "Radius (in Chunks) to show the edges (red line)").getInt();
		chunk_ShowMiddle = config.get("chunkbounds", "middle", true, "Show the middle of the current Chunk (yellow line)").getBoolean();

		config.setCategoryComment("rendersettings", "Settings for lines & colors\nValues: 0xRRGGBB (Hex)");
		render_chunkEdgeColor = config.get("rendersettings", "chunk_edge_color", 0xBB2222, "Color for the chunk edge").getInt();
		render_chunkGridColor = config.get("rendersettings", "chunk_grid_color", 0x228B22, "Color for the chunk grid").getInt();
		render_chunkMiddleColor = config.get("rendersettings", "chunk_mid_color", 0xFDFD96, "Color for the middle chunk line").getInt();
		render_chunkLineWidth = (float) config.get("rendersettings", "chunk_line_width", 1.5, "Line width for chunk boundaries").getDouble();
		render_spawnAColor = config.get("rendersettings", "spawn_always_color", 0xBB2222, "Color the X that marks \"Spawns always possible\"").getInt();
		render_spawnNColor = config.get("rendersettings", "spawn_night_color", 0xFDFD96, "Color the X that marks \"Spawns at night possible\"").getInt();
		render_spawnLineWidth = (float) config.get("rendersettings", "spawn_line_width", 2, "Line width for spawn indication").getDouble();
	}

	public static void getCategories(List<String> list) {
		list.add("lightoverlay");
		list.add("chunkbounds");
		list.add("itemsearch");
		list.add("rendersettings");
	}
}
