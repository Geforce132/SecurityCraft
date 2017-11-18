package net.geforcemods.securitycraft.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class SecurityCraftGuiFactory implements IModGuiFactory{

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement arg0) {
		return null;
	}

	@Override
	public void initialize(Minecraft arg0) {

	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return SecurityCraftConfigGUI.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public boolean hasConfigGui()
	{
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen)
	{
		return new SecurityCraftConfigGUI(parentScreen);
	}

}
