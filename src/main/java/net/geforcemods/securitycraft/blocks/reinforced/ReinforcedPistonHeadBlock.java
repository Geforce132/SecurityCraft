package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedPistonHeadBlock extends PistonHeadBlock implements EntityBlock, IReinforcedBlock {

	public ReinforcedPistonHeadBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if(placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

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
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
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

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return defaultBlockState().setValue(FACING, vanillaState.getValue(FACING)).setValue(TYPE, vanillaState.getValue(TYPE)).setValue(SHORT, vanillaState.getValue(SHORT));
	}
}
