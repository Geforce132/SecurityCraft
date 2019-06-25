package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerTEGeneric;
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

public class TileEntityLogger extends TileEntityOwnable implements INamedContainerProvider {

	public String[] players = new String[100];

	public TileEntityLogger()
	{
		super(SCContent.teTypeUsernameLogger);
	}

	@Override
	public boolean attackEntity(Entity entity) {
		if (!world.isRemote) {
			addPlayerName(((PlayerEntity) entity).getName().getFormattedText());
			sendChangeToClient();
		}

		return true;
	}

	@Override
	public boolean canAttack() {
		return world.getRedstonePowerFromNeighbors(pos) > 0;
	}

	public void logPlayers(){
		double range = CommonConfig.CONFIG.usernameLoggerSearchRadius.get();

		AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(range, range, range);
		List<?> entities = world.getEntitiesWithinAABB(PlayerEntity.class, area);
		Iterator<?> iterator = entities.iterator();

		while(iterator.hasNext())
			addPlayerName(((PlayerEntity)iterator.next()).getName().getFormattedText());

		sendChangeToClient();
	}

	private void addPlayerName(String username) {
		if(!hasPlayerName(username))
			for(int i = 0; i < players.length; i++)
				if(players[i] == "" || players[i] == null){
					players[i] = username;
					break;
				}
				else
					continue;
	}

	private boolean hasPlayerName(String username) {
		for(int i = 0; i < players.length; i++)
			if(players[i] == username)
				return true;
			else
				continue;

		return false;
	}

	@Override
	public CompoundNBT write(CompoundNBT tag){
		super.write(tag);

		for(int i = 0; i < players.length; i++)
			if(players[i] != null)
				tag.putString("player" + i, players[i]);

		return tag;
	}

	@Override
	public void read(CompoundNBT tag){
		super.read(tag);

		for(int i = 0; i < players.length; i++)
			if (tag.contains("player" + i))
				players[i] = tag.getString("player" + i);
	}

	public void sendChangeToClient(){
		for(int i = 0; i < players.length; i++)
			if(players[i] != null)
				SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new UpdateLogger(pos.getX(), pos.getY(), pos.getZ(), i, players[i]));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new ContainerTEGeneric(SCContent.cTypeUsernameLogger, windowId, world, pos);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(SCContent.usernameLogger.getTranslationKey());
	}
}
