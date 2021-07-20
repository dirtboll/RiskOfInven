package xyz.xmaximuskl.riskofinven.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import xyz.xmaximuskl.riskofinven.capabilities.RiskPouchCapabilityProvider;
import xyz.xmaximuskl.riskofinven.gui.RiskPouchContainer;
import xyz.xmaximuskl.riskofinven.gui.RiskPouchContext;
import xyz.xmaximuskl.riskofinven.capabilities.RiskPouchHandler;

import javax.annotation.Nullable;
import java.util.List;

public class RiskPouchItem extends Item {

    public static final int INIT_SIZE = 9;

    public RiskPouchItem() {
        super(new Properties().stacksTo(1).tab(new ROIItemGroup()).craftRemainder(Items.TOTEM_OF_UNDYING));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide && player instanceof ServerPlayerEntity) {
            RiskPouchHandler handler = (RiskPouchHandler) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
            RiskPouchContext context = new RiskPouchContext(player.inventory.findSlotMatchingItem(stack), handler.getSlots());
            ITextComponent itemName = stack.getHoverName().plainCopy();
            NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider(
                    (w, p, pl) -> RiskPouchContainer.createServerSide(w, p, stack), itemName), context::addToBuffer);
        }
        return ActionResult.success(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new RiskPouchCapabilityProvider(stack, INIT_SIZE);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isClientSide) {
            RiskPouchHandler handler = (RiskPouchHandler) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .orElse(null);
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack item = handler.getStackInSlot(i);
                item.inventoryTick(worldIn, entityIn, i, false);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, world, tooltip, tooltipFlag);
        ITextComponent description = new TranslationTextComponent("item.riskofinven.risk_pouch.description")
                .withStyle(TextFormatting.DARK_PURPLE).withStyle(TextFormatting.ITALIC);
        tooltip.add(description);
    }
}
