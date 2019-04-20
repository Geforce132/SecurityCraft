package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockLaserField extends BlockContainer implements IIntersectable{

	public static final IntegerProperty BOUNDTYPE = IntegerProperty.create("boundtype", 1, 3);
	private static final VoxelShape SHAPE_X = Block.makeCuboidShape(0, 4, 4, 16, 12, 12);
	private static final VoxelShape SHAPE_Y = Block.makeCuboidShape(4, 0, 4, 12, 16, 12);
	private static final VoxelShape SHAPE_Z = Block.makeCuboidShape(4, 4, 0, 12, 12, 16);

	public BlockLaserField(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(BOUNDTYPE, 1));
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState blockState, IBlockReader world, BlockPos pos)
	{
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote && entity instanceof EntityLivingBase && !EntityUtils.doesMobHavePotionEffect((EntityLivingBase) entity, ForgeRegistries.POTIONS.getValue(new ResourceLocation("minecraft:invisibility"))))
		{
			for(EnumFacing facing : EnumFacing.values())
			{
				for(int i = 0; i < CommonConfig.CONFIG.laserBlockRange.get(); i++)
				{
					BlockPos offsetPos = pos.offset(facing, i);
					Block block = world.getBlockState(offsetPos).getBlock();

					if(block == SCContent.laserBlock && !BlockUtils.getBlockPropertyAsBoolean(world, offsetPos, BlockLaserBlock.POWERED))
					{
						TileEntity te = world.getTileEntity(offsetPos);

						if(te instanceof CustomizableSCTE && ((CustomizableSCTE)te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, offsetPos, EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getName().getFormattedText().toLowerCase()))
							return;

						BlockUtils.setBlockProperty(world, offsetPos, BlockLaserBlock.POWERED, true, true);
						world.notifyNeighborsOfStateChange(offsetPos, SCContent.laserBlock);
						world.getPendingBlockTicks().scheduleTick(offsetPos, SCContent.laserBlock, 50);

						if(te instanceof CustomizableSCTE && ((CustomizableSCTE)te).hasModule(EnumCustomModules.HARMING))
							((EntityLivingBase) entity).attackEntityFrom(CustomDamageSources.laser, 10F);

					}
				}
			}
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, pos, state
	 */
	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, IBlockState state)
	{
		if(!world.isRemote())
		{
			for(EnumFacing facing : EnumFacing.values())
			{
				for(int i = 0; i < CommonConfig.CONFIG.laserBlockRange.get(); i++)
				{
					if(BlockUtils.getBlock(world, pos.offset(facing, i)) == SCContent.laserBlock)
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
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		if(source.getBlockState(pos).getBlock() instanceof BlockLaserField)
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
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return getDefaultState().with(BOUNDTYPE, 1);
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(BOUNDTYPE);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, EntityPlayer player)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntitySCTE().intersectsEntities();
	}

}
