package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WireCuttersItem extends Item {
	public WireCuttersItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext ctx) {
		PlayerEntity player = ctx.getPlayer();
		World level = ctx.getLevel();

		if (player.isShiftKeyDown()) {
			BlockPos pos = ctx.getClickedPos();
			BlockState state = level.getBlockState(pos);
			ItemStack stack = ctx.getItemInHand();
			Hand hand = ctx.getHand();

			for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
				if (pc.isProtectedBlock(state)) {
					TileEntity be = level.getBlockEntity(pos);

					if (be instanceof IOwnable && ((IOwnable) be).isOwnedBy(player) && pc.unprotect(player, level, pos)) {
						stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
						level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
						Block.popResource(level, pos, new ItemStack(SCContent.KEY_PANEL.get(), pc.getRequiredKeyPanels(state)));
					}
				}
			}
		}

		return ActionResultType.sidedSuccess(level.isClientSide);
	}
}
