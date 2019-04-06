package net.geforcemods.securitycraft.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;

public class ServerProxy implements IProxy
{
	@Override
	public void registerRenderThings() {}

	@Override
	public List<Block> getOrPopulateToTint()
	{
		return new ArrayList<>();
	}

	@Override
	public void cleanup() {}

	@Override
	public void registerKeybindings() {}
}
