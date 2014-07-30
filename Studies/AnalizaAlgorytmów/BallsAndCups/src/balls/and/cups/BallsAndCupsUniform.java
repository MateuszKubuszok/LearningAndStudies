package balls.and.cups;

public class BallsAndCupsUniform extends BallsAndCups {
	public BallsAndCupsUniform(int cups) {
		super(cups);
	}
	
	protected int selectCup() {
		return random.nextInt(cups);
	}
}
