package xyz.uniblood.thaumicmixins.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ThaumicMixinsConfig {

    //Category names
    static final String categoryCommands = "commands";
    static final String categoryStructures = "structures";
    static final String categoryLoot = "loot";

    // Commands
    public static boolean enableCommand = true;
    public static int commandPermissionLevel = 2;
    public static boolean enableFindResearch = true;
    public static boolean enableForgetResearch = true;
    public static boolean enableForgetScanned = true;
    public static boolean enableListResearch = true;

    // Structure
    public static boolean moundEnabled = true;
    public static int moundFrequency = 150;
    public static boolean stoneRingEnabled = true;
    public static int stoneRingFrequency = 66;
    public static boolean hillTopStonesEnabled = true;
    public static int hillTopStonesFrequency = 40;
    public static boolean totemEnabled = true;
    public static int totemFrequency = 10;

    public static boolean moundRareLootEnabled = true;
    public static int[] auraNodeDimWhitelist = {0, 7};

    // Loot
    public static boolean championLootBagEnabled = true;
    public static int championLootBagRarityMax = 2;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        enableCommand = configuration.getBoolean("Enable Command", categoryCommands, enableCommand, "Enable the /tmixins command");
        commandPermissionLevel = configuration.getInt("Command Required Permission Level", categoryCommands, commandPermissionLevel, 0, 4, "0 (all), 1 (moderator), 2 (gamemaster), 3 (admin), and 4 (owner)");
        enableFindResearch = configuration.getBoolean("findResearch Enabled", categoryCommands, enableFindResearch, "Enable the 'findResearch' subcommand");
        enableForgetResearch = configuration.getBoolean("forgetResearch Enabled", categoryCommands, enableForgetResearch, "Enable the 'forgetResearch' subcommand");
        enableForgetScanned = configuration.getBoolean("forgetScanned Enabled", categoryCommands, enableForgetScanned, "Enable the 'forgetScanned' subcommand");
        enableListResearch = configuration.getBoolean("listResearch Enabled", categoryCommands, enableListResearch, "Enable the 'listResearch' subcommand");

        // Structures
        moundEnabled = configuration.getBoolean("Mound Enabled", categoryStructures, moundEnabled, "");
        moundFrequency = configuration.getInt("Mound Frequency", categoryStructures, moundFrequency, 0, 999999, "Higher is less common");
        stoneRingEnabled = configuration.getBoolean("Eldritch Obelisk Enabled", categoryStructures, stoneRingEnabled, "");
        stoneRingFrequency = configuration.getInt("Eldritch Obelisk Frequency", categoryStructures, stoneRingFrequency, 0, 999999, "Higher is less common");
        hillTopStonesEnabled = configuration.getBoolean("Hilltop Stones Enabled", categoryStructures, hillTopStonesEnabled, "");
        hillTopStonesFrequency = configuration.getInt("Hilltop Stones Frequency", categoryStructures, hillTopStonesFrequency, 0, 999999, "Higher is less common");
        totemEnabled = configuration.getBoolean("Totem Enabled", categoryStructures, totemEnabled,"");
        totemFrequency = configuration.getInt("Totem Frequency", categoryStructures, totemFrequency, 0, 999999, "Higher is less common");

        moundRareLootEnabled = configuration.getBoolean("Mound Rare Loot Enabled", categoryStructures, moundRareLootEnabled,"Remove Rare Urns and Crates from the mounds");
        auraNodeDimWhitelist = configuration.get(categoryStructures, "Aura Node Dimension Whitelist",  auraNodeDimWhitelist, "").getIntList();

        // Loot
        championLootBagEnabled = configuration.getBoolean("Champion Loot Bag Drop Enabled", categoryLoot, championLootBagEnabled,"Toggle champion mobs dropping loot bags");
        championLootBagRarityMax = configuration.getInt("Max Champion Loot Bag Rarity", categoryLoot, championLootBagRarityMax, 0, 2, "Set max rarity for champion mob loot bags [0 = common, 1 = uncommon, 2 = rare]");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
