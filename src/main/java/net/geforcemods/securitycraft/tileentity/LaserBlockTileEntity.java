package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.LinkableTileEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fml.network.PacketDistributor;

public class LaserBlockTileEntity extends LinkableTileEntity {

	private BooleanOption enabledOption = new BooleanOption("enabled", true) {
		@Override
		public void toggle() {
			setValue(!get());

			toggleLaser(this);
		}
	};

	public LaserBlockTileEntity()
	{
		super(SCContent.teTypeLaserBlock);
	}

	private void toggleLaser(BooleanOption option) {
		if(option.get())
			((LaserBlock)getBlockState().getBlock()).setLaser(world, pos);
		else
			LaserBlock.destroyAdjacentLasers(world, pos);
	}

	@Override
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<LinkableTileEntity> excludedTEs) {
		if(action == LinkedAction.OPTION_CHANGED) {
			Option<?> option = (Option<?>) parameters[0];
			enabledOption.copy(option);
			toggleLaser((BooleanOption) option);

			excludedTEs.add(this);
			createLinkedBlockAction(LinkedAction.OPTION_CHANGED, new Option[]{ option }, excludedTEs);
		}
		else if(action == LinkedAction.MODULE_INSERTED) {
			ItemStack module = (ItemStack) parameters[0];

			insertModule(module);

			excludedTEs.add(this);
			createLinkedBlockAction(LinkedAction.MODULE_INSERTED, parameters, excludedTEs);
		}
		else if(action == LinkedAction.MODULE_REMOVED) {
			ModuleType module = (ModuleType) parameters[1];

			removeModule(module);

			excludedTEs.add(this);
			createLinkedBlockAction(LinkedAction.MODULE_REMOVED, parameters, excludedTEs);
		}
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
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.HARMING, ModuleType.ALLOWLIST, ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ enabledOption };
	}

	@Override
	public IModelData getModelData()
	{
		return new ModelDataMap.Builder().withInitial(DisguisableDynamicBakedModel.DISGUISED_BLOCK_RL, getBlockState().getBlock().getRegistryName()).build();
	}

	public boolean isEnabled()
	{
		return enabledOption.get();
	}
}
