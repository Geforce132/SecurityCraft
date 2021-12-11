package net.geforcemods.securitycraft;

import java.util.Map;

import javax.annotation.Nullable;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(ForgeVersion.mcVersion)
public class SecurityCraftLoadingPlugin implements IFMLLoadingPlugin
{
	public SecurityCraftLoadingPlugin()
	{
		MixinBootstrap.init();
		Mixins.addConfiguration(SecurityCraft.MODID + ".mixins.json");
	}

	@Override
	public String[] getASMTransformerClass() { return new String[0]; }

	@Override
	public String getModContainerClass() { return null; }

	@Nullable
	@Override
	public String getSetupClass() { return null; }

	@Override
	public void injectData(Map<String,Object> data) {}

	@Override
	public String getAccessTransformerClass() { return null; }
}