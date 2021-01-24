package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.SyncBlockPocketManager;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.minecraftforge.fml.network.PacketDistributor;

public class BlockPocketManagerScreen extends ContainerScreen<GenericTEContainer>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager.png");
	private final TranslationTextComponent blockPocketManager = ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey());
	private final TranslationTextComponent youNeed = ClientUtils.localize("gui.securitycraft:blockPocketManager.youNeed");
	public BlockPocketManagerTileEntity te;
	private int size = 5;
	private Button toggleButton;
	private Button sizeButton;
	private Button assembleButton;
	private Button outlineButton;
	private Slider offsetSlider;
	private static final ItemStack BLOCK_POCKET_WALL = new ItemStack(SCContent.BLOCK_POCKET_WALL.get());
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get());
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get());

	public BlockPocketManagerScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);

		te = (BlockPocketManagerTileEntity)container.te;
		size = te.size;
		ySize = 194;
	}

	@Override
	public void init()
	{
		super.init();

		addButton(toggleButton = new ClickButton(0, guiLeft + xSize / 2 - 45, guiTop + ySize / 2 - 40, 90, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager." + (!te.enabled ? "activate" : "deactivate")), this::toggleButtonClicked));
		addButton(sizeButton = new ClickButton(1, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 - 70, 120, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size), this::sizeButtonClicked));
		addButton(assembleButton = new ClickButton(2, guiLeft + xSize / 2 - 45, guiTop + ySize / 2 + 23, 90, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.assemble"), this::assembleButtonClicked));
		addButton(outlineButton = new ClickButton(3, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 + 47, 120, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.outline." + (!te.showOutline ? "show" : "hide")), this::outlineButtonClicked));
		addButton(offsetSlider = new NamedSlider(ClientUtils.localize("gui.securitycraft:projector.offset", te.autoBuildOffset), StringTextComponent.EMPTY, 4, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 + 71, 120, 20, ClientUtils.localize("gui.securitycraft:projector.offset", ""), "", (-size + 2) / 2, (size - 2) / 2, te.autoBuildOffset, false, true, null, this::offsetSliderReleased));
		offsetSlider.updateSlider();

		if(!te.getOwner().isOwner(Minecraft.getInstance().player))
			sizeButton.active = toggleButton.active = assembleButton.active = outlineButton.active  = offsetSlider.active = false;
		else
			sizeButton.active = assembleButton.active = offsetSlider.active = !te.enabled;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY)
	{
		font.func_243248_b(matrix, blockPocketManager, xSize / 2 - font.getStringPropertyWidth(blockPocketManager) / 2, 6, 4210752);

		if(!te.enabled)
		{
			font.func_243248_b(matrix, youNeed, xSize / 2 - font.getStringPropertyWidth(youNeed) / 2, 83, 4210752);

			font.drawString(matrix, (size - 2) * (size - 2) * 6 + "", 42, 100, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(BLOCK_POCKET_WALL, 25, 96);

			font.drawString(matrix, (size - 2) * 12 - 1 + "", 94, 100, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 77, 96);

			font.drawString(matrix, "8", 147, 100, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 130, 96);

			if(mouseX >= guiLeft + 23 && mouseX < guiLeft + 48 && mouseY >= guiTop + 93 && mouseY < guiTop + 115)
				renderTooltip(matrix, BLOCK_POCKET_WALL, mouseX - guiLeft, mouseY - guiTop);

			if(mouseX >= guiLeft + 75 && mouseX < guiLeft + 100 && mouseY >= guiTop + 93 && mouseY < guiTop + 115)
				renderTooltip(matrix, REINFORCED_CRYSTAL_QUARTZ_PILLAR, mouseX - guiLeft, mouseY - guiTop);

			if(mouseX >= guiLeft + 128 && mouseX < guiLeft + 153 && mouseY >= guiTop + 93 && mouseY < guiTop + 115)
				renderTooltip(matrix, REINFORCED_CHISELED_CRYSTAL_QUARTZ, mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		blit(matrix, startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(offsetSlider.dragging)
			offsetSlider.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
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
				PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), feedback, TextFormatting.DARK_AQUA);
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

		te.size = size;
		offsetSlider.minValue = newMin;
		offsetSlider.maxValue = newMax;
		te.autoBuildOffset = newOffset;
		offsetSlider.setValue(newOffset);
		offsetSlider.updateSlider();
		button.setMessage(ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size));
		sync();
	}

	public void assembleButtonClicked(ClickButton button)
	{
		IFormattableTextComponent feedback;

		te.size = size;
		feedback = te.autoAssembleMultiblock(Minecraft.getInstance().player);

		if(feedback != null)
			PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), feedback, TextFormatting.DARK_AQUA);

		Minecraft.getInstance().player.closeScreen();
	}

	public void outlineButtonClicked(ClickButton button)
	{
		te.toggleOutline();
		outlineButton.setMessage(ClientUtils.localize("gui.securitycraft:blockPocketManager.outline."+ (!te.showOutline ? "show" : "hide")));
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
