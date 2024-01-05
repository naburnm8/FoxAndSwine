
import java.util.*;

class DefaultV{
    public static int WinCondition = 3;
    public static int StartCards = 4;
    public static String[] Types = {"SWAWS", "AMOGUS", "SUS", "ZAVA", "MEOW"};
    public static boolean isTypeLegal (String type){
        for (int i = 0; i < Types.length; i++){
            if(type.equals(Types[i])){
                return true;
            }
        }
        return false;
    }
    public static String Foe_Name = "Swine";
}
abstract class Card_Deck {
    protected HashMap<String, Integer> Deck = new HashMap<>();
    protected String user = new String();
    public boolean hasCard (String card){
        for (String k : Deck.keySet()) {
            if (card.equals(k)){
                return true;
            }
        }
        return false;
    }
    public void addToDeck(String[] cards){
        for (int i = 0; i < cards.length; i++){
            if (hasCard(cards[i])){
                Deck.put(cards[i], Deck.get(cards[i]) + 1);
            }
            else{
                Deck.put(cards[i], 1);
            }
        }

    }
    public void addToDeck(String card){
        if (hasCard(card)){
            Deck.put(card, Deck.get(card) + 1);
        }
        else{
            Deck.put(card, 1);
        }
    }
    public void removeFromDeck (String card){
        Deck.remove(card);
    }
    abstract String pickCard();
    Card_Deck(String username, String[] cards){
        user = username;
        addToDeck(cards);
    }
    public void printDeck(){
        System.out.println(user + "'s current deck: ");
        for(String k: Deck.keySet()){
            System.out.print(k + ": " + Deck.get(k) + " ");
        }
    }
    public boolean isWinner(){
        return Deck.isEmpty();
    }
    public int getFromDeck(String card){
        if (hasCard(card)){
            return Deck.get(card);
        }
        else{
            return -1;
        }
    }
}

class Deck_Player extends Card_Deck{
    Deck_Player(String username, String[] cards) {
        super(username, cards);
    }

    public String pickCard(){
        Scanner player_stream = new Scanner(System.in);
        String chosenCard = player_stream.next();
        boolean isLegal = DefaultV.isTypeLegal(chosenCard);
        if (!isLegal){
            return "failure";
        }
        return chosenCard;
    }
}
class Deck_Foe extends Card_Deck{
    Deck_Foe(String username, String[] cards) {
        super(username, cards);
    }
    public String pickCard(){
        Random rand = new Random();
        int factor = rand.nextInt(1, 1000);
        if (factor % 2 == 0){
            return findMax();
        }
        else{
            return findMin();
        }
    }
    private String findMax(){
        int maxQ = -1;
        String chosen = new String();
        for(String k: Deck.keySet()){
            if (Deck.get(k) > maxQ){
                maxQ = Deck.get(k);
                chosen = k;
            }
        }
        return chosen;
    }
    private String findMin(){
        int minQ = DefaultV.WinCondition + 1000;
        String chosen = new String();
        for(String k: Deck.keySet()){
            if (Deck.get(k) < minQ){
                minQ = Deck.get(k);
                chosen = k;
            }
        }
        return chosen;
    }
}
class Game_Handler {
    private Deck_Foe Swine;
    private Deck_Player Player;
    Game_Handler(String playername){
        Random gen = new Random();
        String[] cards = new String[DefaultV.StartCards];
        String[] cards1 = new String[DefaultV.StartCards];
        for (int i = 0; i < DefaultV.StartCards; i++) {
            int pos = gen.nextInt(0, DefaultV.Types.length - 1);
            cards[i] = DefaultV.Types[pos];
        }
        for (int i = 0; i < DefaultV.StartCards; i++) {
            int pos = gen.nextInt(0, DefaultV.Types.length - 1);
            cards1[i] = DefaultV.Types[pos];
        }
        Player = new Deck_Player(playername, cards);
        Swine = new Deck_Foe(DefaultV.Foe_Name, cards1);
        System.out.println("Current game rules are: ");
        System.out.println("Initial cards: " + DefaultV.StartCards + ", winning cards: " +
                DefaultV.WinCondition + ", legal card types: ");
        for(String k: DefaultV.Types){
            System.out.print(k + " ");
        }
        System.out.println();
    }
    public boolean Player_Turn(){
        Player.printDeck();
        System.out.println("Choose a card: ");
        String chosencard = Player.pickCard();
        if (chosencard.equals("failure")){
            return true;
        }
        boolean foeHasCard = Swine.hasCard(chosencard);
        if(!foeHasCard){
            System.out.println("Swing and a miss! Pick a card!");
            Random gen = new Random();
            int pos = gen.nextInt(0, DefaultV.Types.length - 1);
            Player.addToDeck(DefaultV.Types[pos]);
            return true;
        }
        System.out.println("ARGH! Take my " + Swine.getFromDeck(chosencard) + " " + chosencard);
        int qfoe = Swine.getFromDeck(chosencard);
        for (int i = 0; i < qfoe; i++){
            Player.addToDeck(chosencard);
        }
        Swine.removeFromDeck(chosencard);
        return false;
    }
    public boolean Foe_Turn(){
        //Player.printDeck();
        System.out.println(DefaultV.Foe_Name + " chooses a card...");
        String chosencard = Swine.pickCard();
        System.out.println(DefaultV.Foe_Name + " chose " + chosencard);
        //if (chosencard.equals("failure")){
          //  return true;
        //}
        boolean plrHasCard = Player.hasCard(chosencard);
        if(!plrHasCard){
            System.out.println(DefaultV.Foe_Name + " picks a card from the deck!");
            Random gen = new Random();
            int pos = gen.nextInt(0, DefaultV.Types.length - 1);
            Swine.addToDeck(DefaultV.Types[pos]);
            return true;
        }
        System.out.println(DefaultV.Foe_Name + " takes your " + Player.getFromDeck(chosencard) +
                " " + chosencard);
        int qplr = Player.getFromDeck(chosencard);
        for (int i = 0; i < qplr; i++){
            Swine.addToDeck(chosencard);
        }
        Player.removeFromDeck(chosencard);
        return false;
    }
    public int After_Turn_Checks(){
        //remove gr or eq to win
        for (int i = 0; i < DefaultV.Types.length; i++){
            int PgotFromDeck = Player.getFromDeck(DefaultV.Types[i]);
            int FgotFromDeck = Swine.getFromDeck(DefaultV.Types[i]);
            if (PgotFromDeck >= DefaultV.WinCondition){
                Player.removeFromDeck(DefaultV.Types[i]);
            }
            if (FgotFromDeck >= DefaultV.WinCondition){
                Swine.removeFromDeck(DefaultV.Types[i]);
            }
        }
        if (Player.isWinner()){
            return 1;
        } else if (Swine.isWinner()) {
            return 2;
        }
        return 0;
    }
}


public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Fox and Swine! State your nickname: ");
        Scanner stream = new Scanner(System.in);
        String user = stream.next();
        Game_Handler game1 = new Game_Handler(user);
        System.out.println("Type go to proceed");
        stream.next();
        int won = 0;
        int turn = 1;
        while (won == 0){
            if (turn == 1){
                boolean SWITCH = game1.Player_Turn();
                if (SWITCH){
                    turn = 2;
                }
            } else if (turn == 2) {
                boolean SWITCH = game1.Foe_Turn();
                if (SWITCH){
                    turn = 1;
                }
            }
            won = game1.After_Turn_Checks();
            if (won == 1){
                System.out.println( user + " wins!");
            } else if (won == 2) {
                System.out.println(DefaultV.Foe_Name + " wins.");
            }
        }
    }
}