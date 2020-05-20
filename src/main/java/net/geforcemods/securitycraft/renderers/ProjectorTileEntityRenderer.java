package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorTileEntityRenderer extends TileEntityRenderer<ProjectorTileEntity> {

	public ProjectorTileEntityRenderer(TileEntityRendererDispatcher terd) 
	{
		super(terd);
	}

	@Override
	public void render(ProjectorTileEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, int arg5) 
	{
		stack.push();

		translateProjection(stack, te.getBlockState().get(ProjectorBlock.FACING), te.getProjectionRange());

	    RenderSystem.disableCull();
	    // Values of around 220 - 240 make the block appear, but lower values make the model progressively darker?
		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Blocks.DIAMOND_ORE.getDefaultState(), stack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
		RenderSystem.enableCull();

		stack.pop();
	}

	private void translateProjection(MatrixStack stack, Direction direction, double distance) 
	{
		if(direction == Direction.NORTH) {
			stack.translate(0.0D, 0.0D, distance);
		}
		else if(direction == Direction.SOUTH) {
			stack.translate(0.0D, 0.0D, -distance);
		}
		else if(direction == Direction.WEST) {
			stack.translate(distance, 0.0D, 0.0D);
		}
		else if(direction == Direction.EAST) {
			stack.translate(-distance, 0.0D, 0.0D);
		}
		else {
			stack.translate(0.0D, 0.0D, 0.0D);
		}
	}
}
