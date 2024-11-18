package net.geforcemods.securitycraft.compat.ftbteams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.Team;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.TeamHandler;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class FTBTeamsCompat implements TeamHandler {
	@Override
	public boolean areOnSameTeam(Owner owner1, Owner owner2) {
		try {
			return FTBTeamsAPI.arePlayersInSameTeam(UUID.fromString(owner1.getUUID()), UUID.fromString(owner2.getUUID()));
		}
		catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public TeamRepresentation getTeamRepresentation(Owner owner) {
		try {
			Team team = FTBTeamsAPI.getPlayerTeam(UUID.fromString(owner.getUUID()));

			if (team != null && team.getMembers().size() > 1)
				return new TeamRepresentation(team.getDisplayName(), team.getColor());
		}
		catch (IllegalArgumentException e) {}

		return null;
	}

	@Override
	public Collection<ServerPlayerEntity> getOnlinePlayersFromOwner(MinecraftServer server, Owner owner) {
		try {
			Team team = FTBTeamsAPI.getPlayerTeam(UUID.fromString(owner.getUUID()));

			if (team != null)
				return team.getOnlineMembers();
		}
		catch (IllegalArgumentException e) {}

		return new ArrayList<>();
	}
}
