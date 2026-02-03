package net.geforcemods.securitycraft.renderers.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class DisplayCaseRenderState extends BlockEntityRenderState {
	public boolean isGlowing;
	public float openness;
	public float rotation;
	public AttachFace attachFace;
	public ItemStackRenderState stack = new ItemStackRenderState();
}
