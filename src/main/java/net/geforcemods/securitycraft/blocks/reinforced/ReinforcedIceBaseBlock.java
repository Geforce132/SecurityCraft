package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ReinforcedIceBaseBlock extends BaseReinforcedBlock {
	public ReinforcedIceBaseBlock(Material mat, SoundType sound, Block... vB) {
		super(mat, sound, vB);
	}

	@Override
	public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		return 0.98F;
	}
}
