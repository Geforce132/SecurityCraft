package net.geforcemods.securitycraft.items;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSecretSign extends WallOrFloorItem
{
	private final String translationKey;

	public ItemSecretSign(Block floor, Block wall, String translationKey)
	{
		super(floor, wall, new Item.Properties().maxStackSize(16).group(SecurityCraft.groupSCDecoration));

		this.translationKey = translationKey;
	}

	@Override
	public String getTranslationKey()
	{
		return translationKey;
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		return getTranslationKey();
	}

	@Override
	public boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
	{
		boolean flag = super.onBlockPlaced(pos, world, player, stack, state);

		if(!flag && player != null)
		{
			TileEntitySecretSign te = (TileEntitySecretSign)world.getTileEntity(pos);

			te.setPlayer(player);

			if(world.isRemote)
				SecurityCraft.proxy.displayEditSecretSignGui(te);
		}

		return flag;
	}
}
