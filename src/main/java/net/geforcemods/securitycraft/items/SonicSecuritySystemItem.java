package net.geforcemods.securitycraft.items;

import java.util.List;
import java.util.Objects;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
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
					GlobalPositions positions = stack.get(SCContent.SSS_LINKED_BLOCKS);

					if (positions != null) {
						GlobalPos globalPos = new GlobalPos(level.dimension(), pos);

						if (positions.remove(SCContent.SSS_LINKED_BLOCKS, stack, globalPos))
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.blockUnlinked", Utils.localize(level.getBlockState(pos).getBlock().getDescriptionId()), pos), ChatFormatting.GREEN);
						else if (positions.add(SCContent.SSS_LINKED_BLOCKS, stack, globalPos))
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.blockLinked", Utils.localize(level.getBlockState(pos).getBlock().getDescriptionId()), pos), ChatFormatting.GREEN);
						else {
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.SONIC_SECURITY_SYSTEM.get().getDescriptionId()), Utils.localize("messages.securitycraft:sonic_security_system.linkMaxReached", SonicSecuritySystemBlockEntity.MAX_LINKED_BLOCKS), ChatFormatting.DARK_RED);
							return InteractionResult.FAIL;
						}

						return InteractionResult.SUCCESS;
					}
				}
			}
		}

		//don't place down the SSS if it has at least one linked block
		//placing is handled by minecraft otherwise
		GlobalPositions sssLinkedBlocks = stack.get(SCContent.SSS_LINKED_BLOCKS);

		if (sssLinkedBlocks != null && sssLinkedBlocks.isEmpty()) {
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
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
		GlobalPositions sssLinkedBlocks = stack.get(SCContent.SSS_LINKED_BLOCKS);

		if (sssLinkedBlocks != null) {
			// If this item is storing block positions, show the number of them in the tooltip
			long numOfLinkedBlocks = sssLinkedBlocks.positions().stream().filter(Objects::nonNull).count();

			if (numOfLinkedBlocks > 0)
				tooltip.add(Utils.localize("tooltip.securitycraft:sonicSecuritySystem.linkedTo", numOfLinkedBlocks).withStyle(Utils.GRAY_STYLE));
		}
	}
}
