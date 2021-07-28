package net.geforcemods.securitycraft.screen;

import java.util.ArrayDeque;
import java.util.Deque;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ScrollPanel;

@OnlyIn(Dist.CLIENT)
public class EditModuleScreen extends Screen
{
	private static CompoundTag savedModule;
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/edit_module.png");
	private final TranslatableComponent editModule = Utils.localize("gui.securitycraft:editModule");
	private ItemStack module = ItemStack.EMPTY;
	private EditBox inputField;
	private IdButton addButton, removeButton, copyButton, pasteButton, clearButton;
	private int xSize = 247, ySize = 166;
	private PlayerList playerList;
	private int guiLeft;

	public EditModuleScreen(ItemStack item)
	{
		super(new TranslatableComponent(item.getDescriptionId()));

		module = item;
	}

	@Override
	public void init()
	{
		super.init();

		guiLeft = (width - xSize) / 2;

		int controlsStartX = (int)(guiLeft + xSize * (3.0F / 4.0F)) - 43;

		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addRenderableWidget(inputField = new EditBox(font, controlsStartX - 17, height / 2 - 65, 110, 15, TextComponent.EMPTY));
		addRenderableWidget(addButton = new IdButton(0, controlsStartX, height / 2 - 45, 76, 20, Utils.localize("gui.securitycraft:editModule.add"), this::actionPerformed));
		addRenderableWidget(removeButton = new IdButton(1, controlsStartX, height / 2 - 20, 76, 20, Utils.localize("gui.securitycraft:editModule.remove"), this::actionPerformed));
		addRenderableWidget(copyButton = new IdButton(2, controlsStartX, height / 2 + 5, 76, 20, Utils.localize("gui.securitycraft:editModule.copy"), this::actionPerformed));
		addRenderableWidget(pasteButton = new IdButton(3, controlsStartX, height / 2 + 30, 76, 20, Utils.localize("gui.securitycraft:editModule.paste"), this::actionPerformed));
		addRenderableWidget(clearButton = new IdButton(4, controlsStartX, height / 2 + 55, 76, 20, Utils.localize("gui.securitycraft:editModule.clear"), this::actionPerformed));
		addRenderableWidget(clearButton);
		addRenderableOnly(playerList = new PlayerList(minecraft, 110, 141, height / 2 - 66, guiLeft + 10));

		addButton.active = false;
		removeButton.active = false;

		if (module.getTag() == null || module.getTag().isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)))
			copyButton.active = false;

		if (savedModule == null || savedModule.isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)))
			pasteButton.active = false;

		if (module.getTag() == null || module.getTag().isEmpty())
			clearButton.active = false;

		inputField.setMaxLength(16);
		inputField.setFilter(s -> !s.contains(" "));
		inputField.setResponder(s -> {
			if(s.isEmpty())
				addButton.active = false;
			else
			{
				if(module.hasTag())
				{
					for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
					{
						if(s.equals(module.getTag().getString("Player" + i)))
						{
							addButton.active = false;
							removeButton.active = true;
							playerList.setSelectedIndex(i - 1);
							return;
						}
					}
				}

				addButton.active = true;
			}

			removeButton.active = false;
			playerList.setSelectedIndex(-1);
		});
		setInitialFocus(inputField);
	}

	@Override
	public void removed(){
		super.removed();

		if(minecraft != null)
			minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		blit(matrix, startX, startY, 0, 0, xSize, ySize);
		super.render(matrix, mouseX, mouseY, partialTicks);
		font.drawWordWrap(editModule, startX + xSize / 2 - font.width(editModule) / 2, startY + 6, width, 4210752);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
	{
		if(playerList != null && playerList.isMouseOver(mouseX, mouseY))
			playerList.mouseScrolled(mouseX, mouseY, scroll);

		return super.mouseScrolled(mouseX, mouseY, scroll);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(playerList != null)
			playerList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(playerList != null)
			playerList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if(playerList != null)
			playerList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	protected void actionPerformed(IdButton button){
		if(button.id == addButton.id)
		{
			if(inputField.getValue().isEmpty())
				return;

			if(module.getTag() == null)
				module.setTag(new CompoundTag());

			for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
			{
				if(module.getTag().contains("Player" + i) && module.getTag().getString("Player" + i).equals(inputField.getValue()))
				{
					if (i == 9)
						addButton.active = false;

					return;
				}
			}

			module.getTag().putString("Player" + getNextFreeSlot(module.getTag()), inputField.getValue());

			if(module.getTag() != null && module.getTag().contains("Player" + ModuleItem.MAX_PLAYERS))
				addButton.active = false;

			inputField.setValue("");
		}
		else if(button.id == removeButton.id)
		{
			if(inputField.getValue().isEmpty())
				return;

			if(module.getTag() == null)
				module.setTag(new CompoundTag());

			for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
			{
				if(module.getTag().contains("Player" + i) && module.getTag().getString("Player" + i).equals(inputField.getValue()))
				{
					module.getTag().remove("Player" + i);
					defragmentTag(module.getTag());
				}
			}

			inputField.setValue("");
		}
		else if(button.id == copyButton.id)
		{
			savedModule = module.getTag().copy();
			copyButton.active = false;
			return;
		}
		else if(button.id == pasteButton.id)
			module.setTag(savedModule.copy());
		else if(button.id == clearButton.id)
		{
			module.setTag(new CompoundTag());
			inputField.setValue("");
		}
		else return;

		if(module.getTag() != null)
			SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(module));

		addButton.active = module.getTag() != null && !module.getTag().contains("Player" + ModuleItem.MAX_PLAYERS) && !inputField.getValue().isEmpty();
		removeButton.active = !(module.getTag() == null || module.getTag().isEmpty() || inputField.getValue().isEmpty());
		copyButton.active = !(module.getTag() == null || module.getTag().isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)));
		pasteButton.active = !(savedModule == null || savedModule.isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)));
		clearButton.active = !(module.getTag() == null || module.getTag().isEmpty());
	}

	private int getNextFreeSlot(CompoundTag tag) {
		for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
		{
			if(!tag.contains("Player" + i) || tag.getString("Player" + i).isEmpty())
				return i;
		}

		return 0;
	}

	private void defragmentTag(CompoundTag tag)
	{
		Deque<Integer> freeIndices = new ArrayDeque<>();

		for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
		{
			if(!tag.contains("Player" + i) || tag.getString("Player" + i).isEmpty())
				freeIndices.add(i);
			else if(!freeIndices.isEmpty())
			{
				String player = tag.getString("Player" + i);
				int nextFreeIndex = freeIndices.poll();

				tag.putString("Player" + nextFreeIndex, player);
				tag.remove("Player" + i);
				freeIndices.add(i);
			}
		}
	}

	class PlayerList extends ScrollPanel
	{
		private final int slotHeight = 12, listLength = ModuleItem.MAX_PLAYERS;
		private int selectedIndex = -1;

		public PlayerList(Minecraft client, int width, int height, int top, int left)
		{
			super(client, width, height, top, left);
		}

		@Override
		protected int getContentHeight()
		{
			int height = 50 + (listLength * font.lineHeight);

			if(height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			if(isMouseOver(mouseX, mouseY) && mouseX < left + width - 6)
			{
				int clickedIndex = ((int)(mouseY - top + scrollDistance - border)) / slotHeight;

				if(module.hasTag() && module.getTag().contains("Player" + (clickedIndex + 1)))
				{
					selectedIndex = clickedIndex;
					inputField.setValue(module.getTag().getString("Player" + (clickedIndex + 1)));
				}
			}

			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		protected void drawPanel(PoseStack matrix, int entryRight, int relativeY, Tesselator tessellator, int mouseX, int mouseY)
		{
			if(module.hasTag())
			{
				CompoundTag tag = module.getTag();
				int baseY = top + border - (int)scrollDistance;
				int mouseListY = (int)(mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / slotHeight;

				//highlight hovered slot
				if(slotIndex != selectedIndex && mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
				{
					if(tag.contains("Player" + (slotIndex + 1)) && !tag.getString("Player" + (slotIndex + 1)).isEmpty())
						renderBox(tessellator.getBuilder(), left, entryRight - 6, baseY + slotIndex * slotHeight, slotHeight - 4, 0x80);
				}
				if(selectedIndex >= 0)
					renderBox(tessellator.getBuilder(), left, entryRight - 6, baseY + selectedIndex * slotHeight, slotHeight - 4, 0xFF);

				//draw entry strings
				for(int i = 0; i < ModuleItem.MAX_PLAYERS; i++)
				{
					if(tag.contains("Player" + (i + 1)))
					{
						String name = tag.getString("Player" + (i + 1));

						if(!name.isEmpty())
							font.draw(matrix, name, left - 2 + width / 2 - font.width(name) / 2, relativeY + (slotHeight * i), 0xC6C6C6);
					}
				}
			}
		}

		private void renderBox(BufferBuilder bufferBuilder, int min, int max, int slotTop, int slotBuffer, int borderColor)
		{
			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
			RenderSystem.defaultBlendFunc();
			bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferBuilder.vertex(min, slotTop + slotBuffer + 2, 0).uv(0, 1).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop + slotBuffer + 2, 0).uv(1, 1).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
			bufferBuilder.vertex(max, slotTop - 2, 0).uv(1, 0).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
			bufferBuilder.vertex(min, slotTop - 2, 0).uv(0, 0).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop + slotBuffer + 1, 0).uv(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop + slotBuffer + 1, 0).uv(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(max - 1, slotTop - 1, 0).uv(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.vertex(min + 1, slotTop - 1, 0).uv(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
			bufferBuilder.end();
			BufferUploader.end(bufferBuilder);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}

		public void setSelectedIndex(int selectedIndex)
		{
			this.selectedIndex = selectedIndex;
		}
	}
}
