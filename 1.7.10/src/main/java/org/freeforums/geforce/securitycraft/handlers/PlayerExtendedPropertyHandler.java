package org.freeforums.geforce.securitycraft.handlers;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerExtendedPropertyHandler implements IExtendedEntityProperties {

	private final String securitycraftTagName = "SCExtraInfo";
	protected EntityPlayer player;
	protected World world;
	
	public void saveNBTData(NBTTagCompound compound) {
		if(mod_SecurityCraft.instance.hasUsePosition(player.getCommandSenderName())){
			NBTTagCompound tempCompound = new NBTTagCompound();
			compound.setTag(securitycraftTagName, tempCompound);
			tempCompound.setDouble("MonitorUseX", mod_SecurityCraft.instance.getUsePosition(player.getCommandSenderName())[0]);
			tempCompound.setDouble("MonitorUseY", mod_SecurityCraft.instance.getUsePosition(player.getCommandSenderName())[1]);
			tempCompound.setDouble("MonitorUseZ", mod_SecurityCraft.instance.getUsePosition(player.getCommandSenderName())[2]);
		}
	}

	public void loadNBTData(NBTTagCompound compound) {
		if(compound.getTag(securitycraftTagName) != null && ((NBTTagCompound) compound.getTag(securitycraftTagName)).hasKey("MonitorUseX") && ((NBTTagCompound) compound.getTag(securitycraftTagName)).hasKey("MonitorUseY") && ((NBTTagCompound) compound.getTag(securitycraftTagName)).hasKey("MonitorUseZ") && !mod_SecurityCraft.instance.hasUsePosition(player.getCommandSenderName())){
			mod_SecurityCraft.instance.setUsePosition(player.getCommandSenderName(), ((NBTTagCompound) compound.getTag(securitycraftTagName)).getDouble("MonitorUseX"), ((NBTTagCompound) compound.getTag(securitycraftTagName)).getDouble("MonitorUseY"), ((NBTTagCompound) compound.getTag(securitycraftTagName)).getDouble("MonitorUseZ"));
		}
	}

	public void init(Entity entity, World world) {
		this.player = (EntityPlayer) entity;
		this.world = world;
	}

}
