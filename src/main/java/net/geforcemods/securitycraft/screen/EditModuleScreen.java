package net.geforcemods.securitycraft.screen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.items.ModuleItem;
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
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import net.neoforged.neoforge.network.PacketDistributor;

public class EditModuleScreen extends Screen {
	private static CompoundTag savedModule;
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/edit_module.png");
	private static final ResourceLocation CONFIRM_SPRITE = new ResourceLocation("container/beacon/confirm");
	private static final ResourceLocation CANCEL_SPRITE = new ResourceLocation("container/beacon/cancel");
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
		super(Component.translatable(item.getDescriptionId()));

		availableTeams = new ArrayList<>(Minecraft.getInstance().player.getScoreboard().getPlayerTeams());
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
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				if (isFocused() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
					addPlayerButtonClicked(addPlayerButton);
					return true;
				}

				return super.keyPressed(keyCode, scanCode, modifiers);
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
		affectEveryPlayerCheckbox = addRenderableWidget(new CallbackCheckbox(guiLeft + xSize / 2 - length / 2, guiTop + ySize - 25, 20, 20, checkboxText, module.hasTag() && module.getTag().getBoolean("affectEveryone"), newState -> module.getOrCreateTag().putBoolean("affectEveryone", newState), 0x404040));

		teamList.active = false;
		editTeamsButton.active = !availableTeams.isEmpty();
		refreshFromNbt();
		updateButtonStates();
		inputField.setMaxLength(16);
		inputField.setFilter(s -> !s.contains(" "));
		inputField.setResponder(s -> {
			if (s.isEmpty())
				addPlayerButton.active = false;
			else {
				if (module.hasTag()) {
					for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
						if (s.equals(module.getTag().getString("Player" + i))) {
							addPlayerButton.active = false;
							removePlayerButton.active = true;
							playerList.setSelectedIndex(i - 1);
							return;
						}
					}
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
		PacketDistributor.SERVER.noArg().send(new SetListModuleData(module.getOrCreateTag()));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawWordWrap(font, editModule, leftPos + xSize / 2 - font.width(editModule) / 2, topPos + 6, width, 4210752);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, xSize, ySize);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (playerList != null)
			playerList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (playerList != null)
			playerList.mouseReleased(mouseX, mouseY, button);

		if (teamList != null)
			teamList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!inputField.isFocused() && minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void addPlayerButtonClicked(Button button) {
		if (inputField.getValue().isEmpty())
			return;

		if (module.getTag() == null)
			module.setTag(new CompoundTag());

		for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
			if (module.getTag().contains("Player" + i) && module.getTag().getString("Player" + i).equals(inputField.getValue())) {
				if (i == 9)
					addPlayerButton.active = false;

				return;
			}
		}

		module.getTag().putString("Player" + getNextFreeSlot(module.getTag()), inputField.getValue());

		if (module.getTag() != null && module.getTag().contains("Player" + ModuleItem.MAX_PLAYERS))
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

		if (module.getTag() == null)
			module.setTag(new CompoundTag());

		for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
			if (module.getTag().contains("Player" + i) && module.getTag().getString("Player" + i).equals(inputField.getValue())) {
				module.getTag().remove("Player" + i);
				defragmentTag(module.getTag());
			}
		}

		inputField.setValue("");
		updateButtonStates();
	}

	private void copyButtonClicked(Button button) {
		savedModule = module.getTag().copy();
		copyButton.active = false;
		updateButtonStates();
	}

	private void pasteButtonClicked(Button button) {
		module.setTag(savedModule.copy());
		updateButtonStates();
		refreshFromNbt();
	}

	private void clearButtonClicked(Button button) {
		module.setTag(new CompoundTag());
		inputField.setValue("");
		updateButtonStates(true);
		refreshFromNbt();
	}

	private void updateButtonStates() {
		updateButtonStates(false);
	}

	private void updateButtonStates(boolean cleared) {
		CompoundTag tag = module.getOrCreateTag();
		boolean tagIsConsideredEmpty = tag.isEmpty() || (tag.size() == 1 && tag.contains("affectEveryone"));

		if (!cleared && tagIsConsideredEmpty) {
			addPlayerButton.active = false;
			removePlayerButton.active = false;
		}
		else {
			addPlayerButton.active = !tag.contains("Player" + ModuleItem.MAX_PLAYERS) && !inputField.getValue().isEmpty();
			removePlayerButton.active = !inputField.getValue().isEmpty();
		}

		copyButton.active = !tagIsConsideredEmpty && !tag.equals(savedModule);
		pasteButton.active = savedModule != null && !savedModule.isEmpty() && !tag.equals(savedModule);
		clearButton.active = !tagIsConsideredEmpty;
	}

