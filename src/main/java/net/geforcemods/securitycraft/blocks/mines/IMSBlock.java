package net.geforcemods.securitycraft.blocks.mines;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IMSBlock extends OwnableBlock {
	public static final PropertyInteger MINES = PropertyInteger.create("mines", 0, 4);

	public IMSBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		setHarvestLevel("pickaxe", 1);
		blockMapColor = MapColor.GREEN_STAINED_HARDENED_CLAY;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0F, 0F, 0F, 1F, 0.45F, 1F);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (world.getBlockState(pos.down()).getMaterial() == Material.AIR)
			world.destroyBlock(pos, true);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (!ConfigHandler.vanillaToolBlockBreaking && te instanceof IModuleInventory)
			((IModuleInventory) te).dropAllModules();

		super.breakBlock(world, pos, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
		if (world.getTileEntity(pos) instanceof IMSBlockEntity && ((IMSBlockEntity) world.getTileEntity(pos)).getBombsRemaining() == 0) {
			double x = pos.getX() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.4F + (random.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;

			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);

			world.spawnParticle(EnumParticleTypes.FLAME, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.FLAME, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		int mines = state.getValue(MINES);
		ArrayList<ItemStack> drops = new ArrayList<>();


		return drops;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(MINES, 4);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(MINES, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(MINES));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, MINES);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new IMSBlockEntity();
	}

	public static class ExtractionBlock implements IExtractionBlock, Function<Object, IExtractionBlock> {
		@Override
		public IExtractionBlock apply(Object o) {
			return this;
		}

		@Override
		public boolean canExtract(IOwnable ownable, World world, BlockPos pos, IBlockState state) {
			return ownable.getOwner().owns((IMSBlockEntity) world.getTileEntity(pos));
		}

		@Override
		public Block getBlock() {
			return SCContent.ims;
		}
	}
}
