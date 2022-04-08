package com.codingame.game;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;
import com.codingame.gameengine.module.entities.World;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Referee extends AbstractReferee {
    @Inject private SoloGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;
    @Inject private TooltipModule tooltips;

    public static final int RADIUS_POOL = 500;
    public static final int MOUSE_SPEED = 10;
    public static final int CAT_RADIUS = 80;
    public static final int CAPPED_MESSAGE_LENGTH = 26;
    public static final int WINNER_X_OFFSET = 200;
    public static final int WINNER_Y_OFFSET = 200;
    public static final int LOSER_X_OFFSET = 182;
    public static final int LOSER_Y_OFFSET = 103;
    public static final int VIEWER_WIDTH = World.DEFAULT_WIDTH;
    public static final int VIEWER_HEIGHT = World.DEFAULT_HEIGHT;
    public static final Complex CENTER_POOL = new Complex(VIEWER_WIDTH / 2, VIEWER_HEIGHT / 2);
    public static final String MOUSE_SPRITE = "mouse.png";
    public static final String BACKGROUND_SPRITE = "background.png";
    public static final String CAT_SPRITE = "Cat-Dizzy-icon.png";

    private Sprite mouseSprite;
    private Sprite catSprite;

    private Complex mousePosition;
    private Complex catPosition;

    private double maxAngle;
    private double catAngle;
    private Integer catSpeed;
    private Text infoText;

    @Override
    public void init() {
        gameManager.setFrameDuration(200);
        gameManager.setMaxTurns(350);


        // Draw background
        graphicEntityModule.createSprite().setImage(BACKGROUND_SPRITE);

        String[] confInput = gameManager.getTestCaseInput().get(0).split("\\s+");
        mousePosition = new Complex(Integer.valueOf(confInput[0]), Integer.valueOf(confInput[1]));
//        for (int i = 0; i < confInput.length; i++) {
//            String s = confInput[i];
//            gameManager.addToGameSummary(s);
//        }


        System.out.println(Integer.valueOf(confInput[0]) + " " + Integer.valueOf(confInput[1]));
        //mousePosition = new Complex(200, 0);
        catAngle = Float.valueOf(confInput[2]);
        catPosition = computeCatPosition(catAngle);
        catSpeed = Integer.valueOf(confInput[3]);
        maxAngle = (double)catSpeed / (double)RADIUS_POOL;

        //display center
        graphicEntityModule.createCircle()
                .setRadius(5)
                .setLineWidth(0)
                .setFillColor(0x009900)
                .setX(CENTER_POOL.getReInt())
                .setY(CENTER_POOL.getImInt());

        graphicEntityModule.createRectangle().setLineWidth(2)
                .setWidth(550)
                .setHeight(55)
                .setLineWidth(2)
                .setLineColor(0x000000)
                .setFillColor(0x030303)
                .setX(20)
                .setY(20)
                .setZIndex(3);

        infoText = graphicEntityModule.createText("")
                .setFontFamily("Lato")
                .setStrokeThickness(2) // Adding an outline
                .setFontSize(35)
                .setX(23)
                .setY(25)
                .setFillColor(0xFFFFFF)
                .setZIndex(4);



        mouseSprite = graphicEntityModule.createSprite().setImage(MOUSE_SPRITE)
                .setX(CENTER_POOL.getReInt() + mousePosition.getReInt())
                .setY(CENTER_POOL.getImInt() - mousePosition.getImInt())
                .setAnchor(.5)
                .setZIndex(1);

        catSprite = graphicEntityModule.createSprite().setImage(CAT_SPRITE)
                .setX(CENTER_POOL.getReInt() + catPosition.getReInt())
                .setY(CENTER_POOL.getImInt() - catPosition.getImInt())
                .setAnchor(.5)
                .setZIndex(2);

        gameManager.getPlayer().sendInputLine(String.valueOf(catSpeed));

        tooltips.setTooltipText(mouseSprite, "X:" + mousePosition.getReInt() + " Y:" + mousePosition.getImInt());
        tooltips.setTooltipText(catSprite, "X:" + catPosition.getReInt() + " Y:" + catPosition.getImInt());
    }

    private Complex computeCatPosition(double catAngle) {
        return new Complex(Math.cos(catAngle), Math.sin(catAngle)).times(RADIUS_POOL);
    }

    @Override
    public void gameTurn(int turn) {
        gameManager.getPlayer().sendInputLine(mousePosition.toGameString() + " " + catPosition.toGameString());
        gameManager.getPlayer().execute();
        try {
            List<String> outputs = gameManager.getPlayer().getOutputs();
            Boolean escape = updateMousePosition(outputs);
            if (escape == null) {
                return;
            }
            if (escape) {
                if (checkEscapeCat()) {
                    displayWin();
                    gameManager.winGame("Yes Escaped");
                }
                else {
                    displayLoose();
                    gameManager.loseGame("The Cat got you");
                }
            }
            String message = getMessage(outputs);
            infoText.setText(message);

            if (turn == gameManager.getMaxTurns() - 1) {
                displayLoose();
            }
            double angle = 0;
            if (!mousePosition.equals(new Complex(0,0))) {
                Complex escapeMouseClosest = computeClosetEscapePointNoMove(mousePosition.times(10));
                double angle4 = Complex.angleRad(escapeMouseClosest, new Complex(0,0), catPosition);
                Complex pos1 = computeCatPosition(catAngle + angle4);
                Complex pos2 = computeCatPosition(catAngle - angle4);
                List<Complex> possiblePos = new ArrayList<>();
                possiblePos.add(pos1);
                possiblePos.add(pos2);
                Complex closestEscape = possiblePos.stream().sorted(Comparator.comparingDouble(pos -> escapeMouseClosest.minus(pos).module()))
                        .findFirst()
                        .orElse(null);
                if (closestEscape.equals(pos1)) {
                    angle = -angle4;
                }
                else {
                    angle = angle4;
                }
            }

            double angleDiff = 0;

            if (angle >= 0) {
                angleDiff = Math.min(maxAngle, angle);
            }
            else {
                angleDiff = Math.max(-maxAngle, angle);
            }
            catAngle -= angleDiff;
            catPosition = computeCatPosition(catAngle).clone();
            tooltips.setTooltipText(mouseSprite, "X:" + mousePosition.getReInt() + " Y:" + mousePosition.getImInt());
            tooltips.setTooltipText(catSprite, "X:" + catPosition.getReInt() + " Y:" + catPosition.getImInt());
        } catch (TimeoutException e) {
            gameManager.loseGame("Timeout!");
        }
        updateView();
    }

    private void displayWin() {
        graphicEntityModule.createSprite().setImage("winner-png-25168.png")
                .setX(CENTER_POOL.getReInt() - WINNER_X_OFFSET)
                .setY(CENTER_POOL.getImInt() - WINNER_Y_OFFSET)
                .setScale(2)
                .setZIndex(5);
    }

    private void displayLoose() {
        graphicEntityModule.createSprite().setImage("loser.png")
                .setX(CENTER_POOL.getReInt() - LOSER_X_OFFSET)
                .setY(CENTER_POOL.getImInt() - LOSER_Y_OFFSET)
                .setScale(.5)
                .setZIndex(5);
    }

    private boolean checkEscapeCat() {
        return mousePosition.minus(catPosition).module() > CAT_RADIUS;
    }

    private Complex computeClosetEscapePointNoMove(Complex diff) {
        double[] tFactors = computeTFactorEscapePoint(diff);
        List<Complex> solutions = new ArrayList<>();
        for (int i = 0; i < tFactors.length; i++) {
            double tFactor = tFactors[i];
            solutions.add(mousePosition.plus(diff.times(tFactor)));
        }
        return solutions.stream().sorted(Comparator.comparingDouble(s -> s.minus(mousePosition).module()))
                .findFirst()
                .orElse(null);
    }

    private Boolean updateMousePosition(List<String> outputs) {
        Complex targetPosition = checkOutput(outputs);
        if (targetPosition == null) {
            return null;
        }
        Complex diff = targetPosition.minus(mousePosition);
        Complex diffReduced = null;

        if (diff.module() <= MOUSE_SPEED) {
            diffReduced = diff;
        }
        else {
            diffReduced =  diff.reduceToNorm1().times(MOUSE_SPEED);
        }
        Complex escape = computeEscapePoint(diffReduced);
        if (escape != null) {
            return true;
        }
        else {
            mousePosition = mousePosition.plus(diffReduced).clone();
            return false;
        }

    }

    private Complex computeEscapePoint(Complex diff) {
        double[] tFactors = computeTFactorEscapePoint(diff);
        if (tFactors != null) {
            for (int i = 0; i < tFactors.length; i++) {
                double tFactor = tFactors[i];
                if (tFactor > 0 && tFactor <= 1) {
                    return mousePosition.plus(diff.times(tFactor));
                }
            }
        }

        return null;
    }

    private double[] computeTFactorEscapePoint(Complex diff) {
        double A = Math.pow(diff.getRe(), 2) + Math.pow(diff.getIm(), 2);
        double positionX = mousePosition.getRe();
        double positionY = mousePosition.getIm();
        double B = 2 * positionX * diff.getRe() + 2 * positionY * diff.getIm();
        double C = Math.pow(positionX, 2) + Math.pow(positionY, 2) - RADIUS_POOL * RADIUS_POOL;

        double delta = B * B - 4 * A * C;

        if (delta >= 0) {
            double tOne = (-B + Math.sqrt(delta)) / (2 * A);
            double tTWo = (-B - Math.sqrt(delta)) / (2 * A);
            return new double[] {tOne, tTWo};
        }
        return null;
    }

    private String getMessage(List<String> outputs) {
        List<String> dest = Arrays.asList(outputs.get(0).split(" "));
        if (dest.size() < 2) {
            return "";
        }
        dest = dest.subList(2, dest.size());
        String message = "";
        for (int i = 0; i < dest.size(); i++) {
            String word =  dest.get(i);
            message += word + " ";
        }
        if (message.length() == 0) {
            return "";
        }
        return message.trim().substring(0, Math.min(message.length() - 1, CAPPED_MESSAGE_LENGTH));
    }

    private Complex checkOutput(List<String> outputs) {
        if (outputs.size() != 1) {
            gameManager.loseGame("You did not send output in your turn.");
            return null;
        }
        List<String> dest = Arrays.asList(outputs.get(0).split(" "));
        if (dest.size() < 2) {
            gameManager.loseGame("You did not provide correct output (2 integers) + Message");
            return null;
        }
        String x = dest.get(0);
        String y = dest.get(1);

        int xint = 0;
        int yint = 0;
        try {
            xint = Integer.parseInt(x);
            yint = Integer.parseInt(y);
        }
        catch (NumberFormatException e) {
            gameManager.loseGame("You didn,t provide valid integers");
            return null;
        }
        return new Complex(xint, yint);
    }

    private void updateView() {
        mouseSprite.setX(mousePosition.getReInt() + CENTER_POOL.getReInt())
                .setY(CENTER_POOL.getImInt() - mousePosition.getImInt());
        catSprite.setX(catPosition.getReInt() + CENTER_POOL.getReInt())
                .setY(CENTER_POOL.getImInt() - catPosition.getImInt());
    }
}
