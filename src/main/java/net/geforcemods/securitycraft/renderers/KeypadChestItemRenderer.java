package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

public class KeypadChestItemRenderer extends BlockEntityWithoutLevelRenderer {
	private static KeypadChestBlockEntity dummyBe;
	private static KeypadChestRenderer dummyRenderer = null;

	public KeypadChestItemRenderer() {
		super(null, null);
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		dummyRenderer = null;
		dummyBe = null;
	}

	@Override
	public void renderByItem(ItemStack stack, TransformType transformType, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (dummyRenderer == null) {
			Minecraft mc = Minecraft.getInstance();

			dummyRenderer = new KeypadChestRenderer(new BlockEntityRendererProvider.Context(mc.getBlockEntityRenderDispatcher(), mc.getBlockRenderer(), mc.getItemRenderer(), mc.getEntityRenderDispatcher(), mc.getEntityModels(), mc.font));
		}

		if (dummyBe == null)
			dummyBe = new KeypadChestBlockEntity(BlockPos.ZERO, SCContent.KEYPAD_CHEST.get().defaultBlockState());

		dummyRenderer.render(dummyBe, 0.0F, pose, buffer, combinedLight, combinedOverlay);
	}
}
