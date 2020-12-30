package lv.herbis.cnakes.controls;

public class ControllerMapping {
	private String name;
	private int confirm = -1;
	private int cancel = -1;
	private int start = -1;
	private int back = -1;
	private int left = -1;
	private int right = -1;
	private int up = -1;
	private int down = -1;

	private int horizontalAxisLeftStick = -1;
	private int verticalAxisLeftStick = -1;
	private int horizontalAxisRightStick = -1;
	private int verticalAxisRightStick = -1;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getConfirm() {
		return confirm;
	}

	public void setConfirm(int confirm) {
		this.confirm = confirm;
	}

	public int getCancel() {
		return cancel;
	}

	public void setCancel(int cancel) {
		this.cancel = cancel;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getBack() {
		return back;
	}

	public void setBack(int back) {
		this.back = back;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getUp() {
		return up;
	}

	public void setUp(int up) {
		this.up = up;
	}

	public int getDown() {
		return down;
	}

	public void setDown(int down) {
		this.down = down;
	}

	public int getHorizontalAxisLeftStick() {
		return horizontalAxisLeftStick;
	}

	public void setHorizontalAxisLeftStick(int horizontalAxisLeftStick) {
		this.horizontalAxisLeftStick = horizontalAxisLeftStick;
	}

	public int getVerticalAxisLeftStick() {
		return verticalAxisLeftStick;
	}

	public void setVerticalAxisLeftStick(int verticalAxisLeftStick) {
		this.verticalAxisLeftStick = verticalAxisLeftStick;
	}

	public int getHorizontalAxisRightStick() {
		return horizontalAxisRightStick;
	}

	public void setHorizontalAxisRightStick(int horizontalAxisRightStick) {
		this.horizontalAxisRightStick = horizontalAxisRightStick;
	}

	public int getVerticalAxisRightStick() {
		return verticalAxisRightStick;
	}

	public void setVerticalAxisRightStick(int verticalAxisRightStick) {
		this.verticalAxisRightStick = verticalAxisRightStick;
	}
}
