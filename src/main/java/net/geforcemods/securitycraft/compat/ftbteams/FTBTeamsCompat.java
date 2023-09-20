package net.geforcemods.securitycraft.compat.ftbteams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.Team;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.server.level.ServerPlayer;

public class FTBTeamsCompat {
	private FTBTeamsCompat() {}

	public static boolean areOnSameTeam(Owner owner1, Owner owner2) {
		try {
			return FTBTeamsAPI.arePlayersInSameTeam(UUID.fromString(owner1.getUUID()), UUID.fromString(owner2.getUUID()));
		}
		catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static TeamRepresentation getTeamRepresentation(Owner owner) {
		try {
			Team team = FTBTeamsAPI.getPlayerTeam(UUID.fromString(owner.getUUID()));

			if (team != null)
				return new TeamRepresentation(team.getDisplayName(), team.getColor());
		}
		catch (IllegalArgumentException e) {}

		return null;
	}

	public static Collection<ServerPlayer> getOnlinePlayersInTeam(Owner owner) {
		try {
			Team team = FTBTeamsAPI.getPlayerTeam(UUID.fromString(owner.getUUID()));

			if (team != null)
				return team.getOnlineMembers();
		}
		catch (IllegalArgumentException e) {}

		return new ArrayList<>();
	}
}
