class Es1_9
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
					state = (ch == 'A') ? 1 : 11;
					break;
				case 1:
					state = (ch == 'l') ? 2 : 12;
					break;
				case 2:
					state = (ch == 'e') ? 3 : 13;
					break;
				case 3:
					state = (ch == 's') ? 4 : 14;
					break;
				case 4:
					state = (ch == 's') ? 5 : 15;
					break;
				case 5:
					state = (ch == 'a') ? 6 : 16;
					break;
				case 6:
					state = (ch == 'n') ? 7 : 17;
					break;
				case 7:
					state = (ch == 'd') ? 8 : 18;
					break;
				case 8:
					state = (ch == 'r') ? 9 : 19;
					break;
				case 9:
					state = 10;
					break;
				case 10:
					state = -1;
					break;
				case 11:
					state = (ch == 'l') ? 12 : -1;
					break;
				case 12:
					state = (ch == 'e') ? 13 : -1;
					break;
				case 13:
					state = (ch == 's') ? 14 : -1;
					break;
				case 14:
					state = (ch == 's') ? 15 : -1;
					break;
				case 15:
					state = (ch == 'a') ? 16 : -1;
					break;
				case 16:
					state = (ch == 'n') ? 17 : -1;
					break;
				case 17:
					state = (ch == 'd') ? 18 : -1;
					break;
				case 18:
					state = (ch == 'r') ? 19 : -1;
					break;
				case 19:
					state = (ch == 'o') ? 10 : -1;
					break;
			}
		}
		return state == 10;
	}

	public static void main(String[] args)
	{
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}