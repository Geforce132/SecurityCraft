package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;

public class ProjectorRenderState extends DisguisableRenderState {
	public BlockEntityRenderState projectedRenderState;
	public BlockState projectedState;
	public boolean isActive;
	public boolean isHanging;
	public boolean isHorizontal;
	public boolean isOverridingBlocks;
	public int projectionWidth;
	public int projectionHeight;
	public int projectionRange;
	public int projectionOffset;
}
