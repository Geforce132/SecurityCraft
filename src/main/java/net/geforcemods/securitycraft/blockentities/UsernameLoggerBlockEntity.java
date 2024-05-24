package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.network.PacketDistributor;

public class UsernameLoggerBlockEntity extends DisguisableBlockEntity implements ITickableTileEntity, ILockable {
	private static final int TICKS_BETWEEN_ATTACKS = 80;
	private IntOption searchRadius = new IntOption(this::getBlockPos, "searchRadius", 3, 1, 20, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private String[] players = new String[100];
	private String[] uuids = new String[100];
	private long[] timestamps = new long[100];
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	public UsernameLoggerBlockEntity() {
		super(SCContent.USERNAME_LOGGER_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		if (!level.isClientSide) {
			if (isDisabled())
				return;

			if (cooldown > 0)
				cooldown--;
			else if (level.getBestNeighborSignal(worldPosition) > 0) {
				long timestamp = System.currentTimeMillis();
				List<PlayerEntity> nearbyPlayers = level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(worldPosition).inflate(searchRadius.get()), e -> !e.isSpectator() && !(isOwnedBy(e) && ignoresOwner() || isAllowed(e)) && !respectInvisibility.isConsideredInvisible(e) && !wasPlayerRecentlyAdded(e.getName().getString(), timestamp));

				if (!nearbyPlayers.isEmpty()) {
					boolean changed = false;
					int playerIndex = 0;

					for (int i = 0; i < getPlayers().length && playerIndex < nearbyPlayers.size(); i++) {
						if (getPlayers()[i] == null || getPlayers()[i].equals("")) {
							PlayerEntity player = nearbyPlayers.get(playerIndex++);

							getPlayers()[i] = player.getName().getString();
							getUuids()[i] = player.getGameProfile().getId().toString();
							getTimestamps()[i] = timestamp;
							changed = true;
						}
					}

					if (changed) {
						setChanged();
						syncLoggedPlayersToClient();
					}
				}

				cooldown = TICKS_BETWEEN_ATTACKS;
			}
		}
	}

	private boolean wasPlayerRecentlyAdded(String username, long timestamp) {
		for (int i = 0; i < getPlayers().length; i++) {
			if (getPlayers()[i] != null && getPlayers()[i].equals(username) && (getTimestamps()[i] + 1000L) > timestamp) //was within the last second that the same player was last added
				return true;
		}

		return false;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		for (int i = 0; i < getPlayers().length; i++) {
			tag.putString("player" + i, getPlayers()[i] == null ? "" : getPlayers()[i]);
			tag.putString("uuid" + i, getUuids()[i] == null ? "" : getUuids()[i]);
			tag.putLong("timestamp" + i, getTimestamps()[i]);
		}

		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		for (int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i] = tag.getString("player" + i);
			getUuids()[i] = tag.getString("uuid" + i);
			getTimestamps()[i] = tag.getLong("timestamp" + i);
		}
	}

	public void syncLoggedPlayersToClient() {
		for (int i = 0; i < getPlayers().length; i++) {
			if (getPlayers()[i] != null)
				SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new UpdateLogger(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), i, getPlayers()[i], getUuids()[i], getTimestamps()[i]));
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				searchRadius, disabled, ignoreOwner, respectInvisibility
		};
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public String[] getPlayers() {
		return players;
	}

	public void setPlayers(String[] players) {
		this.players = players;
	}

	public String[] getUuids() {
		return uuids;
	}

	public long[] getTimestamps() {
		return timestamps;
	}
}
