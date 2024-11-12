package xyz.uniblood.thaumicmixins.mixins.late;

import cpw.mods.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import thaumcraft.common.lib.WarpEvents;

import static baubles.api.expanded.BaubleExpandedSlots.slotsCurrentlyUsed;

@Mixin(WarpEvents.class)
public class MixinWarpEvents {

    @ModifyConstant(
            method = "getWarpFromGear",
            constant = @Constant(intValue = 4, ordinal = 1),
            remap = false
    )
    private static int checkExpandedBaublesSlots(int original) {
        if (Loader.isModLoaded("Baubles|Expanded")) {
            return slotsCurrentlyUsed();
        }
        return original;
    }
}
