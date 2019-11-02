package net.geforcemods.securitycraft.util;

import java.net.URI;
import java.net.URISyntaxException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.StatCollector;

public class ClientUtils{

	/**
	 * Closes any GUI that is currently open. <p>
	 *
	 * Only works on the CLIENT side.
	 */
	@SideOnly(Side.CLIENT)
	public static void closePlayerScreen(){
		Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
		Minecraft.getMinecraft().setIngameFocus();
	}

	/**
	 * Takes a screenshot, and sends the player a notification. <p>
	 *
	 * Only works on the CLIENT side.
	 */
	@SideOnly(Side.CLIENT)
	public static void takeScreenshot() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(Minecraft.getMinecraft().mcDataDir, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, Minecraft.getMinecraft().getFramebuffer()));
	}

	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 *
	 * Only works on the CLIENT side.
	 */
	@SideOnly(Side.CLIENT)
	public static String getFormattedMinecraftTime(){
		long time = Minecraft.getMinecraft().theWorld.provider.getWorldTime();
		int hours24 = (int) ((float) time / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (time / 16.666666F % 60.0F);

		return String.format("%02d:%02d %s", new Object[]{Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM"});
	}

	/**
	 * Sends the client-side NBTTagCompound of a block's TileEntity to the server.
	 *
	 * Only works on the CLIENT side.
	 */
	@SideOnly(Side.CLIENT)
	public static void syncTileEntity(TileEntity tileEntity){
		NBTTagCompound tag = new NBTTagCompound();
		tileEntity.writeToNBT(tag);
		SecurityCraft.network.sendToServer(new PacketSSyncTENBTTag(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tag));
	}

	/**
	 * Sends the client-side NBTTagCompound of a player's held item to the server.
	 *
	 * Only works on the CLIENT side.
	 */
	@SideOnly(Side.CLIENT)
	public static void syncItemNBT(ItemStack item){
		SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(item));
	}

	@SideOnly(Side.CLIENT)
	public static void openURL(String url) {
		URI uri = null;

		try {
			uri = new URI(url);
		}
		catch(URISyntaxException e) {
			e.printStackTrace();
		}

		if(uri == null) return;

		try {
			Class oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
		}

		catch (Throwable throwable) {}
	}

	public static String localize(String toTranslate, Object... params)
	{
		for(int i = 0; i < params.length; i++)
		{
			if(params[i] instanceof TranslatableString)
				params[i] = localize(((TranslatableString)params[i]).getString(), ((TranslatableString)params[i]).getArgs());
			else if(params[i] instanceof BlockPos)
				params[i] = Utils.getFormattedCoordinates((BlockPos)params[i]);
		}

		return StatCollector.translateToLocalFormatted(toTranslate, params);
	}
}