import com.codingame.game.Complex;

public class ComputeAngles {
    public static void main(String[] args) {
        double radius = 400;
        double angle = 1.2;
        System.out.println(new Complex(Math.cos(angle), Math.sin(angle)).times(radius));
    }
}
