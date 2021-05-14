package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.containers.ContainerBlockPocketManager;
import net.geforcemods.securitycraft.containers.ContainerBlockReinforcer;
import net.geforcemods.securitycraft.containers.ContainerBriefcase;
import net.geforcemods.securitycraft.containers.ContainerCustomizeBlock;
import net.geforcemods.securitycraft.containers.ContainerDisguiseModule;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.containers.ContainerInventoryScanner;
import net.geforcemods.securitycraft.containers.ContainerKeypadFurnace;
import net.geforcemods.securitycraft.containers.ContainerProjector;
import net.geforcemods.securitycraft.inventory.BriefcaseInventory;
import net.geforcemods.securitycraft.inventory.ModuleItemInventory;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityProjector;
import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final int SETUP_KEYCARD_READER_ID = 1;
	public static final int MRAT_MENU_ID = 2;
	public static final int SRAT_MENU_ID = 3;
	public static final int INVENTORY_SCANNER_GUI_ID = 6;
	public static final int USERNAME_LOGGER_GUI_ID = 7;
	public static final int KEYPAD_FURNACE_GUI_ID = 8;
	public static final int SETUP_PASSWORD_ID = 9;
	public static final int INSERT_PASSWORD_ID = 10;
	public static final int IMS_GUI_ID = 11;
	public static final int CAMERA_MONITOR_GUI_ID = 12;
	public static final int BRIEFCASE_CODE_SETUP_GUI_ID = 13;
	public static final int BRIEFCASE_INSERT_CODE_GUI_ID = 14;
	public static final int BRIEFCASE_GUI_ID = 15;
	public static final int KEY_CHANGER_GUI_ID = 16;
	public static final int TROPHY_SYSTEM_GUI_ID = 17;
	public static final int CUSTOMIZE_BLOCK = 100;
	public static final int DISGUISE_MODULE = 102;
	public static final int BLOCK_REINFORCER = 103;
	public static final int MODULES = 104;
	public static final int BLOCK_POCKET_MANAGER = 105;
	public static final int PROJECTOR = 106;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(BlockUtils.toPos(x, y, z));

		switch(id)
		{
			case SETUP_KEYCARD_READER_ID:
				return new ContainerGeneric(player.inventory, te);
			case MRAT_MENU_ID:
				return new ContainerGeneric(player.inventory, te);
			case SRAT_MENU_ID:
				return new ContainerGeneric(player.inventory, te);
			case INVENTORY_SCANNER_GUI_ID:
				return new ContainerInventoryScanner(player.inventory, (TileEntityInventoryScanner) te);
			case USERNAME_LOGGER_GUI_ID:
				return new ContainerGeneric(player.inventory, te);
			case KEYPAD_FURNACE_GUI_ID:
				return new ContainerKeypadFurnace(player.inventory, (TileEntityKeypadFurnace) te);
			case SETUP_PASSWORD_ID:
				return new ContainerGeneric(player.inventory, te);
			case INSERT_PASSWORD_ID:
				return new ContainerGeneric(player.inventory, te);
			case IMS_GUI_ID:
				return new ContainerGeneric(player.inventory, te);
			case CAMERA_MONITOR_GUI_ID:
				if(!PlayerUtils.isHoldingItem(player, SCContent.cameraMonitor, null))
					return null;
				return new ContainerGeneric(player.inventory, te);
			case BRIEFCASE_CODE_SETUP_GUI_ID:
			case BRIEFCASE_INSERT_CODE_GUI_ID:
				if(!PlayerUtils.isHoldingItem(player, SCContent.briefcase, null))
					return null;
				return null;
			case BRIEFCASE_GUI_ID:
				if(!PlayerUtils.isHoldingItem(player, SCContent.briefcase, null))
					return null;
				return new ContainerBriefcase(player.inventory, new BriefcaseInventory(PlayerUtils.getSelectedItemStack(player, SCContent.briefcase)));
			case KEY_CHANGER_GUI_ID:
				if(te == null || !PlayerUtils.isHoldingItem(player, SCContent.universalKeyChanger, null))
					return null;
				return new ContainerGeneric(player.inventory, te);
			case TROPHY_SYSTEM_GUI_ID:
				return new ContainerGeneric(player.inventory, te);
			case CUSTOMIZE_BLOCK:
				return new ContainerCustomizeBlock(player.inventory, (IModuleInventory) te);
			case DISGUISE_MODULE:
				ItemStack module = player.inventory.getCurrentItem().getItem() instanceof ItemModule ? player.inventory.getCurrentItem() : player.inventory.offHandInventory.get(0);
				if(!((ItemModule)module.getItem()).canBeCustomized())
					return null;
				return new ContainerDisguiseModule(player.inventory, new ModuleItemInventory(module));
			case BLOCK_REINFORCER:
				return new ContainerBlockReinforcer(player, player.inventory, player.getHeldItemMainhand().getItem() == SCContent.universalBlockReinforcerLvL1);
			case MODULES:
				return new ContainerGeneric(player.inventory, te);
			case BLOCK_POCKET_MANAGER:
				if(te instanceof TileEntityBlockPocketManager)
					return new ContainerBlockPocketManager(player.inventory, (TileEntityBlockPocketManager)te);
				return null;
			case PROJECTOR:
				if(te instanceof TileEntityProjector)
					return new ContainerProjector(player.inventory, (TileEntityProjector)te);
				return null;
			default:
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(BlockUtils.toPos(x, y, z));

		switch(id)
		{
			case SETUP_KEYCARD_READER_ID:
				return new GuiKeycardSetup(player.inventory, (TileEntityKeycardReader) te);
			case MRAT_MENU_ID:
				if(PlayerUtils.isHoldingItem(player, SCContent.remoteAccessMine, null))
					return new GuiMRAT(player.inventory, PlayerUtils.getSelectedItemStack(player, SCContent.remoteAccessMine));
				else return null;
			case SRAT_MENU_ID:
				if(PlayerUtils.isHoldingItem(player, SCContent.remoteAccessSentry, null))
					return new GuiSRAT(player.inventory, PlayerUtils.getSelectedItemStack(player, SCContent.remoteAccessSentry), x);
				else return null;
			case INVENTORY_SCANNER_GUI_ID:
				return new GuiInventoryScanner(player.inventory, (TileEntityInventoryScanner) te, player);
			case USERNAME_LOGGER_GUI_ID:
				return new GuiLogger(player.inventory, (TileEntityLogger) te);
			case KEYPAD_FURNACE_GUI_ID:
				return new GuiKeypadFurnaceInventory(player.inventory, (TileEntityKeypadFurnace) te);
			case SETUP_PASSWORD_ID:
				return new GuiSetPassword(player.inventory, te, BlockUtils.getBlock(world, x, y, z));
			case INSERT_PASSWORD_ID:
				return new GuiCheckPassword(player.inventory, te, BlockUtils.getBlock(world, x, y, z));
			case IMS_GUI_ID:
				return new GuiIMS(player.inventory, (TileEntityIMS) te);
			case CAMERA_MONITOR_GUI_ID:
				if(!PlayerUtils.isHoldingItem(player, SCContent.cameraMonitor, null))
					return null;
				return new GuiCameraMonitor(player.inventory, (ItemCameraMonitor) PlayerUtils.getSelectedItemStack(player.inventory, SCContent.cameraMonitor).getItem(),  PlayerUtils.getSelectedItemStack(player.inventory, SCContent.cameraMonitor).getTagCompound());
			case BRIEFCASE_CODE_SETUP_GUI_ID:
				if(!PlayerUtils.isHoldingItem(player, SCContent.briefcase, null))
					return null;
				return new GuiBriefcaseSetup(player.inventory, null);
			case BRIEFCASE_INSERT_CODE_GUI_ID:
				if(!PlayerUtils.isHoldingItem(player, SCContent.briefcase, null))
					return null;
				return new GuiBriefcase(player.inventory, null);
			case BRIEFCASE_GUI_ID:
				if(!PlayerUtils.isHoldingItem(player, SCContent.briefcase, null))
					return null;
				return new GuiBriefcaseInventory(player.inventory, PlayerUtils.getSelectedItemStack(player, SCContent.briefcase));
			case KEY_CHANGER_GUI_ID:
				if(te == null || !PlayerUtils.isHoldingItem(player, SCContent.universalKeyChanger, null))
					return null;
				return new GuiKeyChanger(player.inventory, te);
			case TROPHY_SYSTEM_GUI_ID:
				return new GuiTrophySystem(player.inventory, (TileEntityTrophySystem) te);
			case CUSTOMIZE_BLOCK:
				return new GuiCustomizeBlock(player.inventory, (IModuleInventory) te);
			case DISGUISE_MODULE:
				ItemStack module = player.inventory.getCurrentItem().getItem() instanceof ItemModule ? player.inventory.getCurrentItem() : player.inventory.offHandInventory.get(0);
				if(!((ItemModule)module.getItem()).canBeCustomized())
					return null;
				return new GuiDisguiseModule(player.inventory);
			case BLOCK_REINFORCER:
				boolean isLvl1 = player.getHeldItemMainhand().getItem() == SCContent.universalBlockReinforcerLvL1;
				return new GuiBlockReinforcer(new ContainerBlockReinforcer(player, player.inventory, isLvl1), isLvl1);
			case MODULES:
				if(PlayerUtils.isHoldingItem(player, SCContent.whitelistModule, null) || PlayerUtils.isHoldingItem(player, SCContent.blacklistModule, null))
					return new GuiEditModule(player.inventory, player.getHeldItemMainhand(), te);
				return null;
			case BLOCK_POCKET_MANAGER:
				if(te instanceof TileEntityBlockPocketManager)
					return new GuiBlockPocketManager(player.inventory, (TileEntityBlockPocketManager)te);
				return null;
			case PROJECTOR:
				if(te instanceof TileEntityProjector)
					return new GuiProjector(player.inventory, (TileEntityProjector)te);
				return null;
			default:
				return null;
		}
	}
}
