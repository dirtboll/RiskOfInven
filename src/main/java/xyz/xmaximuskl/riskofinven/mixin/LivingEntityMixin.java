package xyz.xmaximuskl.riskofinven.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.xmaximuskl.riskofinven.RiskOfInven;
import xyz.xmaximuskl.riskofinven.utils.MixinUtils;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
    private void _riskofinven_checkTotemDeathProtection(DamageSource src,
                                                        CallbackInfoReturnable<Boolean> cir) {
        if (src.isBypassInvul()) {
            cir.setReturnValue(false);
        } else if (MixinUtils.checkTotemInInvenPouch((LivingEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
