package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class SecurityCameraRenderState extends DisguisableBlockEntityRenderState {
	public Direction direction;
	public boolean isDown;
	public boolean isShutDown;
	public boolean hasLens;
	public boolean isBeingViewed;
	public boolean isBeingCaptured;
	public float cameraYRot;
	public int lensColor;
	public Identifier texture;
}
