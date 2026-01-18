import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class StudyPortal {
    private List<Resource> resourceList = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> branchBox;
    private JLabel statusLabel;

    // Modern Colors
    private final Color SIDEBAR_COLOR = new Color(28, 35, 49);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new StudyPortal().createGUI());
    }

    public void createGUI() {
        loadData();

        JFrame frame = new JFrame("Engineering Nexus v8.0");
        frame.setSize(1100, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 750));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));

        JLabel logo = new JLabel("<html><font color='white' size='6'><b>NEXUS</b></font></html>");
        sidebar.add(logo);
        frame.add(sidebar, BorderLayout.WEST);

        // --- MAIN PANEL ---
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Search Section
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel headerTitle = new JLabel("Global Engineering Resource Library");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        branchBox = new JComboBox<>(new String[]{"All Departments", "Computer Science", "Mechanical", "Civil", "Electronics"});
        searchField = new JTextField(25);
        searchField.setToolTipText("Enter keywords like 'Agile', 'Linked List', or 'Thermodynamics'");

        filterBar.add(new JLabel("Department: "));
        filterBar.add(branchBox);
        filterBar.add(new JLabel("  Quick Search: "));
        filterBar.add(searchField);

        topPanel.add(headerTitle);
        topPanel.add(filterBar);

        // Results Table
        String[] columns = {"Dept", "Format", "Topic", "Link (Double-Click to Launch)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);

        // Listeners for Instant Filtering
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
        branchBox.addActionListener(e -> filterTable());

        // Double Click Logic
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) openBrowser((String) table.getValueAt(row, 3));
                }
            }
        });

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        statusLabel = new JLabel(" Ready.");
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);
        filterTable();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void filterTable() {
        tableModel.setRowCount(0);
        String dept = (String) branchBox.getSelectedItem();
        String query = searchField.getText().toLowerCase();
        int count = 0;

        for (Resource r : resourceList) {
            boolean matchesDept = dept.equals("All Departments") || r.department.equals(dept);
            boolean matchesQuery = r.title.toLowerCase().contains(query) || r.department.toLowerCase().contains(query);
            if (matchesDept && matchesQuery) {
                tableModel.addRow(new Object[]{r.department, r.type, r.title, r.url});
                count++;
            }
        }
        statusLabel.setText(" Found " + count + " educational resources.");
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionBackground(new Color(235, 245, 255));
    }

    private void openBrowser(String url) {
        try { Desktop.getDesktop().browse(new URI(url)); } catch (Exception ex) {}
    }

    private void loadData() {
        // --- COMPUTER SCIENCE ---
        resourceList.add(new Resource("Computer Science", "Video", "SE: Software Development Life Cycle (SDLC)", "https://nptel.ac.in/courses/106101061"));
        resourceList.add(new Resource("Computer Science", "PDF", "SE: Agile & Scrum Framework", "https://www.agilealliance.org/agile101/"));
        resourceList.add(new Resource("Computer Science", "Video", "DSA: Linked Lists & Arrays", "https://nptel.ac.in/courses/106102064"));
        resourceList.add(new Resource("Computer Science", "PDF", "DSA: Sorting Algorithms", "https://visualgo.net/en/sorting"));
        resourceList.add(new Resource("Computer Science", "Video", "OS: CPU Scheduling", "https://nptel.ac.in/courses/106106144"));
        resourceList.add(new Resource("Computer Science", "Video", "LDCO: Logic Design", "https://nptel.ac.in/courses/106105185"));
        resourceList.add(new Resource("Computer Science", "PDF", "OOPS: Java Programming", "https://docs.oracle.com/javase/tutorial/"));

        // --- MECHANICAL ---
        resourceList.add(new Resource("Mechanical", "Video", "Thermodynamics Basics", "https://nptel.ac.in/courses/112105123"));
        resourceList.add(new Resource("Mechanical", "PDF", "Fluid Mechanics", "https://nptel.ac.in/courses/112105171"));

        // --- CIVIL ---
        resourceList.add(new Resource("Civil", "Video", "Structural Analysis", "https://nptel.ac.in/courses/105105166"));
        resourceList.add(new Resource("Civil", "PDF", "Environmental Engineering", "https://nptel.ac.in/courses/105104099"));

        // --- ELECTRONICS ---
        resourceList.add(new Resource("Electronics", "Video", "Digital Circuits", "https://nptel.ac.in/courses/117106086"));
        resourceList.add(new Resource("Electronics", "PDF", "Analog Communication", "https://nptel.ac.in/courses/117105143"));
    }
}