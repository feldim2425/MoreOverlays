package at.feldim2425.moreoverlays;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = MoreOverlays.MOD_ID, version = MoreOverlays.VERSION, name = MoreOverlays.NAME, clientSideOnly = true, dependencies = "after:JEI@[2.28.12.180,);")
public class MoreOverlays {

    public static final String MOD_ID = "moreoverlays";
    public static final String NAME = "MoreOverlays";
    public static final String VERSION = "0.4";

    @SidedProxy(clientSide = "at.feldim2425.moreoverlays.Proxy")
    public static Proxy proxy;

    public static Logger logger = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (proxy != null)
            proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (proxy != null)
            proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (proxy != null)
            proxy.postInit();
    }
}
