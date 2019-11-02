package net.geforcemods.securitycraft.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockInventoryScanner extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon furnaceIconTop;
	@SideOnly(Side.CLIENT)
	private IIcon furnaceIconFront;

	public BlockInventoryScanner(Material material) {
		super(material);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		super.onBlockAdded(world, x, y, z);
		setDefaultDirection(world, x, y, z);
	}

	/**
	 * set a blocks direction
	 */
	private void setDefaultDirection(World world, int x, int y, int z){
		if(!world.isRemote){
			Block north = world.getBlock(x, y, z - 1);
			Block south = world.getBlock(x, y, z + 1);
			Block west = world.getBlock(x - 1, y, z);
			Block east = world.getBlock(x + 1, y, z);
			byte b0 = 3;

			if (north.isFullBlock() && !south.isFullBlock())
				b0 = 3;

			if (south.isFullBlock() && !north.isFullBlock())
				b0 = 2;

			if (west.isFullBlock() && !east.isFullBlock())
				b0 = 5;

			if (east.isFullBlock() && !west.isFullBlock())
				b0 = 4;

			world.setBlockMetadataWithNotify(x, y, z, b0, 2);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;
		else{
			if(isFacingAnotherScanner(world, x, y, z))
				player.openGui(SecurityCraft.instance, GuiHandler.INVENTORY_SCANNER_GUI_ID, world, x, y, z);
			else
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.securitycraft:inventoryScanner.name"), StatCollector.translateToLocal("messages.securitycraft:invScan.notConnected"), EnumChatFormatting.RED);

			return true;
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		if(world.isRemote)
			return;

		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);

		if (entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);

		if (entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if (entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);

		checkAndPlaceAppropriately(world, x, y, z);
	}

	private void checkAndPlaceAppropriately(World world, int x, int y, int z)
	{
		TileEntityInventoryScanner connectedScanner = getConnectedInventoryScanner(world, x, y, z);

		if(connectedScanner == null)
			return;

		boolean place = true;

		if(world.getBlockMetadata(x, y, z) == 4)
		{
			for(int j = 1; j < Math.abs(x - connectedScanner.xCoord); j++)
			{
				if(world.getBlock(x - j, y, z) == SCContent.inventoryScannerField)
				{
					place = false;
					break;
				}
			}

			if(place)
			{
				for(int j = 1; j < Math.abs(x - connectedScanner.xCoord); j++)
				{
					world.setBlock(x - j, y, z, SCContent.inventoryScannerField, 1, 3);
				}
			}
		}
		else if(world.getBlockMetadata(x, y, z) == 5)
		{
			for(int j = 1; j < Math.abs(x - connectedScanner.xCoord); j++)
			{
				if(world.getBlock(x + j, y, z) == SCContent.inventoryScannerField)
				{
					place = false;
					break;
				}
			}

			if(place)
			{
				for(int j = 1; j < Math.abs(x - connectedScanner.xCoord); j++)
				{
					world.setBlock(x + j, y, z, SCContent.inventoryScannerField, 1, 3);
				}
			}
		}
		else if(world.getBlockMetadata(x, y, z) == 2)
		{
			for(int j = 1; j < Math.abs(z - connectedScanner.zCoord); j++)
			{
				if(world.getBlock(x, y, z - j) == SCContent.inventoryScannerField)
				{
					place = false;
					break;
				}
			}

			if(place)
			{
				for(int j = 1; j < Math.abs(z - connectedScanner.zCoord); j++)
				{
					world.setBlock(x, y, z - j, SCContent.inventoryScannerField, 2, 3);
				}
			}
		}
		else if(world.getBlockMetadata(x, y, z) == 3)
		{
			for(int j = 1; j < Math.abs(z - connectedScanner.zCoord); j++)
			{
				if(world.getBlock(x, y, z + j) == SCContent.inventoryScannerField)
				{
					place = false;
					break;
				}
			}

			if(place)
			{
				for(int j = 1; j < Math.abs(z - connectedScanner.zCoord); j++)
				{
					world.setBlock(x, y, z + j, SCContent.inventoryScannerField, 2, 3);
				}
			}
		}

		if(place)
			CustomizableSCTE.link((CustomizableSCTE)world.getTileEntity(x, y, z), connectedScanner);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		if(world.isRemote)
			return;

		TileEntityInventoryScanner connectedScanner = null;

		for(int i = 1; i <= SecurityCraft.config.inventoryScannerRange; i++)
		{
			if(world.getBlock(x - i, y, z) == SCContent.inventoryScanner)
			{
				for(int j = 1; j < i; j++)
				{
					if(world.getBlock(x - j, y, z) == SCContent.inventoryScannerField && world.getBlockMetadata(x - j, y, z) == 1)
						world.breakBlock(x - j, y, z, false);
				}

				connectedScanner = (TileEntityInventoryScanner) world.getTileEntity(x - i, y, z);
				break;
			}
		}

		for(int i = 1; i <= SecurityCraft.config.inventoryScannerRange; i++)
		{
			if(world.getBlock(x + i, y, z) == SCContent.inventoryScanner)
			{
				for(int j = 1; j < i; j++)
				{
					if(world.getBlock(x + j, y, z) == SCContent.inventoryScannerField && world.getBlockMetadata(x + j, y, z) == 1)
						world.breakBlock(x + j, y, z, false);
				}

				connectedScanner = (TileEntityInventoryScanner) world.getTileEntity(x + i, y, z);
				break;
			}
		}

		for(int i = 1; i <= SecurityCraft.config.inventoryScannerRange; i++)
		{
			if(world.getBlock(x, y, z - i) == SCContent.inventoryScanner)
			{
				for(int j = 1; j < i; j++)
				{
					if(world.getBlock(x, y, z - j) == SCContent.inventoryScannerField && world.getBlockMetadata(x, y, z - j) == 2)
						world.breakBlock(x, y, z - j, false);
				}

				connectedScanner = (TileEntityInventoryScanner) world.getTileEntity(x, y, z - i);
				break;
			}
		}

		for(int i = 1; i <= SecurityCraft.config.inventoryScannerRange; i++)
		{
			if(world.getBlock(x, y, z + i) == SCContent.inventoryScanner)
			{
				for(int j = 1; j < i; j++)
				{
					if(world.getBlock(x, y, z + j) == SCContent.inventoryScannerField && world.getBlockMetadata(x, y, z + j) == 2)
						world.breakBlock(x, y, z + j, false);
				}

				connectedScanner = (TileEntityInventoryScanner) world.getTileEntity(x, y, z + i);
				break;
			}
		}

		for(int i = 10; i < ((TileEntityInventoryScanner) world.getTileEntity(x, y, z)).getContents().length; i++)
		{
			if(((TileEntityInventoryScanner) world.getTileEntity(x, y, z)).getContents()[i] != null)
				world.spawnEntityInWorld(new EntityItem(world, x, y, z, ((TileEntityInventoryScanner) world.getTileEntity(x, y, z)).getContents()[i]));
		}

		if(connectedScanner != null)
		{
			for(int i = 0; i < connectedScanner.getContents().length; i++)
			{
				connectedScanner.getContents()[i] = null;
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

	private boolean isFacingAnotherScanner(World world, int x, int y, int z)
	{
		return getConnectedInventoryScanner(world, x, y, z) != null;
	}

	/**
	 * @return the scanner, null if none found
	 */
	public static TileEntityInventoryScanner getConnectedInventoryScanner(World world, int x, int y, int z)
	{
		switch(world.getBlockMetadata(x, y, z))
		{
			case 4:
				if(world.getBlock(x, y, z) == SCContent.inventoryScanner)
				{
					for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
					{
						if(world.getBlock(x - i, y, z) != Blocks.air && world.getBlock(x - i, y, z) != SCContent.inventoryScannerField && world.getBlock(x - i, y, z) != SCContent.inventoryScanner)
							return null;

						if(world.getBlock(x - i, y, z) == SCContent.inventoryScanner && world.getBlockMetadata(x - i, y, z) == 5)
							return (TileEntityInventoryScanner)world.getTileEntity(x - i, y, z);
					}
				}

				return null;
			case 5:
				if(world.getBlock(x, y, z) == SCContent.inventoryScanner)
				{
					for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
					{
						if(world.getBlock(x + i, y, z) != Blocks.air && world.getBlock(x + i, y, z) != SCContent.inventoryScannerField && world.getBlock(x + i, y, z) != SCContent.inventoryScanner)
							return null;

						if(world.getBlock(x + i, y, z) == SCContent.inventoryScanner && world.getBlockMetadata(x + i, y, z) == 4)
							return (TileEntityInventoryScanner)world.getTileEntity(x + i, y, z);
					}
				}

				return null;
			case 2:
				if(world.getBlock(x, y, z) == SCContent.inventoryScanner)
				{
					for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
					{
						if(world.getBlock(x, y, z - i) != Blocks.air && world.getBlock(x, y, z - i) != SCContent.inventoryScannerField && world.getBlock(x, y, z - i) != SCContent.inventoryScanner)
							return null;

						if(world.getBlock(x, y, z - i) == SCContent.inventoryScanner && world.getBlockMetadata(x, y, z - i) == 3)
							return (TileEntityInventoryScanner)world.getTileEntity(x, y, z - i);
					}

					return null;
				}
				else if(world.getBlock(x, y, z) == SCContent.inventoryScannerField)
				{
					for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
					{
						if(world.getBlock(x, y, z - i) == SCContent.inventoryScanner)
							return (TileEntityInventoryScanner)world.getTileEntity(x, y, z - i);
					}
				}

				break;
			case 3:
				if(world.getBlock(x, y, z) == SCContent.inventoryScanner)
				{
					for(int i = 0; i <= SecurityCraft.config.inventoryScannerRange; i++)
					{
						if(world.getBlock(x, y, z + i) != Blocks.air && world.getBlock(x, y, z + i) != SCContent.inventoryScannerField && world.getBlock(x, y, z + i) != SCContent.inventoryScanner)
							return null;

						if(world.getBlock(x, y, z + i) == SCContent.inventoryScanner && world.getBlockMetadata(x, y, z + i) == 2)
							return (TileEntityInventoryScanner)world.getTileEntity(x, y, z + i);
					}
				}

				return null;
			case 1:
				if(world.getBlock(x, y, z) == SCContent.inventoryScannerField)
				{
					for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
					{
						if(world.getBlock(x - i, y, z) == SCContent.inventoryScanner)
							return (TileEntityInventoryScanner)world.getTileEntity(x - i, y, z);
					}
				}
		}

		return null;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower(){
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side){
		if(!(access.getTileEntity(x, y, z) instanceof TileEntityInventoryScanner) || ((TileEntityInventoryScanner) access.getTileEntity(x, y, z)).getType() == null)
			return 0 ;

		return (((TileEntityInventoryScanner) access.getTileEntity(x, y, z)).getType().equals("redstone") && ((TileEntityInventoryScanner) access.getTileEntity(x, y, z)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int side){
		if(((TileEntityInventoryScanner) access.getTileEntity(x, y, z)).getType() == null)
			return 0;

		return (((TileEntityInventoryScanner) access.getTileEntity(x, y, z)).getType().equals("redstone") && ((TileEntityInventoryScanner) access.getTileEntity(x, y, z)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(side == 3 && meta == 0)
			return furnaceIconFront;

		return side == 1 ? furnaceIconTop : (side == 0 ? furnaceIconTop : (side != meta ? blockIcon : furnaceIconFront));
	}

	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		blockIcon = register.registerIcon("furnace_side");
		furnaceIconFront = register.registerIcon("securitycraft:inventoryScanner");
		furnaceIconTop = register.registerIcon("furnace_top");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityInventoryScanner();
	}

}
