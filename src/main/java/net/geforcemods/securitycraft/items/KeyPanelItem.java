package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeyPanelItem extends BlockItem {
	public KeyPanelItem(Item.Properties properties) {
		super(SCContent.KEY_PANEL_BLOCK.get(), properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = level.getBlockState(pos);
		PlayerEntity player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();

		for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
			if (pc.isUnprotectedBlock(state) && pc.protect(player, level, pos)) {
				if (!player.isCreative())
					stack.shrink(1);

				level.playSound(null, pos, SCSounds.LOCK.event, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return ActionResultType.SUCCESS;
			}
		}

		return super.useOn(ctx); //allow key panel to be placed when it did not convert anything
	}
}
