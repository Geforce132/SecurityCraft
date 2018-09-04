package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityIMS extends CustomizableSCTE {

	/** Number of bombs remaining in storage. **/
	private int bombsRemaining = 4;

	/** The targeting option currently selected for this IMS. 0 = players, 1 = hostile mobs & players.**/
	private int targetingOption = 1;

	@Override
	public void updateEntity(){
		if(worldObj.getTotalWorldTime() % 80L == 0L)
			launchMine();
	}

	/**
	 * Create a bounding box around the IMS, and fire a mine if a mob or player is found.
	 */
	private void launchMine() {
		if(bombsRemaining > 0){
			double d0 = SecurityCraft.config.imsRange;

			AxisAlignedBB area = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(d0, d0, d0);
			List<?> playersWithinArea = worldObj.getEntitiesWithinAABB(EntityPlayer.class, area);
			List<?> mobsWithinArea = worldObj.getEntitiesWithinAABB(IMob.class, area);
			Iterator<?> playerIterator = playersWithinArea.iterator();
			Iterator<?> mobIterator = mobsWithinArea.iterator();

			while(targetingOption == 1 && mobIterator.hasNext()){
				EntityLivingBase entity = (EntityLivingBase) mobIterator.next();
				int launchHeight = getLaunchHeight();

				if(PlayerUtils.isPlayerMountedOnCamera(entity))
					continue;

				if(WorldUtils.isPathObstructed(worldObj, xCoord + 0.5D, yCoord + (((launchHeight - 1) / 3) + 0.5D), zCoord + 0.5D, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ))
					continue;
				if(hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(worldObj, xCoord, yCoord, zCoord, EnumCustomModules.WHITELIST).contains(entity.getCommandSenderName().toLowerCase()))
					continue;

				double targetX = entity.posX - (xCoord + 0.5D);
				double targetY = entity.boundingBox.minY + entity.height / 2.0F - (yCoord + 1.25D);
				double targetZ = entity.posZ - (zCoord + 0.5D);

				this.spawnMine(entity, targetX, targetY, targetZ, launchHeight);

				if(worldObj.isRemote)
					SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(xCoord, yCoord, zCoord, "random.bow", 1.0F));

				bombsRemaining--;

				if(bombsRemaining == 0)
					worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 140);

				return;
			}

			while(playerIterator.hasNext()){
				EntityPlayer entity = (EntityPlayer) playerIterator.next();
				int launchHeight = getLaunchHeight();
				if((entity != null && getOwner().isOwner((entity))) || PlayerUtils.isPlayerMountedOnCamera(entity))
					continue;
				if(WorldUtils.isPathObstructed(worldObj, xCoord + 0.5D, yCoord + (((launchHeight - 1) / 3) + 0.5D), zCoord + 0.5D, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ))
					continue;
				if(hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(worldObj, xCoord, yCoord, zCoord, EnumCustomModules.WHITELIST).contains(entity.getCommandSenderName()))
					continue;

				double targetX = entity.posX - (xCoord + 0.5D);
				double targetY = entity.boundingBox.minY + entity.height / 2.0F - (yCoord + 1.25D);
				double targetZ = entity.posZ - (zCoord + 0.5D);

				this.spawnMine(entity, targetX, targetY, targetZ, launchHeight);

				if(worldObj.isRemote)
					SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(xCoord, yCoord, zCoord, "random.bow", 1.0F));

				bombsRemaining--;

				if(bombsRemaining == 0)
					worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 140);
			}
		}
	}

	/**
	 * Spawn a mine at the correct position on the IMS model.
	 */
	private void spawnMine(EntityPlayer target, double x, double y, double z, int launchHeight){
		double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 1.2D : 0.55D;
		double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 1.2D : 0.6D;

		worldObj.spawnEntityInWorld(new EntityIMSBomb(worldObj, target, xCoord + addToX, yCoord, zCoord + addToZ, x, y, z, launchHeight));
	}

	/**
	 * Spawn a mine at the correct position on the IMS model.
	 */
	private void spawnMine(EntityLivingBase target, double x, double y, double z, int launchHeight){
		double addToX = bombsRemaining == 4 || bombsRemaining == 3 ? 1.2D : 0.55D;
		double addToZ = bombsRemaining == 4 || bombsRemaining == 2 ? 1.2D : 0.6D;

		worldObj.spawnEntityInWorld(new EntityIMSBomb(worldObj, target, xCoord + addToX, yCoord, zCoord + addToZ, x, y, z, launchHeight));
	}

	/**
	 * Returns the amount of ticks the {@link EntityIMSBomb} should float in the air before firing at an entity.
	 */
	private int getLaunchHeight() {
		int height;

		for(height = 1; height <= 9; height++)
			if(worldObj.getBlock(xCoord, yCoord + height, zCoord) == null || worldObj.getBlock(xCoord, yCoord + height, zCoord) == Blocks.air)
				continue;
			else
				break;

		return height * 3;
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		tag.setInteger("bombsRemaining", bombsRemaining);
		tag.setInteger("targetingOption", targetingOption);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		if (tag.hasKey("bombsRemaining"))
			bombsRemaining = tag.getInteger("bombsRemaining");

		if (tag.hasKey("targetingOption"))
			targetingOption = tag.getInteger("targetingOption");
	}

	public int getBombsRemaining() {
		return bombsRemaining;
	}

	public void setBombsRemaining(int bombsRemaining) {
		this.bombsRemaining = bombsRemaining;
	}

	public int getTargetingOption() {
		return targetingOption;
	}

	public void setTargetingOption(int targetingOption) {
		this.targetingOption = targetingOption;
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}

}
