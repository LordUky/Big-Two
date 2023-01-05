/**
 * this is a subclass extends Hand class to model a hand of FullHouse
 * @author Fan Zheyu
 *
 */
public class FullHouse extends Hand{

    /**
     * a constructor for building a FullHouse with the specified player as well as a list of cards
     * @param player the player (object) that holds this hand
     * @param cards a CardList suggesting the content of this hand of cards
     */
    public FullHouse(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if the hand is a valid FullHouse
     * @return if it is valid.
     */

    /**
     * find and return the top card of the hand.
     * @return top card of the hand, or null if the hand is empty.
     */
    public Card getTopCard(){
        //either aabbb or bbbaa
        if (this.getCard(2).getRank() == this.getCard(4).getRank()){
            // case of aabbb
            return this.getCard(4);
        }
        // case of bbbaa
        return this.getCard(2);
    }

    public boolean isValid() {
        if (this.size() != 5){
            return false;
        }
        int diff = 0;
        for (int i = 0; i < 4; i++){
            if (this.getCard(i).getRank() != this.getCard(i+1).getRank()){
                diff++;
            }
        }
        if (diff != 1){
            return false;
        }
        return this.getCard(1).getRank() != this.getCard(2).getRank() | this.getCard(2).getRank() != this.getCard(3).getRank();
    }

    /**
     * get the string specifying the type of this hand, which is FullHouse
     * @return the string specifying the type of this hand.
     */
    public String getType() {
        return "FullHouse";
    }
}