	private void refreshFromNbt() {
		if (!module.hasTag()) {
			availableTeams.forEach(team -> teamsListedStatus.put(team, false));
			affectEveryPlayerCheckbox.setSelected(false);
		}
		else {
			CompoundTag tag = module.getTag();
			//@formatter:off
			List<String> teamNames = tag.getList("ListedTeams", Tag.TAG_STRING)
					.stream()
					.filter(StringTag.class::isInstance)
					.map(e -> ((StringTag) e).getAsString())
					.toList();
			//@formatter:on

			availableTeams.forEach(team -> teamsListedStatus.put(team, teamNames.contains(team.getName())));
			affectEveryPlayerCheckbox.setSelected(tag.getBoolean("affectEveryone"));
		}
	}

	private int getNextFreeSlot(CompoundTag tag) {
		for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
			if (!tag.contains("Player" + i) || tag.getString("Player" + i).isEmpty())
				return i;
		}

		return 0;
	}

	private void defragmentTag(CompoundTag tag) {
		Deque<Integer> freeIndices = new ArrayDeque<>();

		for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
			if (!tag.contains("Player" + i) || tag.getString("Player" + i).isEmpty())
				freeIndices.add(i);
			else if (!freeIndices.isEmpty()) {
				String player = tag.getString("Player" + i);
				int nextFreeIndex = freeIndices.poll();

				tag.putString("Player" + nextFreeIndex, player);
				tag.remove("Player" + i);
				freeIndices.add(i);
			}
		}
	}

	class PlayerList extends ScrollPanel {
		private static final int SLOT_HEIGHT = 12, LIST_LENGTH = ModuleItem.MAX_PLAYERS;
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
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (isMouseOver(mouseX, mouseY) && mouseX < left + width - 6) {
				int clickedIndex = ((int) (mouseY - top + scrollDistance - border)) / SLOT_HEIGHT;

				if (module.hasTag() && module.getTag().contains("Player" + (clickedIndex + 1))) {
					selectedIndex = clickedIndex;
					inputField.setValue(module.getTag().getString("Player" + (clickedIndex + 1)));
				}
			}

			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tessellator, int mouseX, int mouseY) {
			if (module.hasTag()) {
				CompoundTag tag = module.getTag();
				int baseY = top + border - (int) scrollDistance;
				int mouseListY = (int) (mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / SLOT_HEIGHT;

				//highlight hovered slot
				if (slotIndex != selectedIndex && mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < LIST_LENGTH && mouseY >= top && mouseY <= bottom) {
					if (tag.contains("Player" + (slotIndex + 1)) && !tag.getString("Player" + (slotIndex + 1)).isEmpty())
						renderBox(tessellator.getBuilder(), left, entryRight - 6, baseY + slotIndex * SLOT_HEIGHT, SLOT_HEIGHT - 4, 0x80);
				}

				if (selectedIndex >= 0)
					renderBox(tessellator.getBuilder(), left, entryRight - 6, baseY + selectedIndex * SLOT_HEIGHT, SLOT_HEIGHT - 4, 0xFF);

				//draw entry strings
				for (int i = 0; i < ModuleItem.MAX_PLAYERS; i++) {
					if (tag.contains("Player" + (i + 1))) {
						String name = tag.getString("Player" + (i + 1));

						if (!name.isEmpty())
							guiGraphics.drawString(font, name, left - 2 + width / 2 - font.width(name) / 2, relativeY + (SLOT_HEIGHT * i), 0xC6C6C6, false);
					}
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
		protected boolean clickPanel(double mouseX, double mouseY, int button) {
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
						guiGraphics.renderTooltip(font, name, left + 3, baseY + (SLOT_HEIGHT * slotIndex + SLOT_HEIGHT));
				}
			}
		}

		@Override
		protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tessellator, int mouseX, int mouseY) {
			int baseY = top + border - (int) scrollDistance;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / SLOT_HEIGHT;

			//highlight hovered slot
			if (slotIndex != selectedIndex && mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom)
				renderBox(tessellator.getBuilder(), left, entryRight - 6, baseY + slotIndex * SLOT_HEIGHT, SLOT_HEIGHT - 4, 0x80);

			//draw entry strings and indicators whether the filter is enabled
			for (int i = 0; i < listLength; i++) {
				int yStart = relativeY + (SLOT_HEIGHT * i);
				PlayerTeam team = availableTeams.get(i);

				guiGraphics.drawString(font, team.getDisplayName(), left + 15, yStart, 0xC6C6C6, false);
				guiGraphics.blitSprite(teamsListedStatus.get(team) ? CONFIRM_SPRITE : CANCEL_SPRITE, left + 1, yStart - 3, 12, 12);
			}
		}

		private void toggleTeam(PlayerTeam teamToAdd) {
			ListTag listedTeams = new ListTag();

			teamsListedStatus.put(teamToAdd, !teamsListedStatus.get(teamToAdd));
			teamsListedStatus.forEach((team, listed) -> {
				if (listed)
					listedTeams.add(StringTag.valueOf(team.getName()));
			});
			module.getOrCreateTag().put("ListedTeams", listedTeams);
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

	private void renderBox(BufferBuilder bufferBuilder, int min, int max, int slotTop, int slotBuffer, int borderColor) {
		RenderSystem.enableBlend();
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
		BufferUploader.drawWithShader(bufferBuilder.end());
		RenderSystem.disableBlend();
	}
}
