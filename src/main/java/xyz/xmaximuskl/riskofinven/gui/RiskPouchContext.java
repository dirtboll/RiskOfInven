package xyz.xmaximuskl.riskofinven.gui;

import net.minecraft.network.PacketBuffer;

public class RiskPouchContext {
    int slotIndex;
    int slotCount;

    public RiskPouchContext(int slotIndex, int slotCount) {
        this.slotIndex = slotIndex;
        this.slotCount = slotCount;
    }

    public static RiskPouchContext fromBuffer(PacketBuffer buffer) {
        int slotIndex = buffer.readInt();
        int slotCount = buffer.readInt();
        return new RiskPouchContext(slotIndex, slotCount);
    }

    public void addToBuffer(PacketBuffer buffer) {
        buffer.writeInt(this.slotIndex);
        buffer.writeInt(this.slotCount);
    }
}
