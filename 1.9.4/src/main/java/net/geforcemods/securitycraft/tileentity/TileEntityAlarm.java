package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.BlockAlarm;
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
					SecurityCraft.log("Cooldown is 0");
			}

			if(isPowered && cooldown == 0){
				TileEntityAlarm te = (TileEntityAlarm) worldObj.getTileEntity(pos);
				getWorld().playSound(null, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SCSounds.ALARM.event, SoundCategory.BLOCKS, SecurityCraft.config.alarmSoundVolume, 1.0F);
				te.setCooldown((SecurityCraft.config.alarmTickDelay * 20));
				worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockAlarm.FACING, worldObj.getBlockState(pos).getValue(BlockAlarm.FACING)), 2);
				worldObj.setTileEntity(pos, te);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setInteger("cooldown", cooldown);
		tag.setBoolean("isPowered", isPowered);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("cooldown"))
			cooldown = tag.getInteger("cooldown");

		if (tag.hasKey("isPowered"))
			isPowered = tag.getBoolean("isPowered");

	}

	public int getCooldown(){
		return cooldown;
	}

	public void setCooldown(int cooldown){
		SecurityCraft.log("Setting cooldown to " + cooldown + " | " + FMLCommonHandler.instance().getEffectiveSide());
		this.cooldown = cooldown;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

}
