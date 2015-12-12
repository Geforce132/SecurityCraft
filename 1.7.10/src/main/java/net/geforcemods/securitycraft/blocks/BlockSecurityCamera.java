package net.geforcemods.securitycraft.blocks;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketCRemoveLGView;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSecurityCamera extends BlockContainer {

	public BlockSecurityCamera(Material par2Material) {
		super(par2Material);
	}

	public boolean renderAsNormalBlock(){
		return false;
	}

	public boolean isNormalCube(){
		return false;
	}

	public boolean isOpaqueCube(){
		return false;
	}

	public int getRenderType(){
		return -1;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		return null;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z){
		int meta = par1IBlockAccess.getBlockMetadata(x, y, z);

		if(meta == 3  || meta == 7){
			this.setBlockBounds(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
		}else if(meta == 4 || meta == 8){
			this.setBlockBounds(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
		}else if(meta == 2 || meta == 6){
			this.setBlockBounds(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
		}else{
			this.setBlockBounds(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
		}
	} 
	
	public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9){
        int k1 = par9 & 8;
        byte b0 = -1;

        if(par5 == 2 && par1World.isSideSolid(par2, par3, par4 + 1, NORTH)){
            b0 = 4;
        }

        if(par5 == 3 && par1World.isSideSolid(par2, par3, par4 - 1, SOUTH)){
            b0 = 3;
        }

        if(par5 == 4 && par1World.isSideSolid(par2 + 1, par3, par4, WEST)){
            b0 = 2;
        }

        if(par5 == 5 && par1World.isSideSolid(par2 - 1, par3, par4, EAST)){
            b0 = 1;
        }

        return b0 + k1;
    }

    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5Block) {    			
    	int metadata = par1World.getBlockMetadata(par2, par3, par4);
    	
    	if(metadata == 1) {
    		if(!par1World.isSideSolid(par2 - 1, par3, par4, EAST)) {
    			BlockUtils.destroyBlock(par1World, par2, par3, par4, true);
    		}
    	}else if(metadata == 2) {
    		if(!par1World.isSideSolid(par2 + 1, par3, par4, WEST)) {
    			BlockUtils.destroyBlock(par1World, par2, par3, par4, true);
    		}
    	}else if(metadata == 3) {
    		if(!par1World.isSideSolid(par2, par3, par4 - 1, SOUTH)) {
    			BlockUtils.destroyBlock(par1World, par2, par3, par4, true);
    		}
    	}else if(metadata == 4) {
    		if(!par1World.isSideSolid(par2, par3, par4 + 1, NORTH)) {
    			BlockUtils.destroyBlock(par1World, par2, par3, par4, true);
    		}
    	}
    }

	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
		mod_SecurityCraft.network.sendToAll(new PacketCRemoveLGView(par2, par3, par4));
	}
	
    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5){
        ForgeDirection dir = ForgeDirection.getOrientation(par5);
        return (dir == NORTH && par1World.isSideSolid(par2, par3, par4 + 1, NORTH)) ||
               (dir == SOUTH && par1World.isSideSolid(par2, par3, par4 - 1, SOUTH)) ||
               (dir == WEST  && par1World.isSideSolid(par2 + 1, par3, par4, WEST )) ||
               (dir == EAST  && par1World.isSideSolid(par2 - 1, par3, par4, EAST ));
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return !world.getBlock(x, y, z).isReplaceable(world, x, y, z) ^ //exclusive or
        		(world.isSideSolid(x - 1, y, z, EAST) ||
        		world.isSideSolid(x + 1, y, z, WEST) ||
        		world.isSideSolid(x, y, z - 1, SOUTH) ||
        		world.isSideSolid(x, y, z + 1, NORTH));
    }
    
    public boolean canProvidePower() {
		return true;
	}

	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
		return BlockUtils.isMetadataBetween(par1IBlockAccess, par2, par3, par4, 7, 10) ? 15 : 0;
	}
	
	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
		return BlockUtils.isMetadataBetween(par1IBlockAccess, par2, par3, par4, 7, 10) ? 15 : 0;
	}

	public void mountCamera(World world, int par2, int par3, int par4, int par5, EntityPlayer player){
		if(!world.isRemote && player.ridingEntity == null){
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.securityCamera.name"), StatCollector.translateToLocal("messages.securityCamera.mounted"), EnumChatFormatting.GREEN);
		}
		
		if(player.ridingEntity != null && player.ridingEntity instanceof EntitySecurityCamera){
			EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, par5, (EntitySecurityCamera) player.ridingEntity);
			world.spawnEntityInWorld(dummyEntity);
			player.mountEntity(dummyEntity);
			return;
		}

		EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, par5, player);
		world.spawnEntityInWorld(dummyEntity);
		player.mountEntity(dummyEntity);
	}

	public Item getItemDropped(int par1, Random par2Random, int par3){
		return Item.getItemFromBlock(mod_SecurityCraft.securityCamera);
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World par1World, int par2, int par3, int par4){
		return Item.getItemFromBlock(mod_SecurityCraft.securityCamera);
	}

	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntitySecurityCamera();
	}

}
