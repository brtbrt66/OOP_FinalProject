import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Cinema_FullSystem extends JFrame {
    private JPanel sidebar;
    private JTable table;
    private DefaultTableModel model;
    private java.util.List<Movie> movies = new ArrayList<>();
    private java.util.List<Booking> bookings = new ArrayList<>();
    private final Path BOOKINGS_FILE = Paths.get(System.getProperty("user.home"), "cinema_bookings.csv");
    private BufferedImage popcornImage = null;

    public Cinema_FullSystem() {
        setTitle("🎬 Movie Booking System");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadMovies();
        loadBookingsFromFile();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        add(mainPanel, BorderLayout.CENTER);

        sidebar = new PopcornPanel();
        sidebar.setPreferredSize(new Dimension(320, 650));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(sidebar, BorderLayout.WEST);

        sidebar.add(makeButton("🎬 View Movies", this::viewMovies));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(makeButton("⭐ Sort by Rating", this::sortByRating));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(makeButton("💺 Book Seats", this::bookSeats));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(makeButton("📄 View Past Bookings", this::viewPastBookings));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(makeButton("🧾 Save All & Export CSV", this::exportCsv));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(makeButton("❌ Exit", () -> System.exit(0)));

        String[] columns = {"Movie Title", "Genre", "Rating", "Showtime", "Price"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.BLACK);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        refreshMovieTable();

        setVisible(true);
    }

    private JButton makeButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 0, 0, 170));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        btn.setMaximumSize(new Dimension(260, 48));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 40, 40));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 0, 0, 170));
            }
        });

        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void loadMovies() {
        movies.clear();
        movies.add(new Movie("Avengers: Endgame", "Action", 9.1, Arrays.asList("2:00 PM", "7:00 PM"), 240.00));
        movies.add(new Movie("Inception", "Sci-Fi", 8.8, Arrays.asList("12:00 PM", "9:00 PM"), 200.00));
        movies.add(new Movie("Joker", "Drama", 8.5, Arrays.asList("6:30 PM", "10:00 PM"), 180.00));
        movies.add(new Movie("Moana 2", "Animation", 7.9, Arrays.asList("11:00 AM", "5:00 PM"), 150.00));
    }

    private void refreshMovieTable() {
        model.setRowCount(0);
        DecimalFormat df = new DecimalFormat("0.00");
        for (Movie m : movies) {
            String show = m.showtimes.isEmpty() ? "-" : m.showtimes.get(0);
            model.addRow(new Object[]{m.title, m.genre, String.valueOf(m.rating), show, df.format(m.price)});
        }
    }

    private void sortByRating() {
        movies.sort((a, b) -> Double.compare(b.rating, a.rating));
        refreshMovieTable();
        JOptionPane.showMessageDialog(this, "Movies sorted by rating (highest → lowest)");
    }

    private void viewMovies() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Select a movie in the table to view details.");
            return;
        }
        Movie m = movies.get(sel);
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(m.title).append('\n');
        sb.append("Genre: ").append(m.genre).append('\n');
        sb.append("Rating: ").append(m.rating).append('\n');
        sb.append("Showtimes: ").append(String.join(", ", m.showtimes)).append('\n');
        sb.append("Price per seat: ").append(new DecimalFormat("0.00").format(m.price)).append('\n');
        JOptionPane.showMessageDialog(this, sb.toString(), "Movie Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void bookSeats() {
        int sel = table.getSelectedRow();
        Movie movie = null;
        if (sel != -1) movie = movies.get(sel);

        if (movie == null) {

            String[] options = movies.stream().map(m -> m.title).toArray(String[]::new);
            String chosen = (String) JOptionPane.showInputDialog(this, "Select a movie to book:", "Choose Movie",
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (chosen == null) return;
            for (Movie m : movies) if (m.title.equals(chosen)) movie = m;
        }

        String show = (String) JOptionPane.showInputDialog(this, "Select showtime:", "Showtime",
                JOptionPane.PLAIN_MESSAGE, null, movie.showtimes.toArray(new String[0]), movie.showtimes.get(0));
        if (show == null) return;

        SeatSelectionDialog seatDialog = new SeatSelectionDialog(this, movie, show);
        seatDialog.setVisible(true);

        java.util.List<String> selectedSeats = seatDialog.getSelectedSeats();
        if (selectedSeats.isEmpty()) {
            return;
        }

        CheckoutDialog checkout = new CheckoutDialog(this, movie, show, selectedSeats);
        checkout.setVisible(true);
        Booking completed = checkout.getCompletedBooking();
        if (completed != null) {
            bookings.add(completed);
            appendBookingToFile(completed);
            JOptionPane.showMessageDialog(this, "Booking completed! Receipt will be shown.");
            ReceiptDialog r = new ReceiptDialog(this, completed);
            r.setVisible(true);
        }
    }

    private void viewPastBookings() {
        new PastBookingsDialog(this, bookings).setVisible(true);
    }

    private void exportCsv() {
        try {
            saveAllBookingsToFile();
            JOptionPane.showMessageDialog(this, "All bookings saved to: " + BOOKINGS_FILE.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save bookings: " + ex.getMessage());
        }
    }

    private void loadBookingsFromFile() {
    bookings.clear();
    if (!Files.exists(BOOKINGS_FILE)) return;
    try (BufferedReader br = Files.newBufferedReader(BOOKINGS_FILE)) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\|", -1);
            if (parts.length >= 7) {
                Booking b = new Booking(parts[0], parts[1], parts[2], parts[3], parts[4],
                                        Arrays.asList(parts[5].split(",")),
                                        Double.parseDouble(parts[6]));
                bookings.add(b);
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

    private void appendBookingToFile(Booking b) {
        try {
            Files.createDirectories(BOOKINGS_FILE.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(BOOKINGS_FILE, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                bw.write(String.join("|", b.timestamp, b.name, b.contact, b.movie, b.showtime, String.join(",", b.seats), String.format(Locale.US, "%.2f", b.total)));
                bw.newLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveAllBookingsToFile() throws IOException {
        Files.createDirectories(BOOKINGS_FILE.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(BOOKINGS_FILE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Booking b : bookings) {
                bw.write(String.join("|", b.timestamp, b.name, b.contact, b.movie, b.showtime, String.join(",", b.seats), String.format(Locale.US, "%.2f", b.total)));
                bw.newLine();
            }
        }
    }


    class Movie {
        String title;
        String genre;
        double rating;
        java.util.List<String> showtimes;
        double price;

        Movie(String t, String g, double r, java.util.List<String> s, double p) {
            title = t; genre = g; rating = r; showtimes = new ArrayList<>(s); price = p;
        }
    }

    class Booking {
        String timestamp;
        String name;
        String contact;
        String movie;
        String showtime;
        java.util.List<String> seats;
        double total;

        Booking(String ts, String n, String c, String m, String s, java.util.List<String> seats, double t) {
            timestamp = ts; name = n; contact = c; movie = m; showtime = s; this.seats = new ArrayList<>(seats); total = t;
        }
    }

    class SeatSelectionDialog extends JDialog {
        private java.util.List<String> selectedSeats = new ArrayList<>();
        private java.util.Set<String> occupied = new HashSet<>();

        SeatSelectionDialog(JFrame owner, Movie movie, String showtime) {
            super(owner, "Select Seats - " + movie.title + " (" + showtime + ")", true);
            setSize(700, 500);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());

            for (Booking b : bookings) {
                if (b.movie.equals(movie.title) && b.showtime.equals(showtime)) occupied.addAll(b.seats);
            }

            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setBorder(new EmptyBorder(12, 12, 12, 12));

            JLabel info = new JLabel("Select seats (click to toggle). Occupied seats are disabled.");
            center.add(info);
            center.add(Box.createVerticalStrut(10));

            JPanel grid = new JPanel(new GridLayout(6, 8, 6, 6));

            for (char r = 'A'; r <= 'F'; r++) {
                for (int c = 1; c <= 8; c++) {
                    String seat = r + String.valueOf(c);
                    JToggleButton tb = new JToggleButton(seat);
                    tb.setFocusPainted(false);
                    if (occupied.contains(seat)) {
                        tb.setEnabled(false);
                        tb.setSelected(true);
                        tb.setBackground(Color.GRAY);
                    }
                    tb.addItemListener(ev -> {
                        if (!tb.isEnabled()) return;
                        if (tb.isSelected()) {
                            selectedSeats.add(seat);
                        } else {
                            selectedSeats.remove(seat);
                        }
                    });
                    grid.add(tb);
                }
            }

            center.add(grid);
            add(center, BorderLayout.CENTER);

            JPanel bottom = new JPanel();
            JButton btnCancel = new JButton("Cancel");
            JButton btnNext = new JButton("Proceed to Checkout");
            bottom.add(btnCancel);
            bottom.add(btnNext);
            add(bottom, BorderLayout.SOUTH);

            btnCancel.addActionListener(e -> {
                selectedSeats.clear();
                dispose();
            });

            btnNext.addActionListener(e -> {
                if (selectedSeats.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select at least one seat.");
                    return;
                }
                dispose();
            });
        }

        java.util.List<String> getSelectedSeats() {
            return new ArrayList<>(selectedSeats);
        }
    }

    class CheckoutDialog extends JDialog {
        private Booking completed = null;

        CheckoutDialog(JFrame owner, Movie movie, String showtime, java.util.List<String> seats) {
            super(owner, "Checkout - " + movie.title, true);
            setSize(460, 380);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());

            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(new EmptyBorder(12, 12, 12, 12));

            p.add(new JLabel("Movie: " + movie.title));
            p.add(new JLabel("Showtime: " + showtime));
            p.add(new JLabel("Seats: " + String.join(", ", seats)));
            p.add(Box.createVerticalStrut(8));

            double subtotal = movie.price * seats.size();
            double tax = subtotal * 0.12;
            double total = subtotal + tax;

            DecimalFormat df = new DecimalFormat("0.00");
            p.add(new JLabel("Price per seat: " + df.format(movie.price)));
            p.add(new JLabel("Subtotal: " + df.format(subtotal)));
            p.add(new JLabel("Tax (12%): " + df.format(tax)));
            p.add(Box.createVerticalStrut(6));
            p.add(new JLabel("TOTAL: " + df.format(total)));
            p.add(Box.createVerticalStrut(12));

            p.add(new JLabel("Your name:"));
            JTextField tfName = new JTextField();
            p.add(tfName);
            p.add(Box.createVerticalStrut(6));
            p.add(new JLabel("Contact (phone/email):"));
            JTextField tfContact = new JTextField();
            p.add(tfContact);

            add(p, BorderLayout.CENTER);

            JPanel bottom = new JPanel();
            JButton cancel = new JButton("Cancel");
            JButton pay = new JButton("Pay (Mock)");
            bottom.add(cancel);
            bottom.add(pay);
            add(bottom, BorderLayout.SOUTH);

            cancel.addActionListener(e -> dispose());

            pay.addActionListener(e -> {
                String name = tfName.getText().trim();
                String contact = tfContact.getText().trim();
                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter name and contact.");
                    return;
                }
                String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                completed = new Booking(ts, name, contact, movie.title, showtime, seats, total);
                dispose();
            });
        }

        Booking getCompletedBooking() {
            return completed;
        }
    }

    class ReceiptDialog extends JDialog {
        ReceiptDialog(JFrame owner, Booking b) {
            super(owner, "Receipt", true);
            setSize(520, 520);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());

            JTextArea ta = new JTextArea();
            ta.setEditable(false);
            ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            StringBuilder sb = new StringBuilder();
            sb.append("******** Cinema Receipt ********\n");
            sb.append("Timestamp: ").append(b.timestamp).append('\n');
            sb.append("Name: ").append(b.name).append('\n');
            sb.append("Contact: ").append(b.contact).append('\n');
            sb.append("Movie: ").append(b.movie).append('\n');
            sb.append("Showtime: ").append(b.showtime).append('\n');
            sb.append("Seats: ").append(String.join(", ", b.seats)).append('\n');
            sb.append("Total: ").append(String.format(Locale.US, "%.2f", b.total)).append('\n');
            sb.append("********************************\n");

            ta.setText(sb.toString());

            add(new JScrollPane(ta), BorderLayout.CENTER);

            JPanel bottom = new JPanel();
            JButton close = new JButton("Close");
            JButton save = new JButton("Save Receipt as TXT");
            bottom.add(save);
            bottom.add(close);
            add(bottom, BorderLayout.SOUTH);

            close.addActionListener(e -> dispose());

            save.addActionListener(e -> {
                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File("receipt_" + b.timestamp.replaceAll("[: ]", "_") + ".txt"));
                int r = fc.showSaveDialog(this);
                if (r == JFileChooser.APPROVE_OPTION) {
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fc.getSelectedFile()))) {
                        bw.write(ta.getText());
                        JOptionPane.showMessageDialog(this, "Receipt saved.");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Failed to save receipt: " + ex.getMessage());
                    }
                }
            });
        }
    }

    class PastBookingsDialog extends JDialog {
        PastBookingsDialog(JFrame owner, java.util.List<Booking> bookings) {
            super(owner, "Past Bookings", true);
            setSize(720, 420);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());

            String[] cols = {"Timestamp", "Name", "Contact", "Movie", "Showtime", "Seats", "Total"};
            DefaultTableModel tm = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            JTable t = new JTable(tm);
            t.setRowHeight(26);
            for (Booking b : bookings) {
                tm.addRow(new Object[]{b.timestamp, b.name, b.contact, b.movie, b.showtime, String.join(",", b.seats), String.format(Locale.US, "%.2f", b.total)});
            }
            add(new JScrollPane(t), BorderLayout.CENTER);

            JPanel bottom = new JPanel();
            JButton close = new JButton("Close");
            bottom.add(close);
            add(bottom, BorderLayout.SOUTH);

            close.addActionListener(e -> dispose());
        }
    }

    class PopcornPanel extends JPanel {
        BufferedImage image;

        public PopcornPanel() {
            String path = System.getProperty("c:\\Users\\RYZEN  5 3500X") + File.separator + "Downloads" + File.separator + "Simply-Recipes-Perfect-Popcorn-LEAD-41-4a75a18443ae45aa96053f30a3ed0a6b.jfif\r\n" +
                                "            try {";
            try {
                image = ImageIO.read(new File(path));
            } catch (Exception e) {
                image = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            if (image != null) {
                Image scaled = image.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                g.drawImage(scaled, 0, 0, null);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRect(0, 0, panelWidth, panelHeight);
            } else {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.DARK_GRAY, panelWidth, panelHeight, Color.BLACK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, panelWidth, panelHeight);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Cinema_FullSystem());
    }
}
