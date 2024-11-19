package net.geforcemods.securitycraft.compat.ftbutilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.TeamHandler;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class FTBUtilitiesCompat implements TeamHandler {
	@Override
	public boolean areOnSameTeam(Owner owner1, Owner owner2) {
		try {
			return FTBLibAPI.arePlayersInSameTeam(UUID.fromString(owner1.getUUID()), UUID.fromString(owner2.getUUID()));
		}
		catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public TeamRepresentation getTeamRepresentation(Owner owner) {
		if (Universe.loaded()) {
			try {
				ForgePlayer player = Universe.get().getPlayer(UUID.fromString(owner.getUUID()));

				if (player != null && player.team != null && player.team.getMembers().size() > 1)
					return new TeamRepresentation(player.team.getTitle().getFormattedText(), player.team.getColor().getTextFormatting());
			}
			catch (IllegalArgumentException e) {}
		}

		return null;
	}

	@Override
	public Collection<EntityPlayerMP> getOnlinePlayersFromOwner(MinecraftServer server, Owner owner) {
		try {
			ForgePlayer player = Universe.get().getPlayer(UUID.fromString(owner.getUUID()));

			if (player != null && player.team != null)
				return player.team.getOnlineMembers();
		}
		catch (IllegalArgumentException e) {}

		return new ArrayList<>();
	}
}
