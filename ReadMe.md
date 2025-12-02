1. Overview

Cinema_FullSystem is a Java Swing desktop application that provides a small cinema/movie booking workflow. It includes a l table view of available movies, seat selection, checkout (mock payment), receipt generation (and saving), and persistent booking storage in a CSV file.

2. Features

GUI built with Java Swing (JFrame, JDialog, JTable, etc.).
Movie catalogue with title, genre, rating, showtimes and price.
Sort movies by rating.
Select movie/showtime and choose seats (6 rows × 8 columns layout).
Occupied seats are disabled for the same showtime (based on saved bookings).
Checkout dialog with subtotal, tax (12%), and mock payment.
Add booking record and append it to a CSV-like file for persistence.
View past bookings in a modal dialog.
Save a receipt as a `.txt` file.
Export all bookings (save/overwrite CSV file).

3. Runtime behavior & UI flow

1. The main table displays movies loaded from `loadMovies()`.
2. Select a movie row and click **View Movies** to see details, or click **Book Seats** to start booking.
3. If no row is selected, **Book Seats** will prompt to choose a movie in a dialog.
4. Choose a showtime, then pick seats in the `SeatSelectionDialog`. Occupied seats (from saved bookings) are disabled.
5. Proceed to `CheckoutDialog` to enter name and contact and complete the (mock) payment.
6. Completed bookings are appended to the CSV file and visible in **View Past Bookings**.
7. You can export/overwrite all bookings via **Save All & Export CSV**.

4. Suggested improvements / Enhancements

 Replace CSV with a lightweight embedded DB (SQLite via JDBC) for robust querying and concurrency.* Add input validation and better error dialogs for file I/O errors.
 Add search/filter for movies and a poster image per movie.
 Add export options (PDF receipts) using a library like iText or Apache PDFBox.
 Add unit tests for persistence and booking validation logic.


