package org.arsparadox.mobtalkerredux.command;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.server.level.ServerLevel;

public class TeamHandler {
    public static final String DEFAULT_TEAM_NAME = "harem_mobs";

    /**
     * Creates a team if it doesn't exist
     */
    public static PlayerTeam getOrCreateTeam(ServerLevel level, String teamName) {
        Scoreboard scoreboard = level.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);

        if (team == null) {
            team = scoreboard.addPlayerTeam(teamName);
            setupTeamDefaults(team);
        }

        return team;
    }

    /**
     * Sets up default team options
     */
    private static void setupTeamDefaults(PlayerTeam team) {
        team.setSeeFriendlyInvisibles(true);
        team.setAllowFriendlyFire(false);
        team.setCollisionRule(Team.CollisionRule.NEVER);
    }

    /**
     * Adds an entity to the same team as a player
     */
    public static void addToPlayerTeam(Player player, Entity entity) {
        if (player.level().isClientSide()) return;

        ServerLevel level = (ServerLevel) player.level();
        Scoreboard scoreboard = level.getScoreboard();

        String teamName = getPlayerTeamName(player);
        if (teamName == null) {
            teamName = DEFAULT_TEAM_NAME;
            PlayerTeam team = getOrCreateTeam(level, teamName);
            scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
        }

        scoreboard.addPlayerToTeam(entity.getScoreboardName(), scoreboard.getPlayerTeam(teamName));
    }

    /**
     * Adds an entity to a specific team
     */
    public static void addToTeam(ServerLevel level, Entity entity, String teamName) {
        PlayerTeam team = getOrCreateTeam(level, teamName);
        level.getScoreboard().addPlayerToTeam(entity.getScoreboardName(), team);
    }

    /**
     * Removes an entity from its team
     */
    public static void removeFromTeam(Entity entity) {
        if (entity.level().isClientSide()) return;

        ServerLevel level = (ServerLevel) entity.level();
        Scoreboard scoreboard = level.getScoreboard();
        scoreboard.removePlayerFromTeam(entity.getScoreboardName());
    }

    /**
     * Checks if an entity is in any team
     */
    public static boolean isInAnyTeam(Entity entity) {
        if (entity.level().isClientSide()) return false;

        ServerLevel level = (ServerLevel) entity.level();
        return level.getScoreboard().getPlayersTeam(entity.getScoreboardName()) != null;
    }

    /**
     * Checks if two entities are in the same team
     */
    public static boolean areInSameTeam(Entity entity1, Entity entity2) {
        if (entity1.level().isClientSide()) return false;

        ServerLevel level = (ServerLevel) entity1.level();
        Scoreboard scoreboard = level.getScoreboard();

        PlayerTeam team1 = scoreboard.getPlayersTeam(entity1.getScoreboardName());
        PlayerTeam team2 = scoreboard.getPlayersTeam(entity2.getScoreboardName());

        return team1 != null && team1 == team2;
    }

    /**
     * Gets the name of the team a player is in
     */
    public static String getPlayerTeamName(Player player) {
        if (player.level().isClientSide()) return null;

        ServerLevel level = (ServerLevel) player.level();
        PlayerTeam team = level.getScoreboard().getPlayersTeam(player.getScoreboardName());
        return team != null ? team.getName() : null;
    }
}