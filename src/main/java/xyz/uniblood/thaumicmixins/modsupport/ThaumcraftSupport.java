package xyz.uniblood.thaumicmixins.modsupport;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thaumcraft.common.config.ConfigBlocks;

public class ThaumcraftSupport {

    public static void enableSupport() {
        ItemStack ancientStone = new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 11);

        //Ore dictionary
        OreDictionary.registerOre("treeLeaves", new ItemStack(ConfigBlocks.blockMagicalLeaves, 1, OreDictionary.WILDCARD_VALUE));

        OreDictionary.registerOre("blockThaumium", new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4));

        OreDictionary.registerOre("stairWood", new ItemStack(ConfigBlocks.blockStairsSilverwood));
        OreDictionary.registerOre("stairWood", new ItemStack(ConfigBlocks.blockStairsGreatwood));

        //Tweaks
        if (!Loader.isModLoaded("dreamcraft")) {
            // craftThaumcraftAncientStoneSlabsAndStairs
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ConfigBlocks.blockSlabStone, 6, 1), "XXX", Character.valueOf('X'), ancientStone));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ConfigBlocks.blockStairsEldritch, 4), "X  ", "XX ", "XXX", Character.valueOf('X'), ancientStone));

            // reverseCraftThaumcraftSlabs
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6), "X", "X", Character.valueOf('X'), new ItemStack(ConfigBlocks.blockSlabWood, 1, 0)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7), "X", "X", Character.valueOf('X'), new ItemStack(ConfigBlocks.blockSlabWood, 1, 1)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7), "X", "X", Character.valueOf('X'), new ItemStack(ConfigBlocks.blockSlabStone, 1, 0)));
            GameRegistry.addRecipe(new ShapedOreRecipe(ancientStone, "X", "X", Character.valueOf('X'), new ItemStack(ConfigBlocks.blockSlabStone, 1, 1)));
        }

    }

}