package xyz.uniblood.thaumicmixins.mixins.late;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import thaumcraft.common.lib.world.WorldGenMound;

@Mixin(WorldGenMound.class)
public abstract class MixinWorldGenMound extends WorldGenerator {

    @ModifyVariable(
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 0),
            to = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0)
        ),
        method = "generate2",
        at = @At("STORE"),
        name = "md",
        remap = false
    )
    private int md1(int original,  @Local(ordinal = 0, index = 9, name = "rr") float rr) {
        return rr < 0.33F?1:0;
    }

}
