/**
 * this is a subclass extends Hand class to model a hand of Pair
 * @author Fan Zheyu
 *
 */
public class Pair extends Hand{

    /**
     * a constructor for building a Pair with the specified player as well as a list of cards
     * @param player the player (object) that holds this hand
     * @param cards a CardList suggesting the content of this hand of cards
     */
    public Pair(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if the hand is a valid Pair
     * @return if it is valid.
     */
    public boolean isValid() {
        return this.size() == 2 && this.getCard(0).getRank() == this.getCard(1).getRank();
    }

    /**
     * get the string specifying the type of this hand, which is Pair.
     * @return the string specifying the type of this hand.
     */
    public String getType() {
        return "Pair";
    }
}
