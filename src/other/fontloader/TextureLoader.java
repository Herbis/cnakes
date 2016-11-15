
package other.fontloader;
 

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_BITMAP;
import static org.lwjgl.opengl.GL11.GL_BLUE;
import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_COLOR_INDEX;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_GREEN;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_LUMINANCE;
import static org.lwjgl.opengl.GL11.GL_LUMINANCE_ALPHA;
import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_PACK_ROW_LENGTH;
import static org.lwjgl.opengl.GL11.GL_PACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.GL_PACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_INDEX;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ROW_LENGTH;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_BGRA;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * Modified from code originally written by:
 * @author Kevin Glass
 * @author Brian Matzon
 * 
 */
public class TextureLoader {
	/** The table of textures that have been loaded in this loader */
	private HashMap<String, Texture> table = new HashMap<String, Texture>();
	
	/** The colour model including alpha for the GL image */
	public ColorModel glAlphaColorModel;
	
	/** The colour model for the GL image */
	private ColorModel glColorModel;
	
	private int target = GL11.GL_TEXTURE_2D;
	private int dstPixelFormat = GL11.GL_RGBA;
	//private int dstPixelFormat = GL13.GL_COMPRESSED_RGBA;
	private int minFilter = GL11.GL_LINEAR_MIPMAP_NEAREST;
	private int magFilter = GL11.GL_LINEAR;
	
	
	
	private static IntBuffer scratch = BufferUtils.createIntBuffer(16);

	/** 
	 * Create a new texture loader based on the game panel
	 *
	 * @param gl The GL content in which the textures should be loaded
	 */
	public TextureLoader() {
		//dstPixelFormat = 4;
		
		glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
				new int[] {8,8,8,8},
				true,
				false,
				ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);
		
		glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
				new int[] {8,8,8,0},
				false,
				false,
				ComponentColorModel.OPAQUE,
				DataBuffer.TYPE_BYTE);
	}
	
	/**
	 * Create a new texture ID 
	 *
	 * @return A new texture ID
	 */
	private int createTextureID() 
	{ 
		IntBuffer tmp = createIntBuffer(1); 
		try {
			GL11.glGenTextures(tmp);
		} catch (NullPointerException e) {
			e.printStackTrace();
			//Sys.alert("Error","Your system is not capable of running this game.\nPlease make sure your video drivers are current.");
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
	public Texture getTexture(String resourceName, boolean injar) throws IOException {
		Texture tex = table.get(resourceName);
		
		if (tex != null) {
			return tex;
		}
		
		tex = getTexture(resourceName, injar,
				target, // target
				dstPixelFormat,     // dst pixel format
				minFilter, // min filter (unused)
				magFilter);
		
		table.put(resourceName,tex);
		
		return tex;
	}
	
	/**
	 * Load a texture into OpenGL from a image reference on
	 * disk.
	 *
	 * @param resourceName The location of the resource to load
	 * @param target The GL target to load the texture against
	 * @param dstPixelFormat The pixel format of the screen
	 * @param minFilter The minimising filter
	 * @param magFilter The magnification filter
	 * @return The loaded texture
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(String resourceName, boolean injar,
			int target, 
			int dstPixelFormat, 
			int minFilter, 
			int magFilter) throws IOException 
			{ 
		int srcPixelFormat = 0;
		
		// create the texture ID for this texture 
		int textureID = createTextureID(); 
		Texture texture = new Texture(target,textureID); 
		
		// bind this texture 
		GL11.glBindTexture(target, textureID); 
		
		BufferedImage bufferedImage = loadImage(resourceName, injar); 
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());
		
		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL11.GL_RGBA;
		} else {
			srcPixelFormat = GL11.GL_RGB;
		}
		
		// convert that image into a byte buffer of texture data 
		ByteBuffer textureBuffer = convertImageData(bufferedImage,texture); 
		
		if (target == GL11.GL_TEXTURE_2D) 
		{ 
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter); 
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter); 
		} 
		
		// produce a texture from the byte buffer
		/*
		GL11.glTexImage2D(target, 
				0, 
				dstPixelFormat, 
				get2Fold(bufferedImage.getWidth()), 
				get2Fold(bufferedImage.getHeight()), 
				0, 
				srcPixelFormat, 
				GL11.GL_UNSIGNED_BYTE, 
				textureBuffer ); 
		 */
		
		gluBuild2DMipmaps(target, dstPixelFormat, get2Fold(bufferedImage.getWidth()), 
				get2Fold(bufferedImage.getHeight()), srcPixelFormat, GL11.GL_UNSIGNED_BYTE, textureBuffer); 

		return texture; 
			} 
	
	
	
	
	/**
	 * Get the closest greater power of 2 to the fold number
	 * 
	 * @param fold The target number
	 * @return The power of 2
	 */
	private int get2Fold(int fold) {
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
	 * @param texture The texture to store the data into
	 * @return A buffer containing the data
	 */
	private ByteBuffer convertImageData(BufferedImage bufferedImage,Texture texture) { 
		ByteBuffer imageBuffer = null; 
		WritableRaster raster;
		BufferedImage texImage;
		
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
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,4,null);
			texImage = new BufferedImage(glAlphaColorModel,raster,false,new Hashtable());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,3,null);
			texImage = new BufferedImage(glColorModel,raster,false,new Hashtable());
		}
		
		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f,0f,0f,0f));
		g.fillRect(0,0,texWidth,texHeight);
		g.drawImage(bufferedImage,0,0,null);
		
		// build a byte buffer from the temporary image 
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData(); 
		
		imageBuffer = ByteBuffer.allocateDirect(data.length); 
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
	private BufferedImage loadImage(String ref, boolean injar) throws IOException 
	{ 
		//URL url = TextureLoader.class.getClassLoader().getResource(ref);
		
		//if (url == null) {
		//	throw new IOException("Cannot find: "+ref);
		//}
		
		if (injar) {
			BufferedImage bufferedImage = ImageIO.read(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(ref)));
			return bufferedImage;
		} else {
			File file = new File(ref);
			BufferedImage bufferedImage = null; 
			try {
				bufferedImage = ImageIO.read(file);
			} catch (IOException e) {
				System.out.println("Could not load texture: " + ref);
			}
			return bufferedImage;
		}
		
	}
	
	/**
	 * Creates an integer buffer to hold specified ints
	 * - strictly a utility method
	 *
	 * @param size how many int to contain
	 * @return created IntBuffer
	 */
	protected IntBuffer createIntBuffer(int size) {
		ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
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
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(String resourceName, BufferedImage resourceImage) throws IOException {
		Texture tex = table.get(resourceName);
		
		if (tex != null) {
			return tex;
		}
		
		tex = getTexture(resourceImage, 
				target, // target
				dstPixelFormat,     // dst pixel format
				minFilter, // min filter (unused)
				magFilter);
		
		table.put(resourceName,tex);
		
		return tex;
	}
	
	/**
	 * Load a texture into OpenGL from a BufferedImage
	 *
	 * @param resourceName The location of the resource to load
	 * @param target The GL target to load the texture against
	 * @param dstPixelFormat The pixel format of the screen
	 * @param minFilter The minimising filter
	 * @param magFilter The magnification filter
	 * @return The loaded texture
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(BufferedImage resourceimage, 
			int target, 
			int dstPixelFormat, 
			int minFilter, 
			int magFilter) throws IOException 
			{ 
		int srcPixelFormat = 0;
		
		// create the texture ID for this texture 
		int textureID = createTextureID(); 
		Texture texture = new Texture(target,textureID); 
		
		// bind this texture 
		GL11.glBindTexture(target, textureID); 
		
		BufferedImage bufferedImage = resourceimage; 
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());
		
		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL11.GL_RGBA;
		} else {
			srcPixelFormat = GL11.GL_RGB;
		}
		
		// convert that image into a byte buffer of texture data 
		ByteBuffer textureBuffer = convertImageData(bufferedImage,texture); 
		
		if (target == GL11.GL_TEXTURE_2D) 
		{ 
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter); 
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter); 
		} 
		
		// produce a texture from the byte buffer
		/*
		GL11.glTexImage2D(target, 
				0, 
				dstPixelFormat, 
				get2Fold(bufferedImage.getWidth()), 
				get2Fold(bufferedImage.getHeight()), 
				0, 
				srcPixelFormat, 
				GL11.GL_UNSIGNED_BYTE, 
				textureBuffer ); 
		 */
		
		gluBuild2DMipmaps(target, dstPixelFormat, get2Fold(bufferedImage.getWidth()), 
				get2Fold(bufferedImage.getHeight()), srcPixelFormat, GL11.GL_UNSIGNED_BYTE, textureBuffer); 

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
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getNMMTexture(String resourceName, BufferedImage resourceImage) throws IOException {
		Texture tex = table.get(resourceName);
		
		if (tex != null) {
			return tex;
		}
		
		tex = getNMMTexture(resourceImage, 
				target, // target
				dstPixelFormat,     // dst pixel format
				GL11.GL_NEAREST, // min filter (unused)
				GL11.GL_LINEAR);
		
		table.put(resourceName,tex);
		
		return tex;
	}
	
	/**
	 * Load a texture into OpenGL from a BufferedImage
	 *
	 * @param resourceName The location of the resource to load
	 * @param target The GL target to load the texture against
	 * @param dstPixelFormat The pixel format of the screen
	 * @param minFilter The minimising filter
	 * @param magFilter The magnification filter
	 * @return The loaded texture
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getNMMTexture(BufferedImage resourceimage, 
			int target, 
			int dstPixelFormat, 
			int minFilter, 
			int magFilter) throws IOException 
			{ 
		int srcPixelFormat = 0;
		
		// create the texture ID for this texture 
		int textureID = createTextureID(); 
		Texture texture = new Texture(target,textureID); 
		
		// bind this texture 
		GL11.glBindTexture(target, textureID); 
		
		BufferedImage bufferedImage = resourceimage; 
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());
		
		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL11.GL_RGBA;
		} else {
			srcPixelFormat = GL11.GL_RGB;
		}
		
		// convert that image into a byte buffer of texture data 
		ByteBuffer textureBuffer = convertImageData(bufferedImage,texture); 
		
		if (target == GL11.GL_TEXTURE_2D) 
		{ 
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter); 
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter); 
		} 
		
		// produce a texture from the byte buffer
		GL11.glTexImage2D(target, 
				0, 
				dstPixelFormat, 
				get2Fold(bufferedImage.getWidth()), 
				get2Fold(bufferedImage.getHeight()), 
				0, 
				srcPixelFormat, 
				GL11.GL_UNSIGNED_BYTE, 
				textureBuffer ); 

		return texture; 
			} 
	
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	
	/**
	 * Method gluBuild2DMipmaps
	 *
	 * @param target
	 * @param components
	 * @param width
	 * @param height
	 * @param format
	 * @param type
	 * @param data
	 * @return int
	 */
	public static int gluBuild2DMipmaps(final int target,
	                                    final int components, final int width, final int height,
	                                    final int format, final int type, final ByteBuffer data) {
		if ( width < 1 || height < 1 ) return 100901; //GLU_INVALID_VALUE

		final int bpp = bytesPerPixel(format, type);
		if ( bpp == 0 )
			return 100900; //GLU_INVALID_ENUM

		final int maxSize = glGetIntegerv(GL_MAX_TEXTURE_SIZE);

		int w = nearestPower(width);
		if ( w > maxSize )
			w = maxSize;

		int h = nearestPower(height);
		if ( h > maxSize )
			h = maxSize;

		// Get current glPixelStore state
		PixelStoreState pss = new PixelStoreState();

		// set pixel packing
		glPixelStorei(GL_PACK_ROW_LENGTH, 0);
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
		glPixelStorei(GL_PACK_SKIP_ROWS, 0);
		glPixelStorei(GL_PACK_SKIP_PIXELS, 0);

		ByteBuffer image;
		int retVal = 0;
		boolean done = false;

		if ( w != width || h != height ) {
			// must rescale image to get "top" mipmap texture image
			image = BufferUtils.createByteBuffer((w + 4) * h * bpp);
			int error = gluScaleImage(format, width, height, type, data, w, h, type, image);
			if ( error != 0 ) {
				retVal = error;
				done = true;
			}

			/* set pixel unpacking */
			glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
			glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
		} else {
			image = data;
		}

		ByteBuffer bufferA = null;
		ByteBuffer bufferB = null;

		int level = 0;
		while ( !done ) {
			if (image != data) {
				/* set pixel unpacking */
				glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
				glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
				glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
				glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
			}

			glTexImage2D(target, level, components, w, h, 0, format, type, image);

			if ( w == 1 && h == 1 )
				break;

			final int newW = (w < 2) ? 1 : w >> 1;
			final int newH = (h < 2) ? 1 : h >> 1;

			final ByteBuffer newImage;

			if ( bufferA == null )
				newImage = (bufferA = BufferUtils.createByteBuffer((newW + 4) * newH * bpp));
			else if ( bufferB == null )
				newImage = (bufferB = BufferUtils.createByteBuffer((newW + 4) * newH * bpp));
			else
				newImage = bufferB;

			int error = gluScaleImage(format, w, h, type, image, newW, newH, type, newImage);
			if ( error != 0 ) {
				retVal = error;
				done = true;
			}

			image = newImage;
			if ( bufferB != null )
				bufferB = bufferA;

			w = newW;
			h = newH;
			level++;
		}

		// Restore original glPixelStore state
		pss.save();

		return retVal;
	}
	
	/**
	 * Method bytesPerPixel.
	 *
	 * @param format
	 * @param type
	 *
	 * @return int
	 */
	protected static int bytesPerPixel(int format, int type) {
		int n, m;

		switch ( format ) {
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
			default :
				n = 0;
		}

		switch ( type ) {
			case GL_UNSIGNED_BYTE:
				m = 1;
				break;
			case GL_BYTE:
				m = 1;
				break;
			case GL_BITMAP:
				m = 1;
				break;
			case GL_UNSIGNED_SHORT:
				m = 2;
				break;
			case GL_SHORT:
				m = 2;
				break;
			case GL_UNSIGNED_INT:
				m = 4;
				break;
			case GL_INT:
				m = 4;
				break;
			case GL_FLOAT:
				m = 4;
				break;
			default :
				m = 0;
		}

		return n * m;
	}
	
	/**
	 * Method gluScaleImage.
	 * @param format
	 * @param widthIn
	 * @param heightIn
	 * @param typein
	 * @param dataIn
	 * @param widthOut
	 * @param heightOut
	 * @param typeOut
	 * @param dataOut
	 * @return int
	 */
	public static int gluScaleImage(int format,
	                                int widthIn, int heightIn, int typein, ByteBuffer dataIn,
	                                int widthOut, int heightOut, int typeOut, ByteBuffer dataOut) {

		final int components = compPerPix(format);
		if ( components == -1 )
			return 100900; //GLU_INVALID_ENUM

		int i, j, k;
		float[] tempIn, tempOut;
		float sx, sy;
		int sizein, sizeout;
		int rowstride, rowlen;

		// temp image data
		tempIn = new float[widthIn * heightIn * components];
		tempOut = new float[widthOut * heightOut * components];

		// Determine bytes per input type
		switch ( typein ) {
			case GL_UNSIGNED_BYTE:
				sizein = 1;
				break;
			case GL_FLOAT:
				sizein = 4;
				break;
			default:
				return GL_INVALID_ENUM;
		}

		// Determine bytes per output type
		switch ( typeOut ) {
			case GL_UNSIGNED_BYTE:
				sizeout = 1;
				break;
			case GL_FLOAT:
				sizeout = 4;
				break;
			default:
				return GL_INVALID_ENUM;
		}

		// Get glPixelStore state
		PixelStoreState pss = new PixelStoreState();

		//Unpack the pixel data and convert to floating point
		if ( pss.unpackRowLength > 0 )
			rowlen = pss.unpackRowLength;
		else
			rowlen = widthIn;

		if ( sizein >= pss.unpackAlignment )
			rowstride = components * rowlen;
		else
			rowstride = pss.unpackAlignment / sizein * ceil(components * rowlen * sizein, pss.unpackAlignment);

		switch ( typein ) {
			case GL_UNSIGNED_BYTE:
				k = 0;
				dataIn.rewind();
				for ( i = 0; i < heightIn; i++ ) {
					int ubptr = i * rowstride + pss.unpackSkipRows * rowstride + pss.unpackSkipPixels * components;
					for ( j = 0; j < widthIn * components; j++ ) {
						tempIn[k++] = dataIn.get(ubptr++) & 0xff;
					}
				}
				break;
			case GL_FLOAT:
				k = 0;
				dataIn.rewind();
				for ( i = 0; i < heightIn; i++ )
				{
					int fptr = 4 * (i * rowstride + pss.unpackSkipRows * rowstride + pss.unpackSkipPixels * components);
					for ( j = 0; j < widthIn * components; j++ )
					{
						tempIn[k++] = dataIn.getFloat(fptr);
						fptr += 4;
					}
				}
				break;
			default:
				return 100900; //GLU_INVALID_ENUM
		}

		// Do scaling
		sx = (float)widthIn / (float)widthOut;
		sy = (float)heightIn / (float)heightOut;

		float[] c = new float[components];
		int src, dst;

		for ( int iy = 0; iy < heightOut; iy++ ) {
			for ( int ix = 0; ix < widthOut; ix++ ) {
				int x0 = (int)(ix * sx);
				int x1 = (int)((ix + 1) * sx);
				int y0 = (int)(iy * sy);
				int y1 = (int)((iy + 1) * sy);

				int readPix = 0;

				// reset weighted pixel
				for ( int ic = 0; ic < components; ic++ ) {
					c[ic] = 0;
				}

				// create weighted pixel
				for ( int ix0 = x0; ix0 < x1; ix0++ ) {
					for ( int iy0 = y0; iy0 < y1; iy0++ ) {

						src = (iy0 * widthIn + ix0) * components;

						for ( int ic = 0; ic < components; ic++ ) {
							c[ic] += tempIn[src + ic];
						}

						readPix++;
					}
				}

				// store weighted pixel
				dst = (iy * widthOut + ix) * components;

				if ( readPix == 0 ) {
					// Image is sized up, caused by non power of two texture as input
					src = (y0 * widthIn + x0) * components;
					for ( int ic = 0; ic < components; ic++ ) {
						tempOut[dst++] = tempIn[src + ic];
					}
				} else {
					// sized down
					for ( k = 0; k < components; k++ ) {
						tempOut[dst++] = c[k] / readPix;
					}
				}
			}
		}


		// Convert temp output
		if ( pss.packRowLength > 0 )
			rowlen = pss.packRowLength;
		else
			rowlen = widthOut;

		if ( sizeout >= pss.packAlignment )
			rowstride = components * rowlen;
		else
			rowstride = pss.packAlignment / sizeout * ceil(components * rowlen * sizeout, pss.packAlignment);

		switch ( typeOut ) {
			case GL_UNSIGNED_BYTE:
				k = 0;
				for ( i = 0; i < heightOut; i++ ) {
					int ubptr = i * rowstride + pss.packSkipRows * rowstride + pss.packSkipPixels * components;

					for ( j = 0; j < widthOut * components; j++ ) {
						dataOut.put(ubptr++, (byte)tempOut[k++]);
					}
				}
				break;
			case GL_FLOAT:
				k = 0;
				for ( i = 0; i < heightOut; i++ ) {
					int fptr = 4 * (i * rowstride + pss.unpackSkipRows * rowstride + pss.unpackSkipPixels * components);

					for ( j = 0; j < widthOut * components; j++ ) {
						dataOut.putFloat(fptr, tempOut[k++]);
						fptr += 4;
					}
				}
				break;
			default:
				return 100900; //GLU_INVALID_ENUM
		}

		return 0;
	}
	
	/**
	 * Convenience method for returning an int, rather than getting it out of a buffer yourself.
	 *
	 * @param what
	 *
	 * @return int
	 */
	protected static int glGetIntegerv(int what) {
		scratch.rewind();
		GL11.glGetIntegerv(what, scratch);//glGetInteger(what);//glGetInteger(what, scratch);
		return scratch.get();
	}
	
	
	/**
	 * Method compPerPix.
	 *
	 * @param format
	 *
	 * @return int
	 */
	protected static int compPerPix(int format) {
		/* Determine number of components per pixel */
		switch ( format ) {
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
			default :
				return -1;
		}
	}
	
	/**
	 * Return ceiling of integer division
	 *
	 * @param a
	 * @param b
	 *
	 * @return int
	 */
	protected static int ceil(int a, int b) {
		return (a % b == 0 ? a / b : a / b + 1);
	}
	
	/**
	 * Method nearestPower.
	 * <p/>
	 * Compute the nearest power of 2 number.  This algorithm is a little strange, but it works quite well.
	 *
	 * @param value
	 *
	 * @return int
	 */
	protected static int nearestPower(int value) {
		int i;

		i = 1;

		/* Error! */
		if ( value == 0 )
			return -1;

		for ( ; ; ) {
			if ( value == 1 ) {
				return i;
			} else if ( value == 3 ) {
				return i << 2;
			}
			value >>= 1;
			i <<= 1;
		}
	}
}