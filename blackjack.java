import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
// Chase Freckmann
// Discrete Mathematics Final Project Blackjack Simulator
/*
 * This BlackJack Simulator Helps Demonstrate The Impact Of Probability Within Gambling
 * It Automatically Updates The Probability of both the Player and Dealer Busting Throughout the Match
 * This is Updated Alongside the Probability of Both Sides Hitting A JackPot.
 * 
 * To Further Show Probability Outside of the Already Throughly Calculated BlackJack Value of 21,
 * This Simulator Allows You To Set BlackJack Values From 0 - 100
 * 
 * This Code Does Not Feature Exception Handling For Every Case
 * The Simulator Has Also Fixated Aces to 11 As The Multivariable Usage of Aces Gets Tricky When
 * Dealing With Larger BlackJack Cases.
 * 
 * 
 * 	Anyways, hope you enjoy using this simulator, it was a lot of fun to make, just be careful
 * with developing any gambling addictions!
 * 
 * - Chase
 */
public class Blackjack
{
	public static void main(String [] args)
	{
		while(true) {
			int blackjack;
			Scanner in = new Scanner(System.in);
			System.out.println("WELCOME TO THE BLACK JACK PROBABILITY SIMULATOR!");
			System.out.print("Would You Like to Simulate A Game of Blackjack? (Y/N): ");
			String choice = in.next();
			if(choice.equalsIgnoreCase("Y"))
			{
				System.out.println("\n<<WARNING: VALUES BELOW 21 CAN RESULT IN INSTANT LOSSES / WINS>>");
				System.out.print("Pick A Number Value to Represent BlackJack (0-100): ");
				{
					blackjack = in.nextInt();
					runGame(blackjack);
				}
			}
			else if(choice.equalsIgnoreCase("N"))
			{
				System.out.println("Thank you for use this simulation c;");
				break;
			}
			else
			{
				System.out.println("Invalid Choice...");
				break;
			}
		}
		
	}
	// This Function Draws a Random Card, and Sets the Card Value to True
	public static Card drawCard(Card[] deck)
	{
		Random rand = new Random();
		int randNum = rand.nextInt(0,52);
		while(deck[randNum].getUsed() == true)
		{
			randNum = rand.nextInt(0,52);
		}
		deck[randNum].setUsed(true);
		return deck[randNum];
	}
	
