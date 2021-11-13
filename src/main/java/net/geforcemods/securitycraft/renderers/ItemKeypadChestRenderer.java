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

public class ItemKeypadChestRenderer extends BlockEntityWithoutLevelRenderer
{
	private static final KeypadChestBlockEntity DUMMY_TE = new KeypadChestBlockEntity(BlockPos.ZERO, SCContent.KEYPAD_CHEST.get().defaultBlockState());
	private static KeypadChestRenderer dummyRenderer = null;

	public ItemKeypadChestRenderer()
	{
		super(null, null);
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
		dummyRenderer = null;
	}

	@Override
	public void renderByItem(ItemStack stack, TransformType transformType, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
	{
		if(dummyRenderer == null)
		{
			Minecraft mc = Minecraft.getInstance();

			dummyRenderer = new KeypadChestRenderer(new BlockEntityRendererProvider.Context(mc.getBlockEntityRenderDispatcher(), mc.getBlockRenderer(), mc.getEntityModels(), mc.font));
		}

		dummyRenderer.render(DUMMY_TE, 0.0F, matrix, buffer, combinedLight, combinedOverlay);
	}
}
