package view;

import controller.PatientController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import model.Patient;

public class PatientView extends JPanel {

    private PatientController controller;

    private JTable table;
    private DefaultTableModel tableModel;

    // 14 CSV-matching fields
    private JLabel lblAutoId;
    private JTextField txtFirstName, txtLastName, txtDob, txtNhs, txtGender;
    private JTextField txtPhone, txtEmail;
    private JTextField txtAddress, txtPostcode;
    private JTextField txtEmergencyName, txtEmergencyPhone;
    private JTextField txtRegistrationDate, txtGpSurgery;

    public PatientView() {

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // ============================================================
    // TABLE (bottom of split pane)
    // ============================================================
    tableModel = new DefaultTableModel(
        new Object[]{
            "ID", "First Name", "Last Name", "DOB", "NHS",
            "Gender", "Phone", "Email", "Address", "Postcode",
            "Emergency Name", "Emergency Phone",
            "Registration Date", "GP Surgery ID"
        }, 0
    );

    table = new JTable(tableModel);
    table.setRowHeight(22);

        // ============================================================
        // FORM (CENTER) — 4 columns using GridBagLayout
        // ============================================================
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 15, 10, 15);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Create fields
        lblAutoId = new JLabel("P001");

    txtFirstName = new JTextField();
    txtLastName = new JTextField();
    // Restrict first/last name inputs to letters, spaces, apostrophes and hyphens
    ((AbstractDocument) txtFirstName.getDocument()).setDocumentFilter(new LetterOnlyFilter());
    ((AbstractDocument) txtLastName.getDocument()).setDocumentFilter(new LetterOnlyFilter());
    txtDob = new JTextField();
        txtNhs = new JTextField();
        txtGender = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextField();
        txtPostcode = new JTextField();
        txtEmergencyName = new JTextField();
        txtEmergencyPhone = new JTextField();
        txtRegistrationDate = new JTextField();
        txtGpSurgery = new JTextField();

        // Row counter
        int row = 0;

        // ============================================================
        // ADD FORM ROWS (4 columns each)
        // ============================================================
        add4(form, gc, row++, "Patient ID:", lblAutoId, "First Name:", txtFirstName);
        add4(form, gc, row++, "Last Name:", txtLastName, "DOB (YYYY-MM-DD):", txtDob);
        add4(form, gc, row++, "NHS Number:", txtNhs, "Gender (M/F):", txtGender);
        add4(form, gc, row++, "Phone Number:", txtPhone, "Email:", txtEmail);
        add4(form, gc, row++, "Address:", txtAddress, "Postcode:", txtPostcode);
        add4(form, gc, row++, "Emergency Name:", txtEmergencyName,
                "Emergency Phone:", txtEmergencyPhone);
        add4(form, gc, row++, "Registration Date:", txtRegistrationDate,
                "GP Surgery ID:", txtGpSurgery);

    // ============================================================
    // BUTTONS
    // ============================================================
    JButton btnAdd = new JButton("Add Patient");
    JButton btnDelete = new JButton("Delete Selected");

    btnAdd.addActionListener(e -> onAdd());
    btnDelete.addActionListener(e -> onDelete());

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
    buttons.add(btnAdd);
    buttons.add(btnDelete);

    // Top panel contains buttons (north) and the form (center)
    JPanel topPanel = new JPanel(new BorderLayout(5, 5));
    topPanel.add(buttons, BorderLayout.NORTH);
    topPanel.add(form, BorderLayout.CENTER);

    // Table scroll pane (bottom)
    JScrollPane tableScroll = new JScrollPane(table);

