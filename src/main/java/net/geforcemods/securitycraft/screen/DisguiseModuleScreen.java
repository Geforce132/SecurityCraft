package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.screen.components.StateSelector;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisguiseModuleScreen extends ContainerScreen<DisguiseModuleMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/customize1.png");
	private StateSelector stateSelector;

	public DisguiseModuleScreen(DisguiseModuleMenu container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
	}

	@Override
	protected void init() {
		super.init();

		leftPos += 90;
		children.add(stateSelector = new StateSelector(menu, title, leftPos - 190, topPos + 7, 0, 200, 15, -2.725F, -1.2F));
		stateSelector.init(minecraft, width, height);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(getSlotUnderMouse().getItem(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(int mouseX, int mouseY) {
		String disguiseModuleName = Utils.localize(SCContent.DISGUISE_MODULE.get().getDescriptionId()).getColoredString();
		font.draw(disguiseModuleName, imageWidth / 2 - font.width(disguiseModuleName) / 2, 6, 0x404040);
	}

	@Override
	protected void renderBg(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(leftPos, topPos, 0, 0, imageWidth, imageHeight);

		if (stateSelector != null)
			stateSelector.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (stateSelector != null && stateSelector.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			return true;

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public void onClose() {
		super.onClose();

		if (!menu.getSlot(0).getItem().isEmpty() && stateSelector.getState() != null) {
			ItemStack module = menu.getInventory().getModule();
			CompoundNBT moduleTag = module.getOrCreateTag();

			moduleTag.put("SavedState", NBTUtil.writeBlockState(stateSelector.getState()));
			moduleTag.putInt("StandingOrWall", stateSelector.getStandingOrWallType().ordinal());
			SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(module));
		}
	}

	@Override
	public List<Rectangle2d> getExtraAreas() {
		if (stateSelector != null)
			return stateSelector.getGuiExtraAreas();
		else
			return new ArrayList<>();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}
}
