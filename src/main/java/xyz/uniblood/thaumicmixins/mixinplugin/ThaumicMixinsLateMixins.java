package xyz.uniblood.thaumicmixins.mixinplugin;


import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

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
        List<String> mixins = new ArrayList<>();
        mixins.add("MixinThaumcraftWorldGenerator");
        mixins.add("MixinWorldGenMound");
        mixins.add("MixinEventHandlerEntity");
        mixins.add("MixinThaumcraftCommand");
        mixins.add("MixinBlockCosmeticSolid");
        return mixins;
    }
}
