package org.freeforums.geforce.securitycraft.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SecurityCraftConfigGUI extends GuiConfig{
	public SecurityCraftConfigGUI(GuiScreen parent) {
        super(parent, new ConfigElement(mod_SecurityCraft.configFile.getCategory("options")).getChildElements(), "securitycraft", false, true, GuiConfig.getAbridgedConfigPath(mod_SecurityCraft.configFile.toString()));
	}

}
