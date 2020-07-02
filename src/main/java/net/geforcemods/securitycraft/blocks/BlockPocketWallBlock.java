package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPocketWallBlock extends OwnableBlock implements IOverlayDisplay
{
	public static final BooleanProperty SEE_THROUGH = BooleanProperty.create("see_through");
	public static final BooleanProperty SOLID = BooleanProperty.create("solid");

	public BlockPocketWallBlock()
	{
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 6000000.0F).doesNotBlockMovement().func_235828_a_(BlockPocketWallBlock::isNormalCube).func_235842_b_(BlockPocketWallBlock::causesSuffocation));

		setDefaultState(stateContainer.getBaseState().with(SEE_THROUGH, true).with(SOLID, false));
	}

	public static boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos)
	{
		return false;
	}

	public static boolean causesSuffocation(BlockState state, IBlockReader world, BlockPos pos)
	{
		return state.get(SOLID);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		if(!state.get(SOLID) && ctx instanceof EntitySelectionContext)
		{
			Entity entity = ((EntitySelectionContext)ctx).getEntity();

			if(entity instanceof PlayerEntity)
			{
				TileEntity te1 = world.getTileEntity(pos);

				if(te1 instanceof BlockPocketTileEntity)
				{
					BlockPocketTileEntity te = (BlockPocketTileEntity)te1;

					if(te.getManager() == null)
						return VoxelShapes.empty();

					if(te.getManager().hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(te.getManager().getWorld(), te.getManager().getPos(), ModuleType.WHITELIST).contains(entity.getName().getString().toLowerCase()))
						return VoxelShapes.empty();
					else if(!te.getOwner().isOwner((PlayerEntity)entity))
						return VoxelShapes.fullCube();
					else
						return VoxelShapes.empty();
				}
			}
		}

		return VoxelShapes.fullCube();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
	{
		return state.get(SEE_THROUGH) && adjacentBlockState.getBlock() == SCContent.BLOCK_POCKET_WALL.get();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return super.getStateForPlacement(context).with(SEE_THROUGH, true);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(SEE_THROUGH, SOLID);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new BlockPocketTileEntity();
	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos)
	{
		return new ItemStack(SCContent.BLOCK_POCKET_WALL.get(), 1);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos)
	{
		return true;
	}
}
