package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedHopperBlock extends HopperBlock implements IReinforcedBlock {
	public ReinforcedHopperBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (Player) placer));

		if (stack.hasCustomHoverName()) {
			if (level.getBlockEntity(pos) instanceof ReinforcedHopperBlockEntity be)
				be.setCustomName(stack.getHoverName());
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide) {
			if (level.getBlockEntity(pos) instanceof ReinforcedHopperBlockEntity be) {
				Owner owner = be.getOwner();

				if (!owner.isValidated()) {
					if (be.isOwnedBy(player)) {
						owner.setValidated(true);
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Component.translatable("messages.securitycraft:ownable.validate"), ChatFormatting.GREEN);
						return InteractionResult.SUCCESS;
					}

					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Component.translatable("messages.securitycraft:ownable.ownerNotValidated"), ChatFormatting.RED);
					return InteractionResult.SUCCESS;
				}

				//only allow the owner or players on the allowlist to access a reinforced hopper
				if (be.isOwnedBy(player) || be.isAllowed(player))
					player.openMenu(be);
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof ReinforcedHopperBlockEntity be) {
				if (!isMoving)
					Containers.dropContents(level, pos, be);

				level.updateNeighbourForOutputSignal(pos, this);
			}

			level.removeBlockEntity(pos);
		}
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.getBlockEntity(pos) instanceof ReinforcedHopperBlockEntity be)
			ReinforcedHopperBlockEntity.entityInside(level, pos, state, entity, be);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedHopperBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, SCContent.REINFORCED_HOPPER_BLOCK_ENTITY.get(), ReinforcedHopperBlockEntity::pushItemsTick);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.HOPPER;
	}

	public static class ExtractionBlock implements IExtractionBlock {
		@Override
		public boolean canExtract(IOwnable be, Level level, BlockPos pos, BlockState state) {
			ReinforcedHopperBlockEntity hopperBe = (ReinforcedHopperBlockEntity) level.getBlockEntity(pos);

			if (!hopperBe.getOwner().isValidated())
				return false;
			else if (!be.getOwner().owns(hopperBe)) {
				if (be instanceof IModuleInventory inv) {
					if (inv.isAllowed(hopperBe.getOwner().getName())) //hoppers can extract out of e.g. chests if the hopper's owner is on the chest's allowlist module
						return true;
					else if (hopperBe.isAllowed(be.getOwner().getName())) //hoppers can extract out of e.g. chests whose owner is on the hopper's allowlist module
						return true;
				}

				return false;
			}
			else
				return true;
		}

		@Override
		public Block getBlock() {
			return SCContent.REINFORCED_HOPPER.get();
		}
	}
}
