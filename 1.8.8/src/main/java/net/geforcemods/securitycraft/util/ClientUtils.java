package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientUtils{
	
	@SideOnly(Side.CLIENT)
	public static void closePlayerScreen(){
		Minecraft.getMinecraft().thePlayer.closeScreen();
	}
	
	/**
	 * Sets the "zoom" of the client's view.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void setCameraZoom(double zoom){
		if(zoom == 0){
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, 1.0D, 48);
			return;
		}
		
		double tempZoom = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, 48);
		ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, tempZoom + zoom, 48);
	}
	
	/**
	 * Gets the "zoom" of the client's view.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static double getCameraZoom(){
		return ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, 48);
	}
	
	/**
	 * Takes a screenshot, and sends the player a notification. <p>
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void takeScreenshot() {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
        	Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(Minecraft.getMinecraft().mcDataDir, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, Minecraft.getMinecraft().getFramebuffer()));	
        }
	}
	
	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static String getFormattedMinecraftTime(){
		Long time = Long.valueOf(Minecraft.getMinecraft().theWorld.provider.getWorldTime());
		
		int hours24 = (int) ((float) time.longValue() / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) ((float) time.longValue() / 16.666666F % 60.0F);
		
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
		mod_SecurityCraft.network.sendToServer(new PacketSSyncTENBTTag(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), tag));
	}
	
	/**
	 * Sends the client-side NBTTagCompound of a player's held item to the server.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void syncItemNBT(ItemStack item){
		mod_SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(item));
	}
	
	/**
	 * Returns true if the client is hosting a LAN world.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static boolean isInLANWorld(){
		return (Minecraft.getMinecraft().getIntegratedServer() != null && Minecraft.getMinecraft().getIntegratedServer().getPublic());
	}
}