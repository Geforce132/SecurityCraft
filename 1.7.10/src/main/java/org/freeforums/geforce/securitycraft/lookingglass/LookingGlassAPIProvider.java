package org.freeforums.geforce.securitycraft.lookingglass;

import com.xcompwiz.lookingglass.api.APIInstanceProvider;
import com.xcompwiz.lookingglass.api.APIUndefined;
import com.xcompwiz.lookingglass.api.APIVersionRemoved;
import com.xcompwiz.lookingglass.api.APIVersionUndefined;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * Simple class that gets the LookingGlass "view" API instance. <p>
 * Called from a IMC message sent in mod_SecurityCraft.init().
 * 
 * @author Geforce
 */
public class LookingGlassAPIProvider {
	
	public static void register(APIInstanceProvider provider){
		try{
			Object viewAPI = provider.getAPIInstance("view-1");
			
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

}
