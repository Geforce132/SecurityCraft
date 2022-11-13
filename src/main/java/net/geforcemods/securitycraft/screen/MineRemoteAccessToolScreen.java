package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.network.server.RemoteControlMine;
import net.geforcemods.securitycraft.network.server.RemoveMineFromMRAT;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class MineRemoteAccessToolScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/mrat.png");
	private static final ResourceLocation INFO_BOOK_ICONS = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png"); //for the explosion icon
	private final TranslatableComponent mratName = Utils.localize(SCContent.REMOTE_ACCESS_MINE.get().getDescriptionId());
	private ItemStack mrat;
	private Button[][] guiButtons = new Button[6][4]; //6 mines, 4 actions (defuse, prime, detonate, unbind)
	private static final int DEFUSE = 0, ACTIVATE = 1, DETONATE = 2, UNBIND = 3;
	private int xSize = 256, ySize = 184;
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();
	private final TranslatableComponent notBound = Utils.localize("gui.securitycraft:mrat.notBound");
	private final TranslatableComponent[] lines = new TranslatableComponent[6];
	private final int[] lengths = new int[6];

	public MineRemoteAccessToolScreen(ItemStack item) {
		super(new TranslatableComponent(item.getDescriptionId()));

		mrat = item;
	}

	@Override
	public void init() {
		super.init();

		int padding = 25;
		int y = padding;
		int[] coords = null;
		int id = 0;
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		hoverCheckers.clear();

		for (int i = 0; i < 6; i++) {
			y += 30;
			coords = getMineCoordinates(i);

			// initialize buttons
			for (int j = 0; j < 4; j++) {
				int btnX = startX + j * padding + 154;
				int btnY = startY + y - 48;
				int mine = id / 4;
				int action = id % 4;

				id++;

				switch (j) {
					case DEFUSE:
						guiButtons[i][j] = new PictureButton(btnX, btnY, 20, 20, itemRenderer, new ItemStack(SCContent.WIRE_CUTTERS.get()), b -> buttonClicked(mine, action));
						guiButtons[i][j].active = false;
						break;
					case ACTIVATE:
						guiButtons[i][j] = new PictureButton(btnX, btnY, 20, 20, itemRenderer, new ItemStack(Items.FLINT_AND_STEEL), b -> buttonClicked(mine, action));
						guiButtons[i][j].active = false;
						break;
					case DETONATE:
						guiButtons[i][j] = new PictureButton(btnX, btnY, 20, 20, INFO_BOOK_ICONS, 54, 1, 0, 1, 18, 18, 256, 256, b -> buttonClicked(mine, action));
						guiButtons[i][j].active = false;
						break;
					case UNBIND:
						guiButtons[i][j] = new ExtendedButton(btnX, btnY, 20, 20, new TextComponent("X"), b -> buttonClicked(mine, action));
						guiButtons[i][j].active = false;
						break;
				}

				addRenderableWidget(guiButtons[i][j]);
			}

			if (coords.length == 3) {
				BlockPos minePos = new BlockPos(coords[0], coords[1], coords[2]);

				guiButtons[i][UNBIND].active = true;
				lines[i] = Utils.localize("gui.securitycraft:mrat.mineLocations", minePos);

				if (Minecraft.getInstance().player.level.isLoaded(minePos)) {
					Block block = minecraft.level.getBlockState(minePos).getBlock();

					if (block instanceof IExplosive explosive) {
						boolean active = explosive.isActive(minecraft.level, minePos);
						boolean defusable = explosive.isDefusable();

						guiButtons[i][DEFUSE].active = active && defusable;
						guiButtons[i][ACTIVATE].active = !active && defusable;
						guiButtons[i][DETONATE].active = active;
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][DEFUSE], Utils.localize("gui.securitycraft:mrat.defuse")));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][ACTIVATE], Utils.localize("gui.securitycraft:mrat.activate")));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][DETONATE], Utils.localize("gui.securitycraft:mrat.detonate")));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:mrat.unbind")));
					}
					else {
						removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

						for (int j = 0; j < 4; j++) {
							guiButtons[i][j].active = false;
						}
					}
				}
				else {
					for (int j = 0; j < 3; j++) {
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][j], Utils.localize("gui.securitycraft:mrat.outOfRange")));
					}

					hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:mrat.unbind")));
				}
			}
			else
				lines[i] = notBound;

			lengths[i] = font.width(lines[i]);
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, startX, startY, 0, 0, xSize, ySize);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, mratName, startX + xSize / 2 - font.width(mratName), startY + -25 + 13, 0xFF0000);

		for (int i = 0; i < 6; i++) {
			font.draw(pose, lines[i], startX + xSize / 2 - lengths[i] + 25, startY + i * 30 + 13, 4210752);
		}

		for (TextHoverChecker chc : hoverCheckers) {
			if (chc != null && chc.checkHover(mouseX, mouseY, getFocused()) && chc.getName() != null)
				renderComponentTooltip(pose, chc.getLines(), mouseX, mouseY);
		}
	}

	private void buttonClicked(int mine, int action) {
		int[] coords = getMineCoordinates(mine);

		if (coords.length == 3) {
			switch (action) {
				case DEFUSE:
					((IExplosive) Minecraft.getInstance().player.level.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).defuseMine(Minecraft.getInstance().player.level, new BlockPos(coords[0], coords[1], coords[2]));
					SecurityCraft.channel.sendToServer(new RemoteControlMine(coords[0], coords[1], coords[2], "defuse"));
					guiButtons[mine][DEFUSE].active = false;
					guiButtons[mine][ACTIVATE].active = true;
					guiButtons[mine][DETONATE].active = false;
					break;
				case ACTIVATE:
					((IExplosive) Minecraft.getInstance().player.level.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).activateMine(Minecraft.getInstance().player.level, new BlockPos(coords[0], coords[1], coords[2]));
					SecurityCraft.channel.sendToServer(new RemoteControlMine(coords[0], coords[1], coords[2], "activate"));
					guiButtons[mine][DEFUSE].active = true;
					guiButtons[mine][ACTIVATE].active = false;
					guiButtons[mine][DETONATE].active = true;
					break;
				case DETONATE:
					SecurityCraft.channel.sendToServer(new RemoteControlMine(coords[0], coords[1], coords[2], "detonate"));
					removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

					for (int i = 0; i < 4; i++) {
						guiButtons[mine][i].active = false;
					}

					break;
				case UNBIND:
					removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

					for (int i = 0; i < 4; i++) {
						guiButtons[mine][i].active = false;
					}
			}
		}
	}

	/**
	 * @param mine 0 based
	 */
	private int[] getMineCoordinates(int mine) {
		mine++; //mines are stored starting by mine1 up to mine6

		if (mrat.getItem() == SCContent.REMOTE_ACCESS_MINE.get() && mrat.hasTag()) {
			int[] coords = mrat.getTag().getIntArray("mine" + mine);

			if (coords.length == 3)
				return coords;
		}

		return new int[0];
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 6; i++) {
			int[] coords = stack.getTag().getIntArray("mine" + i);

			if (coords.length == 3 && coords[0] == x && coords[1] == y && coords[2] == z) {
				stack.getTag().remove("mine" + i);
				SecurityCraft.channel.sendToServer(new RemoveMineFromMRAT(i));
				return;
			}
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
