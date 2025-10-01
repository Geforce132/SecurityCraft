package net.geforcemods.securitycraft.renderers.state;

import java.util.List;

import net.geforcemods.securitycraft.renderers.ProjectorRenderer.ProjectionInfo;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;

public class ProjectorRenderState extends DisguisableBlockEntityRenderState {
	public BlockEntityRenderState projectedBlockEntityRenderState;
	public BlockState projectedState;
	public List<ProjectionInfo> renderPositions;
}
