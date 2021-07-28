package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.LaserBlockTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LaserBlock extends DisguisableBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public LaserBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(POWERED, false));
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.setPlacedBy(world, pos, state, entity, stack);

		if(!world.isClientSide)
			setLaser(world, pos);
	}

	public void setLaser(Level world, BlockPos pos)
	{
		LaserBlockTileEntity thisTe = (LaserBlockTileEntity)world.getBlockEntity(pos);

		for(Direction facing : Direction.values())
		{
			int boundType = facing == Direction.UP || facing == Direction.DOWN ? 1 : (facing == Direction.NORTH || facing == Direction.SOUTH ? 2 : 3);

			inner: for(int i = 1; i <= ConfigHandler.SERVER.laserBlockRange.get(); i++)
			{
				BlockPos offsetPos = pos.relative(facing, i);
				BlockState offsetState = world.getBlockState(offsetPos);
				Block offsetBlock = offsetState.getBlock();

				if(!offsetState.isAir() && offsetBlock != SCContent.LASER_BLOCK.get())
					break inner;
				else if(offsetBlock == SCContent.LASER_BLOCK.get())
				{
					LaserBlockTileEntity thatTe = (LaserBlockTileEntity)world.getBlockEntity(offsetPos);

					if(thisTe.getOwner().equals(thatTe.getOwner()))
					{
						CustomizableTileEntity.link(thisTe, thatTe);

						for(ModuleType type : thatTe.getInsertedModules())
						{
							thisTe.insertModule(thatTe.getModule(type));
						}

						if (thisTe.isEnabled() && thatTe.isEnabled())
						{
							for(int j = 1; j < i; j++)
							{
								offsetPos = pos.relative(facing, j);

								if(world.getBlockState(offsetPos).isAir())
								{
									world.setBlockAndUpdate(offsetPos, SCContent.LASER_FIELD.get().defaultBlockState().setValue(LaserFieldBlock.BOUNDTYPE, boundType));

									BlockEntity te = world.getBlockEntity(offsetPos);

									if(te instanceof IOwnable ownable)
										ownable.setOwner(thisTe.getOwner().getUUID(), thisTe.getOwner().getName());
								}
							}
						}
					}

					break inner;
				}
			}
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		if(!world.isClientSide())
			destroyAdjacentLasers(world, pos);
	}

	public static void destroyAdjacentLasers(LevelAccessor world, BlockPos pos)
	{
		for(Direction facing : Direction.values())
		{
			int boundType = facing == Direction.UP || facing == Direction.DOWN ? 1 : (facing == Direction.NORTH || facing == Direction.SOUTH ? 2 : 3);

			for(int i = 1; i <= ConfigHandler.SERVER.laserBlockRange.get(); i++)
			{
				BlockPos offsetPos = pos.relative(facing, i);
				BlockState state = world.getBlockState(offsetPos);

				if(state.getBlock() == SCContent.LASER_BLOCK.get())
					break;
				else if(state.getBlock() == SCContent.LASER_FIELD.get() && state.getValue(LaserFieldBlock.BOUNDTYPE) == boundType)
					world.destroyBlock(offsetPos, false);
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		setLaser(world, pos);
	}

	@Override
	public boolean isSignalSource(BlockState state){
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side)
	{
		return false;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side){
		if(blockState.getValue(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side){
		if(blockState.getValue(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random)
	{
		if (!world.isClientSide && state.getValue(POWERED))
			world.setBlockAndUpdate(pos, state.setValue(POWERED, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand){
		if((state.getValue(POWERED))){
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float r = 0.6F + 0.4F;
			float g = Math.max(0.0F, 0.7F - 0.5F);
			float b = Math.max(0.0F, 0.6F - 0.7F);
			Vector3f vec = new Vector3f(r, g, b);

			world.addParticle(new DustParticleOptions(vec, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleOptions(vec, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleOptions(vec, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleOptions(vec, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new DustParticleOptions(vec, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}

	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(POWERED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LaserBlockTileEntity(pos, state).linkable();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return BaseEntityBlock.createTickerHelper(type, SCContent.teTypeLaserBlock, LaserBlockTileEntity::tick);
	}
}
