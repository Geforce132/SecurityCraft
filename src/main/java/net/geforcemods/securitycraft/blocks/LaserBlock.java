package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.tileentity.LaserBlockTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LaserBlock extends DisguisableBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public LaserBlock(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).tickRandomly().sound(SoundType.METAL));
		setDefaultState(stateContainer.getBaseState().with(POWERED, false));
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		if(!world.isRemote)
			setLaser(world, pos);
	}

	public void setLaser(World world, BlockPos pos)
	{
		for(Direction facing : Direction.values())
		{
			int boundType = facing == Direction.UP || facing == Direction.DOWN ? 1 : (facing == Direction.NORTH || facing == Direction.SOUTH ? 2 : 3);

			inner: for(int i = 1; i <= ConfigHandler.CONFIG.laserBlockRange.get(); i++)
			{
				BlockPos offsetPos = pos.offset(facing, i);
				BlockState state = world.getBlockState(offsetPos);
				Block id = world.getBlockState(offsetPos).getBlock();

				if(!state.isAir(world, offsetPos) && id != SCContent.laserBlock)
					break inner;
				else if(id == SCContent.laserBlock)
				{
					CustomizableTileEntity thisTe = (CustomizableTileEntity)world.getTileEntity(pos);
					CustomizableTileEntity thatTe = (CustomizableTileEntity)world.getTileEntity(offsetPos);

					if(thisTe.getOwner().equals(thatTe.getOwner()))
					{
						CustomizableTileEntity.link(thisTe, thatTe);

						for(int j = 1; j < i; j++)
						{
							offsetPos = pos.offset(facing, j);

							if(world.getBlockState(offsetPos).isAir(world, offsetPos))
								world.setBlockState(offsetPos, SCContent.laserField.getDefaultState().with(LaserFieldBlock.BOUNDTYPE, boundType));
						}
					}
				}
			}
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state) {
		if(!world.isRemote())
			destroyAdjacentLasers(world, pos);
	}

	public static void destroyAdjacentLasers(IWorld world, BlockPos pos)
	{
		for(Direction facing : Direction.values())
		{
			int boundType = facing == Direction.UP || facing == Direction.DOWN ? 1 : (facing == Direction.NORTH || facing == Direction.SOUTH ? 2 : 3);

			for(int i = 1; i <= ConfigHandler.CONFIG.laserBlockRange.get(); i++)
			{
				BlockPos offsetPos = pos.offset(facing, i);
				BlockState state = world.getBlockState(offsetPos);

				if(state.getBlock() == SCContent.laserBlock)
					return;
				else if(state.getBlock() == SCContent.laserField && state.get(LaserFieldBlock.BOUNDTYPE) == boundType)
					world.destroyBlock(offsetPos, false);
			}
		}
	}

	@Override
	public boolean canProvidePower(BlockState state){
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side)
	{
		return false;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (!world.isRemote && state.get(POWERED))
			BlockUtils.setBlockProperty(world, pos, POWERED, false, true);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand){
		if((state.get(POWERED))){
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float f1 = 0.6F + 0.4F;
			float f2 = Math.max(0.0F, 0.7F - 0.5F);
			float f3 = Math.max(0.0F, 0.6F - 0.7F);

			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(f1, f2, f3, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}

	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(POWERED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new LaserBlockTileEntity().linkable();
	}

}
