import java.awt.*;
import java.io.File;
import javax.swing.*;

// Adam Mounir
public class BookingConfirmationPanel extends JPanel {

    private MainApp mainApp;
    private JLabel fileStatusLabel; // shows if file has been uploaded
    private File uploadedIDFile; // stores uploaded file

    public BookingConfirmationPanel(MainApp mainApp) {
        this.mainApp = mainApp;

        // stacks components vertically w/ padding

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // title of page

        JLabel titleLabel = new JLabel("Booking Confirmation Page");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //shows booking details (status and driver assignment info)
        JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Your Booking Details"));
        detailsPanel.add(new JLabel("Status: Confirmed"));
        detailsPanel.add(new JLabel("Driver Assigned: Pending ID Verification"));
        detailsPanel.setMaximumSize(new Dimension(400, 100));
        detailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // section for uploading driver ID
        JPanel uploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        uploadPanel.setBorder(BorderFactory.createTitledBorder("Driver ID Verification"));

        JButton uploadButton = new JButton("Upload Driver's License");
        fileStatusLabel = new JLabel("No file uploaded");

        // opens file chooser, saves file and updates the label if confirmed
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                uploadedIDFile = fileChooser.getSelectedFile();
                fileStatusLabel.setText("Uploaded: " + uploadedIDFile.getName());
            }
        });

        uploadPanel.add(uploadButton);
        uploadPanel.add(fileStatusLabel);
        uploadPanel.setMaximumSize(new Dimension(400, 80));
        uploadPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // back button to menu page
        JButton backButton = new JButton("Back to Menu");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainApp.showPage("menu"));
        // add everything to the main panel with spacing

        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(detailsPanel);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(uploadPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(backButton);
    }
}