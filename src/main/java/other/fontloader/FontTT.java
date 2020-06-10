package other.fontloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
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
	private static final Logger LOG = LogManager.getLogger(FontTT.class);


	private Texture[] charactersp, characterso;
	private final HashMap<String, IntObject> charlistp = new HashMap<>();
	private final HashMap<String, IntObject> charlisto = new HashMap<>();
	private final TextureLoader textureloader;
	private final int kerneling;
	private int fontsize = 32;
	private final Font font;

	/*
	 * Need a special class to hold character information in the hasmaps
	 */
	private class IntObject {
		private final int charnum;

		IntObject(final int charnumpass) {
			charnum = charnumpass;
		}

		public int getCharnum() {
			return charnum;
		}
	}


	/*
	 * Pass in the preloaded truetype font, the resolution at which
	 * you wish the initial texture to be rendered at, and any extra
	 * kerneling you want inbetween characters
	 */
	public FontTT(final Font font, final int fontresolution, final int extrakerneling) {

		textureloader = new TextureLoader();
		this.kerneling = extrakerneling;
		this.font = font;
		fontsize = fontresolution;

		createPlainSet();
		createOutlineSet();
	}

	/*
	 * Create a standard Java2D bufferedimage to later be transferred into a texture
	 */
	private BufferedImage getFontImage(final char ch) {
		final Font tempfont;
		tempfont = font.deriveFont((float) fontsize);
		//Create a temporary image to extract font size
		final BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
		//// Add AntiAliasing /////
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		///////////////////////////
		g.setFont(tempfont);
		final FontMetrics fm = g.getFontMetrics();
		int charwidth = fm.charWidth(ch);

		if (charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = fm.getHeight();
		if (charheight <= 0) {
			charheight = fontsize;
		}

		//Create another image for texture creation
		final BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth, charheight, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		//// Add AntiAliasing /////
		gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		///////////////////////////
		gt.setFont(tempfont);

		//// Uncomment these to fill in the texture with a background color
		//// (used for debugging)
		//gt.setColor(Color.RED);
		//gt.fillRect(0, 0, charwidth, fontsize);

		gt.setColor(new java.awt.Color(Color4f.WHITE.getRed(), Color4f.WHITE.getGreen(), Color4f.WHITE.getBlue(), Color4f.WHITE.getAlpha()));
		final int charx = 0;
		final int chary = 0;
		gt.drawString(String.valueOf(ch), (charx), (chary) + fm.getAscent());

		return fontImage;

	}

	/*
	 * Create a standard Java2D bufferedimage for the font outline to later be
	 * converted into a texture
	 */
	private BufferedImage getOutlineFontImage(final char ch) {
		final Font tempfont;
		tempfont = font.deriveFont((float) fontsize);

		//Create a temporary image to extract font size
		final BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
		//// Add AntiAliasing /////
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		///////////////////////////
		g.setFont(tempfont);
		final FontMetrics fm = g.getFontMetrics();
		int charwidth = fm.charWidth(ch);

		if (charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = fm.getHeight();
		if (charheight <= 0) {
			charheight = fontsize;
		}

		//Create another image for texture creation
		final int ot = (int) ((float) fontsize / 24f);

		final BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth + 4 * ot, charheight + 4 * ot, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		//// Add AntiAliasing /////
		gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		///////////////////////////
		gt.setFont(tempfont);

		//// Uncomment these to fill in the texture with a background color
		//// (used for debugging)
		//gt.setColor(Color.RED);
		//gt.fillRect(0, 0, charwidth, fontsize);

		//// Create Outline by painting the character in multiple positions and blurring it
		gt.setColor(new java.awt.Color(Color4f.WHITE.getRed(), Color4f.WHITE.getGreen(), Color4f.WHITE.getBlue(), Color4f.WHITE.getAlpha()));
		final int charx = -fm.getLeading() + 2 * ot;
		final int chary = 2 * ot;
		gt.drawString(String.valueOf(ch), (charx) + ot, (chary) + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) - ot, (chary) + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx), (chary) + ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx), (chary) - ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) + ot, (chary) + ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) + ot, (chary) - ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) - ot, (chary) + ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) - ot, (chary) - ot + fm.getAscent());

		final float ninth = 1.0f / 9.0f;
		final float[] blurKernel = {
				ninth, ninth, ninth,
				ninth, ninth, ninth,
				ninth, ninth, ninth
		};
		final BufferedImageOp blur = new ConvolveOp(new Kernel(3, 3, blurKernel));

		final BufferedImage returnimage = blur.filter(fontImage, null);

		return returnimage;

	}


	/*
	 * Create and store the plain (non-outlined) set of the given fonts
	 */
	private void createPlainSet() {
		charactersp = new Texture[256];

		for (int i = 0; i < 256; i++) {
			final char ch = (char) i;

			BufferedImage fontImage = getFontImage(ch);

			final String temptexname = "Char." + i;
			charactersp[i] = textureloader.getTexture(temptexname, fontImage);

			charlistp.put(String.valueOf(ch), new IntObject(i));

		}
	}

	/*
	 * creates and stores the outlined set for the font
	 */
	private void createOutlineSet() {
		characterso = new Texture[256];

		for (int i = 0; i < 256; i++) {
			final char ch = (char) i;

			BufferedImage fontImage = getOutlineFontImage(ch);

			final String temptexname = "Charo." + i;
			characterso[i] = textureloader.getTexture(temptexname, fontImage);

			charlisto.put(String.valueOf(ch), new IntObject(i));
		}
	}


	/*
	 * Draws the given characters to the screen
	 * size = size of the font (does not change resolution)
	 * x,y,z = position to draw at
	 * color = color of font to draw
	 * rotx, roty, rotz = how much to rotate the font on each axis
	 * centered = center the font at the given location, or left justify
	 *
	 */
	public void drawText(final String whatchars, final float size, final float x, final float y, final float z, final Color4f color, final float rotxpass, final float rotypass, final float rotzpass, final boolean centered) {
		final float fontsizeratio = size / (float) fontsize;

		final int tempkerneling = kerneling;

		int k = 0;
		final float realwidth = getWidth(whatchars, size, false);
		GL11.glPushMatrix();
		final boolean islightingon = GL11.glIsEnabled(GL11.GL_LIGHTING);

		if (islightingon) {
			GL11.glDisable(GL11.GL_LIGHTING);
		}

		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(rotxpass, 1, 0, 0);
		GL11.glRotatef(rotypass, 0, 1, 0);
		GL11.glRotatef(rotzpass, 0, 0, 1);
		float totalwidth = 0;
		if (centered) {
			totalwidth = -realwidth / 2f;
		}
		for (int i = 0; i < whatchars.length(); i++) {
			final String tempstr = whatchars.substring(i, i + 1);
			k = charlistp.get(tempstr).getCharnum();
			drawtexture(charactersp[k], fontsizeratio, totalwidth, 0, color, rotxpass, rotypass, rotzpass);
			totalwidth += (charactersp[k].getImageWidth() * fontsizeratio + tempkerneling);
		}
		if (islightingon) {
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		GL11.glPopMatrix();

	}


	/*
	 * Draws the given characters to the screen with a drop shadow
	 * size = size of the font (does not change resolution)
	 * x,y,z = position to draw at
	 * color = color of font to draw
	 * shadowcolor = color of the drop shadow
	 * rotx, roty, rotz = how much to rotate the font on each axis
	 * centered = center the font at the given location, or left justify
	 *
	 */
	public void drawText(final String whatchars, final float size, final float x, final float y, final float z, final Color4f color, final Color4f shadowcolor, final float rotxpass, final float rotypass, final float rotzpass, final boolean centered) {
		drawText(whatchars, size, x + 1f, y - 1f, z, shadowcolor, rotxpass, rotypass, rotzpass, centered);
		drawText(whatchars, size, x, y, z, color, rotxpass, rotypass, rotzpass, centered);
	}


	/*
	 * Draws the given characters to the screen
	 * size = size of the font (does not change resolution)
	 * x,y,z = position to draw at
	 * color = color of font to draw
	 * outlinecolor = color of the font's outline
	 * rotx, roty, rotz = how much to rotate the font on each axis
	 * centered = center the font at the given location, or left justify
	 *
	 */
	public void drawOutlinedText(final String whatchars, final float size, final float x, final float y, final float z, final Color4f color, final Color4f outlinecolor, final float rotxpass, final float rotypass, final float rotzpass, final boolean centered) {
		final float fontsizeratio = size / (float) fontsize;

		final float tempkerneling = kerneling;

		int k = 0;
		int ko = 0;
		final float realwidth = getWidth(whatchars, size, true);
		GL11.glPushMatrix();
		final boolean islightingon = GL11.glIsEnabled(GL11.GL_LIGHTING);

		if (islightingon) {
			GL11.glDisable(GL11.GL_LIGHTING);
		}

		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(rotxpass, 1, 0, 0);
		GL11.glRotatef(rotypass, 0, 1, 0);
		GL11.glRotatef(rotzpass, 0, 0, 1);
		float xoffset, yoffset;
		float totalwidth = 0;
		if (centered) {
			totalwidth = -realwidth / 2f;
		}
		for (int i = 0; i < whatchars.length(); i++) {
			final String tempstr = whatchars.substring(i, i + 1);
			ko = charlisto.get(tempstr).getCharnum();
			drawtexture(characterso[ko], fontsizeratio, totalwidth, 0, outlinecolor, rotxpass, rotypass, rotzpass);

			k = charlistp.get(tempstr).getCharnum();
			xoffset = (characterso[k].getImageWidth() - charactersp[k].getImageWidth()) * fontsizeratio / 2f;
			yoffset = (characterso[k].getImageHeight() - charactersp[k].getImageHeight()) * fontsizeratio / 2f;
			drawtexture(charactersp[k], fontsizeratio, totalwidth + xoffset, yoffset, color, rotxpass, rotypass, rotzpass);
			totalwidth += ((characterso[k].getImageWidth() * fontsizeratio) + tempkerneling);
		}
		if (islightingon) {
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		GL11.glPopMatrix();

	}

	/*
	 * Draw the actual quad with character texture
	 */
	private void drawtexture(final Texture texture, final float ratio, final float x, final float y, final Color4f color, final float rotx, final float roty, final float rotz) {
		// Get the appropriate measurements from the texture itself
		final float imgwidth = texture.getImageWidth() * ratio;
		final float imgheight = -texture.getImageHeight() * ratio;
		final float texwidth = texture.getWidth();
		final float texheight = texture.getHeight();

		// Bind the texture
		texture.bind();

		// translate to the right location
		GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

		// draw a quad with to place the character onto
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0 + x, 0 - y);

			GL11.glTexCoord2f(0, texheight);
			GL11.glVertex2f(0 + x, imgheight - y);

			GL11.glTexCoord2f(texwidth, texheight);
			GL11.glVertex2f(imgwidth + x, imgheight - y);

			GL11.glTexCoord2f(texwidth, 0);
			GL11.glVertex2f(imgwidth + x, 0 - y);
		}
		GL11.glEnd();

	}

	/*
	 * Returns the width in pixels of the given string, size, outlined or not
	 * used for determining how to position the string, either for the user
	 * or for this object
	 *
	 */
	public float getWidth(final String whatchars, final float size, final boolean outlined) {
		final float fontsizeratio = size / (float) fontsize;

		final float tempkerneling = ((float) kerneling * fontsizeratio);
		float totalwidth = 0;
		int k = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			final String tempstr = whatchars.substring(i, i + 1);
			if (outlined) {
				k = charlisto.get(tempstr).getCharnum();
				totalwidth += (characterso[k].getImageWidth() * fontsizeratio) + tempkerneling;
			} else {
				k = charlistp.get(tempstr).getCharnum();
				totalwidth += (charactersp[k].getImageWidth() * fontsizeratio) + tempkerneling;
			}
		}
		return totalwidth;

	}
}
