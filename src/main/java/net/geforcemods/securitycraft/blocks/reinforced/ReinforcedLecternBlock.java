package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkHooks;

public class ReinforcedLecternBlock extends LecternBlock implements IReinforcedBlock {
	private final float destroyTimeForOwner;

	public ReinforcedLecternBlock(BlockBehaviour.Properties properties) {
		super(OwnableBlock.withReinforcedDestroyTime(properties));
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		//only allow the owner or players on the allowlist to access a reinforced lectern
		if (level.getBlockEntity(pos) instanceof ReinforcedLecternBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)))
			return super.use(state, level, pos, player, hand, hit);

		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ReinforcedLecternBlockEntity be) {
			if (isMoving)
				be.clearContent(); //Clear the items from the block before it is moved by a piston to prevent duplication

			level.updateNeighbourForOutputSignal(pos, this);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void openScreen(Level level, BlockPos pos, Player player) {
		if (level.getBlockEntity(pos) instanceof ReinforcedLecternBlockEntity be) {
			NetworkHooks.openGui((ServerPlayer) player, be, pos);
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
