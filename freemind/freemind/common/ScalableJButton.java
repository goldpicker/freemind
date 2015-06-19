package freemind.common;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;

/**
 * @author foltin
 * @date 19.06.2015
 * @see http://stackoverflow.com/questions/8183949/swing-scale-a-text-font-of-component 
 */
public class ScalableJButton extends JButton {
	int mCurrentSize = 0;
	Font mInitialFont = null;
	int mInitialHeight;
	
	private static final long serialVersionUID = 1L;

	public ScalableJButton(String pString) {
		super(pString);
		init();
	}

	public ScalableJButton() {
		super();
		init();
	}

	private void init() {
		mInitialFont = getFont();
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (mInitialHeight == 0) {
			mInitialHeight = getHeight();
		}
		int resizal = this.getHeight() * mInitialFont.getSize() / mInitialHeight;
		if(resizal != mCurrentSize){
			setFont(mInitialFont.deriveFont((float) resizal));
			mCurrentSize = resizal;
		}
		super.paintComponent(g);
	}
}