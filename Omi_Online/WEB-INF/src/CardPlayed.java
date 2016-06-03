import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import org.json.simple.*;

public class CardPlayed extends HttpServlet 
{
    
    public void init() throws ServletException
    {
    	
    }

	/************************************************************************/
    public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
    {

		if(GameLogic.players == 4)
			GameLogic.trickStarted = true;		

		String resp = "";
        response.setContentType("application/json");  
        PrintWriter out = response.getWriter();        

        HttpSession session = request.getSession();

     
		resp = cardPlayed(request.getParameter("card"), session);
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
    
	
    /***************************************************************************/
	/*return suitable response to cardPlayed request from client*/
    public String cardPlayed(String card, HttpSession session)
    {
		JSONObject innerObject, mainObject;
    	JSONArray jArray = new JSONArray();
   		mainObject = new JSONObject();
    
		//wait or scoring state:invalid
		if(GameLogic.state.toString().equals("WAIT") || GameLogic.state.toString().equals("SCORING")) // card play is not valid
		{
			mainObject.put("valid", false); 
			System.out.println("wait or scoring");
			return mainObject.toString().replace("\\","");
		}

		//if not start of trick > check if trump or tricksuit
		if(GameLogic.trickState != 0) 
			if(!isCardValid(card, session))
			{
				mainObject.put("valid", false);
				System.out.println("invalid card");
				return mainObject.toString().replace("\\","");
			}

		//my chance
		if(GameLogic.state.toString().contains( "" + (int)session.getAttribute("playerId") ) )
		{
			mainObject.put("valid", true);
			GameLogic.state = GameLogic.state.eval("cardPlayed");


			if(GameLogic.trickState == 0)
				GameLogic.trickSuit = Integer.parseInt(card.replace("\"","").charAt(6)+""); // set tricksuit for current trick
			GameLogic.trickState++;

		}
		else // not my chance
		{
			mainObject.put("valid", false);
			return mainObject.toString().replace("\\","");
		}
		
        GameLogic.CurrentTrick[(int)session.getAttribute("playerId")-1] = card.replace("\"","");

        String[] cards = (String[])session.getAttribute("cards");
        if(cards == null)
            return "NULLLL";
        
    	for(int i = 0; i<13; i++) // add my cards to json
    	{
    		innerObject = new JSONObject();
    		if(!(card.replace("\"","")).equals(cards[i]) && !cards[i].equals(""))
    		{
			    innerObject.put("image",cards[i]);
			    jArray.add(innerObject);
			}    
			else
			    cards[i] = "";
			
    	}
		session.setAttribute("cards",cards);		

		mainObject.put("cards",jArray);
 
    	return mainObject.toString().replace("\\","");
    	
    }


	/*check if card played is a valid card to play*/
	boolean isCardValid(String card, HttpSession session)
	{
		String[] myCards = (String[])session.getAttribute("cards"); // get my cards from session
		boolean hasTrickSuit = false; 

		for(int i = 0; i<13; i++)
		{
			if(!myCards[i].equals(""))
			{
				if( Integer.parseInt(myCards[i].replace("\"","").charAt(6)+"") == GameLogic.trickSuit) // if player has trick suit cards
				{
					hasTrickSuit = true;
					break;
				}
			}
		}
		
		if(hasTrickSuit) // if player has tricksuit and played other card
		{
			if(Integer.parseInt(card.replace("\"","").charAt(6)+"") != GameLogic.trickSuit)
				return false;
		}
		return true;
	}
    
}
