package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HorizontalReinforcedIronBarsBlock extends OwnableBlock {
	protected static final AxisAlignedBB SHAPE = new AxisAlignedBB(-8.0D / 16D, 14.0D / 16D, -8.0D / 16D, 24.0D / 16D, 16.0D / 16D, 24.0D / 16D);

	public HorizontalReinforcedIronBarsBlock() {
		super(Material.IRON);
		setSoundType(SoundType.METAL);
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
		return SCContent.reinforcedIronBars.getDefaultState().getBlockHardness(world, pos);
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return SCContent.reinforcedIronBars.getDefaultState().getMaterial();
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState vanillaState = SCContent.reinforcedIronBars.getDefaultState();

		return vanillaState.getBlock().getSoundType(vanillaState, world, pos, entity);
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess level, BlockPos pos) {
		return SCContent.reinforcedIronBars.getDefaultState().getMapColor(level, pos);
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		return SCContent.reinforcedIronBars.getHarvestTool(SCContent.reinforcedIronBars.getDefaultState());
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return SCContent.reinforcedIronBars.isToolEffective(type, SCContent.reinforcedIronBars.getDefaultState());
	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return SCContent.reinforcedIronBars.getDefaultState().isTranslucent();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, SHAPE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return NonNullList.from(ItemStack.EMPTY);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(SCContent.reinforcedIronBars);
	}
}