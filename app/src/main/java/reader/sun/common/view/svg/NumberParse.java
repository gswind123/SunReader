package reader.sun.common.view.svg;

import java.util.ArrayList;

public class NumberParse {
	private ArrayList<Float> numbers;
	private int nextCmd;

	public NumberParse(ArrayList<Float> numbers, int nextCmd) {
		this.numbers = numbers;
		this.nextCmd = nextCmd;
	}

	public int getNextCmd() {
		return nextCmd;
	}

	public float getNumber(int index) {
		return numbers.get(index);
	}

}