package org.freeforums.geforce.securitycraft.imc.lookingglass;

import com.xcompwiz.lookingglass.api.IWorldViewAPI;

public class LookingGlassPanelRenderer {
	
	private final IWorldViewAPI api;

	public LookingGlassPanelRenderer(IWorldViewAPI api) {
		this.api = api;
	}

	public IWorldViewAPI getApi() {
		return api;
	}

}
