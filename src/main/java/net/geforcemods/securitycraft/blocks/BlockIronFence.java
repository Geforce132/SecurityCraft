package net.geforcemods.securitycraft.blocks;

import java.util.Map;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedFenceGate;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSixWay;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockIronFence extends Block implements IIntersectable {
	public static final BooleanProperty NORTH = BlockSixWay.NORTH;
	public static final BooleanProperty EAST = BlockSixWay.EAST;
	public static final BooleanProperty SOUTH = BlockSixWay.SOUTH;
	public static final BooleanProperty WEST = BlockSixWay.WEST;
	protected static final Map<EnumFacing, BooleanProperty> FACING_TO_PROPERTY_MAP = BlockSixWay.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199775_0_) -> {
		return p_199775_0_.getKey().getAxis().isHorizontal();
	}).collect(Util.toMapCollector());
	protected final VoxelShape[] collisionShapes;
	protected final VoxelShape[] shapes;
	private final VoxelShape[] renderShapes;

	public BlockIronFence(Material material)
	{
		super(Block.Properties.create(material, MaterialColor.IRON).hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.METAL));

		setDefaultState(stateContainer.getBaseState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
		renderShapes = func_196408_a(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
		collisionShapes = func_196408_a(2.0F, 2.0F, 24.0F, 0.0F, 24.0F);
		shapes = func_196408_a(2.0F, 2.0F, 16.0F, 0.0F, 16.0F);
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public VoxelShape getRenderShape(IBlockState state, IBlockReader world, BlockPos pos)
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
	public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return shapes[getIndex(state)];
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return collisionShapes[getIndex(state)];
	}

	private static int getMask(EnumFacing facing)
	{
		return 1 << facing.getHorizontalIndex();
	}

	protected int getIndex(IBlockState state)
	{
		int i = 0;

		if (state.get(NORTH))
			i |= getMask(EnumFacing.NORTH);

		if (state.get(EAST))
			i |= getMask(EnumFacing.EAST);

		if (state.get(SOUTH))
			i |= getMask(EnumFacing.SOUTH);

		if (state.get(WEST))
			i |= getMask(EnumFacing.WEST);

		return i;
	}

	@Override
	public boolean allowsMovement(IBlockState state, IBlockReader world, BlockPos pos, PathType type)
	{
		return false;
	}

	@Override
	public IBlockState rotate(IBlockState state, Rotation rot)
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
	public IBlockState mirror(IBlockState state, Mirror mirror)
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
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		IBlockReader world = ctx.getWorld();
		BlockPos pos = ctx.getPos();

		return super.getStateForPlacement(ctx)
				.with(NORTH, canFenceConnectTo(world, pos, EnumFacing.NORTH))
				.with(EAST, canFenceConnectTo(world, pos, EnumFacing.EAST))
				.with(SOUTH, canFenceConnectTo(world, pos, EnumFacing.SOUTH))
				.with(WEST, canFenceConnectTo(world, pos, EnumFacing.WEST));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
	{
		builder.add(NORTH, EAST, WEST, SOUTH);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState state, EnumFacing facing, IBlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		return facing.getAxis().getPlane() == EnumFacing.Plane.HORIZONTAL ? state.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(canFenceConnectTo(world, currentPos, facing))) : super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));
	}

	@Override
	public boolean canBeConnectedTo(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing)
	{
		IBlockState other = world.getBlockState(pos.offset(facing));
		return attachesTo(other, other.getBlockFaceShape(world, pos.offset(facing), facing.getOpposite()));
	}

	public boolean attachesTo(IBlockState state, BlockFaceShape faceShape)
	{
		Block block = state.getBlock();
		boolean flag = faceShape == BlockFaceShape.MIDDLE_POLE && (state.getMaterial() == this.material || block instanceof BlockReinforcedFenceGate);
		return !isExceptBlockForAttachWithPiston(block) && faceShape == BlockFaceShape.SOLID || flag;
	}

	private boolean canFenceConnectTo(IBlockReader world, BlockPos pos, EnumFacing facing)
	{
		BlockPos offset = pos.offset(facing);
		IBlockState other = world.getBlockState(offset);
		return other.canBeConnectedTo(world, offset, facing.getOpposite()) || getDefaultState().canBeConnectedTo(world, pos, facing);
	}

	public static boolean isExcepBlockForAttachWithPiston(Block block)
	{
		return Block.isExceptBlockForAttachWithPiston(block) || block == Blocks.BARRIER || block == Blocks.MELON || block == Blocks.PUMPKIN || block == Blocks.CARVED_PUMPKIN || block == Blocks.JACK_O_LANTERN || block == Blocks.FROSTED_ICE || block == Blocks.TNT;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		//so dropped items don't get destroyed
		if(entity instanceof EntityItem)
			return;
		//owner check
		else if(entity instanceof EntityPlayer)
		{
			if(((TileEntityOwnable) world.getTileEntity(pos)).getOwner().isOwner((EntityPlayer)entity))
				return;
		}
		else if(entity instanceof EntityCreeper)
		{
			EntityCreeper creeper = (EntityCreeper)entity;
			EntityLightningBolt lightning = new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), true);

			creeper.onStruckByLightning(lightning);
			creeper.extinguish();
			return;
		}

		entity.attackEntityFrom(CustomDamageSources.electricity, 6.0F); //3 hearts per attack
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
	{
		if(state.hasTileEntity() && state.getBlock() != newState.getBlock())
			world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int eventID, int eventParam)
	{
		super.eventReceived(state, world, pos, eventID, eventParam);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new TileEntityOwnable().intersectsEntities();
	}
}