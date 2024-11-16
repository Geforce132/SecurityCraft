package net.geforcemods.securitycraft.blockentities;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
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
	private IntOption searchRadiusOption = new IntOption("searchRadius", 25, 1, 50, 1);
	private IntOption searchDelayOption = new IntOption("searchDelay", 4, 4, 10, 1);
	private BooleanOption repeatMessageOption = new BooleanOption("repeatMessage", true);
	private BooleanOption sendToTeamMembersOption = new BooleanOption("sendToTeamMembers", true) {
		@Override
		public Boolean get() {
			return ConfigHandler.SERVER.enableTeamOwnership.get() && super.get();
		}
	};
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
			List<Player> closebyPlayers = level.getEntitiesOfClass(Player.class, area, e -> !(isOwnedBy(e) && ignoresOwner()) && ((!isModuleEnabled(ModuleType.DENYLIST) && !isAllowed(e)) || isDenied(e)) && e.canBeSeenByAnyone() && !respectInvisibility.isConsideredInvisible(e));
			List<Owner> closebyOwners = closebyPlayers.stream().map(Owner::new).toList();

			if (isModuleEnabled(ModuleType.REDSTONE))
				PortableRadarBlock.togglePowerOutput(level, pos, !closebyPlayers.isEmpty());

			if (!closebyPlayers.isEmpty()) {
				Component coords = Utils.getFormattedCoordinates(pos);
				Collection<ServerPlayer> messageReceivers;

				if (sendToTeamMembersOption.get())
					messageReceivers = TeamUtils.getOnlinePlayersFromOwner(level.getServer(), getOwner());
				else
					messageReceivers = PlayerUtils.getPlayerListFromOwner(getOwner());

				for (Player closebyPlayer : closebyPlayers) {
					if (shouldSendMessage(closebyPlayer)) {
						MutableComponent attackedName = closebyPlayer.getName().plainCopy().withStyle(ChatFormatting.ITALIC);
						MutableComponent text;

						if (hasCustomName())
							text = Utils.localize("messages.securitycraft:portableRadar.withName", attackedName, getCustomName().plainCopy().withStyle(ChatFormatting.ITALIC), coords);
						else
							text = Utils.localize("messages.securitycraft:portableRadar.withoutName", attackedName, coords);

						if (!messageReceivers.isEmpty())
							messageReceivers.forEach(player -> PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.PORTABLE_RADAR.get().getDescriptionId()), text, ChatFormatting.BLUE));
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
				ModuleType.REDSTONE, ModuleType.ALLOWLIST, ModuleType.DENYLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		Option<?>[] options = new Option[] {
				searchRadiusOption, searchDelayOption, repeatMessageOption, disabled, ignoreOwner, respectInvisibility
		};

		if (ConfigHandler.SERVER.enableTeamOwnership.get())
			options = ArrayUtils.insert(3, options, sendToTeamMembersOption);

		return options;
	}
}
