package net.breakinbad.securitycraft.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class SecurityCraftGuiFactory implements IModGuiFactory{

	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement arg0) {
		return null;
	}

	public void initialize(Minecraft arg0) {
		
	}

	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return SecurityCraftConfigGUI.class;
	}

	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

}
