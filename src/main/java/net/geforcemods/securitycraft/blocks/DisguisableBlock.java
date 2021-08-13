package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class DisguisableBlock extends OwnableBlock implements IOverlayDisplay
{
	public DisguisableBlock(Block.Properties properties)
	{
		super(properties);
	}

	public static boolean isNormalCube(BlockState state, BlockGetter world, BlockPos pos)
	{
		if(state.getBlock() instanceof DisguisableBlock disguisableBlock) //should not happen, but just to be safe
		{
			BlockState disguisedState = disguisableBlock.getDisguisedStateOrDefault(state, world, pos);

			if(disguisedState.getBlock() != state.getBlock())
				return disguisedState.isRedstoneConductor(world, pos);
		}

		return state.getMaterial().isSolidBlocking() && state.isCollisionShapeFullBlock(world, pos);
	}

	public static boolean isSuffocating(BlockState state, BlockGetter world, BlockPos pos)
	{
		if(state.getBlock() instanceof DisguisableBlock disguisableBlock) //should not happen, but just to be safe
		{
			BlockState disguisedState = disguisableBlock.getDisguisedStateOrDefault(state, world, pos);

			if(disguisedState.getBlock() != state.getBlock())
				return disguisedState.isSuffocating(world, pos);
		}

		return state.getMaterial().blocksMotion() && state.isCollisionShapeFullBlock(world, pos);
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getSoundType(world, pos, entity);
		else return super.getSoundType(state, world, pos, entity);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getShape(world, pos, ctx);
		else return super.getShape(state, world, pos, ctx);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getCollisionShape(world, pos, ctx);
		else return super.getCollisionShape(state, world, pos, ctx);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getOcclusionShape(world, pos);
		else return super.getOcclusionShape(state, world, pos);
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getShadeBrightness(world, pos);
		else return super.getShadeBrightness(state, world, pos);
	}

	public final BlockState getDisguisedStateOrDefault(BlockState state, BlockGetter world, BlockPos pos)
	{
		BlockState disguisedState = getDisguisedBlockState(world, pos);

		return disguisedState != null ? disguisedState : state;
	}

	public BlockState getDisguisedBlockState(BlockGetter world, BlockPos pos)
	{
		if(world.getBlockEntity(pos) instanceof DisguisableBlockEntity te)
		{
			ItemStack module = te.hasModule(ModuleType.DISGUISE) ? te.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY;

			if(!module.isEmpty() && !((ModuleItem) module.getItem()).getBlockAddons(module.getTag()).isEmpty())
				return ((ModuleItem) module.getItem()).getBlockAddons(module.getTag()).get(0).defaultBlockState();
		}

		return null;
	}

	public ItemStack getDisguisedStack(BlockGetter world, BlockPos pos)
	{
		if(world != null && world.getBlockEntity(pos) instanceof DisguisableBlockEntity te)
		{
			ItemStack stack = te.hasModule(ModuleType.DISGUISE) ? te.getModule(ModuleType.DISGUISE) : ItemStack.EMPTY;

			if(!stack.isEmpty() && !((ModuleItem) stack.getItem()).getBlockAddons(stack.getTag()).isEmpty())
			{
				ItemStack disguisedStack = ((ModuleItem) stack.getItem()).getAddons(stack.getTag()).get(0);

				if(Block.byItem(disguisedStack.getItem()) != this)
					return disguisedStack;
			}
		}

		return new ItemStack(this);
	}

	@Override
	public ItemStack getDisplayStack(Level world, BlockState state, BlockPos pos)
	{
		return getDisguisedStack(world, pos);
	}

	@Override
	public boolean shouldShowSCInfo(Level world, BlockState state, BlockPos pos)
	{
		return getDisguisedStack(world, pos).getItem() == asItem();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
	{
		return getDisguisedStack(world, pos);
	}
}
