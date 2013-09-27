package mms.sniffer;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.SwingConstants;

public class SnifferDialog extends JDialog implements TableModelListener {
	
	private int mode;
	public boolean result = false;
	
	private final JPanel contentPanel = new JPanel();
	private JLabel lblName;
	private JLabel lblClass;
	private JLabel lblEventType;
	public JTextField txtName;
	public JTextField txtClass;
	public JTextField txtEvtType;
	private JTable table;
	public DefaultTableModel tableModel;

	/**
	 * Create the dialog.
	 */
	public SnifferDialog(final int mode) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		
		setBounds(100, 100, 343, 322);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 326, 242);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		{
			lblName = new JLabel("Name");
			lblName.setHorizontalAlignment(SwingConstants.RIGHT);
			lblName.setBounds(33, 15, 38, 14);
			contentPanel.add(lblName);
		}
		{
			txtName = new JTextField();
			txtName.setBounds(75, 12, 243, 20);
			contentPanel.add(txtName);
			txtName.setColumns(10);
		}
		{
			lblClass = new JLabel("Class");
			lblClass.setHorizontalAlignment(SwingConstants.RIGHT);
			lblClass.setBounds(35, 46, 36, 14);
			contentPanel.add(lblClass);
		}
		{
			txtClass = new JTextField();
			txtClass.setBounds(75, 43, 243, 20);
			contentPanel.add(txtClass);
			txtClass.setColumns(20);
		}
		{
			lblEventType = new JLabel("Event Type");
			lblEventType.setHorizontalAlignment(SwingConstants.RIGHT);
			lblEventType.setBounds(5, 77, 66, 14);
			contentPanel.add(lblEventType);
		}
		{
			txtEvtType = new JTextField();
			txtEvtType.setBounds(75, 74, 243, 20);
			contentPanel.add(txtEvtType);
			txtEvtType.setColumns(10);
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.setBounds(15, 102, 303, 139);
			contentPanel.add(panel);
			panel.setLayout(null);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(61, 22, 221, 106);
			panel.add(scrollPane);
			
			tableModel = new DefaultTableModel(
					new Object[][] {},
					new String[] {
						"NAME", "VALUE"
					}
				);
			table = new JTable();
			scrollPane.setViewportView(table);
			table.setModel(tableModel);
			table.setBorder(new LineBorder(Color.LIGHT_GRAY));
			table.setRowSelectionAllowed(false);
			
			JButton btnInsert = new JButton("+");
			btnInsert.setBounds(10, 80, 41, 23);
			panel.add(btnInsert);
			
			JButton btnRemove = new JButton("-");
			btnRemove.setBounds(10, 105, 41, 23);
			panel.add(btnRemove);
			btnRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int i = table.getSelectedRow();
					if (i >= 0) {
						tableModel.removeRow(i);
					}
				}
			});
			btnInsert.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tableModel.addRow(new String[] {"",""});
				}
			});
			table.getModel().addTableModelListener(this);

			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(10, 240, 307, 33);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("Create");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						result = true;
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		if (mode == 0) {
			setTitle("Create Agent...");
			lblEventType.setVisible(false);
			txtEvtType.setVisible(false);
		}
		else if (mode == 1) {
			setTitle("Create Component...");
		}
		
	}
	
	public void tableChanged(TableModelEvent e) {
//		int row = e.getFirstRow();
//		int column = e.getColumn();
//		TableModel model = (TableModel)e.getSource();
//		String columnName = model.getColumnName(column);
//		Object data = model.getValueAt(row, column);
	}
}
