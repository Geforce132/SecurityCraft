package net.geforcemods.securitycraft.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class GuiTrophySystem extends GuiContainer {

	private static final ResourceLocation FILTER_ENABLED_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/item_bound.png");
	private static final ResourceLocation FILTER_DISABLED_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/item_not_bound.png");
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/blank.png");
	private final String projectiles = Utils.localize("gui.securitycraft:trophy_system.targetableProjectiles").getFormattedText();
	private final String moduleRequired = Utils.localize("gui.securitycraft:trophy_system.moduleRequired").getFormattedText();
	private final String toggle = Utils.localize("gui.securitycraft:trophy_system.toggle").getFormattedText();
	private final String moddedProjectiles = Utils.localize("gui.securitycraft:trophy_system.moddedProjectiles").getFormattedText();
	private final boolean isSmart;
	private final List<EntityEntry> orderedFilterList;
	private TileEntityTrophySystem tileEntity;
	private ProjectileScrollList projectileList;

	public GuiTrophySystem(InventoryPlayer inventory, TileEntityTrophySystem te) {
		super(new ContainerGeneric(inventory, te));

		tileEntity = te;
		isSmart = tileEntity.hasModule(EnumModuleType.SMART);
		orderedFilterList = new ArrayList<>(tileEntity.getFilters().keySet());
		orderedFilterList.sort((e1, e2) -> {
			//the entry for modded projectiles always shows at the bottom of the list
			if(e1 == TileEntityTrophySystem.MODDED_PROJECTILES)
				return 1;
			else if(e2 == TileEntityTrophySystem.MODDED_PROJECTILES)
				return -1;
			else return e1.getName().compareTo(e2.getName());
		});
	}

	@Override
	public void initGui() {
		super.initGui();

		projectileList = new ProjectileScrollList(mc, xSize - 24, ySize - 60, guiTop + 40, guiLeft + 12, width, height);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String name = Utils.localize("tile.securitycraft:trophy_system.name").getFormattedText();
		fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 4210752);
		fontRenderer.drawString(projectiles, xSize / 2 - fontRenderer.getStringWidth(projectiles) / 2, 31, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(projectileList != null)
			projectileList.drawScreen(mouseX, mouseY, partialTicks);

		GuiUtils.renderSmartModuleInfo(toggle, moduleRequired, isSmart, guiLeft, guiTop, width, height, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		projectileList.handleMouseInput(mouseX, mouseY);
	}

	class ProjectileScrollList extends GuiScrollingList {
		private int hoveredSlot = -1;

		public ProjectileScrollList(Minecraft client, int width, int height, int top, int left, int screenWidth, int screenHeight)
		{
			super(client, width, height, top, top + height, left, 12, screenWidth, screenHeight);
		}

		@Override
		protected int getSize()
		{
			return orderedFilterList.size();
		}

		@Override
		protected boolean isSelected(int index)
		{
			return index == hoveredSlot;
		}

		@Override
		protected void drawBackground() {}

		@Override
		protected int getContentHeight()
		{
			int height = 50 + (getSize() * fontRenderer.FONT_HEIGHT);

			if(height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		protected void elementClicked(int index, boolean doubleClick) {
			if(isSmart) {
				tileEntity.toggleFilter(orderedFilterList.get(index));
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}
		}

		@Override
		protected void drawSlot(int slotIndex, int entryRight, int slotTop, int slotBuffer, Tessellator tess)
		{
			//highlight hovered slot
			if(isSmart && (mouseX >= left && mouseX <= entryRight && slotIndex >= 0 && slotIndex < getSize() && mouseY >= slotTop - 1 && mouseY <= slotTop + slotBuffer + 2)) {
				hoveredSlot = slotIndex;
			}
			else if (mouseX < left || mouseX > right || mouseY < top || mouseY > bottom){
				hoveredSlot = -1;
			}

			//draw entry strings and indicators whether the filter is enabled
			EntityEntry projectileType = orderedFilterList.get(slotIndex);
			String projectileName = projectileType == TileEntityTrophySystem.MODDED_PROJECTILES ? moddedProjectiles : projectileType.newInstance(mc.world).getName();

			projectileName = projectileName.replace("entity.", "").replace(".name", "");
			fontRenderer.drawString(projectileName, left + listWidth / 2 - fontRenderer.getStringWidth(projectileName) / 2, slotTop, 0xC6C6C6);
			mc.getTextureManager().bindTexture(tileEntity.getFilter(projectileType) ? FILTER_ENABLED_TEXTURE : FILTER_DISABLED_TEXTURE);
			drawModalRectWithCustomSizedTexture(left, slotTop - 2, 0, 0, 12, 12, 12, 12);
		}
	}
}
