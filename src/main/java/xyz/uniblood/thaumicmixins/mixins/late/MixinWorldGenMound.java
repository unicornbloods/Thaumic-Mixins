package xyz.uniblood.thaumicmixins.mixins.late;

import net.minecraft.world.gen.feature.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import thaumcraft.common.lib.world.WorldGenMound;

@Mixin(WorldGenMound.class)
public abstract class MixinWorldGenMound extends WorldGenerator {

    @ModifyVariable(
            method = "generate2()",
            at = @At(value = "INVOKE_ASSIGN", target = "thaumcraft/common/lib/world/Dummy;generate2()D"),
            index = 1
    )
    private int md1(int original) {
        return 1;
    }

}