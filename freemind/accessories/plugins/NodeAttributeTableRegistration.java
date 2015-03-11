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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import freemind.common.TextTranslator;
import freemind.controller.Controller.SplitComponentType;
import freemind.controller.MenuItemSelectedListener;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.NodeLifetimeListener;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.NodeView;

public class NodeAttributeTableRegistration implements HookRegistration,
		MenuItemSelectedListener {
	private final class AttributeManager implements NodeSelectionListener,
			NodeLifetimeListener {

		private boolean mDontUpdateModel = false;
		
		private MindMapNode mCurrentNode = null;
		
		@Override
		public void onCreateNodeHook(MindMapNode pNode) {
		}

		@Override
		public void onPreDeleteNode(MindMapNode pNode) {
		}

		@Override
		public void onPostDeleteNode(MindMapNode pNode, MindMapNode pParent) {
		}

		@Override
		public void onUpdateNodeHook(MindMapNode pNode) {
			if(mDontUpdateModel) {
				return;
			}
			if (pNode == controller.getSelected()) {
				if(!areModelAndNodeAttributesEqual(pNode)) {
					setModelFromNode(pNode);
				}
			}
		}

		@Override
		public void onFocusNode(NodeView pNode) {
			MindMapNode node = pNode.getModel();
			setModelFromNode(node);
		}

		/**
		 * @param node
		 */
		private void setModelFromNode(MindMapNode node) {
			mAttributeTableModel.clear();
			for (int position = 0; position < node.getAttributeTableLength(); position++) {
				Attribute attribute = node.getAttribute(position);
				mAttributeTableModel.addAttributeHolder(attribute);
			}
			mCurrentNode = node;
		}

		@Override
		public void onLostFocusNode(NodeView pNode) {
			// store its content:
			onSaveNode(pNode.getModel());
		}

		@Override
		public void onSaveNode(MindMapNode pNode) {
			try {
				mDontUpdateModel = true;
				// check correct node:
				if(pNode != mCurrentNode) {
					return;
				}
				// first check for changes:
				if(areModelAndNodeAttributesEqual(pNode)) {
					return;
				}
				// delete all attributes
				while(pNode.getAttributeTableLength()>0) {
					controller.removeAttribute(pNode, 0);
				}
				// write it from new.
				for (Iterator it = mAttributeTableModel.mData.iterator(); it.hasNext();) {
					AttributeHolder holder = (AttributeHolder) it.next();
					// add at end
					controller.addAttribute(pNode, new Attribute(holder.mKey, holder.mValue));
				}
			} finally {
				mDontUpdateModel = false;
			}
		}

		/**
		 * @param pNode
		 * @return
		 */
		private boolean areModelAndNodeAttributesEqual(MindMapNode pNode) {
			boolean equal = false;
			if(pNode.getAttributeTableLength() == mAttributeTableModel.mData.size()) {
				int index = 0;
				equal = true;
				for (Iterator it = mAttributeTableModel.mData.iterator(); it.hasNext();) {
					AttributeHolder holder = (AttributeHolder) it.next();
					Attribute attribute = pNode.getAttribute(index);
					if(Tools.safeEquals(holder.mKey, attribute.getName()) && Tools.safeEquals(holder.mValue, attribute.getValue())) {
						// ok
					} else {
						equal = false;
						break;
					}
					index++;
				}
			}
			return equal;
		}

		@Override
		public void onSelectionChange(NodeView pNode, boolean pIsSelected) {
		}

	}

	public static final class AttributeHolder {
		public String mKey;
		public String mValue;
	}

	public static final int KEY_COLUMN = 0;

	public static final int VALUE_COLUMN = 1;

	private static final String KEY_COLUMN_TEXT = "accessories/plugins/NodeAttributeTable_key";

	private static final String VALUE_COLUMN_TEXT = "accessories/plugins/NodeAttributeTable_value";

	private static final String DELETE_ROW_TEXT_ID = "accessories/plugins/NodeAttributeTable_delete_row_text_id";

	public static interface ChangeValueInterface {
		void addValue(Object pAValue, int pColumnIndex);
		void removeValue(int pRowIndex);
	}
	
	/**
	 * @author foltin
	 * @date 4.09.2014
	 */
	public final class AttributeTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private final String[] COLUMNS = new String[] { KEY_COLUMN_TEXT,
				VALUE_COLUMN_TEXT };
		Vector<AttributeHolder> mData = new Vector<AttributeHolder>();
		private final TextTranslator mTextTranslator;

		/**
		 */
		public AttributeTableModel(TextTranslator pTextTranslator) {
			super();
			mTextTranslator = pTextTranslator;
		}

		/**
		 * @param pAttribute
		 */
		public void addAttributeHolder(Attribute pAttribute) {
			AttributeHolder holder = new AttributeHolder();
			holder.mKey = pAttribute.getName();
			holder.mValue = pAttribute.getValue();
			addAttributeHolder(holder);
		}

		/**
		 * @param pAttribute
		 * @return 
		 */
		public int addAttributeHolder(AttributeHolder pAttribute) {
			mData.add(pAttribute);
			final int row = mData.size() - 1;
			fireTableRowsInserted(row, row);
			makeMapDirty();
			return row;
		}

		/**
		 * 
		 */
		private void makeMapDirty() {
			controller.setSaved(false);
		}

		public void removeAttributeHolder(int pIndex) {
			mData.remove(pIndex);
			makeMapDirty();
			fireTableRowsDeleted(pIndex, pIndex);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int arg0) {
			switch (arg0) {
			case KEY_COLUMN:
			case VALUE_COLUMN:
				return String.class;
			default:
				return Object.class;
			}
		}

		public AttributeHolder getAttributeHolder(int pIndex) {
			return mData.get(pIndex);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		public String getColumnName(int pColumn) {
			return mTextTranslator.getText(COLUMNS[pColumn]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return mData.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return 2;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int pRowIndex, int pColumnIndex) {
			final AttributeHolder attr = getAttributeHolder(pRowIndex);
			switch (pColumnIndex) {
			case KEY_COLUMN:
				return attr.mKey;
			case VALUE_COLUMN:
				return attr.mValue;
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int pRowIndex, int pColumnIndex) {
			return true;
		}
		
		/**
		 * 
		 */
		public void clear() {
			mData.clear();
			fireTableDataChanged();
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(Object pAValue, int pRowIndex, int pColumnIndex) {
			AttributeHolder holder;
			holder = getAttributeHolder(pRowIndex);
			switch(pColumnIndex) {
			case KEY_COLUMN:
				holder.mKey = (String) pAValue;
				break;
			case VALUE_COLUMN:
				holder.mValue = (String) pAValue;
				break;
			}
			makeMapDirty();
			fireTableCellUpdated(pRowIndex, pColumnIndex);
		}

	}

	private final MindMapController controller;

	private final java.util.logging.Logger logger;

	private boolean mSplitPaneVisible = false;

	private JPanel mAttributeViewerComponent = null;

	private JTable mAttributeTable;

	private AttributeTableModel mAttributeTableModel;

	private AttributeManager mAttributeManager;

	private JPopupMenu mPopupMenu;

	public NodeAttributeTableRegistration(ModeController controller, MindMap map) {
		this.controller = (MindMapController) controller;
		logger = Resources.getInstance().getLogger(this.getClass().getName());
	}

	/**
	 * @return true, if the split pane is to be shown. E.g. when freemind was
	 *         closed before, the state of the split pane was stored and is
	 *         restored at the next start.
	 */
	public boolean shouldShowSplitPane() {
		return "true".equals(controller
				.getProperty(FreeMind.RESOURCES_SHOW_ATTRIBUTE_PANE));
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
		mAttributeViewerComponent.setLayout(new BorderLayout());
		mAttributeTable = new NewLineTable();
		mAttributeTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		mAttributeTable.getTableHeader().setReorderingAllowed(false);
		mAttributeTableModel = new AttributeTableModel(controller);
		mAttributeTable.setModel(mAttributeTableModel);
		RowSorter<TableModel> sorter =
	             new TableRowSorter<TableModel>(mAttributeTableModel);
		mAttributeTable.setRowSorter(sorter);
		mAttributeViewerComponent.add(new JScrollPane(mAttributeTable), BorderLayout.CENTER);
		// register "leave note" action:
		if (shouldShowSplitPane()) {
			showAttributeTablePanel();
		}
		mAttributeManager = new AttributeManager();
		controller.registerNodeSelectionListener(mAttributeManager, false);
		controller.registerNodeLifetimeListener(mAttributeManager, true);
		mPopupMenu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(controller.getText(DELETE_ROW_TEXT_ID));
		mPopupMenu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Component c = (Component) e.getSource();
				JPopupMenu popup = (JPopupMenu) c.getParent();
				JTable table = (JTable) popup.getInvoker();
				mAttributeTableModel.removeAttributeHolder(table.convertRowIndexToModel(table.getSelectedRow()));
			}
		});
        mAttributeTable.addMouseListener( new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
            	logger.fine("pressed");
            	showPopup(e);
            }

            public void mouseReleased(MouseEvent e)
            {
            	logger.fine("released");
                showPopup(e);
            }

			/**
			 * @param e
			 */
			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger())
                {
                    JTable source = (JTable)e.getSource();
                    int row = source.rowAtPoint( e.getPoint() );
                    int column = source.columnAtPoint( e.getPoint() );

                    if (! source.isRowSelected(row))
                        source.changeSelection(row, column, false, false);

                    mPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                    e.consume();
                }
			}
        });
		
	}
	
	

	public void deRegister() {
		controller.deregisterNodeSelectionListener(mAttributeManager);
		controller.deregisterNodeLifetimeListener(mAttributeManager);
		if (mAttributeViewerComponent != null && shouldShowSplitPane()) {
			hideAttributeTablePanel();
			mAttributeViewerComponent = null;
		}
	}

	public void showAttributeTablePanel() {
		mAttributeViewerComponent.setVisible(true);
		controller.getController().insertComponentIntoSplitPane(
				mAttributeViewerComponent, SplitComponentType.ATTRIBUTE_PANEL);
		mSplitPaneVisible = true;
	}

	public void hideAttributeTablePanel() {
		// shut down the display:
		mAttributeViewerComponent.setVisible(false);
		controller.getController().removeSplitPane(
				SplitComponentType.ATTRIBUTE_PANEL);
		mSplitPaneVisible = false;
	}

	public boolean getSplitPaneVisible() {
		return mSplitPaneVisible;
	}

	public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
		return getSplitPaneVisible();
	}
	
	public void focusAttributeTable() {
		mAttributeTable.requestFocus();
		mAttributeTable.getSelectionModel().setSelectionInterval(0, 0);
	}
}