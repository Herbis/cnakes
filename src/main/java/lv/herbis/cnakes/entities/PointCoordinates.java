package lv.herbis.cnakes.entities;

import java.util.Objects;

public class PointCoordinates {
	private long x = 0;
	private long y = 0;
	
	public PointCoordinates(long x, long y) {
		this.x = x;
		this.y = y;
	}
	
	public void setLocation(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	@Override
	public boolean equals(Object object) {
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
}
