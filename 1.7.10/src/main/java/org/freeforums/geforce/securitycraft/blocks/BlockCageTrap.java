package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import org.freeforums.geforce.securitycraft.api.IOwnable;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCageTrap extends BlockOwnable {

	public final boolean deactivated;
	private final int blockTextureIndex;

	@SideOnly(Side.CLIENT)
	private IIcon topIcon;

	public BlockCageTrap(Material par2Material, boolean deactivated, int blockTextureIndex) {
		super(par2Material);
		this.deactivated = deactivated;
		this.blockTextureIndex = blockTextureIndex;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		if(!deactivated){
			return null;
		}else{
			return AxisAlignedBB.getBoundingBox((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
		}
	}

	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(par1World.isRemote){
			return;
		}else{
			if(par5Entity instanceof EntityPlayer && !deactivated){
				IOwnable originalTrap = (IOwnable)par1World.getTileEntity(par2, par3, par4);
				
				par1World.setBlock(par2, par3, par4, mod_SecurityCraft.deactivatedCageTrap);

				par1World.setBlock(par2, par3 + 4, par4, mod_SecurityCraft.unbreakableIronBars);
				par1World.setBlock(par2 + 1, par3 + 4, par4, mod_SecurityCraft.unbreakableIronBars);	
				par1World.setBlock(par2 - 1, par3 + 4, par4, mod_SecurityCraft.unbreakableIronBars);	
				par1World.setBlock(par2, par3 + 4, par4 + 1, mod_SecurityCraft.unbreakableIronBars);	
				par1World.setBlock(par2, par3 + 4, par4 - 1, mod_SecurityCraft.unbreakableIronBars);	

				BlockUtils.setBlockInBox(par1World, par2, par3, par4, mod_SecurityCraft.unbreakableIronBars);
				setTileEntities(par1World, par2, par3, par4, originalTrap.getOwnerUUID(), originalTrap.getOwnerName());

				par1World.playSoundAtEntity(par5Entity, "random.anvil_use", 3.0F, 1.0F);
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(((EntityPlayer) par5Entity).getCommandSenderName() + " was captured in a trap at" + Utils.getFormattedCoordinates(par2, par3, par4)));
			}
		}
	}

	public IIcon getIcon(int par1, int par2){
		if(this.blockTextureIndex == 9999){
			return par1 == 1 ? this.topIcon : this.blockIcon;
		}else{
			return this.blockIcon;
		}
	}

	public int quantityDropped(Random par1Random){
		return this.deactivated ? 0 : 1;
	}

	public Item getItemDropped(int par1, Random par2Random, int par3){
		return this.deactivated ? BlockUtils.getItemFromBlock(mod_SecurityCraft.deactivatedCageTrap) : BlockUtils.getItemFromBlock(this);
	}

	public void setTileEntities(World par1World, int par2, int par3, int par4, String uuid, String name)
	{
		((IOwnable)par1World.getTileEntity(par2, par3, par4)).setOwner(uuid, name);

		((IOwnable)par1World.getTileEntity(par2, par3 + 4, par4)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 4, par4)).setOwner(uuid, name);	
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 4, par4)).setOwner(uuid, name);	
		((IOwnable)par1World.getTileEntity(par2, par3 + 4, par4 + 1)).setOwner(uuid, name);	
		((IOwnable)par1World.getTileEntity(par2, par3 + 4, par4 - 1)).setOwner(uuid, name);
		
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 1, par4)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 2, par4)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 3, par4)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 1, par4 + 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 2, par4 + 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 3, par4 + 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 1, par4)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 2, par4)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 3, par4)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 1, par4 + 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 2, par4 + 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 3, par4 + 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 1, par4 + 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 2, par4 + 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 3, par4 + 1)).setOwner(uuid, name);

		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 1, par4)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 2, par4)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 3, par4)).setOwner(uuid, name);

		((IOwnable)par1World.getTileEntity(par2, par3 + 1, par4 - 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 2, par4 - 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 3, par4 - 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 1, par4 - 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 2, par4 - 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 3, par4 - 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 1, par4 - 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 2, par4 - 1)).setOwner(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 3, par4 - 1)).setOwner(uuid, name);
	}
}
