import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Board extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int w = 512;
	private int h = 512;

	private Integer sizeX;
	private Integer sizeY;
	private Integer nbBombs;
	private Element[][] map;

	public Board(Integer sizeX, Integer sizeY, Integer nbBombs) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.nbBombs = nbBombs;
		map = new Element[sizeX][sizeY];
		genMap();
	}

	public void genMap() {
		for (int r = 0; r < sizeX; r++) {
			for (int c = 0; c < sizeY; c++) {
				map[r][c] = new Element(0);
			}
		}

		for (int i = 0; i < nbBombs; i++) {
			int r1 = (int) Math.floor(Math.random() * sizeX);
			int c1 = (int) Math.floor(Math.random() * sizeY);
			map[r1][c1].setNum(-1);
			// mappy[r1][c1].reveal();
		}

		for (int r = 0; r < sizeX; r++) {
			for (int c = 0; c < sizeY; c++) {
				if (map[r][c].getNum() != -1) {
					int count = 0;
					if (r > 0) {
						if (map[r - 1][c].getNum() == -1)
							count++;
						if (c > 0) {
							if (map[r - 1][c - 1].getNum() == -1)
								count++;
						}
						if (c < sizeY - 1) {
							if (map[r - 1][c + 1].getNum() == -1)
								count++;
						}
					}
					if (c > 0) {
						if (map[r][c - 1].getNum() == -1)
							count++;
					}
					if (r < sizeX - 1) {
						if (map[r + 1][c].getNum() == -1)
							count++;
						if (c < sizeY - 1) {
							if (map[r + 1][c + 1].getNum() == -1)
								count++;
						}
						if (c > 0) {
							if (map[r + 1][c - 1].getNum() == -1)
								count++;
						}
					}
					if (c < sizeY - 1) {
						if (map[r][c + 1].getNum() == -1)
							count++;
					}
					map[r][c].setNum(count);
				}
			}
		}
	}

	public void paintComponent(Graphics g) {
		setOpaque(true);
		super.paintComponent(g);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, w, h);
		g.setColor(Color.black);

		g.setFont(g.getFont().deriveFont(g.getFont().getSize() * 2));
		g.setFont(getFont().deriveFont(g.getFont().BOLD));

		for (int i = 1; i < sizeX + 1; i++) {
			int x = (w / sizeX) * i;
			g.drawLine(x, 0, x, h);
		}
		for (int i = 1; i < sizeY + 1; i++) {
			int y = (h / sizeY) * i;
			g.drawLine(0, y, w, y);
		}

		for (int r = 0; r < sizeX; r++) {
			Element[] row = map[r];
			for (int c = 0; c < sizeY; c++) {
				Element e = row[c];
				if (!e.getHidden()) {
					if (e.getNum() == 0) {
						g.setColor(Color.DARK_GRAY);
					} else if (e.getNum() == 1) {
						g.setColor(Color.BLUE);
					} else if (e.getNum() == 2) {
						g.setColor(Color.GREEN);
					} else if (e.getNum() == 3) {
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.PINK);
					}
					int x = c * w / sizeX + w / (2 * sizeX) - 2;
					int y = (r + 1) * h / sizeY - h / (2 * sizeY) + 5;
					g.drawString("" + e.getNum(), x, y);
				} else if (e.getFlagged()) {
					g.setColor(Color.ORANGE);
					int x = c * w / sizeX + 2;
					int y = (r + 1) * h / sizeY - h / (2 * sizeY) + 5;

					g.drawString("FLAG", x, y);
				}

			}
		}
		//
		// g.setColor(Color.YELLOW);
		// g.drawLine(a[0] * w / s, a[1] * h / s, a[0] * w / s, (a[1] + 1) * h /
		// s);
		// g.drawLine((a[0] + 1) * w / s, a[1] * h / s, (a[0] + 1) * w / s,
		// (a[1] + 1) * h / s);
		// g.drawLine(a[0] * w / s, a[1] * h / s, (a[0] + 1) * w / s, a[1] * h /
		// s);
		// g.drawLine(a[0] * w / s, (a[1] + 1) * h / s, (a[0] + 1) * w / s,
		// (a[1] + 1) * h / s);

		repaint();
	}
}
