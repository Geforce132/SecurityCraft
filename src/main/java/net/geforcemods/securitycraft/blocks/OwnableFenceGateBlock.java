package net.geforcemods.securitycraft.blocks;

import java.util.function.BiConsumer;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import net.neoforged.neoforge.common.NeoForge;

public class OwnableFenceGateBlock extends FenceGateBlock implements EntityBlock {
	private final float destroyTimeForOwner;

	public OwnableFenceGateBlock(BlockBehaviour.Properties properties, WoodType woodType) {
		super(woodType, OwnableBlock.withReinforcedDestroyTime(properties));
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	public OwnableFenceGateBlock(BlockBehaviour.Properties properties, SoundEvent openSound, SoundEvent closeSound) {
		super(OwnableBlock.withReinforcedDestroyTime(properties), openSound, closeSound);
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
		if (!explosion.canTriggerBlocks())
			super.onExplosionHit(state, level, pos, explosion, dropConsumer);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(context.getLevel(), context.getClickedPos());

		return super.getStateForPlacement(context).setValue(OPEN, hasActiveSCBlock).setValue(POWERED, hasActiveSCBlock);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean isMoving) {
		if (!level.isClientSide()) {
			boolean isPoweredSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, pos);

			if (state.getValue(POWERED) != isPoweredSCBlock) {
				level.setBlock(pos, state.setValue(POWERED, isPoweredSCBlock).setValue(OPEN, isPoweredSCBlock), 2);

				if (state.getValue(OPEN) != isPoweredSCBlock) {
					level.playSound(null, pos, isPoweredSCBlock ? openSound : closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
					level.gameEvent(null, isPoweredSCBlock ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
				}
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(pos, state);
	}
}
