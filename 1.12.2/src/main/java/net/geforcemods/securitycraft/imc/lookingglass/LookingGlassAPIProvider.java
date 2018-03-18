package net.geforcemods.securitycraft.imc.lookingglass;

import com.xcompwiz.lookingglass.api.APIInstanceProvider;
import com.xcompwiz.lookingglass.api.APIUndefined;
import com.xcompwiz.lookingglass.api.APIVersionRemoved;
import com.xcompwiz.lookingglass.api.APIVersionUndefined;
import com.xcompwiz.lookingglass.api.hook.WorldViewAPI2;
import com.xcompwiz.lookingglass.api.view.IWorldView;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
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

	public static void register(APIInstanceProvider provider){
		try{
			WorldViewAPI2 viewAPI = (WorldViewAPI2)provider.getAPIInstance("view-2");

			if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				SecurityCraft.instance.lookingGlass = viewAPI;
		}catch(APIUndefined e){
			e.printStackTrace();
		}catch(APIVersionUndefined e){
			e.printStackTrace();
		}catch(APIVersionRemoved e){
			e.printStackTrace();
		}
	}

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
				IWorldView lgView = SecurityCraft.instance.lookingGlass.createWorldView(dimension, pos, viewWidth, viewHeight);

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
