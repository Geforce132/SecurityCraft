package net.geforcemods.securitycraft.items;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.TEInteractionObject;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWallOrFloor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemSecretSign extends ItemWallOrFloor
{
	public ItemSecretSign()
	{
		super(SCContent.secretSignStanding, SCContent.secretSignWall, new Item.Properties().maxStackSize(16).group(SecurityCraft.groupSCDecoration));
	}

	@Override
	public String getTranslationKey()
	{
		return "item.securitycraft.secret_sign_item";
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		return getTranslationKey();
	}
	//
	@Override
	public boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
	{
		boolean flag = super.onBlockPlaced(pos, world, player, stack, state);

		if(!flag && player != null)
		{
			((TileEntitySecretSign)world.getTileEntity(pos)).setPlayer(player);

			if(!world.isRemote)
				NetworkHooks.openGui((ServerPlayerEntity)player, new TEInteractionObject(GuiHandler.EDIT_SECRET_SIGN, world, pos), pos);
		}

		return flag;
	}
}
