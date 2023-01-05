/**
 * this is a subclass extends Hand class to model a hand of Triple
 * @author Fan Zheyu
 *
 */
public class Triple extends Hand{

    /**
     * a constructor for building a Triple with the specified player as well as a list of cards
     * @param player the player (object) that holds this hand
     * @param cards a CardList suggesting the content of this hand of cards
     */
    public Triple(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if the hand is a valid Triple
     * @return if it is valid.
     */
    public boolean isValid() {
        // size is 3 and all cards are equal.
        return this.size() == 3 &&
                (this.getCard(0).getRank() == this.getCard(1).getRank()
                        && this.getCard(1).getRank() == this.getCard(2).getRank());
    }

    /**
     * get the string specifying the type of this hand, which is Triple.
     * @return the string specifying the type of this hand.
     */
    public String getType() {
        return "Triple";
    }
}
