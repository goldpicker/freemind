package freemind.view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.net.URL;

import javax.swing.ImageIcon;

public class ScalableImageIcon extends ImageIcon {
	private static final long serialVersionUID = 1110980781217268145L;

	private float mScale = 2.0f;

	private Image mScaledImage;

	public ScalableImageIcon(URL pURL) {
		super(pURL);
	}

	public float getScale() {
		return mScale;
	}

	public void setScale(float pScale) {
		this.mScale = pScale;
	}

	@Override
	public int getIconHeight() {
		return (int) (super.getIconHeight() * mScale);
	}

	@Override
	public int getIconWidth() {
		return (int) (super.getIconWidth() * mScale);
	}

	public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
		ImageObserver observer = getImageObserver();

		if (observer == null) {
			observer = c;
		}

		Image image = super.getImage();
		int width = image.getWidth(observer);
		int height = image.getHeight(observer);
		final Graphics2D g2d = (Graphics2D) g.create(x, y,
				(int) (width * mScale), (int) (height * mScale));

		g2d.scale(mScale, mScale);
		g2d.drawImage(image, 0, 0, observer);
		g2d.scale(1, 1);
		g2d.dispose();
	}
	
	@Override
	public Image getImage() {
		if(mScaledImage !=  null){
			return mScaledImage;
		}
		mScaledImage = super.getImage().getScaledInstance(getIconWidth(), getIconHeight(),  java.awt.Image.SCALE_SMOOTH); 
		return mScaledImage;
	}

	public ImageIcon getUnscaledIcon() {
		Image image = super.getImage();
		return new ImageIcon(image);
	}
}
