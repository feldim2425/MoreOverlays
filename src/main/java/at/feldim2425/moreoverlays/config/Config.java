package at.feldim2425.moreoverlays.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
	public static ForgeConfigSpec config_client;

	public static ForgeConfigSpec.IntValue light_UpRange;
	public static ForgeConfigSpec.IntValue light_DownRange;
	public static ForgeConfigSpec.IntValue light_HRange;
	public static ForgeConfigSpec.BooleanValue light_IgnoreLayer;
	public static ForgeConfigSpec.BooleanValue light_IgnoreSpawn;
	public static ForgeConfigSpec.IntValue light_SaveLevel;

	public static ForgeConfigSpec.IntValue chunk_EdgeRadius;
	public static ForgeConfigSpec.BooleanValue chunk_ShowMiddle;

	public static ForgeConfigSpec.IntValue render_chunkEdgeColor;
	public static ForgeConfigSpec.IntValue render_chunkGridColor;
	public static ForgeConfigSpec.IntValue render_chunkMiddleColor;
	public static ForgeConfigSpec.DoubleValue render_chunkLineWidth;
	public static ForgeConfigSpec.IntValue render_spawnAColor;
	public static ForgeConfigSpec.IntValue render_spawnNColor;
	public static ForgeConfigSpec.DoubleValue render_spawnLineWidth;


	public static void initialize() {
		final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.comment("Settings for the light / mobspawn overlay").push("lightoverlay");
		light_UpRange = builder.comment("Range of the lightoverlay (positive Y)").defineInRange("uprange", 4, 0, Integer.MAX_VALUE);
		light_DownRange = builder.comment("Range of the lightoverlay (negative Y)").defineInRange("downrange", 16, 0, Integer.MAX_VALUE);
		light_HRange = builder.comment("Range of the lightoverlay (Horizontal N,E,S,W)").defineInRange("hrange", 16, 0, Integer.MAX_VALUE);
		light_IgnoreLayer = builder.comment("Ignore if there in no 2 Block space to spawn. (Less lag if true)").define("ignoreLayer", false);
		light_IgnoreSpawn = builder.comment("Ignore if mobs can actually spawn and just go by light value").define("ignoreSpawns", false);
		light_SaveLevel = builder.comment("Minimum save light level where no mobs can spawn").defineInRange("saveLevel", 8, 0, Integer.MAX_VALUE);
		builder.pop();

		builder.comment("Settings for the chunk bounds overlay").push("chunkbounds");
		chunk_EdgeRadius = builder.comment("Radius (in Chunks) to show the edges (red line)").defineInRange("radius", 1, 0, Integer.MAX_VALUE);
		chunk_ShowMiddle = builder.comment("Show the middle of the current Chunk (yellow line)").define("middle", true);
		builder.pop();
		
		builder.comment("General render settings.\nLine thickness, Colors, ...").push("rendersettings");
		render_chunkEdgeColor = builder.comment("Color for the chunk edge").defineInRange("chunk_edge_color", 0xFF0000, 0, 0xFFFFFF);
		render_chunkGridColor = builder.comment("Color for the chunk grid").defineInRange("chunk_grid_color", 0x00FF00, 0, 0xFFFFFF);
		render_chunkMiddleColor = builder.comment("Color for the middle chunk line").defineInRange("chunk_mid_color", 0xFFFF00, 0, 0xFFFFFF);
		render_chunkLineWidth = builder.comment("Line width for chunk boundaries").defineInRange("chunk_line_width", 1.5, 0, Double.MAX_VALUE);
		render_spawnAColor = builder.comment("Color the X that marks \"Spawns always possible\"").defineInRange("spawn_always_color", 0xFF0000, 0, 0xFFFFFF);
		render_spawnNColor = builder.comment("Color the X that marks \"Spawns at night possible\"").defineInRange("spawn_night_color", 0xFFFF00, 0, 0xFFFFFF);
		render_spawnLineWidth = builder.comment("Line width for spawn indication").defineInRange("spawn_line_width", 2, 0, Double.MAX_VALUE);
		builder.pop();

		config_client = builder.build();
	}

	/*public static void getCategories(List<String> list) {
		list.add("lightoverlay");
		list.add("chunkbounds");
		//list.add("itemsearch");
		list.add("rendersettings");
	}*/
}
