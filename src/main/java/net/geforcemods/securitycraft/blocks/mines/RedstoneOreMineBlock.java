package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneOreMineBlock extends BaseFullMineBlock
{
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public RedstoneOreMineBlock(Block.Properties properties, Block disguisedBlock)
	{
		super(properties, disguisedBlock);

		registerDefaultState(defaultBlockState().setValue(LIT, false));
	}

	@Override
	public void attack(BlockState state, Level level, BlockPos pos, Player player)
	{
		activate(state, level, pos);
		super.attack(state, level, pos, player);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
	{
		activate(state, level, pos);
		super.stepOn(level, pos, state, entity);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		ItemStack stack = player.getItemInHand(hand);

		if(level.isClientSide)
			spawnParticles(level, pos);
		else
			activate(state, level, pos);

		return stack.getItem() instanceof BlockItem && (new BlockPlaceContext(player, hand, stack, hit)).canPlace() ? InteractionResult.PASS : InteractionResult.SUCCESS;
	}

	private static void activate(BlockState state, Level level, BlockPos pos)
	{
		spawnParticles(level, pos);

		if(!state.getValue(LIT))
			level.setBlock(pos, state.setValue(LIT, true), 3);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state)
	{
		return state.getValue(LIT);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
	{
		if(state.getValue(LIT))
			level.setBlock(pos, state.setValue(LIT, false), 3);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level level, BlockPos pos, Random rand)
	{
		if(state.getValue(LIT))
			spawnParticles(level, pos);
	}

	private static void spawnParticles(Level level, BlockPos pos)
	{
		Random random = level.random;

		for(Direction direction : Direction.values())
		{
			BlockPos offsetPos = pos.relative(direction);

			if(!level.getBlockState(offsetPos).isSolidRender(level, offsetPos))
			{
				Direction.Axis axis = direction.getAxis();
				double d1 = axis == Direction.Axis.X ? 0.5D + 0.5625D * direction.getStepX() : (double)random.nextFloat();
				double d2 = axis == Direction.Axis.Y ? 0.5D + 0.5625D * direction.getStepY() : (double)random.nextFloat();
				double d3 = axis == Direction.Axis.Z ? 0.5D + 0.5625D * direction.getStepZ() : (double)random.nextFloat();

				level.addParticle(DustParticleOptions.REDSTONE, pos.getX() + d1, pos.getY() + d2, pos.getZ() + d3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(LIT);
	}
}
