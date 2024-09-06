package com.amronos.automatedworkstations.registry;

import com.amronos.automatedworkstations.Constants;
import com.amronos.automatedworkstations.block.SmitherBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    private static Block registerModBlocks(String name, Block block){
        Block toReturn = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static void registerBlockItem(String name, Block block){
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name), new BlockItem(block, new Item.Properties()));
    }

    public static void registerModBlocks() {
    }

    public static final Block SMITHER = registerModBlocks("smither", new SmitherBlock(BlockBehaviour.Properties.of()));
}
