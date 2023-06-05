import java.util.ArrayList;
import java.util.List;

public class Player {
    private final int id;
    private final String firstName;
    private final String lastName;

    public Player(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public int getId(){return this.id;}
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "Lionel", "Messi"));
        players.add(new Player(2, "Cristiano", "Ronaldo"));
        players.add(new Player(3, "Neymar", "Jr."));
        players.add(new Player(4, "Kylian", "Mbappé"));
        players.add(new Player(5, "Robert", "Lewandowski"));
        players.add(new Player(6, "Kevin", "De Bruyne"));
        players.add(new Player(7, "Virgil", "van Dijk"));
        players.add(new Player(8, "Mohamed", "Salah"));
        players.add(new Player(9, "Harry", "Kane"));
        players.add(new Player(10, "Sergio", "Ramos"));
        players.add(new Player(11, "Manuel", "Neuer"));
        players.add(new Player(12, "Eden", "Hazard"));
        players.add(new Player(13, "Antoine", "Griezmann"));
        players.add(new Player(14, "Luka", "Modrić"));
        players.add(new Player(15, "Karim", "Benzema"));
        players.add(new Player(16, "Paulo", "Dybala"));
        players.add(new Player(17, "Raheem", "Sterling"));
        players.add(new Player(18, "Sadio", "Mané"));
        players.add(new Player(19, "Romelu", "Lukaku"));
        players.add(new Player(20, "N'Golo", "Kanté"));
        players.add(new Player(21, "Pierre-Emerick", "Aubameyang"));
        players.add(new Player(22, "Joshua", "Kimmich"));
        players.add(new Player(23, "Toni", "Kroos"));
        players.add(new Player(24, "Thiago", "Alcântara"));
        players.add(new Player(25, "Alisson", "Becker"));
        players.add(new Player(26, "Bernardo", "Silva"));
        players.add(new Player(27, "Son", "Heung-min"));
        players.add(new Player(28, "Sergio", "Agüero"));
        players.add(new Player(29, "Marco", "Reus"));
        players.add(new Player(30, "David", "Silva"));
        return players;
    }
}
