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
		if(!te.isActive())
			return;

		if(!te.isEmpty())
		{
			EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockProjector.FACING);

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z + 1); //everything's offset by one on z, no idea why
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			for(int i = 0; i < te.getProjectionWidth(); i++) {
				for(int j = 0; j < te.getProjectionWidth(); j++) {
					GlStateManager.pushMatrix();

					BlockPos pos = translateProjection(te, facing, i, j, te.getProjectionRange(), te.getProjectionOffset());

					if(pos != null && !te.getWorld().isAirBlock(pos))
					{
						GlStateManager.popMatrix();
						continue;
					}

					BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
					IBlockState state = te.getProjectedBlock().getDefaultState();

					GlStateManager.disableCull();
					GlStateManager.scale(0.9999D, 0.9999D, 0.9999D); //counteract z-fighting between fake blocks
					blockRendererDispatcher.renderBlockBrightness(state, te.getWorld().getLightBrightness(pos));
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
	private BlockPos translateProjection(TileEntityProjector te, EnumFacing direction, int x, int y, double distance, double offset)
	{
		BlockPos pos;

		if(direction == EnumFacing.NORTH) {
			pos = new BlockPos(te.getPos().getX() + x + offset, te.getPos().getY() + y, te.getPos().getZ() + distance);
			GlStateManager.translate(0.0D + x + offset, 0.0D + y, distance);
		}
		else if(direction == EnumFacing.SOUTH) {
			pos = new BlockPos(te.getPos().getX() + x + offset, te.getPos().getY() + y, te.getPos().getZ() + -distance);
			GlStateManager.translate(0.0D + x + offset, 0.0D + y, -distance);
		}
		else if(direction == EnumFacing.WEST) {
			pos = new BlockPos(te.getPos().getX() + distance, te.getPos().getY() + y, te.getPos().getZ() + x + offset);
			GlStateManager.translate(distance, 0.0D + y, 0.0D + x + offset);
		}
		else if(direction == EnumFacing.EAST) {
			pos = new BlockPos(te.getPos().getX() + -distance, te.getPos().getY() + y, te.getPos().getZ() + x + offset);
			GlStateManager.translate(-distance, 0.0D + y, 0.0D + x + offset);
		}
		else {
			return te.getPos();
		}

		return pos;
	}

	@Override
	public boolean isGlobalRenderer(TileEntityProjector te)
	{
		return true;
	}
}
