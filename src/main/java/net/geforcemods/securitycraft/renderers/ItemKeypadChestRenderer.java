package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemKeypadChestRenderer extends ItemStackTileEntityRenderer
{
	private static final KeypadChestTileEntity DUMMY_TE = new KeypadChestTileEntity();
	private static KeypadChestTileEntityRenderer dummyRenderer = null;

	@Override
	public void func_228364_a_(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer buffer, int p_228364_4_, int p_228364_5_)
	{
		if(dummyRenderer == null)
			dummyRenderer = new KeypadChestTileEntityRenderer(TileEntityRendererDispatcher.instance);

		dummyRenderer.func_225616_a_(DUMMY_TE, 0.0F, matrix, buffer, p_228364_4_, p_228364_5_);
	}
}
