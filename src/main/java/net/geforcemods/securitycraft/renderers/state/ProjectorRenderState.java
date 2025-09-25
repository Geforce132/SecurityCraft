package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class ProjectorRenderState extends DisguisableBlockEntityRenderState {
	public BlockEntityRenderState projectedBlockEntityRenderState;
	public BlockState projectedState;
	public Direction facing;
	public boolean isActive;
	public boolean isHanging;
	public boolean isHorizontal;
	public boolean isOverridingBlocks;
	public boolean projectsBlock;
	public int projectionHeight;
	public int projectionWidth;
	public int projectionRange;
	public int projectionOffset;
}
