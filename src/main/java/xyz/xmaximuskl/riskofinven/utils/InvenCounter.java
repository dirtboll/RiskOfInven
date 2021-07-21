package xyz.xmaximuskl.riskofinven.utils;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import xyz.xmaximuskl.riskofinven.capabilities.RiskPouchHandler;
import xyz.xmaximuskl.riskofinven.items.RiskPouchItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvenCounter {
    public LivingEntity entity;
    public Map<Item, Integer> summary;
    public List<Item> itemSearch;

    public InvenCounter(LivingEntity entity, List<Item> itemsSearch) {
        this.entity = entity;
        this.summary = new HashMap<>();
        this.itemSearch = itemsSearch;
        iterateSummary(getInventory(entity));
    }

    private void iterateSummary(Iterable<ItemStack> inventory) {
        for (ItemStack itemStack : inventory) {
            if (itemStack.getItem() instanceof RiskPouchItem) {
                itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                    iterateSummary(((RiskPouchHandler) iItemHandler).getStacks());
                });
            }
            if (!this.itemSearch.contains(itemStack.getItem())) continue;

            Integer sum = this.summary.getOrDefault(itemStack.getItem(), 0);
            sum += itemStack.getCount();
            this.summary.put(itemStack.getItem(), sum);
        }
    }

    public static Iterable<ItemStack> getInventory(LivingEntity entity) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            return player.inventory.items;
        } else
            return entity.getHandSlots();
    }
}
