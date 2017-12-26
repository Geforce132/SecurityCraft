package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
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

	public BlockKeypadChest(int par1){
		super(par1);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(!par1World.isRemote) {
			System.out.println("rc:"+par1World.getBlockMetadata(par2, par3, par4));
			if(!PlayerUtils.isHoldingItem(par5EntityPlayer, mod_SecurityCraft.codebreaker) && par1World.getTileEntity(par2, par3, par4) != null && par1World.getTileEntity(par2, par3, par4) instanceof TileEntityKeypadChest)
				((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).openPasswordGUI(par5EntityPlayer);

			return true;
		}

		return true;
	}

	public static void activate(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer){
		IInventory iinventory = ((BlockKeypadChest) par1World.getBlock(par2, par3, par4)).getInventory(par1World, par2, par3, par4);

		if(iinventory != null)
			par5EntityPlayer.displayGUIChest(iinventory);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);

		((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());

		if(par1World.getTileEntity(par2 + 1, par3, par4) != null && par1World.getTileEntity(par2 + 1, par3, par4) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setPassword(((IPasswordProtected) par1World.getTileEntity(par2 + 1, par3, par4)).getPassword());
		else if(par1World.getTileEntity(par2 - 1, par3, par4) != null && par1World.getTileEntity(par2 - 1, par3, par4) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setPassword(((IPasswordProtected) par1World.getTileEntity(par2 - 1, par3, par4)).getPassword());
		else if(par1World.getTileEntity(par2, par3, par4 + 1) != null && par1World.getTileEntity(par2, par3, par4 + 1) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setPassword(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4 + 1)).getPassword());
		else if(par1World.getTileEntity(par2, par3, par4 - 1) != null && par1World.getTileEntity(par2, par3, par4 - 1) instanceof TileEntityKeypadChest)
			((TileEntityKeypadChest) par1World.getTileEntity(par2, par3, par4)).setPassword(((IPasswordProtected) par1World.getTileEntity(par2, par3, par4 - 1)).getPassword());
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5Block){
		if(!(par1World.getTileEntity(par2, par3, par4) instanceof TileEntityKeypadChest))
			return;
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5Block);
		TileEntityKeypadChest tileentitychest = (TileEntityKeypadChest)par1World.getTileEntity(par2, par3, par4);

		if (tileentitychest != null)
			tileentitychest.updateContainingBlockInfo();

	}

	@Override
	public TileEntity createNewTileEntity(World par1World, int par2){
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

		world.setBlock(x, y, z, mod_SecurityCraft.keypadChest, newMeta, 3);
		world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
		((IOwnable) world.getTileEntity(x, y, z)).getOwner().set(player.getCommandSenderName(), player.getUniqueID().toString());
		((TileEntityChest)world.getTileEntity(x, y, z)).readFromNBT(tag);
		return true;
	}
}
