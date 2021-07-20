package xyz.xmaximuskl.riskofinven;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.xmaximuskl.riskofinven.gui.RiskPouchContainer;
import xyz.xmaximuskl.riskofinven.gui.RiskPouchContainerScreen;
import xyz.xmaximuskl.riskofinven.items.RiskPouchItem;


@Mod(RiskOfInven.MODID)
public class RiskOfInven
{
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RiskOfInven.MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, RiskOfInven.MODID);

    public static final RegistryObject<Item> RISK_POUCH = ITEMS.register("risk_pouch", RiskPouchItem::new);
    public static final RegistryObject<ContainerType<RiskPouchContainer>> POUCH_CONTAINER_TYPE = CONTAINERS.register("risk_pouch_cont",
            () -> IForgeContainerType.create(RiskPouchContainer::createClientSide));

    public static final String MODID = "riskofinven";

    public static final Logger LOGGER = LogManager.getLogger();

    public RiskOfInven() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modBus);
        CONTAINERS.register(modBus);

        LOGGER.info("Setting up Risk Of Inven");
        modBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        ScreenManager.register(POUCH_CONTAINER_TYPE.get(), RiskPouchContainerScreen::new);
        LOGGER.info("Screen registered");
    }
}
