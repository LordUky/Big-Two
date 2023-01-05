

/**
 * this is a subclass extends Hand class to model a hand of StraightFlush
 * @author Fan Zheyu
 *
 */
public class StraightFlush extends Hand{

    /**
     * a constructor for building a StraightFlush with the specified player as well as a list of cards
     * @param player the player (object) that holds this hand
     * @param cards a CardList suggesting the content of this hand of cards
     */
    public StraightFlush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if the hand is a valid StraightFlush
     * @return if it is valid.
     */
    public boolean isValid() {

        //size must be 5
        if (this.size() != 5){
            return false;
        }

        //must be straight
        if (this.getCard(0).getRank() >= 11 | this.getCard(0).getRank() <= 1){
            return false;
        }
        else{
            for (int i = 0; i < 4; i++){
                if ((this.getCard(i+1).getRank() + 11) % 13 - (this.getCard(i).getRank() + 11) % 13 != 1){
                    return false;
                }
            }
        }

        //must be flush
        for (int i = 0; i < 4; i++){
            if (this.getCard(i).getSuit() != this.getCard(i+1).getSuit()){
                return false;
            }
        }

        return true;
    }

    /**
     * get the string specifying the type of this hand, which is StraightFlush.
     * @return the string specifying the type of this hand.
     */
    public String getType() {
        return "StraightFlush";
    }
}
