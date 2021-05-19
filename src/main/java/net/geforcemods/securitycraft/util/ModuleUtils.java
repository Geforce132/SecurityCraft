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
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntitySpecialDoor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
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

			if(module == EnumModuleType.ALLOWLIST && keypad.hasModule(EnumModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.ALLOWLIST).contains(player.getName().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, translate(SCContent.keypad), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				return true;
			}

			if(module == EnumModuleType.DENYLIST && keypad.hasModule(EnumModuleType.DENYLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.DENYLIST).contains(player.getName().toLowerCase())){
				if(keypad.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, translate(SCContent.keypad), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
		}
		else if(te instanceof TileEntityKeypadChest)
		{
			TileEntityKeypadChest chest = (TileEntityKeypadChest)te;

			if(module == EnumModuleType.ALLOWLIST && ((IModuleInventory) te).hasModule(EnumModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.ALLOWLIST).contains(player.getName().toLowerCase())){
				if(chest.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, translate(SCContent.keypadChest), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				return true;
			}

			if(module == EnumModuleType.DENYLIST && ((IModuleInventory) te).hasModule(EnumModuleType.DENYLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.DENYLIST).contains(player.getName().toLowerCase())){
				if(chest.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, translate(SCContent.keypadChest), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
		}
		else if(te instanceof TileEntityKeypadFurnace)
		{
			TileEntityKeypadFurnace furnace = (TileEntityKeypadFurnace)te;

			if(module == EnumModuleType.ALLOWLIST && ((IModuleInventory) te).hasModule(EnumModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.ALLOWLIST).contains(player.getName().toLowerCase())){
				if(furnace.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, translate(SCContent.keypadFurnace), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				return true;
			}

			if(module == EnumModuleType.DENYLIST && ((IModuleInventory) te).hasModule(EnumModuleType.DENYLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.DENYLIST).contains(player.getName().toLowerCase())){
				if(furnace.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, translate(SCContent.keypadFurnace), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
		}else if(te instanceof TileEntityKeycardReader){
			TileEntityKeycardReader reader = (TileEntityKeycardReader)te;

			if(module == EnumModuleType.ALLOWLIST && reader.hasModule(EnumModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.ALLOWLIST).contains(player.getName().toLowerCase())){
				if(reader.sendsMessages() && world.isRemote)
					PlayerUtils.sendMessageToPlayer(player, translate(SCContent.keycardReader), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
				return true;
			}

			if(module == EnumModuleType.DENYLIST && reader.hasModule(EnumModuleType.DENYLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.DENYLIST).contains(player.getName().toLowerCase())){
				if(reader.sendsMessages() && world.isRemote)
					PlayerUtils.sendMessageToPlayer(player, translate(SCContent.keycardReader), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
		}else if(te instanceof TileEntityRetinalScanner){
			if(module == EnumModuleType.ALLOWLIST && ((CustomizableSCTE) te).hasModule(EnumModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.ALLOWLIST).contains(player.getName().toLowerCase()))
				return true;
		}else if(te instanceof TileEntityInventoryScanner){
			if(module == EnumModuleType.ALLOWLIST && ((CustomizableSCTE) te).hasModule(EnumModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.ALLOWLIST).contains(player.getName().toLowerCase()))
				return true;
		}else if(te instanceof TileEntitySecretSign) {
			if(module == EnumModuleType.ALLOWLIST && ((TileEntitySecretSign) te).hasModule(EnumModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.ALLOWLIST).contains(player.getName().toLowerCase()))
				return true;
		}
		else if(te instanceof TileEntitySpecialDoor){
			TileEntitySpecialDoor door = (TileEntitySpecialDoor)te;

			if(module == EnumModuleType.ALLOWLIST && door.hasModule(EnumModuleType.ALLOWLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.ALLOWLIST).contains(player.getName().toLowerCase())){
				if(door.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, translate(door.getBlockType()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				return true;
			}

			if(module == EnumModuleType.DENYLIST && door.hasModule(EnumModuleType.DENYLIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.DENYLIST).contains(player.getName().toLowerCase())){
				if(door.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, translate(door.getBlockType()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
		}
		return false;
	}

	public static void createLinkedAction(EnumLinkedAction action, ItemStack stack, CustomizableSCTE te)
	{
		if(action == EnumLinkedAction.MODULE_INSERTED)
			te.createLinkedBlockAction(action, new Object[] {stack, (ItemModule)stack.getItem()}, te);
		else if(action == EnumLinkedAction.MODULE_REMOVED)
			te.createLinkedBlockAction(action, new Object[] {stack, ((ItemModule)stack.getItem()).getModuleType()}, te);

		if(te instanceof TileEntitySecurityCamera)
			te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getWorld().getBlockState(te.getPos()).getValue(BlockSecurityCamera.FACING), -1), te.getBlockType(), false);
	}

	private static TextComponentTranslation translate(Block block)
	{
		return Utils.localize(block.getTranslationKey() + ".name");
	}
}