    // Use a vertical split pane: top = inputs (buttons+form), bottom = records (table)
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, tableScroll);
    // Give top (inputs) ~55% of the height and bottom ~45% when resizing
    split.setResizeWeight(0.55);
    split.setOneTouchExpandable(true);

    add(split, BorderLayout.CENTER);
    }

    // =================================================================
    // Helper — Adds one ROW with 4 columns
    // =================================================================
    private void add4(JPanel panel, GridBagConstraints gc, int row,
                      String label1, JComponent field1,
                      String label2, JComponent field2) {

        gc.gridy = row;

        // Left label
        gc.gridx = 0; gc.weightx = 0.15;
        panel.add(new JLabel(label1), gc);

        // Left field
        gc.gridx = 1; gc.weightx = 0.35;
        panel.add(field1, gc);

        // Right label
        gc.gridx = 2; gc.weightx = 0.15;
        panel.add(new JLabel(label2), gc);

        // Right field
        gc.gridx = 3; gc.weightx = 0.35;
        panel.add(field2, gc);
    }

    // ============================================================
    // CONTROLLER LINK
    // ============================================================
    public void setController(PatientController controller) {
        this.controller = controller;
    }

    // ============================================================
    // SHOW PATIENTS
    // ============================================================
    public void showPatients(List<Patient> list) {
        tableModel.setRowCount(0);

        updateAutoId(list);

        for (Patient p : list) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getFirstName(), p.getLastName(),
                    p.getDateOfBirth(), p.getNhsNumber(), p.getGender(),
                    p.getPhoneNumber(), p.getEmail(), p.getAddress(),
                    p.getPostcode(), p.getEmergencyContactName(),
                    p.getEmergencyContactPhone(), p.getRegistrationDate(),
                    p.getGpSurgeryId()
            });
        }
    }

    // ============================================================
    // AUTO-ID P001 → P002
    // ============================================================
    private void updateAutoId(List<Patient> list) {
        if (list.isEmpty()) {
            lblAutoId.setText("P001");
            return;
        }

        String lastId = list.get(list.size() - 1).getId();
        int num = Integer.parseInt(lastId.substring(1)) + 1;
        lblAutoId.setText(String.format("P%03d", num));
    }

    // ============================================================
    // ADD PATIENT
    // ============================================================
    private void onAdd() {
        if (controller == null) return;
        // Validate first/last name (DocumentFilter prevents non-letters but ensure non-empty)
        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        if (first.isEmpty() || last.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name and last name are required and must contain only letters.");
            return;
        }

        Patient p = new Patient(
                lblAutoId.getText(),
                first,
                last,
                txtDob.getText(),
                txtNhs.getText(),
                txtGender.getText(),
                txtPhone.getText(),
                txtEmail.getText(),
                txtAddress.getText(),
                txtPostcode.getText(),
                txtEmergencyName.getText(),
                txtEmergencyPhone.getText(),
                txtRegistrationDate.getText(),
                txtGpSurgery.getText()
        );

        controller.addPatient(p);
    }

    // ============================================================
    // DELETE PATIENT
    // ============================================================
    private void onDelete() {
        if (controller == null) return;

        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a patient first!");
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();
        Patient p = controller.findById(id);

        if (p != null) controller.deletePatient(p);
    }

    // DocumentFilter that only permits letters, spaces, apostrophes and hyphens
    // This one validates the resulting full text so digits can't be inserted via typing or paste.
    private static class LetterOnlyFilter extends DocumentFilter {
        // Allowed characters: letters (A-Z,a-z), whitespace, apostrophe and hyphen
        private static final String ALLOWED_FULL_REGEX = "^[a-zA-Z\\s'\\-]*$";

        private boolean isResultingTextAllowed(javax.swing.text.Document doc, int offset, int length, String text) {
            try {
                String current = doc.getText(0, doc.getLength());
                StringBuilder sb = new StringBuilder();
                sb.append(current.substring(0, offset));
                if (text != null) sb.append(text);
                if (offset + length < current.length()) sb.append(current.substring(offset + length));
                String candidate = sb.toString();
                return candidate.matches(ALLOWED_FULL_REGEX);
            } catch (BadLocationException e) {
                return false;
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (isResultingTextAllowed(fb.getDocument(), offset, 0, string)) {
                super.insertString(fb, offset, string, attr);
            } else {
                // invalid input — reject and provide feedback
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            if (isResultingTextAllowed(fb.getDocument(), offset, length, text)) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}
