package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class SecureRedstoneInterfaceRenderState extends BlockEntityRenderState {
	public Direction facing;
	public boolean hasDisguiseModule;
	public boolean isSender;
	public float dishRotationDegrees;
}
