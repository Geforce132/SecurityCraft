package net.geforcemods.securitycraft.compat.hudmods;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;

public final class WTHITCommonDataProvider implements IWailaCommonPlugin {
	@Override
	public void register(ICommonRegistrar registrar) {
		registrar.syncedConfig(HudModHandler.SHOW_OWNER, true, false);
		registrar.syncedConfig(HudModHandler.SHOW_MODULES, true, false);
		registrar.syncedConfig(HudModHandler.SHOW_CUSTOM_NAME, true, false);
	}
}
