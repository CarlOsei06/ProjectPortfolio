import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.awt.*;

public class BookingConfirmationPanelTest {

    private BookingConfirmationPanel panel;
    private DummyMainApp dummyApp;

    class DummyMainApp extends MainApp {
        String lastPageRequested = null;

        @Override
        public void showPage(String name) {
            lastPageRequested = name;
        }
    }

    @BeforeEach
    public void setUp() {
        dummyApp = new DummyMainApp();
        panel = new BookingConfirmationPanel(dummyApp);
    }

    @Test
    public void testPanelInitialization() {
        assertNotNull(panel);
        assertTrue(panel.getComponentCount() > 0);
    }

    @Test
    public void testInitialFileStatusLabel() {
        JLabel fileLabel = findLabelByText(panel, "No file uploaded");
        assertNotNull(fileLabel);
    }

    @Test
    public void testBackButtonExists() {
        JButton backButton = findButtonByText(panel, "Back to Menu");
        assertNotNull(backButton);
    }

    @Test
    public void testBackButtonNavigatesToMenu() {
        JButton backButton = findButtonByText(panel, "Back to Menu");
        assertNotNull(backButton);
        backButton.doClick();
        assertEquals("menu", dummyApp.lastPageRequested);
    }

    @Test
    public void testUploadButtonExists() {
        JButton uploadButton = findButtonByText(panel, "Upload Driver's License");
        assertNotNull(uploadButton);
    }

    private JButton findButtonByText(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton btn && text.equals(btn.getText())) {
                return btn;
            } else if (comp instanceof Container c) {
                JButton found = findButtonByText(c, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    private JLabel findLabelByText(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel lbl && text.equals(lbl.getText())) {
                return lbl;
            } else if (comp instanceof Container c) {
                JLabel found = findLabelByText(c, text);
                if (found != null) return found;
            }
        }
        return null;
    }
}