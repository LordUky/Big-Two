/**
 * this is a subclass extends Hand class to model a hand of Flush
 * @author Fan Zheyu
 *
 */
public class Flush extends Hand{

    /**
     * a constructor for building a Flush with the specified player as well as a list of cards
     * @param player the player (object) that holds this hand
     * @param cards a CardList suggesting the content of this hand of cards
     */
    public Flush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if the hand is a valid Flush
     * @return if it is valid.
     */
    public boolean isValid() {
        if (this.size() != 5){
            return false;
        }
        for (int i = 0; i < 4; i++){
            if (this.getCard(i).getSuit() != this.getCard(i+1).getSuit()){
                return false;
            }
        }
        return true;
    }

    /**
     * get the string specifying the type of this hand, which is Flush
     * @return the string specifying the type of this hand.
     */
    public String getType() {
        return "Flush";
    }
}
