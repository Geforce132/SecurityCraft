package net.geforcemods.securitycraft.screen;

import java.util.Set;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList;
import net.geforcemods.securitycraft.screen.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class SSSItemScreen extends Screen implements ConnectionAccessor {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final ItemStack stack;
	private final int imageWidth = 176;
	private final int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private SSSConnectionList<SSSItemScreen> connectionList;

	public SSSItemScreen(ItemStack stack) {
		super(Utils.localize(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get().getDescriptionId()));

		if (stack.getItem() instanceof SonicSecuritySystemItem)
			this.stack = stack;
		else
			this.stack = ItemStack.EMPTY;
	}

	@Override
	protected void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		children.add(connectionList = new SSSConnectionList<>(this, minecraft, imageWidth - 24, imageHeight - 40, topPos + 20, leftPos + 12));
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		int textWidth = font.width(title);

		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTicks);

		if (connectionList != null)
			connectionList.render(pose, mouseX, mouseY, partialTicks);

		font.draw(pose, title, leftPos + imageWidth / 2 - textWidth / 2, topPos + 6, 4210752);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (connectionList != null)
			connectionList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public Set<BlockPos> getPositions() {
		return SonicSecuritySystemItem.stackTagToBlockPosSet(stack.getTag());
	}

	@Override
	public void removePosition(BlockPos pos) {
		SonicSecuritySystemItem.removeLinkedBlock(stack.getTag(), pos);
		SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(stack));
		connectionList.refreshPositions();
	}
}
