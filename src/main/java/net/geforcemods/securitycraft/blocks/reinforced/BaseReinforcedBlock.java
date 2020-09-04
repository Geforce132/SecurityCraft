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
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext.Builder;
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

			entity.setMotionMultiplier(state, new Vec3d(0.25D, 0.05D, 0.25D));
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
	public boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side)
	{
		return this == SCContent.REINFORCED_NETHERRACK.get() && side == Direction.UP;
	}

	@Override
	public boolean isBeaconBase(BlockState state, IWorldReader world, BlockPos pos, BlockPos beacon)
	{
		return this == SCContent.REINFORCED_IRON_BLOCK.get() || this == SCContent.REINFORCED_GOLD_BLOCK.get() || this == SCContent.REINFORCED_DIAMOND_BLOCK.get() || this == SCContent.REINFORCED_EMERALD_BLOCK.get();
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable)
	{
		BlockState plant = plantable.getPlant(world, pos.offset(facing));
		PlantType type = plantable.getPlantType(world, pos.offset(facing));

		if (plant.getBlock() == Blocks.CACTUS)
			return this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();

		switch (type) {
			case Desert: return this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();
			case Cave:   return Block.hasSolidSide(state, world, pos, Direction.UP);
			case Plains: return SCTags.Blocks.REINFORCED_DIRT.contains(this);
			case Beach:
				boolean isBeach = SCTags.Blocks.REINFORCED_DIRT.contains(this) || this == SCContent.REINFORCED_SAND.get() || this == SCContent.REINFORCED_RED_SAND.get();
				boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
						world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
						world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
						world.getBlockState(pos.south()).getMaterial() == Material.WATER);
				return isBeach && hasWater;
			default: break;
		}
		return false;
	}

	@Override
	public boolean isPortalFrame(BlockState state, IWorldReader world, BlockPos pos)
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
	public void randomTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if (this == SCContent.REINFORCED_ICE.get())
		{
			if (world.getLightFor(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos))
			{
				if (world.dimension.doesWaterVaporize()) {
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
