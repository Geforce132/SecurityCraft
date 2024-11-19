package net.geforcemods.securitycraft.util;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.compat.ftbutilities.FTBUtilitiesCompat;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

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

		if (Loader.isModLoaded("ftbutilities"))
			return FTBUtilitiesCompat.areOnSameTeam(owner1, owner2);

		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner1.getName());

		return team != null && team.getMembershipCollection().contains(owner2.getName());
	}

	/**
	 * Gets the scoreboard team the given player is on
	 *
	 * @param playerName The player whose team to get
	 * @return The team the given player is on. null if the player is not part of a team
	 */
	public static ScorePlayerTeam getVanillaTeamFromPlayer(String playerName) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		if (server != null)
			return server.getEntityWorld().getScoreboard().getPlayersTeam(playerName);
		else
			return SecurityCraft.proxy.getClientPlayer().world.getScoreboard().getPlayersTeam(playerName);
	}

	/**
	 * Gets a representation containing the team's name and color
	 *
	 * @param owner The owner whose team to get
	 * @return The {@link TeamRepresentation} of the owner's team, {@code null} if they are not part of a team
	 */
	public static TeamRepresentation getTeamRepresentation(Owner owner) {
		if (Loader.isModLoaded("ftbutilities"))
			return FTBUtilitiesCompat.getTeamRepresentation(owner);

		ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

		if (team != null && team.getMembershipCollection().size() > 1) {
			TextFormatting color = team.getColor();

			return new TeamRepresentation(team.getDisplayName(), !color.isColor() ? TextFormatting.GRAY : color);
		}

		return null;
	}

	/**
	 * Gets all players that are in the same team as the given owner, and currently online
	 *
	 * @param server The server
	 * @param owner The owner whose team to get the players of
	 * @return A list containing all online players who are in the same team as the owner. If the owner is not in a team, the
	 *         list will only contain the owning player, if they're online.
	 */
	public static Collection<EntityPlayerMP> getOnlinePlayersFromOwner(MinecraftServer server, Owner owner) {
		Collection<EntityPlayerMP> onlinePlayers = null;

		if (Loader.isModLoaded("ftbutilities"))
			onlinePlayers = FTBUtilitiesCompat.getOnlinePlayersInTeam(owner);
		else {
			ScorePlayerTeam team = TeamUtils.getVanillaTeamFromPlayer(owner.getName());

			if (team != null)
				onlinePlayers = team.getMembershipCollection().stream().map(server.getPlayerList()::getPlayerByUsername).filter(Objects::nonNull).collect(Collectors.toList());
		}

		if (onlinePlayers == null || onlinePlayers.isEmpty())
			return PlayerUtils.getPlayerListFromOwner(owner);

		return onlinePlayers;
	}

	public static class TeamRepresentation {
		private final String name;
		private final TextFormatting color;

		public TeamRepresentation(String name, TextFormatting color) {
			this.name = name;
			this.color = color;
		}

		public String name() {
			return name;
		}

		public TextFormatting color() {
			return color;
		}
	}
}
