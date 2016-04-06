package at.feldim2425.moreoverlays.config;

import at.feldim2425.moreoverlays.MoreOverlays;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    public static final String CONFIG_FILENAME = "MoreOverlays.cfg";
    public static Configuration config;
    public static List<String> categories = new ArrayList<>();

    public static void init(FMLPreInitializationEvent event){
        File configFile = new File(event.getModConfigurationDirectory(), CONFIG_FILENAME);
        config = new Configuration(configFile);

        MinecraftForge.EVENT_BUS.register(new ConfigHandler());
        config.load();
        Config.getCategories(categories);
        Config.loadValues();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
        if(!event.modID.equals(MoreOverlays.MOD_ID))
            return;
        Config.loadValues();
    }

}
