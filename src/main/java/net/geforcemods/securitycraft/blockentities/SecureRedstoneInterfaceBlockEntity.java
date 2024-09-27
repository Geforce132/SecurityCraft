package net.geforcemods.securitycraft.blockentities;

import java.util.Collection;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SpawnInterfaceHighlightParticle;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class SecureRedstoneInterfaceBlockEntity extends DisguisableBlockEntity implements ITickable {
	public static final Vec3d SENDER_PARTICLE_COLOR = new Vec3d(0.0D, 1.0D, 0.0D);
	public static final Vec3d RECEIVER_PARTICLE_COLOR = new Vec3d(1.0D, 1.0D, 0.0D);
	public static final Vec3d RECEIVER_PARTICLE_COLOR_NO_SIGNAL = new Vec3d(1.0D, 1.0D, 1.0D);
	public static final Vec3d RECEIVER_PROTECTED_PARTICLE_COLOR = new Vec3d(1.0D, 0.0D, 0.0D);
	public static final Vec3d RECEIVER_PROTECTED_PARTICLE_COLOR_NO_SIGNAL = new Vec3d(0.0D, 0.0D, 0.0D);
	public final DisabledOption disabled = new DisabledOption(false);
	private boolean tracked = false, refreshed = false;
	private boolean sender = true;
	private int power = 0;
	private int frequency = 0;
	private int senderRange = 24;
	private boolean protectedSignal = false;
	private boolean sendExactPower = true;
	private boolean receiveInvertedPower = false;
	private boolean highlightConnections = false;
	private float dishRotationDegrees = 0;
	private float oDishRotationDegrees = 0;
	private boolean changed = false;
	private boolean updateNeighbors = false;

	@Override
	public void update() {
		if (!world.isRemote) {
			if (!tracked) {
				if (isSender())
					refreshPower();

				BlockEntityTracker.SECURE_REDSTONE_INTERFACE.track(this);
				tracked = true;
			}
			else if (!refreshed) {
				refreshed = true;

				if (isSender())
					tellSimilarReceiversToRefresh();
			}

			if (changed || updateNeighbors) {
				IBlockState state = world.getBlockState(pos);

				if (changed) {
					world.notifyBlockUpdate(pos, state, state, 2);
					changed = false;
				}

				if (updateNeighbors) {
					BlockUtils.updateIndirectNeighbors(world, pos, state.getBlock(), state.getValue(SecureRedstoneInterfaceBlock.FACING).getOpposite());
					updateNeighbors = false;
				}
			}

			if (shouldHighlightConnections() && world.getTotalWorldTime() % 5 == 0) {
				Collection<EntityPlayerMP> players = TeamUtils.getOnlinePlayersFromOwner(world.getMinecraftServer(), getOwner());

				if (!players.isEmpty()) {
					WorldServer serverLevel = (WorldServer) world;
					Vec3d myPos = Utils.atCenterOf(pos);

					if (isSender()) {
						for (SecureRedstoneInterfaceBlockEntity be : getReceiversISendTo()) {
							if (!be.isDisabled()) {
								Vec3d receiverPos = Utils.atCenterOf(be.pos);

								showParticleTrail(players, serverLevel, myPos, receiverPos, SENDER_PARTICLE_COLOR);
							}
						}
					}
					else {
						for (SecureRedstoneInterfaceBlockEntity be : getSendersThatSendToMe()) {
							Vec3d senderPos = Utils.atCenterOf(be.pos);
							Vec3d color;

							if (be.getPower() == 0)
								color = be.isProtectedSignal() ? RECEIVER_PROTECTED_PARTICLE_COLOR_NO_SIGNAL : RECEIVER_PARTICLE_COLOR_NO_SIGNAL;
							else
								color = be.isProtectedSignal() ? RECEIVER_PROTECTED_PARTICLE_COLOR : RECEIVER_PARTICLE_COLOR;

							showParticleTrail(players, serverLevel, senderPos, myPos, color);
						}
					}
				}
			}
		}
		else {
			oDishRotationDegrees = dishRotationDegrees;

			if (!isDisabled()) {
				dishRotationDegrees = oDishRotationDegrees + 0.05F;

				if (dishRotationDegrees >= 360)
					dishRotationDegrees = 0;
			}
		}
	}

	public void showParticleTrail(Collection<EntityPlayerMP> players, WorldServer level, Vec3d senderPos, Vec3d receiverPos, Vec3d color) {
		Vec3d senderToReceiver = receiverPos.subtract(senderPos);
		double step = senderToReceiver.length() * 6;
		Vec3d particleDirection = senderToReceiver.normalize().scale(0.01F);

		for (int i = 0; i < step; i++) {
			Vec3d particlePos = Utils.lerp(receiverPos, senderPos, i / step);

			for (EntityPlayerMP player : players) {
				SecurityCraft.network.sendTo(new SpawnInterfaceHighlightParticle(particlePos.x, particlePos.y, particlePos.z, color.x, color.y, color.z, particleDirection.x, particleDirection.y, particleDirection.z), player);
			}
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if (!world.isRemote)
			BlockEntityTracker.SECURE_REDSTONE_INTERFACE.stopTracking(this);
	}

	@Override
	public void onOwnerChanged(IBlockState state, World level, BlockPos pos, EntityPlayer player, Owner oldOwner, Owner newOwner) {
		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);

		if (isSender()) {
			int currentFrequency = getFrequency();
			int range = getSenderRange();

			tellSimilarReceiversToRefresh(oldOwner, currentFrequency, range);
		}
		else
			setPower(0);
	}

	@Override
	public boolean needsValidation() {
		return true;
	}

	@Override
	public void onValidate() {
		refreshPower();
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == disabled) {
			if (isDisabled())
				setPower(0);
			else
				refreshPower();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		sender = tag.getBoolean("sender");
		power = tag.getInteger("power");
		frequency = tag.getInteger("frequency");
		senderRange = tag.getInteger("sender_range");
		protectedSignal = tag.getBoolean("protected_signal");
		sendExactPower = tag.getBoolean("send_exact_power");
		receiveInvertedPower = tag.getBoolean("receive_inverted_power");
		highlightConnections = tag.getBoolean("highlight_connections");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("sender", sender);
		tag.setInteger("power", power);
		tag.setInteger("frequency", frequency);
		tag.setInteger("sender_range", senderRange);
		tag.setBoolean("protected_signal", protectedSignal);
		tag.setBoolean("send_exact_power", sendExactPower);
		tag.setBoolean("receive_inverted_power", receiveInvertedPower);
		tag.setBoolean("highlight_connections", highlightConnections);
		return tag;
	}

	public boolean isSender() {
		return sender;
	}

	public void setSender(boolean sender) {
		if (isSender() == sender || !getOwner().isValidated())
			return;

		this.sender = sender;

		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);

			world.setBlockState(pos, state.withProperty(SecureRedstoneInterfaceBlock.SENDER, sender));
			markDirty();

			if (!isDisabled())
				tellSimilarReceiversToRefresh();

			updateNeighbors();
		}
	}

	public int getPower() {
		return power;
	}

	public void refreshPower() {
		refreshPower(getFrequency());
	}

	public void refreshPower(int frequency) {
		if (world.isRemote || isDisabled() || !getOwner().isValidated())
			return;

		if (isSender()) {
			int bestSignal;

			if (isProtectedSignal())
				bestSignal = BlockUtils.hasActiveSCBlockNextTo(world, pos) ? 15 : 0;
			else
				bestSignal = world.getRedstonePowerFromNeighbors(pos);

			if (sendsExactPower())
				setPower(bestSignal);
			else
				setPower(bestSignal > 0 ? 15 : 0);
		}
		else {
			int highestPower = 0;
			boolean protectedSignal = true;
			boolean foundSender = false;

			for (SecureRedstoneInterfaceBlockEntity be : getSendersThatSendToMe(frequency)) {
				int ownPower = be.getPower();

				foundSender = true;

				if (ownPower > highestPower)
					highestPower = ownPower;

				if (!be.isProtectedSignal())
					protectedSignal = false;

				if (highestPower == 15 && !protectedSignal)
					break;
			}

			//if no sender is there, the signal mustn't be protected
			//however if there are only protected senders that don't send a signal, the inverted signal should be protected still
			if (!foundSender)
				protectedSignal = false;

			if (receivesInvertedPower())
				highestPower = 15 - highestPower;

			this.protectedSignal = protectedSignal;
			setPower(highestPower);
		}
	}

	public void setPower(int power) {
		if (getPower() == power)
			return;

		this.power = power;

		if (!world.isRemote) {
			if (isSender())
				tellSimilarReceiversToRefresh();

			markDirty();
			updateNeighbors();
		}
	}

	public int getRedstonePowerOutput() {
		if (!isSender() && !isDisabled())
			return getPower();
		else
			return 0;
	}

	public boolean isProtectedSignal() {
		return protectedSignal;
	}

	public void setProtectedSignal(boolean protectedSignal) {
		if (isProtectedSignal() == protectedSignal || !getOwner().isValidated())
			return;

		this.protectedSignal = protectedSignal;

		if (!world.isRemote) {
			refreshPower();

			if (isSender())
				tellSimilarReceiversToRefresh();
			else
				updateNeighbors();

			markDirty();
		}
	}

	public boolean sendsExactPower() {
		return sendExactPower;
	}

	public void setSendExactPower(boolean sendExactPower) {
		if (sendsExactPower() == sendExactPower || !getOwner().isValidated())
			return;

		this.sendExactPower = sendExactPower;

		if (!world.isRemote && isSender()) {
			refreshPower();
			markDirty();
			updateNeighbors();
		}
	}

	public boolean receivesInvertedPower() {
		return receiveInvertedPower;
	}

	public void setReceiveInvertedPower(boolean receiveInvertedPower) {
		if (receivesInvertedPower() == receiveInvertedPower || !getOwner().isValidated())
			return;

		this.receiveInvertedPower = receiveInvertedPower;

		if (!world.isRemote && !isSender()) {
			refreshPower();
			markDirty();
			updateNeighbors();
		}
	}

	/**
	 * Tells receivers in range with the same owner and frequency of this one to refresh their power
	 */
	public void tellSimilarReceiversToRefresh() {
		tellSimilarReceiversToRefresh(getOwner(), getFrequency(), getSenderRange());
	}

	/**
	 * Tells receivers in range that have the same owner and frequency given to refresh their power
	 *
	 * @param owner The owner the receivers that should be refreshed belong to
	 * @param frequency The frequency that the receivers that should be refreshed need to have
	 * @param range The range around this block entity's position in which to update the receivers
	 */
	public void tellSimilarReceiversToRefresh(Owner owner, int frequency, int range) {
		if (!world.isRemote && getOwner().isValidated()) {
			for (SecureRedstoneInterfaceBlockEntity be : getReceiversToSendTo(owner, frequency, range)) {
				be.refreshPower(frequency);
			}
		}
	}

	public void setFrequency(int frequency) {
		int oldFrequency = getFrequency();

		if (oldFrequency == frequency || !getOwner().isValidated())
			return;

		this.frequency = frequency;

		if (!world.isRemote) {
			Owner owner = getOwner();
			int range = getSenderRange();

			if (isSender()) {
				tellSimilarReceiversToRefresh(owner, oldFrequency, range);
				tellSimilarReceiversToRefresh(owner, frequency, range);
			}
			else
				refreshPower();

			markDirty();
		}
	}

	public int getFrequency() {
		return frequency;
	}

	public boolean isSameFrequency(int frequency) {
		return getFrequency() == frequency;
	}

	public void setSenderRange(int senderRange) {
		senderRange = MathHelper.clamp(senderRange, 1, 64);

		if (getSenderRange() == senderRange || !getOwner().isValidated())
			return;

		int oldRange = this.senderRange;

		this.senderRange = senderRange;

		if (!world.isRemote && !isDisabled() && isSender()) {
			Owner owner = getOwner();
			int frequency = getFrequency();

			tellSimilarReceiversToRefresh(owner, frequency, Math.max(oldRange, senderRange));
			markDirty();
		}
	}

	public List<SecureRedstoneInterfaceBlockEntity> getReceiversISendTo() {
		return getReceiversToSendTo(getOwner(), getFrequency(), getSenderRange());
	}

	public List<SecureRedstoneInterfaceBlockEntity> getReceiversToSendTo(Owner owner, int frequency, int range) {
		List<SecureRedstoneInterfaceBlockEntity> all = BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getTileEntitiesAround(world, pos, range);

		all.removeIf(be -> be.isSender() || !be.isOwnedBy(owner) || !be.isSameFrequency(frequency));
		return all;
	}

	public List<SecureRedstoneInterfaceBlockEntity> getSendersThatSendToMe() {
		return getSendersThatSendToMe(getFrequency());
	}

	public List<SecureRedstoneInterfaceBlockEntity> getSendersThatSendToMe(int frequency) {
		List<SecureRedstoneInterfaceBlockEntity> all = BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getTileEntitiesInRange(world, pos);

		all.removeIf(be -> be.isDisabled() || !be.isSender() || !be.isOwnedBy(getOwner()) || !be.isSameFrequency(frequency));
		return all;
	}

	public int getSenderRange() {
		return senderRange;
	}

	public boolean shouldHighlightConnections() {
		return highlightConnections;
	}

	public void setHighlightConnections(boolean highlightConnections) {
		this.highlightConnections = highlightConnections;
		markDirty();
	}

	public void updateNeighbors() {
		updateNeighbors = true;
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				disabled
		};
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE
		};
	}

	public float getOriginalDishRotationDegrees() {
		return oDishRotationDegrees;
	}

	public float getDishRotationDegrees() {
		return dishRotationDegrees;
	}

	@Override
	public void markDirty() {
		changed = true;
		super.markDirty();
	}
}
