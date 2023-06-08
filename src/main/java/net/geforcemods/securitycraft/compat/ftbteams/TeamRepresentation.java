package net.geforcemods.securitycraft.compat.ftbteams;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.PlayerTeam;

public record TeamRepresentation(String name, int color) {
	public static TeamRepresentation get(Owner owner) {
		//		if (ModList.get().isLoaded("ftbteams"))
		//			return FTBTeamsCompat.getTeamRepresentation(owner);

		PlayerTeam team = PlayerUtils.getPlayersVanillaTeam(owner.getName());

		if (team != null) {
			Integer color = team.getColor().getColor();

			return new TeamRepresentation(team.getDisplayName().getString(), color == null ? ChatFormatting.GRAY.getColor() : color);
		}

		return null;
	}
}
