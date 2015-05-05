package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.interfaces.IIntersectable;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class BlockCageTrap extends BlockOwnable implements IIntersectable, IHelpInfo {

	public final boolean deactivated;

	public BlockCageTrap(Material par2Material, boolean deactivated, int blockTextureIndex) {
		super(par2Material);
		this.deactivated = deactivated;
	}
	
	public int getRenderType(){
		return 3;
	}

	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state)
	{
		if(!deactivated){
			return null;
		}else{
			return AxisAlignedBB.fromBounds((double) pos.getX() + this.minX, (double) pos.getY() + this.minY, (double) pos.getZ() + this.minZ, (double) pos.getX() + this.maxX, (double) pos.getY() + this.maxY, (double) pos.getZ() + this.maxZ);
		}
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random par1Random){
		return this.deactivated ? 0 : 1;
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public Item getItemDropped(IBlockState state, Random par2Random, int par3)
	{
		return this.deactivated ? BlockUtils.getItemFromBlock(mod_SecurityCraft.deactivatedCageTrap) : BlockUtils.getItemFromBlock(this);
	}
	
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			if(entity instanceof EntityPlayer && !deactivated){
				BlockUtils.setBlock(world, pos, mod_SecurityCraft.deactivatedCageTrap);
				BlockUtils.setBlock(world, pos.up(4), mod_SecurityCraft.unbreakableIronBars);
				BlockUtils.setBlock(world, pos.getX() + 1, pos.getY() + 4, pos.getZ(), mod_SecurityCraft.unbreakableIronBars);	
				BlockUtils.setBlock(world, pos.getX() - 1, pos.getY() + 4, pos.getZ(), mod_SecurityCraft.unbreakableIronBars);	
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() + 1, mod_SecurityCraft.unbreakableIronBars);	
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() - 1, mod_SecurityCraft.unbreakableIronBars);	

				BlockUtils.setBlockInBox(world, pos.getX(), pos.getY(), pos.getZ(), mod_SecurityCraft.unbreakableIronBars);

				world.playSoundAtEntity(entity, "random.anvil_use", 3.0F, 1.0F);
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation(((EntityPlayer) entity).getName() + " was captured in a trap at " + Utils.getFormattedCoordinates(pos)));
			}
		}
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable().intersectsEntities();
	}

	public String getHelpInfo() {
		return "The cage trap will spawn a 'cage' around any player who walks on top of it. (*needs textures & recipe*)";
	}

	public String[] getRecipe() {
		return new String[]{"The cage trap requires: 3 reinforced iron bars, 2 gold ingots, 1 redstone, 3 iron blocks", "WWW", "XYX", "ZZZ", "W = reinforced iron bars, X = gold ingot, Y = redstone, Z = iron block"};
	}
    
}
