package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneOreMineBlock extends BaseFullMineBlock
{
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public RedstoneOreMineBlock(Block.Properties properties, Block disguisedBlock)
	{
		super(properties, disguisedBlock);

		setDefaultState(getDefaultState().with(LIT, false));
	}

	@Override
	public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player)
	{
		activate(state, world, pos);
		super.onBlockClicked(state, world, pos, player);
	}

	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity)
	{
		activate(world.getBlockState(pos), world, pos);
		super.onEntityWalk(world, pos, entity);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		ItemStack stack = player.getHeldItem(hand);

		if(world.isRemote)
			spawnParticles(world, pos);
		else
			activate(state, world, pos);

		return stack.getItem() instanceof BlockItem && (new BlockItemUseContext(player, hand, stack, hit)).canPlace() ? ActionResultType.PASS : ActionResultType.SUCCESS;
	}

	private static void activate(BlockState state, World world, BlockPos pos)
	{
		spawnParticles(world, pos);

		if(!state.get(LIT))
			world.setBlockState(pos, state.with(LIT, true), 3);
	}
	@Override
	public boolean ticksRandomly(BlockState state)
	{
		return state.get(LIT);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if(state.get(LIT))
			world.setBlockState(pos, state.with(LIT, false), 3);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if(state.get(LIT))
			spawnParticles(world, pos);
	}

	private static void spawnParticles(World world, BlockPos pos)
	{
		Random random = world.rand;

		for(Direction direction : Direction.values())
		{
			BlockPos offsetPos = pos.offset(direction);

			if(!world.getBlockState(offsetPos).isOpaqueCube(world, offsetPos))
			{
				Direction.Axis axis = direction.getAxis();
				double d1 = axis == Direction.Axis.X ? 0.5D + 0.5625D * direction.getXOffset() : (double)random.nextFloat();
				double d2 = axis == Direction.Axis.Y ? 0.5D + 0.5625D * direction.getYOffset() : (double)random.nextFloat();
				double d3 = axis == Direction.Axis.Z ? 0.5D + 0.5625D * direction.getZOffset() : (double)random.nextFloat();

				world.addParticle(RedstoneParticleData.REDSTONE_DUST, pos.getX() + d1, pos.getY() + d2, pos.getZ() + d3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(LIT);
	}
}
