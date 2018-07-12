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

	public BlockCageTrap(Material material, boolean deactivated) {
		super(material);
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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		if(!deactivated)
			return null;
		else
			return AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if(!world.isRemote){
			TileEntityCageTrap tileEntity = (TileEntityCageTrap) world.getTileEntity(x, y, z);
			boolean isPlayer = entity instanceof EntityPlayer;
			boolean shouldCaptureMobs = tileEntity.getOptionByName("captureMobs").asBoolean();

			if((isPlayer || entity instanceof EntityMob) && !deactivated){
				IOwnable originalTrap = (IOwnable)world.getTileEntity(x, y, z);

				if(isPlayer && originalTrap.getOwner().isOwner((EntityPlayer)entity))
					return;

				if(!isPlayer && !shouldCaptureMobs)
					return;

				world.setBlock(x, y, z, SCContent.deactivatedCageTrap);

				world.setBlock(x, y + 4, z, SCContent.reinforcedIronBars);
				world.setBlock(x + 1, y + 4, z, SCContent.reinforcedIronBars);
				world.setBlock(x - 1, y + 4, z, SCContent.reinforcedIronBars);
				world.setBlock(x, y + 4, z + 1, SCContent.reinforcedIronBars);
				world.setBlock(x, y + 4, z - 1, SCContent.reinforcedIronBars);

				BlockUtils.setBlockInBox(world, x, y, z, SCContent.reinforcedIronBars);
				setTileEntities(world, x, y, z, originalTrap.getOwner().getUUID(), originalTrap.getOwner().getName());

				world.playSoundEffect(x,y,z, "random.anvil_use", 3.0F, 1.0F);

				if(isPlayer)
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("["+ EnumChatFormatting.BLACK + StatCollector.translateToLocal("tile.securitycraft:cageTrap.name") + EnumChatFormatting.RESET + "] " + StatCollector.translateToLocal("messages.securitycraft:cageTrap.captured").replace("#player", ((EntityPlayer) entity).getCommandSenderName()).replace("#location", Utils.getFormattedCoordinates(x, y, z))));
			}
		}
	}

	@Override
	public IIcon getIcon(int side, int meta){
		return blockIcon;
	}

	@Override
	public int quantityDropped(Random par1Random){
		return deactivated ? 0 : 1;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune){
		return deactivated ? Item.getItemFromBlock(SCContent.deactivatedCageTrap) : Item.getItemFromBlock(this);
	}

	public void setTileEntities(World world, int x, int y, int z, String uuid, String name)
	{
		((IOwnable)world.getTileEntity(x, y, z)).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(x, y + 4, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 4, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 4, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x, y + 4, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x, y + 4, z - 1)).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(x + 1, y + 1, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 2, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 3, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 1, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 2, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 3, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 1, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 2, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 3, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 1, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 2, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 3, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x, y + 1, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x, y + 2, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x, y + 3, z + 1)).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(x + 1, y + 1, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 2, z)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 3, z)).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(x, y + 1, z - 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x, y + 2, z - 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x, y + 3, z - 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 1, z - 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 2, z - 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 3, z - 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 1, z - 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 2, z - 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 3, z - 1)).getOwner().set(uuid, name);

		((IOwnable)world.getTileEntity(x + 1, y + 4, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x + 1, y + 4, z - 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 4, z + 1)).getOwner().set(uuid, name);
		((IOwnable)world.getTileEntity(x - 1, y + 4, z - 1)).getOwner().set(uuid, name);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCageTrap();
	}
}
