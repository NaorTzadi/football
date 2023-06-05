import java.util.List;

public class Match {
    private final int id;
    private final Team homeTeam;
    private final Team awayTeam;
    private final List<Goal> goals;

    public  Match(int id, Team homeTeam,Team awayTeam,List<Goal> goals){
        this.id=id;
        this.homeTeam=homeTeam;
        this.awayTeam=awayTeam;
        this.goals=goals;
    }
    public int getId(){return this.id;}
    public Team getHomeTeam(){return this.homeTeam;}
    public Team getAwayTeam(){return this.awayTeam;}
    public List<Goal> getGoals(){return this.goals;}
}
