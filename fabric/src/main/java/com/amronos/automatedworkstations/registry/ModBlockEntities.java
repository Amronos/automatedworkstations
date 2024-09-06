package com.amronos.automatedworkstations.registry;

import com.amronos.automatedworkstations.Constants;
import com.amronos.automatedworkstations.block.entity.SmitherBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static void registerBlockEntities(){
    }

    public static final BlockEntityType<SmitherBlockEntity> SMITHER_BLOCK_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "smither_block_entity"),
                    BlockEntityType.Builder.of(SmitherBlockEntity::new, ModBlocks.SMITHER).build());
}
