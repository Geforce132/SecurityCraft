package net.geforcemods.securitycraft.imc.lookingglass;

import com.xcompwiz.lookingglass.api.hook.WorldViewAPI2;

import net.geforcemods.securitycraft.SecurityCraft;


public class LookingGlassAPIHandler {

	public static void handleAPICast(Object api) {
		if(api instanceof WorldViewAPI2)
			SecurityCraft.instance.lgPanelRenderer = new LookingGlassPanelRenderer((WorldViewAPI2) api);
	}

}
