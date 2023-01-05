/**
 * this is a subclass extends Hand class to model a hand of Single
 * @author Fan Zheyu
 *
 */
public class Single extends Hand{

    /**
     * a constructor for building a Single with the specified player as well as a list of cards
     * @param player the player (object) that holds this hand
     * @param cards a CardList suggesting the content of this hand of cards
     */
    public Single(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if the hand is a valid Single
     * @return if it is valid.
     */
    public boolean isValid() {
        return this.size() == 1;
    }

    /**
     * get the string specifying the type of this hand, which is Single.
     * @return the string specifying the type of this hand.
     */
    public String getType() {
        return "Single";
    }
}
