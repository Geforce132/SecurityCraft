package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.inventory.BlockChangeDetectorMenu;
import net.geforcemods.securitycraft.inventory.BlockPocketManagerMenu;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ClaymoreMenu;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.inventory.KeycardHolderMenu;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.geforcemods.securitycraft.inventory.LaserBlockMenu;
import net.geforcemods.securitycraft.inventory.ModuleItemContainer;
import net.geforcemods.securitycraft.inventory.ProjectorMenu;
import net.geforcemods.securitycraft.inventory.TrophySystemMenu;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ScreenHandler implements IGuiHandler {
	public static final int KEYCARD_READER_ID = 1;
	public static final int MRAT_MENU_ID = 2;
	public static final int SRAT_MENU_ID = 3;
	public static final int INVENTORY_SCANNER_GUI_ID = 6;
	public static final int USERNAME_LOGGER_GUI_ID = 7;
	public static final int KEYPAD_FURNACE_GUI_ID = 8;
	public static final int SETUP_PASSCODE_ID = 9;
	public static final int INSERT_PASSCODE_ID = 10;
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
	public static final int SONIC_SECURITY_SYSTEM = 107;
	public static final int BLOCK_CHANGE_DETECTOR = 108;
	public static final int SSS_ITEM = 109;
	public static final int RIFT_STABILIZER = 110;
	public static final int ALARM = 111;
	public static final int KEYCARD_HOLDER = 112;
	public static final int LASER_BLOCK = 113;
	public static final int CLAYMORE = 114;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

		switch (id) {
			case KEYCARD_READER_ID:
				return new KeycardReaderMenu(player.inventory, (KeycardReaderBlockEntity) te);
			case MRAT_MENU_ID:
				return new GenericMenu(te);
			case SRAT_MENU_ID:
				return new GenericMenu(te);
			case INVENTORY_SCANNER_GUI_ID:
				return new InventoryScannerMenu(player.inventory, (InventoryScannerBlockEntity) te);
			case USERNAME_LOGGER_GUI_ID:
				return new GenericMenu(te);
			case KEYPAD_FURNACE_GUI_ID:
				return new KeypadFurnaceMenu(player.inventory, (KeypadFurnaceBlockEntity) te);
			case SETUP_PASSCODE_ID:
				return new GenericMenu(te);
			case INSERT_PASSCODE_ID:
				return new GenericMenu(te);
			case IMS_GUI_ID:
				return new GenericMenu(te);
			case CAMERA_MONITOR_GUI_ID:
				if (PlayerUtils.getItemStackFromAnyHand(player, SCContent.cameraMonitor).isEmpty())
					return null;
				return new GenericMenu(te);
			case BRIEFCASE_CODE_SETUP_GUI_ID:
			case BRIEFCASE_INSERT_CODE_GUI_ID:
				if (PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase).isEmpty())
					return null;
				return new GenericMenu(te);
			case BRIEFCASE_GUI_ID:
				if (PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase).isEmpty())
					return null;
				return new BriefcaseMenu(player.inventory, ItemContainer.briefcase(PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase)));
			case KEY_CHANGER_GUI_ID:
				if (te == null || PlayerUtils.getItemStackFromAnyHand(player, SCContent.universalKeyChanger).isEmpty())
					return null;
				return new GenericMenu(te);
			case TROPHY_SYSTEM_GUI_ID:
				return new TrophySystemMenu((TrophySystemBlockEntity) te, player.inventory);
			case CUSTOMIZE_BLOCK:
				return new CustomizeBlockMenu(player.inventory, (IModuleInventory) te);
			case DISGUISE_MODULE:
				ItemStack module = player.inventory.getCurrentItem().getItem() instanceof ModuleItem ? player.inventory.getCurrentItem() : player.inventory.offHandInventory.get(0);
				if (!((ModuleItem) module.getItem()).canBeCustomized())
					return null;
				return new DisguiseModuleMenu(player.inventory, new ModuleItemContainer(module));
			case BLOCK_REINFORCER:
				return new BlockReinforcerMenu(player, player.inventory, player.getHeldItemMainhand().getItem() == SCContent.universalBlockReinforcerLvL1);
			case MODULES:
				return new GenericMenu(te);
			case BLOCK_POCKET_MANAGER:
				if (te instanceof BlockPocketManagerBlockEntity)
					return new BlockPocketManagerMenu(player.inventory, (BlockPocketManagerBlockEntity) te);
				return null;
			case PROJECTOR:
				if (te instanceof ProjectorBlockEntity)
					return new ProjectorMenu(player.inventory, (ProjectorBlockEntity) te);
				return null;
			case SONIC_SECURITY_SYSTEM:
				if (te instanceof SonicSecuritySystemBlockEntity)
					return new GenericMenu(te);
				return null;
			case BLOCK_CHANGE_DETECTOR:
				if (te instanceof BlockChangeDetectorBlockEntity)
					return new BlockChangeDetectorMenu(player.inventory, (BlockChangeDetectorBlockEntity) te);
				return null;
			case SSS_ITEM:
				return new GenericMenu(te);
			case RIFT_STABILIZER:
				return new GenericMenu(te);
			case ALARM:
				return new GenericMenu(te);
			case KEYCARD_HOLDER:
				if (PlayerUtils.getItemStackFromAnyHand(player, SCContent.keycardHolder).isEmpty())
					return null;
				return new KeycardHolderMenu(player.inventory, ItemContainer.keycardHolder(PlayerUtils.getItemStackFromAnyHand(player, SCContent.keycardHolder)));
			case LASER_BLOCK:
				return new LaserBlockMenu((LaserBlockBlockEntity) te, player.inventory);
			case CLAYMORE:
				return new ClaymoreMenu((ClaymoreBlockEntity) te, player.inventory);
			default:
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

		switch (id) {
			case KEYCARD_READER_ID:
				return new KeycardReaderScreen(player.inventory, (KeycardReaderBlockEntity) te);
			case MRAT_MENU_ID:
				if (!PlayerUtils.getItemStackFromAnyHand(player, SCContent.remoteAccessMine).isEmpty())
					return new MineRemoteAccessToolScreen(PlayerUtils.getItemStackFromAnyHand(player, SCContent.remoteAccessMine));
				else
					return null;
			case SRAT_MENU_ID:
				if (!PlayerUtils.getItemStackFromAnyHand(player, SCContent.remoteAccessSentry).isEmpty())
					return new SentryRemoteAccessToolScreen(PlayerUtils.getItemStackFromAnyHand(player, SCContent.remoteAccessSentry), x);
				else
					return null;
			case INVENTORY_SCANNER_GUI_ID:
				return new InventoryScannerScreen(player.inventory, (InventoryScannerBlockEntity) te, player);
			case USERNAME_LOGGER_GUI_ID:
				return new UsernameLoggerScreen((UsernameLoggerBlockEntity) te);
			case KEYPAD_FURNACE_GUI_ID:
				return new KeypadFurnaceScreen(player.inventory, (KeypadFurnaceBlockEntity) te);
			case SETUP_PASSCODE_ID:
				return new SetPasscodeScreen(te);
			case INSERT_PASSCODE_ID:
				return new CheckPasscodeScreen(te);
			case IMS_GUI_ID:
				return new IMSScreen((IMSBlockEntity) te);
			case CAMERA_MONITOR_GUI_ID:
				if (PlayerUtils.getItemStackFromAnyHand(player, SCContent.cameraMonitor).isEmpty())
					return null;
				return new CameraMonitorScreen(player.inventory, (CameraMonitorItem) PlayerUtils.getItemStackFromAnyHand(player, SCContent.cameraMonitor).getItem(), PlayerUtils.getItemStackFromAnyHand(player, SCContent.cameraMonitor).getTagCompound());
			case BRIEFCASE_CODE_SETUP_GUI_ID:
				ItemStack briefcase0 = PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase);

				if (!briefcase0.isEmpty())
					return new BriefcasePasscodeScreen(true, briefcase0.getDisplayName() + " " + Utils.localize("gui.securitycraft:passcode.setup").getFormattedText());
				return null;
			case BRIEFCASE_INSERT_CODE_GUI_ID:
				ItemStack briefcase1 = PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase);

				if (!briefcase1.isEmpty())
					return new BriefcasePasscodeScreen(false, briefcase1.getDisplayName());
				return null;
			case BRIEFCASE_GUI_ID:
				ItemStack briefcase2 = PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase);

				if (!briefcase2.isEmpty())
					return new ItemInventoryScreen.Briefcase(new BriefcaseMenu(player.inventory, ItemContainer.briefcase(briefcase2)), player.inventory, briefcase2.getDisplayName());
				else
					return null;
			case KEY_CHANGER_GUI_ID:
				if (te == null || PlayerUtils.getItemStackFromAnyHand(player, SCContent.universalKeyChanger).isEmpty())
					return null;
				return new KeyChangerScreen(te);
			case TROPHY_SYSTEM_GUI_ID:
				return new TrophySystemScreen(new TrophySystemMenu((TrophySystemBlockEntity) te, player.inventory));
			case CUSTOMIZE_BLOCK:
				return new CustomizeBlockScreen(player.inventory, (IModuleInventory) te);
			case DISGUISE_MODULE:
				ItemStack module = player.inventory.getCurrentItem().getItem() instanceof ModuleItem ? player.inventory.getCurrentItem() : player.inventory.offHandInventory.get(0);
				if (!((ModuleItem) module.getItem()).canBeCustomized())
					return null;
				return new DisguiseModuleScreen(player.inventory);
			case BLOCK_REINFORCER:
				boolean isLvl1 = player.getHeldItemMainhand().getItem() == SCContent.universalBlockReinforcerLvL1;
				return new BlockReinforcerScreen(new BlockReinforcerMenu(player, player.inventory, isLvl1), isLvl1);
			case MODULES:
				if (!PlayerUtils.getItemStackFromAnyHand(player, SCContent.allowlistModule).isEmpty() || !PlayerUtils.getItemStackFromAnyHand(player, SCContent.denylistModule).isEmpty())
					return new EditModuleScreen(player.getHeldItemMainhand(), te);
				return null;
			case BLOCK_POCKET_MANAGER:
				if (te instanceof BlockPocketManagerBlockEntity)
					return new BlockPocketManagerScreen(player.inventory, (BlockPocketManagerBlockEntity) te);
				return null;
			case PROJECTOR:
				if (te instanceof ProjectorBlockEntity)
					return new ProjectorScreen(player.inventory, (ProjectorBlockEntity) te);
				return null;
			case SONIC_SECURITY_SYSTEM:
				if (te instanceof SonicSecuritySystemBlockEntity)
					return new SonicSecuritySystemScreen((SonicSecuritySystemBlockEntity) te);
				return null;
			case BLOCK_CHANGE_DETECTOR:
				if (te instanceof BlockChangeDetectorBlockEntity)
					return new BlockChangeDetectorScreen(player.inventory, (BlockChangeDetectorBlockEntity) te);
				return null;
			case SSS_ITEM:
				if (PlayerUtils.getItemStackFromAnyHand(player, SCContent.sonicSecuritySystemItem).isEmpty())
					return null;
				return new SSSItemScreen(PlayerUtils.getItemStackFromAnyHand(player, SCContent.sonicSecuritySystemItem));
			case RIFT_STABILIZER:
				return new RiftStabilizerScreen((RiftStabilizerBlockEntity) te);
			case ALARM:
				return new AlarmScreen((AlarmBlockEntity) te, ((AlarmBlockEntity) te).getSound().getRegistryName());
			case KEYCARD_HOLDER:
				ItemStack keycardHolder = PlayerUtils.getItemStackFromAnyHand(player, SCContent.keycardHolder);

				if (!keycardHolder.isEmpty())
					return new ItemInventoryScreen.KeycardHolder(new KeycardHolderMenu(player.inventory, ItemContainer.keycardHolder(keycardHolder)), player.inventory, keycardHolder.getDisplayName());
				return null;
			case LASER_BLOCK:
				return new LaserBlockScreen(new LaserBlockMenu((LaserBlockBlockEntity) te, player.inventory));
			case CLAYMORE:
				return new ClaymoreScreen(new ClaymoreMenu((ClaymoreBlockEntity) te, player.inventory));
			default:
				return null;
		}
	}
}
