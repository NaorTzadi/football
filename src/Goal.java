import java.util.ArrayList;
import java.util.List;
public class Goal {
    private final int id;
    private final int minute;
    private final Player scorer;

    public Goal(int id, int minute,Player scorer){
        this.id=id;
        this.minute=minute;
        this.scorer=scorer;
    }
    public Player getScorer(){return this.scorer;}
    public static List<Goal> goals=new ArrayList<>();
}
