package xyz.uniblood.thaumicmixins.mixinplugin;


import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.FMLLaunchHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class ThaumicMixinsLateMixins implements ILateMixinLoader {
    @Override
    public String getMixinConfig() {
        return "mixins.thaumicmixins.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        boolean client = FMLLaunchHandler.side().isClient();
        List<String> mixins = new ArrayList<>();

        mixins.add("MixinThaumcraftWorldGenerator");
        mixins.add("MixinWorldGenMound");
        mixins.add("MixinEventHandlerEntity");
        mixins.add("MixinThaumcraftCommand");
        mixins.add("MixinBlockCosmeticSolid");
        mixins.add("MixinEventHandlerRunic");
        mixins.add("MixinWarpEvents");

        if (client) {
            mixins.add("MixinItemShard");
        }

        // Just remove this check after bugtorch removes the mixins
        if (!Loader.isModLoaded("bugtorch")) {
            if (client) {
                mixins.add("MixinBlockCandleRenderer");
            }
            mixins.add("MixinBlockCandle");
        }

        return mixins;
    }
}
