import java.util.*;

public class Deck {
    private ArrayList<Card> deck;

    public Deck() {
        List<String> suits = Card.getValidSuits();
        List<Integer> magnitudes = Card.getValidMagnitude();

        deck = new ArrayList<>();
        for (String suit: suits)
        {
            for (Integer magnitude: magnitudes)
                deck.add(new Card(suit, magnitude));
        }
    }

    public Card dealTopCard()
    {
        if (deck.size()>0)
            return deck.remove(0);
        else
            return null;
    }

    public void shuffle()
    {
        Collections.shuffle(deck);
    }
}
