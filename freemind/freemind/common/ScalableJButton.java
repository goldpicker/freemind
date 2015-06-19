package freemind.common;

import java.awt.Graphics;

import javax.swing.JButton;

/**
 * @author foltin
 * @date 19.06.2015
 * @see http://stackoverflow.com/questions/8183949/swing-scale-a-text-font-of-component 
 */
public class ScalableJButton extends JButton {
	private static final long serialVersionUID = 1L;

	public ScalableJButton(String pString) {
		// TODO Auto-generated constructor stub
	}

	public ScalableJButton() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void paintComponent(Graphics g) {
		int h = this.getHeight();
		final int DEFAULT_H = 26;
		double resizal = ((double) h) / DEFAULT_H;

		String t = getText();
		setText("<html><span style='font-size:" + (resizal * 11) + "'>" + t);
		super.paintComponent(g);
		setText(t);
	}
}