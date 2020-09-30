package lv.herbis.cnakes.controls;

public class ControllerState {
	private boolean confirmPressed;
	private boolean cancelPressed;
	private boolean startPressed;
	private boolean backPressed;
	private boolean leftPressed;
	private boolean rightPressed;
	private boolean upPressed;
	private boolean downPressed;
	private AxisDirection leftPadAxisDirection = AxisDirection.NONE;
	private AxisDirection rightPadAxisDirection = AxisDirection.NONE;

	public boolean isConfirmPressed() {
		return confirmPressed;
	}

	public void setConfirmPressed(boolean confirmPressed) {
		this.confirmPressed = confirmPressed;
	}

	public boolean isCancelPressed() {
		return cancelPressed;
	}

	public void setCancelPressed(boolean cancelPressed) {
		this.cancelPressed = cancelPressed;
	}

	public boolean isStartPressed() {
		return startPressed;
	}

	public void setStartPressed(boolean startPressed) {
		this.startPressed = startPressed;
	}

	public boolean isBackPressed() {
		return backPressed;
	}

	public void setBackPressed(boolean backPressed) {
		this.backPressed = backPressed;
	}

	public boolean isLeftPressed() {
		return leftPressed;
	}

	public void setLeftPressed(boolean leftPressed) {
		this.leftPressed = leftPressed;
	}

	public boolean isRightPressed() {
		return rightPressed;
	}

	public void setRightPressed(boolean rightPressed) {
		this.rightPressed = rightPressed;
	}

	public boolean isUpPressed() {
		return upPressed;
	}

	public void setUpPressed(boolean upPressed) {
		this.upPressed = upPressed;
	}

	public boolean isDownPressed() {
		return downPressed;
	}

	public void setDownPressed(boolean downPressed) {
		this.downPressed = downPressed;
	}

	public AxisDirection getLeftPadAxisDirection() {
		return leftPadAxisDirection;
	}

	public void setLeftPadAxisDirection(AxisDirection leftPadAxisDirection) {
		this.leftPadAxisDirection = leftPadAxisDirection;
	}

	public AxisDirection getRightPadAxisDirection() {
		return rightPadAxisDirection;
	}

	public void setRightPadAxisDirection(AxisDirection rightPadAxisDirection) {
		this.rightPadAxisDirection = rightPadAxisDirection;
	}
}
