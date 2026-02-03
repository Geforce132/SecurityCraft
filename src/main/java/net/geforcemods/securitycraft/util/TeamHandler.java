package net.geforcemods.securitycraft.util;

import java.util.Collection;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface TeamHandler {
	/**
	 * Checks if two given players are on the same scoreboard/FTB Teams team
	 *
	 * @param owner1 The first owner object representing a player
	 * @param owner2 The second owner object representing a player
	 * @return true if both players are on the same team, false otherwise or if team ownership has been disabled through the
	 *         config
	 */
	public boolean areOnSameTeam(Owner owner1, Owner owner2);

	/**
	 * Gets a representation containing the team's name and color
	 *
	 * @param owner The owner whose team to get
	 * @return The {@link TeamRepresentation} of the owner's team, {@code null} if they are not part of a team or if team
	 *         ownership has been disabled through the config
	 */
	public TeamRepresentation getTeamRepresentation(Owner owner);

	/**
	 * Gets all players that are in the same team as the given owner, and currently online
	 *
	 * @param server The server
	 * @param owner The owner whose team to get the players of
	 * @return A list containing all online players who are in the same team as the owner. If the owner is not in a team or team
	 *         ownership has been disabled through the config, the list will only contain the owning player, if they're online.
	 */
	public Collection<ServerPlayer> getOnlinePlayersFromOwner(MinecraftServer server, Owner owner);
}
