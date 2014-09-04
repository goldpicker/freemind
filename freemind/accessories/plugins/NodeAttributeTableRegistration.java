/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Christian Foltin, Dimitry Polivaev and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 11.09.2007
 */
/*$Id: NodeNoteRegistration.java,v 1.1.2.25 2010/10/07 21:19:51 christianfoltin Exp $*/

package accessories.plugins;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import freemind.controller.Controller.SplitComponentType;
import freemind.controller.MenuItemSelectedListener;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;

public class NodeAttributeTableRegistration implements HookRegistration, 
		MenuItemSelectedListener {

	private final MindMapController controller;

	private final java.util.logging.Logger logger;

	private boolean mSplitPaneVisible = false;
	
	private JPanel mAttributeViewerComponent = null;

	public NodeAttributeTableRegistration(ModeController controller, MindMap map) {
		this.controller = (MindMapController) controller;
		logger = Resources.getInstance().getLogger(this.getClass().getName());
	}

	/**
	 * @return true, if the split pane is to be shown. 
	 * E.g. when freemind was closed before, the state of the split pane was stored and
	 * is restored at the next start.
	 */
	public boolean shouldShowSplitPane() {
		return "true".equals(controller.getProperty(
				FreeMind.RESOURCES_SHOW_ATTRIBUTE_PANE));
	}

	class JumpToMapAction extends AbstractAction {
		private static final long serialVersionUID = -531070508254258791L;

		public void actionPerformed(ActionEvent e) {
			logger.info("Jumping back to map!");
			controller.getController().obtainFocusForSelected();
		}
	};

	public void register() {
		mAttributeViewerComponent = new JPanel();
		mAttributeViewerComponent.add(new JLabel("Tabelle!"));
		// register "leave note" action:
		if (shouldShowSplitPane()) {
			showAttributeTablePanel();
		}
	}

	public void deRegister() {
		if (mAttributeViewerComponent != null && shouldShowSplitPane()) {
			hideAttributeTablePanel();
			mAttributeViewerComponent = null;
		}
	}

	public void showAttributeTablePanel() {
		mAttributeViewerComponent.setVisible(true);
		controller.getController().insertComponentIntoSplitPane(
				mAttributeViewerComponent, SplitComponentType.ATTRIBUTE_PANEL);
		mSplitPaneVisible  = true;
	}

	public void hideAttributeTablePanel() {
		// shut down the display:
		mAttributeViewerComponent.setVisible(false);
		controller.getController().removeSplitPane(SplitComponentType.ATTRIBUTE_PANEL);
		mSplitPaneVisible = false;
	}

	public boolean getSplitPaneVisible() {
		return mSplitPaneVisible;
	}

	public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
		return getSplitPaneVisible();
	}
}