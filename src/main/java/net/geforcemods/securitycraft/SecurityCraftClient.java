package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.compat.ium.IumCompat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = SecurityCraft.MODID, dist = Dist.CLIENT)
public class SecurityCraftClient {
	public static final IumCompat INSTALLED_IUM_MOD = IumCompat.getInstalledIumMod();

	public SecurityCraftClient(ModContainer container) {
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		container.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
		INSTALLED_IUM_MOD.resolve();
	}
}
