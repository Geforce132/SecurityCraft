package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.EntityDataWrappedOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class ExplosiveBlock extends OwnableBlock implements IExplosive {
	protected ExplosiveBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return !ConfigHandler.SERVER.ableToBreakMines.get() ? -1F : super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (heldItem.is(SCContent.MINE_REMOTE_ACCESS_TOOL.get()))
			return ItemInteractionResult.SUCCESS;

		if (heldItem.getItem() == SCContent.WIRE_CUTTERS.get() && isActive(level, pos) && isDefusable()) {
			if (defuseMine(level, pos)) {
				if (!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

				level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
			}

			return ItemInteractionResult.SUCCESS;
		}

		if (heldItem.is(Items.FLINT_AND_STEEL) && !isActive(level, pos) && activateMine(level, pos)) {
			if (!player.isCreative())
				player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

			level.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
			return ItemInteractionResult.SUCCESS;
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (explodesWhenInteractedWith() && isActive(level, pos)) {
			BlockEntity be = level.getBlockEntity(pos);

			if (level.getBlockEntity(pos) instanceof ICustomizable mine) {
				for (Option<?> option : mine.customOptions()) {
					if (option instanceof EntityDataWrappedOption wrapped)
						option = wrapped.getWrapped();

					if (option instanceof TargetingModeOption targetingMode && !targetingMode.get().allowsPlayers())
						return InteractionResult.PASS;
					else if (option instanceof IgnoreOwnerOption ignoreOwner && ((IOwnable) be).isOwnedBy(player) && ignoreOwner.get())
						return InteractionResult.PASS;
				}
			}

			explode(level, pos);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	/**
	 * @return If the mine should explode when right-clicked?
	 */
	public boolean explodesWhenInteractedWith() {
		return true;
	}

	@Override
	public boolean isDefusable() {
		return true;
	}
}
