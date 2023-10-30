package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;

public class RefreshDisguisableModel {
	private BlockPos pos;
	private boolean insert;
	private ItemStack stack;
	private boolean toggled;

	public RefreshDisguisableModel() {}

	public RefreshDisguisableModel(BlockPos pos, boolean insert, ItemStack stack, boolean toggled) {
		this.pos = pos;
		this.insert = insert;
		this.stack = stack;
		this.toggled = toggled;
	}

	public RefreshDisguisableModel(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		insert = buf.readBoolean();
		stack = buf.readItem();
		toggled = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(insert);
		buf.writeItem(stack);
		buf.writeBoolean(toggled);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		IModuleInventory be = (IModuleInventory) Minecraft.getInstance().level.getBlockEntity(pos);

		if (be != null) {
			if (insert)
				be.insertModule(stack, toggled);
			else
				be.removeModule(ModuleType.DISGUISE, toggled);

			ClientHandler.refreshModelData(be.getBlockEntity());
		}
	}
}
