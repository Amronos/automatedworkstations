package com.amronos.automatedworkstations.registry;

import com.amronos.automatedworkstations.Constants;
import com.amronos.automatedworkstations.block.AnvilatorBlock;
import com.amronos.automatedworkstations.block.SmitherBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    private static <T extends Block> DeferredBlock<T> registerModBlocks(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static final DeferredBlock<Block> ANVILATOR = registerModBlocks("anvilator", () -> new AnvilatorBlock(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<Block> SMITHER = registerModBlocks("smither", () -> new SmitherBlock(BlockBehaviour.Properties.of()));
}
