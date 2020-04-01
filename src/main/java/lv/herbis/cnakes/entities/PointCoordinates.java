package lv.herbis.cnakes.entities;

import java.util.Objects;

public class PointCoordinates {
	public long X = 0;
	public long Y = 0; 
	
	public PointCoordinates(long x, long y) {
		this.X = x;
		this.Y = y;
	}
	
	public void setLocation(long x, long y) {
		this.X = x;
		this.Y = y;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof PointCoordinates) {
			final PointCoordinates pc = (PointCoordinates) object;
			return this.X == pc.X && this.Y == pc.Y;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(X, Y);
	}
}
