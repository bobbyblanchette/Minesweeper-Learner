import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

public class Board extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int w = 512;
	private final int h = 512;

	private Integer sizeX;
	private Integer sizeY;
	private Integer nbBombs;
	private ArrayList<ArrayList<Element>> map;
	private Point selected = new Point(-1, -1);

	public Board(Integer sizeX, Integer sizeY, Integer nbBombs) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.nbBombs = nbBombs;
		map = new ArrayList<ArrayList<Element>>();
		genMap();
	}

	public void genMap() {
		// Initializing elements in array
		Supplier<Element> elSup = () -> new Element(0);
		Supplier<ArrayList<Element>> elArraySup = () -> Stream.generate(elSup).limit(sizeX).collect(Collectors.toCollection(ArrayList::new));
		map = Stream.generate(elArraySup).limit(sizeY).collect(Collectors.toCollection(ArrayList::new));

		for (int i = 0; i < nbBombs; i++) {
			int x1 = (int) Math.floor(Math.random() * sizeX);
			int y1 = (int) Math.floor(Math.random() * sizeY);
			map.get(x1).get(y1).setNum(-1);
		}
		for (ListIterator<ArrayList<Element>> rowI = map.listIterator(); rowI.hasNext(); ) {
			ArrayList<Element> row = rowI.next();
			int y = 0;
			for (ListIterator<Element> colI = row.listIterator(); colI.hasNext(); y = colI.nextIndex()) {
				Element el = colI.next();
				
				if (el.getNum() != -1) {
					int num = 0;
					if (colI.hasPrevious() && row.get(colI.previousIndex()).getNum() == -1
							|| colI.hasNext() && row.get(colI.nextIndex()).getNum() == -1)
						num++;
					if (rowI.hasPrevious()) {
						ArrayList<Element> prevRow = map.get(rowI.previousIndex());
						if (prevRow.get(y).getNum() == -1
								|| colI.hasPrevious() && prevRow.get(colI.previousIndex()).getNum() == -1
								|| colI.hasNext() && prevRow.get(colI.nextIndex()).getNum() == -1)
							num++;
					}
					if (rowI.hasNext()) {
						ArrayList<Element> nextRow = map.get(rowI.nextIndex());
						if (nextRow.get(y).getNum() == -1
								|| colI.hasPrevious() && nextRow.get(colI.previousIndex()).getNum() == -1
								|| colI.hasNext() && nextRow.get(colI.nextIndex()).getNum() == -1)
							num++;
					}
					el.setNum(num);
				}
			}
		}
	}

	public Boolean select(Integer x, Integer y) {
		selected = new Point(x, y);
		if (map.get(x).get(y).getNum() == -1) {
			return false;
		}
		reveal(x, y);
		return true;
	}
	
	private void reveal(Integer x, Integer y) {
		
	}
	
	public Boolean check() {
		int count = 0;
		for (ArrayList<Element> row : map) {
			for (Element el : row) {
				if (el.getHidden())
					count++;
			}
		}
		return count == nbBombs;
	}
	
	public ArrayList<ArrayList<Element>> getMap() {
		return map;
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
			ArrayList<Element> row = map.get(r);
			for (int c = 0; c < sizeY; c++) {
				Element e = row.get(c);
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
						g.setColor(Color.BLACK);
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

		g.setColor(Color.YELLOW);
		g.drawLine(selected.x * w/sizeX, selected.y * h/sizeY, selected.x* w/sizeX, (selected.y+1) * h/sizeY);
		g.drawLine((selected.x+1)* w/sizeX, selected.y* h/sizeY, (selected.x+1)* w/sizeX, (selected.y+1) * h/sizeY);
		g.drawLine(selected.x* w/sizeX, selected.y* h/sizeY, (selected.x+1)* w/sizeX, selected.y* h/sizeY);
		g.drawLine(selected.x* w/sizeX, (selected.y+1) * h/sizeY, (selected.x+1)* w/sizeX, (selected.y+1) * h/sizeY);
		
		repaint();
	}
}
