package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.containers.ContainerBlockReinforcer;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLPlayMessages.OpenContainer;

public class GuiHandler {

	public static final ResourceLocation SETUP_KEYCARD_READER = new ResourceLocation(SecurityCraft.MODID, "keycard_reader");
	public static final ResourceLocation MRAT = new ResourceLocation(SecurityCraft.MODID, "mrat");
	public static final ResourceLocation INVENTORY_SCANNER = new ResourceLocation(SecurityCraft.MODID, "inventory_scanner");
	public static final ResourceLocation USERNAME_LOGGER = new ResourceLocation(SecurityCraft.MODID, "username_logger");
	public static final ResourceLocation KEYPAD_FURNACE = new ResourceLocation(SecurityCraft.MODID, "keypad_furnace");
	public static final ResourceLocation SETUP_PASSWORD = new ResourceLocation(SecurityCraft.MODID, "setup_password");
	public static final ResourceLocation INSERT_PASSWORD = new ResourceLocation(SecurityCraft.MODID, "insert_password");
	public static final ResourceLocation IMS = new ResourceLocation(SecurityCraft.MODID, "ims");
	public static final ResourceLocation CAMERA_MONITOR = new ResourceLocation(SecurityCraft.MODID, "camera_monitor");
	public static final ResourceLocation BRIEFCASE_SETUP = new ResourceLocation(SecurityCraft.MODID, "briefcase_setup");
	public static final ResourceLocation BRIEFCASE_INSERT = new ResourceLocation(SecurityCraft.MODID, "briefcase_insert");
	public static final ResourceLocation BRIEFCASE = new ResourceLocation(SecurityCraft.MODID, "briefcase");
	public static final ResourceLocation KEY_CHANGER = new ResourceLocation(SecurityCraft.MODID, "key_changer");
	public static final ResourceLocation CUSTOMIZE_BLOCK = new ResourceLocation(SecurityCraft.MODID, "customize_block");
	public static final ResourceLocation DISGUISE_MODULE = new ResourceLocation(SecurityCraft.MODID, "disguise_module");
	public static final ResourceLocation BLOCK_REINFORCER = new ResourceLocation(SecurityCraft.MODID, "block_reinforcer");
	public static final ResourceLocation MODULES = new ResourceLocation(SecurityCraft.MODID, "modules");
	public static final ResourceLocation MANUAL = new ResourceLocation(SecurityCraft.MODID, "manual");

	public static GuiScreen getClientGuiElement(OpenContainer message)
	{
		EntityPlayerSP player = Minecraft.getInstance().player;

		if(message.getId().equals(SETUP_KEYCARD_READER))
		{
			TileEntity te = getTe(message);

			if(te instanceof TileEntityKeycardReader)
				return new GuiKeycardSetup((TileEntityKeycardReader)te);
		}
		else if(message.getId().equals(MRAT) && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == SCContent.remoteAccessMine)
			return new GuiMRAT(player.getHeldItemMainhand());
		else if(message.getId().equals(INVENTORY_SCANNER))
		{
			TileEntity te = getTe(message);

			if(te instanceof TileEntityInventoryScanner)
				return new GuiInventoryScanner(player.inventory, (TileEntityInventoryScanner)te, player);
		}
		else if(message.getId().equals(USERNAME_LOGGER))
		{
			TileEntity te = getTe(message);

			if(te instanceof TileEntityLogger)
				return new GuiLogger((TileEntityLogger)te);
		}
		else if(message.getId().equals(KEYPAD_FURNACE))
		{
			TileEntity te = getTe(message);

			if(te instanceof TileEntityKeypadFurnace)
				return new GuiKeypadFurnaceInventory(player.inventory, (TileEntityKeypadFurnace)te);
		}
		else if(message.getId().equals(SETUP_PASSWORD))
		{
			TileEntity te = getTe(message);

			if(te != null)
				return new GuiSetPassword(te, te.getWorld().getBlockState(te.getPos()).getBlock());
		}
		else if(message.getId().equals(INSERT_PASSWORD))
		{
			TileEntity te = getTe(message);

			if(te != null)
				return new GuiCheckPassword(te, te.getWorld().getBlockState(te.getPos()).getBlock());
		}
		else if(message.getId().equals(IMS))
		{
			TileEntity te = getTe(message);

			if(te instanceof TileEntityIMS)
				return new GuiIMS((TileEntityIMS)te);
		}
		else if(message.getId().equals(CAMERA_MONITOR) && PlayerUtils.isHoldingItem(player, SCContent.cameraMonitor))
			return new GuiCameraMonitor(player.inventory, (ItemCameraMonitor) player.inventory.getCurrentItem().getItem(), player.inventory.getCurrentItem().getTag());
		else if(message.getId().equals(BRIEFCASE_SETUP) && PlayerUtils.isHoldingItem(player, SCContent.briefcase))
			return new GuiBriefcaseSetup();
		else if(message.getId().equals(BRIEFCASE_INSERT) && PlayerUtils.isHoldingItem(player, SCContent.briefcase))
			return new GuiBriefcase();
		else if(message.getId().equals(BRIEFCASE) && PlayerUtils.isHoldingItem(player, SCContent.briefcase))
			return new GuiBriefcaseInventory(player, player.inventory);
		else if(message.getId().equals(KEY_CHANGER))
		{
			TileEntity te = getTe(message);

			if(te != null && PlayerUtils.isHoldingItem(player, SCContent.universalKeyChanger))
				return new GuiKeyChanger(te);
		}
		else if(message.getId().equals(CUSTOMIZE_BLOCK))
		{
			TileEntity te = getTe(message);

			if(te instanceof CustomizableSCTE)
				return new GuiCustomizeBlock(player.inventory, (CustomizableSCTE)te);
		}
		else if(message.getId().equals(DISGUISE_MODULE) && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canBeCustomized())
			return new GuiDisguiseModule(player, player.inventory);
		else if(message.getId().equals(BLOCK_REINFORCER))
			return new GuiBlockReinforcer(new ContainerBlockReinforcer(player, player.inventory));
		else if(message.getId().equals(MODULES) && (player.getHeldItemMainhand().getItem() == SCContent.whitelistModule || player.getHeldItemMainhand().getItem() == SCContent.blacklistModule))
			return new GuiEditModule(player.getHeldItemMainhand());
		else if(message.getId().equals(MANUAL))
			return new GuiSCManual();

		return null;
	}

	private static TileEntity getTe(OpenContainer message)
	{
		return Minecraft.getInstance().world.getTileEntity(message.getAdditionalData().readBlockPos());
	}
}
