package net.geforcemods.securitycraft.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockReinforcedDoor;
import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
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
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.universalOwnerChanger.name"), StatCollector.translateToLocal("messages.universalOwnerChanger.noName"), EnumChatFormatting.RED);
				return false;
			}

			if(!(te instanceof IOwnable))
			{
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.universalOwnerChanger.name"), StatCollector.translateToLocal("messages.universalOwnerChanger.cantChange"), EnumChatFormatting.RED);
				return false;
			}

			if(!((IOwnable)te).getOwner().isOwner(player))
			{
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.universalOwnerChanger.name"), StatCollector.translateToLocal("messages.universalOwnerChanger.notOwned"), EnumChatFormatting.RED);
				return false;
			}

			boolean door = false;
			boolean updateTop = true;
			
			if(world.getBlock(x, y, z) instanceof BlockReinforcedDoor || world.getBlock(x, y, z) instanceof BlockScannerDoor)
			{
				door = true;
				((IOwnable)world.getTileEntity(x, y, z)).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
				
				if(world.getBlock(x, y + 1, z) instanceof BlockReinforcedDoor || world.getBlock(x, y + 1, z) instanceof BlockScannerDoor)
					((IOwnable)world.getTileEntity(x, y + 1, z)).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
				else if(world.getBlock(x, y - 1, z) instanceof BlockReinforcedDoor || world.getBlock(x, y - 1, z) instanceof BlockScannerDoor)
				{
					((IOwnable)world.getTileEntity(x, y - 1, z)).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
					updateTop = false;
				}
			}
			
			if(te instanceof IOwnable)
				((IOwnable)te).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);

			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(te.getDescriptionPacket());
			
			if(door)
				MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(((TileEntityOwnable)world.getTileEntity(x, updateTop ? y + 1 : y - 1, z)).getDescriptionPacket());
			
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.universalOwnerChanger.name"), StatCollector.translateToLocal("messages.universalOwnerChanger.changed").replace("#", newOwner), EnumChatFormatting.GREEN);
			return true;
		}

		return false;
	}
}