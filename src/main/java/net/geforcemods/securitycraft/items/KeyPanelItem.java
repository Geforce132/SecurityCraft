package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KeyPanelItem extends BlockItem {
	public KeyPanelItem(Item.Properties properties) {
		super(SCContent.KEY_PANEL_BLOCK.get(), properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Player player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();

		if (!(level.getBlockEntity(pos) instanceof IOwnable ownable) || ownable.isOwnedBy(player)) {
			for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
				if (pc.isUnprotectedBlock(state)) {
					int requiredKeyPanels = pc.getRequiredKeyPanels(state);

					if (requiredKeyPanels > stack.getCount()) {
						PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Component.translatable("messages.securitycraft:notEnoughKeyPanels", requiredKeyPanels), ChatFormatting.RED);
						return InteractionResult.FAIL;
					}

					if (pc.protect(player, level, pos)) {
						if (!player.isCreative())
							stack.shrink(requiredKeyPanels);

						level.playSound(null, pos, SCSounds.LOCK.event, SoundSource.BLOCKS, 1.0F, 1.0F);
						return InteractionResult.SUCCESS;
					}

					return InteractionResult.FAIL;
				}
			}
		}

		return super.useOn(ctx); //allow key panel to be placed when it did not convert anything
	}
}
