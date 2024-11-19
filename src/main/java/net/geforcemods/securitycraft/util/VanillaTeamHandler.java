package net.geforcemods.securitycraft.util;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

public class VanillaTeamHandler implements TeamHandler {
	@Override
	public boolean areOnSameTeam(Owner owner1, Owner owner2) {
		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner1.getName());

		return team != null && team.getMembershipCollection().contains(owner2.getName());
	}

	@Override
	public TeamRepresentation getTeamRepresentation(Owner owner) {
		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

		if (team != null && team.getMembershipCollection().size() > 1) {
			TextFormatting color = team.getColor();

			return new TeamRepresentation(team.getDisplayName(), !color.isColor() ? TextFormatting.GRAY : color);
		}

		return null;
	}

	@Override
	public Collection<EntityPlayerMP> getOnlinePlayersFromOwner(MinecraftServer server, Owner owner) {
		Collection<EntityPlayerMP> onlinePlayers = null;
		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

		if (team != null)
			onlinePlayers = team.getMembershipCollection().stream().map(server.getPlayerList()::getPlayerByUsername).filter(Objects::nonNull).collect(Collectors.toList());

		if (onlinePlayers == null || onlinePlayers.isEmpty())
			return PlayerUtils.getPlayerListFromOwner(owner);

		return onlinePlayers;
	}
}
