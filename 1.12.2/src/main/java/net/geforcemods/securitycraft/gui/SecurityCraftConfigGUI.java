package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class SecurityCraftConfigGUI extends GuiConfig{

	public SecurityCraftConfigGUI(GuiScreen parent) {
		super(parent, new ConfigElement(SecurityCraft.configFile.getCategory("options")).getChildElements(), "securitycraft", true, false, GuiConfig.getAbridgedConfigPath(SecurityCraft.configFile.toString()));
	}

}
