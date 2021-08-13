package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.SecurityCraftBlockEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LaserFieldBlock extends OwnableBlock implements IIntersectable{

	public static final IntegerProperty BOUNDTYPE = IntegerProperty.create("boundtype", 1, 3);
	private static final VoxelShape SHAPE_X = Block.box(0, 6.75, 6.75, 16, 9.25, 9.25);
	private static final VoxelShape SHAPE_Y = Block.box(6.75, 0, 6.75, 9.25, 16, 9.25);
	private static final VoxelShape SHAPE_Z = Block.box(6.75, 6.75, 0, 9.25, 9.25, 16);

	public LaserFieldBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BOUNDTYPE, 1));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockGetter world, BlockPos pos, CollisionContext ctx)
	{
		return Shapes.empty();
	}

	@Override
	public void onEntityIntersected(Level world, BlockPos pos, Entity entity)
	{
		if(!world.isClientSide && entity instanceof LivingEntity livingEntity && !EntityUtils.isInvisible(livingEntity))
		{
			for(Direction facing : Direction.values())
			{
				for(int i = 0; i < ConfigHandler.SERVER.laserBlockRange.get(); i++)
				{
					BlockPos offsetPos = pos.relative(facing, i);
					BlockState offsetState = world.getBlockState(offsetPos);
					Block offsetBlock = offsetState.getBlock();

					if(offsetBlock == SCContent.LASER_BLOCK.get() && !offsetState.getValue(LaserBlock.POWERED))
					{
						BlockEntity te = world.getBlockEntity(offsetPos);

						if(te instanceof IModuleInventory moduleInv && ModuleUtils.isAllowed(moduleInv, entity))
							return;

						world.setBlockAndUpdate(offsetPos, offsetState.setValue(LaserBlock.POWERED, true));
						world.updateNeighborsAt(offsetPos, SCContent.LASER_BLOCK.get());
						world.getBlockTicks().scheduleTick(offsetPos, SCContent.LASER_BLOCK.get(), 50);

						if(te instanceof IModuleInventory moduleInv && moduleInv.hasModule(ModuleType.HARMING))
						{
							if(!(entity instanceof Player player && ((IOwnable)te).getOwner().isOwner(player)))
								livingEntity.hurt(CustomDamageSources.LASER, 10F);
						}
					}
				}
			}
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, pos, state
	 */
	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state)
	{
		if(!world.isClientSide())
		{
			Direction[] facingArray = {Direction.from3DDataValue((state.getValue(LaserFieldBlock.BOUNDTYPE) - 1) * 2), Direction.from3DDataValue((state.getValue(LaserFieldBlock.BOUNDTYPE) - 1) * 2).getOpposite()};

			for(Direction facing : facingArray)
			{
				for(int i = 0; i < ConfigHandler.SERVER.laserBlockRange.get(); i++)
				{
					if(world.getBlockState(pos.relative(facing, i)).getBlock() == SCContent.LASER_BLOCK.get())
					{
						for(int j = 1; j < i; j++)
						{
							world.destroyBlock(pos.relative(facing, j), false);
						}

						break;
					}
				}
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext ctx)
	{
		return switch(state.getValue(BOUNDTYPE)) {
			case 1 -> SHAPE_Y;
			case 2 -> SHAPE_Z;
			case 3 -> SHAPE_X;
			default -> Shapes.empty();
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, Player placer)
	{
		return defaultBlockState().setValue(BOUNDTYPE, 1);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(BOUNDTYPE);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SecurityCraftBlockEntity(pos, state).intersectsEntities();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.teTypeAbstract, SecurityCraftBlockEntity::tick);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		int boundType = state.getValue(BOUNDTYPE);

		return rot == Rotation.CLOCKWISE_180 ? state : state.setValue(BOUNDTYPE, boundType == 2 ? 3 : (boundType == 3 ? 2 : 1));
	}
}
