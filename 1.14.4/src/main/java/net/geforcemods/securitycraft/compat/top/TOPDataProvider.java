package net.geforcemods.securitycraft.compat.top;

import java.util.function.Function;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.IBlockDisplayOverride;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

public class TOPDataProvider implements Function<ITheOneProbe, Void>
{
	private final String formatting = TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString();

	@Nullable
	@Override
	public Void apply(ITheOneProbe theOneProbe)
	{
		theOneProbe.registerBlockDisplayOverride(new IBlockDisplayOverride() {
			@Override
			public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data)
			{
				if(blockState.getBlock() instanceof BlockKeypad)
				{
					ItemStack disguisedAs = BlockKeypad.getDisguisedStack(world, data.getPos());

					probeInfo.horizontal()
					.item(disguisedAs)
					.vertical()
					.itemLabel(disguisedAs)
					.text(formatting + ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName());
					return true;
				}

				return false;
			}
		});
		theOneProbe.registerProvider(new IProbeInfoProvider() {
			@Override
			public String getID()
			{
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data)
			{
				Block block = blockState.getBlock();

				if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(world, blockState, data.getPos()))
					return;

				TileEntity te = world.getTileEntity(data.getPos());

				if(te instanceof IOwnable)
					probeInfo.vertical().text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) te).getOwner().getName());

				if(te instanceof CustomizableSCTE && ((CustomizableSCTE)te).getOwner().isOwner(player))
				{
					if(!((CustomizableSCTE)te).getModules().isEmpty())
					{
						probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:equipped"));

						for(EnumCustomModules module : ((CustomizableSCTE) te).getModules())
							probeInfo.text(TextFormatting.GRAY + "- " + module.getName());
					}
				}

				if(te instanceof IPasswordProtected && !(te instanceof TileEntityKeycardReader) && ((IOwnable)te).getOwner().isOwner(player))
				{
					String password = ((IPasswordProtected) te).getPassword();

					probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet")));
				}

				if(te instanceof INameable && ((INameable) te).canBeNamed()){
					String name = ((INameable) te).getCustomSCName().toString();

					probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) te).hasCustomSCName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet")));
				}
			}
		});
		return null;
	}
}