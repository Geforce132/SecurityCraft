package net.geforcemods.securitycraft.renderers.state;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Vec3i;

public class FrameRenderState extends BlockEntityRenderState {
	public GpuTextureView renderTargetColorTexture;
	public Vector3f backgroundColor;
	public Vector4f innerVertices;
	public Vector4f outerVertices;
	public Vec3i normal;
	public boolean isDisabled;
	public boolean canSeeFeed;
	public boolean hasCamerasLinked;
	public boolean isCameraSelected;
	public boolean isRedstoneSignalDisabled;
	public boolean hasClientInteracted;
	public boolean isCameraPresent;
	public boolean hasLens;
	public int lensColor;
}