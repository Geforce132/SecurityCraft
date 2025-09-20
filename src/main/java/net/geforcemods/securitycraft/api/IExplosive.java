package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option.EntityDataWrappedOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Enables a Block to be remotely detonated using SecurityCraft's mine remote access tool.
 *
 * @author Geforce
 */
public interface IExplosive {
	/**
	 * Handle your explosive's explosion here.
	 *
	 * @param level The level your block is in.
	 * @param pos Your block's position.
	 */
	public void explode(Level level, BlockPos pos);

	/**
	 * Re-activate your defused mine.
	 *
	 * @param level The level your block is in.
	 * @param pos Your block's position.
	 * @return true if the mine was activated, false otherwise
	 */
	public boolean activateMine(Level level, BlockPos pos);

	/**
	 * Defuse your active mine.
	 *
	 * @param level The level your block is in.
	 * @param pos Your block's position.
	 * @return true if the mine was defused, false otherwise
	 */
	public boolean defuseMine(Level level, BlockPos pos);

	/**
	 * Whether the mine is currently active, aka if it can explode
	 *
	 * @param level The level the block is in
	 * @param pos true if the mine is active, false otherwise
	 */
	public boolean isActive(Level level, BlockPos pos);

	/**
	 * @return Is your mine defusable?
	 */
	public boolean isDefusable();

	/**
	 * @return If the mine should explode when right-clicked?
	 */
	public boolean explodesWhenInteractedWith();

	public default InteractionResult exUseItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (heldItem.is(SCContent.MINE_REMOTE_ACCESS_TOOL.get()))
			return InteractionResult.SUCCESS;

		if (heldItem.getItem() == SCContent.WIRE_CUTTERS.get() && isActive(level, pos) && isDefusable()) {
			if (defuseMine(level, pos)) {
				if (!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, hand.asEquipmentSlot());

				level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
			}

			return InteractionResult.SUCCESS;
		}

		if (heldItem.is(Items.FLINT_AND_STEEL) && !isActive(level, pos) && activateMine(level, pos)) {
			if (!player.isCreative())
				player.getItemInHand(hand).hurtAndBreak(1, player, hand.asEquipmentSlot());

			level.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	public default InteractionResult exUseWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
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
}
