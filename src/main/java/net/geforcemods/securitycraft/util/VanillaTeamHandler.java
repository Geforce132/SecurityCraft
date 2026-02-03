package net.geforcemods.securitycraft.util;

import java.util.Collection;
import java.util.Objects;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

public class VanillaTeamHandler implements TeamHandler {
	@Override
	public boolean areOnSameTeam(Owner owner1, Owner owner2) {
		PlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner1.getName());

		return team != null && team.getPlayers().contains(owner2.getName());
	}

	@Override
	public TeamRepresentation getTeamRepresentation(Owner owner) {
		PlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

		if (team != null && team.getPlayers().size() > 1) {
			Integer color = team.getColor().getColor();

			return new TeamRepresentation(team.getDisplayName().getString(), color == null ? ChatFormatting.GRAY.getColor() : color);
		}

		return null;
	}

	@Override
	public Collection<ServerPlayer> getOnlinePlayersFromOwner(MinecraftServer server, Owner owner) {
		Collection<ServerPlayer> onlinePlayers = null;
		PlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

		if (team != null)
			onlinePlayers = team.getPlayers().stream().map(server.getPlayerList()::getPlayerByName).filter(Objects::nonNull).toList();

		if (onlinePlayers == null || onlinePlayers.isEmpty())
			return PlayerUtils.getPlayerListFromOwner(owner);

		return onlinePlayers;
	}
}
