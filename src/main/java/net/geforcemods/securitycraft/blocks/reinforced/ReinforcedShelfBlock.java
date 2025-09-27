package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedShelfBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedShelfBlock extends ShelfBlock implements IReinforcedBlock {
	private final Block vanillaBlock;
	private final float destroyTimeForOwner;

	public ReinforcedShelfBlock(BlockBehaviour.Properties properties, Block vanillaBlock) {
		super(OwnableBlock.withReinforcedDestroyTime(properties));
		this.vanillaBlock = vanillaBlock;
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		//Only allow interaction if this shelf, as well as all connected shelves, are owned by or allow the interacting player
		if (!isConnectable(state)) {
			if ((!(level.getBlockEntity(pos) instanceof ReinforcedShelfBlockEntity be) || (!be.isOwnedBy(player) && !be.isAllowed(player))))
				return InteractionResult.SUCCESS;
		}
		else {
			for (BlockPos connectedPos : getAllBlocksConnectedTo(level, pos)) {
				if (!(level.getBlockEntity(connectedPos) instanceof ReinforcedShelfBlockEntity be) || (!be.isOwnedBy(player) && !be.isAllowed(player)))
					return InteractionResult.SUCCESS;
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public boolean isConnectable(BlockState state) {
		return state.is(SCTags.Blocks.REINFORCED_WOODEN_SHELVES) && state.hasProperty(POWERED) && state.getValue(POWERED);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedShelfBlockEntity(pos, state);
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}
}
