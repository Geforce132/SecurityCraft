package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.blocks.mines.BlockClaymore;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityClaymore extends TileEntitySCTE{

	private double entityX = -1D;
	private double entityY = -1D;
	private double entityZ = -1D;
	private int cooldown = -1;
	
	public void update() {
		if(getWorld().isRemote){
			return;
		}else{		
			if(getWorld().getBlockState(getPos()).getValue(BlockClaymore.DEACTIVATED).booleanValue()){
				return;
			}
			
			if(cooldown > 0){
				cooldown--;
				return;
			}
			
			if(cooldown == 0){
				BlockUtils.destroyBlock(getWorld(), getPos(), false);
				getWorld().createExplosion((Entity) null, entityX, entityY + 0.5F, entityZ, 3.5F, true);
				return;
			}
			
			EnumFacing dir = BlockUtils.getBlockProperty(getWorld(), getPos(), BlockClaymore.FACING);
			AxisAlignedBB axisalignedbb = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
			
			if(dir == EnumFacing.NORTH){
				axisalignedbb = axisalignedbb.addCoord(0, 0, -mod_SecurityCraft.configHandler.claymoreRange);
			}else if(dir == EnumFacing.SOUTH){
				axisalignedbb = axisalignedbb.addCoord(0, 0, mod_SecurityCraft.configHandler.claymoreRange);
			}if(dir == EnumFacing.EAST){
				axisalignedbb = axisalignedbb.addCoord(mod_SecurityCraft.configHandler.claymoreRange, 0, 0);
			}else if(dir == EnumFacing.WEST){
				axisalignedbb = axisalignedbb.addCoord(-mod_SecurityCraft.configHandler.claymoreRange, 0, 0);
			}
			
			List<?> list = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
			Iterator<?> iterator = list.iterator();
			EntityLivingBase entityliving;
			
			while(iterator.hasNext()){
				entityliving = (EntityLivingBase) iterator.next();
				
				entityX = entityliving.posX;
				entityY = entityliving.posY;
				entityZ = entityliving.posZ;
				cooldown = 20;
				
				for(EntityPlayer player : getWorld().playerEntities)
				{
					getWorld().playSound(player, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SoundEvent.REGISTRY.getObject(new ResourceLocation("random.click")), SoundCategory.BLOCKS, 0.3F, 0.6F);
				}
				
				break;
			}
		}

	}
	
	/**
     * Writes a tile entity to NBT.
	 * @return 
     */
    public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("cooldown", cooldown);
        par1NBTTagCompound.setDouble("entityX", entityX);
        par1NBTTagCompound.setDouble("entityY", entityY);
        par1NBTTagCompound.setDouble("entityZ", entityZ);
        return par1NBTTagCompound;
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("cooldown"))
        {
            this.cooldown = par1NBTTagCompound.getInteger("cooldown");
        }
        
        if (par1NBTTagCompound.hasKey("entityX"))
        {
            this.entityX = par1NBTTagCompound.getDouble("entityX");
        }
        
        if (par1NBTTagCompound.hasKey("entityY"))
        {
            this.entityY = par1NBTTagCompound.getDouble("entityY");
        }
        
        if (par1NBTTagCompound.hasKey("entityZ"))
        {
            this.entityZ = par1NBTTagCompound.getDouble("entityZ");
        }
    }

}
