package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;

public class DisguisableTileEntity extends CustomizableTileEntity
{
	public DisguisableTileEntity(TileEntityType<?> type)
	{
		super(type);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		super.onModuleInserted(stack, module);

		if(!world.isRemote && module == ModuleType.DISGUISE)
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(pos, true, stack));
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(!world.isRemote && module == ModuleType.DISGUISE)
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(pos, false, stack));
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public IModelData getModelData()
	{
		return new ModelDataMap.Builder().withInitial(DisguisableDynamicBakedModel.DISGUISED_BLOCK_RL, getBlockState().getBlock().getRegistryName()).build();
	}

	@Override
	public void onLoad()
	{
		super.onLoad();

		if(world != null && world.isRemote)
			refreshModel();
	}

	public void refreshModel()
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			ModelDataManager.requestModelDataRefresh(this);
			Minecraft.getInstance().worldRenderer.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
		});
	}
}
