package net.geforcemods.securitycraft.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.containers.ContainerCustomizeBlock;
import net.geforcemods.securitycraft.gui.components.GuiPictureButton;
import net.geforcemods.securitycraft.gui.components.GuiSlider;
import net.geforcemods.securitycraft.network.packets.PacketSToggleOption;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCustomizeBlock extends GuiContainer{

	private CustomizableSCTE tileEntity;
	private GuiPictureButton[] descriptionButtons = new GuiPictureButton[5];
	private GuiButton[] optionButtons = new GuiButton[5];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];

	private final String blockName;

	public GuiCustomizeBlock(InventoryPlayer inventory, CustomizableSCTE te)
	{
		super(new ContainerCustomizeBlock(inventory, te));
		tileEntity = te;
		blockName = BlockUtils.getBlock(Minecraft.getMinecraft().theWorld, tileEntity.getPos()).getUnlocalizedName().substring(5);
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
				Option option = tileEntity.customOptions()[i];

				if(option instanceof OptionDouble && ((OptionDouble)option).isSlider())
				{
					optionButtons[i] = new GuiSlider((ClientUtils.localize("option." + blockName + "." + option.getName()) + " ").replace("#", option.toString()), blockName, i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, "", "", (double)option.getMin(), (double)option.getMax(), (double)option.getValue(), true, true, (OptionDouble)option);
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
					this.drawHoveringText(mc.fontRendererObj.listFormattedStringToWidth(getModuleDescription(i), 150), mouseX, mouseY, mc.fontRendererObj);
				else
					this.drawHoveringText(mc.fontRendererObj.listFormattedStringToWidth(getOptionDescription(i), 150), mouseX, mouseY, mc.fontRendererObj);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = tileEntity.hasCustomName() ? tileEntity.getName() : ClientUtils.localize(tileEntity.getName(), new Object[0]);
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(ClientUtils.localize("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(new ResourceLocation("securitycraft:textures/gui/container/customize" + tileEntity.getNumberOfCustomizableOptions() + ".png"));
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(!(button instanceof GuiPictureButton)) {
			Option<?> tempOption = tileEntity.customOptions()[button.id];
			tempOption.toggle();
			button.packedFGColour = tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632;
			button.displayString = getOptionButtonTitle(tempOption);
			SecurityCraft.network.sendToServer(new PacketSToggleOption(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), button.id));
		}
	}

	private String getModuleDescription(int buttonID) {
		String moduleDescription = "module." + blockName + "." + descriptionButtons[buttonID].getItemStack().getUnlocalizedName().substring(5).replace("securitycraft:", "") + ".description";

		return ClientUtils.localize(descriptionButtons[buttonID].getItemStack().getUnlocalizedName() + ".name") + ":" + TextFormatting.RESET + "\n\n" + ClientUtils.localize(moduleDescription);
	}

	private String getOptionDescription(int buttonID) {
		String optionDescription = "option." + blockName + "." + tileEntity.customOptions()[buttonID - tileEntity.getNumberOfCustomizableOptions()].getName() + ".description";

		return ClientUtils.localize(optionDescription);
	}

	private String getOptionButtonTitle(Option<?> option) {
		return (ClientUtils.localize("option." + blockName + "." + option.getName()) + " ").replace("#", option.toString());
	}

	public List<Rectangle> getGuiExtraAreas()
	{
		List<Rectangle> rects = new ArrayList<Rectangle>();

		for(GuiButton button : optionButtons)
		{
			if(button == null)
				continue;

			rects.add(new Rectangle(button.xPosition, button.yPosition, button.width, button.height));
		}

		return rects;
	}
}