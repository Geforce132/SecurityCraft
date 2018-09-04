package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockSecretSignStanding;
import net.geforcemods.securitycraft.blocks.BlockSecretSignWall;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSecretSign extends Item
{
	public ItemSecretSign()
	{
		maxStackSize = 16;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (side == EnumFacing.DOWN)
		{
			return false;
		}
		else if (!world.getBlockState(pos).getBlock().getMaterial().isSolid())
		{
			return false;
		}
		else
		{
			pos = pos.offset(side);

			if (!player.canPlayerEdit(pos, side, stack))
			{
				return false;
			}
			else if (!SCContent.secretSignStanding.canPlaceBlockAt(world, pos))
			{
				return false;
			}
			else if (world.isRemote)
			{
				return true;
			}
			else
			{
				if (side == EnumFacing.UP)
				{
					int rotation = MathHelper.floor_double((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
					world.setBlockState(pos, SCContent.secretSignStanding.getDefaultState().withProperty(BlockSecretSignStanding.ROTATION, Integer.valueOf(rotation)), 3);
				}
				else
				{
					world.setBlockState(pos, SCContent.secretSignWall.getDefaultState().withProperty(BlockSecretSignWall.FACING, side), 3);
				}

				--stack.stackSize;
				TileEntity tileentity = world.getTileEntity(pos);

				if (tileentity instanceof TileEntitySecretSign && !ItemBlock.setTileEntityNBT(world, player, pos, stack))
				{
					player.openEditSign((TileEntitySecretSign)tileentity);
				}

				return true;
			}
		}
	}
}