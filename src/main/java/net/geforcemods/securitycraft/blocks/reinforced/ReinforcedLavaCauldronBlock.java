package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class ReinforcedLavaCauldronBlock extends ReinforcedCauldronBlock {
	public ReinforcedLavaCauldronBlock(Properties properties) {
		super(properties, IReinforcedCauldronInteraction.LAVA);
	}

	@Override
	protected double getContentHeight(BlockState state) {
		return 0.9375D;
	}

	@Override
	public boolean isFull(BlockState state) {
		return true;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (isEntityInsideContent(state, pos, entity))
			entity.lavaHurt();
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return 3;
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.LAVA_CAULDRON;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return new ItemStack(SCContent.REINFORCED_CAULDRON.get());
	}
}
