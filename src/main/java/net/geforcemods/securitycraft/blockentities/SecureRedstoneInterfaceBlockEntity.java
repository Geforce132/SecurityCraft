package net.geforcemods.securitycraft.blockentities;

import java.util.Collection;
import java.util.List;

import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.SecureRedstoneInterfaceBlock;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.particle.InterfaceHighlightParticleOptions;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SecureRedstoneInterfaceBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity {
	public static final Vector3f SENDER_PARTICLE_COLOR = new Vector3f(0.0F, 1.0F, 0.0F);
	public static final Vector3f RECEIVER_PARTICLE_COLOR = new Vector3f(1.0F, 1.0F, 0.0F);
	public static final Vector3f RECEIVER_PARTICLE_COLOR_NO_SIGNAL = new Vector3f(1.0F, 1.0F, 1.0F);
	public static final Vector3f RECEIVER_PROTECTED_PARTICLE_COLOR = new Vector3f(1.0F, 0.0F, 0.0F);
	public static final Vector3f RECEIVER_PROTECTED_PARTICLE_COLOR_NO_SIGNAL = new Vector3f(0.0F, 0.0F, 0.0F);
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

	public SecureRedstoneInterfaceBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.SECURE_REDSTONE_INTERFACE_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!level.isClientSide) {
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

			if (changed) {
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
				changed = false;
			}

			if (updateNeighbors) {
				BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock(), getBlockState().getValue(SecureRedstoneInterfaceBlock.FACING).getOpposite());
				updateNeighbors = false;
			}

			if (shouldHighlightConnections() && level.getGameTime() % 5 == 0) {
				Collection<ServerPlayer> players = TeamUtils.getOnlinePlayersFromOwner(level.getServer(), getOwner());

				if (!players.isEmpty()) {
					ServerLevel serverLevel = (ServerLevel) level;
					Vec3 myPos = Vec3.atCenterOf(pos);

					if (isSender()) {
						for (SecureRedstoneInterfaceBlockEntity be : getReceiversISendTo()) {
							if (!be.isDisabled()) {
								Vec3 receiverPos = Vec3.atCenterOf(be.worldPosition);

								showParticleTrail(players, serverLevel, myPos, receiverPos, SENDER_PARTICLE_COLOR);
							}
						}
					}
					else {
						for (SecureRedstoneInterfaceBlockEntity be : getSendersThatSendToMe()) {
							Vec3 senderPos = Vec3.atCenterOf(be.worldPosition);
							Vector3f color;

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

	public void showParticleTrail(Collection<ServerPlayer> players, ServerLevel level, Vec3 senderPos, Vec3 receiverPos, Vector3f color) {
		Vec3 senderToReceiver = receiverPos.subtract(senderPos);
		double step = senderToReceiver.length() * 6;
		Vector3f particleDirection = new Vector3f(senderToReceiver.normalize().scale(0.01F));

		for (int i = 0; i < step; i++) {
			Vec3 particlePos = receiverPos.lerp(senderPos, i / step);

			for (ServerPlayer player : players) {
				level.sendParticles(player, new InterfaceHighlightParticleOptions(new Vector3f(color.x(), color.y(), color.z()), particleDirection, 1.0F), false, particlePos.x, particlePos.y, particlePos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (!level.isClientSide)
			BlockEntityTracker.SECURE_REDSTONE_INTERFACE.stopTracking(this);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
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
	public void load(CompoundTag tag) {
		super.load(tag);
		sender = tag.getBoolean("sender");
		power = tag.getInt("power");
		frequency = tag.getInt("frequency");
		senderRange = tag.getInt("sender_range");
		protectedSignal = tag.getBoolean("protected_signal");
		sendExactPower = tag.getBoolean("send_exact_power");
		receiveInvertedPower = tag.getBoolean("receive_inverted_power");
		highlightConnections = tag.getBoolean("highlight_connections");
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("sender", sender);
		tag.putInt("power", power);
		tag.putInt("frequency", frequency);
		tag.putInt("sender_range", senderRange);
		tag.putBoolean("protected_signal", protectedSignal);
		tag.putBoolean("send_exact_power", sendExactPower);
		tag.putBoolean("receive_inverted_power", receiveInvertedPower);
		tag.putBoolean("highlight_connections", highlightConnections);
	}

	public boolean isSender() {
		return sender;
	}

	public void setSender(boolean sender) {
		if (isSender() == sender || !getOwner().isValidated())
			return;

		this.sender = sender;

		if (!level.isClientSide) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecureRedstoneInterfaceBlock.SENDER, sender));
			setChanged();

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
		if (level.isClientSide || isDisabled() || !getOwner().isValidated())
			return;

		if (isSender()) {
			int bestSignal;

			if (isProtectedSignal())
				bestSignal = BlockUtils.hasActiveSCBlockNextTo(level, worldPosition) ? 15 : 0;
			else
				bestSignal = level.getBestNeighborSignal(worldPosition);

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

		if (!level.isClientSide) {
			if (isSender())
				tellSimilarReceiversToRefresh();

			setChanged();
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

		if (!level.isClientSide) {
			refreshPower();

			if (isSender())
				tellSimilarReceiversToRefresh();
			else
				updateNeighbors();

			setChanged();
		}
	}

	public boolean sendsExactPower() {
		return sendExactPower;
	}

	public void setSendExactPower(boolean sendExactPower) {
		if (sendsExactPower() == sendExactPower || !getOwner().isValidated())
			return;

		this.sendExactPower = sendExactPower;

		if (!level.isClientSide && isSender()) {
			refreshPower();
			setChanged();
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

		if (!level.isClientSide && !isSender()) {
			refreshPower();
			setChanged();
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
		if (!level.isClientSide && getOwner().isValidated()) {
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

		if (!level.isClientSide) {
			Owner owner = getOwner();
			int range = getSenderRange();

			if (isSender()) {
				tellSimilarReceiversToRefresh(owner, oldFrequency, range);
				tellSimilarReceiversToRefresh(owner, frequency, range);
			}
			else
				refreshPower();

			setChanged();
		}
	}

	public int getFrequency() {
		return frequency;
	}

	public boolean isSameFrequency(int frequency) {
		return getFrequency() == frequency;
	}

	public void setSenderRange(int senderRange) {
		senderRange = Mth.clamp(senderRange, 1, 64);

		if (getSenderRange() == senderRange || !getOwner().isValidated())
			return;

		int oldRange = this.senderRange;

		this.senderRange = senderRange;

		if (!level.isClientSide && !isDisabled() && isSender()) {
			Owner owner = getOwner();
			int frequency = getFrequency();

			tellSimilarReceiversToRefresh(owner, frequency, Math.max(oldRange, senderRange));
			setChanged();
		}
	}

	public List<SecureRedstoneInterfaceBlockEntity> getReceiversISendTo() {
		return getReceiversToSendTo(getOwner(), getFrequency(), getSenderRange());
	}

	public List<SecureRedstoneInterfaceBlockEntity> getReceiversToSendTo(Owner owner, int frequency, int range) {
		List<SecureRedstoneInterfaceBlockEntity> all = BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getBlockEntitiesAround(level, worldPosition, range);

		all.removeIf(be -> be.isSender() || !be.isOwnedBy(owner) || !be.isSameFrequency(frequency));
		return all;
	}

	public List<SecureRedstoneInterfaceBlockEntity> getSendersThatSendToMe() {
		return getSendersThatSendToMe(getFrequency());
	}

	public List<SecureRedstoneInterfaceBlockEntity> getSendersThatSendToMe(int frequency) {
		List<SecureRedstoneInterfaceBlockEntity> all = BlockEntityTracker.SECURE_REDSTONE_INTERFACE.getBlockEntitiesInRange(level, worldPosition);

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
		setChanged();
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
	public void setChanged() {
		changed = true;
		super.setChanged();
	}
}
