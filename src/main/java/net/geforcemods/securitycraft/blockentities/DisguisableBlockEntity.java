package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.RefreshDiguisedModel;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class DisguisableBlockEntity extends CustomizableBlockEntity {
	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onInsertDisguiseModule(this, stack, toggled);
	}

	public static void onInsertDisguiseModule(TileEntity te, ItemStack stack, boolean toggled) {
		if (te.hasWorld()) {
			World world = te.getWorld();
			BlockPos pos = te.getPos();

			if (!world.isRemote)
				SecurityCraft.network.sendToAllTracking(new RefreshDiguisedModel(pos, true, stack, toggled), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
			else {
				IBlockState state = world.getBlockState(pos);

				BlockEntityRenderDelegate.putDisguisedTeRenderer(te, stack);

				if (state.getLightValue(world, pos) > 0)
					world.checkLight(pos);
			}
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onRemoveDisguiseModule(this, stack, toggled);
	}

	public static void onRemoveDisguiseModule(TileEntity te, ItemStack stack, boolean toggled) {
		if (te.hasWorld()) {
			World world = te.getWorld();
			BlockPos pos = te.getPos();

			if (!world.isRemote)
				SecurityCraft.network.sendToAllTracking(new RefreshDiguisedModel(pos, false, stack, toggled), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
			else {
				IBlockState disguisedState = ((IDisguisable) te.getBlockType()).getDisguisedBlockStateFromStack(null, null, stack);

				BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(te);

				if (disguisedState != null && disguisedState.getLightValue(world, pos) > 0)
					world.checkLight(pos);
			}
		}
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		onLoad(this);
	}

	public static <T extends TileEntity & IModuleInventory> void onLoad(T te) {
		if (te.hasWorld() && te.getWorld().isRemote) {
			BlockEntityRenderDelegate.putDisguisedTeRenderer(te, te.getModule(ModuleType.DISGUISE));
			te.getWorld().markBlockRangeForRenderUpdate(te.getPos(), te.getPos());
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		onInvalidate(this);
	}

	public static void onInvalidate(TileEntity te) {
		if (te.hasWorld() && te.getWorld().isRemote)
			BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(te);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[0];
	}
}
