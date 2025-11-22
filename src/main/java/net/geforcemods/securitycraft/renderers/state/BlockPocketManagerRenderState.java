package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockPocketManagerRenderState extends BlockEntityRenderState {
	public boolean showsOutline;
	public boolean ownedByPlayer;
	public VoxelShape shape;
	public int color;
}
