package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedLecternBlock extends LecternBlock implements IReinforcedBlock {
	public ReinforcedLecternBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, state, player, level, pos);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		//only allow the owner or players on the allowlist to access a reinforced lectern
		if (level.getBlockEntity(pos) instanceof ReinforcedLecternBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)))
			return super.useItemOn(stack, state, level, pos, player, hand, hit);

		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		//only allow the owner or players on the allowlist to access a reinforced lectern
		if (level.getBlockEntity(pos) instanceof ReinforcedLecternBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)))
			return super.useWithoutItem(state, level, pos, player, hit);

		return InteractionResult.SUCCESS;
	}

	@Override
	public void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean isMoving) {
		level.updateNeighbourForOutputSignal(pos, this);
		super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
	}

	@Override
	public void openScreen(Level level, BlockPos pos, Player player) {
		if (level.getBlockEntity(pos) instanceof ReinforcedLecternBlockEntity be) {
			player.openMenu(be);
			player.awardStat(Stats.INTERACT_WITH_LECTERN);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedLecternBlockEntity(pos, state);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.LECTERN;
	}
}
