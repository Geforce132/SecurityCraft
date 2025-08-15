package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TrophySystemBlock extends DisguisableBlock {
	public TrophySystemBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		destroyTimeForOwner = 5.0F;
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
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
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).isTopSolid();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!canPlaceBlockAt(world, pos)) {
			dropBlockAsItemWithChance(world, pos, state, 1.0F, 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TrophySystemBlockEntity) {
			TrophySystemBlockEntity te = (TrophySystemBlockEntity) tile;

			if (te.isOwnedBy(player)) {
				if (!world.isRemote) {
					if (te.isDisabled())
						player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else
						player.openGui(SecurityCraft.instance, Screens.TROPHY_SYSTEM.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.065D, 0, 0.065D, 0.935D, 0.96D, 0.935D);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof TrophySystemBlockEntity)
				((TrophySystemBlockEntity) te).getInventory().clear();
		}

		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TrophySystemBlockEntity) {
			if (!ConfigHandler.vanillaToolBlockBreaking)
				((TrophySystemBlockEntity) te).dropAllModules();

			InventoryHelper.dropInventoryItems(world, pos, ((TrophySystemBlockEntity) te).getLensContainer());
			BlockUtils.updateIndirectNeighbors(world, pos, SCContent.trophySystem);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TrophySystemBlockEntity();
	}
}
