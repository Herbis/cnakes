package lv.herbis.cnakes.entities;

import java.util.Objects;

public class PointCoordinates {
	private int x = 0;
	private int y = 0;

	public PointCoordinates(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public void setLocation(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(final Object object) {
		if (object instanceof PointCoordinates) {
			final PointCoordinates pc = (PointCoordinates) object;
			return this.x == pc.x && this.y == pc.y;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "[x=" + x + ", y=" + y + ']';
	}
}
