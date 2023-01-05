/**
 * This is a class extends Card class that models a card used in a BigTwo card game
 * @author Fan Zheyu
 *
 */

public class BigTwoCard extends Card{

    /**
     * Constructor
     *
     * @param suit an int value between 0 and 3 representing the suit of a card:
     *             <p>
     *             0 = Diamond,
     *             1 = Club,
     *             2 = Heart,
     *             3 = Spade
     * @param rank an int value between 0 and 12 representing the rank of a card:
     *             <p>
     *             0 = 'A',
     *             1 = '2',
     *             2 = '3',
     *             ...,
     *             8 = '9',
     *             9 = '0',
     *             10 = 'J',
     *             11 = 'Q',
     *             12 = 'K'
     */
    public BigTwoCard(int suit, int rank){
        super(suit, rank);
    }


    /**
     * make the comparison of the card and the specified card and return the result
     * @param card the specified card to be compared.
     * @return 1, 0, -1 if the card is superior to, equal to, or inferior to the specified card in terms of the Big Two Game.
     */
    public int compareTo(Card card){

        int thisGetRank_Rank = (this.getRank() + 11) % 13;
        int specifiedGetRank_Rank = (card.getRank() + 11) % 13;
        int thisGetSuit_Rank = this.getSuit();
        int specifiedGetSuit_Rank = card.getSuit();

        return (4 * thisGetRank_Rank + thisGetSuit_Rank) - (4 * specifiedGetRank_Rank + specifiedGetSuit_Rank);

    }
}
