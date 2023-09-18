package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.compat.ftbteams.FTBTeamsCompat;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class TeamUtils {
	private TeamUtils() {}

	/**
	 * Checks if two given players are on the same scoreboard/FTB Teams team
	 *
	 * @param owner1 The first owner object representing a player
	 * @param owner2 The second owner object representing a player
	 * @return true if both players are on the same team, false otherwise
	 */
	public static boolean areOnSameTeam(Owner owner1, Owner owner2) {
		if (owner1.equals(owner2))
			return true;

		if (ModList.get().isLoaded("ftbteams"))
			return FTBTeamsCompat.areOnSameTeam(owner1, owner2);

		ScorePlayerTeam team = getVanillaTeamFromPlayer(owner1.getName());

		return team != null && team.getPlayers().contains(owner2.getName());
	}

	/**
	 * Gets the scoreboard team the given player is on
	 *
	 * @param playerName The player whose team to get
	 * @return The team the given player is on. null if the player is not part of a team
	 */
	public static ScorePlayerTeam getVanillaTeamFromPlayer(String playerName) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

		if (server != null)
			return server.getScoreboard().getPlayersTeam(playerName);
		else
			return ClientHandler.getClientPlayer().getScoreboard().getPlayersTeam(playerName);
	}

	/**
	 * Gets a representation containing the team's name and color
	 *
	 * @param owner The owner whose team to get
	 * @return The {@link TeamRepresentation} of the owner's team, {@code null} if they are not part of a team
	 */
	public static TeamRepresentation getTeamRepresentation(Owner owner) {
		if (ModList.get().isLoaded("ftbteams"))
			return FTBTeamsCompat.getTeamRepresentation(owner);

		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

		if (team != null) {
			Integer color = team.getColor().getColor();

			return new TeamRepresentation(team.getDisplayName().getString(), color == null ? TextFormatting.GRAY.getColor() : color);
		}

		return null;
	}

	/**
	 * Gets all players that are in a team and currently online
	 *
	 * @param server The server
	 * @param owner The owner whose team to get the players of
	 * @return A list containing all online players who are in the same team as the owner
	 */
	public static Collection<ServerPlayerEntity> getOnlinePlayersInTeam(MinecraftServer server, Owner owner) {
		if (ModList.get().isLoaded("ftbteams"))
			return FTBTeamsCompat.getOnlinePlayersInTeam(owner);

		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

		if (team != null)
			return team.getPlayers().stream().map(server.getPlayerList()::getPlayerByName).filter(Objects::nonNull).collect(Collectors.toList());

		return new ArrayList<>();
	}

	public static class TeamRepresentation {
		private final String name;
		private final int color;

		public TeamRepresentation(String name, int color) {
			this.name = name;
			this.color = color;
		}

		public String name() {
			return name;
		}

		public int color() {
			return color;
		}
	}
}
