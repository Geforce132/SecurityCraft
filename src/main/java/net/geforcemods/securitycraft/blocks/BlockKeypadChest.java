package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class BlockKeypadChest extends BlockChest implements IPasswordConvertible{

	public BlockKeypadChest(int type){
		super(type);
	}

	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
	{
		if(world.getBlock(x, y, z) instanceof BlockKeypadChest)
		{
			effectRenderer.addBlockDestroyEffects(x, y, z, SCContent.frame, 0);
			return true;
		}
		else return false;
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(!world.isRemote) {
			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker) && world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityKeypadChest)
				((TileEntityKeypadChest) world.getTileEntity(x, y, z)).openPasswordGUI(player);

			return true;
		}

		return true;
	}

	public static void activate(World world, int x, int y, int z, EntityPlayer player){
		IInventory iinventory = ((BlockKeypadChest) world.getBlock(x, y, z)).getInventory(world, x, y, z);

		if(iinventory != null)
			player.displayGUIChest(iinventory);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		super.onBlockPlacedBy(world, x, y, z, entity, stack);

		((TileEntityKeypadChest) world.getTileEntity(x, y, z)).setOwner(((EntityPlayer) entity).getGameProfile().getId().toString(), entity.getCommandSenderName());

		if(world.getTileEntity(x + 1, y, z) != null && world.getTileEntity(x + 1, y, z) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest) world.getTileEntity(x, y, z)).setPassword(((IPasswordProtected) world.getTileEntity(x + 1, y, z)).getPassword());
		else if(world.getTileEntity(x - 1, y, z) != null && world.getTileEntity(x - 1, y, z) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest) world.getTileEntity(x, y, z)).setPassword(((IPasswordProtected) world.getTileEntity(x - 1, y, z)).getPassword());
		else if(world.getTileEntity(x, y, z + 1) != null && world.getTileEntity(x, y, z + 1) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest) world.getTileEntity(x, y, z)).setPassword(((IPasswordProtected) world.getTileEntity(x, y, z + 1)).getPassword());
		else if(world.getTileEntity(x, y, z - 1) != null && world.getTileEntity(x, y, z - 1) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest) world.getTileEntity(x, y, z)).setPassword(((IPasswordProtected) world.getTileEntity(x, y, z - 1)).getPassword());
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		if(!(world.getTileEntity(x, y, z) instanceof TileEntityKeypadChest))
			return;
		super.onNeighborBlockChange(world, x, y, z, block);
		TileEntityKeypadChest tileentitychest = (TileEntityKeypadChest)world.getTileEntity(x, y, z);

		if (tileentitychest != null)
			tileentitychest.updateContainingBlockInfo();

	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityKeypadChest();
	}

	@Override
	public Block getOriginalBlock()
	{
		return Blocks.chest;
	}

	@Override
	public boolean convert(EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntityChest chest = (TileEntityChest)world.getTileEntity(x, y, z);
		NBTTagCompound tag = new NBTTagCompound();
		int newMeta = world.getBlockMetadata(x, y, z);

		chest.writeToNBT(tag);

		for(int i = 0; i < chest.getSizeInventory(); i++)
		{
			chest.setInventorySlotContents(i, null);
		}

		world.setBlock(x, y, z, SCContent.keypadChest, newMeta, 3);
		world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
		((IOwnable) world.getTileEntity(x, y, z)).getOwner().set(player.getUniqueID().toString(), player.getCommandSenderName());
		((TileEntityChest)world.getTileEntity(x, y, z)).readFromNBT(tag);
		return true;
	}
}
