import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static List<Team> teams = new ArrayList<>();
    public static HashMap<Team,Integer> scoreBoard =new HashMap<>();
    public static HashMap<Team,Integer> goalsScored=new HashMap<>();
    private static  HashMap<Team,Integer> goalsReceived=new HashMap<>();
    public static List<Match> matches=new ArrayList<>();
    public static void main(String[] args) {
        List<String> teamNames = readTeamNames();
        List<Player> allPlayers = Player.getPlayers();

        teams = teamNames.stream()
                .map(teamName -> {
                    int teamId = teamNames.indexOf(teamName) + 1;
                    List<Player> players = randomlySelectPlayers(allPlayers, 15);
                    return new Team(teamId, teamName, players);
                })
                .collect(Collectors.toList());

        Collections.shuffle(teams);

        List<List<Team>> matches = IntStream.range(0, Constants.PERIODS)
                .mapToObj(period -> IntStream.range(0, Constants.MATCHES_PER_PERIOD)
                        .mapToObj(game -> {
                            int teamIndex = (game + period * Constants.MATCHES_PER_PERIOD) % teams.size();
                            return teams.get(teamIndex);
                        })
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        teams.stream().forEach(team -> {
            scoreBoard.put(team, 0);
            goalsScored.put(team, 0);
            goalsReceived.put(team, 0);
        });

        startTheGame();
        System.out.println();
        printOptionsMenu();

    }
    private static void printOptionsMenu(){
        System.out.println();
        Scanner scanner=new Scanner(System.in);
        System.out.println("options menu: ");
        System.out.println("➤ type 1 for option 1 - to find a match by a team ID");
        System.out.println("➤ type 2 for option 2 - to find top scoring teams by number");
        System.out.println("➤ type 3 for option 3 - to find players with at least the given number of goals");
        System.out.println("➤ type 4 for option 4 - to get a team in the leader boards by the given position number");
        System.out.println("➤ type 5 for option 5 - to get the top goal scoring players by number");
        System.out.println("➤ type 6 for option 6 - to start a new period");
        System.out.println("➤ type 7 for option 7 - to get the DEV_TOOLS menu");
        System.out.println();

        String scannerOption=promptCorrectOptionInputFromUser(scanner);
        if(scannerOption.equals(Constants.OPTION_1)){
            LeagueManager.findMatchesByTeam(Integer.parseInt(promptCorrectUserInput(scanner, scoreBoard.size())))
                    .stream()
                    .map(match -> "match ID: " + match.getId())
                    .forEach(System.out::println);
        }
        if(scannerOption.equals(Constants.OPTION_2)){
            LeagueManager.findTopScoringTeams(Integer.parseInt(promptCorrectUserInput(scanner, scoreBoard.size())))
                    .stream()
                    .map(Team::getName)
                    .forEach(System.out::println);
        }
        if(scannerOption.equals(Constants.OPTION_3)){
            String userInput;
            do{
                System.out.println("choose a number of goals from 0 to "+Goal.goals.size());
                userInput=scanner.nextLine();
            }while (!userInput.matches("\\d+") || Integer.parseInt(userInput)>Goal.goals.size());

            LeagueManager.findPlayersWithAtLeastNGoals(Integer.parseInt(userInput))
                    .forEach(player -> System.out.println(player.getFullName()));
        }
        if(scannerOption.equals(Constants.OPTION_4)){
            System.out.println(LeagueManager.getTeamByPosition(Integer.parseInt(promptCorrectUserInput(scanner, scoreBoard.size()))).getName());
        }
        if(scannerOption.equals(Constants.OPTION_5)){
            LeagueManager.getTopScorers(Integer.parseInt(promptCorrectUserInput(scanner,Player.getPlayers().size())))
                    .entrySet()
                    .stream()
                    .forEach(entry -> {
                        int playerId = entry.getKey();
                        int score = entry.getValue();
                        System.out.println("Player ID: " + playerId + " Score: " + score);
                    });
        }
        if(scannerOption.equals(Constants.OPTION_6)){
            if(matches.size()==Constants.PERIODS*Constants.MATCHES_PER_PERIOD){System.out.println("**********************");System.out.println("*  END OF THE GAME!  *");System.out.println("**********************");
                System.exit(1);
            }else {
                return;
            }
        }
        if(scannerOption.equals(Constants.OPTION_7)){
            printDevToolsMenu();
        }
        printOptionsMenu();
    }


    private static void startTheGame() {
        Random randomId = new Random();
        Random randomMinute = new Random();
        Random randomPlayers = new Random();

        IntStream.rangeClosed(1, Constants.PERIODS)
                .mapToObj(i -> {

                    List<Team> availableTeams = new ArrayList<>(teams);
                    List<Team> homeTeams = new ArrayList<>();
                    List<Team> awayTeams = new ArrayList<>();

                    IntStream.rangeClosed(1, Constants.MATCHES_PER_PERIOD)
                            .forEach(j -> {

                                int sumOfGoals = randomId.nextInt(21);
                                List<Goal> goalsPerMatch = IntStream.range(0, sumOfGoals)
                                        .mapToObj(k -> {
                                            int minute = randomMinute.nextInt(91);
                                            Player randomPlayer = Player.getPlayers().get(randomPlayers.nextInt(30));
                                            Goal goal = new Goal(Goal.goals.size()+1, minute, randomPlayer);
                                            Goal.goals.add(goal);
                                            return goal;
                                        })
                                        .collect(Collectors.toList());

                                Team homeTeam = getRandomTeam(availableTeams);
                                Team awayTeam = getRandomTeam(availableTeams);

                                homeTeams.add(homeTeam);
                                awayTeams.add(awayTeam);

                                System.out.println(homeTeam.getName() + " VS " + awayTeam.getName());
                                matches.add(new Match(matches.size()+1, homeTeam, awayTeam, goalsPerMatch));
                                if(!Constants.isSkipModOn){countDown();}
                                getMatchResults(goalsPerMatch, homeTeam, awayTeam);
                            });
                    printScoreBoard();
                    printOptionsMenu();

                    return null;
                })
                .collect(Collectors.toList());

    }
    private static void countDown(){
        int seconds = 10;
        while (seconds >= 0) {System.out.println(seconds);try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}seconds--;}
        System.out.println("MATCH OVER!");
    }
    private static void getMatchResults(List<Goal> goals, Team homeTeam, Team awayTeam) {
        int homeTeamGoalsScored = (int) goals.stream()
                .filter(goal -> homeTeam.getPlayers().stream()
                        .anyMatch(player -> player.getId() == goal.getScorer().getId()))
                .count();

        int awayTeamGoalsScored = (int) goals.stream()
                .filter(goal -> awayTeam.getPlayers().stream()
                        .anyMatch(player -> player.getId() == goal.getScorer().getId()))
                .count();

        System.out.println(homeTeam.getName() + ": " + homeTeamGoalsScored + " | " + awayTeam.getName() + ": " + awayTeamGoalsScored);

        Integer currentHomeTeamGoalsScored=goalsScored.get(homeTeam);
        Integer currentAwayTeamGoalsScored=goalsScored.get(awayTeam);
        goalsScored.put(homeTeam,currentHomeTeamGoalsScored+homeTeamGoalsScored);
        goalsScored.put(awayTeam,currentAwayTeamGoalsScored+awayTeamGoalsScored);

        Integer currentHomeTeamGoalsReceived=goalsReceived.get(homeTeam);
        Integer currentAwayTeamGoalsReceived=goalsReceived.get(awayTeam);
        goalsReceived.put(homeTeam,currentHomeTeamGoalsReceived+awayTeamGoalsScored);
        goalsReceived.put(awayTeam,currentAwayTeamGoalsReceived+homeTeamGoalsScored);

        Integer currentHomeTeamScore= scoreBoard.get(homeTeam);
        Integer currentAwayTeamScore= scoreBoard.get(awayTeam);

        if (homeTeamGoalsScored > awayTeamGoalsScored) {
            System.out.println(homeTeam.getName() + " WON!");
            scoreBoard.put(homeTeam,currentHomeTeamScore+Constants.ADD_WIN_POINTS);
        } else if (awayTeamGoalsScored > homeTeamGoalsScored) {
            System.out.println(awayTeam.getName() + " WON!");
            scoreBoard.put(awayTeam,currentAwayTeamScore+Constants.ADD_WIN_POINTS);
        } else {
            System.out.println("ITS A TIE!");
            scoreBoard.put(homeTeam,currentHomeTeamScore+Constants.ADD_TIE_POINTS);
            scoreBoard.put(awayTeam,currentAwayTeamScore+Constants.ADD_TIE_POINTS);
        }

        if(Constants.isOption3){System.out.println(homeTeam.getName() + " Score: " + scoreBoard.get(homeTeam));System.out.println(awayTeam.getName() + " Score: " + scoreBoard.get(awayTeam));}
        System.out.println();
    }
    private static Team getRandomTeam(List<Team> teams) {
        int randomIndex = new Random().nextInt(teams.size());
        return teams.remove(randomIndex);
    }
    private static void printScoreBoard() {
        LeagueManager.teamsByScoreBoardOrder.clear();
        scoreBoard.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    Team team1 = entry1.getKey();
                    Team team2 = entry2.getKey();
                    Integer score1 = entry1.getValue();
                    Integer score2 = entry2.getValue();

                    int goalsDiff1 = goalsScored.getOrDefault(team1, 0) - goalsReceived.getOrDefault(team1, 0);
                    int goalsDiff2 = goalsScored.getOrDefault(team2, 0) - goalsReceived.getOrDefault(team2, 0);

                    if (score1.equals(score2)) {
                        if (goalsDiff1 == goalsDiff2) {
                            return team1.getName().compareTo(team2.getName());
                        } else {
                            return Integer.compare(goalsDiff2, goalsDiff1);
                        }
                    } else {
                        return Integer.compare(score2, score1);
                    }
                })
                .forEach(entry -> {
                    Team team = entry.getKey();
                    Integer score = entry.getValue();
                    System.out.println("Team: " + team.getName() + " | Score: " + score);

                    LeagueManager.teamsByScoreBoardOrder.add(team);
                    scoreBoard.put(team,score);
                });

    }

    private static boolean isValidOptionInput(String userInput){
        return userInput.equals(Constants.OPTION_1) || userInput.equals(Constants.OPTION_2) || userInput.equals(Constants.OPTION_3) || userInput.equals(Constants.OPTION_4) || userInput.equals(Constants.OPTION_5) || userInput.equals(Constants.OPTION_6)||userInput.equals(Constants.OPTION_7);
    }
    private static String promptCorrectOptionInputFromUser(Scanner scanner) {
        System.out.println("choose an option from the above");
        String userInput = scanner.nextLine();
        return Stream.of(userInput)
                .filter(Main::isValidOptionInput)
                .findFirst()
                .orElseGet(() -> promptCorrectOptionInputFromUser(scanner));
    }
    private static boolean isUserInputInRange(String userInput,int sizeOfGivenCollection){
        try {
            int number = Integer.parseInt(userInput);
            return number >= 1 && number <= sizeOfGivenCollection;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String promptCorrectUserInput(Scanner scanner,int sizeOfGivenCollection){
        String userInput;
        do{
            System.out.println("choose a number between 1 to "+sizeOfGivenCollection);
            userInput=scanner.nextLine();
        }while (!isUserInputInRange(userInput,sizeOfGivenCollection));
        return userInput;
    }
    private static List<String> readTeamNames() {
        if(Constants.CSV_FILE_PATH==null){System.out.println("something went wrong with the file");System.exit(1);}
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.CSV_FILE_PATH))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(teamName -> !teamName.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("couldn't find the necessary csv file");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static List<Player> randomlySelectPlayers(List<Player> allPlayers, int count) {
        Random random = new Random();
        return random.ints(0, allPlayers.size())
                .distinct()
                .limit(count)
                .mapToObj(allPlayers::get)
                .collect(Collectors.toList());
    }
    private static void printDevToolsMenu(){
        Scanner scanner = new Scanner(System.in);
        String userInput;

        if(!Constants.isDev){
            if(!Constants.hasAttempted) {
                System.out.println("ARE YOU A DEVELOPER ??");
                System.out.println("type 1 for - YES");
                System.out.println("type 2 for - NO");
                System.out.println("if you type anything else you're out");
                String answer=scanner.nextLine();

                if(!answer.equals("1")){
                    System.out.println("ok, well i guess that's it then.");
                    Constants.hasAttempted=true;
                    return;
                }
                Constants.isDev=true;

            }else {
                System.out.println("you again...");
                return;
            }
        }
        do {
            System.out.println("\uD83D\uDC7B type 1 to view the sum of goals every team scored");
            System.out.println("\uD83D\uDC7B type 2 to view the sum of goals every team received");
            System.out.println("\uD83D\uDC7B type 3 to view/hide the match teams score after every match");
            System.out.println("\uD83D\uDC7B type 4 to view the sum of goals every player scored by full name");
            System.out.println("\uD83D\uDC7B type 5 to view the sum of goals every player scored by ID");
            System.out.println("\uD83D\uDC7B type 6 to turn skip mode on/off");
            System.out.println("\uD83D\uDC7B type 7 to go back to the previous menu");

            userInput = scanner.nextLine();

            if (userInput.matches("[1-7]")) {
                switch (userInput) {
                    case "1" -> {
                        Constants.isOption1 = true;
                        goalsScored.forEach((team, score) -> {
                            System.out.println("Team: " + team.getName() + " | goals scored: " + score);
                        });
                        System.out.println();
                        Constants.isOption1 = false;
                    }
                    case "2" -> {
                        Constants.isOption2 = true;
                        goalsReceived.forEach((team, score) -> {
                            System.out.println("Team: " + team.getName() + " | goals received: " + score);
                        });
                        System.out.println();
                        Constants.isOption2 = false;
                    }
                    case "3" -> Constants.isOption3 = !Constants.isOption3;
                    case "4" -> {
                        Constants.isOption4 = true;LeagueManager.getPlayersGoalsScore();System.out.println();Constants.isOption4 = false;
                        // מכיוון ויש לנו 30 שחקנים ל-10 קבוצות יש כפילויות של שחקנים ומכך ישנה הסבירות
                        //  שסך הגולים שכל השחקנים הכניסו יותר גדול מסך כמות הגולים שנכנסו במשחקים עד כה
                    }
                    case "5" ->{Constants.isOption5=true;LeagueManager.getPlayersGoalsScore();System.out.println();Constants.isOption5=false;}
                    case "6" -> Constants.isSkipModOn = !Constants.isSkipModOn;
                    case "7" -> printOptionsMenu();
                }

            } else {
                System.out.println("Invalid input. Please try again.");
            }
        } while (!userInput.equals("7"));


    }



}