	public static void runGame(int blackJackVal)
	{
		Scanner in = new Scanner(System.in);
		Card[] deck = createDeck();
		ArrayList<Card> playerDeck = new ArrayList<Card>();
		ArrayList<Card> dealerDeck = new ArrayList<Card>();
		playerDeck.add(drawCard(deck));// Creates initial Deck Values
		dealerDeck.add(drawCard(deck));
		playerDeck.add(drawCard(deck));
		dealerDeck.add(drawCard(deck));
		boolean playerStand = false, dealerStand = false;
		while(true)
		{
			printHands(playerDeck, dealerDeck);
			// First Check if Instant Loss On Deal / BlackJacks / Ties
			if(playerStand == true && dealerStand == true)
			{
				if(getHandSum(playerDeck) == getHandSum(dealerDeck))
				{
					System.out.println("You Tied! Both You And The Dealer Have The Same Value!");
					break;
				}
				else if(getHandSum(playerDeck) > getHandSum(dealerDeck))
				{
					System.out.println("You Won! You Have A Higher Card Value Than The Dealer!");
					break;
				}
				else if(getHandSum(playerDeck) < getHandSum(dealerDeck))
				{
					System.out.println("You Lost! You Have A Worse Hand Than The Dealer!");
					break;
				}
			}
			if(getHandSum(playerDeck) > blackJackVal && getHandSum(dealerDeck) > blackJackVal)
			{
				System.out.println("You Tied! Both You And The Dealer Busted");
				break;
			}
			else if(getHandSum(playerDeck) == blackJackVal && getHandSum(dealerDeck) == blackJackVal)
			{
				System.out.println("You Tied! Both You And The Dealer Got BlackJack!");
				break;
			}
			else if(getHandSum(playerDeck) == blackJackVal)
			{
				System.out.println("You've Won By Hitting BlackJack!");
				break;
			}
			else if(getHandSum(dealerDeck) == blackJackVal)
			{
				System.out.println("You've Lost! The Dealer Hit BlackJack!");
				break;
			}
			else if(getHandSum(playerDeck) > blackJackVal)
			{
				System.out.println("You Busted! Your Card Values Are Too High!");
				break;
			}
			else if(getHandSum(dealerDeck) > blackJackVal)
			{
				System.out.println("You've Won! The Dealer Busted!");
				break;
			}
			double[] probabilities = getJackBustProb(deck, playerDeck, dealerDeck, blackJackVal);
			printProbabilities(probabilities);
			if(playerStand == false) // Player Cannot Choose When Standing
			{
				printMenu(blackJackVal);
				int choice = in.nextInt();
				if(choice == 1)// hit
				{
					playerDeck.add(drawCard(deck));
				}
				else if(choice == 2)// stand
				{
					playerStand = true;
				}
			}
			if(getHandSum(playerDeck) > blackJackVal)
			{
				System.out.println("You Busted! Your Card Values Are Too High!");
				break;
			}
			// Dealer Logic -- Hits When Bust Probability is 66 or below... otherwise stands
			if(dealerStand == false)
			{
				if(probabilities[3] < 66)
				{
					dealerDeck.add(drawCard(deck));
					System.out.println("The Dealer Has Decided to Hit!");
				}
				else
				{
					dealerStand = true;
					System.out.println("The Dealer Has Decided to Stand!");
				}
			}
		}
		
	}
	public static double[] getJackBustProb(Card[] deck, ArrayList<Card> player, ArrayList<Card> dealer, int blackJack)
	{
		double[] prob = {0,0,0,0};
		int cardCount = 0, plrBreakCount = 0, plrJackCount = 0, playerCnt = getHandSum(player);
		int dealerBreakCount = 0, dealerJackCount = 0, dealerCnt = getHandSum(dealer);
		System.out.println("The Player's Card Value Is: " + playerCnt);
		System.out.println("The Dealer's Card Value Is: " + dealerCnt);
		for(int i = 0; i < 52; i++)
		{
			if(deck[i].getUsed() == false)
			{
				cardCount++;
				int cardVal = deck[i].getValue();
				if(playerCnt + cardVal == blackJack)// Checks if Player hits BlackJack
					plrJackCount++;
				if(playerCnt + cardVal > blackJack)// Checks if Players busts
					plrBreakCount++;
				if(dealerCnt + cardVal == blackJack)// Checks if Dealer hits BlackJack
					dealerJackCount++;
				if(dealerCnt + cardVal > blackJack)// Checks if Dealer hits BlackJack
					dealerBreakCount++;
			}
		}
		prob[0] = (plrJackCount / (double)cardCount) * 100;
		prob[1] = (plrBreakCount / (double)cardCount) * 100;
		prob[2] = (dealerJackCount / (double)cardCount) * 100;
		prob[3] = (dealerBreakCount / (double)cardCount) * 100;
		return prob;
	}
	public static void printProbabilities(double[] probabilities)
	{
		System.out.printf("\tPLAYER PROBABILITIES\n");
		System.out.printf("Black Jack Probability: %24.2f\n", probabilities[0]);
		System.out.printf("Bust Probability: %30.2f\n\n", probabilities[1]);
		System.out.printf("\tDEALER PROBABILITIES\n");
		System.out.printf("Black Jack Probability: %24.2f\n", probabilities[2]);
		System.out.printf("Bust Probability: %30.2f\n\n", probabilities[3]);
	}
	public static int getHandSum(ArrayList<Card> hand)
	{
		int handSum = 0;
		for(int i = 0; i < hand.size(); i++)
		{
			handSum += hand.get(i).getValue();
		}
		return handSum;
	}
	public static void printHands(ArrayList<Card> plr, ArrayList<Card> deal)
	{
		System.out.println("\n\t<<PLAYER'S CARDS>>:\n");
		int plrSize = plr.size(); 
		for(int i = 0; i < plrSize; i++)
			System.out.print("|-_-_-|");
		System.out.println();
		for(int i = 0; i < plrSize; i++)
			System.out.print("|     |");
		System.out.println();
		for(int i = 0; i < plrSize; i++) {
			String s = getCode(plr.get(i));
			System.out.printf("|%s|", s);
		}
		System.out.println();
		for(int i = 0; i < plrSize; i++)
			System.out.print("|     |");
		System.out.println();
		for(int i = 0; i < plrSize; i++)
			System.out.print("|-_-_-|");
		System.out.println("\n\n\t<<DEALER'S CARDS>>:\n");
		int dealSize = deal.size(); 
		for(int i = 0; i < dealSize; i++)
			System.out.print("|-_-_-|");
		System.out.println();
		for(int i = 0; i < dealSize; i++)
			System.out.print("|     |");
		System.out.println();
		for(int i = 0; i < dealSize; i++) {
			String s = getCode(deal.get(i));
			System.out.printf("|%s|", s);
		}
		System.out.println();
		for(int i = 0; i < dealSize; i++)
			System.out.print("|     |");
		System.out.println();
		for(int i = 0; i < dealSize; i++)
			System.out.print("|-_-_-|");
		System.out.println("\n");
	}
	public static String getCode(Card c)
	{
		String val;
		if(c.getValue() == 10 && (c.getName().charAt(0) == 'J' || c.getName().charAt(0) == 'Q' || c.getName().charAt(0) == 'K'))
			val = c.getName().charAt(0) + "";
		else if(c.getValue() == 11)
			val = c.getName().charAt(0) + "";
		else
			val = c.getValue() + "";
		if(val.length() < 2)
			val = val + " ";
		String ret = " " + val + c.getSuit().charAt(0) + " ";
		return ret;
	}
	public static void printMenu(int blackjack)
	{
		System.out.println("\tBLACKJACK OPTION MENU");
		System.out.println("\t---------------------");
		System.out.printf("Current Blackjack Value: %d\n", blackjack);
		System.out.println("----------");
		System.out.println("[1]: HIT");
		System.out.println("[2]: STAND");
		System.out.println("----------");
		System.out.print("Enter Choice: ");
	}
	// This Function Creates a Deck of Cards to Simulate Black Jack
	public static Card[] createDeck()
	{
		Card[] deck =
		{
			new Card("Two", 2, "Heart", false),
			new Card("Two", 2, "Club", false),
			new Card("Two", 2, "Spade", false),
			new Card("Two", 2, "Diamond", false),
			new Card("Three", 3, "Heart", false),
			new Card("Three", 3, "Club", false),
			new Card("Three", 3, "Spade", false),
			new Card("Three", 3, "Diamond", false),
			new Card("Four", 4, "Heart", false),
			new Card("Four", 4, "Club", false),
			new Card("Four", 4, "Spade", false),
			new Card("Four", 4, "Diamond", false),
			new Card("Five", 5, "Heart", false),
			new Card("Five", 5, "Club", false),
			new Card("Five", 5, "Spade", false),
			new Card("Five", 5, "Diamond", false),
			new Card("Six", 6, "Heart", false),
			new Card("Six", 6, "Club", false),
			new Card("Six", 6, "Spade", false),
			new Card("Six", 6, "Diamond", false),
			new Card("Seven", 7, "Heart", false),
			new Card("Seven", 7, "Club", false),
			new Card("Seven", 7, "Spade", false),
			new Card("Seven", 7, "Diamond", false),
			new Card("Eight", 8, "Heart", false),
			new Card("Eight", 8, "Club", false),
			new Card("Eight", 8, "Spade", false),
			new Card("Eight", 8, "Diamond", false),
			new Card("Nine", 9, "Heart", false),
			new Card("Nine", 9, "Club", false),
			new Card("Nine", 9, "Spade", false),
			new Card("Nine", 9, "Diamond", false),
			new Card("Ten", 10, "Heart", false),
			new Card("Ten", 10, "Club", false),
			new Card("Ten", 10, "Spade", false),
			new Card("Ten", 10, "Diamond", false),
			new Card("Jack", 10, "Heart", false),
			new Card("Jack", 10, "Club", false),
			new Card("Jack", 10, "Spade", false),
			new Card("Jack", 10, "Diamond", false),
			new Card("Queen", 10, "Heart", false),
			new Card("Queen", 10, "Club", false),
			new Card("Queen", 10, "Spade", false),
			new Card("Queen", 10, "Diamond", false),
			new Card("King", 10, "Heart", false),
			new Card("King", 10, "Club", false),
			new Card("King", 10, "Spade", false),
			new Card("King", 10, "Diamond", false),
			new Card("Ace", 11, "Heart", false),
			new Card("Ace", 11, "Club", false),
			new Card("Ace", 11, "Spade", false),
			new Card("Ace", 11, "Diamond", false),
		};
		return deck;
	}
}
// Card Class
class Card
{
	private String name;
	private int value;
	private String suit;
	private boolean used;
	public Card(String name, int value, String suit, boolean used)// Card Constructor
	{
		this.name = name;
		this.value = value;
		this.suit = suit;
		this.used = used;
	}
	// Getter Functions
	public String getName()
	{
		return name;
	}
	public int getValue()
	{
		return value;
	}
	public String getSuit()
	{
		return suit;
	}
	public boolean getUsed()
	{
		return used;
	}
	// Setter Function
	public void setUsed(boolean used)
	{
		this.used = used;
	}
}