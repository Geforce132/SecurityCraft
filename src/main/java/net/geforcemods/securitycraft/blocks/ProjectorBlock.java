package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProjectorBlock extends DisguisableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool HANGING = PropertyBool.create("hanging");
	private static final AxisAlignedBB FLOOR_NORTH = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 10.0F / 16.0F, 15.0F / 16.0F);
	private static final AxisAlignedBB FLOOR_EAST = new AxisAlignedBB(1.0F / 16.0F, 0.0F, 0.0F, 1.0F, 10.0F / 16.0F, 1.0F);
	private static final AxisAlignedBB FLOOR_SOUTH = new AxisAlignedBB(0.0F, 0.0F, 1.0F / 16.0F, 1.0F, 10.0F / 16.0F, 1.0F);
	private static final AxisAlignedBB FLOOR_WEST = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 15.0F / 16.0F, 10.0F / 16.0F, 1.0F);
	private static final AxisAlignedBB CEILING = new AxisAlignedBB(1.0F / 16.0F, 6.0F / 16.0F, 1.0F / 16.0F, 15.0F / 16.0F, 11.0F / 16.0F, 15.0F / 16.0F);

	public ProjectorBlock() {
		super(Material.IRON);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HANGING, false));
		setSoundType(SoundType.METAL);
		setHardness(5.0F);
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (!state.getValue(HANGING)) {
			switch (state.getValue(FACING)) {
				case NORTH:
					return FLOOR_NORTH;
				case EAST:
					return FLOOR_EAST;
				case SOUTH:
					return FLOOR_SOUTH;
				case WEST:
					return FLOOR_WEST;
				default:
					return super.getBoundingBox(state, world, pos);
			}
		}
		else
			return CEILING;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState actualState = getDisguisedBlockState(world.getTileEntity(pos));

		if (actualState != null && actualState.getBlock() != this)
			return actualState.getCollisionBoundingBox(world, pos);
		else
			return getBoundingBox(state, world, pos);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);

		if (!(te instanceof ProjectorBlockEntity))
			return false;

		boolean isOwner = ((ProjectorBlockEntity) te).isOwnedBy(player);

		if (!world.isRemote && isOwner)
			player.openGui(SecurityCraft.instance, Screens.PROJECTOR.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());

		return isOwner;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side != null && side.getAxis() == Axis.Y;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof ProjectorBlockEntity) {
			// Drop the block being projected
			EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), ((ProjectorBlockEntity) te).getStackInSlot(36));

			Utils.addScheduledTask(world, () -> world.spawnEntity(item));
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof ProjectorBlockEntity) {
				ProjectorBlockEntity te = (ProjectorBlockEntity) tile;

				if (te.isActivatedByRedstone()) {
					te.setActive(world.isBlockPowered(pos));
					te.sync();
				}
			}
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!world.isBlockPowered(pos)) {
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof ProjectorBlockEntity) {
				ProjectorBlockEntity te = (ProjectorBlockEntity) tile;

				if (te.isActivatedByRedstone())
					te.setActive(false);
			}
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing clickedFace, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(HANGING, clickedFace == EnumFacing.DOWN);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() + (state.getValue(HANGING) ? 4 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta)).withProperty(HANGING, meta > 3);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, HANGING);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new ProjectorBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add(new TextComponentTranslation("tooltip.securitycraft:projector").getFormattedText());
	}
}
