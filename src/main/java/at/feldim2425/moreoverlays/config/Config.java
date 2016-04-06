package at.feldim2425.moreoverlays.config;

import java.util.List;

import static at.feldim2425.moreoverlays.config.ConfigHandler.config;

public class Config {

    public static int light_UpRange;
    public static int light_DownRange;
    public static int light_HRange;


    public static void loadValues(){

        config.setCategoryComment("lightoverlay","Settings for the light / mobspawn overlay");
        light_UpRange = config.get("lightoverlay","uprange",4,"Range of the lightoverlay (positive Y)").getInt();
        light_DownRange =  config.get("lightoverlay","downrange",16,"Range of the lightoverlay (negative Y)").getInt();
        light_HRange =  config.get("lightoverlay","hrange",16,"Range of the lightoverlay (Horizontal N,E,S,W)").getInt();

        if(config.hasChanged())
            config.save();
    }

    public static void getCategories(List<String> list){
        list.add("lightoverlay");
    }
}
