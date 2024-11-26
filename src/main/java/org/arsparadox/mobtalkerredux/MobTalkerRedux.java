package org.arsparadox.mobtalkerredux;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("mobtalkerredux")
public class MobTalkerRedux {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mobtalkerredux";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> MOB_TALKER_ITEM = ITEMS.register("mob_talker_item", MobTalkerItem::new);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER.key(), MODID);

    public MobTalkerRedux(IEventBus modEventBus) {
        // Register setup methods directly to the mod event bus
        modEventBus.addListener(this::setup);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(this::onRegisterEvent);

    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(MOB_TALKER_ITEM);
    }
    private void onRegisterEvent(RegisterEvent event) {
        event.register(BuiltInRegistries.RECIPE_SERIALIZER.key(), helper -> {
            // If you have any custom recipe serializers, register them here
            LOGGER.info("Registering recipes for " + MODID);
        });
    }



    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM PREINIT");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    public static class RegistryEvents {




        // Move command registration to the mod event bus
        public static void registerCommands(RegisterCommandsEvent event) {
            DemoCommand.register(event.getDispatcher());
        }




    }


}