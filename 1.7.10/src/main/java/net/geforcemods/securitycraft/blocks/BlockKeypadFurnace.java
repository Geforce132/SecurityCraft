package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockKeypadFurnace extends BlockContainer implements IPasswordConvertible {

	private Random random = new Random();

	public BlockKeypadFurnace(Material material) {
		super(material);
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
		{
			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker))
				((TileEntityKeypadFurnace) world.getTileEntity(x, y, z)).openPasswordGUI(player);
		}

		return true;
	}

	public static void activate(World world, int x, int y, int z, EntityPlayer player){
		player.openGui(SecurityCraft.instance, GuiHandler.KEYPAD_FURNACE_GUI_ID, world, x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 5, 3);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if(entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);

		if(entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);

		if(entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if(entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		else
			return;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		TileEntityKeypadFurnace tileentityfurnace = (TileEntityKeypadFurnace) world.getTileEntity(x, y, z);

		if (tileentityfurnace != null)
		{
			for (int i1 = 0; i1 < tileentityfurnace.getSizeInventory(); ++i1)
			{
				ItemStack itemstack = tileentityfurnace.getStackInSlot(i1);

				if (itemstack != null)
				{
					float f = random.nextFloat() * 0.8F + 0.1F;
					float f1 = random.nextFloat() * 0.8F + 0.1F;
					float f2 = random.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0)
					{
						int j1 = random.nextInt(21) + 10;

						if (j1 > itemstack.stackSize)
							j1 = itemstack.stackSize;

						itemstack.stackSize -= j1;
						EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getMetadata()));

						if (itemstack.hasTagCompound())
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());

						float f3 = 0.05F;
						entityitem.motionX = (float)random.nextGaussian() * f3;
						entityitem.motionY = (float)random.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float)random.nextGaussian() * f3;
						world.spawnEntityInWorld(entityitem);
					}
				}
			}

			world.updateNeighborsAboutBlockChange(x, y, z, block);
		}

		super.breakBlock(world, x, y, z, block, meta);
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
	public boolean convert(EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntityFurnace furnace = (TileEntityFurnace)world.getTileEntity(x, y, z);
		NBTTagCompound tag = new NBTTagCompound();
		int newMeta = 3;

		furnace.writeToNBT(tag);

		for(int i = 0; i < furnace.getSizeInventory(); i++)
		{
			furnace.setInventorySlotContents(i, null);
		}

		switch(world.getBlockMetadata(x, y, z))
		{
			case 5: newMeta = 4; break;
			case 4: newMeta = 2; break;
			case 2: newMeta = 1; break;
		}

		world.setBlock(x, y, z, SCContent.keypadFurnace, newMeta, 3);
		((IOwnable) world.getTileEntity(x, y, z)).getOwner().set(player.getUniqueID().toString(), player.getCommandSenderName());
		((TileEntityFurnace)world.getTileEntity(x, y, z)).readFromNBT(tag);
		return true;
	}
}
