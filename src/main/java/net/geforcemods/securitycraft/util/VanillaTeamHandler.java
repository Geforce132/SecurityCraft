package net.geforcemods.securitycraft.util;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

public class VanillaTeamHandler implements TeamHandler {
	@Override
	public boolean areOnSameTeam(Owner owner1, Owner owner2) {
		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner1.getName());

		return team != null && team.getPlayers().contains(owner2.getName());
	}

	@Override
	public TeamRepresentation getTeamRepresentation(Owner owner) {
		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

		if (team != null && team.getPlayers().size() > 1) {
			Integer color = team.getColor().getColor();

			return new TeamRepresentation(team.getDisplayName().getString(), color == null ? TextFormatting.GRAY.getColor() : color);
		}

		return null;
	}

	@Override
	public Collection<ServerPlayerEntity> getOnlinePlayersFromOwner(MinecraftServer server, Owner owner) {
		Collection<ServerPlayerEntity> onlinePlayers = null;
		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

		if (team != null)
			onlinePlayers = team.getPlayers().stream().map(server.getPlayerList()::getPlayerByName).filter(Objects::nonNull).collect(Collectors.toList());

		if (onlinePlayers == null || onlinePlayers.isEmpty())
			return PlayerUtils.getPlayerListFromOwner(owner);

		return onlinePlayers;
	}
}
