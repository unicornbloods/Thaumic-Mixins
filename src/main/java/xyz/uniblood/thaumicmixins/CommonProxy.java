package xyz.uniblood.thaumicmixins;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import xyz.uniblood.thaumicmixins.commands.CommandThaumicMixins;
import xyz.uniblood.thaumicmixins.config.ThaumicMixinsConfig;
import xyz.uniblood.thaumicmixins.modsupport.ThaumcraftSupport;
import xyz.uniblood.thaumicmixins.whitelisting.ChampionWhitelist;

import java.io.File;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        String configFolder = event.getModConfigurationDirectory().getAbsolutePath() + File.separator;
        ThaumicMixinsConfig.synchronizeConfiguration(new File(configFolder + "ThaumicMixins.cfg"));
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        new ChampionWhitelist();

        if (!Loader.isModLoaded("bugtorch")) {
            ThaumcraftSupport.enableSupport();
        }
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        if (ThaumicMixinsConfig.enableCommand) {
            event.registerServerCommand(new CommandThaumicMixins());
        }
    }
}
