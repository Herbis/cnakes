package other.fontloader;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;

/**
 * @author Jeremy Adams (elias4444)
 * <p>
 * This module utilizes the modules Texture and TextureLoader
 * in order to load and store texture information. The most
 * complicated thing to know about these classes is that TextureLoader
 * takes the BufferedImage and converts it into a Texture. If an image
 * is not "power of 2" Textureloader makes it a power of 2 and sets the
 * texture coordinates appropriately.
 */
public class FontTT {


	private Texture[] charactersP;
	private Texture[] charactersO;
	private final HashMap<String, IntObject> charListP = new HashMap<>();
	private final HashMap<String, IntObject> charListO = new HashMap<>();
	private final TextureLoader textureloader;
	private final int kerneling;
	private final int fontSize;
	private final Font font;

	/*
	 * Need a special class to hold character information in the hashMaps
	 */
	private static class IntObject {
		private final int charNum;

		IntObject(final int charNum) {
			this.charNum = charNum;
		}

		public int getCharNum() {
			return this.charNum;
		}
	}


	/*
	 * Pass in the preloaded TrueType font, the resolution at which
	 * you wish the initial texture to be rendered at, and any extra
	 * kerneling you want in-between characters
	 */
	public FontTT(final Font font, final int fontResolution, final int extraKerneling) {

		this.textureloader = new TextureLoader();
		this.kerneling = extraKerneling;
		this.font = font;
		this.fontSize = fontResolution;

		createPlainSet();
		createOutlineSet();
	}

	/*
	 * Create a standard Java2D BufferedImage to later be transferred into a texture
	 */
	private BufferedImage getFontImage(final char ch) {
		final Font tempFont = this.font.deriveFont((float) this.fontSize);
		//Create a temporary image to extract font size
		final BufferedImage tempFontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D) tempFontImage.getGraphics();
		//// Add AntiAliasing /////
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		///////////////////////////
		g.setFont(tempFont);
		final FontMetrics fm = g.getFontMetrics();
		int charWidth = fm.charWidth(ch);

		if (charWidth <= 0) {
			charWidth = 1;
		}
		int charHeight = fm.getHeight();
		if (charHeight <= 0) {
			charHeight = this.fontSize;
		}

		// Create another image for texture creation
		final BufferedImage fontImage;
		fontImage = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		//// Add AntiAliasing /////
		gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		///////////////////////////
		gt.setFont(tempFont);

		gt.setColor(new java.awt.Color(Color4f.WHITE.getRed(), Color4f.WHITE.getGreen(), Color4f.WHITE.getBlue(),
									   Color4f.WHITE.getAlpha()));
		final int charX = 0;
		final int charY = 0;
		gt.drawString(String.valueOf(ch), (charX), (charY) + fm.getAscent());

