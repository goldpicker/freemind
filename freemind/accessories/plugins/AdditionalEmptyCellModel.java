/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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
*/

package accessories.plugins;

import java.util.EventListener;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import accessories.plugins.NodeAttributeTableRegistration.ChangeValueInterface;

public final class AdditionalEmptyCellModel extends AbstractTableModel {

	
	private AbstractTableModel mParentModel;
	private ChangeValueInterface mChangeValueInterface;

	/**
	 * 
	 */
	public AdditionalEmptyCellModel(AbstractTableModel parentModel, ChangeValueInterface changeValueInterface) {
		mParentModel = parentModel;
		mChangeValueInterface = changeValueInterface;
	}
	
	public void removeRow(int pRowIndex) {
		if(pRowIndex >= mParentModel.getRowCount()+1) {
			throw new IllegalArgumentException(pRowIndex+" is out of range.");
		}
		if(pRowIndex == mParentModel.getRowCount()) {
			return;
		}
		mChangeValueInterface.removeValue(pRowIndex);
	}
	
	@Override
	public int getRowCount() {
		return mParentModel.getRowCount() +1;
	}

	@Override
	public int getColumnCount() {
		return mParentModel.getColumnCount();
	}

	@Override
	public Object getValueAt(int pRowIndex, int pColumnIndex) {
		if(pRowIndex >= mParentModel.getRowCount()) {
			return "";
		}
		return mParentModel.getValueAt(pRowIndex, pColumnIndex);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object pAValue, int pRowIndex, int pColumnIndex) {
		if(pRowIndex >= mParentModel.getRowCount()) {
			if (pAValue != null && !((String)pAValue).isEmpty()) {
				// add new value.
				mChangeValueInterface.addValue(pAValue, pColumnIndex);
			}
		} else {
			mParentModel.setValueAt(pAValue, pRowIndex, pColumnIndex);
		}
	}
	
	/**
	 * @param pColumn
	 * @return
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int pColumn) {
		return mParentModel.getColumnName(pColumn);
	}

	/**
	 * @param pColumnIndex
	 * @return
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class<?> getColumnClass(int pColumnIndex) {
		return mParentModel.getColumnClass(pColumnIndex);
	}

	/**
	 * @param pRowIndex
	 * @param pColumnIndex
	 * @return
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int pRowIndex, int pColumnIndex) {
		if(pRowIndex >= mParentModel.getRowCount()) {
			// only the key is editable, because it has to be there, first.
			return pColumnIndex == NodeAttributeTableRegistration.KEY_COLUMN;
		}
		return mParentModel.isCellEditable(pRowIndex, pColumnIndex);
	}

	/**
	 * @param pColumnName
	 * @return
	 * @see javax.swing.table.AbstractTableModel#findColumn(java.lang.String)
	 */
	public int findColumn(String pColumnName) {
		return mParentModel.findColumn(pColumnName);
	}

	/**
	 * @param pL
	 * @see javax.swing.table.AbstractTableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void addTableModelListener(TableModelListener pL) {
		mParentModel.addTableModelListener(pL);
	}

	/**
	 * @param pL
	 * @see javax.swing.table.AbstractTableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void removeTableModelListener(TableModelListener pL) {
		mParentModel.removeTableModelListener(pL);
	}

	/**
	 * @return
	 * @see javax.swing.table.AbstractTableModel#getTableModelListeners()
	 */
	public TableModelListener[] getTableModelListeners() {
		return mParentModel.getTableModelListeners();
	}

	/**
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableDataChanged()
	 */
	public void fireTableDataChanged() {
		mParentModel.fireTableDataChanged();
	}

	/**
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableStructureChanged()
	 */
	public void fireTableStructureChanged() {
		mParentModel.fireTableStructureChanged();
	}

	/**
	 * @param pFirstRow
	 * @param pLastRow
	 * @see javax.swing.table.AbstractTableModel#fireTableRowsInserted(int, int)
	 */
	public void fireTableRowsInserted(int pFirstRow, int pLastRow) {
		mParentModel.fireTableRowsInserted(pFirstRow, pLastRow);
	}

	/**
	 * @param pFirstRow
	 * @param pLastRow
	 * @see javax.swing.table.AbstractTableModel#fireTableRowsUpdated(int, int)
	 */
	public void fireTableRowsUpdated(int pFirstRow, int pLastRow) {
		mParentModel.fireTableRowsUpdated(pFirstRow, pLastRow);
	}

	/**
	 * @param pFirstRow
	 * @param pLastRow
	 * @see javax.swing.table.AbstractTableModel#fireTableRowsDeleted(int, int)
	 */
	public void fireTableRowsDeleted(int pFirstRow, int pLastRow) {
		mParentModel.fireTableRowsDeleted(pFirstRow, pLastRow);
	}

	/**
	 * @param pRow
	 * @param pColumn
	 * @see javax.swing.table.AbstractTableModel#fireTableCellUpdated(int, int)
	 */
	public void fireTableCellUpdated(int pRow, int pColumn) {
		mParentModel.fireTableCellUpdated(pRow, pColumn);
	}

	/**
	 * @param pE
	 * @see javax.swing.table.AbstractTableModel#fireTableChanged(javax.swing.event.TableModelEvent)
	 */
	public void fireTableChanged(TableModelEvent pE) {
		mParentModel.fireTableChanged(pE);
	}

	/**
	 * @param pListenerType
	 * @return
	 * @see javax.swing.table.AbstractTableModel#getListeners(java.lang.Class)
	 */
	public <T extends EventListener> T[] getListeners(Class<T> pListenerType) {
		return mParentModel.getListeners(pListenerType);
	}
	
}