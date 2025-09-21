package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class SecurityCameraRenderState extends BlockEntityRenderState {
	public Direction side;
	public boolean isInsideThisCamera;
	public boolean isDown;
	public boolean isShutDown;
	public boolean hasDisguiseModule;
	public boolean hasLens;
	public boolean isViewed;
	public float cameraRotation;
	public float r;
	public float g;
	public float b;
}
