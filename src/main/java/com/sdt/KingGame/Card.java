import java.util.*;

public class Card {
    private String suit;
    private int magnitude;

    public Card(String suit, int magnitude) {
        setMagnitude(magnitude);
        setSuit(suit);
    }

    public String getSuit() {
        return suit;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public static List<Integer> getValidMagnitude()
    {
        return Arrays.asList(7, 8, 9, 10, 11, 12, 13, 14);
    }

    public static List<String> getValidSuits()
    {
        return Arrays.asList("hearts", "clubs", "diamonds", "spades");
    }
    public void setSuit(String suit) {
        List<String> validSuits = getValidSuits();
        if (validSuits.contains(suit))
            this.suit = suit;
        else
            throw new IllegalArgumentException("Valid suits are: " + validSuits);

    }

    public void setMagnitude(int magnitude) {
        List<Integer> validMagnitudes = getValidMagnitude();
        if (validMagnitudes.contains(magnitude))
            this.magnitude = magnitude;
        else
            throw new IllegalArgumentException("Valid magnitudes are: " + validMagnitudes);
    }

    public String toString()
    {
        return String.format("%d of %s", magnitude, suit);
    }
}
