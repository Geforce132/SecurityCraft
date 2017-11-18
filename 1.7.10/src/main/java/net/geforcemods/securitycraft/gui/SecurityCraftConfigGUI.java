package net.geforcemods.securitycraft.gui;

import cpw.mods.fml.client.config.GuiConfig;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SecurityCraftConfigGUI extends GuiConfig{
	public SecurityCraftConfigGUI(GuiScreen parent) {
		super(parent, new ConfigElement(mod_SecurityCraft.configFile.getCategory("options")).getChildElements(), "securitycraft", true, false, GuiConfig.getAbridgedConfigPath(mod_SecurityCraft.configFile.toString()));
	}

}
