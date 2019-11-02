package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedDoor;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUniversalOwnerChanger extends Item
{
	public ItemUniversalOwnerChanger(){}

	/**
	 * Returns True is the item is renderer in full 3D when hold.
	 */
	@Override
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
			if(!(te instanceof IOwnable))
			{
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:universalOwnerChanger.name"), StatCollector.translateToLocal("messages.securitycraft:universalOwnerChanger.cantChange"), EnumChatFormatting.RED);
				return false;
			}

			Owner owner = ((IOwnable)te).getOwner();
			boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

			if(!owner.isOwner(player) && !isDefault)
			{
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:universalOwnerChanger.name"), StatCollector.translateToLocal("messages.securitycraft:universalOwnerChanger.notOwned"), EnumChatFormatting.RED);
				return false;
			}

			if(!stack.hasDisplayName() && !isDefault)
			{
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:universalOwnerChanger.name"), StatCollector.translateToLocal("messages.securitycraft:universalOwnerChanger.noName"), EnumChatFormatting.RED);
				return false;
			}

			if(isDefault)
			{
				if(SecurityCraft.config.allowBlockClaim)
					newOwner = player.getCommandSenderName();
				else
				{
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:universalOwnerChanger.name"), StatCollector.translateToLocal("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), EnumChatFormatting.RED);
					return false;
				}
			}

			boolean door = false;
			boolean updateTop = true;

			if(BlockUtils.getBlock(world, pos) instanceof BlockReinforcedDoor || BlockUtils.getBlock(world, pos) instanceof BlockScannerDoor)
			{
				door = true;
				((IOwnable)world.getTileEntity(pos)).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);

				if(BlockUtils.getBlock(world, pos.up()) instanceof BlockReinforcedDoor || BlockUtils.getBlock(world, pos.up()) instanceof BlockScannerDoor)
					((IOwnable)world.getTileEntity(pos.up())).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
				else if(BlockUtils.getBlock(world, pos.down()) instanceof BlockReinforcedDoor || BlockUtils.getBlock(world, pos.down()) instanceof BlockScannerDoor)
				{
					((IOwnable)world.getTileEntity(pos.down())).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
					updateTop = false;
				}
			}

			if(te instanceof IOwnable)
				((IOwnable)te).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);

			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(te.getDescriptionPacket());

			if(door)
				MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(((TileEntityOwnable)world.getTileEntity(updateTop ? pos.up() : pos.down())).getDescriptionPacket());

			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("item.securitycraft:universalOwnerChanger.name"), StatCollector.translateToLocal("messages.securitycraft:universalOwnerChanger.changed").replace("#", newOwner), EnumChatFormatting.GREEN);
			return true;
		}

		return false;
	}
}