/**
 * This is a class extends Deck that models a deck of cards used in a Big Two card game
 * @author Fan Zheyu
 *
 */

public class BigTwoDeck extends Deck{

    /**
     * an overriding method to for initializing a deck of Big Two cards
     */
    public void initialize() {
        removeAllCards();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                BigTwoCard card = new BigTwoCard(i, j);
                addCard(card);
            }
        }
    }
}