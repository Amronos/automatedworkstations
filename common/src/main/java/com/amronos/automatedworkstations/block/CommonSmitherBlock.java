package com.amronos.automatedworkstations.block;

import com.amronos.automatedworkstations.block.entity.CommonSmitherBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommonSmitherBlock extends BaseEntityBlock {
    public static final MapCodec<CommonSmitherBlock> CODEC = simpleCodec(CommonSmitherBlock::new);
    public static final BooleanProperty SMITHING = BlockStateProperties.CRAFTING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    private static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

    public CommonSmitherBlock(Properties properties) {
        super(properties.strength(1.5F, 3.5F));
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(ORIENTATION, FrontAndTop.NORTH_UP)
                        .setValue(TRIGGERED, Boolean.FALSE)
                        .setValue(SMITHING, Boolean.FALSE)
        );
    }

    @Override
    protected MapCodec<CommonSmitherBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    /**
     * Returns the analog signal this block emits. This is the signal a comparator can read from it.
     *
     */
    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CommonSmitherBlockEntity smitherblockentity ? smitherblockentity.getRedstoneSignal() : 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        boolean flag = level.hasNeighborSignal(pos);
        boolean flag1 = state.getValue(TRIGGERED);
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (flag && !flag1) {
            level.scheduleTick(pos, this, 4);
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 2);
            this.setBlockEntityTriggered(blockentity, true);
        } else if (!flag && flag1) {
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.FALSE).setValue(SMITHING, Boolean.FALSE), 2);
            this.setBlockEntityTriggered(blockentity, false);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.dispenseFrom(state, level, pos);
    }

    private void setBlockEntityTriggered(@Nullable BlockEntity blockEntity, boolean triggered) {
        if (blockEntity instanceof CommonSmitherBlockEntity smitherblockentity) {
            smitherblockentity.setTriggered(triggered);
        }
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getNearestLookingDirection().getOpposite();

        Direction direction1 = switch (direction) {
            case DOWN -> context.getHorizontalDirection().getOpposite();
            case UP -> context.getHorizontalDirection();
            case NORTH, SOUTH, WEST, EAST -> Direction.UP;
        };
        return this.defaultBlockState()
                .setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(direction, direction1))
                .setValue(TRIGGERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    protected void dispenseFrom(BlockState state, ServerLevel level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof CommonSmitherBlockEntity smitherblockentity) {
            SmithingRecipeInput smithingrecipeinput = smitherblockentity.createRecipeInput();
            List<RecipeHolder<SmithingRecipe>> list = level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, smithingrecipeinput, level);
            if (list.isEmpty()) {
                level.levelEvent(1050, pos, 0);
            }
            else {
                RecipeHolder<SmithingRecipe> recipeholder = list.getFirst();
                ItemStack itemstack = recipeholder.value().assemble(smithingrecipeinput, level.registryAccess());
                if (itemstack.isEmpty()) {
                    level.levelEvent(1050, pos, 0);
                }
                else {
                    smitherblockentity.setSmithingTicksRemaining(6);
                    level.setBlock(pos, state.setValue(SMITHING, Boolean.TRUE), 2);
                    itemstack.onCraftedBySystem(level);
                    this.dispenseItem(level, pos, smitherblockentity, itemstack, state, recipeholder);

                    for (ItemStack itemstack1 : recipeholder.value().getRemainingItems(smithingrecipeinput)) {
                        if (!itemstack1.isEmpty()) {
                            this.dispenseItem(level, pos, smitherblockentity, itemstack1, state, recipeholder);
                        }
                    }

                    smitherblockentity.getItems().forEach(itemStack -> {
                        if (!itemStack.isEmpty()) {
                            itemStack.shrink(1);
                        }
                    });
                    smitherblockentity.setChanged();
                }
            }
        }
    }

    /**
     * Called by BlockItem after this block has been placed.
     */
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (pState.getValue(TRIGGERED)) {
            pLevel.scheduleTick(pPos, this, 4);
        }
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        Containers.dropContentsOnDestroy(pState, pNewState, pLevel, pPos);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof CommonSmitherBlockEntity) {
                player.openMenu((CommonSmitherBlockEntity) blockentity);
            }

            return InteractionResult.CONSUME;
        }
    }

    private void dispenseItem(ServerLevel pLevel, BlockPos pPos, CommonSmitherBlockEntity smither, ItemStack stack, BlockState state, RecipeHolder<SmithingRecipe> recipe) {
        Direction direction = state.getValue(ORIENTATION).front();
        Container container = HopperBlockEntity.getContainerAt(pLevel, pPos.relative(direction));
        ItemStack itemstack = stack.copy();
        if (container != null && (container instanceof CommonSmitherBlockEntity || stack.getCount() > container.getMaxStackSize(stack))) {
            while (!itemstack.isEmpty()) {
                ItemStack itemstack2 = itemstack.copyWithCount(1);
                ItemStack itemstack1 = HopperBlockEntity.addItem(smither, container, itemstack2, direction.getOpposite());
                if (!itemstack1.isEmpty()) {
                    break;
                }

                itemstack.shrink(1);
            }
        }
        else if (container != null) {
            while (!itemstack.isEmpty()) {
                int i = itemstack.getCount();
                itemstack = HopperBlockEntity.addItem(smither, container, itemstack, direction.getOpposite());
                if (i == itemstack.getCount()) {
                    break;
                }
            }
        }

        if (!itemstack.isEmpty()) {
            Vec3 vec3 = Vec3.atCenterOf(pPos);
            Vec3 vec31 = vec3.relative(direction, 0.7);
            DefaultDispenseItemBehavior.spawnItem(pLevel, itemstack, 6, direction, vec31);

            for (ServerPlayer serverplayer : pLevel.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(vec3, 17.0, 17.0, 17.0))) {
                CriteriaTriggers.CRAFTER_RECIPE_CRAFTED.trigger(serverplayer, recipe.id(), smither.getItems());
            }

            pLevel.levelEvent(1049, pPos, 0);
            pLevel.levelEvent(2010, pPos, direction.get3DDataValue());
        }
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    protected BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(ORIENTATION, pRotation.rotation().rotate(pState.getValue(ORIENTATION)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.setValue(ORIENTATION, pMirror.rotation().rotate(pState.getValue(ORIENTATION)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ORIENTATION, TRIGGERED, SMITHING);
    }
}
