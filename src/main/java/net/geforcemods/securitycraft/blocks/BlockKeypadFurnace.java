package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKeypadFurnace extends BlockOwnable implements IPasswordConvertible {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool OPEN = PropertyBool.create("open");

	public BlockKeypadFurnace(Material material) {
		super(material);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube()
	{
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof IInventory)
		{
			InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileentity);
			world.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote)
		{
			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker))
				((TileEntityKeypadFurnace) world.getTileEntity(pos)).openPasswordGUI(player);
		}

		return true;
	}

	public static void activate(World world, BlockPos pos, EntityPlayer player){
		if(!BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockKeypadFurnace.OPEN))
			BlockUtils.setBlockProperty(world, pos, BlockKeypadFurnace.OPEN, true, false);

		world.playAuxSFXAtEntity((EntityPlayer)null, 1006, pos, 0);
		player.openGui(SecurityCraft.instance, GuiHandler.KEYPAD_FURNACE_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		return (world.getBlockState(pos).getValue(OPEN) && te != null && te instanceof TileEntityKeypadFurnace && ((TileEntityKeypadFurnace)te).isBurning()) ? 15 : 0;
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(OPEN, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if(meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(OPEN, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(OPEN, true);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(state.getValue(OPEN).booleanValue())
			return state.getValue(FACING).getIndex() + 6;
		else
			return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING, OPEN});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityKeypadFurnace();
	}

	@Override
	public Block getOriginalBlock()
	{
		return Blocks.furnace;
	}

	@Override
	public boolean convert(EntityPlayer player, World world, BlockPos pos)
	{
		EnumFacing facing = world.getBlockState(pos).getValue(FACING);
		TileEntityFurnace furnace = (TileEntityFurnace)world.getTileEntity(pos);
		NBTTagCompound tag = new NBTTagCompound();

		furnace.writeToNBT(tag);
		furnace.clear();
		world.setBlockState(pos, SCContent.keypadFurnace.getDefaultState().withProperty(FACING, facing).withProperty(OPEN, false));
		((IOwnable) world.getTileEntity(pos)).getOwner().set(player.getUniqueID().toString(), player.getName());
		((TileEntityKeypadFurnace)world.getTileEntity(pos)).readFromNBT(tag);
		return true;
	}
}
