package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WireCuttersItem extends Item {
	public WireCuttersItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		Level level = ctx.getLevel();

		if (player.isShiftKeyDown()) {
			BlockPos pos = ctx.getClickedPos();
			BlockState state = level.getBlockState(pos);
			ItemStack stack = ctx.getItemInHand();
			InteractionHand hand = ctx.getHand();

			for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
				if (pc.isProtectedBlock(state) && level.getBlockEntity(pos) instanceof IOwnable ownable && ownable.isOwnedBy(player) && pc.unprotect(player, level, pos)) {
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
					level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
					Block.popResource(level, pos, new ItemStack(SCContent.KEY_PANEL.get(), pc.getRequiredKeyPanels(state)));
				}
			}
		}

		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
