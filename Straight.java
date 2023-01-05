public class Straight extends Hand{

    /**
     * a constructor for building a Straight with the specified player as well as a list of cards
     * @param player the player (object) that holds this hand
     * @param cards a CardList suggesting the content of this hand of cards
     */
    public Straight(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if the hand is a valid Straight
     * @return if it is valid.
     */
    public boolean isValid() {
        if (this.size() != 5){
            return false;
        }
        if (this.getCard(0).getRank() >= 11 || this.getCard(0).getRank() <= 1){
            return false;
        }
        else{
            for (int i = 0; i < 4; i++){
                if ((this.getCard(i+1).getRank() + 11) % 13 - (this.getCard(i).getRank() + 11) % 13 != 1){
                    return false;
                }
            }
        return true;
        }
    }

    /**
     * get the string specifying the type of this hand, which is Straight
     * @return the string specifying the type of this hand.
     */
    public String getType() {
        return "Straight";
    }
}
