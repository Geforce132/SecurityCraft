package net.geforcemods.securitycraft.gui;

import java.io.IOException;
import java.util.Arrays;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerBlockPocketManager;
import net.geforcemods.securitycraft.gui.components.GuiSlider;
import net.geforcemods.securitycraft.gui.components.GuiSlider.ISlider;
import net.geforcemods.securitycraft.gui.components.StackHoverChecker;
import net.geforcemods.securitycraft.gui.components.StringHoverChecker;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.IItemHandler;

public class GuiBlockPocketManager extends GuiContainer implements ISlider
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager.png");
	private static final ResourceLocation TEXTURE_STORAGE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager_storage.png");
	private static final ItemStack BLOCK_POCKET_WALL = new ItemStack(SCContent.blockPocketWall);
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 1);
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 2);
	private final String blockPocketManager = ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name").getFormattedText();
	private final String youNeed = ClientUtils.localize("gui.securitycraft:blockPocketManager.youNeed").getFormattedText();
	private final boolean storage;
	private final boolean isOwner;
	private final int[] materialCounts = new int[3];
	private final InventoryPlayer playerInventory;
	public TileEntityBlockPocketManager te;
	private int size = 5;
	private GuiButton toggleButton;
	private GuiButton sizeButton;
	private GuiButton assembleButton;
	private GuiButton outlineButton;
	private GuiSlider offsetSlider;
	private StackHoverChecker[] hoverCheckers = new StackHoverChecker[3];
	private StringHoverChecker assembleHoverChecker;
	private int wallsNeededOverall = (size - 2) * (size - 2) * 6;
	private int pillarsNeededOverall = (size - 2) * 12 - 1;
	private final int chiseledNeededOverall = 8;
	private int wallsStillNeeded;
	private int pillarsStillNeeded;
	private int chiseledStillNeeded;

	public GuiBlockPocketManager(InventoryPlayer inventory, TileEntityBlockPocketManager te)
	{
		super(new ContainerBlockPocketManager(inventory, te));

		this.te = te;
		playerInventory = inventory;
		size = te.size;
		isOwner = te.getOwner().isOwner(inventory.player);
		storage = te != null && te.hasModule(EnumModuleType.STORAGE) && isOwner;

		if(storage)
			xSize = 256;

		ySize = !storage ? 194 : 240;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int width = storage ? 123 : xSize;
		int widgetWidth = storage ? 110 : 120;
		int widgetOffset = widgetWidth / 2;
		int[] yOffset = storage ? new int[]{-76, -100, -52, -28, -4} : new int[]{-40, -70, 23, 47, 71};

		buttonList.add(toggleButton = new GuiButton(0, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[0], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager." + (!te.enabled ? "activate" : "deactivate")).getFormattedText()));
		buttonList.add(sizeButton = new GuiButton(1, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[1], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size).getFormattedText()));
		buttonList.add(assembleButton = new GuiButton(2, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[2], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.assemble").getFormattedText()));
		buttonList.add(outlineButton = new GuiButton(3, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[3], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.outline." + (!te.showOutline ? "show" : "hide")).getFormattedText()));
		buttonList.add(offsetSlider = new GuiSlider(ClientUtils.localize("gui.securitycraft:projector.offset", te.autoBuildOffset).getFormattedText(), "", 4, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[4], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:projector.offset", "").getFormattedText(), (-size + 2) / 2, (size - 2) / 2, te.autoBuildOffset, false, true, this));
		offsetSlider.updateSlider();

		if(!te.getOwner().isOwner(Minecraft.getMinecraft().player))
			sizeButton.enabled = toggleButton.enabled = assembleButton.enabled = outlineButton.enabled = offsetSlider.enabled = false;
		else
		{
			updateMaterialInformation(true);
			sizeButton.enabled = offsetSlider.enabled = !te.enabled;
		}

		if(!storage)
		{
			hoverCheckers[0] = new StackHoverChecker(BLOCK_POCKET_WALL, guiTop + 93, guiTop + 113, guiLeft + 23, guiLeft + 43);
			hoverCheckers[1] = new StackHoverChecker(REINFORCED_CRYSTAL_QUARTZ_PILLAR, guiTop + 93, guiTop + 113, guiLeft + 75, guiLeft + 95);
			hoverCheckers[2] = new StackHoverChecker(REINFORCED_CHISELED_CRYSTAL_QUARTZ, guiTop + 93, guiTop + 113, guiLeft + 128, guiLeft + 148);
		}
		else
		{
			hoverCheckers[0] = new StackHoverChecker(BLOCK_POCKET_WALL, guiTop + ySize - 73, guiTop + ySize - 54, guiLeft + 174, guiLeft + 191);
			hoverCheckers[1] = new StackHoverChecker(REINFORCED_CRYSTAL_QUARTZ_PILLAR, guiTop + ySize - 50, guiTop + ySize - 31, guiLeft + 174, guiLeft + 191);
			hoverCheckers[2] = new StackHoverChecker(REINFORCED_CHISELED_CRYSTAL_QUARTZ, guiTop + ySize - 27, guiTop + ySize - 9, guiLeft + 174, guiLeft + 191);
		}

		assembleHoverChecker = new StringHoverChecker(assembleButton, Arrays.asList(ClientUtils.localize("gui.securitycraft:blockPocketManager.needStorageModule").getFormattedText(), ClientUtils.localize("messages.securitycraft:blockpocket.notEnoughItems").getFormattedText()));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(blockPocketManager, (storage ? 123 : xSize) / 2 - fontRenderer.getStringWidth(blockPocketManager) / 2, 6, 4210752);

		if(!te.enabled && isOwner)
		{
			if(!storage)
			{
				fontRenderer.drawString(youNeed, xSize / 2 - fontRenderer.getStringWidth(youNeed) / 2, 83, 4210752);

				fontRenderer.drawString(wallsNeededOverall + "", 42, 100, 4210752);
				GuiUtils.drawItemStackToGui(BLOCK_POCKET_WALL, 25, 96, false);

				fontRenderer.drawString(pillarsNeededOverall + "", 94, 100, 4210752);
				GuiUtils.drawItemStackToGui(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 77, 96, false);

				fontRenderer.drawString(chiseledNeededOverall + "", 147, 100, 4210752);
				GuiUtils.drawItemStackToGui(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 130, 96, false);
			}
			else
			{
				fontRenderer.drawString(youNeed, 169 + 87 / 2 - fontRenderer.getStringWidth(youNeed) / 2, ySize - 83, 4210752);

				fontRenderer.drawString(Math.max(0, wallsStillNeeded) + "", 192, ySize - 66, 4210752);
				GuiUtils.drawItemStackToGui(BLOCK_POCKET_WALL, 175, ySize - 70, false);

				fontRenderer.drawString(Math.max(0, pillarsStillNeeded) + "", 192, ySize - 44, 4210752);
				GuiUtils.drawItemStackToGui(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 175, ySize - 48, false);

				fontRenderer.drawString(Math.max(0, chiseledStillNeeded) + "", 192, ySize - 22, 4210752);
				GuiUtils.drawItemStackToGui(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 175, ySize - 26, false);
			}
		}

		if(storage)
		{
			fontRenderer.drawString(playerInventory.getDisplayName().getFormattedText(), 8, ySize - 94, 4210752);
			renderHoveredToolTip(mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		for(StackHoverChecker shc : hoverCheckers)
		{
			if(shc.checkHover(mouseX, mouseY))
			{
				renderToolTip(shc.getStack(), mouseX, mouseY);
				return;
			}
		}

		if(!te.enabled && isOwner && !assembleButton.enabled && assembleHoverChecker.checkHover(mouseX, mouseY))
		{
			if(!storage)
				net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(assembleHoverChecker.getLines().subList(0, 1), mouseX, mouseY, width, height, -1, fontRenderer);
			else
				net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(assembleHoverChecker.getLines().subList(1, 2), mouseX, mouseY, width, height, -1, fontRenderer);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(storage ? TEXTURE_STORAGE : TEXTURE);
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type)
	{
		//the super call needs to be before calculating the stored materials, as it is responsible for putting the stack inside the slot
		super.handleMouseClick(slot, slotId, mouseButton, type);
		//every time items are added/removed, the mouse is clicking a slot and these values are recomputed
		//not the best place, as this code will run when an empty slot is clicked while not holding any item, but it's good enough
		updateMaterialInformation(true);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(button.id == toggleButton.id)
		{
			if(te.enabled)
				te.disableMultiblock();
			else
			{
				TextComponentTranslation feedback;

				te.size = size;
				feedback = te.enableMultiblock();

				if(feedback != null)
					PlayerUtils.sendMessageToPlayer(Minecraft.getMinecraft().player, ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), feedback, TextFormatting.DARK_AQUA, true);
			}

			Minecraft.getMinecraft().player.closeScreen();
		}
		else if(button.id == sizeButton.id)
		{
			int newOffset;
			int newMin;
			int newMax;

			size += 4;

			if(size > 25)
				size = 5;

			newMin = (-size + 2) / 2;
			newMax = (size - 2) / 2;

			if(te.autoBuildOffset > 0)
				newOffset = Math.min(te.autoBuildOffset, newMax);
			else
				newOffset = Math.max(te.autoBuildOffset, newMin);

			updateMaterialInformation(false);
			te.size = size;
			offsetSlider.minValue = newMin;
			offsetSlider.maxValue = newMax;
			te.autoBuildOffset = newOffset;
			offsetSlider.setValue(newOffset);
			offsetSlider.updateSlider();
			button.displayString = ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size).getFormattedText();
			sync();
		}
		else if(button.id == assembleButton.id)
		{
			ITextComponent feedback;

			te.size = size;
			feedback = te.autoAssembleMultiblock();

			if(feedback != null)
				PlayerUtils.sendMessageToPlayer(Minecraft.getMinecraft().player, ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), feedback, TextFormatting.DARK_AQUA);

			Minecraft.getMinecraft().player.closeScreen();
		}
		else if(button.id == outlineButton.id)
		{
			te.toggleOutline();
			outlineButton.displayString = ClientUtils.localize("gui.securitycraft:blockPocketManager.outline."+ (!te.showOutline ? "show" : "hide")).getFormattedText();
			sync();
		}
	}

	private void sync()
	{
		SecurityCraft.network.sendToServer(new SyncBlockPocketManager(te.getPos(), te.size, te.showOutline, te.autoBuildOffset));
	}

	@Override
	public void onMouseRelease(int id)
	{
		if(id == offsetSlider.id)
		{
			te.autoBuildOffset = offsetSlider.getValueInt();
			sync();
		}
	}

	private void updateMaterialInformation(boolean recalculateStoredStacks)
	{
		if(recalculateStoredStacks)
		{
			materialCounts[0] = materialCounts[1] = materialCounts[2] = 0;

			IItemHandler handler = te.getStorageHandler();

			for(int i = 0; i < handler.getSlots(); i++)
			{
				ItemStack stack = handler.getStackInSlot(i);

				if(stack.getItem() instanceof ItemBlock)
				{
					Block block = ((ItemBlock)stack.getItem()).getBlock();

					if(block == SCContent.blockPocketWall)
						materialCounts[0] += stack.getCount();
					else if(block == SCContent.reinforcedCrystalQuartz && stack.getMetadata() >= 2)
						materialCounts[1] += stack.getCount();
					else if(block == SCContent.reinforcedCrystalQuartz && stack.getMetadata() == 1)
						materialCounts[2] += stack.getCount();
				}
			}
		}

		wallsNeededOverall = (size - 2) * (size - 2) * 6;
		pillarsNeededOverall = (size - 2) * 12 - 1;
		wallsStillNeeded = wallsNeededOverall - materialCounts[0];
		pillarsStillNeeded = pillarsNeededOverall - materialCounts[1];
		chiseledStillNeeded = chiseledNeededOverall - materialCounts[2];
		//the assemble button should always be active when the player is in creative mode
		assembleButton.enabled = isOwner && (mc.player.isCreative() || (!te.enabled && storage && wallsStillNeeded <= 0 && pillarsStillNeeded <= 0 && chiseledStillNeeded <= 0));
	}

	@Override
	public void onChangeSliderValue(GuiSlider slider, String blockName, int id)
	{
		if(slider.id == offsetSlider.id)
			slider.displayString = slider.prefix + slider.getValueInt();
	}
}
