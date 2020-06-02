package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ModuleUtils{
	public static List<String> getPlayersFromModule(World world, BlockPos pos, EnumModuleType module) {
		IModuleInventory te = (IModuleInventory) world.getTileEntity(pos);

		if(te.hasModule(module))
			return getPlayersFromModule(te.getModule(module));
		else return new ArrayList<>();
	}

	public static List<String> getPlayersFromModule(ItemStack stack)
	{
		List<String> list = new ArrayList<>();

		if(stack.getItem() instanceof ItemModule)
		{
			for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
			{
				if(stack.getTagCompound() != null && stack.getTagCompound().getString("Player" + i) != null && !stack.getTagCompound().getString("Player" + i).isEmpty())
					list.add(stack.getTagCompound().getString("Player" + i).toLowerCase());
			}
		}

		return list;
	}

	public static boolean checkForModule(World world, BlockPos pos, EntityPlayer player, EnumModuleType module){
		TileEntity te = world.getTileEntity(pos);

		if(!(te instanceof IModuleInventory))
			return false;

		if(te instanceof TileEntityKeypad){
			TileEntityKeypad keypad = (TileEntityKeypad)te;

			if(module == EnumModuleType.WHITELIST && keypad.hasModule(EnumModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(player.getName().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keypad.getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				return true;
			}

			if(module == EnumModuleType.BLACKLIST && keypad.hasModule(EnumModuleType.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.BLACKLIST).contains(player.getName().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keypad.getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}
		else if(te instanceof TileEntityKeypadChest)
		{
			TileEntityKeypadChest chest = (TileEntityKeypadChest)te;

			if(module == EnumModuleType.WHITELIST && ((IModuleInventory) te).hasModule(EnumModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(player.getName().toLowerCase())){
				if(chest.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keypadChest.getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				return true;
			}

			if(module == EnumModuleType.BLACKLIST && ((IModuleInventory) te).hasModule(EnumModuleType.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.BLACKLIST).contains(player.getName().toLowerCase())){
				if(chest.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keypadChest.getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}
		else if(te instanceof TileEntityKeypadFurnace)
		{
			TileEntityKeypadFurnace furnace = (TileEntityKeypadFurnace)te;

			if(module == EnumModuleType.WHITELIST && ((IModuleInventory) te).hasModule(EnumModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(player.getName().toLowerCase())){
				if(furnace.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keypadFurnace.getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				return true;
			}

			if(module == EnumModuleType.BLACKLIST && ((IModuleInventory) te).hasModule(EnumModuleType.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.BLACKLIST).contains(player.getName().toLowerCase())){
				if(furnace.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keypadFurnace.getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}else if(te instanceof TileEntityKeycardReader){
			TileEntityKeycardReader reader = (TileEntityKeycardReader)te;

			if(module == EnumModuleType.WHITELIST && reader.hasModule(EnumModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(player.getName().toLowerCase())){
				if(reader.sendsMessages() && world.isRemote)
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keycardReader.getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
				return true;
			}

			if(module == EnumModuleType.BLACKLIST && reader.hasModule(EnumModuleType.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.BLACKLIST).contains(player.getName().toLowerCase())){
				if(reader.sendsMessages() && world.isRemote)
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keycardReader.getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}else if(te instanceof TileEntityRetinalScanner){
			if(module == EnumModuleType.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(player.getName().toLowerCase()))
				return true;
		}else if(te instanceof TileEntityInventoryScanner)
			if(module == EnumModuleType.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(player.getName().toLowerCase()))
				return true;

		return false;
	}

	public static void createLinkedAction(EnumLinkedAction action, ItemStack stack, CustomizableSCTE te)
	{
		if(action == EnumLinkedAction.MODULE_INSERTED)
			te.createLinkedBlockAction(action, new Object[] {stack, (ItemModule)stack.getItem()}, te);
		else if(action == EnumLinkedAction.MODULE_REMOVED)
			te.createLinkedBlockAction(action, new Object[] {stack, ((ItemModule)stack.getItem()).getModule()}, te);

		if(te instanceof TileEntitySecurityCamera)
			te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getWorld().getBlockState(te.getPos()).getValue(BlockSecurityCamera.FACING), -1), te.getBlockType(), false);
	}
}