package net.geforcemods.securitycraft.blockentities;

import java.util.List;
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
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.network.PacketDistributor;

public class DisguisableBlockEntity extends CustomizableBlockEntity {
	public static final Supplier<IModelData> DEFAULT_MODEL_DATA = () -> new ModelDataMap.Builder().withInitial(DisguisableDynamicBakedModel.DISGUISED_STATE, Blocks.AIR.defaultBlockState()).build();

	public DisguisableBlockEntity(BlockPos pos, BlockState state) {
		this(SCContent.DISGUISABLE_BLOCK_ENTITY.get(), pos, state);
	}

	public DisguisableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onDisguiseModuleInserted(this, stack, toggled);
	}

	public static void onDisguiseModuleInserted(BlockEntity be, ItemStack stack, boolean toggled) {
		BlockState state = be.getBlockState();
		Level level = be.getLevel();
		BlockPos worldPosition = be.getBlockPos();

		if (!level.isClientSide) {
			SecurityCraft.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, true, stack, toggled));

			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
				level.scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
				level.updateNeighborsAt(worldPosition, state.getBlock());
			}
		}
		else {
			ClientHandler.putDisguisedBeRenderer(be, stack);

			if (state.getLightEmission(level, worldPosition) > 0)
				level.getChunkSource().getLightEngine().checkBlock(worldPosition);
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			onDisguiseModuleRemoved(this, stack, toggled);
	}

	public static void onDisguiseModuleRemoved(BlockEntity be, ItemStack stack, boolean toggled) {
		Level level = be.getLevel();
		BlockPos worldPosition = be.getBlockPos();

		if (!level.isClientSide) {
			BlockState state = be.getBlockState();

			SecurityCraft.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, false, stack, toggled));

			if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
				level.scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
				level.updateNeighborsAt(worldPosition, state.getBlock());
			}
		}
		else {
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(be);
			IDisguisable.getDisguisedBlockStateFromStack(stack).ifPresent(disguisedState -> {
				if (disguisedState.getLightEmission(level, worldPosition) > 0)
					level.getChunkSource().getLightEngine().checkBlock(worldPosition);
			});
		}
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		super.onDataPacket(net, packet);
		onHandleUpdateTag(this);
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (level != null && level.isClientSide && level.getBlockEntity(worldPosition) != this) //On the client side, onLoad is usually only called without this BE being added to the level, which breaks model data update requests.
			level.addFreshBlockEntities(List.of(this)); //By marking this BE as a fresh block entity in such cases, the client will call onLoad again on the first BE tick, on which it is registered properly.
		else
			onHandleUpdateTag(this);
	}

	public static <T extends BlockEntity & IModuleInventory> void onHandleUpdateTag(T be) {
		Level level = be.getLevel();

		if (level != null && level.isClientSide) {
			ItemStack stack = be.getModule(ModuleType.DISGUISE);

			if (!stack.isEmpty())
				ClientHandler.putDisguisedBeRenderer(be, stack);
			else
				ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(be);

			ClientHandler.refreshModelData(be);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		onSetRemoved(this);
	}

	public static void onSetRemoved(BlockEntity be) {
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
