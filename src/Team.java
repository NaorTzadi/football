import java.util.List;

public class Team {
    private final int id;
    private final String name;
    private final List<Player> players;

    public Team(int id,String name,List<Player> players){
        this.id=id;
        this.name=name;
        this.players=players;
    }
    public int getId(){return this.id;}
    public List<Player> getPlayers(){return this.players;}
    public String getName(){return this.name;}

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", players=" + players +
                '}';
    }
}
