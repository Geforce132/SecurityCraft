package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SetBlock{

	private int x, y, z, meta;
	private String blockID;

	public SetBlock(){

	}

	public SetBlock(int x, int y, int z, String id, int meta){
		this.x = x;
		this.y = y;
		this.z = z;
		blockID = id;
		this.meta = meta;
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeString(blockID);
		buf.writeInt(meta);
	}

	public void fromBytes(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		blockID = buf.readString(Integer.MAX_VALUE);
		meta = buf.readInt();
	}

	public static void encode(SetBlock message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SetBlock decode(PacketBuffer packet)
	{
		SetBlock message = new SetBlock();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SetBlock message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			int x = message.x;
			int y = message.y;
			int z = message.z;
			BlockPos pos = BlockUtils.toPos(x, y, z);
			String blockID = message.blockID;
			int meta = message.meta;
			EntityPlayer player = ctx.get().getSender();
			World world = player.world;
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

			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockID));
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
				world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).get(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), true);
		});
		ctx.get().setPacketHandled(true);
	}

}
