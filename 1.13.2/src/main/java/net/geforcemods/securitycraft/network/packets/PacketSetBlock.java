package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketSetBlock implements IMessage{

	private int x, y, z, meta;
	private String blockID;

	public PacketSetBlock(){

	}

	public PacketSetBlock(int x, int y, int z, String id, int meta){
		this.x = x;
		this.y = y;
		this.z = z;
		blockID = id;
		this.meta = meta;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, blockID);
		buf.writeInt(meta);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		blockID = ByteBufUtils.readUTF8String(buf);
		meta = buf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetBlock, IMessage> {
		@Override
		public IMessage onMessage(PacketSetBlock message, MessageContext context) {
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), () -> {
				int x = message.x;
				int y = message.y;
				int z = message.z;
				BlockPos pos = BlockUtils.toPos(x, y, z);
				String blockID = message.blockID;
				int meta = message.meta;
				EntityPlayer player = context.getServerHandler().player;
				World world = getWorld(player);
				TileEntity te = world.getTileEntity(pos);
				NonNullList<ItemStack> modules = null;
				NonNullList<ItemStack> inventory = null;
				int[] times = new int[4];
				String password = "";
				Owner owner = null;

				if(te instanceof CustomizableSCTE)
					modules = ((CustomizableSCTE) te).modules;

				if(te instanceof TileEntityKeypadFurnace){
					inventory = ((TileEntityKeypadFurnace) te).furnaceItemStacks;
					times[0] = ((TileEntityKeypadFurnace) te).furnaceBurnTime;
					times[1] = ((TileEntityKeypadFurnace) te).currentItemBurnTime;
					times[2] = ((TileEntityKeypadFurnace) te).cookTime;
					times[3] = ((TileEntityKeypadFurnace) te).totalCookTime;
				}

				if(te instanceof TileEntityOwnable && ((TileEntityOwnable) te).getOwner() != null)
					owner = ((TileEntityOwnable) te).getOwner();

				if(te instanceof TileEntityKeypadChest && ((TileEntityKeypadChest) te).getPassword() != null)
					password = ((TileEntityKeypadChest) te).getPassword();

				Block block = Block.REGISTRY.getObject(new ResourceLocation(blockID));
				world.setBlockState(pos, meta >= 0 ? block.getStateFromMeta(meta) : block.getStateFromMeta(0));

				if(modules != null)
					((CustomizableSCTE) te).modules = modules;

				if(inventory != null && te instanceof TileEntityKeypadFurnace){
					((TileEntityKeypadFurnace) te).furnaceItemStacks = inventory;
					((TileEntityKeypadFurnace) te).furnaceBurnTime = times[0];
					((TileEntityKeypadFurnace) te).currentItemBurnTime = times[1];
					((TileEntityKeypadFurnace) te).cookTime = times[2];
					((TileEntityKeypadFurnace) te).totalCookTime = times[3];
				}

				if(owner != null)
					((TileEntityOwnable) te).getOwner().set(owner.getUUID(), owner.getName());

				if(!password.isEmpty() && te instanceof TileEntityKeypadChest)
					((TileEntityKeypadChest) te).setPassword(password);

				if(te instanceof TileEntitySecurityCamera)
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), true);
			});
			return null;
		}
	}

}
