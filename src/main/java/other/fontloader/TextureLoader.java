package other.fontloader;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Hashtable;

import static java.awt.Transparency.OPAQUE;
import static java.awt.Transparency.TRANSLUCENT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_BGRA;

/**
 * Modified from code originally written by:
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class TextureLoader {
	private static final Logger LOG = LogManager.getLogger(TextureLoader.class);
	private static final int TARGET = GL11.GL_TEXTURE_2D;
	private static final int DST_PIXEL_FORMAT = GL11.GL_RGBA;
	private static final int MIN_FILTER = GL11.GL_LINEAR_MIPMAP_NEAREST;
	private static final int MAG_FILTER = GL11.GL_LINEAR;
	private static final IntBuffer scratch = BufferUtils.createIntBuffer(16);

	/**
	 * The table of textures that have been loaded in this loader
	 */
	private final HashMap<String, Texture> table = new HashMap<>();

	/**
	 * The colour model including alpha for the GL image
	 */
	private final ColorModel glAlphaColorModel;

	/**
	 * The colour model for the GL image
	 */
	private final ColorModel glColorModel;


	/**
	 * Create a new texture loader based on the game panel
	 */
	public TextureLoader() {
		this.glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
														 new int[]{8, 8, 8, 8}, true, false, TRANSLUCENT,
														 DataBuffer.TYPE_BYTE);

		this.glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 0},
													false, false, OPAQUE, DataBuffer.TYPE_BYTE);
	}

	/**
	 * Create a new texture ID
	 *
	 * @return A new texture ID
	 */
	private int createTextureID() {
		final IntBuffer tmp = createIntBuffer(1);
		try {
			GL11.glGenTextures(tmp);
		} catch (final NullPointerException e) {
			LOG.fatal(
					"Your system is not capable of running this game.\nPlease make sure your video drivers are current.",
					e);
			System.exit(0);
		}
		return tmp.get(0);
	}

	/**
	 * Load a texture
	 *
	 * @param resourceName The location of the resource to load
	 * @return The loaded texture
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(final String resourceName, final boolean injar) throws IOException {
		Texture tex = this.table.get(resourceName);

		if (tex != null) {
			return tex;
		}

		tex = getTexture(resourceName, injar, TARGET, // target
						 DST_PIXEL_FORMAT,     // dst pixel format
						 MIN_FILTER, // min filter (unused)
						 MAG_FILTER);

		this.table.put(resourceName, tex);

		return tex;
	}

	/**
	 * Load a texture into OpenGL from a image reference on
	 * disk.
	 *
	 * @param resourceName   The location of the resource to load
	 * @param target         The GL target to load the texture against
	 * @param dstPixelFormat The pixel format of the screen
	 * @param minFilter      The minimising filter
	 * @param magFilter      The magnification filter
	 * @return The loaded texture
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(final String resourceName, final boolean injar, final int target,
							  final int dstPixelFormat, final int minFilter, final int magFilter) throws IOException {

		// create the texture ID for this texture
		final int textureID = createTextureID();
		final Texture texture = new Texture(target, textureID);

		// bind this texture
		GL11.glBindTexture(target, textureID);

		final BufferedImage bufferedImage = loadImage(resourceName, injar);
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		final int srcPixelFormat;

		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL11.GL_RGBA;
		} else {
			srcPixelFormat = GL11.GL_RGB;
		}

		// convert that image into a byte buffer of texture data
		final ByteBuffer textureBuffer = convertImageData(bufferedImage, texture);

		if (target == GL11.GL_TEXTURE_2D) {
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
		}

		final int returnValue = gluBuild2DMipmaps(target, dstPixelFormat, get2Fold(bufferedImage.getWidth()),
						  get2Fold(bufferedImage.getHeight()), srcPixelFormat, GL11.GL_UNSIGNED_BYTE, textureBuffer);
		if (returnValue != 0)
		{
			LOG.warn("Return value for gluBuild2DMipmaps is {}. Resource name: {}", returnValue, resourceName);
		}
		return texture;
	}


	/**
	 * Get the closest greater power of 2 to the fold number
	 *
	 * @param fold The target number
	 * @return The power of 2
	 */
	private int get2Fold(final int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	}

	/**
	 * Convert the buffered image to a texture
	 *
	 * @param bufferedImage The image to convert to a texture
	 * @param texture       The texture to store the data into
	 * @return A buffer containing the data
	 */
	private ByteBuffer convertImageData(final BufferedImage bufferedImage, final Texture texture) {
		final WritableRaster raster;
		final BufferedImage texImage;

		int texWidth = 2;
		int texHeight = 2;

		// find the closest power of 2 for the width and height
		// of the produced texture
		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}

		texture.setTextureHeight(texHeight);
		texture.setTextureWidth(texWidth);

		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
			texImage = new BufferedImage(this.glAlphaColorModel, raster, false, new Hashtable<>());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
			texImage = new BufferedImage(this.glColorModel, raster, false, new Hashtable<>());
		}

		// copy the source image into the produced image
		final Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		final byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

		final ByteBuffer imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
	}

	/**
	 * Load a given resource as a buffered image
	 *
	 * @param ref The location of the resource to load
	 * @return The loaded buffered image
	 * @throws IOException Indicates a failure to find a resource
	 */
	private BufferedImage loadImage(final String ref, final boolean inJar) throws IOException {
		if (inJar) {
			final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ref);
			if (inputStream == null) {
				throw new FontException("Could not load image: " + ref);
			} else {
				return ImageIO.read(new BufferedInputStream(inputStream));
			}
		} else {
			return ImageIO.read(Files.newInputStream(Path.of(ref)));
		}
	}

	/**
	 * Creates an integer buffer to hold specified ints
	 * - strictly a utility method
	 *
	 * @param size how many int to contain
	 * @return created IntBuffer
	 */
	protected IntBuffer createIntBuffer(final int size) {
		final ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
		temp.order(ByteOrder.nativeOrder());

		return temp.asIntBuffer();
	}


	//////////////////////////////////////////////////
	//// Added for BufferedImage Support /////////////
	//////////////////////////////////////////////////

	/**
	 * Load a texture
	 *
	 * @param resourceName The location of the resource to load
	 * @return The loaded texture
	 */
	public Texture getTexture(final String resourceName, final BufferedImage resourceImage) {
		Texture tex = this.table.get(resourceName);

		if (tex != null) {
			return tex;
		}

		tex = getTexture(resourceImage, TARGET, // target
						 DST_PIXEL_FORMAT,     // dst pixel format
						 MIN_FILTER, // min filter (unused)
						 MAG_FILTER);

		this.table.put(resourceName, tex);

		return tex;
	}

	/**
	 * Load a texture into OpenGL from a BufferedImage
	 *
	 * @param bufferedImage  buffered image.
	 * @param target         The GL target to load the texture against
	 * @param dstPixelFormat The pixel format of the screen
	 * @param minFilter      The minimising filter
	 * @param magFilter      The magnification filter
	 * @return The loaded texture
	 */
	public Texture getTexture(final BufferedImage bufferedImage, final int target, final int dstPixelFormat,
							  final int minFilter, final int magFilter) {

		// create the texture ID for this texture
		final int textureID = createTextureID();
		final Texture texture = new Texture(target, textureID);

		// bind this texture
		GL11.glBindTexture(target, textureID);

		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		final int srcPixelFormat;
		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL11.GL_RGBA;
		} else {
			srcPixelFormat = GL11.GL_RGB;
		}

		// convert that image into a byte buffer of texture data
		final ByteBuffer textureBuffer = convertImageData(bufferedImage, texture);

		if (target == GL11.GL_TEXTURE_2D) {
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
		}

		final int returnValue = gluBuild2DMipmaps(target, dstPixelFormat, get2Fold(bufferedImage.getWidth()),
						  get2Fold(bufferedImage.getHeight()), srcPixelFormat, GL11.GL_UNSIGNED_BYTE, textureBuffer);
		if (returnValue != 0)
		{
			LOG.warn("Return value for gluBuild2DMipmaps in getTexture is {}. Texture ID: {}", returnValue, textureID);
		}

		return texture;
	}


	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////


	//////////////////////////////////////////////////
	//// Added for No MipMapping Support /////////////
	//////////////////////////////////////////////////

	/**
	 * Load a texture
	 *
	 * @param resourceName The location of the resource to load
	 * @return The loaded texture
	 */
	public Texture getNMMTexture(final String resourceName, final BufferedImage resourceImage) {
		Texture tex = this.table.get(resourceName);

		if (tex != null) {
			return tex;
		}

		tex = getNMMTexture(resourceImage, TARGET, // target
							DST_PIXEL_FORMAT,     // dst pixel format
							GL11.GL_NEAREST, // min filter (unused)
							GL11.GL_LINEAR);

		this.table.put(resourceName, tex);

		return tex;
	}

	/**
	 * Load a texture into OpenGL from a BufferedImage
	 *
	 * @param bufferedImage  buffered Image
	 * @param target         The GL target to load the texture against
	 * @param dstPixelFormat The pixel format of the screen
	 * @param minFilter      The minimising filter
	 * @param magFilter      The magnification filter
	 * @return The loaded texture
	 */
	public Texture getNMMTexture(final BufferedImage bufferedImage, final int target, final int dstPixelFormat,
								 final int minFilter, final int magFilter) {
		// create the texture ID for this texture
		final int textureID = createTextureID();
		final Texture texture = new Texture(target, textureID);

		// bind this texture
		GL11.glBindTexture(target, textureID);

		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		final int srcPixelFormat;
		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL11.GL_RGBA;
		} else {
			srcPixelFormat = GL11.GL_RGB;
		}

		// convert that image into a byte buffer of texture data
		final ByteBuffer textureBuffer = convertImageData(bufferedImage, texture);

		if (target == GL11.GL_TEXTURE_2D) {
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
		}

		// produce a texture from the byte buffer
		GL11.glTexImage2D(target, 0, dstPixelFormat, get2Fold(bufferedImage.getWidth()),
						  get2Fold(bufferedImage.getHeight()), 0, srcPixelFormat, GL11.GL_UNSIGNED_BYTE, textureBuffer);

		return texture;
	}

	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////

	public static int gluBuild2DMipmaps(final int target, final int components, final int width, final int height,
										final int format, final int type, final ByteBuffer data) {
		if (width < 1 || height < 1) {
			return GL_INVALID_VALUE;
		}

		final int bytesPerPixel = bytesPerPixel(format, type);
		if (bytesPerPixel == 0) {
			return GL_INVALID_ENUM;
		}

		final int maxSize = glGetIntegerv(GL_MAX_TEXTURE_SIZE);

		int w = nearestPower(width);
		if (w > maxSize) {
			w = maxSize;
		}

		int h = nearestPower(height);
		if (h > maxSize) {
			h = maxSize;
		}

		// Get current glPixelStore state
		final PixelStoreState pss = new PixelStoreState();

		setPixelUnpacking();

		final ByteBuffer image;
		int retVal = 0;
		boolean done = false;

		if (w != width || h != height) {
			// must rescale image to get "top" mipmap texture image
			image = BufferUtils.createByteBuffer((w + 4) * h * bytesPerPixel);
			final int error = gluScaleImage(format, width, height, type, data, w, h, type, image);
			if (error != 0) {
				retVal = error;
				done = true;
			}

			setPixelUnpacking();
		} else {
			image = data;
		}

		if (!done) {
			gluBuild2DMipmapsFinishScaling(target, components, image, w, h, format, type, data, bytesPerPixel);
		}


		// Restore original glPixelStore state
		pss.save();

		return retVal;
	}

	public static int gluBuild2DMipmapsFinishScaling(final int target, final int components, final ByteBuffer image,
											  		 final int scaleWidth, final int scaleHeight, final int format,
											  		 final int type, final ByteBuffer data, final int bytesPerPixel) {
		ByteBuffer bufferA = null;
		ByteBuffer bufferB = null;
		int level = 0;
		boolean done = false;
		int retVal = 0;

		ByteBuffer currentImage = image;
		int w = scaleWidth;
		int h = scaleHeight;

		while (!done) {
			if (currentImage != data) {
				setPixelUnpacking();
			}

			glTexImage2D(target, level, components, w, h, 0, format, type, currentImage);

			if (w == 1 && h == 1) {
				break;
			}

			final int newW = computeNewSizeBits(w);
			final int newH = computeNewSizeBits(h);

			final ByteBuffer newImage;

			if (bufferA == null) {
				newImage = (bufferA = BufferUtils.createByteBuffer((newW + 4) * newH * bytesPerPixel));
			} else if (bufferB == null) {
				newImage = (bufferB = BufferUtils.createByteBuffer((newW + 4) * newH * bytesPerPixel));
			} else {
				newImage = bufferB;
			}

			final int error = gluScaleImage(format, w, h, type, currentImage, newW, newH, type, newImage);
			if (error != 0) {
				retVal = error;
				done = true;
			}

			currentImage = newImage;
			if (bufferB != null) {
				bufferB = bufferA;
			}

			w = newW;
			h = newH;
			level++;
		}

		return retVal;
	}

	protected static int computeNewSizeBits(final int size)
	{
		return (size < 2) ? 1 : size >> 1;
	}

	protected static void setPixelUnpacking() {
		glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
		glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
	}

	protected static int bytesPerPixel(final int format, final int type) {
		final int n;
		final int m;

		switch (format) {
			case GL_COLOR_INDEX:
			case GL_STENCIL_INDEX:
			case GL_DEPTH_COMPONENT:
			case GL_RED:
			case GL_GREEN:
			case GL_BLUE:
			case GL_ALPHA:
			case GL_LUMINANCE:
				n = 1;
				break;
			case GL_LUMINANCE_ALPHA:
				n = 2;
				break;
			case GL_RGB:
			case GL_BGR:
				n = 3;
				break;
			case GL_RGBA:
			case GL_BGRA:
				n = 4;
				break;
			default:
				n = 0;
		}

		switch (type) {
			case GL_UNSIGNED_BYTE:
			case GL_BYTE:
			case GL_BITMAP:
				m = 1;
				break;
			case GL_UNSIGNED_SHORT:
			case GL_SHORT:
				m = 2;
				break;
			case GL_UNSIGNED_INT:
			case GL_INT:
			case GL_FLOAT:
				m = 4;
				break;
			default:
				m = 0;
		}

		return n * m;
	}

	public static int gluScaleImage(final int format, final int widthIn, final int heightIn, final int typeIn,
									final ByteBuffer dataIn, final int widthOut, final int heightOut, final int typeOut,
									final ByteBuffer dataOut) {

		final int components = compPerPix(format);
		if (components == -1) {
			return 100900; //GLU_INVALID_ENUM
		}

		// Get glPixelStore state
		final PixelStoreState pss = new PixelStoreState();

		// Determine bytes per input type
		final int sizeIn = determineBytesPerInputType(typeIn);
		final int sizeOut = determineBytesPerInputType(typeOut);
		if (sizeIn == 0 || sizeOut == 0) {
			return GL_INVALID_ENUM;
		}

		// temp image data
		final float[] tempIn;
		try {
			tempIn = gluGenerateTempInputImageData(dataIn, typeIn, sizeIn, widthIn, heightIn, components, pss);
		} catch (final IllegalArgumentException e) {
			return GL_INVALID_ENUM;
		}


		final float[] tempOut = gluGenerateTempOutputImageData(tempIn, widthIn, heightIn, widthOut, heightOut,
															   components);
		gluConvertTempToOutputImageData(dataOut, tempOut, typeOut, sizeOut, widthOut, heightOut, components, pss);

		return 0;
	}

	protected static int determineBytesPerInputType(final int type) {
		switch (type) {
			case GL_UNSIGNED_BYTE:
				return 1;
			case GL_FLOAT:
				return 4;
			default:
				return 0;
		}
	}

	protected static float[] gluGenerateTempInputImageData(final ByteBuffer dataIn, final int typeIn, final int sizeIn,
														   final int widthIn, final int heightIn, final int components,
														   final PixelStoreState pss) {

		final float[] tempIn = new float[widthIn * heightIn * components];

		//Unpack the pixel data and convert to floating point
		final int rowLen;
		if (pss.getUnpackRowLength() > 0) {
			rowLen = pss.getUnpackRowLength();
		} else {
			rowLen = widthIn;
		}


		final int rowStride;
		if (sizeIn >= pss.getUnpackAlignment()) {
			rowStride = components * rowLen;
		} else {
			rowStride = pss.getUnpackAlignment() / sizeIn * ceil(components * rowLen * sizeIn,
																 pss.getUnpackAlignment());
		}

		dataIn.rewind();

		int q = 0;
		if (typeIn == GL_UNSIGNED_BYTE) {
			for (int i = 0; i < heightIn; i++) {
				int ubptr = i * rowStride + pss.getUnpackSkipRows() * rowStride + pss
						.getUnpackSkipPixels() * components;
				for (int j = 0; j < widthIn * components; j++) {
					tempIn[q++] = dataIn.get(ubptr++) & 0xff;
				}
			}
		} else {
			for (int i = 0; i < heightIn; i++) {
				int fptr = sizeIn * (i * rowStride + pss.getUnpackSkipRows() * rowStride + pss
						.getUnpackSkipPixels() * components);
				for (int j = 0; j < widthIn * components; j++) {
					tempIn[q++] = dataIn.getFloat(fptr);
					fptr += sizeIn;
				}
			}
		}

		return tempIn;
	}

	protected static float[] gluGenerateTempOutputImageData(final float[] tempIn, final int widthIn, final int heightIn,
															final int widthOut, final int heightOut,
															final int components) {
		final float[] c = new float[components];

		// Do scaling
		final float sx = (float) widthIn / widthOut;
		final float sy = (float) heightIn / heightOut;

		// temp image data
		final float[] tempOut = new float[widthOut * heightOut * components];

		for (int iy = 0; iy < heightOut; iy++) {
			for (int ix = 0; ix < widthOut; ix++) {
				final int x0 = (int) (ix * sx);
				final int x1 = (int) ((ix + 1) * sx);
				final int y0 = (int) (iy * sy);
				final int y1 = (int) ((iy + 1) * sy);

				int readPix = 0;

				// reset weighted pixel
				for (int ic = 0; ic < components; ic++) {
					c[ic] = 0;
				}

				// create weighted pixel
				for (int ix0 = x0; ix0 < x1; ix0++) {
					for (int iy0 = y0; iy0 < y1; iy0++) {

						final int src = (iy0 * widthIn + ix0) * components;

						for (int ic = 0; ic < components; ic++) {
							c[ic] += tempIn[src + ic];
						}

						readPix++;
					}
				}

				// store weighted pixel
				int dst = (iy * widthOut + ix) * components;

				if (readPix == 0) {
					// Image is sized up, caused by non power of two texture as input
					final int src = (y0 * widthIn + x0) * components;
					for (int ic = 0; ic < components; ic++) {
						tempOut[dst++] = tempIn[src + ic];
					}
				} else {
					// sized down
					for (int k = 0; k < components; k++) {
						tempOut[dst++] = c[k] / readPix;
					}
				}
			}
		}

		return tempOut;
	}

	protected static void gluConvertTempToOutputImageData(final ByteBuffer dataOut, final float[] tempOut,
														  final int typeOut, final int sizeOut, final int widthOut,
														  final int heightOut, final int components,
														  final PixelStoreState pss) {

		// Convert temp output
		final int rowLen;
		if (pss.getPackRowLength() > 0) {
			rowLen = pss.getPackRowLength();
		} else {
			rowLen = widthOut;
		}

		final int rowStride;
		if (sizeOut >= pss.getPackAlignment()) {
			rowStride = components * rowLen;
		} else {
			rowStride = pss.getPackAlignment() / sizeOut * ceil(components * rowLen * sizeOut, pss.getPackAlignment());
		}

		int q = 0;
		if (typeOut == GL_UNSIGNED_BYTE) {
			for (int i = 0; i < heightOut; i++) {
				int ubptr = i * rowStride + pss.getPackSkipRows() * rowStride + pss.getPackSkipPixels() * components;

				for (int j = 0; j < widthOut * components; j++) {
					dataOut.put(ubptr++, (byte) tempOut[q++]);
				}
			}
		} else {
			for (int i = 0; i < heightOut; i++) {
				int fptr = 4 * (i * rowStride + pss.getPackSkipRows() * rowStride + pss
						.getPackSkipPixels() * components);

				for (int j = 0; j < widthOut * components; j++) {
					dataOut.putFloat(fptr, tempOut[q++]);
					fptr += 4;
				}
			}
		}
	}

	/**
	 * Convenience method for returning an int, rather than getting it out of a buffer yourself.
	 */
	protected static int glGetIntegerv(final int what) {
		scratch.rewind();
		GL11.glGetIntegerv(what, scratch);
		return scratch.get();
	}


	protected static int compPerPix(final int format) {
		/* Determine number of components per pixel */
		switch (format) {
			case GL_COLOR_INDEX:
			case GL_STENCIL_INDEX:
			case GL_DEPTH_COMPONENT:
			case GL_RED:
			case GL_GREEN:
			case GL_BLUE:
			case GL_ALPHA:
			case GL_LUMINANCE:
				return 1;
			case GL_LUMINANCE_ALPHA:
				return 2;
			case GL_RGB:
			case GL_BGR:
				return 3;
			case GL_RGBA:
			case GL_BGRA:
				return 4;
			default:
				return -1;
		}
	}

	/**
	 * Return ceiling of integer division
	 */
	protected static int ceil(final int a, final int b) {
		return (a % b == 0 ? a / b : a / b + 1);
	}

	/**
	 * Method nearestPower.
	 * <p/>
	 * Compute the nearest power of 2 number.  This algorithm is a little strange, but it works quite well.
	 *
	 * @return int
	 */
	protected static int nearestPower(int value) {
		int i;

		i = 1;

		/* Error! */
		if (value == 0) {
			return -1;
		}

		for (; ; ) {
			if (value == 1) {
				return i;
			} else if (value == 3) {
				return i << 2;
			}
			value >>= 1;
			i <<= 1;
		}
	}
}