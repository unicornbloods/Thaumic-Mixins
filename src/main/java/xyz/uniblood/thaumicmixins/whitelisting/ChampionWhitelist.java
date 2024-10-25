package xyz.uniblood.thaumicmixins.whitelisting;

import net.minecraft.entity.EntityList;
import thaumcraft.common.config.ConfigEntities;

import static xyz.uniblood.thaumicmixins.ThaumicMixins.LOG;
import static xyz.uniblood.thaumicmixins.config.ThaumicMixinsConfig.championMobBlacklist;
import static xyz.uniblood.thaumicmixins.config.ThaumicMixinsConfig.championMobWhitelist;

public class ChampionWhitelist {
    public ChampionWhitelist() {
        for (String mob: championMobBlacklist) {
            Class mobClass = EntityList.stringToClassMapping.get(mob);

            if (mobClass != null) {
                LOG.debug("Removing champion mob [{}]", mobClass);
                ConfigEntities.championModWhitelist.remove(mobClass);
            } else {
                LOG.warn("Failed to blacklist [{}]", mob);
            }
        }

        for (String mob: championMobWhitelist) {
            String[] mobData = mob.split(":");
            Class mobClass = EntityList.stringToClassMapping.get(mobData[0]);
            int mobLevel = 0;

            if (mobData.length == 2) {
                try {
                    mobLevel = Integer.parseInt(mobData[1]);
                }
                catch (NumberFormatException e) {
                    LOG.warn("Failed to parse champion mob [{}]'s level", mob);
                }
            }

            if (mobClass != null) {
                LOG.debug("Adding champion mob [{}]", mobData[0]);
                ConfigEntities.championModWhitelist.put(mobClass, mobLevel);
            }
        }
    }
}