package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.ClearLoggerClient;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.BlockState;
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

	private IntOption searchRadius = new IntOption(this::getBlockPos, "searchRadius", 3, 1, 20, 1, true);
	public String[] players = new String[100];
	public String[] uuids = new String[100];
	public long[] timestamps = new long[100];

	public UsernameLoggerTileEntity()
	{
		super(SCContent.teTypeUsernameLogger);
	}

	@Override
	public boolean attackEntity(Entity entity) {
		if (!level.isClientSide && entity instanceof PlayerEntity) {
			addPlayer((PlayerEntity)entity);
			sendChangeToClient(false);
		}

		return true;
	}

	@Override
	public boolean canAttack() {
		return level.getBestNeighborSignal(worldPosition) > 0;
	}

	public void logPlayers(){
		int range = searchRadius.get();

		AxisAlignedBB area = new AxisAlignedBB(worldPosition).inflate(range);
		List<?> entities = level.getEntitiesOfClass(PlayerEntity.class, area);
		Iterator<?> iterator = entities.iterator();

		while(iterator.hasNext())
			addPlayer((PlayerEntity)iterator.next());

		sendChangeToClient(false);
	}

	private void addPlayer(PlayerEntity player) {
		String playerName = player.getName().getString();
		long timestamp = System.currentTimeMillis();

		if(!getOwner().isOwner(player) && !EntityUtils.isInvisible(player) && !hasPlayerName(playerName, timestamp))
		{
			//ignore players on the allowlist
			if(ModuleUtils.isAllowed(this, player))
				return;

			for(int i = 0; i < players.length; i++)
			{
				if(players[i] == null || players[i].equals("")){
					players[i] = player.getName().getString();
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
	public CompoundNBT save(CompoundNBT tag){
		super.save(tag);

		for(int i = 0; i < players.length; i++)
		{
			tag.putString("player" + i, players[i] == null ? "" : players[i]);
			tag.putString("uuid" + i, uuids[i] == null ? "" : uuids[i]);
			tag.putLong("timestamp" + i, timestamps[i]);
		}

		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag){
		super.load(state, tag);

		for(int i = 0; i < players.length; i++)
		{
			players[i] = tag.getString("player" + i);
			uuids[i] = tag.getString("uuid" + i);
			timestamps[i] = tag.getLong("timestamp" + i);
		}
	}

	public void sendChangeToClient(boolean clear){
		if(!clear)
		{
			for(int i = 0; i < players.length; i++)
			{
				if(players[i] != null)
					SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new UpdateLogger(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), i, players[i], uuids[i], timestamps[i]));
			}
		}
		else
			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new ClearLoggerClient(worldPosition));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new GenericTEContainer(SCContent.cTypeUsernameLogger, windowId, level, worldPosition);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(SCContent.USERNAME_LOGGER.get().getDescriptionId());
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.DISGUISE, ModuleType.ALLOWLIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{searchRadius};
	}
}
