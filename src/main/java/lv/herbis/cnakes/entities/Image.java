package lv.herbis.cnakes.entities;

import java.nio.ByteBuffer;

public class Image {

	private final ByteBuffer image;
	private final int width;
	private final int height;

	public Image(final int width, final int height, final ByteBuffer image) {
		this.image = image;
		this.height = height;
		this.width = width;
	}

	public ByteBuffer getImage() {
		return this.image;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
