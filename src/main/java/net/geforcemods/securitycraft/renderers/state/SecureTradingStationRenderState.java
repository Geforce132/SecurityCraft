package net.geforcemods.securitycraft.renderers.state;

import org.joml.Quaternionf;

import net.minecraft.client.renderer.item.ItemStackRenderState;

public class SecureTradingStationRenderState extends DisguisableBlockEntityRenderState {
	public ItemStackRenderState payment = new ItemStackRenderState();
	public ItemStackRenderState reward = new ItemStackRenderState();
	public Quaternionf rotation;
}
