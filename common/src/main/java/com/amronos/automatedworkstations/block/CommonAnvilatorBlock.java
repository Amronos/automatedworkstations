package com.amronos.automatedworkstations.block;

import com.amronos.automatedworkstations.block.entity.CommonAnvilatorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class CommonAnvilatorBlock extends CommonAutomatedWorkstationBlock {

    protected CommonAnvilatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(ORIENTATION, FrontAndTop.NORTH_UP)
                        .setValue(TRIGGERED, Boolean.FALSE)
                        .setValue(CRAFTING, Boolean.FALSE)
        );
    }

    @Override
    protected abstract MapCodec<CommonAnvilatorBlock> codec();

    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType);

    /**
     * Returns the analog signal this block emits. This is the signal a comparator can read from it.
     *
     */
    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CommonAnvilatorBlockEntity anvilatorblockentity ? anvilatorblockentity.getRedstoneSignal() : 0;
    }

    @Override
    protected void setBlockEntityTriggered(@Nullable BlockEntity blockEntity, boolean triggered) {
        if (blockEntity instanceof CommonAnvilatorBlockEntity anvilatorblockentity) {
            anvilatorblockentity.setTriggered(triggered);
        }
    }

    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @Override
    protected void dispenseFrom(BlockState state, ServerLevel level, BlockPos pos) {
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof CommonAnvilatorBlockEntity) {
                player.openMenu((CommonAnvilatorBlockEntity) blockentity);
            }
            return InteractionResult.CONSUME;
        }
    }

    private void dispenseItem(){
    }
}
