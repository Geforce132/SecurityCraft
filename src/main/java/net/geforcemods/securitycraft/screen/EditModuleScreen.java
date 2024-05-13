package net.geforcemods.securitycraft.screen;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.network.server.SetListModuleData;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.ColorableScrollPanel;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.GuiScrollingList;

public class EditModuleScreen extends GuiContainer implements GuiResponder {
	private static NBTTagCompound savedModule;
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/edit_module.png");
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private final String editModule = Utils.localize("gui.securitycraft:editModule").getFormattedText();
	private final ItemStack module;
	private final List<ScorePlayerTeam> availableTeams;
	private final Map<ScorePlayerTeam, Boolean> teamsListedStatus = new HashMap<>();
	private GuiTextField inputField;
	private GuiButton addPlayerButton, removePlayerButton, copyButton, pasteButton, clearButton;
	private CallbackCheckbox affectEveryPlayerCheckbox;
	private PlayerList playerList;
	private TeamList teamList;

	public EditModuleScreen(ItemStack item, TileEntity te) {
		super(new GenericMenu(te));

		availableTeams = new ArrayList<>(Minecraft.getMinecraft().player.getWorldScoreboard().getTeams());
		module = item;
		xSize = 247;
		ySize = 211;
	}

	@Override
	public void initGui() {
		super.initGui();

		int controlsStartX = (int) (guiLeft + xSize * (3.0F / 4.0F)) - 57;
		int controlsWidth = 107;
		String checkboxText = Utils.localize("gui.securitycraft:editModule.affectEveryone").getFormattedText();
		int length = fontRenderer.getStringWidth(checkboxText) + 24; //24 = checkbox width + 4 pixels of buffer
		GuiButton editTeamsButton;

		Keyboard.enableRepeatEvents(true);
		inputField = new GuiTextField(5, fontRenderer, controlsStartX, height / 2 - 88, controlsWidth, 15) {
			@Override
			public boolean textboxKeyTyped(char typedChar, int keyCode) {
				if (isFocused() && (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER)) {
					actionPerformed(addPlayerButton);
					return true;
				}

				return super.textboxKeyTyped(typedChar, keyCode);
			}
		};
		addPlayerButton = addButton(new GuiButton(0, controlsStartX, height / 2 - 68, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.add_player").getFormattedText()));
		removePlayerButton = addButton(new GuiButton(1, controlsStartX, height / 2 - 43, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.remove_player").getFormattedText()));
		editTeamsButton = addButton(new ToggleComponentButton(2, controlsStartX, height / 2 - 18, controlsWidth, 20, i -> Utils.localize("gui.securitycraft:editModule.edit_teams").getFormattedText(), 0, 2, b -> {}));
		copyButton = addButton(new GuiButton(3, controlsStartX, height / 2 + 7, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.copy").getFormattedText()));
		pasteButton = addButton(new GuiButton(4, controlsStartX, height / 2 + 32, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.paste").getFormattedText()));
		clearButton = addButton(new GuiButton(5, controlsStartX, height / 2 + 57, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.clear").getFormattedText()));
		affectEveryPlayerCheckbox = addButton(new CallbackCheckbox(6, guiLeft + xSize / 2 - length / 2, guiTop + ySize - 25, 20, 20, checkboxText, module.hasTagCompound() && module.getTagCompound().getBoolean("affectEveryone"), newState -> {
			if (!module.hasTagCompound())
				module.setTagCompound(new NBTTagCompound());

			module.getTagCompound().setBoolean("affectEveryone", newState);
		}, 0x404040));
		playerList = new PlayerList(mc, 110, 165, height / 2 - 88, guiLeft + 10, width, height);
		teamList = new TeamList(mc, editTeamsButton.getButtonWidth(), 75, editTeamsButton.y + editTeamsButton.height, editTeamsButton.x);

		teamList.active = false;
		editTeamsButton.enabled = !availableTeams.isEmpty();
		refreshFromNbt();
		updateButtonStates();
		inputField.setGuiResponder(this);
		inputField.setMaxStringLength(16);
		inputField.setFocused(true);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if (!module.hasTagCompound())
			module.setTagCompound(new NBTTagCompound());

		SecurityCraft.network.sendToServer(new SetListModuleData(module.getTagCompound()));
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		inputField.drawTextBox();

		if (playerList != null)
			playerList.drawScreen(mouseX, mouseY, partialTicks);

		if (teamList != null)
			teamList.drawScreen(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawSplitString(editModule, xSize / 2 - fontRenderer.getStringWidth(editModule) / 2, 6, xSize, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode != Keyboard.KEY_ESCAPE && inputField.isFocused()) {
			if (keyCode == Keyboard.KEY_SPACE)
				return;

			inputField.textboxKeyTyped(typedChar, keyCode);
		}
		else
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		inputField.mouseClicked(mouseX, mouseY, mouseButton);

		if (teamList != null)
			teamList.mouseClicked(mouseX, mouseY);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
			case 0: //add
				if (inputField.getText().isEmpty())
					return;

				if (module.getTagCompound() == null)
					module.setTagCompound(new NBTTagCompound());

				for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
					if (module.getTagCompound().hasKey("Player" + i) && module.getTagCompound().getString("Player" + i).equals(inputField.getText())) {
						if (i == 9)
							addPlayerButton.enabled = false;

						return;
					}
				}

				module.getTagCompound().setString("Player" + getNextSlot(module.getTagCompound()), inputField.getText());

				if (module.getTagCompound() != null && module.getTagCompound().hasKey("Player" + ModuleItem.MAX_PLAYERS))
					addPlayerButton.enabled = false;

				inputField.setText("");
				updateButtonStates();
				break;
			case 1: //remove
				if (inputField.getText().isEmpty())
					return;

				if (module.getTagCompound() == null)
					module.setTagCompound(new NBTTagCompound());

				for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
					if (module.getTagCompound().hasKey("Player" + i) && module.getTagCompound().getString("Player" + i).equals(inputField.getText()))
						module.getTagCompound().removeTag("Player" + i);
				}

				inputField.setText("");
				defragmentTag(module.getTagCompound());
				updateButtonStates();
				break;
			case 2: //edit teams
				ToggleComponentButton tcb = (ToggleComponentButton) button;
				boolean buttonState;

				tcb.cycleIndex(1);
				buttonState = tcb.getCurrentIndex() == 0;
				copyButton.visible = pasteButton.visible = clearButton.visible = buttonState;
				teamList.active = !buttonState;
				break;
			case 3: //copy
				savedModule = module.getTagCompound().copy();
				copyButton.enabled = false;
				updateButtonStates();
				return;
			case 4: //paste
				module.setTagCompound(savedModule.copy());
				updateButtonStates();
				refreshFromNbt();
				break;
			case 5: //clear
				module.setTagCompound(new NBTTagCompound());
				inputField.setText("");
				updateButtonStates(true);
				refreshFromNbt();
				break;
			case 6: //checkbox
				((CallbackCheckbox) button).onClick();
				break;
			default:
				return;
		}
	}

	private void updateButtonStates() {
		updateButtonStates(false);
	}

	private void updateButtonStates(boolean cleared) {
		if (!module.hasTagCompound())
			module.setTagCompound(new NBTTagCompound());

		NBTTagCompound tag = module.getTagCompound();
		boolean tagIsConsideredEmpty = tag.isEmpty() || (tag.getSize() == 1 && tag.hasKey("affectEveryone"));

		if (!cleared && tagIsConsideredEmpty) {
			addPlayerButton.enabled = false;
			removePlayerButton.enabled = false;
		}
		else {
			addPlayerButton.enabled = !tag.hasKey("Player" + ModuleItem.MAX_PLAYERS) && !inputField.getText().isEmpty();
			removePlayerButton.enabled = !inputField.getText().isEmpty();
		}

		copyButton.enabled = !tagIsConsideredEmpty && !tag.equals(savedModule);
		pasteButton.enabled = savedModule != null && !savedModule.isEmpty() && !tag.equals(savedModule);
		clearButton.enabled = !tagIsConsideredEmpty;
	}

	private void refreshFromNbt() {
		if (!module.hasTagCompound()) {
			availableTeams.forEach(team -> teamsListedStatus.put(team, false));
			affectEveryPlayerCheckbox.setSelected(false);
		}
		else {
			NBTTagCompound tag = module.getTagCompound();
			//@formatter:off
			List<String> teamNames = StreamSupport.stream(tag.getTagList("ListedTeams", Constants.NBT.TAG_STRING).spliterator(), false)
					.filter(NBTTagString.class::isInstance)
					.map(e -> ((NBTTagString) e).getString())
					.collect(Collectors.toList());
			//@formatter:on

			availableTeams.forEach(team -> teamsListedStatus.put(team, teamNames.contains(team.getName())));
			affectEveryPlayerCheckbox.setSelected(tag.getBoolean("affectEveryone"));
		}
	}

	private int getNextSlot(NBTTagCompound tag) {
		for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
			if (!tag.hasKey("Player" + i) || tag.getString("Player" + i).isEmpty())
				return i;
		}

		return 0;
	}

	private void defragmentTag(NBTTagCompound tag) {
		Deque<Integer> freeIndices = new ArrayDeque<>();

		for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
			if (!tag.hasKey("Player" + i) || tag.getString("Player" + i).isEmpty())
				freeIndices.add(i);
			else if (!freeIndices.isEmpty()) {
				String player = tag.getString("Player" + i);
				int nextFreeIndex = freeIndices.poll();

				tag.setString("Player" + nextFreeIndex, player);
				tag.removeTag("Player" + i);
				freeIndices.add(i);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		playerList.handleMouseInput(mouseX, mouseY);
		teamList.handleMouseInput(mouseX, mouseY);
		setEntryValue(inputField.getId(), inputField.getText());
	}

	@Override
	public void setEntryValue(int id, String text) {
		if (text.isEmpty())
			addPlayerButton.enabled = false;
		else {
			if (module.hasTagCompound()) {
				for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
					if (text.equals(module.getTagCompound().getString("Player" + i))) {
						addPlayerButton.enabled = false;
						removePlayerButton.enabled = true;
						playerList.setSelectedIndex(i - 1);
						return;
					}
				}
			}

			addPlayerButton.enabled = true;
		}

		removePlayerButton.enabled = false;
		playerList.setSelectedIndex(-1);
	}

	@Override
	public void setEntryValue(int id, boolean value) {}

	@Override
	public void setEntryValue(int id, float value) {}

	class PlayerList extends GuiScrollingList {
		public PlayerList(Minecraft client, int width, int height, int top, int left, int screenWidth, int screenHeight) {
			super(client, width, height, top, top + height, left, 12, screenWidth, screenHeight);
		}

		@Override
		protected int getSize() {
			return ModuleItem.MAX_PLAYERS;
		}

		@Override
		protected void elementClicked(int index, boolean doubleClick) {
			if (module.hasTagCompound() && module.getTagCompound().hasKey("Player" + (index + 1)))
				inputField.setText(module.getTagCompound().getString("Player" + (index + 1)));
		}

		@Override
		protected boolean isSelected(int index) {
			return index == selectedIndex && module.hasTagCompound() && module.getTagCompound().hasKey("Player" + (index + 1)) && !module.getTagCompound().getString("Player" + (index + 1)).isEmpty();
		}

		@Override
		protected void drawBackground() {}

		@Override
		protected void drawSlot(int slotIndex, int entryRight, int slotTop, int slotBuffer, Tessellator tessellator) {
			if (module.hasTagCompound()) {
				NBTTagCompound tag = module.getTagCompound();

				slotBuffer--;

				//highlighted selected slot
				if (isSelected(slotIndex))
					renderBox(tessellator, left, entryRight + 1, slotTop, slotBuffer, 0xFF);
				else if (mouseX >= left && mouseX <= entryRight && slotIndex >= 0 && slotIndex < getSize() && mouseY >= slotTop - 1 && mouseY <= slotTop + slotBuffer + 2) {
					if (tag.hasKey("Player" + (slotIndex + 1)) && !tag.getString("Player" + (slotIndex + 1)).isEmpty())
						renderBox(tessellator, left, entryRight + 1, slotTop, slotBuffer, 0x80);
				}

				//draw name
				if (tag.hasKey("Player" + (slotIndex + 1))) {
					String name = tag.getString("Player" + (slotIndex + 1));

					if (!name.isEmpty())
						fontRenderer.drawString(name, width / 2 - 110 + listWidth / 2 - fontRenderer.getStringWidth(name) / 2 - 4, slotTop, 0xC6C6C6);
				}
			}
		}

		public void setSelectedIndex(int selectedIndex) {
			this.selectedIndex = selectedIndex;
		}
	}

	class TeamList extends ColorableScrollPanel {
		private final int listLength;
		private final FontRenderer font;
		private boolean active = true;

		public TeamList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);

			listLength = availableTeams.size();
			font = mc.fontRenderer;
		}

		@Override
		public int getSize() {
			return listLength;
		}

		@Override
		public int getContentHeight() {
			int height = getSize() * (font.FONT_HEIGHT + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		public boolean mouseClicked(int mouseX, int mouseY) {
			if (active) {
				int mouseListY = (int) (mouseY - top + scrollDistance - BORDER);
				int slotIndex = mouseListY / slotHeight;

				if (slotIndex >= 0 && slotIndex < listLength && mouseY >= 0 && mouseX < scrollBarLeft) {
					toggleTeam(availableTeams.get(slotIndex));
					mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					return true;
				}
			}

			return false;
		}

		@Override
		public void drawScreen(int mouseX, int mouseY) {
			if (active) {
				super.drawScreen(mouseX, mouseY);

				//draw tooltip for long team names
				int mouseListY = (int) (mouseY - top + scrollDistance - (BORDER / 2));
				int slotIndex = mouseListY / slotHeight;

				if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
					String name = availableTeams.get(slotIndex).getDisplayName();
					int length = font.getStringWidth(name);
					int baseY = top + BORDER - (int) scrollDistance;

					if (length >= width - 6) //6 = barWidth
						drawHoveringText(name, left + 3, baseY + (slotHeight * slotIndex + slotHeight));
				}
			}
		}

		@Override
		public void drawPanel(int entryRight, int relativeY, Tessellator tessellator, int mouseX, int mouseY) {
			int baseY = top + BORDER - (int) scrollDistance;
			int mouseListY = (int) (mouseY - top + scrollDistance - (BORDER / 2));
			int slotIndex = mouseListY / slotHeight;

			//highlight hovered slot
			if (slotIndex != selectedIndex && mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
				renderBox(tessellator, left, entryRight - 6, baseY + slotIndex * slotHeight, slotHeight - 4, 0x80);

			//draw entry strings and indicators whether the filter is enabled
			for (int i = 0; i < listLength; i++) {
				int yStart = relativeY + (slotHeight * i);
				ScorePlayerTeam team = availableTeams.get(i);

				font.drawString(team.getDisplayName(), left + 15, yStart, 0xC6C6C6);
				mc.getTextureManager().bindTexture(BEACON_GUI);
				drawScaledCustomSizeModalRect(left, yStart - 3, teamsListedStatus.get(team) ? 88 : 110, 219, 21, 22, 14, 14, 256, 256);
			}
		}

		private void toggleTeam(ScorePlayerTeam teamToAdd) {
			NBTTagList listedTeams = new NBTTagList();

			if (!module.hasTagCompound())
				module.setTagCompound(new NBTTagCompound());

			teamsListedStatus.put(teamToAdd, !teamsListedStatus.get(teamToAdd));
			teamsListedStatus.forEach((team, listed) -> {
				if (listed)
					listedTeams.appendTag(new NBTTagString(team.getName()));
			});
			module.getTagCompound().setTag("ListedTeams", listedTeams);
			updateButtonStates();
		}
	}

	private void renderBox(Tessellator tessellator, int min, int max, int slotTop, int slotBuffer, int borderColor) {
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		GlStateManager.disableTexture2D();
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferBuilder.pos(min, slotTop + slotBuffer + 3, 0).tex(0, 1).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
		bufferBuilder.pos(max, slotTop + slotBuffer + 3, 0).tex(1, 1).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
		bufferBuilder.pos(max, slotTop - 2, 0).tex(1, 0).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
		bufferBuilder.pos(min, slotTop - 2, 0).tex(0, 0).color(borderColor, borderColor, borderColor, 0xFF).endVertex();
		bufferBuilder.pos(min + 1, slotTop + slotBuffer + 2, 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
		bufferBuilder.pos(max - 1, slotTop + slotBuffer + 2, 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
		bufferBuilder.pos(max - 1, slotTop - 1, 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
		bufferBuilder.pos(min + 1, slotTop - 1, 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
	}
}
