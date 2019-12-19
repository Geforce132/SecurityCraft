package net.geforcemods.securitycraft.util;

import java.net.URI;
import java.net.URISyntaxException;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.SyncTENBTTag;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientUtils{

	@OnlyIn(Dist.CLIENT)
	public static void closePlayerScreen(){
		Minecraft.getInstance().player.closeScreen();
	}

	/**
	 * Takes a screenshot, and sends the player a notification. <p>
	 *
	 * Only works on the CLIENT side.
	 */
	@OnlyIn(Dist.CLIENT)
	public static void takeScreenshot() {
		ScreenShotHelper.saveScreenshot(
				Minecraft.getInstance().gameDir,
				Minecraft.getInstance().func_228018_at_().getWidth(),
				Minecraft.getInstance().func_228018_at_().getHeight(),
				Minecraft.getInstance().getFramebuffer(),
				msg -> Minecraft.getInstance().execute(() -> Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(msg)));
	}

	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 *
	 * Only works on the CLIENT side.
	 */
	@OnlyIn(Dist.CLIENT)
	public static String getFormattedMinecraftTime(){
		Long time = Long.valueOf(Minecraft.getInstance().world.getDayTime());

		int hours24 = (int) ((float) time.longValue() / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) (time.longValue() / 16.666666F % 60.0F);

		return String.format("%02d:%02d %s", new Object[]{Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM"});
	}

	/**
	 * Sends the client-side CompoundNBT of a block's TileEntity to the server.
	 *
	 * Only works on the CLIENT side.
	 */
	@OnlyIn(Dist.CLIENT)
	public static void syncTileEntity(TileEntity tileEntity){
		CompoundNBT tag = new CompoundNBT();
		tileEntity.write(tag);
		SecurityCraft.channel.sendToServer(new SyncTENBTTag(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), tag));
	}

	/**
	 * Sends the client-side CompoundNBT of a player's held item to the server.
	 *
	 * Only works on the CLIENT side.
	 */
	@OnlyIn(Dist.CLIENT)
	public static void syncItemNBT(ItemStack item){
		SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(item));
	}

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
			Class<?> oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
		}

		catch (Throwable throwable) {}
	}

	/**
	 * Localizes a String with the given format
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static String localize(String key, Object... params)
	{
		for(int i = 0; i < params.length; i++)
		{
			if(params[i] instanceof TranslationTextComponent)
				params[i] = localize(((TranslationTextComponent)params[i]).getKey(), ((TranslationTextComponent)params[i]).getFormatArgs());
			else if(params[i] instanceof BlockPos)
				params[i] = Utils.getFormattedCoordinates((BlockPos)params[i]);
		}

		return new TranslationTextComponent(key, params).getFormattedText();
	}
}