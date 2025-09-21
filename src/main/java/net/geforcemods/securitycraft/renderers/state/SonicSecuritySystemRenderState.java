package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class SonicSecuritySystemRenderState extends BlockEntityRenderState {
	public boolean isRecording;
	public boolean isListening;
	public boolean isShutDown;
	public boolean hasDisguiseModule;
	public float radarRotationDegrees;
}
