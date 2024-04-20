package xyz.uniblood.thaumicmixins.mixins.late;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.items.ItemCrystalEssence;
import thaumcraft.common.lib.events.EventHandlerEntity;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.lib.utils.EntityUtils;

import static xyz.uniblood.thaumicmixins.config.ThaumicMixinsConfig.championLootBagEnabled;
import static xyz.uniblood.thaumicmixins.config.ThaumicMixinsConfig.championLootBagRarityMax;

@Mixin(value = EventHandlerEntity.class, remap = false)
public abstract class MixinEventHandlerEntity {

    @Overwrite
    @SubscribeEvent
    public void livingDrops(LivingDropsEvent event) {
        boolean fakeplayer = event.source.getEntity() != null && event.source.getEntity() instanceof FakePlayer;
        if(!event.entity.worldObj.isRemote && event.recentlyHit && !fakeplayer && event.entity instanceof EntityMob && !(event.entity instanceof EntityThaumcraftBoss) && ((EntityMob)event.entity).getEntityAttribute(EntityUtils.CHAMPION_MOD).getAttributeValue() >= 0.0D) {
            int i = 5 + event.entity.worldObj.rand.nextInt(3);

            while(i > 0) {
                int j = EntityXPOrb.getXPSplit(i);
                i -= j;
                event.entity.worldObj.spawnEntityInWorld(new EntityXPOrb(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, j));
            }

            if (championLootBagEnabled) {
                int lb = Math.min(championLootBagRarityMax, MathHelper.floor_float((float)(event.entity.worldObj.rand.nextInt(9) + event.lootingLevel) / 5.0F));
                event.drops.add(new EntityItem(event.entity.worldObj, event.entityLiving.posX, event.entityLiving.posY + (double)event.entityLiving.getEyeHeight(), event.entityLiving.posZ, new ItemStack(ConfigItems.itemLootbag, 1, lb)));
            }

        }

        if(event.entityLiving instanceof EntityZombie && !(event.entityLiving instanceof EntityBrainyZombie) && event.recentlyHit && event.entity.worldObj.rand.nextInt(10) - event.lootingLevel < 1) {
            event.drops.add(new EntityItem(event.entity.worldObj, event.entityLiving.posX, event.entityLiving.posY + (double)event.entityLiving.getEyeHeight(), event.entityLiving.posZ, new ItemStack(ConfigItems.itemZombieBrain)));
        }

        if(event.entityLiving instanceof EntityVillager && event.entity.worldObj.rand.nextInt(10) - event.lootingLevel < 1) {
            event.drops.add(new EntityItem(event.entity.worldObj, event.entityLiving.posX, event.entityLiving.posY + (double)event.entityLiving.getEyeHeight(), event.entityLiving.posZ, new ItemStack(ConfigItems.itemResource, 1, 18)));
        }

        if(event.source == DamageSourceThaumcraft.dissolve) {
            AspectList aspects = ScanManager.generateEntityAspects(event.entityLiving);
            if(aspects != null && aspects.size() > 0) {
                for(Aspect aspect : aspects.getAspects()) {
                    if(!event.entity.worldObj.rand.nextBoolean()) {
                        int size = 1 + event.entity.worldObj.rand.nextInt(aspects.getAmount(aspect));
                        size = Math.max(1, size / 2);
                        ItemStack stack = new ItemStack(ConfigItems.itemCrystalEssence, size, 0);
                        ((ItemCrystalEssence)stack.getItem()).setAspects(stack, (new AspectList()).add(aspect, 1));
                        event.drops.add(new EntityItem(event.entity.worldObj, event.entityLiving.posX, event.entityLiving.posY + (double)event.entityLiving.getEyeHeight(), event.entityLiving.posZ, stack));
                    }
                }
            }
        }

    }
}
