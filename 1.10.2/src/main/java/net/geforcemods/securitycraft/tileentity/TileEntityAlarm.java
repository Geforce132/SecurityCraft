package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.blocks.BlockAlarm;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityAlarm extends TileEntityOwnable {

	private int cooldown = 0;
	private boolean isPowered = false;

	@Override
	public void update(){
		if(worldObj.isRemote)
			return;
		else{
			if(cooldown > 0){
				cooldown--;

				if(cooldown == 0)
					mod_SecurityCraft.log("Cooldown is 0");
			}

			if(isPowered && cooldown == 0){
				TileEntityAlarm TEA = (TileEntityAlarm) worldObj.getTileEntity(pos);
				getWorld().playSound(null, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SCSounds.ALARM.event, SoundCategory.BLOCKS, 0.3F, 0.6F);
				TEA.setCooldown((mod_SecurityCraft.configHandler.alarmTickDelay * 20));
				worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockAlarm.FACING, worldObj.getBlockState(pos).getValue(BlockAlarm.FACING)), 2); //TODO
				worldObj.setTileEntity(pos, TEA);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("cooldown", cooldown);
		par1NBTTagCompound.setBoolean("isPowered", isPowered);
		return par1NBTTagCompound;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		if (par1NBTTagCompound.hasKey("cooldown"))
			cooldown = par1NBTTagCompound.getInteger("cooldown");

		if (par1NBTTagCompound.hasKey("isPowered"))
			isPowered = par1NBTTagCompound.getBoolean("isPowered");

	}

	public int getCooldown(){
		return cooldown;
	}

	public void setCooldown(int par1){
		mod_SecurityCraft.log("Setting cooldown to " + par1 + " | " + FMLCommonHandler.instance().getEffectiveSide());
		cooldown = par1;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

}
