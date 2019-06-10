package net.geforcemods.securitycraft.blocks;

import javax.annotation.Nullable;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

public class BlockSecretSign extends ContainerBlock implements IBucketPickupHandler, ILiquidContainer
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

	public BlockSecretSign()
	{
		super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if (state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager)
	{
		if(world.getBlockState(pos).getBlock() instanceof BlockSecretSign)
		{
			manager.addBlockDestroyEffects(pos, Blocks.OAK_PLANKS.getDefaultState());
			return true;
		}
		else return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		return SHAPE;
	}

	@Nullable
	public VoxelShape getCollisionShape(BlockState blockState, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return VoxelShapes.empty();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean hasCustomBreakingProgress(BlockState state)
	{
		return true;
	}

	@Override
	public boolean canSpawnInBlock()
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new TileEntitySecretSign();
	}

	@Override
	public IItemProvider getItemDropped(BlockState state, World worldIn, BlockPos pos, int fortune)
	{
		return SCContent.secretSignItem;
	}

	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state)
	{
		return new ItemStack(SCContent.secretSignItem);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(world.isRemote)
			return true;
		else
		{
			if(player.getHeldItem(hand).getItem() == SCContent.adminTool)
				SCContent.adminTool.onItemUse(new ItemUseContext(player, hand, hit));

			TileEntity tileentity = world.getTileEntity(pos);
			return tileentity instanceof TileEntitySecretSign ? ((TileEntitySecretSign)tileentity).executeCommand(player) : false;
		}
	}

	@Override
	public Fluid pickupFluid(IWorld world, BlockPos pos, BlockState state)
	{
		if(state.get(WATERLOGGED))
		{
			world.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
			return Fluids.WATER;
		}
		else
			return Fluids.EMPTY;
	}

	@Override
	public IFluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
	{
		return !state.get(WATERLOGGED) && fluid == Fluids.WATER;
	}

	@Override
	public boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, IFluidState fluidState)
	{
		if(!state.get(WATERLOGGED) && fluidState.getFluid() == Fluids.WATER)
		{
			if(!world.isRemote())
			{
				world.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
				world.getPendingFluidTicks().scheduleTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			}

			return true;
		}
		else
			return false;
	}
}
