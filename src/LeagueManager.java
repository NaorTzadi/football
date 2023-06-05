import java.util.*;
import java.util.stream.Collectors;


public class LeagueManager {

    public static List<Team> teamsByScoreBoardOrder =new ArrayList<>();
    public static List<Match> findMatchesByTeam(int teamId) {
        return Main.matches.stream()
                .filter(match -> match.getHomeTeam().getId() ==teamId || match.getAwayTeam().getId() == teamId)
                .sorted(Comparator.comparingInt(match -> {
                    if (match.getHomeTeam().getId() == teamId) {return 0;} else {return 1;}}))
                .collect(Collectors.toList());
    }

    public static List<Team> findTopScoringTeams(int num){
        List<Team> teams = new ArrayList<>(Main.goalsScored.keySet());
        teams.sort(Comparator.comparingInt(Main.goalsScored::get).reversed());
        return teams.stream().limit(num).collect(Collectors.toList());
    }
    public static List<Player> findPlayersWithAtLeastNGoals(int n) {
        return Player.getPlayers()
                .stream()
                .filter(player -> Goal.goals.stream()
                        .filter(goal -> goal.getScorer().getFullName().equals(player.getFullName()))
                        .count() >= n)
                .collect(Collectors.toList());
    }

    public static Team getTeamByPosition(int position) {
        List<Team> teams = new ArrayList<>(teamsByScoreBoardOrder);
        return teams.get(position - 1);
    }
    public static Map<Integer, Integer> getTopScorers(int n) {
        return getPlayersGoalsScore().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(n)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getId(),
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public static Map<Player,Integer> getPlayersGoalsScore(){
        HashMap<Player, Integer> playersGoalScore = new HashMap<>();

        for (Player player : Player.getPlayers()) {
            int goalsCounter = 0;

            for (Goal goal : Goal.goals) {
                String fullNameByPlayer = player.getFullName();
                String fullNameByGoalScorer = goal.getScorer().getFullName();
                if (fullNameByGoalScorer.equals(fullNameByPlayer)) {
                    goalsCounter++;
                }
            }
            playersGoalScore.put(player, goalsCounter);


            if (Constants.isOption4) {System.out.println(player.getFullName() + " score is: " + goalsCounter);}
            if (Constants.isOption5) {System.out.println("player's ID: " + player.getId() + " score is " + goalsCounter);}
        }
        return playersGoalScore;
    }



}