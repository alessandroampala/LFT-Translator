public class NumberTok extends Token {
	int number;
	public NumberTok(int number) {
		super(Tag.NUM);
		this.number = number;
	}
	public String toString(){
		return "<" + Tag.NUM + ", " + number + ">";
	}
}