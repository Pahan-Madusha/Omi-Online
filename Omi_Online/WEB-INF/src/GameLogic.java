import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import org.json.simple.*;

public class GameLogic extends HttpServlet 
{
    public static int players = 0;
    public static boolean gameFull = false;
    public static String[] AllCards = getCards();
    public static String[] CurrentTrick = new String[4];
    public static int trumpSuit;
	public static int whoWonLast = 1;
	public static int trickState = 0; 
	public static int trickSuit;
	public static boolean trickStarted = false;
	public static State state;
	public static int[] scores = new int[4];    
	public static String[] names = new String[4];

    public void init() throws ServletException
    {
    	shuffle(AllCards);	
		state = State.WAIT;	// wait state
    }

	/************************************************************************/
    public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
    {

		if(players == 4)
			trickStarted = true;		

		String resp = "";
        response.setContentType("application/json");  
        PrintWriter out = response.getWriter();        

        HttpSession session = request.getSession();

        if(request.getParameter("type").equals("addPlayer")) // New player
        {
        	if(state.toString() == "WAIT")
			{
            	resp = addPlayer(request.getParameter("name"), session);
			}
            else // new player but full
            	resp = "Full!";
        }
		else if(request.getParameter("type").equals("update")) // update request from client
		{
			resp = update(session);
		}
		
        out.write(resp);
        
    }

	/***************************************************************************/
    public void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException 
    {  

    }
	
	/****************************************************************************/
    public void destroy()
    {
      // do nothing.
    }
    
	/*****************************************************************************/
    /*return suitable response to addPlayer request from client*/
    public String addPlayer(String name, HttpSession session)
    {
    	//add new player
		names[players] = name.replace("\"","");
        session.setAttribute("playerName", name);
        players++;
		session.setAttribute("playerId", players);

    	//setting up cards for player
    	String[] cards = new String[13];
    	JSONObject innerObject, mainObject;
    	JSONArray jArray = new JSONArray();
   
    	for(int i = 0; i<13; i++) // add cards to player
    	{
    		innerObject = new JSONObject();
			innerObject.put("image",AllCards[13*(players-1)+i]);
			jArray.add(innerObject);
    		cards[i] = AllCards[13*(players-1)+i];
    	}
		
		mainObject = new JSONObject();
		mainObject.put("cards",jArray); // add card list
		mainObject.put("trumpSuit", Integer.parseInt(AllCards[AllCards.length-1].charAt(6)+"")); // add trumpsuit	
		
		//store player's cards in session data
        session.setAttribute("cards", cards); // store user's card in session
		
		if(players == 4)
		{
            gameFull = true;
    		state = state.eval("ready");
			shuffle(AllCards);		
		}
		
    	return mainObject.toString().replace("\\","");
    }
	
	/********************************************************************************/
	/*returns the suitable response to polling requests*/
    public static String update(HttpSession session)
    {
		JSONObject innerObject, mainObject;
    	JSONArray jArray = new JSONArray();
		JSONArray score = new JSONArray();
		JSONArray name = new JSONArray();

		String message = "";

		mainObject = new JSONObject();

		int max = 0, maxIndex = 0;
		for(int i = 0; i<4; i++)
		{
			if(scores[i] > 9) // check if a player has won
			{
				trickStarted = false;
				mainObject.put("won",true);
				message = names[i]+" won the game!";
				
			}
			else if(Score.rounds == 13) // if all cards are played, player with max marks win
			{
				System.out.println(Score.rounds);
				for(int j = 0; j<4; j++)
				{
					if(scores[j] >max)
					{
						maxIndex = j;
						max = scores[j];
					}
				}
				trickStarted = false;
				mainObject.put("won",true);	
				message = names[maxIndex]+" won the game!";			
			}	
			else 	
				mainObject.put("won",false);				
		}
   
		if(!trickStarted) // trick not started yet
		{
			mainObject.put("cards",jArray);
			mainObject.put("shouldShowHand", false);
			mainObject.put("message", message);

    		return mainObject.toString().replace("\\","");
		}
		
		if(state.toString().equals("SCORING")) // calculate score
		{
			trickStarted = false;
			whoWonLast = (new Score(GameLogic.CurrentTrick, GameLogic.trumpSuit)).winner();
			scores[whoWonLast-1]++;
			System.out.println(whoWonLast);
			for(int i = 0; i<4; i++)
				CurrentTrick[i] = null;
			trickState = 0;
			state = state.eval("ready");
			trickStarted = true;
		}

    	for(int i = 0; i<4; i++)//
    	{
			
			if(i == (int)session.getAttribute("playerId")-1)
				continue;
    		innerObject = new JSONObject();
			if(CurrentTrick[i] != null)
				innerObject.put("image",CurrentTrick[i]);
			else
				innerObject.put("image",null);
			jArray.add(innerObject);//current cards
			score.add(scores[i]); // add scores
			name.add(names[i]); //add names
		
    	}
		/*add my card last*/
		innerObject = new JSONObject();
		innerObject.put("image",CurrentTrick[(int)session.getAttribute("playerId")-1]);
		jArray.add(innerObject); // add my cards

		score.add(scores[(int)session.getAttribute("playerId")-1]); //add scores to json
		name.add(names[(int)session.getAttribute("playerId")-1]); //add names to json


		mainObject = new JSONObject();
		mainObject.put("cards",jArray);
		mainObject.put("shouldShowHand", true);
		mainObject.put("scores",score);
		mainObject.put("names",name);

		if(state.toString().equals("SCORING") || state.toString().equals("WAIT"))
			mainObject.put("message","Waiting!");
		else		
			mainObject.put("message",names[Integer.parseInt(state.toString().charAt(8)+"")-1]+"'s chance");// chance

		mainObject.put("won",false);
    	return mainObject.toString().replace("\\","");
    }

	/***********************************************************************************/
	/*returns a string array of card names in order*/
    public static String[] getCards()
    {
    	int suit = 0;
    	int value = 1;
    	
    	String[] cards = new String[52];
    	
    	for(int i = 0; i<52; i++)
    	{
    		cards[i] = "cards/" + suit + "_" + value + ".png";
    		if(value == 13)
    		{
    			value = 0;
    			suit++;
    		}
    		value++;
    	}
    	
    	return cards;
    }
    
	/******************************************************************************/
	/*Shuffle a given string array*/
    public static String[] shuffle(String[] cards)
    {
    	String tmp;
    	int pos;
    	for(int i = 0; i<cards.length; i++)
    	{
    		pos = (int )(Math.random() * 52);;
    		tmp = cards[i];
    		cards[i] = cards[pos];
    		cards[pos] = tmp;
    	}
    	
    	return cards;
    }
    
}
