package net.breakinbad.securitycraft.gui;

import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class SecurityCraftConfigGUI extends GuiConfig{
	
	public SecurityCraftConfigGUI(GuiScreen parent) {
        super(parent, new ConfigElement(mod_SecurityCraft.configFile.getCategory("options")).getChildElements(), "securitycraft", true, false, GuiConfig.getAbridgedConfigPath(mod_SecurityCraft.configFile.toString()));
	}

}
