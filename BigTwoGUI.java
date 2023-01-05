import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * This class implements the CardGame interface and is used to model a Big Two card game
 * @author Fan Zheyu
 */
public class BigTwoGUI implements CardGameUI{
    private final static int MAX_CARD_NUM = 13; // max. no. of cards each player holds, which is 13
    private ArrayList<CardGamePlayer> playerList;
    private ArrayList<Hand> handsOnTable;
    private BigTwo game;
    private boolean[] selected = new boolean[13];
    private int activePlayer;
    private final int cardWidth = 55;
    private final int cardHeight = 80;
    private JFrame frame;
    private JPanel bigTwoPanel;
    private JButton playButton;
    private JButton passButton;
    private JTextArea msgArea;
    private JTextArea chatArea;

    private JLabel InputboxLabel;
    private JTextField chatInput;

    private final Image[] avatars = new Image[4];
    private Image[][] cardFaces; // [rank=0][suit=0] => 'da' (i.e. Diamond A)
    private Image cardBack;

    /**
     * constructor
     * @param game the current game on show
     */
    public BigTwoGUI(BigTwo game) {

        this.game = game;
        playerList = game.getPlayerList();
        handsOnTable = game.getHandsOnTable();
        frame = new JFrame();
        bigTwoPanel = new BigTwoPanel();
        chatArea = new JTextArea(15, 20);
        chatArea.setLineWrap(true);
        chatArea.setEditable(false);
        msgArea = new JTextArea(10,40);
        msgArea.setLineWrap(true);
        msgArea.setEditable(false);
        chatInput = new JTextField( 40);
        cardFaces = new Image[13][4];

        this.setUp();
    }

/*
Do general setting up work, including creating frame, panels, labels, text fields and areas,
 as well as instantiating and registering corresponding listeners, and composing them together.
 */
    private void setUp(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Big Two Game by Uky");
        frame.setLayout(new BorderLayout());
        frame.setSize(1300,780);
        bigTwoPanel.setBackground(new Color(31, 31, 31));

        chatInput.addActionListener(new InputListener());


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.gray);
        rightPanel.setPreferredSize(new Dimension(500,800));
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.setBackground(Color.cyan);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuMain = new JMenu("Game");
        JMenuItem connect = new JMenuItem("Connect");
        JMenuItem quit = new JMenuItem("Quit");
        connect.addActionListener(new RestartMenuItemListener());
        quit.addActionListener(new QuitMenuItemListener());
        menuMain.add(connect);
        menuMain.add(quit);
        menuBar.add(menuMain);
        frame.setJMenuBar(menuBar);

        passButton = new JButton("   Pass   ");
        playButton = new JButton("   Play   ");
        String temp = "                                               ";
        InputboxLabel = new JLabel(temp + "Message: ");
        passButton.addActionListener(new PassButtonListener());
        playButton.addActionListener(new PlayButtonListener());

        bottomPanel.add(playButton);
        bottomPanel.add(passButton);
        bottomPanel.add(InputboxLabel);
        bottomPanel.add(chatInput);

        JScrollPane msgScroller = new JScrollPane(msgArea);
        JScrollPane chatScroller = new JScrollPane(chatArea);
        msgScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        msgScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        DefaultCaret caret = (DefaultCaret) msgArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        //rightPanel.add(msgArea); // sth goes wrong if I add this line
        rightPanel.add(msgScroller);
        //rightPanel.add(chatArea); // sth goes wrong if I add this line
        rightPanel.add(chatScroller);

