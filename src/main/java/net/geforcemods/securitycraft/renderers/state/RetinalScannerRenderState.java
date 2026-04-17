package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;

public class RetinalScannerRenderState extends DisguisableBlockEntityRenderState {
	public RenderType renderType;
	public Direction direction;
	public Direction rotation;
	public boolean isDisguised;
	public int combinedSkinLight;
}
