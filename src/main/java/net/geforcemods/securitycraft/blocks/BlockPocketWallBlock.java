package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPocketWallBlock extends OwnableBlock implements IBlockPocket
{
	public static final BooleanProperty SEE_THROUGH = BooleanProperty.create("see_through");
	public static final BooleanProperty SOLID = BooleanProperty.create("solid");

	public BlockPocketWallBlock(Block.Properties properties)
	{
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(SEE_THROUGH, true).setValue(SOLID, false));
	}

	public static boolean causesSuffocation(BlockState state, BlockGetter world, BlockPos pos)
	{
		return state.getValue(SOLID);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext collisionContext)
	{
		if(!state.getValue(SOLID) && collisionContext instanceof EntityCollisionContext ctx && ctx.getEntity().isPresent() && ctx.getEntity().get() instanceof Player player)
		{
			BlockEntity tile = world.getBlockEntity(pos);

			if(tile instanceof BlockPocketBlockEntity te)
			{
				if(te.getManager() == null)
					return Shapes.empty();
				else if(ModuleUtils.isAllowed(te.getManager(), player))
					return Shapes.empty();
				else if(!te.getOwner().isOwner(player))
					return Shapes.block();
				else
					return Shapes.empty();
			}
		}

		return Shapes.block();
	}

	@Override
	public boolean canCreatureSpawn(BlockState state, BlockGetter world, BlockPos pos, Type type, EntityType<?> entityType)
	{
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return state.getValue(SEE_THROUGH) && adjacentBlockState.getBlock() == SCContent.BLOCK_POCKET_WALL.get();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return super.getStateForPlacement(context).setValue(SEE_THROUGH, true);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(SEE_THROUGH, SOLID);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new BlockPocketBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeBlockPocket, WorldUtils::blockEntityTicker);
	}
}
