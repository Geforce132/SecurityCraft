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
		if(te.isActive() && !te.isEmpty())
		{
			Direction direction = te.getBlockState().get(ProjectorBlock.FACING);

			GlStateManager.pushMatrix();
			GlStateManager.translated(x, y, z + 1); //everything's offset by one on z, no idea why
			Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

			for(int fakeX = 0; fakeX < te.getProjectionWidth(); fakeX++) {
				for(int fakeY = 0; fakeY < te.getProjectionHeight(); fakeY++) {
					GlStateManager.pushMatrix();

					BlockPos pos;

					if(!te.isHorizontal())
						pos = translateProjection(te.getPos(), direction, fakeX, fakeY, te.getProjectionRange(), te.getProjectionOffset());
					else
						pos = translateProjection(te.getPos(), direction, fakeX, te.getProjectionRange() - 16, fakeY + 1, te.getProjectionOffset());

					if(pos != null && te.getWorld().isAirBlock(pos))
					{
						BlockRendererDispatcher blockRendererDispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
						BlockState state = te.getProjectedBlock().getDefaultState();

						GlStateManager.disableCull();
						GlStateManager.scaled(0.9999D, 0.9999D, 0.9999D); //counteract z-fighting between fake blocks
						blockRendererDispatcher.renderBlockBrightness(state, te.getWorld().getBrightness(pos));
						GlStateManager.enableCull();
					}

					GlStateManager.popMatrix();
				}
			}

			GlStateManager.popMatrix();
		}
	}

	/**
	 * Shifts the projection depending on the offset and range set in the projector
	 *
	 * @param pos The position of the projector which draws the fake block
	 * @param stack the MatrixStack of the current render context
	 * @param direction The direction the projector is facing
	 * @param x The offset from the projectors position on the x axis of the position at which to draw the fake block
	 * @param y The offset from the projectors position on the y axis of the position at which to draw the fake block
	 * @param distance The distance in blocks that the fake block is away from the projector (set by player)
	 * @param offset The offset in blocks that the fake block is moved to the side from the projector (set by player)
	 *
	 * @return The BlockPos of the fake block to be drawn, null if an invalid direction was given
	 */
	private BlockPos translateProjection(BlockPos tePos, Direction direction, int x, int y, double distance, double offset)
	{
		BlockPos pos = null;

		if(direction == Direction.NORTH) {
			pos = new BlockPos(tePos.getX() + x + offset, tePos.getY() + y, tePos.getZ() + distance);
			GlStateManager.translated(0.0D + x + offset, 0.0D + y, distance);
		}
		else if(direction == Direction.SOUTH) {
			pos = new BlockPos(tePos.getX() + x + offset, tePos.getY() + y, tePos.getZ() + -distance);
			GlStateManager.translated(0.0D + x + offset, 0.0D + y, -distance);
		}
		else if(direction == Direction.WEST) {
			pos = new BlockPos(tePos.getX() + distance, tePos.getY() + y, tePos.getZ() + x + offset);
			GlStateManager.translated(distance, 0.0D + y, 0.0D + x + offset);
		}
		else if(direction == Direction.EAST) {
			pos = new BlockPos(tePos.getX() + -distance, tePos.getY() + y, tePos.getZ() + x + offset);
			GlStateManager.translated(-distance, 0.0D + y, 0.0D + x + offset);
		}

		return pos;
	}

	@Override
	public boolean isGlobalRenderer(ProjectorTileEntity te)
	{
		return true;
	}
}
