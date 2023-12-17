package net.geforcemods.securitycraft.blockentities;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.PortableRadarBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;

public class PortableRadarBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity {
	private DoubleOption searchRadiusOption = new DoubleOption(this::getBlockPos, "searchRadius", 25.0D, 1.0D, 50.0D, 1.0D, true);
	private IntOption searchDelayOption = new IntOption(this::getBlockPos, "searchDelay", 4, 4, 10, 1, true);
	private BooleanOption repeatMessageOption = new BooleanOption("repeatMessage", true);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private boolean shouldSendNewMessage = true;
	private Owner lastPlayer = new Owner();
	private int ticksUntilNextSearch = getSearchDelay();

	public PortableRadarBlockEntity() {
		super(SCContent.PORTABLE_RADAR_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		if (!level.isClientSide && !disabled.get() && ticksUntilNextSearch-- <= 0) {
			ticksUntilNextSearch = getSearchDelay();

			AxisAlignedBB area = new AxisAlignedBB(worldPosition).inflate(getSearchRadius(), getSearchRadius(), getSearchRadius());
			List<PlayerEntity> closebyPlayers = level.getEntitiesOfClass(PlayerEntity.class, area, e -> !(isOwnedBy(e) && ignoresOwner()) && !isAllowed(e) && !e.isSpectator() && !EntityUtils.isInvisible(e));

			if (isModuleEnabled(ModuleType.REDSTONE))
				PortableRadarBlock.togglePowerOutput(level, worldPosition, !closebyPlayers.isEmpty());

			if (!closebyPlayers.isEmpty()) {
				Collection<ServerPlayerEntity> onlineTeamPlayers = TeamUtils.getOnlinePlayersInTeam(level.getServer(), getOwner());

				if (onlineTeamPlayers.isEmpty()) { //owner may not be in a team
					ServerPlayerEntity ownerPlayer = level.getServer().getPlayerList().getPlayerByName(getOwner().getName());

					if (ownerPlayer != null)
						onlineTeamPlayers = Arrays.asList(ownerPlayer);
				}

				for (PlayerEntity closebyPlayer : closebyPlayers) {
					if (shouldSendMessage(closebyPlayer)) {
						IFormattableTextComponent attackedName = closebyPlayer.getName().plainCopy().withStyle(TextFormatting.ITALIC);
						IFormattableTextComponent text;

						if (hasCustomName())
							text = Utils.localize("messages.securitycraft:portableRadar.withName", attackedName, getCustomName().plainCopy().withStyle(TextFormatting.ITALIC));
						else
							text = Utils.localize("messages.securitycraft:portableRadar.withoutName", attackedName, Utils.getFormattedCoordinates(worldPosition));

						if (!onlineTeamPlayers.isEmpty())
							onlineTeamPlayers.forEach(player -> PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.PORTABLE_RADAR.get().getDescriptionId()), text, TextFormatting.BLUE));

						setSentMessage();
					}
				}
			}
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.REDSTONE)
			PortableRadarBlock.togglePowerOutput(level, worldPosition, false);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		CompoundNBT lastPlayerTag = new CompoundNBT();

		tag.putBoolean("shouldSendNewMessage", shouldSendNewMessage);
		lastPlayer.save(lastPlayerTag, needsValidation());
		tag.put("lastPlayer", lastPlayerTag);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		shouldSendNewMessage = tag.getBoolean("shouldSendNewMessage");
		lastPlayer = Owner.fromCompound(tag.getCompound("lastPlayer"));
	}

	@Override
	public void readOptions(CompoundNBT tag) {
		if (tag.contains("enabled"))
			tag.putBoolean("disabled", !tag.getBoolean("enabled")); //legacy support

		for (Option<?> option : customOptions()) {
			option.load(tag);
		}
	}

	public boolean shouldSendMessage(PlayerEntity player) {
		Owner currentPlayer = new Owner(player);

		if (!currentPlayer.equals(lastPlayer)) {
			shouldSendNewMessage = true;
			lastPlayer = currentPlayer;
		}

		return (shouldSendNewMessage || repeatMessageOption.get()) && !(lastPlayer.owns(this) && ignoresOwner());
	}

	public void setSentMessage() {
		shouldSendNewMessage = false;
	}

	public double getSearchRadius() {
		return searchRadiusOption.get();
	}

	public int getSearchDelay() {
		return searchDelayOption.get() * 20;
	}

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
				searchRadiusOption, searchDelayOption, repeatMessageOption, disabled, ignoreOwner
		};
	}
}
