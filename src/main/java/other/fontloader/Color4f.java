package other.fontloader;

/**
 * @author saucecode
 * A floating-point Color container written for use with FonTT.java and Texture.java.
 * This file is public domain. Use it for whatever.
 */
public class Color4f {

	public static final Color4f WHITE = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Color4f RED = new Color4f(1.0f, 0.0f, 0.0f, 1.0f);
	public static final Color4f GREEN = new Color4f(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Color4f BLUE = new Color4f(0.0f, 0.0f, 1.0f, 1.0f);
	public static final Color4f YELLOW = new Color4f(1.0f, 1.0f, 0.0f, 1.0f);
	public static final Color4f GREY = new Color4f(0.5f, 0.5f, 0.5f, 1.0f);

	private float redAmount;
	private float greenAmount;
	private float blueAmount;
	private float alpha;

	public Color4f(final float redAmount, final float greenAmount, final float blueAmount, final float alpha) {
		setRedAmount(redAmount);
		setBlueAmount(blueAmount);
		setGreenAmount(greenAmount);
		setAlpha(alpha);
	}

	public Color4f(final float redAmount, final float greenAmount, final float blueAmount) {
		this(redAmount, greenAmount, blueAmount, 1.0f);
	}

	public float getRedAmount() {
		return redAmount;
	}

	public void setRedAmount(final float redAmount) {
		this.redAmount = redAmount;
	}

	public float getGreenAmount() {
		return greenAmount;
	}

	public void setGreenAmount(final float greenAmount) {
		this.greenAmount = greenAmount;
	}

	public float getBlueAmount() {
		return blueAmount;
	}

	public void setBlueAmount(final float blueAmount) {
		this.blueAmount = blueAmount;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
	}
}
