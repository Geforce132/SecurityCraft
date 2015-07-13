package org.freeforums.geforce.securitycraft.imc.lookingglass;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import com.xcompwiz.lookingglass.api.IWorldViewAPI;


public class LookingGlassAPIHandler {

	public static void handleAPICast(Object api) {
		if(api instanceof IWorldViewAPI){ //Dim, chunk location, x, y
			mod_SecurityCraft.instance.lgPanelRenderer = new LookingGlassPanelRenderer((IWorldViewAPI) api);
		}
	}

}
