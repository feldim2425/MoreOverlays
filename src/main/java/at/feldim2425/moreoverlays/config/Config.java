package at.feldim2425.moreoverlays.config;

import java.util.List;

import static at.feldim2425.moreoverlays.config.ConfigHandler.config;

public class Config {

    public static int light_UpRange;
    public static int light_DownRange;
    public static int light_HRange;

    public static int chunk_EdgeRadius;
    public static boolean chunk_ShowMiddle;

    public static void loadValues(){

        config.setCategoryComment("lightoverlay","Settings for the light / mobspawn overlay");
        light_UpRange = config.get("lightoverlay","uprange",4,"Range of the lightoverlay (positive Y)").getInt();
        light_DownRange =  config.get("lightoverlay","downrange",16,"Range of the lightoverlay (negative Y)").getInt();
        light_HRange =  config.get("lightoverlay","hrange",16,"Range of the lightoverlay (Horizontal N,E,S,W)").getInt();

        config.setCategoryComment("chunkbounds","Settings for the chunk bounds overlay");
        chunk_EdgeRadius = config.get("chunkbounds","radius", 1, "Radius (in Chunks) to show the edges (red line)").getInt();
        chunk_ShowMiddle = config.get("chunkbounds","middle", true, "Show the middle of the current Chunk (yellow line)").getBoolean();

        if(config.hasChanged())
            config.save();
    }

    public static void getCategories(List<String> list){
        list.add("lightoverlay");
        list.add("chunkbounds");
    }
}
