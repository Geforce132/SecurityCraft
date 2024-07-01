package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ReinforcedSoulSandBlock extends BaseReinforcedBlock {
	protected static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);

	public ReinforcedSoulSandBlock() {
		super(Material.SAND, MapColor.BROWN, Blocks.SOUL_SAND);
		setSoundType(SoundType.SAND);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		entity.motionX *= 0.4D;
		entity.motionZ *= 0.4D;
	}
}
