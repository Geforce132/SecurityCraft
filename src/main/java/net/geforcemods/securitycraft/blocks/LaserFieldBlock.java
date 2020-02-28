package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class LaserFieldBlock extends OwnableBlock implements IIntersectable{

	public static final IntegerProperty BOUNDTYPE = IntegerProperty.create("boundtype", 1, 3);
	private static final VoxelShape SHAPE_X = Block.makeCuboidShape(0, 6.75, 6.75, 16, 9.25, 9.25);
	private static final VoxelShape SHAPE_Y = Block.makeCuboidShape(6.75, 0, 6.75, 9.25, 16, 9.25);
	private static final VoxelShape SHAPE_Z = Block.makeCuboidShape(6.75, 6.75, 0, 9.25, 9.25, 16);

	public LaserFieldBlock(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(BOUNDTYPE, 1));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote && entity instanceof LivingEntity && !EntityUtils.doesMobHavePotionEffect((LivingEntity) entity, ForgeRegistries.POTIONS.getValue(new ResourceLocation("minecraft:invisibility"))))
		{
			for(Direction facing : Direction.values())
			{
				for(int i = 0; i < ConfigHandler.CONFIG.laserBlockRange.get(); i++)
				{
					BlockPos offsetPos = pos.offset(facing, i);
					Block block = world.getBlockState(offsetPos).getBlock();

					if(block == SCContent.LASER_BLOCK.get() && !BlockUtils.getBlockProperty(world, offsetPos, LaserBlock.POWERED))
					{
						TileEntity te = world.getTileEntity(offsetPos);

						if(te instanceof CustomizableTileEntity && ((CustomizableTileEntity)te).hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, offsetPos, CustomModules.WHITELIST).contains(((LivingEntity) entity).getName().getFormattedText().toLowerCase()))
							return;

						BlockUtils.setBlockProperty(world, offsetPos, LaserBlock.POWERED, true, true);
						world.notifyNeighborsOfStateChange(offsetPos, SCContent.LASER_BLOCK.get());
						world.getPendingBlockTicks().scheduleTick(offsetPos, SCContent.LASER_BLOCK.get(), 50);

						if(te instanceof CustomizableTileEntity && ((CustomizableTileEntity)te).hasModule(CustomModules.HARMING))
						{
							if(!(entity instanceof PlayerEntity && ((IOwnable)te).getOwner().isOwner((PlayerEntity)entity)))
								((LivingEntity) entity).attackEntityFrom(CustomDamageSources.LASER, 10F);
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
	public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state)
	{
		if(!world.isRemote())
		{
			for(Direction facing : Direction.values())
			{
				for(int i = 0; i < ConfigHandler.CONFIG.laserBlockRange.get(); i++)
				{
					if(BlockUtils.getBlock(world, pos.offset(facing, i)) == SCContent.LASER_BLOCK.get())
					{
						for(int j = 1; j < i; j++)
						{
							world.destroyBlock(pos.offset(facing, j), false);
						}
					}
				}
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		if(source.getBlockState(pos).getBlock() instanceof LaserFieldBlock)
		{
			int boundType = source.getBlockState(pos).get(BOUNDTYPE);

			if (boundType == 1)
				return SHAPE_Y;
			else if (boundType == 2)
				return SHAPE_Z;
			else if (boundType == 3)
				return SHAPE_X;
		}

		return VoxelShapes.empty();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(BOUNDTYPE, 1);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(BOUNDTYPE);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SecurityCraftTileEntity().intersectsEntities();
	}

}
