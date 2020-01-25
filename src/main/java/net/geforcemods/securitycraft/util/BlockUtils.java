package net.geforcemods.securitycraft.util;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.KeycardReaderBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadTileEntity;
import net.geforcemods.securitycraft.tileentity.OwnableTileEntity;
import net.geforcemods.securitycraft.tileentity.PortableRadarTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockUtils{
	private static final List<Block> PRESSURE_PLATES = Arrays.asList(new Block[] {
			SCContent.reinforcedStonePressurePlate,
			SCContent.reinforcedOakPressurePlate,
			SCContent.reinforcedSprucePressurePlate,
			SCContent.reinforcedBirchPressurePlate,
			SCContent.reinforcedJunglePressurePlate,
			SCContent.reinforcedAcaciaPressurePlate,
			SCContent.reinforcedDarkOakPressurePlate
	});

	public static boolean isSideSolid(IWorldReader world, BlockPos pos, Direction side)
	{
		return Block.hasSolidSide(world.getBlockState(pos), world, pos, side);
	}

	/**
	 * Updates a block and notify's neighboring blocks of a change.
	 *
	 * Args: worldObj, pos, blockID, tickRate, shouldUpdate
	 *
	 *
	 */
	public static void updateAndNotify(World world, BlockPos pos, Block block, int delay, boolean shouldUpdate){
		if(shouldUpdate)
			world.getPendingBlockTicks().scheduleTick(pos, block, delay);
		world.neighborChanged(pos.east(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.west(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.south(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.north(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.up(), world.getBlockState(pos).getBlock(), pos);
		world.neighborChanged(pos.down(), world.getBlockState(pos).getBlock(), pos);
	}

	public static Block getBlock(World world, BlockPos pos){
		return world.getBlockState(pos).getBlock();
	}

	public static Block getBlock(IBlockReader access, BlockPos pos){
		return access.getBlockState(pos).getBlock();
	}

	public static Block getBlock(World world, int x, int y, int z){
		return world.getBlockState(toPos(x, y, z)).getBlock();
	}

	public static void setBlockProperty(World world, BlockPos pos, BooleanProperty property, boolean value) {
		setBlockProperty(world, pos, property, value, false);
	}

	public static void setBlockProperty(World world, BlockPos pos, BooleanProperty property, boolean value, boolean retainOldTileEntity) {
		if(retainOldTileEntity){
			NonNullList<ItemStack> modules = null;
			NonNullList<ItemStack> inventory = null;
			int[] times = new int[4];
			String password = "";
			Owner owner = null;
			int cooldown = -1;

			if(world.getTileEntity(pos) instanceof CustomizableTileEntity)
				modules = ((CustomizableTileEntity) world.getTileEntity(pos)).modules;

			if(world.getTileEntity(pos) instanceof KeypadFurnaceTileEntity){
				inventory = ((KeypadFurnaceTileEntity) world.getTileEntity(pos)).furnaceItemStacks;
				times[0] = ((KeypadFurnaceTileEntity) world.getTileEntity(pos)).furnaceBurnTime;
				times[1] = ((KeypadFurnaceTileEntity) world.getTileEntity(pos)).currentItemBurnTime;
				times[2] = ((KeypadFurnaceTileEntity) world.getTileEntity(pos)).cookTime;
				times[3] = ((KeypadFurnaceTileEntity) world.getTileEntity(pos)).totalCookTime;
			}

			if(world.getTileEntity(pos) instanceof OwnableTileEntity && ((OwnableTileEntity) world.getTileEntity(pos)).getOwner() != null)
				owner = ((OwnableTileEntity) world.getTileEntity(pos)).getOwner();

			if(world.getTileEntity(pos) instanceof KeypadTileEntity && ((KeypadTileEntity) world.getTileEntity(pos)).getPassword() != null)
				password = ((KeypadTileEntity) world.getTileEntity(pos)).getPassword();

			if(world.getTileEntity(pos) instanceof KeypadFurnaceTileEntity && ((KeypadFurnaceTileEntity) world.getTileEntity(pos)).getPassword() != null)
				password = ((KeypadFurnaceTileEntity) world.getTileEntity(pos)).getPassword();

			if(world.getTileEntity(pos) instanceof KeypadChestTileEntity && ((KeypadChestTileEntity) world.getTileEntity(pos)).getPassword() != null)
				password = ((KeypadChestTileEntity) world.getTileEntity(pos)).getPassword();

			if(world.getTileEntity(pos) instanceof PortableRadarTileEntity && ((PortableRadarTileEntity) world.getTileEntity(pos)).getAttackCooldown() != 0)
				cooldown = ((PortableRadarTileEntity) world.getTileEntity(pos)).getAttackCooldown();

			TileEntity tileEntity = world.getTileEntity(pos);
			world.setBlockState(pos, world.getBlockState(pos).with(property, value));
			world.setTileEntity(pos, tileEntity);

			if(modules != null)
				((CustomizableTileEntity) world.getTileEntity(pos)).modules = modules;

			if(inventory != null && world.getTileEntity(pos) instanceof KeypadFurnaceTileEntity){
				((KeypadFurnaceTileEntity) world.getTileEntity(pos)).furnaceItemStacks = inventory;
				((KeypadFurnaceTileEntity) world.getTileEntity(pos)).furnaceBurnTime = times[0];
				((KeypadFurnaceTileEntity) world.getTileEntity(pos)).currentItemBurnTime = times[1];
				((KeypadFurnaceTileEntity) world.getTileEntity(pos)).cookTime = times[2];
				((KeypadFurnaceTileEntity) world.getTileEntity(pos)).totalCookTime = times[3];
			}

			if(owner != null)
				((OwnableTileEntity) world.getTileEntity(pos)).getOwner().set(owner);

			if(!password.isEmpty() && world.getTileEntity(pos) instanceof KeypadTileEntity)
				((KeypadTileEntity) world.getTileEntity(pos)).setPassword(password);

			if(!password.isEmpty() && world.getTileEntity(pos) instanceof KeypadFurnaceTileEntity)
				((KeypadFurnaceTileEntity) world.getTileEntity(pos)).setPassword(password);

			if(!password.isEmpty() && world.getTileEntity(pos) instanceof KeypadChestTileEntity)
				((KeypadChestTileEntity) world.getTileEntity(pos)).setPassword(password);

			if(cooldown != -1 && world.getTileEntity(pos) instanceof PortableRadarTileEntity)
				((PortableRadarTileEntity) world.getTileEntity(pos)).setAttackCooldown(cooldown);
		}
		else
			world.setBlockState(pos, world.getBlockState(pos).with(property, value));
	}

	public static void setBlockProperty(World world, BlockPos pos, IntegerProperty property, int value) {
		world.setBlockState(pos, world.getBlockState(pos).with(property, value));
	}

	public static boolean hasBlockProperty(World world, BlockPos pos, IProperty<?> property){
		try{
			world.getBlockState(pos).get(property);
			return true;
		}catch(IllegalArgumentException e){
			return false;
		}
	}

	public static boolean getBlockPropertyAsBoolean(World world, BlockPos pos, BooleanProperty property){
		return world.getBlockState(pos).get(property).booleanValue();
	}

	public static int getBlockPropertyAsInteger(World world, BlockPos pos, IntegerProperty property){
		return world.getBlockState(pos).get(property).intValue();
	}

	public static Direction getBlockPropertyAsEnum(World world, BlockPos pos, EnumProperty<?> property){
		return ((Direction) world.getBlockState(pos).get(property));
	}

	public static Direction getBlockProperty(World world, BlockPos pos, DirectionProperty property) {
		return world.getBlockState(pos).get(property);
	}

	public static Material getBlockMaterial(IWorldReader world, BlockPos pos){
		return world.getBlockState(pos).getMaterial();
	}

	/**
	 * returns an AABB with corners x1, y1, z1 and x2, y2, z2
	 */
	public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		double d6 = Math.min(x1, x2);
		double d7 = Math.min(y1, y2);
		double d8 = Math.min(z1, z2);
		double d9 = Math.max(x1, x2);
		double d10 = Math.max(y1, y2);
		double d11 = Math.max(z1, z2);
		return new AxisAlignedBB(d6, d7, d8, d9, d10, d11);
	}

	public static BlockPos toPos(int x, int y, int z){
		return new BlockPos(x, y, z);
	}

	public static int[] fromPos(BlockPos pos){
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}

	public static boolean hasActiveSCBlockNextTo(World world, BlockPos pos)
	{
		return hasActiveLaserNextTo(world, pos) || hasActiveScannerNextTo(world, pos) || hasActiveKeypadNextTo(world, pos) || hasActiveReaderNextTo(world, pos) || hasActiveInventoryScannerNextTo(world, pos) || hasActiveReinforcedPressurePlateNextTo(world, pos);
	}

	private static boolean hasActiveLaserNextTo(World world, BlockPos pos) {
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), LaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), LaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), LaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), LaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), LaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.laserBlock && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), LaserBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveScannerNextTo(World world, BlockPos pos) {
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), RetinalScannerBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), RetinalScannerBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), RetinalScannerBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), RetinalScannerBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), RetinalScannerBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.retinalScanner && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), RetinalScannerBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveKeypadNextTo(World world, BlockPos pos){
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), KeypadBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), KeypadBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), KeypadBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), KeypadBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), KeypadBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.keypad && ((Boolean) BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), KeypadBlock.POWERED)).booleanValue())
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveReaderNextTo(World world, BlockPos pos){
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.east(), KeycardReaderBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.west(), KeycardReaderBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.south(), KeycardReaderBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.north(), KeycardReaderBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.up(), KeycardReaderBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.keycardReader && BlockUtils.getBlockPropertyAsBoolean(world, pos.down(), KeycardReaderBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveInventoryScannerNextTo(World world, BlockPos pos){
		if(BlockUtils.getBlock(world, pos.east()) == SCContent.inventoryScanner && ((InventoryScannerTileEntity) world.getTileEntity(pos.east())).getScanType().equals("redstone") && ((InventoryScannerTileEntity) world.getTileEntity(pos.east())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.west()) == SCContent.inventoryScanner && ((InventoryScannerTileEntity) world.getTileEntity(pos.west())).getScanType().equals("redstone") && ((InventoryScannerTileEntity) world.getTileEntity(pos.west())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.south()) == SCContent.inventoryScanner && ((InventoryScannerTileEntity) world.getTileEntity(pos.south())).getScanType().equals("redstone") && ((InventoryScannerTileEntity) world.getTileEntity(pos.south())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.north()) == SCContent.inventoryScanner && ((InventoryScannerTileEntity) world.getTileEntity(pos.north())).getScanType().equals("redstone") && ((InventoryScannerTileEntity) world.getTileEntity(pos.north())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.up()) == SCContent.inventoryScanner && ((InventoryScannerTileEntity) world.getTileEntity(pos.up())).getScanType().equals("redstone") && ((InventoryScannerTileEntity) world.getTileEntity(pos.up())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(BlockUtils.getBlock(world, pos.down()) == SCContent.inventoryScanner && ((InventoryScannerTileEntity) world.getTileEntity(pos.down())).getScanType().equals("redstone") && ((InventoryScannerTileEntity) world.getTileEntity(pos.down())).shouldProvidePower())
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}

	private static boolean hasActiveReinforcedPressurePlateNextTo(World world, BlockPos pos){
		if(PRESSURE_PLATES.contains(BlockUtils.getBlock(world, pos.east())) && world.getBlockState(pos.east()).get(ReinforcedPressurePlateBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.east())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(PRESSURE_PLATES.contains(BlockUtils.getBlock(world, pos.west())) && world.getBlockState(pos.west()).get(ReinforcedPressurePlateBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.west())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(PRESSURE_PLATES.contains(BlockUtils.getBlock(world, pos.south())) && world.getBlockState(pos.south()).get(ReinforcedPressurePlateBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.south())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(PRESSURE_PLATES.contains(BlockUtils.getBlock(world, pos.north())) && world.getBlockState(pos.north()).get(ReinforcedPressurePlateBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.north())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(PRESSURE_PLATES.contains(BlockUtils.getBlock(world, pos.up())) && world.getBlockState(pos.up()).get(ReinforcedPressurePlateBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.up())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else if(PRESSURE_PLATES.contains(BlockUtils.getBlock(world, pos.down())) && world.getBlockState(pos.down()).get(ReinforcedPressurePlateBlock.POWERED))
			return ((IOwnable) world.getTileEntity(pos.down())).getOwner().owns((IOwnable) world.getTileEntity(pos));
		else
			return false;
	}
}
