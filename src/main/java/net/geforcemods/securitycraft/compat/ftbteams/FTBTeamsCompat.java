package net.geforcemods.securitycraft.compat.ftbteams;

import java.util.Optional;
import java.util.UUID;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import net.geforcemods.securitycraft.api.Owner;

public class FTBTeamsCompat {
	public static boolean areOnSameTeam(Owner owner1, Owner owner2) {
		try {
			return FTBTeamsAPI.api().getManager().arePlayersInSameTeam(UUID.fromString(owner1.getUUID()), UUID.fromString(owner2.getUUID()));
		}
		catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static TeamRepresentation getTeamRepresentation(Owner owner) {
		try {
			Optional<Team> optional = FTBTeamsAPI.api().getManager().getTeamForPlayerID(UUID.fromString(owner.getUUID()));

			if (optional.isPresent()) {
				Team team = optional.get();

				return new TeamRepresentation(team.getProperty(TeamProperties.DISPLAY_NAME), team.getProperty(TeamProperties.COLOR).toStyle().getColor().getValue());
			}
		}
		catch (IllegalArgumentException e) {}

		return null;
	}
}
