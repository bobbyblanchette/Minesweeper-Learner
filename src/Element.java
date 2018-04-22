
public class Element {

	private Integer num; // Number of bombs around. -1 if contains bomb itself
	private Boolean hidden = true;
	private Boolean flagged = false;
	
	public Element(Integer num) {
		this.setNum(num);
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void hide() {
		this.hidden = true;
	}
	
	public void show() {
		this.hidden = false;
	}
	
	public Boolean getFlagged() {
		return this.flagged;
	}
	
	public void switchFlag() {
		this.flagged = !this.flagged;
	}
	
	public String toString() {
		return this.num.toString();
	}
}
