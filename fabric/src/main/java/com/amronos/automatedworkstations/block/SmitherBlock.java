package com.amronos.automatedworkstations.block;

import com.amronos.automatedworkstations.block.entity.CommonSmitherBlockEntity;
import com.amronos.automatedworkstations.block.entity.SmitherBlockEntity;
import com.amronos.automatedworkstations.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

public class SmitherBlock extends CommonSmitherBlock {
    public static final MapCodec<CommonSmitherBlock> CODEC = simpleCodec(SmitherBlock::new);

    public SmitherBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<CommonSmitherBlock> codec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return null;
        }
        else {
            return createTickerHelper(blockEntityType, ModBlockEntities.SMITHER_BLOCK_ENTITY, CommonSmitherBlockEntity::serverTick);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        SmitherBlockEntity smitherblockentity = new SmitherBlockEntity(pos, state);
        smitherblockentity.setTriggered(state.hasProperty(TRIGGERED) && state.getValue(TRIGGERED));
        return smitherblockentity;
    }
}
