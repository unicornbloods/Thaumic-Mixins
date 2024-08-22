package xyz.uniblood.thaumicmixins;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ThaumicMixins.MODID, version = Tags.VERSION, name = "Thaumic Mixins", acceptedMinecraftVersions = "[1.7.10]")
public class ThaumicMixins {

    public static final String MODID = "thaumicmixins";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "xyz.uniblood.thaumicmixins.ClientProxy", serverSide = "xyz.uniblood.thaumicmixins.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
