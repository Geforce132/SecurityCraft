package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedPistonHeadBlock extends PistonHeadBlock implements EntityBlock, IReinforcedBlock {
	public ReinforcedPistonHeadBlock(BlockBehaviour.Properties properties) {
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
	public boolean isFittingBase(BlockState baseState, BlockState extendedState) {
		Block block = baseState.getValue(TYPE) == PistonType.DEFAULT ? SCContent.REINFORCED_PISTON.get() : SCContent.REINFORCED_STICKY_PISTON.get();

		return extendedState.is(block) && extendedState.getValue(PistonBaseBlock.EXTENDED) && extendedState.getValue(FACING) == baseState.getValue(FACING);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState oppositeState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));

		return isFittingBase(state, oppositeState) || oppositeState.is(SCContent.REINFORCED_MOVING_PISTON.get()) && oppositeState.getValue(FACING) == state.getValue(FACING);
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
		return new ItemStack(state.getValue(TYPE) == PistonType.STICKY ? SCContent.REINFORCED_STICKY_PISTON.get() : SCContent.REINFORCED_PISTON.get());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(pos, state);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.PISTON_HEAD;
	}
}
