import java.util.ArrayList;
import java.util.Arrays;

/**
 * To model a Big Two card game
 * @author Fan Zheyu
 */
public class BigTwo {

    /**
     * Constructor
     */
    public BigTwo(){
        this.playerList = new ArrayList<CardGamePlayer>();
        for (int ii = 0; ii < 4; ii++){
            this.playerList.add(new CardGamePlayer());
        }
        this.handsOnTable = new ArrayList<Hand>();
        this.gui = new BigTwoGUI(this);
        this.numOfPlayers = 0;
        this.client = new BigTwoClient(this, this.gui);
        this.client.connect();
    }

    private int numOfPlayers;

    private int myId = -1;

    private boolean isFirstPlayer = false;

    private Deck deck;

    private final ArrayList<CardGamePlayer> playerList;

    private ArrayList<Hand> handsOnTable;

    private int currentPlayerIdx;

    private boolean haveSent = false;

    private final BigTwoGUI gui;

    private BigTwoClient client;

    /**
     * getter for numOfPlayers, the number of players in this card game
     * @return the number of players in this card game
     */

    public void setMyId(int id){
        this.myId = id;
    }

    public int getMyId(){
        return this.myId;
    }

    public void setNumOfPlayers(int n){
        this.numOfPlayers = n;
    }

    public int getNumberOfPlayers(){
        return this.numOfPlayers;
    }

    public BigTwoClient getClient(){
        return this.client;
    }

    public int getNumOfPlayers(){
        return this.numOfPlayers;
    }

    /**
     * getter for the deck, the deck of cards being used in this card game
     * @return the deck of cards being used in this card game
     */
    public Deck getDeck(){
        return this.deck;
    }

    /**
     * getter for the playerList, the list of players (object, not index)
     * @return the list of players
     */
    public ArrayList<CardGamePlayer> getPlayerList(){
        return this.playerList;
    }

    /**
     * getter for the list of hands played on the table.
     *
     * @return the list of hands played on the table
     */
    ArrayList<Hand> getHandsOnTable(){
        return this.handsOnTable;
    }

    /**
     * getter for the index of the current player.
     *
     * @return the index of the current player
     */
    public int getCurrentPlayerIdx(){
        return this.currentPlayerIdx;
    }

    /**
     * Starts the card game.
     *
     * @param deck the shuffled deck of cards to be used in this game
     */
    void start(Deck deck){
        this.deck = deck;
        this.handsOnTable = new ArrayList<>();

        for (int i = 0; i < 4; i++){
            this.playerList.get(i).removeAllCards();
            for (int j = 0; j < 13; j++){
                this.playerList.get(i).addCard(deck.getCard(i * 13 + j));
            }
            this.playerList.get(i).sortCardsInHand();
        }

        int firstPlayer = 0;
        while (!playerList.get(firstPlayer).getCardsInHand().contains(new Card(0, 2))){
            firstPlayer++;
        }
        this.currentPlayerIdx = firstPlayer;
        if (this.getMyId() == firstPlayer){
            this.isFirstPlayer = true;
        }

        this.gui.setActivePlayer(this.currentPlayerIdx);
        if (this.getMyId() == this.getCurrentPlayerIdx() && this.isFirstPlayer){
            this.gui.enable();
        }
        gui.repaint();

        gui.promptActivePlayer();

    }

    /**
     * Makes a move by the player. (simply call checkMove(int playerIdx, int[] cardIdx))
     *
     * @param playerIdx the index of the player that makes the move
     * @param cardIdx   the list of the indices of the cards selected by the player
     */
    void makeMove(int playerIdx, int[] cardIdx){
        checkMove(playerIdx, cardIdx);
    }

    private boolean isStarterPlayer = true; // record if the game is at the biginning

