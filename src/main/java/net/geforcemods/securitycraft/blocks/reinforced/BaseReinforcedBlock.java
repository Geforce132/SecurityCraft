package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.BushBlock;
import net.minecraft.block.FungusBlock;
import net.minecraft.block.NetherRootsBlock;
import net.minecraft.block.NetherSproutsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class BaseReinforcedBlock extends OwnableBlock implements IReinforcedBlock
{
	private final Supplier<Block> vanillaBlockSupplier;

	public BaseReinforcedBlock(Block.Properties properties, Block vB)
	{
		this(properties, () -> vB);
	}

	public BaseReinforcedBlock(Block.Properties properties, Supplier<Block> vB)
	{
		super(properties);

		vanillaBlockSupplier = vB;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (this == SCContent.REINFORCED_COBWEB.get())
		{
			if (entity instanceof PlayerEntity)
			{
				TileEntity te = world.getTileEntity(pos);

				if (te instanceof OwnableTileEntity)
				{
					if(((OwnableTileEntity)te).getOwner().isOwner((PlayerEntity) entity))
						return;
				}
			}

			entity.setMotionMultiplier(state, new Vector3d(0.25D, 0.05D, 0.25D));
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return this == SCContent.REINFORCED_GRASS_PATH.get() ? Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D) : VoxelShapes.fullCube();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return this == SCContent.REINFORCED_COBWEB.get() ? VoxelShapes.empty() : this.getShape(state, world, pos, context);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable)
	{
		BlockState plant = plantable.getPlant(world, pos.offset(facing));
		PlantType type = plantable.getPlantType(world, pos.offset(facing));

		if(plant.getBlock() == Blocks.CACTUS)
			return this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();

		if (plantable instanceof BushBlock) //a workaround because BaseReinforcedBlock can't use isValidGround because it is protected
		{
			boolean bushCondition = state.isIn(SCContent.REINFORCED_GRASS_BLOCK.get()) || state.isIn(SCContent.REINFORCED_DIRT.get()) || state.isIn(SCContent.REINFORCED_COARSE_DIRT.get()) || state.isIn(SCContent.REINFORCED_PODZOL.get());

			if (plantable instanceof NetherSproutsBlock || plantable instanceof NetherRootsBlock || plantable instanceof FungusBlock)
				return state.isIn(SCTags.Blocks.REINFORCED_NYLIUM) || state.isIn(SCContent.REINFORCED_SOUL_SOIL.get()) || bushCondition;
		}

		if(type == PlantType.DESERT)
			return this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();
		else if(type == PlantType.CAVE)
			return state.isSolidSide(world, pos, Direction.UP);
		else if(type == PlantType.PLAINS)
			return isIn(SCTags.Blocks.REINFORCED_DIRT);
		else if(type == PlantType.BEACH)
		{
			boolean isBeach = isIn(SCTags.Blocks.REINFORCED_DIRT) || this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();
			boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
					world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
					world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
					world.getBlockState(pos.south()).getMaterial() == Material.WATER);
			return isBeach && hasWater;
		}
		return false;
	}

	@Override
	public boolean isConduitFrame(BlockState state, IWorldReader world, BlockPos pos, BlockPos conduit)
	{
		return this == SCContent.REINFORCED_PRISMARINE.get() || this == SCContent.REINFORCED_PRISMARINE_BRICKS.get() || this == SCContent.REINFORCED_SEA_LANTERN.get() || this == SCContent.REINFORCED_DARK_PRISMARINE.get();
	}

	@Override
	public boolean isPortalFrame(BlockState state, IBlockReader world, BlockPos pos)
	{
		return this == SCContent.REINFORCED_OBSIDIAN.get();
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos)
	{
		return this == SCContent.REINFORCED_BOOKSHELF.get() ? 1.0F : 0.0F;
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (this.getVanillaBlock() instanceof BreakableBlock)
			return adjacentBlockState.getBlock() == this ? true : super.isSideInvisible(state, adjacentBlockState, side);
		return false;
	}

	@Override
	public boolean ticksRandomly(BlockState state) {
		return this == SCContent.REINFORCED_ICE.get();
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if (this == SCContent.REINFORCED_ICE.get())
		{
			if (world.getLightFor(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos))
			{
				if (world.func_230315_m_().func_236040_e_()) { //getDimensionType, doesWaterVaporize
					world.removeBlock(pos, false);
				}
				else {
					world.setBlockState(pos, Blocks.WATER.getDefaultState());
					world.neighborChanged(pos, Blocks.WATER, pos);
				}
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (this == SCContent.REINFORCED_CRYING_OBSIDIAN.get())
		{
			if(rand.nextInt(5) == 0)
			{
				Direction direction = Direction.func_239631_a_(rand);

				if(direction != Direction.UP)
				{
					BlockPos offsetPos = pos.offset(direction);
					BlockState offsetState = world.getBlockState(offsetPos);

					if(!state.isSolid() || !offsetState.isSolidSide(world, offsetPos, direction.getOpposite()))
					{
						double xOffset = direction.getXOffset() == 0 ? rand.nextDouble() : 0.5D + direction.getXOffset() * 0.6D;
						double yOffset = direction.getYOffset() == 0 ? rand.nextDouble() : 0.5D + direction.getYOffset() * 0.6D;
						double zOffset = direction.getZOffset() == 0 ? rand.nextDouble() : 0.5D + direction.getZOffset() * 0.6D;

						world.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlockSupplier.get();
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState();
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder)
	{
		return NonNullList.from(ItemStack.EMPTY, new ItemStack(this));
	}
}
