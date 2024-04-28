package net.geforcemods.securitycraft.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;

public record ListModuleData(List<String> players, List<String> teams, boolean affectEveryone) implements TooltipProvider {

	public static final int MAX_PLAYERS = 50;
	public static final ListModuleData EMPTY = new ListModuleData(List.of(), List.of(), false);
	//@formatter:off
	public static final Codec<ListModuleData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.STRING.sizeLimitedListOf(MAX_PLAYERS).fieldOf("players").forGetter(ListModuleData::players),
					Codec.STRING.listOf().fieldOf("teams").forGetter(ListModuleData::teams),
					Codec.BOOL.fieldOf("affect_everyone").forGetter(ListModuleData::affectEveryone))
			.apply(instance, ListModuleData::new));
	public static final StreamCodec<ByteBuf, ListModuleData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(MAX_PLAYERS)), ListModuleData::players,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ListModuleData::teams,
			ByteBufCodecs.BOOL, ListModuleData::affectEveryone,
			ListModuleData::new);
	//@formatter:on
	public ListModuleData addPlayer(ItemStack stack, String playerName) {
		if (players.size() == MAX_PLAYERS || isPlayerOnList(playerName))
			return this;

		List<String> newPlayers = new ArrayList<>(players);
		ListModuleData newListModuleData;

		newPlayers.add(playerName);
		newListModuleData = new ListModuleData(newPlayers, teams, affectEveryone);
		stack.set(SCContent.LIST_MODULE_DATA, newListModuleData);
		return newListModuleData;
	}

	public ListModuleData removePlayer(ItemStack stack, String playerName) {
		if (players.isEmpty() || !isPlayerOnList(playerName))
			return this;

		List<String> newPlayers = new ArrayList<>(players);
		ListModuleData newListModuleData;

		newPlayers.remove(playerName);
		newListModuleData = new ListModuleData(newPlayers, teams, affectEveryone);
		stack.set(SCContent.LIST_MODULE_DATA, newListModuleData);
		return newListModuleData;
	}

	public ListModuleData toggleTeam(ItemStack stack, String teamName) {
		List<String> newTeams = new ArrayList<>(teams);
		ListModuleData newListModuleData;

		if (isTeamOnList(teamName))
			newTeams.remove(teamName);
		else
			newTeams.add(teamName);

		newListModuleData = new ListModuleData(players, newTeams, affectEveryone);
		stack.set(SCContent.LIST_MODULE_DATA, newListModuleData);
		return newListModuleData;
	}

	public boolean isTeamOfPlayerOnList(Level level, String playerName) {
		PlayerTeam team = level.getScoreboard().getPlayersTeam(playerName);

		return team != null && isTeamOnList(team.getName());
	}

	public boolean isTeamOnList(String teamName) {
		return teams.contains(teamName);
	}

	public boolean isPlayerOnList(String playerName) {
		return players.stream().anyMatch(playerName::equalsIgnoreCase);
	}

	public void updateAffectEveryone(ItemStack stack, boolean newAffectEveryone) {
		if (newAffectEveryone != affectEveryone)
			stack.set(SCContent.LIST_MODULE_DATA, new ListModuleData(players, teams, newAffectEveryone));
	}

	@Override
	public void addToTooltip(TooltipContext ctx, Consumer<Component> lineAdder, TooltipFlag flag) {
		if (affectEveryone)
			lineAdder.accept(Utils.localize("tooltip.securitycraft.component.list_module_data.affects_everyone").setStyle(Utils.GRAY_STYLE));
		else {
			lineAdder.accept(Utils.localize("tooltip.securitycraft.component.list_module_data.added_players", players.size()).setStyle(Utils.GRAY_STYLE));
			lineAdder.accept(Utils.localize("tooltip.securitycraft.component.list_module_data.added_teams", teams.size()).setStyle(Utils.GRAY_STYLE));
		}
	}
}
