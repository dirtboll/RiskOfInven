package xyz.xmaximuskl.riskofinven.items;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import xyz.xmaximuskl.riskofinven.RiskOfInven;

public class ROIItemGroup extends ItemGroup {
    ROIItemGroup() {
        super(RiskOfInven.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RiskOfInven.RISK_POUCH.get());
    }
}
