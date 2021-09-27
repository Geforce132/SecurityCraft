package net.geforcemods.securitycraft.blocks;

import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKeypadFurnace extends BlockOwnable {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool OPEN = PropertyBool.create("open");

	public BlockKeypadFurnace(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
		{
			TileEntityKeypadFurnace te = (TileEntityKeypadFurnace)world.getTileEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return true;
			}
			else if(ModuleUtils.isAllowed(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(state, world, pos, player);
			}
			else if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker, hand))
				te.openPasswordGUI(player);
		}

		return true;
	}

	public void activate(IBlockState state, World world, BlockPos pos, EntityPlayer player){
		if(!state.getValue(OPEN))
			world.setBlockState(pos, state.withProperty(OPEN, true));

		world.playEvent(null, 1006, pos, 0);
		player.openGui(SecurityCraft.instance, GuiHandler.KEYPAD_FURNACE_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		return (state.getValue(OPEN) && te != null && te instanceof TileEntityKeypadFurnace && ((TileEntityKeypadFurnace)te).isBurning()) ? 15 : 0;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(OPEN, false);
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
		if(state.getValue(OPEN))
			return (state.getValue(FACING).getIndex() + 6);
		else
			return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, OPEN);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityKeypadFurnace();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror)
	{
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}

	public static class Convertible implements Function<Object,IPasswordConvertible>, IPasswordConvertible
	{
		@Override
		public IPasswordConvertible apply(Object o)
		{
			return this;
		}

		@Override
		public Block getOriginalBlock()
		{
			return Blocks.FURNACE;
		}

		@Override
		public boolean convert(EntityPlayer player, World world, BlockPos pos)
		{
			EnumFacing facing = world.getBlockState(pos).getValue(FACING);
			TileEntityFurnace furnace = (TileEntityFurnace)world.getTileEntity(pos);
			NBTTagCompound tag = furnace.writeToNBT(new NBTTagCompound());
			TileEntity newTe;

			furnace.clear();
			world.setBlockState(pos, SCContent.keypadFurnace.getDefaultState().withProperty(FACING, facing).withProperty(OPEN, false));
			newTe = world.getTileEntity(pos);
			((TileEntityKeypadFurnace)newTe).readFromNBT(tag);
			((IOwnable)newTe).setOwner(player.getUniqueID().toString(), player.getName());
			return true;
		}
	}
}
