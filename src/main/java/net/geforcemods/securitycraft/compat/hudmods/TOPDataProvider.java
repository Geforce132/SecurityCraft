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
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

public class TOPDataProvider extends HudModHandler implements Function<ITheOneProbe, Void> {
	@Nullable
	@Override
	public Void apply(ITheOneProbe theOneProbe) {
		theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, level, state, data) -> {
			ItemStack disguisedAs = ItemStack.EMPTY;

			if (state.getBlock() instanceof DisguisableBlock)
				disguisedAs = ((DisguisableBlock) state.getBlock()).getDisguisedStack(level, data.getPos());
			else if (state.getBlock() instanceof IOverlayDisplay) {
				ItemStack displayStack = ((IOverlayDisplay) state.getBlock()).getDisplayStack(level, state, data.getPos());

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
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World level, BlockState state, IProbeHitData data) {
				BlockPos pos = data.getPos();

				addOwnerModuleNameInfo(level, pos, state, state.getBlock(), level.getBlockEntity(pos), player, probeInfo::mcText, $ -> true);
			}
		});
		theOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo probeInfo, PlayerEntity player, World level, Entity entity, IProbeHitEntityData data) {
				addEntityInfo(entity, player, probeInfo::mcText, $ -> true);
			}
		});
		return null;
	}
}