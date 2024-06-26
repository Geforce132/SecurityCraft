package net.geforcemods.securitycraft.blockentities;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fml.network.PacketDistributor;

public class DisguisableBlockEntity extends CustomizableBlockEntity {
	public static final Supplier<IModelData> DEFAULT_MODEL_DATA = () -> new ModelDataMap.Builder().withInitial(DisguisableDynamicBakedModel.DISGUISED_STATE, Blocks.AIR.defaultBlockState()).build();

	public DisguisableBlockEntity() {
		super(SCContent.DISGUISABLE_BLOCK_ENTITY.get());
	}

	public DisguisableBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onDisguiseModuleInserted(this, stack, toggled);
	}

	public static void onDisguiseModuleInserted(TileEntity be, ItemStack stack, boolean toggled) {
		BlockState state = be.getBlockState();
		World level = be.getLevel();
		BlockPos worldPosition = be.getBlockPos();

		if (!level.isClientSide) {
			SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, true, stack, toggled));

			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
				level.getLiquidTicks().scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
				level.updateNeighborsAt(worldPosition, state.getBlock());
			}
		}
		else {
			ClientHandler.putDisguisedBeRenderer(be, stack);

			if (state.getLightValue(level, worldPosition) > 0)
				level.getChunkSource().getLightEngine().checkBlock(worldPosition);
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onDisguiseModuleRemoved(this, stack, toggled);
	}

	public static void onDisguiseModuleRemoved(TileEntity be, ItemStack stack, boolean toggled) {
		World level = be.getLevel();
		BlockPos worldPosition = be.getBlockPos();

		if (!level.isClientSide) {
			BlockState state = be.getBlockState();

			SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, false, stack, toggled));

			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
				level.getLiquidTicks().scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
				level.updateNeighborsAt(worldPosition, state.getBlock());
			}
		}
		else {
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(be);
			IDisguisable.getDisguisedBlockStateFromStack(stack).ifPresent(disguisedState -> {
				if (disguisedState.getLightValue(level, worldPosition) > 0)
					level.getChunkSource().getLightEngine().checkBlock(worldPosition);
			});
		}
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		onHandleUpdateTag(this);
	}

	public static <T extends TileEntity & IModuleInventory> void onHandleUpdateTag(T be) {
		World level = be.getLevel();

		if (level != null && level.isClientSide) {
			ItemStack stack = be.getModule(ModuleType.DISGUISE);

			if (!stack.isEmpty())
				ClientHandler.putDisguisedBeRenderer(be, stack);
			else
				ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(be);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
	}

	public static void onSetRemoved(TileEntity be) {
		if (be.getLevel().isClientSide)
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(be);
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

	@Override
	public IModelData getModelData() {
		return DEFAULT_MODEL_DATA.get();
	}
}
