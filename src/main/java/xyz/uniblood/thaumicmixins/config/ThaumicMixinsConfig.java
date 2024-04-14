package xyz.uniblood.thaumicmixins.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ThaumicMixinsConfig {

    //Category names
    static final String categoryStructures = "structures";

    public static boolean moundEnabled = true;
    public static int moundFrequency = 150;
    public static boolean stoneRingEnabled = true;
    public static int stoneRingFrequency = 66;
    public static boolean hillTopStonesEnabled = true;
    public static int hillTopStonesFrequency = 40;
    public static boolean totemEnabled = true;
    public static int totemFrequency = 10;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        moundEnabled = configuration.getBoolean("Mound Enabled", categoryStructures, moundEnabled, "");
        moundFrequency = configuration.getInt("Mound Frequency", categoryStructures, moundFrequency, 0, 999999, "Higher is less common");
        stoneRingEnabled = configuration.getBoolean("Eldritch Obelisk Enabled", categoryStructures, stoneRingEnabled, "");
        stoneRingFrequency = configuration.getInt("Eldritch Obelisk Frequency", categoryStructures, stoneRingFrequency, 0, 999999, "Higher is less common");
        hillTopStonesEnabled = configuration.getBoolean("Hilltop Stones Enabled", categoryStructures, hillTopStonesEnabled, "");
        hillTopStonesFrequency = configuration.getInt("Hilltop Stones Frequency", categoryStructures, hillTopStonesFrequency, 0, 999999, "Higher is less common");
        totemEnabled = configuration.getBoolean("Totem Enabled", categoryStructures, totemEnabled,"");
        totemFrequency = configuration.getInt("Totem Frequency", categoryStructures, totemFrequency, 0, 999999, "Higher is less common");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
