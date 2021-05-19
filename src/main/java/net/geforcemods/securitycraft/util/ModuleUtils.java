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
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.tileentity.SpecialDoorTileEntity;
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

			if(module == ModuleType.ALLOWLIST && keypad.hasModule(ModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.ALLOWLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.KEYPAD.get().getTranslationKey()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				return true;
			}

			if(module == ModuleType.DENYLIST && keypad.hasModule(ModuleType.DENYLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.DENYLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.KEYPAD.get().getTranslationKey()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
		}
		else if(te instanceof KeypadChestTileEntity)
		{
			KeypadChestTileEntity chest = (KeypadChestTileEntity)te;

			if(module == ModuleType.ALLOWLIST && ((IModuleInventory) te).hasModule(ModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.ALLOWLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(chest.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.KEYPAD_CHEST.get().getTranslationKey()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				return true;
			}

			if(module == ModuleType.DENYLIST && ((IModuleInventory) te).hasModule(ModuleType.DENYLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.DENYLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(chest.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.KEYPAD_CHEST.get().getTranslationKey()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
		}
		else if(te instanceof KeypadFurnaceTileEntity)
		{
			KeypadFurnaceTileEntity furnace = (KeypadFurnaceTileEntity)te;

			if(module == ModuleType.ALLOWLIST && ((IModuleInventory) te).hasModule(ModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.ALLOWLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(furnace.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.KEYPAD_FURNACE.get().getTranslationKey()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				return true;
			}

			if(module == ModuleType.DENYLIST && ((IModuleInventory) te).hasModule(ModuleType.DENYLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.DENYLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(furnace.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.KEYPAD_FURNACE.get().getTranslationKey()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
		}else if(te instanceof RetinalScannerTileEntity){
			if(module == ModuleType.ALLOWLIST && ((CustomizableTileEntity) te).hasModule(ModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.ALLOWLIST).contains(player.getName().getFormattedText().toLowerCase()))
				return true;
		}else if(te instanceof InventoryScannerTileEntity){
			if(module == ModuleType.ALLOWLIST && ((CustomizableTileEntity) te).hasModule(ModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.ALLOWLIST).contains(player.getName().getFormattedText().toLowerCase()))
				return true;
		}else if(te instanceof SecretSignTileEntity) {
			if(module == ModuleType.ALLOWLIST && ((SecretSignTileEntity) te).hasModule(ModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.ALLOWLIST).contains(player.getName().getString().toLowerCase()))
				return true;
		}
		else if(te instanceof SpecialDoorTileEntity){
			SpecialDoorTileEntity door = (SpecialDoorTileEntity)te;

			if(module == ModuleType.ALLOWLIST && door.hasModule(ModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.ALLOWLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(door.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(door.getBlockState().getBlock().getTranslationKey()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				return true;
			}

			if(module == ModuleType.DENYLIST && door.hasModule(ModuleType.DENYLIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.DENYLIST).contains(player.getName().getFormattedText().toLowerCase())){
				if(door.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(door.getBlockState().getBlock().getTranslationKey()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
		}

		return false;
	}

	public static void createLinkedAction(LinkedAction action, ItemStack stack, CustomizableTileEntity te)
	{
		if(action == LinkedAction.MODULE_INSERTED)
			te.createLinkedBlockAction(action, new Object[] {stack, (ModuleItem)stack.getItem()}, te);
		else if(action == LinkedAction.MODULE_REMOVED)
			te.createLinkedBlockAction(action, new Object[] {stack, ((ModuleItem)stack.getItem()).getModuleType()}, te);

		if(te instanceof SecurityCameraTileEntity)
			te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getBlockState().get(SecurityCameraBlock.FACING), -1), te.getBlockState().getBlock());
	}
}