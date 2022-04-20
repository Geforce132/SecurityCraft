package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

public class UsernameLoggerBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity, ILockable {
	private static final int TICKS_BETWEEN_ATTACKS = 80;
	private IntOption searchRadius = new IntOption(this::getBlockPos, "searchRadius", 3, 1, 20, 1, true);
	public String[] players = new String[100];
	public String[] uuids = new String[100];
	public long[] timestamps = new long[100];
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	public UsernameLoggerBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.USERNAME_LOGGER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (cooldown-- > 0)
			return;

		if (level.getBestNeighborSignal(pos) > 0) {
			level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(searchRadius.get()), e -> !e.isSpectator()).forEach(this::addPlayer);
			syncLoggedPlayersToClient();
		}

		cooldown = TICKS_BETWEEN_ATTACKS;
	}

	public void addPlayer(Player player) {
		String playerName = player.getName().getString();
		long timestamp = System.currentTimeMillis();

		if (!getOwner().isOwner(player) && !EntityUtils.isInvisible(player) && !wasPlayerRecentlyAdded(playerName, timestamp)) {
			//ignore players on the allowlist
			if (ModuleUtils.isAllowed(this, player))
				return;

			for (int i = 0; i < players.length; i++) {
				if (players[i] == null || players[i].equals("")) {
					players[i] = player.getName().getString();
					uuids[i] = player.getGameProfile().getId().toString();
					timestamps[i] = timestamp;
					setChanged();
					break;
				}
			}
		}
	}

	private boolean wasPlayerRecentlyAdded(String username, long timestamp) {
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null && players[i].equals(username) && (timestamps[i] + 1000L) > timestamp) //was within the last second that the same player was last added
				return true;
		}

		return false;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		for (int i = 0; i < players.length; i++) {
			tag.putString("player" + i, players[i] == null ? "" : players[i]);
			tag.putString("uuid" + i, uuids[i] == null ? "" : uuids[i]);
			tag.putLong("timestamp" + i, timestamps[i]);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		for (int i = 0; i < players.length; i++) {
			players[i] = tag.getString("player" + i);
			uuids[i] = tag.getString("uuid" + i);
			timestamps[i] = tag.getLong("timestamp" + i);
		}
	}

	public void syncLoggedPlayersToClient() {
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null)
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new UpdateLogger(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), i, players[i], uuids[i], timestamps[i]));
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
				searchRadius
		};
	}
}
