package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.blockentities.ReinforcedChiseledBookshelfBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedChiseledBookshelfBlock extends ChiseledBookShelfBlock implements IReinforcedBlock {
	public ReinforcedChiseledBookshelfBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof ReinforcedChiseledBookshelfBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)))
			return super.use(state, level, pos, player, hand, hit);

		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (isMoving && level.getBlockEntity(pos) instanceof ChiseledBookShelfBlockEntity be)
			be.clearContent(); //Clear the books from the block before it is moved by a piston to prevent book duplication

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedChiseledBookshelfBlockEntity(pos, state);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.CHISELED_BOOKSHELF;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}
}
