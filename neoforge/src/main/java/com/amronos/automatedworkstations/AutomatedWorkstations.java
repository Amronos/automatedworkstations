package com.amronos.automatedworkstations;


import com.amronos.automatedworkstations.inventory.CommonSmitherScreen;
import com.amronos.automatedworkstations.registry.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(Constants.MOD_ID)
public class AutomatedWorkstations {
    public AutomatedWorkstations(IEventBus bus) {
        CommonClass.init();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenuTypes.MENU_TYPES.register(bus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
    }

    @EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.SMITHER_MENU.get(), CommonSmitherScreen::new);
        }
    }
}