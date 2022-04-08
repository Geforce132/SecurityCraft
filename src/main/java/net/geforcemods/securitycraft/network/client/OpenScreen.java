package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class OpenScreen {
	private DataType dataType;
	private BlockPos pos;

	public OpenScreen() {}

	public OpenScreen(DataType dataType, BlockPos pos) {
		this.dataType = dataType;
		this.pos = pos;
	}

	public static void encode(OpenScreen message, FriendlyByteBuf buf) {
		buf.writeEnum(message.dataType);
		buf.writeBlockPos(message.pos);
	}

	public static OpenScreen decode(FriendlyByteBuf buf) {
		DataType dataType = buf.readEnum(DataType.class);
		BlockPos pos = buf.readBlockPos();

		return new OpenScreen(dataType, pos);
	}

	public static void onMessage(OpenScreen message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			switch(message.dataType) {
				case CHECK_PASSWORD:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IPasswordProtected be)
						ClientHandler.displayCheckPasswordGui((BlockEntity) be);

					break;
				case IMS:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IMSBlockEntity ims)
						ClientHandler.displayIMSGui(ims);

					break;
				case SET_PASSWORD:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IPasswordProtected be)
						ClientHandler.displaySetPasswordGui((BlockEntity) be);

					break;
				case SONIC_SECURITY_SYSTEM:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof SonicSecuritySystemBlockEntity sss)
						ClientHandler.displaySonicSecuritySystemGui(sss);

					break;
				case TROPHY_SYSTEM:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof TrophySystemBlockEntity trophySystem)
						ClientHandler.displayTrophySystemGui(trophySystem);

					break;
				case UNIVERSAL_KEY_CHANGER:
					if (Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof IPasswordProtected passwordProtected)
						ClientHandler.displayUniversalKeyChangerGui((BlockEntity) passwordProtected);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public enum DataType {
		CHECK_PASSWORD,
		IMS,
		SET_PASSWORD,
		SONIC_SECURITY_SYSTEM,
		TROPHY_SYSTEM,
		UNIVERSAL_KEY_CHANGER;
	}
}
