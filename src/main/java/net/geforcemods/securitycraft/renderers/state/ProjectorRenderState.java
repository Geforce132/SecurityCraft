package net.geforcemods.securitycraft.renderers.state;

import java.util.List;

import net.geforcemods.securitycraft.renderers.ProjectorRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class ProjectorRenderState extends DisguisableBlockEntityRenderState {
	public BlockEntityRenderState projectedBlockEntityRenderState;
	public List<ProjectorRenderer.ProjectionInfo> renderPositions;
}
