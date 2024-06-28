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
import net.geforcemods.securitycraft.blocks.FakeLavaBaseBlock;
import net.geforcemods.securitycraft.blocks.FakeWaterBaseBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class TOPDataProvider extends HudModHandler implements Function<ITheOneProbe, Void> {
	private final String formatting = TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString();

	@Nullable
	@Override
	public Void apply(ITheOneProbe theOneProbe) {
		theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
			boolean edited = false;
			ItemStack item = ItemStack.EMPTY;
			ItemStack itemLabel = ItemStack.EMPTY;
			String labelText = "";
			String text = formatting + "Minecraft";

			//split up so the display override does not work for every block
			if (blockState.getBlock() instanceof IDisguisable) {
				item = ((IDisguisable) blockState.getBlock()).getDisguisedStack(world, data.getPos());
				itemLabel = item;
				text = formatting + Loader.instance().getIndexedModList().get(item.getItem().getRegistryName().getNamespace()).getName();
				edited = true;
			}
			else if (blockState.getBlock() instanceof FakeLavaBaseBlock) {
				item = new ItemStack(Items.LAVA_BUCKET);
				labelText = Utils.localize("tile.lava.name").getFormattedText();
				edited = true;
			}
			else if (blockState.getBlock() instanceof FakeWaterBaseBlock) {
				item = new ItemStack(Items.WATER_BUCKET);
				labelText = Utils.localize("tile.water.name").getFormattedText();
				edited = true;
			}
			else if (blockState.getBlock() instanceof IOverlayDisplay) {
				ItemStack displayStack = ((IOverlayDisplay) blockState.getBlock()).getDisplayStack(world, blockState, data.getPos());

				if (displayStack != null) {
					item = itemLabel = displayStack;
					edited = true;
				}
			}

			if (edited) {
				IProbeInfo info = probeInfo.horizontal().item(item).vertical();

				if (itemLabel.isEmpty())
					info.text(labelText);
				else
					info.itemLabel(itemLabel);

				info.text(text);
				return true;
			}

			return false;
		});
		theOneProbe.registerProvider(new IProbeInfoProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World level, IBlockState state, IProbeHitData data) {
				BlockPos pos = data.getPos();

				addOwnerModuleNameInfo(level, pos, state, state.getBlock(), level.getTileEntity(pos), player, string -> probeInfo.text(TextFormatting.GRAY + string), $ -> true);
			}
		});
		theOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo probeInfo, EntityPlayer player, World level, Entity entity, IProbeHitEntityData data) {
				addEntityInfo(entity, player, string -> probeInfo.text(TextFormatting.GRAY + string), $ -> true);
			}
		});
		return null;
	}
}