    /**
     * Checks the move made by the player.
     *
     * @param playerIdx the index of the player who makes the move
     * @param cardIdx   the list of the indices of the cards selected by the player
     */
    void checkMove(int playerIdx, int[] cardIdx){
        if (this.getMyId() == currentPlayerIdx && !haveSent){
            this.haveSent = true;
            this.gui.printMsg("move sended to server! my id: "+this.getMyId());
            this.getClient().sendMessage(new CardGameMessage(6, this.getMyId(), cardIdx));
        }
        else{
            this.haveSent = false;
            if (cardIdx != null) Arrays.sort(cardIdx);

            /*
            if someone wants to pass :
             */

            //if the first player want to pass
            if (isStarterPlayer && cardIdx == null) {
                this.gui.printMsg("Not a legal move!!!\n");
                this.gui.promptActivePlayer();
                return;
            }

            //someone other than the first player want to pass
            if (!isStarterPlayer && cardIdx == null){

                //if the last hand is played by him
                if (this.handsOnTable.get(handsOnTable.size() - 1).getPlayer() == this.playerList.get(this.currentPlayerIdx)){
                    this.gui.printMsg("Cannot pass if last hand is yours!\n");
                    this.gui.promptActivePlayer();
                    return;
                }

                //if the last hand is not made by him
                this.gui.printMsg("{Pass}\n\n"); // show the Hand played
                this.currentPlayerIdx = (playerIdx + 1) % 4;
                this.gui.setActivePlayer(this.currentPlayerIdx); // turn for the next player
                this.gui.repaint();
                this.gui.promptActivePlayer();
                return;
            }

            /*
            if someone wants to play a hand
             */

            CardList temp = new CardList();
            for (int i: cardIdx){
                temp.addCard(playerList.get(playerIdx).getCardsInHand().getCard(i));
            }
            Hand hand = composeHand(playerList.get(playerIdx), temp);

            if (hand == null){
                this.gui.printMsg("Not a legal move!!!\n");
                this.gui.promptActivePlayer();
                return;
            }

            //if the first player wants to play
            if (isStarterPlayer) {
                // diamond 3 not included
                if (!hand.contains(new Card(0, 2))) {
                    this.gui.printMsg("Your hand has to contain Diamond 3!!!\n");
                    this.gui.promptActivePlayer();
                    return;
                }
                // diamond 3 included
                this.playerList.get(playerIdx).removeCards(hand); //remove cards from the player
                this.handsOnTable.add(hand); //add cards to table
                this.gui.printMsg("{"+hand.getType()+"} "+ hand +"\n"); //show the cards played

                isStarterPlayer = false;
                this.currentPlayerIdx = (playerIdx + 1) % 4; //move on to the next player (in BigTwo)
                //this.gui = new BigTwoGUI(this); // new UI
                this.gui.setActivePlayer(this.currentPlayerIdx); //move on to the next player (in BigTwoUI)
                this.gui.repaint(); // print the UI
                this.gui.promptActivePlayer();
                return;
            }

            // if player other than the 1st player wants to play

            // if the hand beats the last hand on table; or it does not, but it can be played because 3 consecutive passes have occurred
            if (hand.beats(this.handsOnTable.get(this.handsOnTable.size() - 1))
                    | this.playerList.get(getCurrentPlayerIdx())
                    == this.handsOnTable.get(this.handsOnTable.size() - 1).getPlayer()){
                this.playerList.get(playerIdx).removeCards(hand); //remove cards from the player
                this.handsOnTable.add(hand); //add cards to table
                this.gui.printMsg("{"+hand.getType()+"} "+ hand +"\n\n"); //show the cards played

                // if someone wins
                if (endOfGame()) {
                    this.gui.disable();
                    this.gui.setActivePlayer(-1);
                    this.gui.repaint(); //print final results
                    this.gui.printMsg("\nGame ends\n");
                    this.gui.printMsg("Player "+ getCurrentPlayerIdx() + " wins the game.\n");
                    for (int i = 0; i < this.getNumOfPlayers(); i++) { // print out the number of cards held by other players
                        if (i != getCurrentPlayerIdx()) {
                            this.gui.printMsg("Player " + i + " has " + this.playerList.get(i).getNumOfCards() + " cards in hand.\n");
                        }
                    }
                }

                // if no one wins for now
                else{
                    this.currentPlayerIdx = (playerIdx + 1) % 4; //move on to the next player (in BigTwo)
                    //this.gui = new BigTwoGUI(this); // new UI
                    this.gui.setActivePlayer(this.currentPlayerIdx); //move on to the next player (in BigTwoUI)
                    this.gui.repaint(); // print the UI
                    this.gui.promptActivePlayer();
                }
                return;
            }

            // if the hand does not beat the last hand on table while it has to
            else{
                this.gui.printMsg("Not a legal move!!!\n");
                this.gui.promptActivePlayer();
                return;
            }
        }

    }

    /**
     * judge if the game ends (i.e. someone's cards are all played out)
     * @return if the game ends
     */
    public boolean endOfGame(){
        for (CardGamePlayer i: this.getPlayerList()){
            if (i.getCardsInHand().size() == 0){
                return true;
            }
        }
        return false;
    }


    /**
     * main method
     * @param args cmd input
     *
     */

    public static void main(String[] args) {
        //create a new Big Two card game
        BigTwo game = new BigTwo();
        //create a deck of cards
        //BigTwoDeck gameDeck = new BigTwoDeck();
        //shuffle the deck
        //gameDeck.shuffle();
        //start the game
        //game.start(gameDeck);
    }

    Hand composeHand(CardGamePlayer player, CardList cards){
        if (new Single(player, cards).isValid()) return new Single(player, cards);
        if (new Pair(player, cards).isValid()) return new Pair(player, cards);
        if (new Triple(player, cards).isValid()) return new Triple(player, cards);
        if (new StraightFlush(player, cards).isValid()) return new StraightFlush(player, cards);
        if (new Straight(player, cards).isValid()) return new Straight(player, cards);
        if (new Flush(player, cards).isValid()) return new Flush(player, cards);
        if (new Quad(player, cards).isValid()) return new Quad(player, cards);
        if (new FullHouse(player, cards).isValid()) return new FullHouse(player, cards);
        return null;
    }

}
