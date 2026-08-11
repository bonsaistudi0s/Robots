package dev.xylonity.bonsai.robots.common.block;

import dev.xylonity.bonsai.robots.common.entity.AbstractMechEntity;
import dev.xylonity.bonsai.robots.registry.RobotsBlocks;
import dev.xylonity.bonsai.robots.registry.RobotsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BatteryCellBlock extends Block {

    private final boolean charged;

    public BatteryCellBlock(Properties properties, boolean charged) {
        super(properties);
        this.charged = charged;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighbor, @NotNull BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighbor, neighborPos, movedByPiston);
        if (!this.charged && !level.isClientSide && neighborPos.equals(pos.above()) && isPoweredLightningRod(level.getBlockState(neighborPos))) {
            charge(level, pos);
        }

    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!this.charged && !level.isClientSide && isPoweredLightningRod(level.getBlockState(pos.above()))) {
            charge(level, pos);
        }

    }

    private static boolean isPoweredLightningRod(BlockState state) {
        return state.is(Blocks.LIGHTNING_ROD) && state.getValue(LightningRodBlock.POWERED);
    }

    private static void charge(Level level, BlockPos pos) {
        level.setBlock(pos, RobotsBlocks.BATTERY_CELL_CHARGED.get().defaultBlockState(), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.15F);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!this.charged || !(player.getVehicle() instanceof AbstractMechEntity mech) || mech.getControllingPassenger() != player || mech.getEnergy() >= mech.getMaxEnergy()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            mech.setEnergy(mech.getMaxEnergy());
            level.setBlock(pos, RobotsBlocks.BATTERY_CELL.get().defaultBlockState(), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.BLOCKS, 1.0F, 1.2F);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

}