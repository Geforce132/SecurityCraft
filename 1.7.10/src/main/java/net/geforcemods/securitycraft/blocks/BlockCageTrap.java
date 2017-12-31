package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
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

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		if(!deactivated)
			return null;
		else
			return AxisAlignedBB.getBoundingBox(par2 + minX, par3 + minY, par4 + minZ, par2 + maxX, par3 + maxY, par4 + maxZ);
	}

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		if(!par1World.isRemote){
			TileEntityCageTrap tileEntity = (TileEntityCageTrap) par1World.getTileEntity(par2, par3, par4);
			boolean isPlayer = par5Entity instanceof EntityPlayer;
			boolean shouldCaptureMobs = tileEntity.getOptionByName("captureMobs").asBoolean();

			if((isPlayer || par5Entity instanceof EntityMob) && !deactivated){
				IOwnable originalTrap = (IOwnable)par1World.getTileEntity(par2, par3, par4);

				if(isPlayer && originalTrap.getOwner().isOwner((EntityPlayer)par5Entity))
					return;

				if(!isPlayer && !shouldCaptureMobs)
					return;

				par1World.setBlock(par2, par3, par4, SCContent.deactivatedCageTrap);

				par1World.setBlock(par2, par3 + 4, par4, SCContent.unbreakableIronBars);
				par1World.setBlock(par2 + 1, par3 + 4, par4, SCContent.unbreakableIronBars);
				par1World.setBlock(par2 - 1, par3 + 4, par4, SCContent.unbreakableIronBars);
				par1World.setBlock(par2, par3 + 4, par4 + 1, SCContent.unbreakableIronBars);
				par1World.setBlock(par2, par3 + 4, par4 - 1, SCContent.unbreakableIronBars);

				BlockUtils.setBlockInBox(par1World, par2, par3, par4, SCContent.unbreakableIronBars);
				setTileEntities(par1World, par2, par3, par4, originalTrap.getOwner().getUUID(), originalTrap.getOwner().getName());

				par1World.playSoundEffect(par2,par3,par4, "random.anvil_use", 3.0F, 1.0F);

				if(isPlayer)
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("["+ EnumChatFormatting.BLACK + StatCollector.translateToLocal("tile.cageTrap.name") + EnumChatFormatting.RESET + "] " + StatCollector.translateToLocal("messages.cageTrap.captured").replace("#player", ((EntityPlayer) par5Entity).getCommandSenderName()).replace("#location", Utils.getFormattedCoordinates(par2, par3, par4))));
			}
		}
	}

	@Override
	public IIcon getIcon(int par1, int par2){
		return blockIcon;
	}

	@Override
	public int quantityDropped(Random par1Random){
		return deactivated ? 0 : 1;
	}

	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3){
		return deactivated ? Item.getItemFromBlock(SCContent.deactivatedCageTrap) : Item.getItemFromBlock(this);
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

		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 4, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 + 1, par3 + 4, par4 - 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 4, par4 + 1)).getOwner().set(uuid, name);
		((IOwnable)par1World.getTileEntity(par2 - 1, par3 + 4, par4 - 1)).getOwner().set(uuid, name);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityCageTrap();
	}
}
