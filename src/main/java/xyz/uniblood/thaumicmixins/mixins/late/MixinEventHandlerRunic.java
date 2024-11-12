package xyz.uniblood.thaumicmixins.mixins.late;

import cpw.mods.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import thaumcraft.common.lib.events.EventHandlerRunic;

import static baubles.api.expanded.BaubleExpandedSlots.slotsCurrentlyUsed;

@Mixin(EventHandlerRunic.class)
public class MixinEventHandlerRunic {

    @ModifyConstant(
            method = "livingTick",
            constant = @Constant(intValue = 4, ordinal = 1),
            remap = false
    )
    private int checkExpandedBaublesSlots(int original) {
        if (Loader.isModLoaded("Baubles|Expanded")) {
            return slotsCurrentlyUsed();
        }
        return original;
    }
}
