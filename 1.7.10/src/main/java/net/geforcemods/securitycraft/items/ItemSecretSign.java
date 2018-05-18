package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSecretSign extends Item
{
	public ItemSecretSign()
	{
		maxStackSize = 16;
	}

	@Override
	public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
	{
		if (p_77648_7_ == 0)
		{
			return false;
		}
		else if (!p_77648_3_.getBlock(p_77648_4_, p_77648_5_, p_77648_6_).getMaterial().isSolid())
		{
			return false;
		}
		else
		{
			if (p_77648_7_ == 1)
			{
				++p_77648_5_;
			}

			if (p_77648_7_ == 2)
			{
				--p_77648_6_;
			}

			if (p_77648_7_ == 3)
			{
				++p_77648_6_;
			}

			if (p_77648_7_ == 4)
			{
				--p_77648_4_;
			}

			if (p_77648_7_ == 5)
			{
				++p_77648_4_;
			}

			if (!p_77648_2_.canPlayerEdit(p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_1_))
			{
				return false;
			}
			else if (!SCContent.secretSignStanding.canPlaceBlockAt(p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_))
			{
				return false;
			}
			else if (p_77648_3_.isRemote)
			{
				return true;
			}
			else
			{
				if (p_77648_7_ == 1)
				{
					int i1 = MathHelper.floor_double((p_77648_2_.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
					p_77648_3_.setBlock(p_77648_4_, p_77648_5_, p_77648_6_, SCContent.secretSignStanding, i1, 3);
				}
				else
				{
					p_77648_3_.setBlock(p_77648_4_, p_77648_5_, p_77648_6_, SCContent.secretSignWall, p_77648_7_, 3);
				}

				--p_77648_1_.stackSize;
				TileEntitySecretSign tileentitysign = (TileEntitySecretSign)p_77648_3_.getTileEntity(p_77648_4_, p_77648_5_, p_77648_6_);

				if (tileentitysign != null)
				{
					p_77648_2_.displayGUIEditSign(tileentitysign);
				}

				return true;
			}
		}
	}
}