package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;

public class RetinalScannerRenderState extends DisguisableBlockEntityRenderState {
	public RenderType renderType;
	public Direction direction;
	public boolean isDisguised;
	public int combinedSkinLight;
}
