package net.geforcemods.securitycraft.items;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SonicSecuritySystemItem extends BlockItem {
	public SonicSecuritySystemItem(Properties properties) {
		super(SCContent.SONIC_SECURITY_SYSTEM.get(), properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
		Level level = ctx.getLevel();
		Player player = ctx.getPlayer();

		// If the player is not sneaking, add/remove positions from the item when right-clicking a lockable block
		if (!player.isShiftKeyDown()) {
			BlockPos pos = ctx.getClickedPos();
			BlockEntity be = level.getBlockEntity(pos);

			if (be instanceof ILockable) {
				if (be instanceof IOwnable ownable && !ownable.isOwnedBy(player)) {
					//only send message when the block is not disguised
					if (!(be.getBlockState().getBlock() instanceof DisguisableBlock) || !DisguisableBlock.getDisguisedBlockState(level, pos).isPresent()) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", ownable.getOwner().getName()), ChatFormatting.GREEN);
						return InteractionResult.SUCCESS;
					}
				}
				else {
					// Remove a block from the tag if it was already linked to.
					// If not, link to it
					if (isAdded(Utils.getTag(stack), pos)) {
						removeLinkedBlock(stack, pos);
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.blockUnlinked", Utils.localize(level.getBlockState(pos).getBlock().getDescriptionId()), pos), ChatFormatting.GREEN);
						return InteractionResult.SUCCESS;
					}
					else if (addLinkedBlock(stack, pos, player)) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.blockLinked", Utils.localize(level.getBlockState(pos).getBlock().getDescriptionId()), pos), ChatFormatting.GREEN);
						return InteractionResult.SUCCESS;
					}
				}
			}
		}

		//don't place down the SSS if it has at least one linked block
		//placing is handled by minecraft otherwise
		if (!stack.has(DataComponents.CUSTOM_DATA) || !hasLinkedBlock(stack)) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.notLinked"), ChatFormatting.DARK_RED);
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide)
			ClientHandler.displaySSSItemScreen(player.getItemInHand(hand));

		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state) {
		if (level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss && sss.transferPositionsFromItem(Utils.getTag(stack)))
			return true;

		return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
		// If this item is storing block positions, show the number of them in the tooltip
		int numOfLinkedBlocks = Utils.getTag(stack).getList("LinkedBlocks", Tag.TAG_COMPOUND).size();

		if (numOfLinkedBlocks > 0)
			tooltip.add(Utils.localize("tooltip.securitycraft:sonicSecuritySystem.linkedTo", numOfLinkedBlocks).withStyle(Utils.GRAY_STYLE));
	}

	/**
	 * Adds a position to a stack
	 *
	 * @param stack The stack to add the position to
	 * @param pos The position to add to the stack
	 * @param player The player who tries to link a block
	 * @return true if the position was added, false otherwise
	 */
	public static boolean addLinkedBlock(ItemStack stack, BlockPos pos, Player player) {
		CompoundTag tag = Utils.getTag(stack);

		// If the position was already added, return
		if (isAdded(tag, pos))
			return false;

		ListTag list = tag.getList("LinkedBlocks", Tag.TAG_COMPOUND);

		if (list.size() >= SonicSecuritySystemBlockEntity.MAX_LINKED_BLOCKS) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.linkMaxReached", SonicSecuritySystemBlockEntity.MAX_LINKED_BLOCKS), ChatFormatting.DARK_RED);
			return false;
		}

		CompoundTag nbt = Utils.writeBlockPos(pos);

		list.add(nbt);
		tag.put("LinkedBlocks", list);
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		return true;
	}

	/**
	 * Removes a position from a tag
	 *
	 * @param stack The stack to remove the position from
	 * @param pos The position to remove from the tag
	 */
	public static void removeLinkedBlock(ItemStack stack, BlockPos pos) {
		CustomData customData = Utils.getCustomData(stack);

		if (!customData.contains("LinkedBlocks"))
			return;

		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			ListTag list = tag.getList("LinkedBlocks", Tag.TAG_COMPOUND);

			// Starting from the end of the list to prevent skipping over entries
			for (int i = list.size() - 1; i >= 0; i--) {
				BlockPos posRead = Utils.readBlockPos(list.getCompound(i));

				if (pos.equals(posRead))
					list.remove(i);
			}
		});
	}

	/**
	 * Checks whether a position is added to a tag
	 *
	 * @param tag The tag to check
	 * @param pos The position to check
	 * @return true if the position is added, false otherwise
	 */
	public static boolean isAdded(CompoundTag tag, BlockPos pos) {
		if (!tag.contains("LinkedBlocks"))
			return false;

		ListTag list = tag.getList("LinkedBlocks", Tag.TAG_COMPOUND);

		for (int i = 0; i < list.size(); i++) {
			BlockPos posRead = Utils.readBlockPos(list.getCompound(i));

			if (pos.equals(posRead))
				return true;
		}

		return false;
	}

	/**
	 * @return true if the tag contains at least one position, false otherwise
	 */
	public static boolean hasLinkedBlock(ItemStack stack) {
		CompoundTag tag = Utils.getTag(stack);

		if (!tag.contains("LinkedBlocks"))
			return false;

		return !tag.getList("LinkedBlocks", Tag.TAG_COMPOUND).isEmpty();
	}

	/**
	 * Copies the positions over from the SSS item's tag into a new set.
	 *
	 * @param itemTag The CompoundTag of the Sonic Security System item to transfer over
	 */
	public static Set<BlockPos> stackTagToBlockPosSet(CompoundTag itemTag) {
		if (itemTag == null || !itemTag.contains("LinkedBlocks"))
			return Set.of();

		ListTag blocks = itemTag.getList("LinkedBlocks", Tag.TAG_COMPOUND);
		Set<BlockPos> positions = new HashSet<>();

		for (int i = 0; i < blocks.size(); i++) {
			positions.add(Utils.readBlockPos(blocks.getCompound(i)));
		}

		return positions;
	}
}
