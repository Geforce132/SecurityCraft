package net.geforcemods.securitycraft.items;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
		if (!level.isClientSide && !player.isShiftKeyDown()) {
			BlockPos pos = ctx.getClickedPos();
			BlockEntity be = level.getBlockEntity(pos);

			if (be instanceof ILockable) {
				if (be instanceof IOwnable ownable && !ownable.isOwnedBy(player)) {
					//only send message when the block is not disguised
					if (!(be.getBlockState().getBlock() instanceof IDisguisable) || !IDisguisable.getDisguisedBlockState(be).isPresent()) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", ownable.getOwner().getName()), ChatFormatting.GREEN);
						return InteractionResult.SUCCESS;
					}
				}
				else {
					if (stack.getTag() == null)
						stack.setTag(new CompoundTag());

					// Remove a block from the tag if it was already linked to.
					// If not, link to it
					if (isAdded(stack.getTag(), pos)) {
						removeLinkedBlock(stack.getTag(), pos);
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.blockUnlinked", Utils.localize(level.getBlockState(pos).getBlock().getDescriptionId()), pos), ChatFormatting.GREEN);
						return InteractionResult.SUCCESS;
					}
					else if (addLinkedBlock(stack.getOrCreateTag(), pos, player)) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.blockLinked", Utils.localize(level.getBlockState(pos).getBlock().getDescriptionId()), pos), ChatFormatting.GREEN);
						return InteractionResult.SUCCESS;
					}
				}
			}
		}

		//don't place down the SSS if it has at least one linked block
		//placing is handled by minecraft otherwise
		if (!stack.hasTag() || !hasLinkedBlock(stack.getTag())) {
			if (!level.isClientSide)
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
		if (level.getBlockEntity(pos) instanceof SonicSecuritySystemBlockEntity sss && sss.transferPositionsFromItem(stack.getOrCreateTag()))
			return true;

		return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		if (!stack.hasTag())
			return;

		// If this item is storing block positions, show the number of them in the tooltip
		int numOfLinkedBlocks = stack.getTag().getList("LinkedBlocks", Tag.TAG_COMPOUND).size();

		if (numOfLinkedBlocks > 0)
			tooltip.add(Utils.localize("tooltip.securitycraft:sonicSecuritySystem.linkedTo", numOfLinkedBlocks).withStyle(Utils.GRAY_STYLE));
	}

	/**
	 * Adds a position to a tag
	 *
	 * @param tag The tag to add the position to
	 * @param pos The position to add to the tag
	 * @param player The player who tries to link a block
	 * @return true if the position was added, false otherwise
	 */
	public static boolean addLinkedBlock(CompoundTag tag, BlockPos pos, Player player) {
		// If the position was already added, return
		if (isAdded(tag, pos))
			return false;

		ListTag list = tag.getList("LinkedBlocks", Tag.TAG_COMPOUND);

		if (list.size() >= SonicSecuritySystemBlockEntity.MAX_LINKED_BLOCKS) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.linkMaxReached", SonicSecuritySystemBlockEntity.MAX_LINKED_BLOCKS), ChatFormatting.DARK_RED);
			return false;
		}

		CompoundTag nbt = NbtUtils.writeBlockPos(pos);

		list.add(nbt);
		tag.put("LinkedBlocks", list);
		return true;
	}

	/**
	 * Removes a position from a tag
	 *
	 * @param tag The tag to remove the position from
	 * @param pos The position to remove from the tag
	 */
	public static void removeLinkedBlock(CompoundTag tag, BlockPos pos) {
		if (tag == null || !tag.contains("LinkedBlocks"))
			return;

		ListTag list = tag.getList("LinkedBlocks", Tag.TAG_COMPOUND);

		// Starting from the end of the list to prevent skipping over entries
		for (int i = list.size() - 1; i >= 0; i--) {
			BlockPos posRead = NbtUtils.readBlockPos(list.getCompound(i));

			if (pos.equals(posRead))
				list.remove(i);
		}
	}

	/**
	 * Checks whether a position is added to a tag
	 *
	 * @param tag The tag to check
	 * @param pos The position to check
	 * @return true if the position is added, false otherwise
	 */
	public static boolean isAdded(CompoundTag tag, BlockPos pos) {
		if (tag == null || !tag.contains("LinkedBlocks"))
			return false;

		ListTag list = tag.getList("LinkedBlocks", Tag.TAG_COMPOUND);

		for (int i = 0; i < list.size(); i++) {
			BlockPos posRead = NbtUtils.readBlockPos(list.getCompound(i));

			if (pos.equals(posRead))
				return true;
		}

		return false;
	}

	/**
	 * @return true if the tag contains at least one position, false otherwise
	 */
	public static boolean hasLinkedBlock(CompoundTag tag) {
		if (tag == null || !tag.contains("LinkedBlocks"))
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
			positions.add(NbtUtils.readBlockPos(blocks.getCompound(i)));
		}

		return positions;
	}
}
