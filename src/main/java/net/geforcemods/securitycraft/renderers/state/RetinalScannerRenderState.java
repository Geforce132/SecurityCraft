package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class RetinalScannerRenderState extends BlockEntityRenderState {
	public RenderType skinRenderType;
	public Direction facing;
	public boolean hasFilledDisguiseModule;
	public int combinedFacingLight;
}
