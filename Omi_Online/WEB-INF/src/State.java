enum State 
{
	WAIT
	{
		State eval(String command) 
		{ 
			if(command.equals("ready"))		
				return CHANCE_P1; 
			return CHANCE_P1;	
		}
	}
	,CHANCE_P1
	{
		State eval(String command) 
		{
			if(command.equals("cardPlayed") && GameLogic.trickState == 3)
			{
				GameLogic.trickState = 0;		
		 		return SCORING; 
			}
			else if(command.equals("cardPlayed"))
				return CHANCE_P2;
			else
				return CHANCE_P1;
		}
	} 
	,CHANCE_P2
	{
		State eval(String command) 
		{ 
			if(command.equals("cardPlayed") && GameLogic.trickState == 3)		
		 	{
				GameLogic.trickState = 0;		
		 		return SCORING; 
			}
			else if(command.equals("cardPlayed"))
				return CHANCE_P3;
			else
				return CHANCE_P2;
		}
	}
	,CHANCE_P3
	{
		State eval(String command) 
		{ 
			if(command.equals("cardPlayed") && GameLogic.trickState == 3)		
		 	{
				GameLogic.trickState = 0;		
		 		return SCORING; 
			}
			else if(command.equals("cardPlayed"))
				return CHANCE_P4;
			else
				return CHANCE_P3;
		}
	}
	,CHANCE_P4
	{
		State eval(String command) 
		{ 
			if(command.equals("cardPlayed") && GameLogic.trickState == 3)		
		 	{
				GameLogic.trickState = 0;		
		 		return SCORING; 
			} 
			else if(command.equals("cardPlayed"))
				return CHANCE_P1;
			else
				return CHANCE_P4;
		}
	}
	,SCORING
	{
		State eval(String command) 
		{ 
			if(command.equals("ready"))
			{
				if(GameLogic.whoWonLast == 1)					
					return CHANCE_P1;
				else if(GameLogic.whoWonLast == 2)
					return CHANCE_P2;
				else if(GameLogic.whoWonLast == 3)
					return CHANCE_P3;
				else if(GameLogic.whoWonLast == 4)
					return CHANCE_P4;
				else
					return SCORING;
			}
			else
				return SCORING;
		}
	} ;
	abstract State eval(String command);

    public String toString()
	{
        switch(this)
		{
	        case WAIT :
		        return "WAIT";
		    case CHANCE_P1 :
		        return "CHANCE_P1";
		     case CHANCE_P2 :
		        return "CHANCE_P2";
 			case CHANCE_P3 :
		        return "CHANCE_P3"; 
			case CHANCE_P4 :
		        return "CHANCE_P4";
			 case SCORING :
		        return "SCORING";
        }
        return null;
    }
}




