package com.amronos.automatedworkstations.block;

import com.amronos.automatedworkstations.block.entity.CommonSmitherBlockEntity;
import com.amronos.automatedworkstations.block.entity.SmitherBlockEntity;
import com.amronos.automatedworkstations.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SmitherBlock extends CommonSmitherBlock {
    public static final MapCodec<CommonSmitherBlock> CODEC = simpleCodec(SmitherBlock::new);

    public SmitherBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<CommonSmitherBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return null;
        }
        else {
            return createTickerHelper(blockEntityType, ModBlockEntities.SMITHER_BLOCK_ENTITY.get(), CommonSmitherBlockEntity::serverTick);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        SmitherBlockEntity smitherblockentity = new SmitherBlockEntity(pos, state);
        smitherblockentity.setTriggered(state.hasProperty(TRIGGERED) && state.getValue(TRIGGERED));
        return smitherblockentity;
    }
}
