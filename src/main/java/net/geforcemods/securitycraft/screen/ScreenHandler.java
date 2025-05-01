package net.geforcemods.securitycraft.screen;

import java.util.function.BiFunction;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.inventory.BlockChangeDetectorMenu;
import net.geforcemods.securitycraft.inventory.BlockPocketManagerMenu;
import net.geforcemods.securitycraft.inventory.BlockReinforcerMenu;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
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
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.geforcemods.securitycraft.inventory.TrophySystemMenu;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

//@formatter:off
public class ScreenHandler implements IGuiHandler {
	public enum Screens {
		KEYCARD_READER(
			(player, te) -> new KeycardReaderMenu(player.inventory, (KeycardReaderBlockEntity) te),
			(player, te) -> new KeycardReaderScreen(player.inventory, (KeycardReaderBlockEntity) te)),
		MRAT(
			(player, te) -> new GenericMenu(te),
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.mineRemoteAccessTool);

				return heldStack.isEmpty() ? null : new MineRemoteAccessToolScreen(heldStack);
			}),
		SRAT(
			(player, te) -> new GenericMenu(te),
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.sentryRemoteAccessTool);

				return heldStack.isEmpty() ? null : new SentryRemoteAccessToolScreen(PlayerUtils.getItemStackFromAnyHand(player, SCContent.sentryRemoteAccessTool));
			}),
		INVENTORY_SCANNER(
			(player, te) -> new InventoryScannerMenu(player.inventory, (InventoryScannerBlockEntity) te),
			(player, te) -> new InventoryScannerScreen(player.inventory, (InventoryScannerBlockEntity) te, player)),
		USERNAME_LOGGER(
			(player, te) -> new GenericMenu(te),
			(player, te) -> new UsernameLoggerScreen((UsernameLoggerBlockEntity) te)),
		KEYPAD_FURNACE(
			(player, te) -> new KeypadFurnaceMenu(player.inventory, (KeypadFurnaceBlockEntity) te),
			(player, te) -> new KeypadFurnaceScreen(player.inventory, (KeypadFurnaceBlockEntity) te)),
		SETUP_PASSCODE(
			(player, te) -> new GenericMenu(te),
			(player, te) -> new SetPasscodeScreen(te)),
		INSERT_PASSCODE(
			(player, te) -> new GenericMenu(te),
			(player, te) -> new CheckPasscodeScreen(te)),
		CAMERA_MONITOR(
			(player, te) -> PlayerUtils.getItemStackFromAnyHand(player, SCContent.cameraMonitor).isEmpty() ? null : new GenericMenu(te),
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.cameraMonitor);

				return heldStack.isEmpty() ? null : new CameraMonitorScreen(heldStack);
			}),
		FRAME(
			(player, te) -> new GenericMenu(te),
			(player, te) -> ClientProxy.frameScreen(player, (FrameBlockEntity) te, false)),
		FRAME_READ_ONLY(
			(player, te) -> new GenericMenu(te),
			(player, te) -> ClientProxy.frameScreen(player, (FrameBlockEntity) te, true)),
		BRIEFCASE_CODE_SETUP(
			(player, te) -> PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase).isEmpty() ? null : new GenericMenu(te),
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase);

				return heldStack.isEmpty() ? null : new BriefcasePasscodeScreen(true, heldStack.getDisplayName() + " " + Utils.localize("gui.securitycraft:passcode.setup").getFormattedText());
			}),
		BRIEFCASE_INSERT_CODE(
			(player, te) -> PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase).isEmpty() ? null : new GenericMenu(te),
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase);

				return heldStack.isEmpty() ? null : new BriefcasePasscodeScreen(false, heldStack.getDisplayName());
			}),
		BRIEFCASE_INVENTORY(
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase);

				return heldStack.isEmpty() ? null : new BriefcaseMenu(player.inventory, ItemContainer.briefcase(heldStack));
			},
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase);

				return heldStack.isEmpty() ? null : new ItemInventoryScreen.Briefcase(new BriefcaseMenu(player.inventory, ItemContainer.briefcase(heldStack)), player.inventory, heldStack.getDisplayName());
			}),
		KEY_CHANGER(
			(player, te) -> te == null || PlayerUtils.getItemStackFromAnyHand(player, SCContent.universalKeyChanger).isEmpty() ? null : new GenericMenu(te),
			(player, te) -> te == null || PlayerUtils.getItemStackFromAnyHand(player, SCContent.universalKeyChanger).isEmpty() ? null : new KeyChangerScreen(te)),
		TROPHY_SYSTEM(
			(player, te) -> new TrophySystemMenu((TrophySystemBlockEntity) te, player.inventory),
			(player, te) -> new TrophySystemScreen(new TrophySystemMenu((TrophySystemBlockEntity) te, player.inventory))),
		CUSTOMIZE_BLOCK(
			(player, te) -> new CustomizeBlockMenu(player.inventory, (IModuleInventory) te),
			(player, te) -> new CustomizeBlockScreen(player.inventory, (IModuleInventory) te)),
		DISGUISE_MODULE(
			(player, te) -> {
				ItemStack module = PlayerUtils.getItemStackFromAnyHand(player, ModuleItem.class::isInstance);

				if (module.isEmpty() || !((ModuleItem) module.getItem()).canBeCustomized())
					return null;

				return new DisguiseModuleMenu(player.inventory, new ModuleItemContainer(module));
			},
			(player, te) -> {
				ItemStack module = PlayerUtils.getItemStackFromAnyHand(player, ModuleItem.class::isInstance);

				if (module.isEmpty() || !((ModuleItem) module.getItem()).canBeCustomized())
					return null;

				return new DisguiseModuleScreen(player.inventory);
			}),
		BLOCK_REINFORCER(
			(player, te) -> {
				ItemStack reinforcer = PlayerUtils.getItemStackFromAnyHand(player, item -> item == SCContent.universalBlockReinforcerLvL1 ||  item == SCContent.universalBlockReinforcerLvL2 ||  item == SCContent.universalBlockReinforcerLvL3);

				if (!reinforcer.isEmpty())
					return new BlockReinforcerMenu(player, player.inventory, reinforcer.getItem() == SCContent.universalBlockReinforcerLvL1);
				else
					return null;
			},
			(player, te) -> {
				ItemStack reinforcer = PlayerUtils.getItemStackFromAnyHand(player, item -> item == SCContent.universalBlockReinforcerLvL1 ||  item == SCContent.universalBlockReinforcerLvL2 ||  item == SCContent.universalBlockReinforcerLvL3);
				boolean isLvl1 = reinforcer.getItem() == SCContent.universalBlockReinforcerLvL1;

				if (!reinforcer.isEmpty())
					return new BlockReinforcerScreen(new BlockReinforcerMenu(player, player.inventory, isLvl1), isLvl1, reinforcer.getDisplayName());
				else
					return null;
			}),
		MODULES(
			(player, te) -> new GenericMenu(te),
			(player, te) -> {
				if (!PlayerUtils.getItemStackFromAnyHand(player, SCContent.allowlistModule).isEmpty() || !PlayerUtils.getItemStackFromAnyHand(player, SCContent.denylistModule).isEmpty())
					return new EditModuleScreen(player.getHeldItemMainhand(), te);

				return null;
			}),
		BLOCK_POCKET_MANAGER(
			(player, te) -> te instanceof BlockPocketManagerBlockEntity ? new BlockPocketManagerMenu(player.inventory, (BlockPocketManagerBlockEntity) te) : null,
			(player, te) -> te instanceof BlockPocketManagerBlockEntity ? new BlockPocketManagerScreen(player.inventory, (BlockPocketManagerBlockEntity) te) : null),
		PROJECTOR(
			(player, te) -> te instanceof ProjectorBlockEntity ? new ProjectorMenu(player.inventory, (ProjectorBlockEntity) te) : null,
			(player, te) -> te instanceof ProjectorBlockEntity ? new ProjectorScreen(player.inventory, (ProjectorBlockEntity) te) : null),
		SONIC_SECURITY_SYSTEM(
			(player, te) -> te instanceof SonicSecuritySystemBlockEntity ? new GenericMenu(te) : null,
			(player, te) -> te instanceof SonicSecuritySystemBlockEntity ? new SonicSecuritySystemScreen((SonicSecuritySystemBlockEntity) te) : null),
		BLOCK_CHANGE_DETECTOR(
			(player, te) -> te instanceof BlockChangeDetectorBlockEntity ? new BlockChangeDetectorMenu(player.inventory, (BlockChangeDetectorBlockEntity) te) : null,
			(player, te) -> te instanceof BlockChangeDetectorBlockEntity ? new BlockChangeDetectorScreen(player.inventory, (BlockChangeDetectorBlockEntity) te) : null),
		SSS_ITEM(
			(player, te) -> new GenericMenu(te),
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.sonicSecuritySystemItem);

				return heldStack.isEmpty() ? null : new SSSItemScreen(heldStack);
			}),
		KEYCARD_HOLDER(
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.keycardHolder);

				return heldStack.isEmpty() ? null : new KeycardHolderMenu(player.inventory, ItemContainer.keycardHolder(heldStack));
			},
			(player, te) -> {
				ItemStack heldStack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.keycardHolder);

				return heldStack.isEmpty() ? null : new ItemInventoryScreen.KeycardHolder(new KeycardHolderMenu(player.inventory, ItemContainer.keycardHolder(heldStack)), player.inventory, heldStack.getDisplayName());
			}),
		LASER_BLOCK(
			(player, te) -> new LaserBlockMenu((LaserBlockBlockEntity) te, player.inventory),
			(player, te) -> new LaserBlockScreen(new LaserBlockMenu((LaserBlockBlockEntity) te, player.inventory))),
		SINGLE_LENS(
			(player, te) -> new SingleLensMenu(te, player.inventory),
			(player, te) -> new SingleLensScreen(new SingleLensMenu(te, player.inventory)));
		//@formatter:on

		private final BiFunction<EntityPlayer, TileEntity, Object> server, client;

		Screens(BiFunction<EntityPlayer, TileEntity, Object> server, BiFunction<EntityPlayer, TileEntity, Object> client) {
			this.server = server;
			this.client = client;
		}

		public BiFunction<EntityPlayer, TileEntity, Object> getServer() {
			return server;
		}

		public BiFunction<EntityPlayer, TileEntity, Object> getClient() {
			return client;
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id >= 0 && id < Screens.values().length)
			return Screens.values()[id].server.apply(player, world.getTileEntity(new BlockPos(x, y, z)));
		else
			return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id >= 0 && id < Screens.values().length)
			return Screens.values()[id].client.apply(player, world.getTileEntity(new BlockPos(x, y, z)));
		else
			return null;
	}
}
