import javax.swing.*;
import java.io.File;

public class AddFolderForm extends JFrame {
	private JTextField folderTextField;
	private JCheckBox setAsFavoriteCheckBox;
	private JButton browseButton;
	private JButton addFolderButton;
	private JPanel mainPanel;

	public AddFolderForm(String title) {
		this.setTitle(title);
		this.setContentPane(mainPanel);
		this.setLocationRelativeTo(null);

		addFolderButton.setEnabled(false);
		addFolderButton.addActionListener(e -> {
			File selectedFolder = new File(folderTextField.getText());

			try {
				QuickFolderAccess.addFolder(selectedFolder.getName(), selectedFolder.getAbsolutePath());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(AddFolderForm.this, ex.getMessage(), "QuickFolderAccess: Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (setAsFavoriteCheckBox.isSelected()) {
				QuickFolderAccess.setFavoriteFolder(selectedFolder.getName());
			}

			JOptionPane.showMessageDialog(AddFolderForm.this, "Folder Added: " + selectedFolder.getAbsolutePath(), "QuickFolderAccess: Success", JOptionPane.INFORMATION_MESSAGE);
			dispose();
		});

		browseButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int result = fileChooser.showOpenDialog(AddFolderForm.this);

			if (result == JFileChooser.APPROVE_OPTION) {
				String selectedFolder = fileChooser.getSelectedFile().getAbsolutePath();
				folderTextField.setText(selectedFolder);
				addFolderButton.setEnabled(true);
			}
		});

		this.pack();
		this.setVisible(true);
	}
}
