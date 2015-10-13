package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockReinforcedDoor;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUniversalOwnerChanger extends Item
{
	public ItemUniversalOwnerChanger(){}

	/**
	 * Returns True is the item is renderer in full 3D when hold.
	 */
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileEntity te = world.getTileEntity(pos);
		String newOwner = stack.getDisplayName();

		if(!world.isRemote)
		{
			if(!stack.hasDisplayName())
			{
				PlayerUtils.sendMessageToPlayer(player, "You need to give this item the name of the new block owner. Use an anvil to rename it.", EnumChatFormatting.RED);
				return false;
			}

			if(!(te instanceof IOwnable))
			{
				PlayerUtils.sendMessageToPlayer(player, "This block cannot hold an owner. Please right-click an appropriate SecurityCraft block.", EnumChatFormatting.RED);
				return false;
			}

			if(!BlockUtils.isOwnerOfBlock((IOwnable)te, player))
			{
				PlayerUtils.sendMessageToPlayer(player, "This is not your block, you cannot change its owner.", EnumChatFormatting.RED);
				return false;
			}

			if(BlockUtils.getBlock(world, pos) instanceof BlockReinforcedDoor)
			{
				if(BlockUtils.getBlock(world, pos.up()) instanceof BlockReinforcedDoor)
					((IOwnable)world.getTileEntity(pos.up())).setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
				else
					((IOwnable)world.getTileEntity(pos.up())).setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
			}

			if(te instanceof IOwnable)
				((IOwnable)te).setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);

			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(te.getDescriptionPacket());
			PlayerUtils.sendMessageToPlayer(player, "Owner successfully changed to " + newOwner + ".", EnumChatFormatting.GREEN);
			return true;
		}

		return false;
	}
}