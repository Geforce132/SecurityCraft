package net.geforcemods.securitycraft.blocks;

import java.util.Map;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.OwnableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class IronFenceBlock extends Block implements IIntersectable {
	public static final BooleanProperty NORTH = SixWayBlock.NORTH;
	public static final BooleanProperty EAST = SixWayBlock.EAST;
	public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
	public static final BooleanProperty WEST = SixWayBlock.WEST;
	protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199775_0_) -> {
		return p_199775_0_.getKey().getAxis().isHorizontal();
	}).collect(Util.toMapCollector());
	protected final VoxelShape[] collisionShapes;
	protected final VoxelShape[] shapes;
	private final VoxelShape[] renderShapes;

	public IronFenceBlock(Material material)
	{
		super(Block.Properties.create(material, MaterialColor.IRON).hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.METAL));

		setDefaultState(stateContainer.getBaseState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
		renderShapes = func_196408_a(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
		collisionShapes = func_196408_a(2.0F, 2.0F, 24.0F, 0.0F, 24.0F);
		shapes = func_196408_a(2.0F, 2.0F, 16.0F, 0.0F, 16.0F);
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos)
	{
		return renderShapes[getIndex(state)];
	}

	protected VoxelShape[] func_196408_a(float p_196408_1_, float p_196408_2_, float p_196408_3_, float p_196408_4_, float p_196408_5_)
	{
		float f = 8.0F - p_196408_1_;
		float f1 = 8.0F + p_196408_1_;
		float f2 = 8.0F - p_196408_2_;
		float f3 = 8.0F + p_196408_2_;
		VoxelShape voxelshape = Block.makeCuboidShape(f, 0.0D, f, f1, p_196408_3_, f1);
		VoxelShape voxelshape1 = Block.makeCuboidShape(f2, p_196408_4_, 0.0D, f3, p_196408_5_, f3);
		VoxelShape voxelshape2 = Block.makeCuboidShape(f2, p_196408_4_, f2, f3, p_196408_5_, 16.0D);
		VoxelShape voxelshape3 = Block.makeCuboidShape(0.0D, p_196408_4_, f2, f3, p_196408_5_, f3);
		VoxelShape voxelshape4 = Block.makeCuboidShape(f2, p_196408_4_, f2, 16.0D, p_196408_5_, f3);
		VoxelShape voxelshape5 = VoxelShapes.or(voxelshape1, voxelshape4);
		VoxelShape voxelshape6 = VoxelShapes.or(voxelshape2, voxelshape3);
		VoxelShape[] returnValue = new VoxelShape[]{VoxelShapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.or(voxelshape2, voxelshape1), VoxelShapes.or(voxelshape3, voxelshape1), VoxelShapes.or(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.or(voxelshape2, voxelshape4), VoxelShapes.or(voxelshape3, voxelshape4), VoxelShapes.or(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.or(voxelshape2, voxelshape5), VoxelShapes.or(voxelshape3, voxelshape5), VoxelShapes.or(voxelshape6, voxelshape5)};

		for(int i = 0; i < 16; ++i)
		{
			returnValue[i] = VoxelShapes.or(voxelshape, returnValue[i]);
		}

		return returnValue;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return shapes[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return collisionShapes[getIndex(state)];
	}

	public boolean func_220111_a(BlockState state, boolean p_220111_2_, Direction direction)
	{
		Block block = state.getBlock();
		boolean flag = block.isIn(BlockTags.FENCES) && state.getMaterial() == material;
		boolean flag1 = block instanceof FenceGateBlock && FenceGateBlock.isParallel(state, direction);

		return !cannotAttach(block) && p_220111_2_ || flag || flag1;
	}

	private static int getMask(Direction facing)
	{
		return 1 << facing.getHorizontalIndex();
	}

	protected int getIndex(BlockState state)
	{
		int i = 0;

		if (state.get(NORTH))
			i |= getMask(Direction.NORTH);

		if (state.get(EAST))
			i |= getMask(Direction.EAST);

		if (state.get(SOUTH))
			i |= getMask(Direction.SOUTH);

		if (state.get(WEST))
			i |= getMask(Direction.WEST);

		return i;
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type)
	{
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		switch(rot)
		{
			case CLOCKWISE_180:
				return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		switch(mirror)
		{
			case LEFT_RIGHT:
				return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		IBlockReader iblockreader = ctx.getWorld();
		BlockPos blockpos = ctx.getPos();
		BlockPos blockpos1 = blockpos.north();
		BlockPos blockpos2 = blockpos.east();
		BlockPos blockpos3 = blockpos.south();
		BlockPos blockpos4 = blockpos.west();
		BlockState blockstate = iblockreader.getBlockState(blockpos1);
		BlockState blockstate1 = iblockreader.getBlockState(blockpos2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos3);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos4);
		return super.getStateForPlacement(ctx).with(NORTH, Boolean.valueOf(func_220111_a(blockstate, Block.hasSolidSide(blockstate, iblockreader, blockpos1, Direction.SOUTH), Direction.SOUTH))).with(EAST, Boolean.valueOf(func_220111_a(blockstate1, Block.hasSolidSide(blockstate1, iblockreader, blockpos2, Direction.WEST), Direction.WEST))).with(SOUTH, Boolean.valueOf(func_220111_a(blockstate2, Block.hasSolidSide(blockstate2, iblockreader, blockpos3, Direction.NORTH), Direction.NORTH))).with(WEST, Boolean.valueOf(func_220111_a(blockstate3, Block.hasSolidSide(blockstate3, iblockreader, blockpos4, Direction.EAST), Direction.EAST)));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, WEST, SOUTH);
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(func_220111_a(facingState, Block.hasSolidSide(facingState, world, facingPos, facing.getOpposite()), facing.getOpposite()))) : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) //onBlockActivated
	{
		return ActionResultType.FAIL;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		//so dropped items don't get destroyed
		if(entity instanceof ItemEntity)
			return;
		//owner check
		else if(entity instanceof PlayerEntity)
		{
			if(((OwnableTileEntity) world.getTileEntity(pos)).getOwner().isOwner((PlayerEntity)entity))
				return;
		}
		else if(entity instanceof CreeperEntity)
		{
			CreeperEntity creeper = (CreeperEntity)entity;
			LightningBoltEntity lightning = new LightningBoltEntity(world, pos.getX(), pos.getY(), pos.getZ(), true);

			creeper.onStruckByLightning(lightning);
			creeper.extinguish();
			return;
		}

		entity.attackEntityFrom(CustomDamageSources.electricity, 6.0F); //3 hearts per attack
	}

	@Override
	public boolean eventReceived(BlockState state, World world, BlockPos pos, int eventID, int eventParam)
	{
		super.eventReceived(state, world, pos, eventID, eventParam);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new OwnableTileEntity().intersectsEntities();
	}
}