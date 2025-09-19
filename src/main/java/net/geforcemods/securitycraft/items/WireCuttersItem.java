package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WireCuttersItem extends Item {
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World level, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (player.isSneaking()) {
			IBlockState state = level.getBlockState(pos);
			ItemStack stack = player.getHeldItem(hand);

			for (IPasscodeConvertible pc : SecurityCraftAPI.getRegisteredPasscodeConvertibles()) {
				if (pc.isProtectedBlock(state)) {
					TileEntity be = level.getTileEntity(pos);

					if (be instanceof IOwnable && ((IOwnable) be).isOwnedBy(player) && pc.unprotect(player, level, pos)) {
						stack.damageItem(1, player);
						level.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
						Block.spawnAsEntity(level, pos, new ItemStack(SCContent.keyPanel, pc.getRequiredKeyPanels(state, level, pos)));
						return EnumActionResult.SUCCESS;
					}
				}
			}
		}

		return super.onItemUseFirst(player, level, pos, facing, hitX, hitY, hitZ, hand);
	}
}
