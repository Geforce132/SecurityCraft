package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.blocks.ProjectorBlock;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorTileEntityRenderer extends TileEntityRenderer<ProjectorTileEntity> {

	@Override
	public void render(ProjectorTileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if(!te.isActive())
			return;

		if(!te.isEmpty())
		{
			Direction direction = te.getBlockState().get(ProjectorBlock.FACING);

			GlStateManager.pushMatrix();
			GlStateManager.translated(x, y, z + 1); //everything's offset by one on z, no idea why
			Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

			for(int i = 0; i < te.getProjectionWidth(); i++) {
				for(int j = 0; j < te.getProjectionWidth(); j++) {
					GlStateManager.pushMatrix();

					BlockPos pos = translateProjection(te, direction, i, j, te.getProjectionRange(), te.getProjectionOffset());

					if(pos != null && !te.getWorld().isAirBlock(pos))
					{
						GlStateManager.popMatrix();
						continue;
					}

					BlockRendererDispatcher blockRendererDispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
					BlockState state = te.getProjectedBlock().getDefaultState();

					GlStateManager.disableCull();
					GlStateManager.scaled(0.9999D, 0.9999D, 0.9999D); //counteract z-fighting between fake blocks
					blockRendererDispatcher.renderBlockBrightness(state, te.getWorld().getBrightness(pos));
					GlStateManager.enableCull();
					GlStateManager.popMatrix();
				}
			}

			GlStateManager.popMatrix();
		}
	}

	/**
	 * Shifts the projection depending on the offset and range set in the projector
	 *
	 * @return The BlockPos of the fake block to be drawn
	 */
	private BlockPos translateProjection(ProjectorTileEntity te, Direction direction, int x, int y, double distance, double offset)
	{
		BlockPos pos;

		if(direction == Direction.NORTH) {
			pos = new BlockPos(te.getPos().getX() + x + offset, te.getPos().getY() + y, te.getPos().getZ() + distance);
			GlStateManager.translated(0.0D + x + offset, 0.0D + y, distance);
		}
		else if(direction == Direction.SOUTH) {
			pos = new BlockPos(te.getPos().getX() + x + offset, te.getPos().getY() + y, te.getPos().getZ() + -distance);
			GlStateManager.translated(0.0D + x + offset, 0.0D + y, -distance);
		}
		else if(direction == Direction.WEST) {
			pos = new BlockPos(te.getPos().getX() + distance, te.getPos().getY() + y, te.getPos().getZ() + x + offset);
			GlStateManager.translated(distance, 0.0D + y, 0.0D + x + offset);
		}
		else if(direction == Direction.EAST) {
			pos = new BlockPos(te.getPos().getX() + -distance, te.getPos().getY() + y, te.getPos().getZ() + x + offset);
			GlStateManager.translated(-distance, 0.0D + y, 0.0D + x + offset);
		}
		else {
			return te.getPos();
		}

		return pos;
	}

	@Override
	public boolean isGlobalRenderer(ProjectorTileEntity te)
	{
		return true;
	}
}
