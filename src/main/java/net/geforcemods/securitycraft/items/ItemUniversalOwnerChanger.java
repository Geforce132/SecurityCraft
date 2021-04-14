package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.blocks.BlockSpecialDoor;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedDoor;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityDisguisable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUniversalOwnerChanger extends Item
{
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
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		Block block = world.getBlockState(pos).getBlock();
		ItemStack stack = player.getHeldItem(hand);
		TileEntity te = world.getTileEntity(pos);
		String newOwner = stack.getDisplayName();

		if(!(te instanceof IOwnable))
		{
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), TextFormatting.RED);
			return EnumActionResult.SUCCESS;
		}

		Owner owner = ((IOwnable)te).getOwner();
		boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

		if(!owner.isOwner(player) && !isDefault)
		{
			if(!(block instanceof IBlockMine) && (!(te instanceof TileEntityDisguisable) || (((ItemBlock)((BlockDisguisable)((TileEntityDisguisable)te).getBlockType()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof BlockDisguisable))) {
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.notOwned"), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}

			return EnumActionResult.PASS;
		}

		if(!stack.hasDisplayName() && !isDefault)
		{
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
			return EnumActionResult.SUCCESS;
		}

		if(isDefault)
		{
			if(ConfigHandler.allowBlockClaim)
				newOwner = player.getName();
			else
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}
		}

		boolean door = false;
		boolean updateTop = true;

		if(BlockUtils.getBlock(world, pos) instanceof BlockReinforcedDoor || BlockUtils.getBlock(world, pos) instanceof BlockSpecialDoor)
		{
			door = true;
			((IOwnable)world.getTileEntity(pos)).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);

			if(BlockUtils.getBlock(world, pos.up()) instanceof BlockReinforcedDoor || BlockUtils.getBlock(world, pos.up()) instanceof BlockSpecialDoor)
				((IOwnable)world.getTileEntity(pos.up())).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
			else if(BlockUtils.getBlock(world, pos.down()) instanceof BlockReinforcedDoor || BlockUtils.getBlock(world, pos.down()) instanceof BlockSpecialDoor)
			{
				((IOwnable)world.getTileEntity(pos.down())).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
				updateTop = false;
			}
		}

		if(te instanceof IOwnable)
			((IOwnable)te).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);

		if (!world.isRemote) {
			world.getMinecraftServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());

			if(door)
				world.getMinecraftServer().getPlayerList().sendPacketToAllPlayers(((TileEntityOwnable)world.getTileEntity(updateTop ? pos.up() : pos.down())).getUpdatePacket());
		}

		if(!world.isRemote && te instanceof IModuleInventory)
		{
			for(EnumModuleType moduleType : ((IModuleInventory)te).getInsertedModules())
			{
				ItemStack moduleStack = ((IModuleInventory)te).getModule(moduleType);

				((IModuleInventory)te).removeModule(moduleType);
				((IModuleInventory)te).onModuleRemoved(moduleStack, moduleType);
				Block.spawnAsEntity(world, pos, moduleStack);
			}
		}

		PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalOwnerChanger.name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack ownerChanger = player.getHeldItem(hand);

		if (!ownerChanger.hasDisplayName()) {
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.universalOwnerChanger.getTranslationKey() + ".name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
			return ActionResult.newResult(EnumActionResult.SUCCESS, ownerChanger);
		}

		if (hand == EnumHand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.briefcase) {
			ItemStack briefcase = player.getHeldItemOffhand();

			if (ItemBriefcase.isOwnedBy(briefcase, player)) {
				String newOwner = ownerChanger.getDisplayName();

				if (!briefcase.hasTagCompound())
					briefcase.setTagCompound(new NBTTagCompound());

				briefcase.getTagCompound().setString("owner", newOwner);
				briefcase.getTagCompound().setString("ownerUUID", PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID");
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.universalOwnerChanger.getTranslationKey() + ".name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
				return ActionResult.newResult(EnumActionResult.SUCCESS, ownerChanger);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.universalOwnerChanger.getTranslationKey() + ".name"), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.briefcase.notOwned"), TextFormatting.RED);

			return ActionResult.newResult(EnumActionResult.SUCCESS, ownerChanger);
		}

		return ActionResult.newResult(EnumActionResult.PASS, ownerChanger);
	}
}