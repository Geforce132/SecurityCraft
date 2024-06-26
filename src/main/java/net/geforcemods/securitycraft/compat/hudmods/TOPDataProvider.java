package net.geforcemods.securitycraft.compat.hudmods;

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
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;

public class TOPDataProvider extends HudModHandler implements Function<ITheOneProbe, Void> {
	@Nullable
	@Override
	public Void apply(ITheOneProbe theOneProbe) {
		theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, level, state, data) -> {
			ItemStack disguisedAs = ItemStack.EMPTY;

			if (state.getBlock() instanceof IDisguisable disguisedBlock)
				disguisedAs = disguisedBlock.getDisguisedStack(level, data.getPos());
			else if (state.getBlock() instanceof IOverlayDisplay display) {
				ItemStack displayStack = display.getDisplayStack(level, state, data.getPos());

				if (displayStack != null)
					disguisedAs = displayStack;
			}

			if (!disguisedAs.isEmpty()) {
				//@formatter:off
				probeInfo.horizontal()
				.item(disguisedAs)
				.vertical()
				.itemLabel(disguisedAs)
				.mcText(Component.literal(ModList.get().getModContainerById(Utils.getRegistryName(disguisedAs.getItem()).getNamespace()).get().getModInfo().getDisplayName()).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
				return true;
				//@formatter:on
			}

			return false;
		});
		theOneProbe.registerProvider(new IProbeInfoProvider() {
			@Override
			public ResourceLocation getID() {
				return new ResourceLocation(SecurityCraft.MODID, SecurityCraft.MODID);
			}

			@Override
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level, BlockState state, IProbeHitData data) {
				BlockPos pos = data.getPos();

				addDisguisedOwnerModuleNameInfo(level, pos, state, state.getBlock(), level.getBlockEntity(pos), player, probeInfo::mcText, $ -> true);
			}
		});
		theOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player player, Level level, Entity entity, IProbeHitEntityData data) {
				addEntityInfo(entity, player, probeInfo::mcText, $ -> true);
			}
		});
		return null;
	}
}