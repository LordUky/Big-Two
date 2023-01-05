/**
 * this is a subclass extends Hand class to model a hand of Quad
 * @author Fan Zheyu
 *
 */
public class Quad extends Hand{

    /**
     * a constructor for building a Quad with the specified player as well as a list of cards
     * @param player the player (object) that holds this hand
     * @param cards a CardList suggesting the content of this hand of cards
     */
    public Quad(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    public Card getTopCard(){
        //either abbbb or bbbba
        if (this.getCard(2).getRank() == this.getCard(4).getRank()){
            // case of abbbb
            return this.getCard(4);
        }
        // case of bbbba
        return this.getCard(3);
    }

    /**
     * Check if the hand is a valid Quad
     * @return if it is valid.
     */
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
        return this.getCard(0).getRank() != this.getCard(1).getRank() | this.getCard(3).getRank() != this.getCard(4).getRank();
    }

    /**
     * get the string specifying the type of this hand, which is Quad
     * @return the string specifying the type of this hand.
     */
    public String getType() {
        return "Quad";
    }
}
