package net.breakinbad.securitycraft.imc.lookingglass;

import com.xcompwiz.lookingglass.api.APIInstanceProvider;
import com.xcompwiz.lookingglass.api.APIUndefined;
import com.xcompwiz.lookingglass.api.APIVersionRemoved;
import com.xcompwiz.lookingglass.api.APIVersionUndefined;
import com.xcompwiz.lookingglass.api.view.IWorldView;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.breakinbad.securitycraft.main.Utils;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.network.ClientProxy;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/**
 * Simple class that gets the LookingGlass "view" API instance. <p>
 * Called from a IMC message sent in mod_SecurityCraft.init().
 * 
 * @author Geforce
 */
public class LookingGlassAPIProvider {
	
	public static void register(APIInstanceProvider provider){
		try{
			Object viewAPI = provider.getAPIInstance("view-2");
			
			if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
				LookingGlassAPIHandler.handleAPICast(viewAPI);
			}
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
	public static void createLookingGlassView(World world, int dimension, int xCoord, int yCoord, int zCoord, int viewWidth, int viewHeight){
		if(!Loader.isModLoaded("LookingGlass")){ return; }
		
		IWorldView lgView = mod_SecurityCraft.instance.getLGPanelRenderer().getApi().createWorldView(dimension, new ChunkCoordinates(xCoord, yCoord, zCoord), viewWidth, viewHeight); 
		
		lgView.setAnimator(new CameraAnimatorSecurityCamera(lgView.getCamera(), world.getBlockMetadata(xCoord, yCoord, zCoord)));

		if(!mod_SecurityCraft.instance.hasViewForCoords(xCoord + " " + yCoord + " " + zCoord)){
			mod_SecurityCraft.log("Inserting new view at" + Utils.getFormattedCoordinates(xCoord, yCoord, zCoord));
			((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.put(xCoord + " " + yCoord + " " + zCoord, new IWorldViewHelper(lgView));		
		}
	}


}
