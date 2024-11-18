package net.geforcemods.securitycraft.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.compat.ftbteams.FTBTeamsCompat;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class TeamUtils {
	private static List<TeamHandler> teamPrecedence;

	private TeamUtils() {}

	/**
	 * @see TeamHandler#areOnSameTeam
	 */
	public static boolean areOnSameTeam(Owner owner1, Owner owner2) {
		if (owner1.equals(owner2))
			return true;
		else if (!ConfigHandler.SERVER.enableTeamOwnership.get())
			return false;

		for (TeamHandler teamHandler : teamPrecedence) {
			if (teamHandler.areOnSameTeam(owner1, owner2))
				return true;
		}

		return false;
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
	 * @see TeamHandler#getTeamRepresentation
	 */
	public static TeamRepresentation getTeamRepresentation(Owner owner) {
		if (ConfigHandler.SERVER.enableTeamOwnership.get()) {
			for (TeamHandler teamHandler : teamPrecedence) {
				TeamRepresentation teamRepresentation = teamHandler.getTeamRepresentation(owner);

				if (teamRepresentation != null)
					return teamRepresentation;
			}
		}

		return null;
	}

	/**
	 * @see TeamHandler#getOnlinePlayersFromOwner
	 */
	public static Collection<ServerPlayerEntity> getOnlinePlayersFromOwner(MinecraftServer server, Owner owner) {
		if (ConfigHandler.SERVER.enableTeamOwnership.get()) {
			for (TeamHandler teamHandler : teamPrecedence) {
				Collection<ServerPlayerEntity> onlinePlayers = teamHandler.getOnlinePlayersFromOwner(server, owner);

				if (onlinePlayers != null && !onlinePlayers.isEmpty())
					return onlinePlayers;
			}
		}

		return PlayerUtils.getPlayerListFromOwner(owner);
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

	public enum TeamType {
		FTB_TEAMS(() -> {
			if (ModList.get().isLoaded("ftbteams"))
				return new FTBTeamsCompat();
			else
				return null;
		}),
		VANILLA(VanillaTeamHandler::new),
		NO_OP(() -> null);

		private final Supplier<TeamHandler> teamHandler;

		private TeamType(Supplier<TeamHandler> teamHandler) {
			this.teamHandler = teamHandler;
		}

		public TeamHandler getTeamHandler() {
			return teamHandler.get();
		}
	}

	/**
	 * Sets the order in which SecurityCraft checks team ownership
	 *
	 * @param list the precedence list
	 * @see ConfigHandler.Server#teamOwnershipPrecedence
	 */
	public static void setPrecedence(List<TeamHandler> list) {
		teamPrecedence = list;
	}
}
