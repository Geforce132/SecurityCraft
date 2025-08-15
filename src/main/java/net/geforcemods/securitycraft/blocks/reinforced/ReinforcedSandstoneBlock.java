package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedSandstoneBlock extends BlockSandStone implements ITileEntityProvider, IReinforcedBlock {
	private final float destroyTimeForOwner;

	public ReinforcedSandstoneBlock() {
		setBlockUnbreakable();
		setSoundType(SoundType.STONE);
		destroyTimeForOwner = getVanillaBlocks().get(0).blockHardness;
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getPlayerRelativeBlockHardness, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess level, BlockPos pos, EntityPlayer player) {
		return ConfigHandler.alwaysDrop || super.canHarvestBlock(level, pos, player);
	}

	@Override
	public Material getMaterial(IBlockState state) {
		return convertToVanillaState(state).getMaterial();
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getSoundType(vanillaState, world, pos, entity);
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess level, BlockPos pos) {
		return convertToVanillaState(state).getMapColor(level, pos);
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestTool(vanillaState);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().isToolEffective(type, vanillaState);
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		IBlockState vanillaState = convertToVanillaState(state);

		return vanillaState.getBlock().getHarvestLevel(vanillaState);
	}

	@Override
	public boolean isTranslucent(IBlockState state) {
		return convertToVanillaState(state).isTranslucent();
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		return Float.MAX_VALUE;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return Float.MAX_VALUE;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(TYPE, BlockSandStone.EnumType.byMetadata(meta));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new OwnableBlockEntity();
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.SANDSTONE);
	}
}
