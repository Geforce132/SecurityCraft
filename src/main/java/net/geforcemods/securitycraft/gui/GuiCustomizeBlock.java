package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.containers.ContainerCustomizeBlock;
import net.geforcemods.securitycraft.gui.components.GuiPictureButton;
import net.geforcemods.securitycraft.gui.components.GuiSlider;
import net.geforcemods.securitycraft.network.packets.PacketSToggleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiCustomizeBlock extends GuiContainer{
	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation("securitycraft:textures/gui/container/customize1.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize2.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize3.png")
	};
	private CustomizableSCTE tileEntity;
	private GuiPictureButton[] descriptionButtons = new GuiPictureButton[5];
	private GuiButton[] optionButtons = new GuiButton[5];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];

	private final String blockName;

	public GuiCustomizeBlock(InventoryPlayer playerInv, CustomizableSCTE te)
	{
		super(new ContainerCustomizeBlock(playerInv, te));
		tileEntity = te;
		blockName = Minecraft.getMinecraft().theWorld.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord).getUnlocalizedName().substring(5);
	}

	@Override
	public void initGui(){
		super.initGui();

		for(int i = 0; i < tileEntity.getNumberOfCustomizableOptions(); i++){
			descriptionButtons[i] = new GuiPictureButton(i, guiLeft + 130, (guiTop + 10) + (i * 25), 20, 20, itemRender, new ItemStack(tileEntity.acceptedModules()[i].getItem()));
			buttonList.add(descriptionButtons[i]);
			hoverCheckers[i] = new HoverChecker(descriptionButtons[i], 20);
		}

		if(tileEntity.customOptions() != null)
			for(int i = 0; i < tileEntity.customOptions().length; i++){
				Option<?> option = tileEntity.customOptions()[i];

				if(option instanceof OptionDouble && ((OptionDouble)option).isSlider())
				{
					optionButtons[i] = new GuiSlider((StatCollector.translateToLocal("option." + blockName + "." + option.getName()) + " ").replace("#", option.toString()), blockName, i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, "", "", (double)option.getMin(), (double)option.getMax(), (double)option.getValue(), true, true, (OptionDouble)option);
					optionButtons[i].packedFGColour = 14737632;
				}
				else
				{
					optionButtons[i] = new GuiButton(i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, getOptionButtonTitle(option));
					optionButtons[i].packedFGColour = option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632;
				}

				buttonList.add(optionButtons[i]);
				hoverCheckers[i + tileEntity.getNumberOfCustomizableOptions()] = new HoverChecker(optionButtons[i], 20);
			}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);

		for(int i = 0; i < hoverCheckers.length; i++)
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY))
				if(i < tileEntity.getNumberOfCustomizableOptions())
					drawHoveringText(mc.fontRendererObj.listFormattedStringToWidth(getModuleDescription(i), 150), mouseX, mouseY, mc.fontRendererObj);
				else
					drawHoveringText(mc.fontRendererObj.listFormattedStringToWidth(getOptionDescription(i), 150), mouseX, mouseY, mc.fontRendererObj);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = tileEntity.isCustomInventoryName() ? tileEntity.getInventoryName() : I18n.format(tileEntity.getInventoryName(), new Object[0]);
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURES[tileEntity.getNumberOfCustomizableOptions() - 1]);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if(!(guibutton instanceof GuiPictureButton)) {
			Option<?> tempOption = tileEntity.customOptions()[guibutton.id];
			tempOption.toggle();
			guibutton.packedFGColour = tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632;
			guibutton.displayString = getOptionButtonTitle(tempOption);
			SecurityCraft.network.sendToServer(new PacketSToggleOption(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, guibutton.id));
		}
	}

	private String getModuleDescription(int buttonID) {
		String moduleDescription = "module." + blockName + "." + descriptionButtons[buttonID].getItemStack().getUnlocalizedName().substring(5).replace("securitycraft:", "") + ".description";

		return StatCollector.translateToLocal(descriptionButtons[buttonID].getItemStack().getUnlocalizedName() + ".name") + ":" + EnumChatFormatting.RESET + "\n\n" + StatCollector.translateToLocal(moduleDescription);
	}

	private String getOptionDescription(int buttonID) {
		return StatCollector.translateToLocal("option." + blockName + "." + tileEntity.customOptions()[buttonID - tileEntity.getNumberOfCustomizableOptions()].getName() + ".description");
	}

	private String getOptionButtonTitle(Option<?> option) {
		return (StatCollector.translateToLocal("option." + blockName + "." + option.getName()) + " ").replace("#", option.toString());
	}

}