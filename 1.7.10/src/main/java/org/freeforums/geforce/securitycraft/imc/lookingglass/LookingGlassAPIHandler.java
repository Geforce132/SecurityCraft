package org.freeforums.geforce.securitycraft.imc.lookingglass;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import com.xcompwiz.lookingglass.api.hook.WorldViewAPI2;


public class LookingGlassAPIHandler {

	public static void handleAPICast(Object api) {
		if(api instanceof WorldViewAPI2){ 
			mod_SecurityCraft.instance.lgPanelRenderer = new LookingGlassPanelRenderer((WorldViewAPI2) api);
		}
	}

}
