class Es1_4
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
					if(Character.isDigit(ch))
					{
						if((ch - '0') % 2 == 0)
							state = 1;
						else
							state = 2;
					}
					else if(ch == ' ')
						state = 0;
					else
						state = -1;
					break;
				case 1:
					if(Character.isDigit(ch))
					{
						if((ch - '0') % 2 == 0)
							state = 1;
						else
							state = 2;
					}
					else if(ch >= 'A' && ch <= 'K')
						state = 3;
					else if(ch == ' ')
						state = 5;
					else
						state = -1;
					break;
				case 2:
					if(Character.isDigit(ch))
					{
						if((ch - '0') % 2 == 0)
							state = 1;
						else
							state = 2;
					}
					else if(ch >= 'L' && ch <= 'Z')
						state = 4;
					else if(ch == ' ')
						state = 6;
					else
						state = -1;
					break;
				case 3:
					if(ch == ' ')
						state = 7;
					else if(Character.isLowerCase(ch))
						state = 3;
					else
						state = -1;
					break;
				case 4:
					if(ch == ' ')
						state = 8;
					else if(Character.isLowerCase(ch))
						state = 4;
					else
						state = -1;
					break;
				case 5:
					if(ch == ' ')
						state = 5;
					else if(ch >= 'A' && ch <= 'K')
						state = 3;
					else
						state = -1;
					break;
				case 6:
					if(ch == ' ')
						state = 6;
					else if(ch >= 'L' && ch <= 'Z')
						state = 4;
					else
						state = -1;
					break;
				case 7:
					if(ch == ' ')
						state = 7;
					else if(Character.isUpperCase(ch))
						state = 3;
					else
						state = -1;
				case 8:
					if(ch == ' ')
						state = 8;
					else if(Character.isUpperCase(ch))
						state = 4;
					else
						state = -1;
			}
		}
		return state == 3 || state == 4 || state == 7 || state == 8;
	}

	public static void main(String[] args)
	{
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}