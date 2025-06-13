package net.cluis.cozy_clothing_mod.item;

import net.cluis.cozy_clothing_mod.CCMod;
import net.cluis.cozy_clothing_mod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CCMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CCMOD_TAB = CREATIVE_MODE_TABS.register("ccmod_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WELLIES.get()))
                    .title(Component.translatable("creativetab.ccmod_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.WELLIES.get());
                        pOutput.accept(ModItems.RUBBER.get());
                        pOutput.accept(ModBlocks.RUBBER_EXTRACTOR.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}