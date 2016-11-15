package com.thehshow.cnakes.entities;

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

	public boolean equals(PointCoordinates object) {
		
		return this.X == object.X && this.Y == object.Y;
	}
}
