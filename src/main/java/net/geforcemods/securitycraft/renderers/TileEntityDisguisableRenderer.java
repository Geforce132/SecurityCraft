package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.util.TileEntityRenderDelegate;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityDisguisableRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
	@Override
	public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		TileEntityRenderDelegate.DISGUISED_BLOCK.tryRenderDelegate(te, x, y, z, partialTicks, destroyStage, alpha);
	}
}
