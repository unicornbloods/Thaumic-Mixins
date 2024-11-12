package xyz.uniblood.thaumicmixins.mixins.late;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.common.blocks.BlockCustomOreItem;
import thaumcraft.common.items.ItemShard;

@Mixin(value = ItemShard.class)
public abstract class MixinItemShard extends Item {

    /**
     * @author UnicornBlood
     * @reason Prevents an array out of bounds exception when metadata greater than 6 is used.
     */
    @Overwrite()
    public int getColorFromItemStack(ItemStack stack, int par2) {
        final int itemDamage = stack.getItemDamage();

        if (itemDamage == 6) {
            return super.getColorFromItemStack(stack, par2);
        }

        // Use modulo operator for color index calculation just so it wraps when it overflows
        final int colorIndex = (itemDamage + 1) % BlockCustomOreItem.colors.length;

        return BlockCustomOreItem.colors[colorIndex];
    }

}
