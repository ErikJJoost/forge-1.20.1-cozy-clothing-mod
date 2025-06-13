package net.cluis.cozy_clothing_mod.block;

import net.cluis.cozy_clothing_mod.CCMod;
import net.cluis.cozy_clothing_mod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CCMod.MOD_ID);

    public static final RegistryObject<Block> RUBBER_EXTRACTOR = BLOCKS.register("rubber_extractor",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f)
                    .requiresCorrectToolForDrops()));

    // This creates the item version of the block
    public static final RegistryObject<Item> RUBBER_EXTRACTOR_ITEM = ModItems.ITEMS.register("rubber_extractor",
            () -> new BlockItem(RUBBER_EXTRACTOR.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}