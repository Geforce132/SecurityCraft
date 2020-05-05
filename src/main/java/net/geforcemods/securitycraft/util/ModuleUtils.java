package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ModuleUtils{
	public static List<String> getPlayersFromModule(World world, BlockPos pos, CustomModules module) {
		CustomizableTileEntity te = (CustomizableTileEntity) world.getTileEntity(pos);

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

	public static boolean checkForModule(World world, BlockPos pos, PlayerEntity player, CustomModules module){
		TileEntity te = world.getTileEntity(pos);

		if(!(te instanceof CustomizableTileEntity))
			return false;

		if(te instanceof KeypadTileEntity){
			KeypadTileEntity keypad = (KeypadTileEntity)te;

			if(module == CustomModules.WHITELIST && keypad.hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				return true;
			}

			if(module == CustomModules.BLACKLIST && keypad.hasModule(CustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.BLACKLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}else if(te instanceof KeycardReaderTileEntity){
			KeycardReaderTileEntity reader = (KeycardReaderTileEntity)te;

			if(module == CustomModules.WHITELIST && reader.hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(reader.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYCARD_READER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.whitelisted"), TextFormatting.GREEN);

				world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
				return true;
			}

			if(module == CustomModules.BLACKLIST && reader.hasModule(CustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.BLACKLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(reader.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYCARD_READER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:module.blacklisted"), TextFormatting.RED);

				return true;
			}
		}else if(te instanceof RetinalScannerTileEntity){
			if(module == CustomModules.WHITELIST && ((CustomizableTileEntity) te).hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(player.getName().getFormattedText().toLowerCase()))
				return true;
		}else if(te instanceof InventoryScannerTileEntity)
			if(module == CustomModules.WHITELIST && ((CustomizableTileEntity) te).hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(player.getName().getFormattedText().toLowerCase()))
				return true;

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