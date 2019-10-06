package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockTrophySystem extends BlockOwnable {

	public BlockTrophySystem(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader reader, BlockPos pos) {
		return false;
	}
	
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos){
		return BlockUtils.isSideSolid(world, pos.down(), Direction.UP);
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if(!isValidPosition(state, world, pos)) {
			world.destroyBlock(pos, true);
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityTrophySystem();
	}

}
