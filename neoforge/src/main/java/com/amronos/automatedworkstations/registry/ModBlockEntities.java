package com.amronos.automatedworkstations.registry;

import com.amronos.automatedworkstations.Constants;
import com.amronos.automatedworkstations.block.entity.SmitherBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final Supplier<BlockEntityType<SmitherBlockEntity>> SMITHER_BLOCK_ENTITY = BLOCK_ENTITIES.register("smither_block_entity", () -> BlockEntityType.Builder.of(SmitherBlockEntity::new, ModBlocks.SMITHER.get()).build(null));
}
