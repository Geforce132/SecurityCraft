package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class SonicSecuritySystemBlock extends DisguisableBlock {
	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.4D, 0.0D, 0.4D, 0.6D, 1.0D, 0.6D);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public SonicSecuritySystemBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(POWERED, false));
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState actualState = getDisguisedBlockState(world.getTileEntity(pos));

		if (actualState != null && actualState.getBlock() != this)
			return actualState.getBoundingBox(world, pos);
		else
			return AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState actualState = getDisguisedBlockState(world.getTileEntity(pos));

		if (actualState != null && actualState.getBlock() != this)
			return actualState.getBoundingBox(world, pos);
		else
			return NULL_AABB;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		BlockPos downPos = pos.down();

		return world.getBlockState(downPos).isSideSolid(world, downPos, EnumFacing.UP);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		TileEntity te = world.getTileEntity(pos);

		if (te instanceof SonicSecuritySystemBlockEntity) {
			((SonicSecuritySystemBlockEntity) te).transferPositionsFromItem(stack.getTagCompound());
			((SonicSecuritySystemBlockEntity) te).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player.getHeldItem(hand).getItem() != SCContent.portableTunePlayer) {
			SonicSecuritySystemBlockEntity te = (SonicSecuritySystemBlockEntity) world.getTileEntity(pos);

			if (!world.isRemote && (te.isOwnedBy(player) || te.isAllowed(player)))
				player.openGui(SecurityCraft.instance, Screens.SONIC_SECURITY_SYSTEM.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());

			return true;
		}
		else
			return false;
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

		if (te instanceof IModuleInventory)
			((IModuleInventory) te).dropAllModules();

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return state.getValue(POWERED);
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) && side == EnumFacing.UP ? 15 : 0;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POWERED);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		NBTTagCompound blockTag = world.getTileEntity(pos).getUpdateTag();
		ItemStack stack = new ItemStack(SCContent.sonicSecuritySystem);

		if (!blockTag.hasKey("LinkedBlocks"))
			return stack;

		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setTag("LinkedBlocks", blockTag.getTagList("LinkedBlocks", Constants.NBT.TAG_COMPOUND));
		return stack;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new SonicSecuritySystemBlockEntity();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POWERED) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(POWERED, meta == 1);
	}
}
