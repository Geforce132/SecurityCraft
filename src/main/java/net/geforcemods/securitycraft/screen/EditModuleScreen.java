package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.ListModuleData;
import net.geforcemods.securitycraft.network.server.SetListModuleData;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class EditModuleScreen extends Screen {
	private static ListModuleData savedData;
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/gui/container/edit_module.png");
	private static final ResourceLocation CONFIRM_SPRITE = SecurityCraft.mcResLoc("container/beacon/confirm");
	private static final ResourceLocation CANCEL_SPRITE = SecurityCraft.mcResLoc("container/beacon/cancel");
	private final Component editModule = Utils.localize("gui.securitycraft:editModule");
	private final ItemStack module;
	private final List<PlayerTeam> availableTeams;
	private final Map<PlayerTeam, Boolean> teamsListedStatus = new HashMap<>();
	private EditBox inputField;
	private Button addPlayerButton, removePlayerButton, copyButton, pasteButton, clearButton;
	private CallbackCheckbox affectEveryPlayerCheckbox;
	private int xSize = 247, ySize = 211, leftPos, topPos;
	private PlayerList playerList;
	private TeamList teamList;

	public EditModuleScreen(ItemStack item) {
		super(item.getItemName());

		availableTeams = new ArrayList<>(Minecraft.getInstance().level.getScoreboard().getPlayerTeams());
		module = item;
	}

	@Override
	public void init() {
		super.init();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		int guiLeft = (width - xSize) / 2;
		int guiTop = (height - ySize) / 2;
		int controlsStartX = (int) (guiLeft + xSize * (3.0F / 4.0F)) - 57;
		int controlsWidth = 107;
		Component checkboxText = Utils.localize("gui.securitycraft:editModule.affectEveryone");
		int length = font.width(checkboxText) + 24; //24 = checkbox width + 4 pixels of buffer
		Button editTeamsButton;

		inputField = addRenderableWidget(new EditBox(font, controlsStartX, height / 2 - 88, 107, 15, Component.empty()) {
			@Override
			public boolean keyPressed(KeyEvent event) {
				int keyCode = event.key();

				if (isFocused() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
					addPlayerButtonClicked(addPlayerButton);
					return true;
				}

				return super.keyPressed(event);
			}
		});
		addPlayerButton = addRenderableWidget(new Button(controlsStartX, height / 2 - 68, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.add_player"), this::addPlayerButtonClicked, Button.DEFAULT_NARRATION));
		removePlayerButton = addRenderableWidget(new Button(controlsStartX, height / 2 - 43, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.remove_player"), this::removePlayerButtonClicked, Button.DEFAULT_NARRATION));
		editTeamsButton = addRenderableWidget(new NonScrollableToggleComponentButton(controlsStartX, height / 2 - 18, controlsWidth, 20, i -> Utils.localize("gui.securitycraft:editModule.edit_teams"), 0, 2, this::editTeamsButtonClicked));
		copyButton = addRenderableWidget(new Button(controlsStartX, height / 2 + 7, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.copy"), this::copyButtonClicked, Button.DEFAULT_NARRATION));
		pasteButton = addRenderableWidget(new Button(controlsStartX, height / 2 + 32, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.paste"), this::pasteButtonClicked, Button.DEFAULT_NARRATION));
		clearButton = addRenderableWidget(new Button(controlsStartX, height / 2 + 57, controlsWidth, 20, Utils.localize("gui.securitycraft:editModule.clear"), this::clearButtonClicked, Button.DEFAULT_NARRATION));
		playerList = addRenderableWidget(new PlayerList(minecraft, 110, 165, height / 2 - 88, guiLeft + 10));
		teamList = addRenderableWidget(new TeamList(minecraft, editTeamsButton.getWidth(), 75, editTeamsButton.getY() + editTeamsButton.getHeight(), editTeamsButton.getX()));
		affectEveryPlayerCheckbox = addRenderableWidget(new CallbackCheckbox(guiLeft + xSize / 2 - length / 2, guiTop + ySize - 25, 20, 20, checkboxText, module.getOrDefault(SCContent.LIST_MODULE_DATA, ListModuleData.EMPTY).affectEveryone(), newState -> module.getOrDefault(SCContent.LIST_MODULE_DATA, ListModuleData.EMPTY).updateAffectEveryone(module, newState), CommonColors.DARK_GRAY));

		teamList.active = false;
		editTeamsButton.active = !availableTeams.isEmpty();
		refreshFromComponent();
		updateButtonStates();
		inputField.setMaxLength(16);
		inputField.setFilter(s -> !s.contains(" "));
		inputField.setResponder(s -> {
			if (s.isEmpty())
				addPlayerButton.active = false;
			else {
				ListModuleData listModuleData = module.get(SCContent.LIST_MODULE_DATA);

				if (listModuleData != null && listModuleData.isPlayerOnList(s)) {
					addPlayerButton.active = false;
					removePlayerButton.active = true;
					playerList.setSelectedIndex(listModuleData.players().indexOf(s));
					return;
				}

				addPlayerButton.active = true;
			}

			removePlayerButton.active = false;
			playerList.setSelectedIndex(-1);
		});
		setInitialFocus(inputField);
	}

	@Override
	public void onClose() {
		super.onClose();
		ClientPacketDistributor.sendToServer(new SetListModuleData(module.getOrDefault(SCContent.LIST_MODULE_DATA, ListModuleData.EMPTY)));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawWordWrap(font, editModule, leftPos + xSize / 2 - font.width(editModule) / 2, topPos + 6, width, CommonColors.DARK_GRAY, false);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, xSize, ySize, 256, 256);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (playerList != null)
			playerList.mouseClicked(event, doubleClick);

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (playerList != null)
			playerList.mouseReleased(event);

		if (teamList != null)
			teamList.mouseReleased(event);

		return super.mouseReleased(event);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (!inputField.isFocused() && minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(event))) {
			onClose();
			return true;
		}

		return super.keyPressed(event);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void addPlayerButtonClicked(Button button) {
		if (inputField.getValue().isEmpty())
			return;

		ListModuleData listModuleData = module.getOrDefault(SCContent.LIST_MODULE_DATA, ListModuleData.EMPTY);

		listModuleData = listModuleData.addPlayer(module, inputField.getValue());

		if (listModuleData.players().size() == ListModuleData.MAX_PLAYERS)
			addPlayerButton.active = false;

		inputField.setValue("");
		updateButtonStates();
	}

	private void editTeamsButtonClicked(Button button) {
		boolean buttonState = ((ToggleComponentButton) button).getCurrentIndex() == 0;

		copyButton.visible = pasteButton.visible = clearButton.visible = buttonState;
		teamList.active = !buttonState;
	}

	private void removePlayerButtonClicked(Button button) {
		if (inputField.getValue().isEmpty())
			return;

		module.getOrDefault(SCContent.LIST_MODULE_DATA, ListModuleData.EMPTY).removePlayer(module, inputField.getValue());
		inputField.setValue("");
		updateButtonStates();
	}

	private void copyButtonClicked(Button button) {
		savedData = module.get(SCContent.LIST_MODULE_DATA);
		copyButton.active = false;
		updateButtonStates();
	}

	private void pasteButtonClicked(Button button) {
		if (savedData != null) {
			module.set(SCContent.LIST_MODULE_DATA, new ListModuleData(ImmutableList.copyOf(savedData.players()), ImmutableList.copyOf(savedData.teams()), savedData.affectEveryone()));
			updateButtonStates();
			refreshFromComponent();
		}
	}

	private void clearButtonClicked(Button button) {
		module.set(SCContent.LIST_MODULE_DATA, ListModuleData.EMPTY);
		inputField.setValue("");
		updateButtonStates(true);
		refreshFromComponent();
	}

	private void updateButtonStates() {
		updateButtonStates(false);
	}

	private void updateButtonStates(boolean cleared) {
		ListModuleData listModuleData = module.getOrDefault(SCContent.LIST_MODULE_DATA, ListModuleData.EMPTY);
		boolean hasNoData = listModuleData.equals(ListModuleData.EMPTY) || (listModuleData.affectEveryone() && listModuleData.players().isEmpty() && listModuleData.teams().isEmpty());

		if (!cleared && hasNoData) {
			addPlayerButton.active = false;
			removePlayerButton.active = false;
		}
		else {
			addPlayerButton.active = listModuleData.players().size() < ListModuleData.MAX_PLAYERS && !inputField.getValue().isEmpty();
			removePlayerButton.active = !inputField.getValue().isEmpty();
		}

		boolean isSameTag = !hasNoData && listModuleData.equals(savedData);

		copyButton.active = !isSameTag;
		pasteButton.active = savedData != null && !savedData.equals(ListModuleData.EMPTY) && !isSameTag;
		clearButton.active = !hasNoData;
	}

	private void refreshFromComponent() {
		ListModuleData listModuleData = module.get(SCContent.LIST_MODULE_DATA);

		if (listModuleData == null || listModuleData.equals(ListModuleData.EMPTY)) {
			availableTeams.forEach(team -> teamsListedStatus.put(team, false));
			affectEveryPlayerCheckbox.setSelected(false);
		}
		else {
			availableTeams.forEach(team -> teamsListedStatus.put(team, listModuleData.teams().contains(team.getName())));
			affectEveryPlayerCheckbox.setSelected(listModuleData.affectEveryone());
		}
	}

	class PlayerList extends ScrollPanel {
		private static final int SLOT_HEIGHT = 12, LIST_LENGTH = ListModuleData.MAX_PLAYERS;
		private int selectedIndex = -1;

		public PlayerList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);
		}

		@Override
		protected int getContentHeight() {
			int height = LIST_LENGTH * (font.lineHeight + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			double mouseX = event.x();
			double mouseY = event.y();

			if (isMouseOver(mouseX, mouseY) && mouseX < left + width - 6) {
				int clickedIndex = ((int) (mouseY - top + scrollDistance - border)) / SLOT_HEIGHT;
				ListModuleData listModuleData = module.getOrDefault(SCContent.LIST_MODULE_DATA, ListModuleData.EMPTY);

				if (clickedIndex < listModuleData.players().size()) {
					selectedIndex = clickedIndex;
					inputField.setValue(listModuleData.players().get(clickedIndex));
				}
			}

			return super.mouseClicked(event, doubleClick);
		}

		@Override
		protected void drawBackground(GuiGraphics guiGraphics, float partialTick) {
			drawGradientRect(guiGraphics, left, top, right, bottom, 0xC0101010, 0xD0101010);
		}

		@Override
		protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, int mouseX, int mouseY) {
			ListModuleData listModuleData = module.get(SCContent.LIST_MODULE_DATA);

			if (listModuleData != null) {
				int baseY = top + border - (int) scrollDistance;
				int mouseListY = (int) (mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / SLOT_HEIGHT;
				List<String> players = listModuleData.players();

				//highlight hovered slot
				if (slotIndex != selectedIndex && mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < LIST_LENGTH && mouseY >= top && mouseY <= bottom) {
					if (slotIndex < players.size() && !players.get(slotIndex).isBlank())
						renderBox(guiGraphics, left, entryRight - 6, baseY + slotIndex * SLOT_HEIGHT, SLOT_HEIGHT - 4, 0xFF808080);
				}

				if (selectedIndex >= 0)
					renderBox(guiGraphics, left, entryRight - 6, baseY + selectedIndex * SLOT_HEIGHT, SLOT_HEIGHT - 4, 0xFFFFFFFF);

				//draw entry strings
				for (int i = 0; i < players.size(); i++) {
					String name = players.get(i);

					if (!name.isEmpty())
						guiGraphics.drawString(font, name, left - 2 + width / 2 - font.width(name) / 2, relativeY + (SLOT_HEIGHT * i), 0xFFC6C6C6, false);
				}
			}
		}

		public void setSelectedIndex(int selectedIndex) {
			this.selectedIndex = selectedIndex;
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}

	class TeamList extends ScrollPanel {
		private static final int SLOT_HEIGHT = 12;
		private final int listLength;
		private int selectedIndex = -1;
		private boolean active = true;

		public TeamList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);

			listLength = availableTeams.size();
		}

		@Override
		protected int getContentHeight() {
			int height = listLength * (font.lineHeight + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		@Override
		protected boolean clickPanel(double mouseX, double mouseY, MouseButtonEvent event) {
			if (active) {
				int slotIndex = (int) (mouseY + (border / 2)) / SLOT_HEIGHT;

				if (slotIndex >= 0 && slotIndex < listLength) {
					Minecraft mc = Minecraft.getInstance();
					double relativeMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

					if (relativeMouseY >= top && relativeMouseY <= bottom) {
						toggleTeam(availableTeams.get(slotIndex));
						minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
						return true;
					}
				}
			}

			return false;
		}

		@Override
		protected void drawBackground(GuiGraphics guiGraphics, float partialTick) {
			drawGradientRect(guiGraphics, left, top, right, bottom, 0xC0101010, 0xD0101010);
		}

		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			if (active) {
				super.render(guiGraphics, mouseX, mouseY, partialTick);

				//draw tooltip for long patron names
				int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
				int slotIndex = mouseListY / SLOT_HEIGHT;

				if (slotIndex >= 0 && slotIndex < listLength && mouseX >= left && mouseX < right - 6 && mouseListY >= 0 && mouseY >= top && mouseY <= bottom) {
					Component name = availableTeams.get(slotIndex).getDisplayName();
					int length = font.width(name);
					int baseY = top + border - (int) scrollDistance;

					if (length >= width - 6) //6 = barWidth
						guiGraphics.setTooltipForNextFrame(font, name, left + 3, baseY + (SLOT_HEIGHT * slotIndex + SLOT_HEIGHT));
				}
			}
		}

		@Override
		protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, int mouseX, int mouseY) {
			int baseY = top + border - (int) scrollDistance;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / SLOT_HEIGHT;

			//highlight hovered slot
			if (slotIndex != selectedIndex && mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
				renderBox(guiGraphics, left, entryRight - 6, baseY + slotIndex * SLOT_HEIGHT, SLOT_HEIGHT - 4, 0xFF808080);

			//draw entry strings and indicators whether the filter is enabled
			for (int i = 0; i < listLength; i++) {
				int yStart = relativeY + (SLOT_HEIGHT * i);
				PlayerTeam team = availableTeams.get(i);

				guiGraphics.drawString(font, team.getDisplayName(), left + 15, yStart, 0xFFC6C6C6, false);
				guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, teamsListedStatus.get(team) ? CONFIRM_SPRITE : CANCEL_SPRITE, left + 1, yStart - 3, 12, 12);
			}
		}

		private void toggleTeam(PlayerTeam teamToToggle) {
			teamsListedStatus.put(teamToToggle, !teamsListedStatus.get(teamToToggle));
			module.getOrDefault(SCContent.LIST_MODULE_DATA, ListModuleData.EMPTY).toggleTeam(module, teamToToggle.getName());
			updateButtonStates();
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}

	class NonScrollableToggleComponentButton extends ToggleComponentButton {
		public NonScrollableToggleComponentButton(int xPos, int yPos, int width, int height, IntFunction<Component> onValueChange, int initialValue, int toggleCount, OnPress onPress) {
			super(xPos, yPos, width, height, onValueChange, initialValue, toggleCount, onPress);
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
			return false;
		}
	}

	private void renderBox(GuiGraphics guiGraphics, int min, int max, int slotTop, int slotBuffer, int borderColor) {
		guiGraphics.fill(min, slotTop - 2, max, slotTop + slotBuffer + 2, borderColor);
		guiGraphics.fill(min + 1, slotTop - 1, max - 1, slotTop + slotBuffer + 1, 0xFF000000);
	}
}
