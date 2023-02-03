package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DisguisableBlockEntityRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
	@Override
	public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		BlockEntityRenderDelegate.DISGUISED_BLOCK.tryRenderDelegate(te, x, y, z, partialTicks, destroyStage, alpha);
	}
}
