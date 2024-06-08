package net.geforcemods.securitycraft.blockentities;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class PortableRadarBlockEntity extends CustomizableBlockEntity implements ITickable {
	private DoubleOption searchRadiusOption = new DoubleOption(this::getPos, "searchRadius", 25.0D, 1.0D, 50.0D, 1.0D);
	private IntOption searchDelayOption = new IntOption(this::getPos, "searchDelay", 4, 4, 10, 1);
	private BooleanOption repeatMessageOption = new BooleanOption("repeatMessage", true);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private Set<Owner> seenPlayers = new HashSet<>();
	private int ticksUntilNextSearch = getSearchDelay();

	@Override
	public void update() {
		if (!world.isRemote && !disabled.get() && ticksUntilNextSearch-- <= 0) {
			ticksUntilNextSearch = getSearchDelay();

			AxisAlignedBB area = new AxisAlignedBB(pos).grow(getSearchRadius(), getSearchRadius(), getSearchRadius());
			List<EntityPlayer> closebyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, area, e -> !(isOwnedBy(e) && ignoresOwner()) && !isAllowed(e) && !e.isSpectator() && !respectInvisibility.isConsideredInvisible(e));
			List<Owner> closebyOwners = closebyPlayers.stream().map(Owner::new).collect(Collectors.toList());

			if (isModuleEnabled(ModuleType.REDSTONE)) {
				PortableRadarBlock.togglePowerOutput(world, pos, !closebyPlayers.isEmpty());
			}

			if (!closebyPlayers.isEmpty()) {
				Collection<EntityPlayerMP> onlineTeamPlayers = TeamUtils.getOnlinePlayersFromOwner(world.getMinecraftServer(), getOwner());

				for (EntityPlayer closebyPlayer : closebyPlayers) {
					if (shouldSendMessage(closebyPlayer)) {
						String attackedName = TextFormatting.ITALIC + closebyPlayer.getName() + TextFormatting.RESET;
						ITextComponent text;

						if (hasCustomName())
							text = Utils.localize("messages.securitycraft:portableRadar.withName", attackedName, TextFormatting.ITALIC + getName() + TextFormatting.RESET, pos);
						else
							text = Utils.localize("messages.securitycraft:portableRadar.withoutName", attackedName, pos);

						if (!onlineTeamPlayers.isEmpty())
							onlineTeamPlayers.forEach(player -> PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.portableRadar), text, TextFormatting.BLUE));
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
			PortableRadarBlock.togglePowerOutput(world, pos, false);
	}

	@Override
	public void readOptions(NBTTagCompound tag) {
		if (tag.hasKey("enabled"))
			tag.setBoolean("disabled", !tag.getBoolean("enabled")); //legacy support

		for (Option<?> option : customOptions()) {
			option.load(tag);
		}
	}

	public boolean shouldSendMessage(EntityPlayer player) {
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
