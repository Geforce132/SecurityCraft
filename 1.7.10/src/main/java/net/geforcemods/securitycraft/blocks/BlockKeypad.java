package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKeypad extends BlockContainer implements ICustomWailaDisplay, IPasswordConvertible {

	public BlockKeypad(Material material) {
		super(material);
	}

	@SideOnly(Side.CLIENT)
	private IIcon keypadIconTop;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconFront;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconFrontActive;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconDisguised;

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);

		if (entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);

		if (entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if (entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		else
			return;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;
		else {
			if(BlockUtils.isMetadataBetween(world, x, y, z, 7, 10))
				return false;

			if(ModuleUtils.checkForModule(world, x, y, z, player, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(world, x, y, z, player, EnumCustomModules.BLACKLIST))
				return true;

			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker))
				((IPasswordProtected) world.getTileEntity(x, y, z)).openPasswordGUI(player);

			return true;
		}
	}

	public static void activate(World world, int x, int y, int z){
		world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 5, 3);
		world.notifyBlocksOfNeighborChange(x, y, z, SCContent.keypad);
		world.scheduleBlockUpdate(x, y, z, SCContent.keypad, 60);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random){
		if(!world.isRemote && world.getBlockMetadata(x, y, z) > 6 && world.getBlockMetadata(x, y, z) < 11)
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 5, 3);
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side){
		if(access.getBlockMetadata(x, y, z) == 7 || access.getBlockMetadata(x, y, z) == 8 || access.getBlockMetadata(x, y, z) == 9 || access.getBlockMetadata(x, y, z) == 10)
			return 15;
		else
			return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int side){
		if(access.getBlockMetadata(x, y, z) == 7 || access.getBlockMetadata(x, y, z) == 8 || access.getBlockMetadata(x, y, z) == 9 || access.getBlockMetadata(x, y, z) == 10)
			return 15;
		else
			return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if(side == 3 && meta == 0)
			return keypadIconFront;

		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side){
		int meta = access.getBlockMetadata(x, y, z);
		TileEntityKeypad tileEntity = ((TileEntityKeypad) access.getTileEntity(x, y, z));
		boolean isDisguised = tileEntity.hasModule(EnumCustomModules.DISGUISE);

		if(isDisguised && !tileEntity.getAddonsFromModule(EnumCustomModules.DISGUISE).isEmpty()) {
			List<ItemStack> stacks = tileEntity.getAddonsFromModule(EnumCustomModules.DISGUISE);

			if(stacks.size() != 0)
			{
				ItemStack stack = stacks.get(0);
				Block disguisedAs = Block.getBlockFromItem(stack.getItem());

				return stack.getHasSubtypes() ? disguisedAs.getIcon(side, stack.getMetadata()) : disguisedAs.getIcon(side, meta);
			}
		}

		if(meta > 6 && meta < 11)
			return side == 1 ? keypadIconTop : (side == 0 ? keypadIconTop : (side != (meta - 5) ? blockIcon : keypadIconFrontActive));
		else
			return side == 1 ? keypadIconTop : (side == 0 ? keypadIconTop : (side != meta ? blockIcon : keypadIconFront));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		blockIcon = register.registerIcon("securitycraft:iron_block");
		keypadIconFront = register.registerIcon("securitycraft:keypadUnactive");
		keypadIconTop = register.registerIcon("securitycraft:iron_block");
		keypadIconFrontActive = register.registerIcon("securitycraft:keypadActive");
		keypadIconDisguised = register.registerIcon("bookshelf");
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityKeypad();
	}

	@Override
	public ItemStack getDisplayStack(World world, int x, int y, int z) {
		TileEntityKeypad tileEntity = (TileEntityKeypad) world.getTileEntity(x, y, z);

		if(tileEntity.hasModule(EnumCustomModules.DISGUISE) && !tileEntity.getAddonsFromModule(EnumCustomModules.DISGUISE).isEmpty())
			return tileEntity.getAddonsFromModule(EnumCustomModules.DISGUISE).get(0);

		return null;
	}

	@Override
	public boolean shouldShowSCInfo(World world, int x, int y, int z) {
		TileEntityKeypad tileEntity = (TileEntityKeypad) world.getTileEntity(x, y, z);

		return !tileEntity.hasModule(EnumCustomModules.DISGUISE);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		ItemStack stack = getDisplayStack(world, x, y, z);

		return stack == null ? new ItemStack(this) : stack;
	}

	@Override
	public Block getOriginalBlock()
	{
		return SCContent.frame;
	}

	@Override
	public boolean convert(EntityPlayer player, World world, int x, int y, int z)
	{
		Owner owner = ((IOwnable) world.getTileEntity(x, y, z)).getOwner();

		world.setBlock(x, y, z, SCContent.keypad, world.getBlockMetadata(x, y, z), 3);
		((IOwnable) world.getTileEntity(x, y, z)).getOwner().set(owner);
		return true;
	}
}
