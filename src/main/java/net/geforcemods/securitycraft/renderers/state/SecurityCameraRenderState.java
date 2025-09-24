package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class SecurityCameraRenderState extends DisguisableBlockEntityRenderState {
	public Direction direction;
	public boolean isDown;
	public boolean isShutDown;
	public boolean isDisguised;
	public boolean hasLens;
	public boolean isBeingViewed;
	public boolean isBeingCaptured;
	public float cameraYRot;
	public int lensColor;
	public ResourceLocation texture;
}
