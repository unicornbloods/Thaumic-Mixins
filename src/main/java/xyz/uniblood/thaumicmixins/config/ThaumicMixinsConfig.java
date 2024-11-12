package xyz.uniblood.thaumicmixins.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ThaumicMixinsConfig {

    //Category names
    static final String categoryBugfixes = "bugfixes";
    static final String categoryCommands = "commands";
    static final String categoryStructures = "structures";
    static final String categoryLoot = "loot";
    static final String categoryEntities = "entities";

    // Blocks
    public static int thaumiumBlockMetadata = 4;

    // Bugfixes
    public static boolean enableCosmeticSolidBeaconFix = true;

    // Commands
    public static boolean enableCommand = true;
    public static int commandPermissionLevel = 2;
    public static boolean enableFindResearch = true;
    public static boolean enableForgetResearch = true;
    public static boolean enableForgetScanned = true;
    public static boolean enableListResearch = true;
    public static boolean enableUpdateNode = true;
    public static boolean enableSummonNode = true;

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

    public static int[] oreDimWhitelist = {0, 7};

    // Loot
    public static boolean championLootBagEnabled = true;
    public static int championLootBagRarityMax = 2;

    // Entities
    public static String[] championMobBlacklist = {
      "Creeper",
      "Thaumcraft.GiantBrainyZombie"
    };
    public static String[] championMobWhitelist = {
      "Zombie:0",
      "Spider:0",
      "Blaze:0",
      "Enderman:0",
      "Skeleton:0",
      "Witch:1",
      "Thaumcraft.EldritchCrab:0",
      "Thaumcraft.Taintacle:2",
      "Thaumcraft.Wisp:1",
      "Thaumcraft.InhabitedZombie:3"
    };

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        // Bugfixes
        enableCosmeticSolidBeaconFix = configuration.getBoolean("Enable BlockCosmeticSolid Beacon Fix", categoryBugfixes, enableCosmeticSolidBeaconFix, "Restrict BlockCosmeticSolid so only Thaumium Blocks are registered as beacon base blocks. Disable to resume normal behavior (i.e. allowing Tallow Blocks and Arcane Stone Blocks, among others, to register as beacon base blocks).");

        // Commands
        enableCommand = configuration.getBoolean("Enable Command", categoryCommands, enableCommand, "Enable the /tmixins command");
        commandPermissionLevel = configuration.getInt("Command Required Permission Level", categoryCommands, commandPermissionLevel, 0, 4, "0 (all), 1 (moderator), 2 (gamemaster), 3 (admin), and 4 (owner)");
        enableFindResearch = configuration.getBoolean("findResearch Enabled", categoryCommands, enableFindResearch, "Enable the 'findResearch' subcommand");
        enableForgetResearch = configuration.getBoolean("forgetResearch Enabled", categoryCommands, enableForgetResearch, "Enable the 'forgetResearch' subcommand");
        enableForgetScanned = configuration.getBoolean("forgetScanned Enabled", categoryCommands, enableForgetScanned, "Enable the 'forgetScanned' subcommand");
        enableListResearch = configuration.getBoolean("listResearch Enabled", categoryCommands, enableListResearch, "Enable the 'listResearch' subcommand");
        enableUpdateNode = configuration.getBoolean("updateNode Enabled", categoryCommands, enableUpdateNode, "Enable the 'updateNode' subcommand");
        enableSummonNode = configuration.getBoolean("summonNode Enabled", categoryCommands, enableSummonNode, "Enable the 'summonNode' subcommand");

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

        oreDimWhitelist = configuration.get(categoryStructures, "Ore gen Dimension Whitelist",  oreDimWhitelist, "").getIntList();

        // Loot
        championLootBagEnabled = configuration.getBoolean("Champion Loot Bag Drop Enabled", categoryLoot, championLootBagEnabled,"Toggle champion mobs dropping loot bags");
        championLootBagRarityMax = configuration.getInt("Max Champion Loot Bag Rarity", categoryLoot, championLootBagRarityMax, 0, 2, "Set max rarity for champion mob loot bags [0 = common, 1 = uncommon, 2 = rare]");

        // Entities
        championMobBlacklist = configuration.getStringList("Champion mob Blacklist", categoryEntities, championMobBlacklist, "Add mobs to remove them from the default champion lists");

        championMobWhitelist = configuration.getStringList("Champion mob Whitelist", categoryEntities, championMobWhitelist, "Add mobs to have them potentially spawn as champions");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
