package xyz.xmaximuskl.riskofinven.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RiskPouchCapabilityProvider implements ICapabilitySerializable<INBT> {
    private final RiskPouchHandler handler;
    private final LazyOptional<IItemHandler> optional;

    public RiskPouchCapabilityProvider(ItemStack stack, int size) {
        this.handler = new RiskPouchHandler(size);
        this.optional = LazyOptional.of(() -> handler);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return optional.cast();
        }
        else return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return handler.serializeNBT();
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        handler.deserializeNBT((CompoundNBT) nbt);
    }
}
