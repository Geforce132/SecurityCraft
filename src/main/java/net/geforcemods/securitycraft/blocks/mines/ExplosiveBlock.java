package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class ExplosiveBlock extends OwnableBlock implements IExplosive {
	protected ExplosiveBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return exUseItemOn(heldItem, state, level, pos, player, hand, hit);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return exUseWithoutItem(state, level, pos, player, hit);
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return true;
	}

	@Override
	public boolean isDefusable() {
		return true;
	}
}
