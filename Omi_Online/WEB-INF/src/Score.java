public class Score
{
	public String[] trick;
	public int trumpSuit;
	public int trickSuit;
	public static int rounds = 0;

	public Score(String[] trick, int trump)
	{
		this.trick = trick;
		this.trumpSuit = trump;	
		rounds++;
	}

	/*find winner id*/
	public int winner()
	{
		int[][] cards = new int[4][2];
		int winner = 0;
		int winVal = 2;
		boolean hasTrump = false;

		for(int i = 0; i<4; i++)
		{
			cards[i][0] = Integer.parseInt(trick[i].charAt(6)+"");

			if(trick[i].charAt(9) == '.')
				cards[i][1] = Integer.parseInt(trick[i].replace("\"","").charAt(8)+"");
			else
				cards[i][1] = Integer.parseInt(trick[i].replace("\"","").charAt(8)+trick[i].replace("\"","").charAt(9)+"");	//cards > 9			
			
			if(cards[i][0] == trumpSuit)// trumpsuit
			{
				hasTrump = true;
				if(cards[i][1] == 1) // trump ace
					return i+1;
				
				if(winVal <= cards[i][1])
				{
					winVal = cards[i][1];
					winner = i;
				}
			}
		}

		if(hasTrump) // if trick has trump suit cards
			return winner+1;

		for(int i = 0; i<4; i++)
		{
			if(cards[i][0] == GameLogic.trickSuit)
			{
				if(cards[i][1] == 1) 
					return i+1;
				
				if(winVal <= cards[i][1])
				{
					winVal = cards[i][1];
					winner = i;
				}
				
			}
		}
		return winner+1;

	}

}
