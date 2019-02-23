package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.BlockSecretSignStanding;
import net.geforcemods.securitycraft.blocks.BlockSecretSignWall;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemSecretSign extends Item
{
	public ItemSecretSign()
	{
		super(new Item.Properties().maxStackSize(16).group(SecurityCraft.tabSCDecoration));
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx);
	}

	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, ItemStack stack, EnumFacing facing, float hitX, float hitY, float hitZ, ItemUseContext ctx)
	{
		IBlockState state = world.getBlockState(pos);
		boolean isReplaceable = state.getBlock().isReplaceable(world.getBlockState(pos), new BlockItemUseContext(ctx));

		if (facing != EnumFacing.DOWN && (state.getMaterial().isSolid() || isReplaceable) && (!isReplaceable || facing == EnumFacing.UP))
		{
			pos = pos.offset(facing);

			if (player.canPlayerEdit(pos, facing, stack) && SCContent.secretSignStanding.isValidPosition(world.getBlockState(pos), world, pos))
			{
				if (world.isRemote)
				{
					return EnumActionResult.SUCCESS;
				}
				else
				{
					pos = isReplaceable ? pos.down() : pos;

					if (facing == EnumFacing.UP)
					{
						int i = MathHelper.floor((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
						world.setBlockState(pos, SCContent.secretSignStanding.getDefaultState().withProperty(BlockSecretSignStanding.ROTATION, Integer.valueOf(i)), 11);
					}
					else
					{
						world.setBlockState(pos, SCContent.secretSignWall.getDefaultState().withProperty(BlockSecretSignWall.FACING, facing), 11);
					}

					TileEntity tileentity = world.getTileEntity(pos);

					if (tileentity instanceof TileEntitySecretSign && !ItemBlock.setTileEntityNBT(world, player, pos, stack))
					{
						player.openSignEditor((TileEntitySign)tileentity);
					}

					if (player instanceof EntityPlayerMP)
					{
						CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
					}

					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
			}
			else
			{
				return EnumActionResult.FAIL;
			}
		}
		else
		{
			return EnumActionResult.FAIL;
		}
	}
}
