package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.blocks.BlockProjector;
import net.geforcemods.securitycraft.tileentity.TileEntityProjector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityProjectorRenderer extends TileEntitySpecialRenderer<TileEntityProjector> {

	@Override
	public void render(TileEntityProjector te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		if(te.isActive() && !te.isEmpty())
		{
			EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockProjector.FACING);

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z + 1); //everything's offset by one on z, no idea why
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			for(int fakeX = 0; fakeX < te.getProjectionWidth(); fakeX++) {
				for(int fakeY = 0; fakeY < te.getProjectionHeight(); fakeY++) {
					GlStateManager.pushMatrix();

					BlockPos pos;

					if(!te.isHorizontal())
						pos = translateProjection(te.getPos(), facing, fakeX, fakeY, te.getProjectionRange(), te.getProjectionOffset());
					else
						pos = translateProjection(te.getPos(), facing, fakeX, te.getProjectionRange() - 16, fakeY + 1, te.getProjectionOffset());

					if(pos != null && te.getWorld().isAirBlock(pos))
					{
						BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
						IBlockState state = te.getProjectedBlock().getDefaultState();

						GlStateManager.disableCull();
						GlStateManager.scale(0.9999D, 0.9999D, 0.9999D); //counteract z-fighting between fake blocks
						blockRendererDispatcher.renderBlockBrightness(state, te.getWorld().getLightBrightness(pos));
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
	private BlockPos translateProjection(BlockPos tePos, EnumFacing direction, int x, int y, double distance, double offset)
	{
		BlockPos pos = null;

		if(direction == EnumFacing.NORTH) {
			pos = new BlockPos(tePos.getX() + x + offset, tePos.getY() + y, tePos.getZ() + distance);
			GlStateManager.translate(0.0D + x + offset, 0.0D + y, distance);
		}
		else if(direction == EnumFacing.SOUTH) {
			pos = new BlockPos(tePos.getX() + x + offset, tePos.getY() + y, tePos.getZ() + -distance);
			GlStateManager.translate(0.0D + x + offset, 0.0D + y, -distance);
		}
		else if(direction == EnumFacing.WEST) {
			pos = new BlockPos(tePos.getX() + distance, tePos.getY() + y, tePos.getZ() + x + offset);
			GlStateManager.translate(distance, 0.0D + y, 0.0D + x + offset);
		}
		else if(direction == EnumFacing.EAST) {
			pos = new BlockPos(tePos.getX() + -distance, tePos.getY() + y, tePos.getZ() + x + offset);
			GlStateManager.translate(-distance, 0.0D + y, 0.0D + x + offset);
		}

		return pos;
	}

	@Override
	public boolean isGlobalRenderer(TileEntityProjector te)
	{
		return true;
	}
}
