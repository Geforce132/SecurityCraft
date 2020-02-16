package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.client.ClearLoggerClient;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

public class UsernameLoggerTileEntity extends DisguisableTileEntity implements INamedContainerProvider {

	public String[] players = new String[100];
	public String[] uuids = new String[100];
	public long[] timestamps = new long[100];

	public UsernameLoggerTileEntity()
	{
		super(SCContent.teTypeUsernameLogger);
	}

	@Override
	public boolean attackEntity(Entity entity) {
		if (!world.isRemote && entity instanceof PlayerEntity) {
			addPlayer((PlayerEntity)entity);
			sendChangeToClient(false);
		}

		return true;
	}

	@Override
	public boolean canAttack() {
		return world.getRedstonePowerFromNeighbors(pos) > 0;
	}

	public void logPlayers(){
		double range = ConfigHandler.CONFIG.usernameLoggerSearchRadius.get();

		AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(range, range, range);
		List<?> entities = world.getEntitiesWithinAABB(PlayerEntity.class, area);
		Iterator<?> iterator = entities.iterator();

		while(iterator.hasNext())
			addPlayer((PlayerEntity)iterator.next());

		sendChangeToClient(false);
	}

	private void addPlayer(PlayerEntity player) {
		long timestamp = System.currentTimeMillis();

		if(!getOwner().isOwner(player) && !hasPlayerName(player.getName().getFormattedText(), timestamp))
		{
			for(int i = 0; i < players.length; i++)
			{
				if(players[i] == "" || players[i] == null){
					players[i] = player.getName().getFormattedText();
					uuids[i] = player.getGameProfile().getId().toString();
					timestamps[i] = timestamp;
					break;
				}
			}
		}
	}

	private boolean hasPlayerName(String username, long timestamp) {
		for(int i = 0; i < players.length; i++)
		{
			if(players[i] != null && players[i].equals(username) && (timestamps[i] + 1000L) > timestamp) //was within the last second that the same player was last added
				return true;
		}

		return false;
	}

	@Override
	public CompoundNBT write(CompoundNBT tag){
		super.write(tag);

		for(int i = 0; i < players.length; i++)
		{
			tag.putString("player" + i, players[i] == null ? "" : players[i]);
			tag.putString("uuid" + i, uuids[i] == null ? "" : uuids[i]);
			tag.putLong("timestamp" + i, timestamps[i]);
		}

		return tag;
	}

	@Override
	public void read(CompoundNBT tag){
		super.read(tag);

		for(int i = 0; i < players.length; i++)
		{
			if(tag.contains("player" + i))
				players[i] = tag.getString("player" + i);

			if(tag.contains("uuid" + i))
				uuids[i] = tag.getString("uuid" + i);

			if(tag.contains("timestamp" + i))
				timestamps[i] = tag.getLong("timestamp" + i);
		}
	}

	public void sendChangeToClient(boolean clear){
		if(!clear)
		{
			for(int i = 0; i < players.length; i++)
			{
				if(players[i] != null)
					SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new UpdateLogger(pos.getX(), pos.getY(), pos.getZ(), i, players[i], uuids[i], timestamps[i]));
			}
		}
		else
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new ClearLoggerClient(pos));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new GenericTEContainer(SCContent.cTypeUsernameLogger, windowId, world, pos);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(SCContent.usernameLogger.getTranslationKey());
	}
}
