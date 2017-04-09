package com.sandbox.client.map;

public class Parallax {

	public final double speedX;  // The speed the parallax scrolls (pixels/second) on the x-axis
	public final double speedY;  // The speed the parallax scrolls (pixels/second) on the y-axis
	public final int tx, ty;     // Texture coordinates of the parallax in the parallax sprite sheet
	public double x = 0, y = 90; // Pixel position of the parallax
	
	public Parallax(int tx, int ty, double speedX, double speedY) {
		this.tx = tx;
		this.ty = ty;
		this.speedX = speedX;
		this.speedY = speedY;
	}
	
	public int x() {
		return (int) x;
	}
	
	public int y() {
		return (int) y;
	}
}
