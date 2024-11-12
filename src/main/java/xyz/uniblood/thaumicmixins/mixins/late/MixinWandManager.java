package xyz.uniblood.thaumicmixins.mixins.late;

import cpw.mods.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import thaumcraft.common.items.wands.WandManager;

import static baubles.api.expanded.BaubleExpandedSlots.slotsCurrentlyUsed;

@Mixin(WandManager.class)
public class MixinWandManager {

    @ModifyConstant(
            method = "getTotalVisDiscount",
            constant = @Constant(intValue = 4, ordinal = 0),
            remap = false
    )
    private static int checkExpandedBaublesSlots(int original) {
        if (Loader.isModLoaded("Baubles|Expanded")) {
            return slotsCurrentlyUsed();
        }
        return original;
    }
}
