package other.fontloader;
/**
	@author saucecode
	A floating-point Color container written for use with FonTT.java and Texture.java.
	This file is public domain. Use it for whatever.
*/
public class Color4f {

	public static final Color4f WHITE = new Color4f(1.0f,1.0f,1.0f,1.0f);
	public static final Color4f RED = new Color4f(1.0f,0.0f,0.0f,1.0f);
	public static final Color4f GREEN = new Color4f(0.0f,1.0f,0.0f,1.0f);
	public static final Color4f BLUE = new Color4f(0.0f,0.0f,1.0f,1.0f);
	public static final Color4f YELLOW = new Color4f(1.0f,1.0f,0.0f,1.0f);
	
	private float red, green, blue, alpha;
	
	public Color4f(float red, float green, float blue, float alpha){
		setRed(red);
		setBlue(blue);
		setGreen(green);
		setAlpha(alpha);
	}
	
	public Color4f(float red, float green, float blue){
		this(red,green,blue,1.0f);
	}

	public float getRed() {
		return red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
}
