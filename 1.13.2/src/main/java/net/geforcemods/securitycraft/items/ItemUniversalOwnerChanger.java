package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockScannerDoor;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedDoor;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemUniversalOwnerChanger extends Item
{
	public ItemUniversalOwnerChanger()
	{
		super(new Item.Properties().group(SecurityCraft.tabSCTechnical).maxStackSize(1).defaultMaxDamage(48));
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ());
	}

	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileEntity te = world.getTileEntity(pos);
		String newOwner = stack.getDisplayName().getFormattedText();

		if(!world.isRemote)
		{
			if(!(te instanceof IOwnable))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), TextFormatting.RED);
				return EnumActionResult.FAIL;
			}

			Owner owner = ((IOwnable)te).getOwner();
			boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

			if(!owner.isOwner(player) && !isDefault)
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.notOwned"), TextFormatting.RED);
				return EnumActionResult.FAIL;
			}

			if(!stack.hasDisplayName() && !isDefault)
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
				return EnumActionResult.FAIL;
			}

			if(isDefault)
			{
				if(ConfigHandler.allowBlockClaim)
					newOwner = player.getName().getFormattedText();
				else
				{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), TextFormatting.RED);
					return EnumActionResult.FAIL;
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

			world.getServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());

			if(door)
				world.getServer().getPlayerList().sendPacketToAllPlayers(((TileEntityOwnable)world.getTileEntity(updateTop ? pos.up() : pos.down())).getUpdatePacket());

			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.changed").replace("#", newOwner), TextFormatting.GREEN);
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}
}