package xyz.uniblood.thaumicmixins.mixins.late;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.blocks.BlockCosmeticSolid;
import xyz.uniblood.thaumicmixins.config.ThaumicMixinsConfig;

@Mixin(value = BlockCosmeticSolid.class, remap = false)
public abstract class MixinBlockCosmeticSolid extends Block {

    protected MixinBlockCosmeticSolid(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "isBeaconBase", at = @At("HEAD"), cancellable = true)
    public void onIsBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ, CallbackInfoReturnable<Boolean> cir) {
        final var returnValue = worldObj.getBlock(x, y, z) == this && thaumic_Mixins$isConfigMetadata(worldObj.getBlockMetadata(x, y, z));
        cir.setReturnValue(returnValue);
        cir.cancel();
    }

    @Unique
    private boolean thaumic_Mixins$isConfigMetadata(int metadata) {
        for (var configMetadata : ThaumicMixinsConfig.blockCosmeticSolidBeaconMetadatas) {
            if (metadata == configMetadata) {
                return true;
            }
        }
        return false;
    }

}
