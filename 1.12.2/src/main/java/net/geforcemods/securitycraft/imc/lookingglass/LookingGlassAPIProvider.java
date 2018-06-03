package net.geforcemods.securitycraft.imc.lookingglass;

import com.xcompwiz.lookingglass.api.view.IWorldView;
import com.xcompwiz.lookingglass.client.proxyworld.ProxyWorldManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Simple class that gets the LookingGlass "view" API instance. <p>
 * Called from an IMC message sent in {@link net.geforcemods.securitycraft.SecurityCraft#init}
 *
 * @author Geforce
 */
public class LookingGlassAPIProvider {

	/**
	 * Creates an {@link IWorldView} object, then adds it to ClientProxy.worldViews.
	 *
	 * Only works on the CLIENT side.
	 *
	 * @param world The world we are in.
	 * @param dimension The dimension to view. (0 = Overworld, -1 = Nether)
	 * @param xCoord View X coordinate.
	 * @param yCoord View Y coordinate.
	 * @param zCoord View Z coordinate.
	 * @param viewWidth View width in pixels.
	 * @param viewHeight View height in pixels.
	 */
	@SideOnly(Side.CLIENT)
	public static void createLookingGlassView(World world, int dimension, BlockPos pos, int viewWidth, int viewHeight){
		if(!Loader.isModLoaded("lookingglass"))
			return;

		try
		{
			WorldUtils.addScheduledTask(Minecraft.getMinecraft().world, () -> {
				IWorldView lgView = ProxyWorldManager.createWorldView(dimension, pos, viewWidth, viewHeight);

				lgView.setAnimator(new CameraAnimatorSecurityCamera(lgView.getCamera(), pos, BlockUtils.getBlockMeta(world, pos)));

				if(!SecurityCraft.instance.hasViewForCoords(pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + dimension)){
					SecurityCraft.log("Inserting new view at" + Utils.getFormattedCoordinates(pos));
					((ClientProxy) SecurityCraft.serverProxy).worldViews.put(pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + dimension, new IWorldViewHelper(lgView));
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