        frame.add(bigTwoPanel, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.add(bottomPanel,BorderLayout.SOUTH);

        char[] ranksForNaming = {'a', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'j', 'q', 'k'};
        char[] suitsForNaming = {'d', 'c', 'h', 's'};

        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 4; j++) {
                Image tempCard  = new ImageIcon("src/images/" + ranksForNaming[i] + suitsForNaming[j] + ".gif").getImage();
                cardFaces[i][j] = tempCard.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH);
            }
        }
        cardBack = new ImageIcon("src/images/back.gif").getImage().getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH);
        String[] brawlers = {"jacky", "bibi", "mr.p", "sandy"};
        for (int i = 0; i < 4; i++){
            avatars[i] = new ImageIcon("src/images/"+brawlers[i]+".png").getImage().getScaledInstance(80,80,4); //scale_smooth
        }

        frame.setVisible(true);
    }

    /**
     * set the activePlayer as the specified one.
     * @param activePlayer an int value representing the index of the active player
     */
    public void setActivePlayer(int activePlayer) {
        if (activePlayer < 0 || activePlayer >= playerList.size()) {
            this.activePlayer = -1;
        } else {
            this.activePlayer = activePlayer;
        }
    }

    /**
     * Redraws the UI.
     */
    public void repaint() {
        if (game.endOfGame()){return;}
        resetSelected();
        frame.repaint();
    }

    /**
     * Prints the specified string to the msgArea of the GUI.
     *
     * @param msg the string to be printed
     */
    public void printMsg(String msg) {
        msgArea.append(msg+'\n');
    }

    public void chat(String s){
//MESSAGE: 7
        this.game.getClient().sendMessage(new CardGameMessage(7, this.game.getMyId(), s));
        chatInput.setText(null);
        /*
        chatArea.append("Player " + activePlayer +" : " + s);
        chatArea.append("\n");
        chatInput.setText(null);
         */
    }

    public void chatAppend(GameMessage msg){
        chatArea.append(this.game.getPlayerList().get(msg.getPlayerID()).getName()+": "+msg.getData());
    }

    /**
     * Clears the message area of the GUI.
     */
    public void clearMsgArea() {
        msgArea.setText("");
    }

    /**
     * Resets the GUI.
     */
    public void reset() {
        resetSelected();
        msgArea.setText("");
        enable();
    }

    /**
     * Enables user interactions.
     */
    public void enable(){
        playButton.setEnabled(true);
        passButton.setEnabled(true);
        bigTwoPanel.setEnabled(true);
    }

    /**
     * Disables user interactions.
     */
    public void disable(){
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        bigTwoPanel.setEnabled(false);
    }

    /**
     * Returns an array of indices of the cards selected through the UI.
     *
     * @return an array of indices of the cards selected, or null if no valid cards
     *         have been selected
     */
    private int[] getSelected() {

        int[] cardIdx = null;
        int count = 0;
        for (boolean b : selected) {
            if (b) {
                count++;
            }
        }

        if (count != 0) {
            cardIdx = new int[count];
            count = 0;
            for (int j = 0; j < selected.length; j++) {
                if (selected[j]) {
                    cardIdx[count] = j;
                    count++;
                }
            }
        }
        return cardIdx;
    }

    /**
     * Resets the list of selected cards to an empty list.
     */
    private void resetSelected() {
        Arrays.fill(selected, false);
    }

    /**
     * class for the BigTwoPanel, which is the main panel where the cards and avatars are.
     */
    class BigTwoPanel extends JPanel implements MouseListener {
        /**
         * register the mouse listener.
         */
        public BigTwoPanel() {
            this.addMouseListener(this);
        } // register

        /**
         * paint the cards, avatars, strings and lines to the GUI.
         * @param g the Graphics object to protect
         */
        public void paintComponent(Graphics g) {
            Graphics2D g2D = (Graphics2D) g;
            super.paintComponent(g2D); // visual artifacts appears if not calling super(), which is confusing
            for ( int playerIdx = 0; playerIdx < game.getNumOfPlayers(); playerIdx ++) {


                if(playerIdx != activePlayer){
                    g2D.setColor(Color.yellow);
                    g2D.drawString(game.getPlayerList().get(playerIdx).getName(), 15, 130 * playerIdx + 1 + 35);
                }
                else {
                    g2D.setColor(Color.red);
                    g2D.drawString(""+game.getPlayerList().get(playerIdx).getName()+" ( ACTIVATED )", 15, 130 * playerIdx + 1 + 35);

                }

                if (playerIdx != game.getMyId()){
                    for(int i = 0; i < game.getPlayerList().get(playerIdx).getNumOfCards(); i++) {
                        g2D.drawImage(cardBack, 35 * i + 150, 130 * playerIdx + 50, this);
                    }
                }
                else{
                    for (int i = 0; i < game.getPlayerList().get(playerIdx).getNumOfCards(); i++) {

                        int rank = game.getPlayerList().get(playerIdx).getCardsInHand().getCard(i).getRank();
                        int suit = game.getPlayerList().get(playerIdx).getCardsInHand().getCard(i).getSuit();

                        if (!selected[i]) {
                            g2D.drawImage(cardFaces[rank][suit], 35 * i + 150, 130 * playerIdx + 50, this);
                        } else {
                            g2D.drawImage(cardFaces[rank][suit], 35 * i + 150, 130 * playerIdx + 40, this);
                        }
                    }
                }

                g2D.setColor(Color.orange);
                g2D.setStroke(new BasicStroke(5));
                g2D.drawLine(0, 150 + 130 * playerIdx, 770, 150 + 130 * playerIdx);

                g2D.drawImage(avatars[playerIdx], 30, 130 * playerIdx + 50, this);
            }

            //drawing the last column that is the previous hand on the table column
            g2D.setColor(Color.green);
            g2D.drawString("LAST HAND :", 10, 600);
            if (!game.getHandsOnTable().isEmpty())
            {
                Hand lastHand = game.getHandsOnTable().get(game.getHandsOnTable().size() - 1);
                for( int i = 0; i < lastHand.size(); i++){
                    g2D.drawImage(cardFaces[lastHand.getCard(i).getRank()][lastHand.getCard(i).getSuit()], 150 + ((cardWidth + 5) * i), 600, this);
                }
            }
            else{
                g2D.drawString("(NONE)", 150, 600);
            }

            repaint();

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        /**
         * for (de)selecting cards.
         * @param e the event to be processed
         */
        @Override
        public void mousePressed(MouseEvent e) {
            /*
            represent for a point, with x and y coordinates.
             */
            class Point{
                public Point(int x, int y){
                    this.x = x;
                    this.y = y;
                }
                public Point(){}
                public int x, y;
            }

            /*
            represents for a rectangle area, determined by its upper left and lower right vertex.
             */
            class Area{
                public int x1, y1, x2, y2;  // diagonal line vertex of a rectangle

                /**
                 * constructor
                 * @param p1 upper left vertex
                 * @param p2 lower right vertex
                 */
                public Area(Point p1, Point p2){
                    this.x1 = p1.x;
                    this.y1 = p1.y;
                    this.x2 = p2.x;
                    this.y2 = p2.y;
                }

                /**
                 * constructor with no parameter
                 */
                public Area(){}

                /**
                 * returns if a point lies in the area
                 * @param p the point to be judged
                 * @return whether point in area
                 */
                boolean contains(Point p) {
                    return (p.x - x1) * (p.x - x2) < 0 && (p.y - y1) * (p.y - y2) < 0;
                }
            }

            Point cursor = new Point(e.getX(), e.getY());
            //printMsg("cursor got");
            //printMsg("cursor: "+cursor.x+","+cursor.y);
            //printMsg(Arrays.toString(selected));

            for (int cardIdx = 0; cardIdx < game.getPlayerList().get(activePlayer).getNumOfCards(); cardIdx++){
                boolean currentlyAtTheLastCard = cardIdx == game.getPlayerList().get(activePlayer).getNumOfCards() - 1;
                int x1 = 35 * cardIdx + 150;
                int y1 = selected[cardIdx] ? 130 * activePlayer + 40 : 130 * activePlayer + 50;
                int x2 = x1 + cardWidth;
                int y2 = y1 + cardHeight;
                int x3 = currentlyAtTheLastCard ? -1 : 35 * (cardIdx + 1) + 150;
                int y3 = currentlyAtTheLastCard ? -1 : selected[cardIdx + 1] ? 130 * activePlayer + 40 : 130 * activePlayer + 50;
                int x4 = currentlyAtTheLastCard ? -1 : x3 + cardWidth;
                int y4 = currentlyAtTheLastCard ? -1 : y3 + cardHeight;
                Point p1 = new Point(x1, y1); // the upper left vertex of the current card
                Point p2 = new Point(x2, y2); // the lower right corner of the current card
                Point p3 = new Point(x3, y3); // the upper left vertex of the next card( (-1, -1) if not exist)
                Point p4 = new Point(x4, y4); // the lower right vertex of the next card( (-1, -1) if not exist)
                //printMsg("p1: "+p1.x+", "+p1.y+"; p2: "+p2.x+", "+p2.y);
                if (new Area(p1, p2).contains(cursor) && ! new Area(p3, p4).contains(cursor)){
                    //System.out.println("selection succeed!");
                    //printMsg("select succeed!");
                    selected[cardIdx] = !selected[cardIdx];
                    break;
                }
            }

            this.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /**
     * ask the player to do things.
     */
    public void promptActivePlayer() {
        printMsg(playerList.get(activePlayer).getName() + "'s turn: ");
        resetSelected();
    }

    /**
     * input box listener
     */
    class InputListener implements ActionListener{
        /**
         * append the message to the chat box, and reset the input box
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            chat(chatInput.getText());
        }
    }

    /**
     * play button listener
     */
    class PlayButtonListener implements ActionListener{
        /**
         * to make the move (by calling makeMove)
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            game.makeMove(game.getCurrentPlayerIdx(), getSelected());
        }
    }

    /**
     * pass button listener
     */
    class PassButtonListener implements ActionListener{
        /**
         * to make the pass (by calling makeMove)
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            resetSelected();
            game.makeMove(game.getCurrentPlayerIdx(), getSelected());
        }
    }

    /**
     * restart menu listener
     */
    class RestartMenuItemListener implements ActionListener{
        /**
         * restart the game
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e) {
            System.exit(1);
        }
    }

    /**
     * quit menu listener
     */
    static class QuitMenuItemListener implements ActionListener{
        /**
         * quit the game
         * @param e the event to be processed
         */
        public void actionPerformed(ActionEvent e){
            System.exit(1);
        }
    }

}
