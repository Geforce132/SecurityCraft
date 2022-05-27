package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockAlarm;
import net.geforcemods.securitycraft.blocks.BlockOldLitAlarm;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;

public class TileEntityAlarm extends CustomizableSCTE implements ITickable {
	public OptionInt range = new OptionInt(this::getPos, "range", 17, 0, ConfigHandler.maxAlarmRange, 1, true);
	private OptionInt delay = new OptionInt(this::getPos, "delay", 2, 1, 30, 1, true);
	private int cooldown = 0;
	private boolean isPowered = false;

	@Override
	public void update() {
		//convert the old lit alarm block to the old unlit alarm block, which now has a LIT property
		if (getBlockType() == SCContent.alarmLit) {
			world.setBlockState(pos, SCContent.alarm.getDefaultState().withProperty(BlockAlarm.FACING, world.getBlockState(pos).getValue(BlockOldLitAlarm.FACING)).withProperty(BlockAlarm.LIT, false));

			TileEntityAlarm newTe = (TileEntityAlarm) world.getTileEntity(pos);

			newTe.getOwner().set(getOwner().getUUID(), getOwner().getName());
			newTe.range.copy(range);
			newTe.delay.copy(delay);
			newTe.setPowered(false);
			invalidate();
			return;
		}

		if (!world.isRemote && isPowered && --cooldown <= 0) {
			double rangeSqr = Math.pow(range.get(), 2);

			for (EntityPlayerMP player : world.getPlayers(EntityPlayerMP.class, p -> p.getPosition().distanceSq(pos) <= rangeSqr)) {
				float volume = (float) (1.0F - ((player.getPosition().distanceSq(pos)) / rangeSqr));

				player.connection.sendPacket(new SPacketSoundEffect(SCSounds.ALARM.event, SoundCategory.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), volume, 1.0F));
			}

			setCooldown(delay.get() * 20);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("cooldown", cooldown);
		tag.setBoolean("isPowered", isPowered);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		cooldown = tag.getInteger("cooldown");
		isPowered = tag.getBoolean("isPowered");
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				range, delay
		};
	}
}
