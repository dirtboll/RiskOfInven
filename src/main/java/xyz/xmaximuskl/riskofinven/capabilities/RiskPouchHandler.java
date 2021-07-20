package xyz.xmaximuskl.riskofinven.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class RiskPouchHandler extends ItemStackHandler {
    private int size;
    private boolean dirty = false;
    private CompoundNBT lastNBT;

    public RiskPouchHandler(int size) {
        super(size);
        this.size = size;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public boolean isDirty() { return this.dirty; }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        setDirty();
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        setDirty();
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        setDirty();
        super.onContentsChanged(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
         ResourceLocation resource = stack.getItem().getRegistryName();
         return resource.getPath().equals("totem_of_undying") || resource.getNamespace().equals("riskofrainmod");
    }

    @Override
    public void deserializeNBT(@Nonnull CompoundNBT nbt) {
        if (nbt.contains("inventory")) {
            super.deserializeNBT(nbt.getCompound("inventory"));
            this.size = nbt.getInt("size");
        }
        lastNBT = nbt;
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (isDirty() || lastNBT == null) {
            CompoundNBT nbt = new CompoundNBT();
            CompoundNBT nbtInv = super.serializeNBT();
            nbt.put("inventory", nbtInv);
            nbt.putInt("size", this.size);
            dirty = false;
            lastNBT = nbt;
        }
        return lastNBT;
    }
}
