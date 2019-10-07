package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocket;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBlockPocketWall extends BlockOwnable implements ITileEntityProvider, IOverlayDisplay
{
	public static final BooleanProperty SEE_THROUGH = BooleanProperty.create("see_through");

	public BlockBlockPocketWall()
	{
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 6000000.0F));

		setDefaultState(stateContainer.getBaseState().with(SEE_THROUGH, false));
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos)
	{
		return false;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		if(ctx instanceof EntitySelectionContext)
		{
			Entity entity = ((EntitySelectionContext)ctx).getEntity();

			if(entity instanceof PlayerEntity)
			{
				TileEntity te1 = world.getTileEntity(pos);

				if(te1 instanceof TileEntityBlockPocket)
				{
					TileEntityBlockPocket te = (TileEntityBlockPocket)te1;

					if(te.getManager() == null)
						return VoxelShapes.empty();

					if(te.getManager().hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(te.getManager().getWorld(), te.getManager().getPos(), EnumCustomModules.WHITELIST).contains(entity.getName().getFormattedText().toLowerCase()))
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
	public boolean causesSuffocation(BlockState state, IBlockReader world, BlockPos pos)
	{
		return !state.get(SEE_THROUGH);
	}

	@Override
	public boolean isSolid(BlockState state)
	{
		return !state.get(SEE_THROUGH);
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos)
	{
		return VoxelShapes.fullCube();
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return super.getStateForPlacement(context).with(SEE_THROUGH, true);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(SEE_THROUGH);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new TileEntityBlockPocket();
	}

	@Override
	public ItemStack getDisplayStack(World world, BlockState state, BlockPos pos)
	{
		return new ItemStack(SCContent.blockPocketWall, 1);
	}

	@Override
	public boolean shouldShowSCInfo(World world, BlockState state, BlockPos pos)
	{
		return true;
	}
}
