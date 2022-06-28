class Es1_5
{
	public static boolean scan(String s)
	{
		int state = 0;
		int i = 0;
		while(state >= 0 && i < s.length())
		{
			char ch = s.charAt(i++);
			switch(state)
			{
				case 0:
					if(ch >= 'A' && ch <= 'K')
						state = 1;
					else if(ch >= 'L' && ch <= 'Z')
						state = 2;
					else
						state = -1;
					break;
				case 1:
					if(Character.isLowerCase(ch))
						state = 1;
					else if(Character.isDigit(ch))
					{
						if((ch - '0') % 2 == 0)
							state = 3;
						else
							state = 5;
					}
					else
						state = -1;
					break;
				case 2:
					if(Character.isLowerCase(ch))
						state = 2;
					else if(Character.isDigit(ch))
					{
						if((ch - '0') % 2 == 0)
							state = 6;
						else
							state = 4;
					}
					else
						state = -1;
					break;
				case 3:
					if(Character.isDigit(ch))
					{
						if((ch - '0') % 2 == 1)
							state = 5;
					}
					else
						state = -1;
					break;
				case 4:
					if(Character.isDigit(ch))
					{
						if((ch - '0') % 2 == 0)
							state = 6;
					}
					else
						state = -1;
					break;
				case 5:
					if(Character.isDigit(ch))
					{
						if((ch - '0') % 2 == 0)
							state = 3;
					}
					else
						state = -1;
					break;
				case 6:
					if(Character.isDigit(ch))
					{
						if((ch - '0') % 2 == 1)
							state = 4;
					}
					else
						state = -1;
					break;
			}
		}
		return state == 3 || state == 4;
	}

	public static void main(String[] args)
	{
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}