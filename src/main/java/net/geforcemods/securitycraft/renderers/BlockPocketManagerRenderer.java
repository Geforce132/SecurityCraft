package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockPocketManagerRenderer extends TileEntitySpecialRenderer<BlockPocketManagerBlockEntity> {
	@Override
	public void render(BlockPocketManagerBlockEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		// The code below draws the outline border of a block pocket.

		if (!te.showOutline || !te.isOwnedBy(Minecraft.getMinecraft().player))
			return;

		EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockPocketManagerBlock.FACING);
		int offset = facing == EnumFacing.NORTH || facing == EnumFacing.EAST ? -te.autoBuildOffset : te.autoBuildOffset; //keep negative values moving the offset to the left consistent
		int size = te.size;
		int half = (size - 1) / 2;
		int leftX = -half + offset;
		int rightX = half + 1 + offset;
		int frontZ = facing == EnumFacing.NORTH || facing == EnumFacing.WEST ? 0 : 1;
		int backZ = facing == EnumFacing.NORTH || facing == EnumFacing.WEST ? size : 1 - size;

		if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) { //x- and z-values get switched when the manager's EnumFacing is west or east
			leftX = frontZ;
			rightX = backZ;
			frontZ = -half + offset;
			backZ = half + 1 + offset;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		ClientUtils.renderBoxInLevel(leftX, rightX, frontZ, backZ, size, te.getColor());
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isGlobalRenderer(BlockPocketManagerBlockEntity te) {
		return te.showOutline;
	}
}
