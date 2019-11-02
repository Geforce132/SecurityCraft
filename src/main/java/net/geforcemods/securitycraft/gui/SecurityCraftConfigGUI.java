package net.geforcemods.securitycraft.gui;

import cpw.mods.fml.client.config.GuiConfig;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

public class SecurityCraftConfigGUI extends GuiConfig{
	public SecurityCraftConfigGUI(GuiScreen parent) {
		super(parent, new ConfigElement(SecurityCraft.configFile.getCategory("options")).getChildElements(), "securitycraft", true, false, GuiConfig.getAbridgedConfigPath(SecurityCraft.configFile.toString()));
	}

}
