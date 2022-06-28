class Es1_11
{
	public static boolean scan(String s)
	{
		int i = 0;
		int state = 0;
		while(state >= 0 && i < s.length())
		{
			char ch = s.charAt(i++);
			switch(state)
			{
				case 0:
					if(ch == 'a' || ch == '*')
						state = 2;
					else if(ch == '/')
						state = 1;
					else
						state = -1;
					break;
				case 1:
					if(ch == '*')
						state = 3;
					else if(ch == 'a')
						state = 2;
					else
						state = -1;
					break;
				case 2:
					if(ch == '/')
						state = 1;
					else if(ch != 'a' && ch != '*')
						state = -1;
					break;
				case 3:
					if(ch == 'a')
						state = 4;
					else if(ch == '*')
						state = 5;
					else
						state = -1;
					break;
				case 4:
					if(ch == '*')
						state = 5;
					else if(ch != 'a')
						state = -1;
					break;
				case 5:
					if(ch == '/')
						state = 6;
					else if(ch == 'a')
						state = 4;
					else if(ch != '*')
						state = -1;
					break;
				case 6:
					if(ch == '/')
						state = 7;
					else if(!(ch == 'a' || ch == '*'))
						state = -1;
					break;
				case 7:
					if(ch == 'a')
						state = 6;
					else if(ch == '*')
						state = 3;
					else
						state = -1;
					break;
			}
		}
		return state == 2 || state == 6 || state == 7;
	}

	public static void main(String[] args)
	{
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}