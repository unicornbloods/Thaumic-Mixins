package xyz.uniblood.thaumicmixins.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ThaumicMixinsConfig {

    //Category names
    static final String categoryStructures = "structures";

    public static int moundFrequency = 150;
    public static int stoneringFrequency = 66;
    public static int hilltopStonesFrequency = 40;
    public static int totemFrequency = 10;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        moundFrequency = configuration.getInt("Mound Frequency", categoryStructures, moundFrequency, 0, 999999, "");
        stoneringFrequency = configuration.getInt("Eldritch Obelisk Frequency", categoryStructures, stoneringFrequency, 0, 999999, "");
        hilltopStonesFrequency = configuration.getInt("Hilltop Stones Frequency", categoryStructures, hilltopStonesFrequency, 0, 999999, "");
        totemFrequency = configuration.getInt("Totem Frequency", categoryStructures, totemFrequency, 0, 999999, "");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
