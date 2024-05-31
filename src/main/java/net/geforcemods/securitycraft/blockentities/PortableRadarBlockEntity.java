package net.geforcemods.securitycraft.blockentities;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.PortableRadarBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PortableRadarBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	private DoubleOption searchRadiusOption = new DoubleOption("searchRadius", 25.0D, 1.0D, 50.0D, 1.0D);
	private IntOption searchDelayOption = new IntOption("searchDelay", 4, 4, 10, 1);
	private BooleanOption repeatMessageOption = new BooleanOption("repeatMessage", true);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private Set<Owner> seenPlayers = new HashSet<>();
	private int ticksUntilNextSearch = getSearchDelay();

	public PortableRadarBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.PORTABLE_RADAR_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!disabled.get() && ticksUntilNextSearch-- <= 0) {
			ticksUntilNextSearch = getSearchDelay();

			AABB area = new AABB(pos).inflate(getSearchRadius());
			List<Player> closebyPlayers = level.getEntitiesOfClass(Player.class, area, e -> !(isOwnedBy(e) && ignoresOwner()) && !isAllowed(e) && e.canBeSeenByAnyone() && !respectInvisibility.isConsideredInvisible(e));
			List<Owner> closebyOwners = closebyPlayers.stream().map(Owner::new).toList();

			if (isModuleEnabled(ModuleType.REDSTONE))
				PortableRadarBlock.togglePowerOutput(level, pos, !closebyPlayers.isEmpty());

			if (!closebyPlayers.isEmpty()) {
				Collection<ServerPlayer> onlineTeamPlayers = TeamUtils.getOnlinePlayersInTeam(level.getServer(), getOwner());

				if (onlineTeamPlayers.isEmpty()) { //owner may not be in a team
					ServerPlayer ownerPlayer = level.getServer().getPlayerList().getPlayerByName(getOwner().getName());

					if (ownerPlayer != null)
						onlineTeamPlayers = Arrays.asList(ownerPlayer);
				}

				for (Player closebyPlayer : closebyPlayers) {
					if (shouldSendMessage(closebyPlayer)) {
						MutableComponent attackedName = closebyPlayer.getName().plainCopy().withStyle(ChatFormatting.ITALIC);
						Component coords = Utils.getFormattedCoordinates(pos);
						MutableComponent text;

						if (hasCustomName())
							text = Utils.localize("messages.securitycraft:portableRadar.withName", attackedName, getCustomName().plainCopy().withStyle(ChatFormatting.ITALIC), coords);
						else
							text = Utils.localize("messages.securitycraft:portableRadar.withoutName", attackedName, coords);

						if (!onlineTeamPlayers.isEmpty())
							onlineTeamPlayers.forEach(player -> PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.PORTABLE_RADAR.get().getDescriptionId()), text, ChatFormatting.BLUE));
					}
				}
			}

			seenPlayers.removeIf(owner -> !closebyOwners.contains(owner));
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.REDSTONE)
			PortableRadarBlock.togglePowerOutput(level, worldPosition, false);
	}

	@Override
	public void readOptions(CompoundTag tag) {
		if (tag.contains("enabled"))
			tag.putBoolean("disabled", !tag.getBoolean("enabled")); //legacy support

		for (Option<?> option : customOptions()) {
			option.load(tag);
		}
	}

	public boolean shouldSendMessage(Player player) {
		return seenPlayers.add(new Owner(player)) || repeatMessageOption.get();
	}

	public double getSearchRadius() {
		return searchRadiusOption.get();
	}

	public int getSearchDelay() {
		return searchDelayOption.get() * 20;
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.REDSTONE, ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				searchRadiusOption, searchDelayOption, repeatMessageOption, disabled, ignoreOwner, respectInvisibility
		};
	}
}
