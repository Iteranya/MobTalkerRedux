package org.arsparadox.mobtalkerredux;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arsparadox.mobtalkerredux.vn.model.TextureLoader;

import java.util.stream.Collectors;

@Mod("mobtalkerredux")
public class MobTalkerRedux {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mobtalkerredux";

    public MobTalkerRedux() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        RegistryEvents.initialize();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        // Register for command registration
        MinecraftForge.EVENT_BUS.register(RegistryEvents.class);
    }

    private void setup(final FMLCommonSetupEvent event) {

        LOGGER.info("HELLO FROM PREINIT");
        TextureLoader.loadTexturesFromConfig();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo(MODID, "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    public static class RegistryEvents {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

        public static final RegistryObject<Item> MOB_TALKER_ITEM = ITEMS.register("mob_talker_item", MobTalkerItem::new);

        // Command registration stays on Forge event bus
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            DemoCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
            // Add to tools tab
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(MOB_TALKER_ITEM);
            }
        }

        public static void register(IEventBus eventBus) {
            BLOCKS.register(eventBus);
            ITEMS.register(eventBus);
        }

        public static void initialize() {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            register(modEventBus);
            // Register only the build contents event to mod bus
            modEventBus.addListener(RegistryEvents::buildContents);
        }
    }
}