package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeyPanelItem extends FloorCeilingWallBlockItem {
	public KeyPanelItem() {
		super(SCContent.keyPanelFloorCeilingBlock, SCContent.keyPanelWallBlock);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		IBlockState state = world.getBlockState(pos);
		TileEntity be = world.getTileEntity(pos);

		if (!(be instanceof IOwnable) || ((IOwnable) be).isOwnedBy(player)) {
			for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
				if (pc.isUnprotectedBlock(state) && pc.protect(player, world, pos)) {
					if (!player.capabilities.isCreativeMode)
						stack.shrink(1);

					world.playSound(player, pos, SCSounds.LOCK.event, SoundCategory.BLOCKS, 1.0F, 1.0F);
					return EnumActionResult.SUCCESS;
				}
			}
		}

		return super.onItemUseFirst(player, world, pos, facing, hitX, hitY, hitZ, hand);
	}
}
