package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClaymoreBlock extends ExplosiveBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");
	public static final AxisAlignedBB NS_BOUNDING_BOX;
	public static final AxisAlignedBB EW_BOUNDING_BOX;

	static {
		float px = 0.0625F;

		NS_BOUNDING_BOX = new AxisAlignedBB(4.0F * px, 0.0F, 6.0F * px, 12.0F * px, 7.0F * px, 10.0F * px);
		EW_BOUNDING_BOX = new AxisAlignedBB(10.0F * px, 0.0F, 12.0F * px, 6.0F * px, 7.0F * px, 4.0F * px);
	}

	public ClaymoreBlock(Material material) {
		super(material);
		destroyTimeForOwner = 3.5F;
		setHarvestLevel("pickaxe", 1);
		blockMapColor = MapColor.GREEN_STAINED_HARDENED_CLAY;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity be = world.getTileEntity(pos);

		if (be instanceof ClaymoreBlockEntity && ((ClaymoreBlockEntity) be).isOwnedBy(player)) {
			if (!world.isRemote)
				player.openGui(SecurityCraft.instance, Screens.SINGLE_LENS.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());

			return true;
		}

		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));
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
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
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

		if (te instanceof ClaymoreBlockEntity) {
			if (!ConfigHandler.vanillaToolBlockBreaking)
				((ClaymoreBlockEntity) te).dropAllModules();

			InventoryHelper.dropInventoryItems(world, pos, ((ClaymoreBlockEntity) te).getLensContainer());
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).getBlock().isSideSolid(world.getBlockState(pos.down()), world, pos.down(), EnumFacing.UP);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!player.capabilities.isCreativeMode && !world.isRemote && !world.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED)) {
			ClaymoreBlockEntity claymore = (ClaymoreBlockEntity) world.getTileEntity(pos);

			if (claymore.getTargetingMode().allowsPlayers() && (!claymore.isOwnedBy(player) || !claymore.ignoresOwner()))
				explode(world, pos);
		}

		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);

			if (state.getPropertyKeys().contains(ClaymoreBlock.DEACTIVATED) && !state.getValue(ClaymoreBlock.DEACTIVATED)) {
				if (pos.equals(new BlockPos(explosion.getPosition())))
					return;

				explode(world, pos);
			}
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(DEACTIVATED, false);
	}

	@Override
	public boolean activateMine(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);

		if (state.getValue(DEACTIVATED)) {
			world.setBlockState(pos, state.withProperty(DEACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);

		if (!state.getValue(DEACTIVATED)) {
			world.setBlockState(pos, state.withProperty(DEACTIVATED, true));
			return true;
		}

		return false;
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if (!world.isRemote) {
			world.destroyBlock(pos, false);
			world.newExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.smallerMineExplosion ? 1.5F : 3.5F, ConfigHandler.shouldSpawnFire, ConfigHandler.mineExplosionsBreakBlocks);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
			case NORTH:
			case SOUTH:
				return NS_BOUNDING_BOX;
			case EAST:
			case WEST:
				return EW_BOUNDING_BOX;
			default:
				return Block.FULL_BLOCK_AABB;
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(DEACTIVATED, true);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(DEACTIVATED, false);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getValue(DEACTIVATED))
			return (state.getValue(FACING).getIndex() + 6);
		else
			return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ClaymoreBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}
}
