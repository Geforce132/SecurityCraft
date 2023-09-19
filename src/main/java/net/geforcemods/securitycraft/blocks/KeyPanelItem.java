package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.items.BasePanelItem;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeyPanelItem extends BasePanelItem {
	public KeyPanelItem() {
		super(SCContent.keyPanelFloorCeilingBlock, SCContent.keyPanelWallBlock);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		IBlockState state = world.getBlockState(pos);

		for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
			if (pc.isValidStateForConversion(state) && pc.convert(player, world, pos)) {
				if (!player.capabilities.isCreativeMode)
					stack.shrink(1);

				world.playSound(player, pos, SCSounds.LOCK.event, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return EnumActionResult.SUCCESS;
			}
		}

		return super.onItemUseFirst(player, world, pos, facing, hitX, hitY, hitZ, hand);
	}
}
