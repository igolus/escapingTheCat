import java.util.Scanner;

public class Solution {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int catSpeed = in.nextInt();

        // game loop
        while (true) {
            int mouseX = in.nextInt();
            int mouseY = in.nextInt();
            int catX = in.nextInt();
            int catY = in.nextInt();
            System.err.println(catX + " " + catY);

            int difx = 2 * mouseX - catX;
            int dify = 2 * mouseY - catY;
            System.out.println(difx + " " + dify);

        }
    }
}