		return fontImage;

	}

	/*
	 * Create a standard Java2D BufferedImage for the font outline to later be
	 * converted into a texture
	 */
	private BufferedImage getOutlineFontImage(final char ch) {
		final Font tempFont = this.font.deriveFont((float) this.fontSize);

		// Create a temporary image to extract font size
		final BufferedImage tempFontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D) tempFontImage.getGraphics();
		//// Add AntiAliasing /////
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		///////////////////////////
		g.setFont(tempFont);
		final FontMetrics fm = g.getFontMetrics();
		int charWidth = fm.charWidth(ch);

		if (charWidth <= 0) {
			charWidth = 1;
		}
		int charHeight = fm.getHeight();
		if (charHeight <= 0) {
			charHeight = this.fontSize;
		}

		//Create another image for texture creation
		final int ot = (int) ((float) this.fontSize / 24f);

		final BufferedImage fontImage;
		fontImage = new BufferedImage(charWidth + 4 * ot, charHeight + 4 * ot, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		//// Add AntiAliasing /////
		gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		///////////////////////////
		gt.setFont(tempFont);

		//// Create Outline by painting the character in multiple positions and blurring it
		gt.setColor(new java.awt.Color(Color4f.WHITE.getRed(), Color4f.WHITE.getGreen(), Color4f.WHITE.getBlue(),
									   Color4f.WHITE.getAlpha()));
		final int charX = -fm.getLeading() + 2 * ot;
		final int charY = 2 * ot;
		gt.drawString(String.valueOf(ch), (charX) + ot, (charY) + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charX) - ot, (charY) + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charX), (charY) + ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charX), (charY) - ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charX) + ot, (charY) + ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charX) + ot, (charY) - ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charX) - ot, (charY) + ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charX) - ot, (charY) - ot + fm.getAscent());

		final float ninth = 1.0f / 9.0f;
		final float[] blurKernel = {ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth};
		final BufferedImageOp blur = new ConvolveOp(new Kernel(3, 3, blurKernel));

		return blur.filter(fontImage, null);
	}


	/*
	 * Create and store the plain (non-outlined) set of the given fonts
	 */
	private void createPlainSet() {
		this.charactersP = new Texture[256];

		for (int i = 0; i < 256; i++) {
			final char ch = (char) i;

			final BufferedImage fontImage = getFontImage(ch);

			this.charactersP[i] = this.textureloader.getTexture("Char." + i, fontImage);

			this.charListP.put(String.valueOf(ch), new IntObject(i));

		}
	}

	/*
	 * creates and stores the outlined set for the font
	 */
	private void createOutlineSet() {
		this.charactersO = new Texture[256];

		for (int i = 0; i < 256; i++) {
			final char ch = (char) i;

			final BufferedImage fontImage = getOutlineFontImage(ch);

			this.charactersO[i] = this.textureloader.getTexture("Charo." + i, fontImage);

			this.charListO.put(String.valueOf(ch), new IntObject(i));
		}
	}


	/*
	 * Draws the given characters to the screen
	 * size = size of the font (does not change resolution)
	 * x,y,z = position to draw at
	 * color = color of font to draw
	 * rotX, rotY, rotZ = how much to rotate the font on each axis
	 * centered = center the font at the given location, or left justify
	 *
	 */
	public void drawText(final String content, final float size, final float x, final float y, final float z,
						 final Color4f color, final float rotX, final float rotY, final float rotZ,
						 final boolean centered) {
		final float fontSizeRatio = size / (float) this.fontSize;

		final float realWidth = getWidth(content, size, false);
		GL11.glPushMatrix();
		final boolean isLightingOn = GL11.glIsEnabled(GL11.GL_LIGHTING);

		if (isLightingOn) {
			GL11.glDisable(GL11.GL_LIGHTING);
		}

		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(rotX, 1, 0, 0);
		GL11.glRotatef(rotY, 0, 1, 0);
		GL11.glRotatef(rotZ, 0, 0, 1);
		float totalwidth = 0;
		if (centered) {
			totalwidth = -realWidth / 2f;
		}
		for (int i = 0; i < content.length(); i++) {
			final String tempstr = content.substring(i, i + 1);
			final int k = this.charListP.get(tempstr).getCharNum();
			drawTexture(this.charactersP[k], fontSizeRatio, totalwidth, 0, color);
			totalwidth += (this.charactersP[k].getImageWidth() * fontSizeRatio + this.kerneling);
		}
		if (isLightingOn) {
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		GL11.glPopMatrix();

	}


	/*
	 * Draws the given characters to the screen with a drop shadow
	 * size = size of the font (does not change resolution)
	 * x,y,z = position to draw at
	 * color = color of font to draw
	 * shadowColor = color of the drop shadow
	 * rotX, rotY, rotZ = how much to rotate the font on each axis
	 * centered = center the font at the given location, or left justify
	 *
	 */
	public void drawText(final String content, final float size, final float x, final float y, final float z,
						 final Color4f color, final Color4f shadowColor, final float rotX, final float rotY,
						 final float rotZ, final boolean centered) {
		drawText(content, size, x + 1f, y - 1f, z, shadowColor, rotX, rotY, rotZ, centered);
		drawText(content, size, x, y, z, color, rotX, rotY, rotZ, centered);
	}


	/*
	 * Draws the given characters to the screen
	 * size = size of the font (does not change resolution)
	 * x,y,z = position to draw at
	 * color = color of font to draw
	 * outlineColor = color of the font's outline
	 * rotX, rotY, rotZ = how much to rotate the font on each axis
	 * centered = center the font at the given location, or left justify
	 *
	 */
	public void drawOutlinedText(final String content, final float size, final float x, final float y, final float z,
								 final Color4f color, final Color4f outlineColor, final float rotX,
								 final float rotY, final float rotZ, final boolean centered) {
		final float fontSizeRatio = size / (float) this.fontSize;

		final float realWidth = getWidth(content, size, true);
		GL11.glPushMatrix();
		final boolean isLightingOn = GL11.glIsEnabled(GL11.GL_LIGHTING);

		if (isLightingOn) {
			GL11.glDisable(GL11.GL_LIGHTING);
		}

		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(rotX, 1, 0, 0);
		GL11.glRotatef(rotY, 0, 1, 0);
		GL11.glRotatef(rotZ, 0, 0, 1);
		float xOffset;
		float yOffset;
		float totalWidth = 0;
		if (centered) {
			totalWidth = -realWidth / 2f;
		}
		for (int i = 0; i < content.length(); i++) {
			final String tempStr = content.substring(i, i + 1);
			final int ko = this.charListO.get(tempStr).getCharNum();
			drawTexture(this.charactersO[ko], fontSizeRatio, totalWidth, 0, outlineColor);

			final int k = this.charListP.get(tempStr).getCharNum();
			xOffset = (this.charactersO[k].getImageWidth() - this.charactersP[k].getImageWidth()) * fontSizeRatio / 2f;
			yOffset = (this.charactersO[k].getImageHeight() - this.charactersP[k]
					.getImageHeight()) * fontSizeRatio / 2f;
			drawTexture(this.charactersP[k], fontSizeRatio, totalWidth + xOffset, yOffset, color);
			totalWidth += ((this.charactersO[k].getImageWidth() * fontSizeRatio) + this.kerneling);
		}
		if (isLightingOn) {
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		GL11.glPopMatrix();

	}

	/*
	 * Draw the actual quad with character texture
	 */
	private void drawTexture(final Texture texture, final float ratio, final float x, final float y,
							 final Color4f color) {
		// Get the appropriate measurements from the texture itself
		final float imgWidth = texture.getImageWidth() * ratio;
		final float imgHeight = -texture.getImageHeight() * ratio;
		final float texWidth = texture.getWidth();
		final float texHeight = texture.getHeight();

		// Bind the texture
		texture.bind();

		// translate to the right location
		GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

		// draw a quad with to place the character onto
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(0 + x, 0 - y);

		GL11.glTexCoord2f(0, texHeight);
		GL11.glVertex2f(0 + x, imgHeight - y);

		GL11.glTexCoord2f(texWidth, texHeight);
		GL11.glVertex2f(imgWidth + x, imgHeight - y);

		GL11.glTexCoord2f(texWidth, 0);
		GL11.glVertex2f(imgWidth + x, 0 - y);

		GL11.glEnd();

	}


	/*
	 * Returns the width in pixels of the given string, size, outlined or not
	 * used for determining how to position the string, either for the user
	 * or for this object
	 */
	public float getWidth(final String whatchars, final float size, final boolean outlined) {
		final float fontSizeRatio = size / (float) this.fontSize;

		final float tempKerneling = ((float) this.kerneling * fontSizeRatio);
		float totalWidth = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			final String tempStr = whatchars.substring(i, i + 1);
			final int k;
			if (outlined) {
				k = this.charListO.get(tempStr).getCharNum();
				totalWidth += (this.charactersO[k].getImageWidth() * fontSizeRatio) + tempKerneling;
			} else {
				k = this.charListP.get(tempStr).getCharNum();
				totalWidth += (this.charactersP[k].getImageWidth() * fontSizeRatio) + tempKerneling;
			}
		}
		return totalWidth;

	}
}
