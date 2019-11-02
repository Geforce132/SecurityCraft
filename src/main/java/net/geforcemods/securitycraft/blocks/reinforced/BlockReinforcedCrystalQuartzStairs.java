package net.geforcemods.securitycraft.blocks.reinforced;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public class BlockReinforcedCrystalQuartzStairs extends BlockReinforcedStairs
{
	public BlockReinforcedCrystalQuartzStairs(Block block, int meta)
	{
		super(block, meta);
	}

	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return 0x0E7063;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta)
	{
		return 0x0E7063;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return 0x0E7063;
	}
}
