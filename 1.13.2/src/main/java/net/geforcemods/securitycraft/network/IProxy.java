package net.geforcemods.securitycraft.network;

import java.util.List;

import net.minecraft.block.Block;

public interface IProxy
{
	public void registerRenderThings();
	public List<Block> getOrPopulateToTint();
	public void cleanup();
	public void registerKeybindings();
}
