package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ModuleUtils{
	public static List<String> getPlayersFromModule(World world, BlockPos pos, ModuleType module) {
		IModuleInventory te = (IModuleInventory) world.getTileEntity(pos);

		if(te.hasModule(module))
			return getPlayersFromModule(te.getModule(module));
		else return new ArrayList<>();
	}

	public static List<String> getPlayersFromModule(ItemStack stack)
	{
		List<String> list = new ArrayList<>();

		if(stack.getItem() instanceof ModuleItem)
		{
			for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
			{
				if(stack.getTag() != null && stack.getTag().getString("Player" + i) != null && !stack.getTag().getString("Player" + i).isEmpty())
					list.add(stack.getTag().getString("Player" + i).toLowerCase());
			}
		}

		return list;
	}

	public static boolean checkForModule(World world, BlockPos pos, PlayerEntity player, ModuleType module){
		TileEntity te = world.getTileEntity(pos);

		if(!(te instanceof IModuleInventory))
			return false;

		if(te instanceof KeypadTileEntity){
			KeypadTileEntity keypad = (KeypadTileEntity)te;

			if(module == ModuleType.WHITELIST && keypad.hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(player.getName().getString().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				return true;
			}

			if(module == ModuleType.BLACKLIST && keypad.hasModule(ModuleType.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.BLACKLIST).contains(player.getName().getString().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}
		else if(te instanceof KeypadChestTileEntity)
		{
			KeypadChestTileEntity chest = (KeypadChestTileEntity)te;

			if(module == ModuleType.WHITELIST && ((IModuleInventory) te).hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(player.getName().getString().toLowerCase())){
				if(chest.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD_CHEST.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				return true;
			}

			if(module == ModuleType.BLACKLIST && ((IModuleInventory) te).hasModule(ModuleType.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.BLACKLIST).contains(player.getName().getString().toLowerCase())){
				if(chest.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD_CHEST.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}
		else if(te instanceof KeypadFurnaceTileEntity)
		{
			KeypadFurnaceTileEntity furnace = (KeypadFurnaceTileEntity)te;

			if(module == ModuleType.WHITELIST && ((IModuleInventory) te).hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(player.getName().getString().toLowerCase())){
				if(furnace.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD_FURNACE.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				return true;
			}

			if(module == ModuleType.BLACKLIST && ((IModuleInventory) te).hasModule(ModuleType.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.BLACKLIST).contains(player.getName().getString().toLowerCase())){
				if(furnace.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD_FURNACE.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}else if(te instanceof KeycardReaderTileEntity){
			KeycardReaderTileEntity reader = (KeycardReaderTileEntity)te;

			if(module == ModuleType.WHITELIST && reader.hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(player.getName().getString().toLowerCase())){
				if(reader.sendsMessages() && world.isRemote)
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYCARD_READER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
				return true;
			}

			if(module == ModuleType.BLACKLIST && reader.hasModule(ModuleType.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.BLACKLIST).contains(player.getName().getString().toLowerCase())){
				if(reader.sendsMessages() && world.isRemote)
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYCARD_READER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}else if(te instanceof RetinalScannerTileEntity){
			if(module == ModuleType.WHITELIST && ((CustomizableTileEntity) te).hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(player.getName().getString().toLowerCase()))
				return true;
		}else if(te instanceof InventoryScannerTileEntity){
			if(module == ModuleType.WHITELIST && ((CustomizableTileEntity)te).hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(player.getName().getString().toLowerCase()))
				return true;
		}else if(te instanceof SecretSignTileEntity) {
			if(module == ModuleType.WHITELIST && ((SecretSignTileEntity) te).hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(player.getName().getString().toLowerCase()))
				return true;
		}

		return false;
	}

	public static void createLinkedAction(LinkedAction action, ItemStack stack, CustomizableTileEntity te)
	{
		if(action == LinkedAction.MODULE_INSERTED)
			te.createLinkedBlockAction(action, new Object[] {stack, (ModuleItem)stack.getItem()}, te);
		else if(action == LinkedAction.MODULE_REMOVED)
			te.createLinkedBlockAction(action, new Object[] {stack, ((ModuleItem)stack.getItem()).getModule()}, te);

		if(te instanceof SecurityCameraTileEntity)
			te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getBlockState().get(SecurityCameraBlock.FACING), -1), te.getBlockState().getBlock());
	}
}