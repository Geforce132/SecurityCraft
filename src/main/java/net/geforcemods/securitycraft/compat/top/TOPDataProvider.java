package net.geforcemods.securitycraft.compat.top;

import java.util.function.Function;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

public class TOPDataProvider implements Function<ITheOneProbe, Void> {
	private static final IFormattableTextComponent EQUIPPED = Utils.localize("waila.securitycraft:equipped").withStyle(Utils.GRAY_STYLE);
	private static final IFormattableTextComponent ALLOWLIST_MODULE = new StringTextComponent("- ").append(new TranslationTextComponent(ModuleType.ALLOWLIST.getTranslationKey())).withStyle(TextFormatting.GRAY);
	private static final IFormattableTextComponent DISGUISE_MODULE = new StringTextComponent("- ").append(new TranslationTextComponent(ModuleType.DISGUISE.getTranslationKey())).withStyle(TextFormatting.GRAY);
	private static final IFormattableTextComponent SPEED_MODULE = new StringTextComponent("- ").append(new TranslationTextComponent(ModuleType.SPEED.getTranslationKey())).withStyle(TextFormatting.GRAY);

	@Nullable
	@Override
	public Void apply(ITheOneProbe theOneProbe) {
		theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
			ItemStack disguisedAs = ItemStack.EMPTY;

			if (blockState.getBlock() instanceof DisguisableBlock)
				disguisedAs = ((DisguisableBlock) blockState.getBlock()).getDisguisedStack(world, data.getPos());
			else if (blockState.getBlock() instanceof IOverlayDisplay) {
				ItemStack displayStack = ((IOverlayDisplay) blockState.getBlock()).getDisplayStack(world, blockState, data.getPos());

				if (displayStack != null)
					disguisedAs = displayStack;
			}

			if (!disguisedAs.isEmpty()) {
				//@formatter:off
				probeInfo.horizontal()
				.item(disguisedAs)
				.vertical()
				.itemLabel(disguisedAs)
				.mcText(new StringTextComponent(ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName()).withStyle(TextFormatting.BLUE, TextFormatting.ITALIC));
				return true;
				//@formatter:on
			}

			return false;
		});
		theOneProbe.registerProvider(new IProbeInfoProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
				Block block = blockState.getBlock();

				if (block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(world, blockState, data.getPos()))
					return;

				TileEntity te = world.getBlockEntity(data.getPos());

				if (te instanceof IOwnable)
					probeInfo.vertical().mcText(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(((IOwnable) te).getOwner())).withStyle(TextFormatting.GRAY));

				//if the te is ownable, show modules only when it's owned, otherwise always show
				if (te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(player))) {
					if (!((IModuleInventory) te).getInsertedModules().isEmpty()) {
						probeInfo.mcText(EQUIPPED);

						for (ModuleType module : ((IModuleInventory) te).getInsertedModules()) {
							probeInfo.mcText(new StringTextComponent("- ").append(new TranslationTextComponent(module.getTranslationKey())).withStyle(TextFormatting.GRAY));
						}
					}
				}

				if (te instanceof INameable && ((INameable) te).hasCustomName()) {
					ITextComponent text = ((INameable) te).getCustomName();

					probeInfo.mcText(Utils.localize("waila.securitycraft:customName", text == null ? StringTextComponent.EMPTY : text).withStyle(TextFormatting.GRAY));
				}
			}
		});
		theOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo probeInfo, PlayerEntity player, World world, Entity entity, IProbeHitEntityData data) {
				if (entity instanceof Sentry) {
					Sentry sentry = (Sentry) entity;
					SentryMode mode = sentry.getMode();

					probeInfo.mcText(Utils.localize("waila.securitycraft:owner", PlayerUtils.getOwnerComponent(sentry.getOwner())).withStyle(TextFormatting.GRAY));

					if (!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule()) {
						probeInfo.mcText(EQUIPPED);

						if (!sentry.getAllowlistModule().isEmpty())
							probeInfo.mcText(ALLOWLIST_MODULE);

						if (!sentry.getDisguiseModule().isEmpty())
							probeInfo.mcText(DISGUISE_MODULE);

						if (sentry.hasSpeedModule())
							probeInfo.mcText(SPEED_MODULE);
					}

					IFormattableTextComponent modeDescription = Utils.localize(mode.getModeKey());

					if (mode != SentryMode.IDLE)
						modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

					probeInfo.mcText(modeDescription.withStyle(TextFormatting.GRAY));
				}
			}
		});
		return null;
	}
}