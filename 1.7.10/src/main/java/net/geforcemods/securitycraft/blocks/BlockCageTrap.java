package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class BlockCageTrap extends BlockOwnable {

	public final boolean deactivated;

	@SideOnly(Side.CLIENT)
	private IIcon topIcon;

	public BlockCageTrap(Material par2Material, boolean deactivated) {
		super(par2Material);
		this.deactivated = deactivated;
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
			return AxisAlignedBB.getBoundingBox(par2 + this.minX, par3 + this.minY, par4 + this.minZ, par2 + this.maxX, par3 + this.maxY, par4 + this.maxZ);
		}
	}

	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(par1World.isRemote){
			return;
		}else{
			TileEntityCageTrap tileEntity = (TileEntityCageTrap) par1World.getTileEntity(par2, par3, par4);
			boolean isPlayer = par5Entity instanceof EntityPlayer;
			boolean shouldCaptureMobs = tileEntity.getOptionByName("captureMobs").asBoolean();
			
			if((isPlayer || par5Entity instanceof EntityMob) && !deactivated){
				IOwnable originalTrap = (IOwnable)par1World.getTileEntity(par2, par3, par4);
				
				if(isPlayer && originalTrap.getOwner().isOwner((EntityPlayer)par5Entity))
					return;
				
				if(!isPlayer && !shouldCaptureMobs)
					return;
				
				par1World.setBlock(par2, par3, par4, mod_SecurityCraft.deactivatedCageTrap);

				par1World.setBlock(par2, par3 + 4, par4, mod_SecurityCraft.unbreakableIronBars);
				par1World.setBlock(par2 + 1, par3 + 4, par4, mod_SecurityCraft.unbreakableIronBars);	
				par1World.setBlock(par2 - 1, par3 + 4, par4, mod_SecurityCraft.unbreakableIronBars);	
				par1World.setBlock(par2, par3 + 4, par4 + 1, mod_SecurityCraft.unbreakableIronBars);	
				par1World.setBlock(par2, par3 + 4, par4 - 1, mod_SecurityCraft.unbreakableIronBars);	

				BlockUtils.setBlockInBox(par1World, par2, par3, par4, mod_SecurityCraft.unbreakableIronBars);
				setTileEntities(par1World, par2, par3, par4, originalTrap.getOwner().getUUID(), originalTrap.getOwner().getName());

				par1World.playSoundEffect((double) par2,(double) par3,(double) par4, "random.anvil_use", 3.0F, 1.0F);
				
				if(isPlayer)
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("["+ EnumChatFormatting.BLACK + StatCollector.translateToLocal("tile.cageTrap.name") + EnumChatFormatting.RESET + "] " + StatCollector.translateToLocal("messages.cageTrap.captured").replace("#player", ((EntityPlayer) par5Entity).getCommandSenderName()).replace("#location", Utils.getFormattedCoordinates(par2, par3, par4))));
			}
		}
	}

	public IIcon getIcon(int par1, int par2){
		return this.blockIcon;
	}

	public int quantityDropped(Random par1Random){
		return this.deactivated ? 0 : 1;
	}

	public Item getItemDropped(int par1, Random par2Random, int par3){
		return this.deactivated ? Item.getItemFromBlock(mod_SecurityCraft.deactivatedCageTrap) : Item.getItemFromBlock(this);
	}

	public void setTileEntities(World par1World, int par2, int par3, int par4, String uuid, String name)
	{
		((IOwnable)par1World.getTileEntity(par2, par3, par4)).getOwner().set(uuid, name);

		((IOwnable)par1World.getTileEntity(par2, par3 + 4, par4)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 4, par4)).getOwner().set(uuid, name);	
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 4, par4)).getOwner().set(uuid, name);	
		((IOwnable)par1World.getTileEntity(par2, par3 + 4, par4 + 1)).getOwner().set(uuid, name);	
		((IOwnable)par1World.getTileEntity(par2, par3 + 4, par4 - 1)).getOwner().set(uuid, name);
		
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 1, par4)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 2, par4)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 3, par4)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 1, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 2, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 3, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 1, par4)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 2, par4)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 3, par4)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 1, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 2, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 3, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 1, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 2, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 3, par4 + 1)).getOwner().set(uuid, name);

		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 1, par4)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 2, par4)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 3, par4)).getOwner().set(uuid, name);

		((IOwnable)par1World.getTileEntity(par2, par3 + 1, par4 - 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 2, par4 - 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2, par3 + 3, par4 - 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 1, par4 - 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 2, par4 - 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 3, par4 - 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 1, par4 - 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 2, par4 - 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 3, par4 - 1)).getOwner().set(uuid, name);
	}
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityCageTrap();
	}
}
