package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedFenceGateBlock extends FenceGateBlock implements EntityBlock {
	public ReinforcedFenceGateBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.FAIL;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (Player) placer));
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.getBlockState(pos).getValue(OPEN))
			return;

		if (entity instanceof ItemEntity)
			return;
		else if (entity instanceof Player player) {
			if (((OwnableBlockEntity) level.getBlockEntity(pos)).getOwner().isOwner(player))
				return;
		}
		else if (!level.isClientSide && entity instanceof Creeper creeper) {
			LightningBolt lightning = LevelUtils.createLightning(level, Vec3.atBottomCenterOf(pos), true);

			creeper.thunderHit((ServerLevel) level, lightning);
			return;
		}

		entity.hurt(CustomDamageSources.ELECTRICITY, 6.0F);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!level.isClientSide) {
			boolean isPoweredSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, pos);

			if (isPoweredSCBlock || block.defaultBlockState().isSignalSource())
				if (isPoweredSCBlock && !state.getValue(OPEN) && !state.getValue(POWERED)) {
					level.setBlock(pos, state.setValue(OPEN, true).setValue(POWERED, true), 2);
					level.levelEvent(null, LevelEvent.SOUND_OPEN_IRON_DOOR, pos, 0);
				}
				else if (!isPoweredSCBlock && state.getValue(OPEN) && state.getValue(POWERED)) {
					level.setBlock(pos, state.setValue(OPEN, false).setValue(POWERED, false), 2);
					level.levelEvent(null, LevelEvent.SOUND_CLOSE_IRON_DOOR, pos, 0);
				}
				else if (isPoweredSCBlock != state.getValue(POWERED))
					level.setBlock(pos, state.setValue(POWERED, isPoweredSCBlock), 2);
		}
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);
		BlockEntity blockEntity = level.getBlockEntity(pos);
		return blockEntity != null ? blockEntity.triggerEvent(id, param) : false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get(), pos, state);
	}
}
