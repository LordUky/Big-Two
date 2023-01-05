import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * this is an abstract subclass extends CardList class to model a hand of cards
 * @author Fan Zheyu
 *
 */
public abstract class Hand extends CardList{

    private static ArrayList<String> combinationsOfFiveCards = new ArrayList<String>(Arrays.asList("Straight", "Flush", "FullHouse", "Quad", "StraightFlush"));;

    /**
     * a constructor for building a hand with the specified player as well as a list of cards
     * @param player the player (object) that holds this hand
     * @param cards a CardList suggesting the content of this hand of cards
     */
    public Hand (CardGamePlayer player, CardList cards){
        this.player = player;
        for (int i = 0; i < cards.size(); i++) {
            this.addCard(cards.getCard(i));
        }
        this.sort();
    }

    private CardGamePlayer player;

    /**
     * getter for the player of this hand
     * @return player of this hand
     */
    public CardGamePlayer getPlayer(){
        return this.player;
    }

    /**
     * find and return the top card of the hand.
     * @return top card of the hand, or null if the hand is empty.
     */
    public Card getTopCard(){
        if (!this.isEmpty()){
            this.sort();
            return this.getCard(this.size()-1);
        }
        else {
            return null;
        }
    }

    /**
     * a method for checking if this hand beats the specified hand
     * @param hand the specified hand to be compared by this hand
     * @return true if the other hand is beaten
     */
    public boolean beats(Hand hand){
        if (hand == null){
            return false;
        }

        if (Objects.equals(hand.getType(), this.getType())){
            //same kind
            if (Objects.equals(this.getType(), "Flush")){
                // special comparison between two Flusher: priority: suit > rank
                return this.getCard(0).getSuit() > hand.getCard(0).getSuit();
            }
            return this.getTopCard().compareTo(hand.getTopCard()) > 0;
        }
        else if (combinationsOfFiveCards.contains(hand.getType()) && combinationsOfFiveCards.contains(this.getType())){
            //both have 5 cards but different kind
            return combinationsOfFiveCards.indexOf(this.getType()) > combinationsOfFiveCards.indexOf(hand.getType());
        }
        else{
            // they cannot match
            return false;
        }

    }

    /**
     * an abstrat method. To check if the hand is valid, according to the game rule.
     * @return if the hand (combination of cards) is valid.
     */
    public abstract boolean isValid();

    /**
     * get the string specifying the type of this hand.
     * @return the string specifying the type of this hand.
     */
    public abstract String getType();

}
