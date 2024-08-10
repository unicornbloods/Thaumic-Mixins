package xyz.uniblood.thaumicmixins.mixins.late;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import thaumcraft.common.lib.world.WorldGenEldritchRing;
import thaumcraft.common.lib.world.WorldGenHilltopStones;
import thaumcraft.common.lib.world.WorldGenMound;
import thaumcraft.common.lib.world.dim.MazeThread;

import java.util.HashMap;
import java.util.Random;

import static thaumcraft.common.lib.world.ThaumcraftWorldGenerator.createRandomNodeAt;
import static thaumcraft.common.lib.world.ThaumcraftWorldGenerator.getDimBlacklist;
import static xyz.uniblood.thaumicmixins.config.ThaumicMixinsConfig.*;

@Mixin(value = ThaumcraftWorldGenerator.class, remap = false)
public abstract class MixinThaumcraftWorldGenerator implements IWorldGenerator {

    @Shadow
    HashMap<Integer, Boolean> structureNode;

    @Shadow
    abstract void generateVegetation(World world, Random random, int chunkX, int chunkZ, boolean newGen);

    @Shadow
    abstract void generateOres(World world, Random random, int chunkX, int chunkZ, boolean newGen);

    @Shadow
    abstract boolean generateWildNodes(World world, Random random, int chunkX, int chunkZ, boolean auraGen, boolean newGen);

    @Shadow
    abstract boolean generateTotem(World world, Random random, int chunkX, int chunkZ, boolean auraGen, boolean newGen);

    /**
     * @author UnicornBlood
     * @reason Allow to customize frequency of structures
     */
    @Overwrite()
    private void generateSurface(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        boolean auraGen = false;
        int blacklist = getDimBlacklist(world.provider.dimensionId);
        if(blacklist == -1 && Config.genTrees && !world.getWorldInfo().getTerrainType().getWorldTypeName().startsWith("flat") && (newGen || Config.regenTrees)) {
            this.generateVegetation(world, random, chunkX, chunkZ, newGen);
        }

        if(blacklist != 0 && blacklist != 2) {
            this.generateOres(world, random, chunkX, chunkZ, newGen);
        }

        if(Config.genAura && (newGen || Config.regenAura) && ArrayUtils.contains(auraNodeDimWhitelist, world.provider.dimensionId)) {

            ChunkPosition var7 = (new MapGenScatteredFeature()).func_151545_a(world, chunkX * 16 + 8, world.getHeightValue(chunkX * 16 + 8, chunkZ * 16 + 8), chunkZ * 16 + 8);
            if(var7 != null && !this.structureNode.containsKey(var7.hashCode())) {
                auraGen = true;
                this.structureNode.put(var7.hashCode(), Boolean.TRUE);
                createRandomNodeAt(world, var7.chunkPosX, world.getHeightValue(var7.chunkPosX, var7.chunkPosZ) + 3, var7.chunkPosZ, random, false, false, false);
            }

            auraGen = this.generateWildNodes(world, random, chunkX, chunkZ, auraGen, newGen);
        }

        if(blacklist == -1 && Config.genStructure && world.provider.dimensionId == 0 && (newGen || Config.regenStructure)) {
            int randPosX = chunkX * 16 + random.nextInt(16);
            int randPosZ = chunkZ * 16 + random.nextInt(16);
            int randPosY = world.getHeightValue(randPosX, randPosZ) - 9;
            if(randPosY < world.getActualHeight()) {
                world.getChunkFromBlockCoords(MathHelper.floor_double(randPosX), MathHelper.floor_double(randPosZ));
                WorldGenerator mound = new WorldGenMound();
                if(moundEnabled && random.nextInt(moundFrequency) == 0) {
                    if(mound.generate(world, random, randPosX, randPosY, randPosZ)) {
                        auraGen = true;
                        int value = random.nextInt(200) + 400;
                        createRandomNodeAt(world, randPosX + 9, randPosY + 8, randPosZ + 9, random, false, true, false);
                    }
                } else if(stoneRingEnabled && random.nextInt(stoneRingFrequency) == 0) {
                    WorldGenEldritchRing stonering = new WorldGenEldritchRing();
                    randPosY = randPosY + 8;
                    int w = 11 + random.nextInt(6) * 2;
                    int h = 11 + random.nextInt(6) * 2;
                    stonering.chunkX = chunkX;
                    stonering.chunkZ = chunkZ;
                    stonering.width = w;
                    stonering.height = h;
                    if(stonering.generate(world, random, randPosX, randPosY, randPosZ)) {
                        auraGen = true;
                        createRandomNodeAt(world, randPosX, randPosY + 2, randPosZ, random, false, true, false);
                        Thread t = new Thread(new MazeThread(chunkX, chunkZ, w, h, random.nextLong()));
                        t.start();
                    }
                } else if( hillTopStonesEnabled && random.nextInt(hillTopStonesFrequency) == 0) {
                    randPosY = randPosY + 9;
                    WorldGenerator hilltopStones = new WorldGenHilltopStones();
                    if(hilltopStones.generate(world, random, randPosX, randPosY, randPosZ)) {
                        auraGen = true;
                        createRandomNodeAt(world, randPosX, randPosY + 5, randPosZ, random, false, true, false);
                    }
                }
                if(totemEnabled && random.nextInt(totemFrequency) == 0) {
                    this.generateTotem(world, random, chunkX, chunkZ, auraGen, newGen);
                }
            }



        }

    }

}
