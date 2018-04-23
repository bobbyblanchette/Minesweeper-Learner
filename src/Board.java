import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Board extends JPanel {

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
			getElement(x1, y1).setNum(-1);
		}
		for (CustomListIterator<ArrayList<Element>> rowI = new CustomListIterator<>(map.listIterator()); rowI.hasNext(); ) {
			ArrayList<Element> row = rowI.next();
			for (CustomListIterator<Element> colI = new CustomListIterator<>(row.listIterator()); colI.hasNext();) {
				Element el = colI.next();
				
				if (el.getNum() != -1) {
					int num = 0;
					if (colI.hasPrevious() && row.get(colI.previousIndex()).getNum() == -1)
						num++;
					if (colI.hasNext() && row.get(colI.nextIndex()).getNum() == -1)
						num++;
					if (rowI.hasPrevious()) {
						ArrayList<Element> prevRow = map.get(rowI.previousIndex());
						if (prevRow.get(colI.currentIndex()).getNum() == -1)
							num++;
						if (colI.hasPrevious() && prevRow.get(colI.previousIndex()).getNum() == -1)
							num++;
						if (colI.hasNext() && prevRow.get(colI.nextIndex()).getNum() == -1)
							num++;
					}
					if (rowI.hasNext()) {
						ArrayList<Element> nextRow = map.get(rowI.nextIndex());
						if (nextRow.get(colI.currentIndex()).getNum() == -1)
							num++;
						if (colI.hasPrevious() && nextRow.get(colI.previousIndex()).getNum() == -1)
							num++;
						if (colI.hasNext() && nextRow.get(colI.nextIndex()).getNum() == -1)
							num++;
					}
					el.setNum(num);
				}
			}
		}
	}

	public Boolean select(Integer x, Integer y) {
		selected = new Point(x, y);
		if (getElement(x, y).getNum() == -1) {
			return false;
		}
		dig(x, y);
		return true;
	}
	
	private void dig(Integer x, Integer y) {
		getElement(x, y).show();
		if (getElement(x, y).getNum() == 0) {
			boolean leftOk = x > 0;
			boolean rightOk = x < sizeX - 1;
			boolean topOk = y > 0;
			boolean bottomOk = y < sizeY - 1;
			if (leftOk) {
				if (getElement(x - 1, y).getHidden())
					dig(x - 1, y);
				if (topOk && getElement(x - 1, y - 1).getHidden()) 
					dig(x - 1, y - 1);
				if (bottomOk && getElement(x - 1, y + 1).getHidden())
					dig(x - 1, y + 1);
			}
			if (rightOk) {
				if (getElement(x + 1, y).getHidden())
					dig(x + 1, y);
				if (topOk && getElement(x + 1, y - 1).getHidden()) 
					dig(x + 1, y - 1);
				if (bottomOk && getElement(x + 1, y + 1).getHidden())
					dig(x + 1, y + 1);
			}
			if (topOk && getElement(x, y - 1).getHidden())
				dig(x, y - 1);
			if (bottomOk && getElement(x, y + 1).getHidden())
				dig(x, y + 1);
		}
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
	
	public void reset() {
		for (ArrayList<Element> row : map)
			for (Element el : row)
				el.hide();
	}
	
	public ArrayList<ArrayList<Element>> getMap() {
		return map;
	}
	
	public Element getElement(Integer x, Integer y) {
		return map.get(y).get(x);
	}
	
	public Integer eval(Integer x, Integer y) {
		if (x < 0 || x > sizeX - 1 || y < 0 || y > sizeY - 1)
			return -2;
		return getElement(x, y).getHidden() ? -1 : getElement(x, y).getNum();
	}
	
	public void paintComponent(Graphics g) {
		setOpaque(true);
		super.paintComponent(g);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, w, h);
		g.setColor(Color.black);

		g.setFont(g.getFont().deriveFont(g.getFont().getSize() * 1.2f));
		g.setFont(g.getFont().deriveFont(Font.BOLD));

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
					if (e.getNum() == -1) {
						g.setColor(Color.BLACK);
					} else if (e.getNum() == 0) {
						g.setColor(Color.DARK_GRAY);
					} else if (e.getNum() == 1) {
						g.setColor(Color.BLUE);
					} else if (e.getNum() == 2) {
						g.setColor(Color.GREEN);
					} else if (e.getNum() == 3) {
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.MAGENTA);
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
	
	public class CustomListIterator<T> implements ListIterator<T> {

	    private final ListIterator<T> underlying;

	    public CustomListIterator(ListIterator<T> underlying) {
	        this.underlying = underlying;
	    }

	    
	    @Override 
	    public boolean hasPrevious() {
	    	underlying.previous();
	    	boolean hasPrevious = underlying.hasPrevious();
	    	underlying.next();
	    	return hasPrevious;
	    }
	    
	    @Override 
	    public int previousIndex() {
	    	underlying.previous();
	    	int previousIndex = underlying.previousIndex();
	    	underlying.next();
	    	return previousIndex;
	    }
	    
	    public int currentIndex() {
	    	return underlying.previousIndex();
	    }
	    
	    @Override public boolean hasNext() {return underlying.hasNext();}
	    @Override public T next() {return underlying.next(); }
	    @Override public T previous() {return underlying.previous();}
	    @Override public int nextIndex() {return underlying.nextIndex();}
	    @Override public void remove() { underlying.remove();}
	    @Override public void set(T o) {underlying.set(o);}
	    @Override public void add(T o) {underlying.add(o);}
	}
}
