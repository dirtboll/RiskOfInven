package xyz.xmaximuskl.riskofinven.utils;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraftforge.items.CapabilityItemHandler;
import xyz.xmaximuskl.riskofinven.capabilities.RiskPouchHandler;
import xyz.xmaximuskl.riskofinven.items.RiskPouchItem;

public class MixinUtils {
    public static boolean checkTotemInInvenPouch(LivingEntity entity) {
        ItemStack totem = null;

        for (ItemStack item : entity.getHandSlots()) {
            if (item.getItem() instanceof RiskPouchItem) {
                totem = getTotemInPouch(item);
                break;
            }
        }

        if (totem == null && entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;
            for (ItemStack item : serverPlayer.inventory.items) {
                if (item.getItem() instanceof RiskPouchItem) {
                    totem = getTotemInPouch(item);
                    break;
                }
            }
        }

        if (totem != null) useTotem(totem, entity);
        return totem != null;
    }

    private static ItemStack getTotemInPouch(ItemStack pouch) {
        if (!(pouch.getItem() instanceof RiskPouchItem)) return null;

        RiskPouchHandler handler = (RiskPouchHandler) pouch.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElse(null);

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack itemStack = handler.getStackInSlot(i);
            if (itemStack.getItem() == Items.TOTEM_OF_UNDYING) {
                return itemStack;
            }
        }
        return null;
    }

    private static void useTotem(ItemStack totem, LivingEntity livingEntity) {
        if (totem == null) return;

        totem.shrink(1);

        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) livingEntity;
            serverplayerentity.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
            CriteriaTriggers.USED_TOTEM.trigger(serverplayerentity, totem);
        }

        livingEntity.setHealth(1.0F);
        livingEntity.removeAllEffects();
        livingEntity.addEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
        livingEntity.addEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
        livingEntity.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 800, 0));
        livingEntity.level.broadcastEntityEvent(livingEntity, (byte)35);
    }
}
