package com.amronos.automatedworkstations.block;

import com.amronos.automatedworkstations.block.entity.AnvilatorBlockEntity;
import com.amronos.automatedworkstations.block.entity.CommonAnvilatorBlockEntity;
import com.amronos.automatedworkstations.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AnvilatorBlock extends CommonAnvilatorBlock {
    public static final MapCodec<CommonAnvilatorBlock> CODEC = simpleCodec(AnvilatorBlock::new);

    public AnvilatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<CommonAnvilatorBlock> codec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return null;
        }
        else {
            return createTickerHelper(blockEntityType, ModBlockEntities.ANVILATOR_BLOCK_ENTITY.get(), CommonAnvilatorBlockEntity::serverTick);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        AnvilatorBlockEntity anvilatorblockentity = new AnvilatorBlockEntity(pos, state);
        anvilatorblockentity.setTriggered(state.hasProperty(TRIGGERED) && state.getValue(TRIGGERED));
        return anvilatorblockentity;
    }
}
