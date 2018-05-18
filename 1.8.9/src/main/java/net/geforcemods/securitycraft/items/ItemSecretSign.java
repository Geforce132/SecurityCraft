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
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (side == EnumFacing.DOWN)
		{
			return false;
		}
		else if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid())
		{
			return false;
		}
		else
		{
			pos = pos.offset(side);

			if (!playerIn.canPlayerEdit(pos, side, stack))
			{
				return false;
			}
			else if (!SCContent.secretSignStanding.canPlaceBlockAt(worldIn, pos))
			{
				return false;
			}
			else if (worldIn.isRemote)
			{
				return true;
			}
			else
			{
				if (side == EnumFacing.UP)
				{
					int i = MathHelper.floor_double((playerIn.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
					worldIn.setBlockState(pos, SCContent.secretSignStanding.getDefaultState().withProperty(BlockSecretSignStanding.ROTATION, Integer.valueOf(i)), 3);
				}
				else
				{
					worldIn.setBlockState(pos, SCContent.secretSignWall.getDefaultState().withProperty(BlockSecretSignWall.FACING, side), 3);
				}

				--stack.stackSize;
				TileEntity tileentity = worldIn.getTileEntity(pos);

				if (tileentity instanceof TileEntitySecretSign && !ItemBlock.setTileEntityNBT(worldIn, playerIn, pos, stack))
				{
					playerIn.openEditSign((TileEntitySecretSign)tileentity);
				}

				return true;
			}
		}
	}
}