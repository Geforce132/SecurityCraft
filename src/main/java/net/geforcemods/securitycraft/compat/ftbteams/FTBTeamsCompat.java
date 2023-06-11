package net.geforcemods.securitycraft.compat.ftbteams;

import java.util.UUID;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.world.scores.Team;

public class FTBTeamsCompat {
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
}
