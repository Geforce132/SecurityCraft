package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPocketWallBlock extends OwnableBlock implements ITileEntityProvider {
	public static final PropertyBool SEE_THROUGH = PropertyBool.create("see_through");
	public static final PropertyBool SOLID = PropertyBool.create("solid");
	private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

	public BlockPocketWallBlock() {
		super(Material.ROCK);
		setDefaultState(blockState.getBaseState().withProperty(SEE_THROUGH, true).withProperty(SOLID, false));
		destroyTimeForOwner = 0.8F;
		setHarvestLevel("pickaxe", 0);
		blockMapColor = MapColor.CYAN;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
		if (!state.getValue(SOLID) && entity instanceof EntityPlayer) {
			TileEntity te1 = world.getTileEntity(pos);

			if (te1 instanceof BlockPocketBlockEntity) {
				BlockPocketBlockEntity te = (BlockPocketBlockEntity) te1;

				if (te.isOwnedBy(entity) || te.getManager() == null || te.getManager().isAllowed(entity))
					return;
			}
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(SOLID) ? FULL_BLOCK_AABB : EMPTY_AABB;
	}

	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return !(state.getValue(SEE_THROUGH) && world.getBlockState(pos.offset(side)).getBlock() == SCContent.blockPocketWall);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return !state.getValue(SEE_THROUGH);
	}

	@Override
	public float getAmbientOcclusionLightValue(IBlockState state) {
		return state.getValue(SEE_THROUGH) ? 1.0F : super.getAmbientOcclusionLightValue(state);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(SEE_THROUGH, true);
	}

	//meta | see_through | solid
	//0    | 1			 | 0
	//1    | 0			 | 0
	//2    | 1			 | 1
	//3    | 0			 | 1

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(SEE_THROUGH, meta == 0 || meta == 2).withProperty(SOLID, meta == 2 || meta == 3);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int solid = state.getValue(SOLID) ? 0 : 2;
		return state.getValue(SEE_THROUGH) ? 1 + solid : 0 + solid;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, SEE_THROUGH, SOLID);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BlockPocketBlockEntity();
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}
}
