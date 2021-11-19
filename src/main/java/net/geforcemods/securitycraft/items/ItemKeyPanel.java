package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.PlaySoundAtPos;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKeyPanel extends Item{
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = player.getHeldItem(hand);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		for(IPasswordConvertible pc : SecurityCraftAPI.getRegisteredPasswordConvertibles()) {
			if(block == pc.getOriginalBlock())
			{
				if(pc.convert(player, world, pos))
				{
					if(!player.capabilities.isCreativeMode)
						stack.shrink(1);

					if (!world.isRemote)
						SecurityCraft.network.sendToAll(new PlaySoundAtPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SCSounds.LOCK.location.toString(), 1.0F, "block"));

					return EnumActionResult.SUCCESS;
				}
			}
		}

		//respect replaceable blocks when trying placing the key panel
		if(state.getBlock().isReplaceable(world, pos))
		{
			pos = pos.offset(facing.getOpposite());
			state = world.getBlockState(pos);
		}

		if(state.isSideSolid(world, pos, facing))
		{
			IBlockState stateToPlace;
			BlockPos placeAt = pos.offset(facing);
			IBlockState stateAtPlacePosition = world.getBlockState(placeAt);

			if(player.canPlayerEdit(placeAt, facing, stack) && stateAtPlacePosition.getBlock().isReplaceable(world, placeAt))
			{
				if(facing.getAxis() == Axis.Y)
					stateToPlace = SCContent.keyPanelFloorCeilingBlock.getStateForPlacement(world, placeAt, facing, hitX, hitY, hitZ, 0, player, hand);
				else
					stateToPlace = SCContent.keyPanelWallBlock.getStateForPlacement(world, placeAt, facing, hitX, hitY, hitZ, 0, player, hand);

				if(stateToPlace != null)
				{
					SoundType soundType = stateToPlace.getBlock().getSoundType(stateToPlace, world, placeAt, player);

					world.setBlockState(placeAt, stateToPlace);
					world.playSound(player, placeAt, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
					ItemBlock.setTileEntityNBT(world, player, placeAt, stack);

					if(player instanceof EntityPlayerMP)
						CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, placeAt, stack);

					if(!player.isCreative())
						stack.shrink(1);

					stateToPlace.getBlock().onBlockPlacedBy(world, placeAt, stateToPlace, player, stack);
					return EnumActionResult.SUCCESS;
				}
			}
		}

		return EnumActionResult.FAIL;
	}
}
