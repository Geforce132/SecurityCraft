package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.GlowDisplayCaseBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DisplayCaseItemRenderer extends BlockEntityWithoutLevelRenderer {
	private DisplayCaseBlockEntity dummyBe;
	private DisplayCaseRenderer dummyRenderer = null;
	private final boolean glowing;

	public DisplayCaseItemRenderer(boolean glowing) {
		super(null, null);
		this.glowing = glowing;
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		dummyRenderer = null;
		dummyBe = null;
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (dummyRenderer == null) {
			Minecraft mc = Minecraft.getInstance();

			dummyRenderer = new DisplayCaseRenderer(new BlockEntityRendererProvider.Context(mc.getBlockEntityRenderDispatcher(), mc.getBlockRenderer(), mc.getItemRenderer(), mc.getEntityRenderDispatcher(), mc.getEntityModels(), mc.font), glowing);
		}

		if (dummyBe == null) {
			if (glowing)
				dummyBe = new GlowDisplayCaseBlockEntity(BlockPos.ZERO, SCContent.GLOW_DISPLAY_CASE.get().defaultBlockState());
			else
				dummyBe = new DisplayCaseBlockEntity(BlockPos.ZERO, SCContent.DISPLAY_CASE.get().defaultBlockState());
		}

		dummyRenderer.render(dummyBe, 0.0F, pose, buffer, combinedLight, combinedOverlay);
	}
}
