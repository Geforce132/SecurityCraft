package org.freeforums.geforce.securitycraft.items;

import org.freeforums.geforce.securitycraft.api.IOwnable;
import org.freeforums.geforce.securitycraft.blocks.BlockReinforcedDoor;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

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

	/**
	 * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
	 * hands.
	 */
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering()
	{
		return true;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float f1, float f2, float f3)
	{
		TileEntity te = world.getTileEntity(x, y, z);
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
				PlayerUtils.sendMessageToPlayer(player, "This block cannot hold an owner. Please right-clock an appropriate SecurityCraft block.", EnumChatFormatting.RED);
				return false;
			}

			if(!BlockUtils.isOwnerOfBlock((IOwnable)te, player))
			{
				PlayerUtils.sendMessageToPlayer(player, "This is not your block, you cannot change its owner.", EnumChatFormatting.RED);
				return false;
			}

			if(world.getBlock(x, y, z) instanceof BlockReinforcedDoor)
			{
				if(world.getBlock(x, y + 1, z) instanceof BlockReinforcedDoor)
					((IOwnable)world.getTileEntity(x, y + 1, z)).setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
				else
					((IOwnable)world.getTileEntity(x, y - 1, z)).setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
			}

			if(te instanceof IOwnable)
				((IOwnable)te).setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);

			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(te.getDescriptionPacket());
			PlayerUtils.sendMessageToPlayer(player, "Owner successfully changed to " + newOwner, EnumChatFormatting.GREEN);
			return true;
		}

		return false;
	}
}