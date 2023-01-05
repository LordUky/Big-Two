import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BigTwoClient implements NetworkGame{
     private BigTwo game;
     private BigTwoGUI gui;
     private Socket sock;
     private ObjectOutputStream oos;
     private ObjectInputStream ois;
     private int playerID;
     private String playerName;
     private String serverIP;
     private int serverPort = 2396;

     /**
      * constructor
      * @param game game reference
      * @param gui GUI
      */
     public BigTwoClient(BigTwo game, BigTwoGUI gui){
          this.game = game;
          this.gui = gui;
          gui.disable();
          gui.repaint();
     }

     /**
      * to get playerID
      * @return playerID
      */
     public int getPlayerID(){
         return this.playerID;
     }

     /**
      * set the playerID
      * @param playerID the ID to set
      * the playerID (index) of the local player.
      */
     public void setPlayerID(int playerID){
         this.playerID = playerID;
     }

     /**
      * get the player name
      * @return return playername
      */
     public String getPlayerName(){
          return this.playerName;
     }

     /**
      * set the player name
      * @param playerName the name of the local player
      */
     public void setPlayerName(String playerName){
          this.playerName = playerName;
     }

     /**
      * get the server IP
      * @return
      */
     public String getServerIP(){
          return this.serverIP;
     }

     /**
      * set the IP
      * @param serverIP
      *            the IP address of the server
      */
     public void setServerIP(String serverIP){
          this.serverIP = serverIP;
     }

     /**
      * return "2396"
      * @return
      */
     public int getServerPort(){
          return this.serverPort;
     }

     /**
      * the port is 2396!
      * @param serverPort
      *            the TCP port of the server
      */
     @Override
     public void setServerPort(int serverPort) {
          System.out.println("Server port is 2396!!!");
          this.serverPort = 2396;
     }

     /**
      * connect to the server
      */
     public void connect(){
          this.setPlayerName(JOptionPane.showInputDialog("Name:", "Player name"));
          this.setServerIP("127.0.0.1");
          try{
               this.sock = new Socket(this.serverIP, this.serverPort);
               this.oos = new ObjectOutputStream(this.sock.getOutputStream());
               System.out.println(playerName+" connected!");

               ServerHandler sh = new ServerHandler();
               Thread thread = new Thread(sh);
               thread.start();
//MESSAGE: 1
               sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, this.playerName));
//MESSAGE: 4
               sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
          }catch(IOException ex){
               ex.printStackTrace();
          }

     }

     /**
      * identify the message and do corresponding things
      * @param message
      *            the specified message received from the server
      */
     public synchronized void parseMessage(GameMessage message){
          if (message.getType() == 0) {
               if (message.getData() != null)
               {
                    String[] playerNames = (String[]) message.getData();
                    for(int i = 0; i < 4; i++)
                    {
                         this.game.getPlayerList().get(i).setName(playerNames[i]);
                    }
               }
               gui.repaint();
               gui.setActivePlayer(playerID);
          }
          else if (message.getType() == 1)
          {
               //this.gui.printMsg("message type 1 received\n");
               this.game.setNumOfPlayers(Math.max(this.game.getNumOfPlayers(), message.getPlayerID() + 1));
               if (message.getData() != null)
               {
                    this.game.getPlayerList().get(message.getPlayerID()).setName((String)message.getData());
                    if (game.getMyId() == -1){
                         this.game.setMyId(message.getPlayerID());
                    }
                    //this.gui.printMsg("current numofplayers:"+this.game.getNumOfPlayers()+", to set:"+Math.max(this.game.getNumOfPlayers(), message.getPlayerID() + 1)+", myID: "+this.game.getMyId());

               }
          }
          else if (message.getType() == 2)
          {
               gui.printMsg("This game has 4 players already!!!.");
          }
          else if (message.getType() == 3)
          {
               this.game.getPlayerList().get(message.getPlayerID()).setName("");
               gui.disable();
               if (!this.game.endOfGame()){
                    gui.printMsg(message.getData() + " has left the game.");
                    gui.printMsg("Waiting for new players to join......");
                    this.sendMessage(new CardGameMessage(4, -1, null));
               }
          }
          else if (message.getType() == 4) {
               gui.printMsg(this.game.getPlayerList().get(message.getPlayerID()).getName() + " is ready.\n");
          }
          else if (message.getType() == 5) {
               this.game.start((BigTwoDeck)message.getData());
          }
          else if (message.getType() == 6) {
               this.game.checkMove(message.getPlayerID(), (int[]) message.getData());
               gui.disable();
               if (this.game.getMyId() == this.game.getCurrentPlayerIdx()) {
                    gui.enable();
                    gui.printMsg("Your turn\n");
               } else {
                    gui.printMsg(this.game.getPlayerList().get(this.game.getCurrentPlayerIdx()).getName() + "'s turn.\n");
               }
          }
          else if (message.getType() == 7) {
               gui.chatAppend(message);
          }
          else {
               System.out.println("message not valid");
          }
     }

     /**
      * send the message to server
      * @param message
      *            the specified message to be sent the server
      */
     public void sendMessage(GameMessage message){
          try
          {
               oos.writeObject(message);
          }
          catch (Exception ex)
          {
               ex.printStackTrace();
          }
     }

     private class ServerHandler implements Runnable{
          public ServerHandler(){
               try {
                    ois = new ObjectInputStream(sock.getInputStream());
               }catch (IOException e){
                    System.out.println("Exception in ServerHandler's constructor!!");
                    e.printStackTrace();
               }
          }
          @Override
          public void run() {
               try{
                    while (true){
                         CardGameMessage messageIn = (CardGameMessage)ois.readObject();
                         if (messageIn != null){
                              parseMessage(messageIn);
                         }
                    }
               } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Exception in run method of ServerHandler!!");
                    throw new RuntimeException(e);
               }
          }
     }

}









