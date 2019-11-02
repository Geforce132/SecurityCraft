package net.geforcemods.securitycraft.compat.lookingglass;

import com.xcompwiz.lookingglass.api.hook.WorldViewAPI2;


public class LookingGlassPanelRenderer {

	private final WorldViewAPI2 api;

	public LookingGlassPanelRenderer(WorldViewAPI2 api) {
		this.api = api;
	}

	public WorldViewAPI2 getApi() {
		return api;
	}

}
