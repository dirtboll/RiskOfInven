package xyz.xmaximuskl.riskofinven.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.xmaximuskl.riskofinven.RiskOfInven;
import xyz.xmaximuskl.riskofinven.capabilities.RiskPouchHandler;

import javax.annotation.Nonnull;

public class RiskPouchContainer extends Container {

    private final PlayerInventory playerInventory;
    private final RiskPouchHandler itemHandler;

    public final ItemStack item;
    public final int itemSlot;
    public final int MAX_SLOT, MAX_COL, MAX_ROW;

    public RiskPouchContainer(int windowId, PlayerInventory playerInventory, RiskPouchHandler handler, ItemStack item) {
        super(RiskOfInven.POUCH_CONTAINER_TYPE.get(), windowId);
        this.item = item;
        this.itemSlot = playerInventory.findSlotMatchingItem(item);
        this.itemHandler = handler;
        this.playerInventory = playerInventory;
        this.MAX_SLOT = handler.getSlots();
        MAX_COL = 9;
        MAX_ROW = Math.floorDiv(this.MAX_SLOT, MAX_COL);

        addSlots();
    }

    public static RiskPouchContainer createServerSide(int windowID, PlayerInventory playerInventory, ItemStack item) {
        RiskPouchHandler handler = (RiskPouchHandler) item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        return new RiskPouchContainer(windowID, playerInventory, handler, item);
    }

    public static RiskPouchContainer createClientSide(int windowId, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
        RiskPouchContext context = RiskPouchContext.fromBuffer(packetBuffer);
        ItemStack item = playerInventory.getItem(context.slotIndex);
        RiskPouchHandler handler = (RiskPouchHandler) item.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        return new RiskPouchContainer(windowId, playerInventory, handler, item);
    }

    private void addSlots() {
        final int SLOT_X_SPACING = 18,
                  SLOT_Y_SPACING = 18,
                  HOTBAR_XPOS = 8,
                  INVEN_XPOS = 8,
                  POUCH_XPOS = 8,
                  POUCH_YPOS = 18,
                  INVEN_YPOS = 103,
                  HOTBAR_YPOS = 161;

        //Pouch
        for(int row = 0; row < MAX_ROW ; ++row) {
            for(int col = 0; col < MAX_COL; ++col) {
                int x = POUCH_XPOS + col * SLOT_X_SPACING;
                int y = POUCH_YPOS + row * SLOT_Y_SPACING;
                int index = col + row * MAX_COL;
                this.addSlot(new SlotItemHandler(this.itemHandler, index, x, y));
            }
        }

        final int INVEN_OFFSET = (MAX_ROW - 4) * SLOT_Y_SPACING;

        //Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                int x = INVEN_XPOS + col * SLOT_X_SPACING;
                int y = INVEN_YPOS + row * SLOT_Y_SPACING + INVEN_OFFSET;
                int index = col + row * MAX_COL + MAX_COL;
                this.addSlot(new Slot(this.playerInventory, index, x, y));
            }
        }

        //Hotbar
        for (int col = 0; col < MAX_COL; col++) {
            int x = HOTBAR_XPOS + col * SLOT_X_SPACING;
            int y = HOTBAR_YPOS + INVEN_OFFSET;
            this.addSlot(new Slot(this.playerInventory, col, x, y));
        }
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity playerEntity, int sourceSlotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(sourceSlotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (sourceSlotIndex < this.MAX_SLOT) {
                if (!this.moveItemStackTo(itemstack1, this.MAX_SLOT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.MAX_SLOT, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        ItemStack main = player.getItemInHand(Hand.MAIN_HAND);
        ItemStack off = player.getItemInHand(Hand.OFF_HAND);
        return (!main.isEmpty() && main == item) || (!off.isEmpty() && off == item);
    }

    @Override
    public void broadcastChanges() {
        itemHandler.serializeNBT();
        super.broadcastChanges();
    }
}
