package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedPiston;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityReinforcedPistonRenderer extends TileEntitySpecialRenderer<TileEntityReinforcedPiston> {
	private BlockRendererDispatcher blockRenderer;

	@Override
	public void render(TileEntityReinforcedPiston te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (blockRenderer == null)
			blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher(); //Forge: Delay this from constructor to allow us to change it later

		BlockPos pos = te.getPos();
		IBlockState movedState = te.getPistonState();
		Block movedBlock = movedState.getBlock();

		if (movedState.getMaterial() != Material.AIR && te.getProgress(partialTicks) < 1.0F) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();

			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.disableCull();

			if (Minecraft.isAmbientOcclusionEnabled()) {
				GlStateManager.shadeModel(7425);
			} else {
				GlStateManager.shadeModel(7424);
			}

			bufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
			bufferBuilder.setTranslation(x - pos.getX() + te.getOffsetX(partialTicks), y - pos.getY() + te.getOffsetY(partialTicks), z - pos.getZ() + te.getOffsetZ(partialTicks));
			World world = getWorld();

			if (movedBlock == SCContent.reinforcedPistonHead && te.getProgress(partialTicks) <= 0.25F) {
				movedState = movedState.withProperty(BlockPistonExtension.SHORT, true);
				renderStateModel(pos, movedState, bufferBuilder, world, true);
			}
			else if (te.shouldPistonHeadBeRendered() && !te.isExtending()) {
				EnumPistonType pistonType = movedBlock == SCContent.reinforcedStickyPiston ? EnumPistonType.STICKY : EnumPistonType.DEFAULT;
				IBlockState pistonHeadState = SCContent.reinforcedPistonHead.getDefaultState().withProperty(BlockPistonExtension.TYPE, pistonType).withProperty(BlockPistonExtension.FACING, movedState.getValue(BlockPistonBase.FACING));

				pistonHeadState = pistonHeadState.withProperty(BlockPistonExtension.SHORT, te.getProgress(partialTicks) >= 0.5F);
				renderStateModel(pos, pistonHeadState, bufferBuilder, world, true);
				bufferBuilder.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
				movedState = movedState.withProperty(BlockPistonBase.EXTENDED, true);
				renderStateModel(pos, movedState, bufferBuilder, world, true);
			}
			else {
				renderStateModel(pos, movedState, bufferBuilder, world, false);
			}

			bufferBuilder.setTranslation(0.0D, 0.0D, 0.0D);
			tessellator.draw();
			RenderHelper.enableStandardItemLighting();
		}
	}

	private void renderStateModel(BlockPos pos, IBlockState state, BufferBuilder buffer, World world, boolean checkSides) {
		blockRenderer.getBlockModelRenderer().renderModel(world, blockRenderer.getModelForState(state), state, pos, buffer, checkSides);
	}
}
