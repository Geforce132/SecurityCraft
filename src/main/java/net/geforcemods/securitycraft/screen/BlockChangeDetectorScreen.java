package net.geforcemods.securitycraft.screen;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.ChangeEntry;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity.DetectionMode;
import net.geforcemods.securitycraft.inventory.GenericBEMenu;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.screen.components.CollapsibleTextList;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class BlockChangeDetectorScreen extends AbstractContainerScreen<GenericBEMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_change_detector.png");
	private static final TranslatableComponent CLEAR = Utils.localize("gui.securitycraft:editModule.clear");
	private static final TranslatableComponent BLOCK_NAME = Utils.localize(SCContent.BLOCK_CHANGE_DETECTOR.get().getDescriptionId());
	private BlockChangeDetectorBlockEntity be;
	private ChangeEntryList changeEntryList;
	TextHoverChecker hoverChecker;

	public BlockChangeDetectorScreen(GenericBEMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		be = (BlockChangeDetectorBlockEntity) menu.be;
		imageHeight = 256;
	}

	@Override
	protected void init() {
		super.init();

		Button clearButton = addRenderableWidget(new ExtendedButton(leftPos + 4, topPos + 4, 8, 8, new TextComponent("x"), b -> {
			be.getEntries().clear();
			be.setChanged();
			SecurityCraft.channel.sendToServer(new ClearChangeDetectorServer(be.getBlockPos()));
		}));

		ChangeEntry entry = new ChangeEntry(minecraft.player.getName().getString(), minecraft.player.getUUID(), System.currentTimeMillis(), DetectionMode.BREAK, be.getBlockPos(), Blocks.ANDESITE_WALL.defaultBlockState());
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		List<? extends Component> list = List.of(
		//@formatter:off
			entry.player(),
			entry.uuid(),
			dateFormat.format(new Date(entry.timestamp())),
			entry.action(),
			Utils.getFormattedCoordinates(entry.pos()).getString(),
			"[" + entry.state().toString().split("\\[")[1].replace(",", ", ")
		//@formatter:on
		).stream().map(Object::toString).map(TextComponent::new).toList();
		clearButton.active = be.getOwner().isOwner(minecraft.player);
		hoverChecker = new TextHoverChecker(clearButton, CLEAR);
		addRenderableWidget(changeEntryList = new ChangeEntryList(minecraft, 160, 114, topPos + 56, leftPos + 8));

		for (int i = 0; i < 10; i++)
			changeEntryList.addEntry(addWidget(new CollapsibleTextList(0, 0, 154, Utils.localize(Blocks.EXPOSED_CUT_COPPER_STAIRS.getDescriptionId()), list, b -> changeEntryList.setOpen((CollapsibleTextList) b), false)));
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, BLOCK_NAME, imageWidth / 2 - font.width(BLOCK_NAME) / 2, 6, 0x404040);

		if (hoverChecker != null && hoverChecker.checkHover(mouseX, mouseY))
			renderTooltip(pose, CLEAR, mouseX - leftPos, mouseY - topPos);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (changeEntryList != null)
			changeEntryList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (changeEntryList != null)
			changeEntryList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (changeEntryList != null)
			changeEntryList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	class ChangeEntryList extends ScrollPanel {
		private final int slotHeight = 12;
		private List<CollapsibleTextList> entries = new ArrayList<>();
		private CollapsibleTextList currentlyOpen = null;
		private boolean hasChanged = false;
		private float previousScrollDistance = 0.0F;

		public ChangeEntryList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left, 4, 6, 0x00000000, 0x00000000);
		}

		@Override
		protected int getContentHeight() {
			int height = entries.stream().reduce(0, (accumulated, ctl) -> accumulated + ctl.getHeight(), (identity, accumulated) -> identity + accumulated);

			if (height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
			super.render(pose, mouseX, mouseY, partialTicks);

			if (hasChanged || scrollDistance != previousScrollDistance) {
				int height = 0;

				previousScrollDistance = scrollDistance;

				for (int i = 0; i < entries.size(); i++) {
					CollapsibleTextList entry = entries.get(i);

					entry.renderLongMessageTooltip(pose);
					entry.y = entry.getInitialY() - (int) scrollDistance + height;

					if (entry.isOpen())
						height += entry.getHeight() - slotHeight;
				}
			}
		}

		@Override
		protected void drawPanel(PoseStack pose, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
			for (int i = 0; i < entries.size(); i++) {
				entries.get(i).render(pose, mouseX, mouseY, 0.0F);
			}
		}

		public void addEntry(CollapsibleTextList entry) {
			entry.setWidth(154);
			entry.setHeight(slotHeight);
			entry.x = left;
			entry.setY(top + slotHeight * entries.size());
			entries.add(entry);
		}

		public void setOpen(CollapsibleTextList newOpenedTextList) {
			if (currentlyOpen == null)
				currentlyOpen = newOpenedTextList;
			else {
				if (currentlyOpen == newOpenedTextList)
					currentlyOpen = null;
				else {
					currentlyOpen.switchOpenStatus();
					currentlyOpen = newOpenedTextList;
				}
			}

			if (currentlyOpen != null) {
				previousScrollDistance = scrollDistance;
				scrollDistance = slotHeight * entries.indexOf(currentlyOpen);
			}

			hasChanged = true;
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}
}
