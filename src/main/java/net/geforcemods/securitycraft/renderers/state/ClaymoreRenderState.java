package net.geforcemods.securitycraft.renderers.state;

import org.joml.Quaternionf;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Vec3i;

public class ClaymoreRenderState extends BlockEntityRenderState {
	public boolean isActivated;
	public Quaternionf rotation;
	public Vec3i normal;
	public int r;
	public int g;
	public int b;
}
