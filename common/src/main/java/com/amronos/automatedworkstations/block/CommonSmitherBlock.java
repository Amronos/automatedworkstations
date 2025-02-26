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
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CommonSmitherBlock extends CommonAutomatedWorkstationBlock {

    public CommonSmitherBlock(Properties properties) {
        super(properties.strength(1.5F, 3.5F));
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(ORIENTATION, FrontAndTop.NORTH_UP)
                        .setValue(TRIGGERED, Boolean.FALSE)
                        .setValue(CRAFTING, Boolean.FALSE)
        );
    }

    @Override
    protected abstract MapCodec<CommonSmitherBlock> codec();

    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType);

    /**
     * Returns the analog signal this block emits. This is the signal a comparator can read from it.
     *
     */
    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CommonSmitherBlockEntity smitherblockentity ? smitherblockentity.getRedstoneSignal() : 0;
    }

    @Override
    protected void setBlockEntityTriggered(@Nullable BlockEntity blockEntity, boolean triggered) {
        if (blockEntity instanceof CommonSmitherBlockEntity smitherblockentity) {
            smitherblockentity.setTriggered(triggered);
        }
    }

    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @Override
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
                    smitherblockentity.setCraftingTicksRemaining(6);
                    level.setBlock(pos, state.setValue(CRAFTING, Boolean.TRUE), 2);
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

    private void dispenseItem(ServerLevel pLevel, BlockPos pPos, CommonSmitherBlockEntity smitherblockentity, ItemStack stack, BlockState state, RecipeHolder<SmithingRecipe> recipe) {
        Direction direction = state.getValue(ORIENTATION).front();
        Container container = HopperBlockEntity.getContainerAt(pLevel, pPos.relative(direction));
        ItemStack itemstack = stack.copy();
        if (container != null && (container instanceof CommonSmitherBlockEntity || stack.getCount() > container.getMaxStackSize(stack))) {
            while (!itemstack.isEmpty()) {
                ItemStack itemstack2 = itemstack.copyWithCount(1);
                ItemStack itemstack1 = HopperBlockEntity.addItem(smitherblockentity, container, itemstack2, direction.getOpposite());
                if (!itemstack1.isEmpty()) {
                    break;
                }

                itemstack.shrink(1);
            }
        }
        else if (container != null) {
            while (!itemstack.isEmpty()) {
                int i = itemstack.getCount();
                itemstack = HopperBlockEntity.addItem(smitherblockentity, container, itemstack, direction.getOpposite());
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
                CriteriaTriggers.CRAFTER_RECIPE_CRAFTED.trigger(serverplayer, recipe.id(), smitherblockentity.getItems());
            }

            pLevel.levelEvent(1049, pPos, 0);
            pLevel.levelEvent(2010, pPos, direction.get3DDataValue());
        }
    }
}
