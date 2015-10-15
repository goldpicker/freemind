package accessories.plugins.time;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import accessories.plugins.time.JTripleCalendar.JSwitchableCalendar;
import freemind.controller.actions.generated.instance.CalendarMarking;
import freemind.controller.actions.generated.instance.CalendarMarkings;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapController;

public class CalendarMarkingDialog extends JDialog {

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = "CalendarMarkingDialog_WindowPosition";
	public static final int CANCEL = -1;

	public static final int OK = 1;

	private MindMapController mController;
	private JPanel jContentPane;
	private JButton jOKButton;
	private JButton jCancelButton;
	private int result = CANCEL;
	private JSwitchableCalendar startDate;
	private JSwitchableCalendar endDate;
	private JColorChooser markerColor;
	private JComboBox<String> repetitionType;
	private JSpinner repeatEachNOccurence;
	private JSpinner firstOccurence;
	private JTextField nameField;
	private SpinnerNumberModel mRepeatEachNOccurenceModel;
	private SpinnerNumberModel mFirstOccurenceModel;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CalendarMarkingDialog dialog = new CalendarMarkingDialog(null);
		dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
		dialog.setVisible(true);
		System.out.println(dialog.getResult());
		CalendarMarking marking = dialog.getCalendarMarking();
		CalendarMarkings markings = new CalendarMarkings();
		markings.addCalendarMarking(marking);
		System.out.println(Tools.marshall(markings));
	}

	public CalendarMarkingDialog(MindMapController pController) {
		mController = pController;
		JPanel contentPane = getJContentPane();
		this.setContentPane(contentPane);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cancelPressed();
			}
		});
		Action cancelAction = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				cancelPressed();
			}
		};
		Tools.addEscapeActionToDialog(this, cancelAction);
		this.pack();
		if (mController != null) {
			WindowConfigurationStorage decorateDialog = (WindowConfigurationStorage) mController
					.decorateDialog(this, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		}
	}

	private void close() {
		if (mController != null) {
			WindowConfigurationStorage storage = new WindowConfigurationStorage();
			mController.storeDialogPositions(this, storage,
					WINDOW_PREFERENCE_STORAGE_PROPERTY);
		}
		this.dispose();

	}

	private void okPressed() {
		result = OK;
		// writePatternBackToModel();
		close();
	}

	private void cancelPressed() {
		result = CANCEL;
		close();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());
			int y =0;
			Insets insets = new Insets(5, 5, 5, 5);
			jContentPane.add(new JLabel(getText("Name")), new GridBagConstraints(0, y, 1, 1,
					1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					insets, 0, 0));
			nameField = new JTextField(80);
			jContentPane.add(nameField, new GridBagConstraints(1, y++, 1, 1,
					1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					insets, 0, 0));
			jContentPane.add(new JLabel(getText("Repetition_Type")), new GridBagConstraints(0, y, 1, 1,
					1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					insets, 0, 0));
			Vector<String> items = new Vector<>();
			items.add(getText("never"));
			items.add(getText("yearly"));
			items.add(getText("yearly_every_nth_day"));
			items.add(getText("yearly_every_nth_week"));
			items.add(getText("yearly_every_nth_month"));
			items.add(getText("monthly"));
			items.add(getText("monthly_every_nth_day"));
			items.add(getText("monthly_every_nth_week"));
			items.add(getText("weekly"));
			items.add(getText("weekly_every_nth_day"));
			items.add(getText("daily"));
			repetitionType = new JComboBox<String>(items);
			jContentPane.add(repetitionType, new GridBagConstraints(1, y++, 1, 1,
					1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					insets, 0, 0));
			jContentPane.add(new JLabel(getText("Repeat_Each_N_Occurence")), new GridBagConstraints(0, y, 1, 1,
					1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					insets, 0, 0));

			mRepeatEachNOccurenceModel = new SpinnerNumberModel(0, 0, 100, 1);
			repeatEachNOccurence = new JSpinner(mRepeatEachNOccurenceModel);
			jContentPane.add(repeatEachNOccurence, new GridBagConstraints(1, y++, 1, 1,
					1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					insets, 0, 0));
			jContentPane.add(new JLabel(getText("First_Occurence")), new GridBagConstraints(0, y, 1, 1,
					1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					insets, 0, 0));
			mFirstOccurenceModel = new SpinnerNumberModel(0, 0, 100, 1);
			firstOccurence = new JSpinner(mFirstOccurenceModel);
			jContentPane.add(firstOccurence, new GridBagConstraints(1, y++, 1, 1,
					1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					insets, 0, 0));
			jContentPane.add(new JLabel(getText("Start_Date")), new GridBagConstraints(0, y, 1, 1,
					1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					insets, 0, 0));
			startDate = new JSwitchableCalendar();
			startDate.setEnabled(true);
			startDate.addPropertyChangeListener(new PropertyChangeListener() {
				boolean ignoreNextEvent = false;
				@Override
				public void propertyChange(PropertyChangeEvent pEvt) {
					if (pEvt.getNewValue() instanceof Calendar) {
						if (!ignoreNextEvent) {
							Calendar cal = (Calendar) pEvt.getNewValue();
							ignoreNextEvent = true;
							startDate.setCalendar(cal);
						} else {
							ignoreNextEvent = false;
						}
					} 
				}
			});
			jContentPane.add(startDate, new GridBagConstraints(1, y++, 1, 1,
					1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
					insets, 0, 0));
			jContentPane.add(new JLabel(getText("End_Date")), new GridBagConstraints(0, y, 1, 1,
					1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					insets, 0, 0));
			endDate = new JSwitchableCalendar();
			endDate.setEnabled(true);
			endDate.addPropertyChangeListener(new PropertyChangeListener() {
				boolean ignoreNextEvent = false;
				@Override
				public void propertyChange(PropertyChangeEvent pEvt) {
					if (pEvt.getNewValue() instanceof Calendar) {
						if (!ignoreNextEvent) {
							Calendar cal = (Calendar) pEvt.getNewValue();
							ignoreNextEvent = true;
							endDate.setCalendar(cal);
						} else {
							ignoreNextEvent = false;
						}
					} 
				}
			});
			jContentPane.add(endDate, new GridBagConstraints(1, y++, 1, 1,
					1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
					insets, 0, 0));
			jContentPane.add(new JLabel(getText("Background_Color")), new GridBagConstraints(0, y, 1, 1,
					1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
					insets, 0, 0));
			markerColor = new JColorChooser();
			jContentPane.add(markerColor, new GridBagConstraints(1, y++, 1, 1,
					1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
					insets, 0, 0));
			jContentPane.add(getJOKButton(), new GridBagConstraints(0, y, 1, 1,
					1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					insets, 0, 0));
			jContentPane.add(getJCancelButton(), new GridBagConstraints(1, y,
					1, 1, 1.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, insets, 0, 0));
			getRootPane().setDefaultButton(getJOKButton());
		}
		return jContentPane;
	}

	public CalendarMarking getCalendarMarking() {
		CalendarMarking marking = new CalendarMarking();
		marking.setName(nameField.getText());
		marking.setColor(Tools.colorToXml(markerColor.getColor()));
		marking.setStartDate(startDate.getCalendar().getTimeInMillis());
		marking.setEndDate(endDate.getCalendar().getTimeInMillis());
		marking.setFirstOccurence(mFirstOccurenceModel.getNumber().intValue());
		marking.setRepeatEachNOccurence(mRepeatEachNOccurenceModel.getNumber().intValue());
		marking.setRepeatType(repetitionType.getSelectedItem().toString());
		return marking;
	}
	
	
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJOKButton() {
		if (jOKButton == null) {
			jOKButton = new JButton();

			jOKButton.setAction(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					okPressed();
				}

			});

			Tools.setLabelAndMnemonic(jOKButton, getText("ok"));
		}
		return jOKButton;
	}

	public String getText(String textId) {
		if (mController != null) {
			return mController.getText(textId);
		}
		return textId;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJCancelButton() {
		if (jCancelButton == null) {
			jCancelButton = new JButton();
			jCancelButton.setAction(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					cancelPressed();
				}
			});
			Tools.setLabelAndMnemonic(jCancelButton,
					getText(("cancel")));
		}
		return jCancelButton;
	}

	/**
	 * @return Returns the result.
	 */
	public int getResult() {
		return result;
	}

}
