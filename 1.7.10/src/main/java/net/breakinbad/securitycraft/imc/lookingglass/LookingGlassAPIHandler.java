package net.breakinbad.securitycraft.imc.lookingglass;

import com.xcompwiz.lookingglass.api.hook.WorldViewAPI2;

import net.breakinbad.securitycraft.main.mod_SecurityCraft;


public class LookingGlassAPIHandler {

	public static void handleAPICast(Object api) {
		if(api instanceof WorldViewAPI2){ 
			mod_SecurityCraft.instance.lgPanelRenderer = new LookingGlassPanelRenderer((WorldViewAPI2) api);
		}
	}

}
