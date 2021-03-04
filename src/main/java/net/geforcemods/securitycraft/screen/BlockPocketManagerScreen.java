package net.geforcemods.securitycraft.screen;

import java.util.Arrays;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.BlockPocketManagerContainer;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.StackHoverChecker;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.minecraftforge.fml.network.PacketDistributor;

public class BlockPocketManagerScreen extends ContainerScreen<BlockPocketManagerContainer>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager.png");
	private static final ResourceLocation TEXTURE_STORAGE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager_storage.png");
	private static final ItemStack BLOCK_POCKET_WALL = new ItemStack(SCContent.BLOCK_POCKET_WALL.get());
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get());
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get());
	private final String blockPocketManager = ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()).getFormattedText();
	private final String youNeed = ClientUtils.localize("gui.securitycraft:blockPocketManager.youNeed").getFormattedText();
	private final boolean storage;
	private final boolean isOwner;
	private final int[] materialCounts = new int[3];
	public BlockPocketManagerTileEntity te;
	private int size = 5;
	private Button toggleButton;
	private Button sizeButton;
	private Button assembleButton;
	private Button outlineButton;
	private Slider offsetSlider;
	private StackHoverChecker[] hoverCheckers = new StackHoverChecker[3];
	private StringHoverChecker assembleHoverChecker;
	private int wallsNeededOverall = (size - 2) * (size - 2) * 6;
	private int pillarsNeededOverall = (size - 2) * 12 - 1;
	private final int chiseledNeededOverall = 8;
	private int wallsStillNeeded;
	private int pillarsStillNeeded;
	private int chiseledStillNeeded;

	public BlockPocketManagerScreen(BlockPocketManagerContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);

		te = container.te;
		size = te.size;
		isOwner = container.isOwner;
		storage = container.storage;

		if(storage)
			xSize = 256;

		ySize = !storage ? 194 : 240;
	}

	@Override
	public void init()
	{
		super.init();

		int width = storage ? 123 : xSize;
		int widgetWidth = storage ? 110 : 120;
		int widgetOffset = widgetWidth / 2;
		int[] yOffset = storage ? new int[]{-76, -100, -52, -28, -4} : new int[]{-40, -70, 23, 47, 71};

		addButton(toggleButton = new ClickButton(0, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[0], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager." + (!te.enabled ? "activate" : "deactivate")).getFormattedText(), this::toggleButtonClicked));
		addButton(sizeButton = new ClickButton(1, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[1], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size).getFormattedText(), this::sizeButtonClicked));
		addButton(assembleButton = new ClickButton(2, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[2], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.assemble").getFormattedText(), this::assembleButtonClicked));
		addButton(outlineButton = new ClickButton(3, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[3], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.outline." + (!te.showOutline ? "show" : "hide")).getFormattedText(), this::outlineButtonClicked));
		addButton(offsetSlider = new NamedSlider(ClientUtils.localize("gui.securitycraft:projector.offset", te.autoBuildOffset).getFormattedText(), "", 4, guiLeft + width / 2 - widgetOffset, guiTop + ySize / 2 + yOffset[4], widgetWidth, 20, ClientUtils.localize("gui.securitycraft:projector.offset", "").getFormattedText(), "", (-size + 2) / 2, (size - 2) / 2, te.autoBuildOffset, false, true, null, this::offsetSliderReleased));
		offsetSlider.updateSlider();

		if(!te.getOwner().isOwner(Minecraft.getInstance().player))
			sizeButton.active = toggleButton.active = assembleButton.active = outlineButton.active  = offsetSlider.active = false;
		else
		{
			updateMaterialInformation(true);
			sizeButton.active = offsetSlider.active = !te.enabled;
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
		font.drawString(blockPocketManager, (storage ? 123 : xSize) / 2 - font.getStringWidth(blockPocketManager) / 2, 6, 4210752);

		if(storage)
		{
			font.drawString(playerInventory.getDisplayName().getFormattedText(), 8, ySize - 94, 4210752);
			renderHoveredToolTip(mouseX - guiLeft, mouseY - guiTop);
		}

		if(!te.enabled && isOwner)
		{
			if(!storage)
			{
				font.drawString(youNeed, xSize / 2 - font.getStringWidth(youNeed) / 2, 83, 4210752);

				font.drawString(wallsNeededOverall + "", 42, 100, 4210752);
				minecraft.getItemRenderer().renderItemAndEffectIntoGUI(BLOCK_POCKET_WALL, 25, 96);

				font.drawString(pillarsNeededOverall + "", 94, 100, 4210752);
				minecraft.getItemRenderer().renderItemAndEffectIntoGUI(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 77, 96);

				font.drawString(chiseledNeededOverall + "", 147, 100, 4210752);
				minecraft.getItemRenderer().renderItemAndEffectIntoGUI(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 130, 96);
			}
			else
			{
				font.drawString(youNeed, 169 + 87 / 2 - font.getStringWidth(youNeed) / 2, ySize - 83, 4210752);

				font.drawString(Math.max(0, wallsStillNeeded) + "", 192, ySize - 66, 4210752);
				minecraft.getItemRenderer().renderItemAndEffectIntoGUI(BLOCK_POCKET_WALL, 175, ySize - 70);

				font.drawString(Math.max(0, pillarsStillNeeded) + "", 192, ySize - 44, 4210752);
				minecraft.getItemRenderer().renderItemAndEffectIntoGUI(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 175, ySize - 48);

				font.drawString(Math.max(0, chiseledStillNeeded) + "", 192, ySize - 22, 4210752);
				minecraft.getItemRenderer().renderItemAndEffectIntoGUI(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 175, ySize - 26);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.render(mouseX, mouseY, partialTicks);

		for(StackHoverChecker shc : hoverCheckers)
		{
			if(shc.checkHover(mouseX, mouseY))
			{
				renderTooltip(shc.getStack(), mouseX, mouseY);
				return;
			}
		}

		if(!te.enabled && isOwner && !assembleButton.active && assembleHoverChecker.checkHover(mouseX, mouseY))
		{
			if(!storage)
				GuiUtils.drawHoveringText(assembleHoverChecker.getLines().subList(0, 1), mouseX, mouseY, width, height, -1, font);
			else
				GuiUtils.drawHoveringText(assembleHoverChecker.getLines().subList(1, 2), mouseX, mouseY, width, height, -1, font);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(storage ? TEXTURE_STORAGE : TEXTURE);
		blit(startX, startY, 0, 0, xSize, ySize);
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
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(offsetSlider.dragging)
			offsetSlider.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}


	private void updateMaterialInformation(boolean recalculateStoredStacks)
	{
		if(recalculateStoredStacks)
		{
			materialCounts[0] = materialCounts[1] = materialCounts[2] = 0;

			te.getStorageHandler().ifPresent(handler -> {
				for(int i = 0; i < handler.getSlots(); i++)
				{
					ItemStack stack = handler.getStackInSlot(i);

					if(stack.getItem() instanceof BlockItem)
					{
						Block block = ((BlockItem)stack.getItem()).getBlock();

						if(block == SCContent.BLOCK_POCKET_WALL.get())
							materialCounts[0] += stack.getCount();
						else if(block == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
							materialCounts[1] += stack.getCount();
						else if(block == SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get())
							materialCounts[2] += stack.getCount();
					}
				}
			});
		}

		wallsNeededOverall = (size - 2) * (size - 2) * 6;
		pillarsNeededOverall = (size - 2) * 12 - 1;
		wallsStillNeeded = wallsNeededOverall - materialCounts[0];
		pillarsStillNeeded = pillarsNeededOverall - materialCounts[1];
		chiseledStillNeeded = chiseledNeededOverall - materialCounts[2];
		//the assemble button should always be active when the player is in creative mode
		assembleButton.active = isOwner && (minecraft.player.isCreative() || (!te.enabled && storage && wallsStillNeeded <= 0 && pillarsStillNeeded <= 0 && chiseledStillNeeded <= 0));
	}

	public void toggleButtonClicked(ClickButton button)
	{
		if(te.enabled)
			te.disableMultiblock();
		else
		{
			TranslationTextComponent feedback;

			te.size = size;
			feedback = te.enableMultiblock();

			if(feedback != null)
				PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), feedback, TextFormatting.DARK_AQUA, true);
		}

		Minecraft.getInstance().player.closeScreen();
	}

	public void sizeButtonClicked(ClickButton button)
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
		button.setMessage(ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size).getFormattedText());
		sync();
	}

	public void assembleButtonClicked(ClickButton button)
	{
		ITextComponent feedback;

		te.size = size;
		feedback = te.autoAssembleMultiblock(Minecraft.getInstance().player);

		if(feedback != null)
			PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), feedback, TextFormatting.DARK_AQUA, true);

		Minecraft.getInstance().player.closeScreen();
	}

	public void outlineButtonClicked(ClickButton button)
	{
		te.toggleOutline();
		outlineButton.setMessage(ClientUtils.localize("gui.securitycraft:blockPocketManager.outline."+ (!te.showOutline ? "show" : "hide")).getFormattedText());
		sync();
	}

	public void offsetSliderReleased(Slider slider)
	{
		te.autoBuildOffset = slider.getValueInt();
		sync();
	}

	private void sync()
	{
		SecurityCraft.channel.send(PacketDistributor.SERVER.noArg(), new SyncBlockPocketManager(te.getPos(), te.size, te.showOutline, te.autoBuildOffset));
	}
}
