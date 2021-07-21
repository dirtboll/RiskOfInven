package xyz.xmaximuskl.riskofinven.mixins;

import com.elcolomanco.riskofrainmod.events.ServerEvents;
import com.elcolomanco.riskofrainmod.setup.RegistrySetup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.xmaximuskl.riskofinven.utils.InvenCounter;

import java.util.Arrays;
import java.util.List;

@Mixin(ServerEvents.class)
public class RORServerEventsMixin {

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    @SubscribeEvent(
            priority = EventPriority.NORMAL
    )
    public static void crowbarEffect(LivingDamageEvent event) {
        final List<Item> countedItems = Arrays.asList(
                RegistrySetup.CROWBAR.get(),
                RegistrySetup.LENS_MARKS_GLASSES.get(),
                RegistrySetup.TOPAZ_BROOCH.get(),
                RegistrySetup.TOUGHER_TIMES.get()
        );

        if (event.getSource().getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity entity = (ServerPlayerEntity) event.getSource().getEntity();
            InvenCounter counter = new InvenCounter(entity, countedItems);
            crowbarProcess(event, counter);
            lensMakerGlassesProcess(event, counter);
            topazBroochProcess(event, counter);
        } else if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            ServerPlayerEntity entity = (ServerPlayerEntity) event.getEntityLiving();
            InvenCounter counter = new InvenCounter(entity, countedItems);
            tougherTimesProcess(event, counter);
        }
    }

    private static void crowbarProcess(LivingDamageEvent event, InvenCounter counter) {
        Integer total = counter.summary.get(RegistrySetup.CROWBAR.get());
        PlayerEntity player = (PlayerEntity) counter.entity;
        if (total == null || total == 0) return;

        boolean proc = false;
        LivingEntity entity = event.getEntityLiving();
        float currentHp = entity.getHealth();
        float maxHp = entity.getMaxHealth();
        if ((double)currentHp >= (double)maxHp * 0.9D) {
            proc = true;
        }

        if (!proc) {
            return;
        }

        float damage = event.getAmount();
        double multiplier = 1.0D + 0.5D * (double)total;
        float crowbarDamage = (float)((double)damage * multiplier);
        World worldIn = player.getCommandSenderWorld();
        SoundCategory soundcategory = SoundCategory.PLAYERS;
        event.setAmount(crowbarDamage);
        worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)RegistrySetup.CROWBAR_PROC.get(), soundcategory, 0.4F, player.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    private static void lensMakerGlassesProcess(LivingDamageEvent event, InvenCounter counter) {
        Integer total = counter.summary.get(RegistrySetup.CROWBAR.get());
        PlayerEntity player = (PlayerEntity) counter.entity;
        if (total == null || total == 0) return;
        total = Math.min(total, 64);

        boolean isCrit = false;
        double chance = 1.5625D * (double)total - 1.0D;
        float damage = event.getAmount();
        float crit = damage * 2.0F;
        int random = player.getRandom().nextInt(99);
        World worldIn = player.getCommandSenderWorld();
        SoundCategory soundcategory = SoundCategory.PLAYERS;
        if ((double)random <= chance) {
            isCrit = true;
        }

        if (!isCrit) {
            return;
        }

        event.setAmount(crit);
        worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), RegistrySetup.LENS_CRIT_PROC.get(), soundcategory, 0.4F, player.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    private static void topazBroochProcess(LivingDamageEvent event, InvenCounter counter) {
        Integer total = counter.summary.get(RegistrySetup.CROWBAR.get());
        PlayerEntity killer = (PlayerEntity) counter.entity;
        if (total == null || total == 0) return;

        if ((float)(total * 4) <= killer.getMaxHealth()) {
            killer.addEffect(new EffectInstance(Effects.ABSORPTION, 160, total - 1, true, false));
        } else {
            killer.addEffect(new EffectInstance(Effects.ABSORPTION, 160, (int)killer.getMaxHealth() / 4 - 1, true, false));
        }
    }

    private static void tougherTimesProcess(LivingDamageEvent event, InvenCounter counter) {
        Integer total = counter.summary.get(RegistrySetup.CROWBAR.get());
        PlayerEntity player = (PlayerEntity) counter.entity;
        if (total == null || total == 0) return;

        boolean isBlocking = false;
        double chance = (1.0D - 1.0D / (1.0D + 0.15D * (double)total) - 0.01D) * 100.0D;
        int random = player.getRandom().nextInt(99);
        World worldIn = player.getCommandSenderWorld();
        SoundCategory soundcategory = SoundCategory.PLAYERS;
        if ((double)random <= chance) {
            isBlocking = true;
        }

        if (!isBlocking) {
            return;
        }

        event.setAmount(0.0f);
        worldIn.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), (SoundEvent)RegistrySetup.TOUGHER_TIMES_PROC.get(), soundcategory, 0.4F, 1.0F);
    }

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    @SubscribeEvent(
            priority = EventPriority.NORMAL
    )
    public static void lensMakersGlassesEffect(LivingDamageEvent event) { }

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    @SubscribeEvent(
            priority = EventPriority.NORMAL
    )
    public static void roseBucklerEffect(LivingDamageEvent event) { }

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    @SubscribeEvent(
            priority = EventPriority.NORMAL
    )
    public static void topazBroochEffect(LivingDeathEvent event) { }

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    @SubscribeEvent(
            priority = EventPriority.NORMAL
    )
    public static void tougherTimesEffect(LivingDamageEvent event) { }
}
