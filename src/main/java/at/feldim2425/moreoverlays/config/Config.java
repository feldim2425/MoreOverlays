package at.feldim2425.moreoverlays.config;

import java.util.List;

import static at.feldim2425.moreoverlays.config.ConfigHandler.config;

public class Config {

    public static int light_UpRange;
    public static int light_DownRange;
    public static int light_HRange;
    public static boolean light_IgnoreLayer;

    public static int chunk_EdgeRadius;
    public static boolean chunk_ShowMiddle;

    public static boolean itemsearch_DisableText;
    public static boolean itemsearch_FadeoutText;

    public static void loadValues(){

        config.setCategoryComment("lightoverlay","Settings for the light / mobspawn overlay");
        light_UpRange = config.get("lightoverlay","uprange",4,"Range of the lightoverlay (positive Y)").getInt();
        light_DownRange =  config.get("lightoverlay","downrange",16,"Range of the lightoverlay (negative Y)").getInt();
        light_HRange =  config.get("lightoverlay","hrange",16,"Range of the lightoverlay (Horizontal N,E,S,W)").getInt();
        light_IgnoreLayer =  config.get("lightoverlay","ignoreLayer", false,"Ignore if there in no 2 Block space to spawn. (Less lag if true)").getBoolean();

        config.setCategoryComment("chunkbounds","Settings for the chunk bounds overlay");
        chunk_EdgeRadius = config.get("chunkbounds","radius", 1, "Radius (in Chunks) to show the edges (red line)").getInt();
        chunk_ShowMiddle = config.get("chunkbounds","middle", true, "Show the middle of the current Chunk (yellow line)").getBoolean();

        config.setCategoryComment("itemsearch","Settings for the item search feature");
        itemsearch_DisableText = config.get("itemsearch","disabletext", true, "Show 'Item Search Disabled'\nIf set to 'false' the text will only show if the Item Search is enabled").getBoolean();
        itemsearch_FadeoutText = config.get("itemsearch","fadouttext", true, "Show the 'Item Search' text only for one secound and fade out").getBoolean();

        if(config.hasChanged())
            config.save();
    }

    public static void getCategories(List<String> list){
        list.add("lightoverlay");
        list.add("chunkbounds");
        list.add("itemsearch");
    }
}
