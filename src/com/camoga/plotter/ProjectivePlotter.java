package com.camoga.plotter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ProjectivePlotter extends JPanel {
	
	ArrayList<Curve> curves = new ArrayList<ProjectivePlotter.Curve>();
	
	class Curve {		
		ArrayList<Double[]> points = new ArrayList<Double[]>();
		Color color;
		Curve(Color color, Function f, double t0, double t1, double step) {
			this.color = color;
			
			for(double t = t0; t < t1; t+=step) {
				points.add(f.eval(t));
			}
		}
		
		Curve(int color, Function f, double t0, double t1, double step) {
			this(new Color(color), f, t0, t1, step);
		}
	}
	
	interface Function {
		public Double[] eval(double x);
	}

	int pointsize = 1;
	int gridlines = 0;
	
	static int width = 1400, height = 1000;
	static int horizon = 100, zero = 700;
	static int scale = zero-horizon;
	
	public ProjectivePlotter() {
		curves.add(new Curve(0xff0000,  t -> new Double[]{t,t}, -2, 200, 0.04));		// recta y = x
		curves.add(new Curve(0xff00,  t -> new Double[]{t,t-1}, -2, 200, 0.04));		// recta y = x-1
		curves.add(new Curve(0x66aaff,  t -> new Double[]{t,t*t}, -200, 200, 0.04));	// y = x^2
		curves.add(new Curve(0xffee00, t -> new Double[]{1.0,t}, -2, 200, 0.04));		// recta x = 1
		curves.add(new Curve(0xff99ff, t -> new Double[]{t,Math.sqrt((t-1)*(t-1)*(t-1)-t+2)}, -0.324717957, 200, 0.01)); // y^2 = x^3-3x^2+2x+1
		curves.add(new Curve(0x00ffff, t -> new Double[] {(1-t*t)/(1+t*t), 2*t/(1+t*t)}, -100, 100, 0.05)); 	// x^2 + y^2 = 1
		curves.add(new Curve(0xff57f29, t -> new Double[] {t,Math.tan(t)}, -Math.PI/2,Math.PI/2, 0.005));		// y = tan(x)
		curves.add(new Curve(0xcc00cc, t-> new Double[] {(1+t*t)/(2*t),(1-t*t)/(2*t)}, -200, 200, 0.01));		// x^2 - y^2 = 1
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.white);
		g.drawLine(0, horizon, width, horizon);
		g.drawLine(0, zero, width, zero);
		
		//vertical lines
		for(int i = 0; i < gridlines; i++) {
			g.drawLine(width/2+i*scale, zero, width/2, horizon);
			g.drawLine(width/2-i*scale, zero, width/2, horizon);
		}
		g.drawLine(width/2, horizon, width/2, zero);
		
		//horizontal lines
		for(double i = 1; i < gridlines; i++) {
			g.drawLine(0, (int)(1/i*(zero-horizon))+horizon, width, (int)(1/i*(zero-horizon))+horizon);
		}
		
		for(Curve c : curves) {
			g.setColor(c.color);
			//points
			for(int i = 0; i < c.points.size(); i++) {
				Double[] p = c.points.get(i);
				if(p[1]+1 < 0) continue;
				double xz = p[0];
				double yz = p[1];
				g.fillOval((int)(xz/(yz+1)*(zero-horizon)+width/2-pointsize/2.0), (int)(1/(yz+1)*(zero-horizon)+horizon-pointsize/2.0), pointsize, pointsize);				
			}
			
			// line segments
			for(int i = 0; i < c.points.size()-1; i++) {
				Double[] p1 = c.points.get(i);
				Double[] p2 = c.points.get(i+1);
				if(p1[1]+1 < 0 || p2[1]+1 <0) continue;
				double xz1 = p1[0];
				double yz1 = p1[1];
				double xz2 = p2[0];
				double yz2 = p2[1];
				g.drawLine((int)(xz1/(yz1+1)*(zero-horizon)+width/2), (int)(1/(yz1+1)*(zero-horizon)+horizon),(int)(xz2/(yz2+1)*(zero-horizon)+width/2), (int)(1/(yz2+1)*(zero-horizon)+horizon));
			}
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setResizable(true);
		frame.add(new ProjectivePlotter());
		frame.setVisible(true);
		frame.pack();
		frame.setSize(width,height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
	}
}