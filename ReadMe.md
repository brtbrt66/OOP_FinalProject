Cinema Booking System 

2. Description / Overview

The Cinema Booking System is a Java desktop application designed to simulate how a real cinema ticket booking system works. It allows users to view available movies, check showtimes, select seats, and complete a booking through a simple and interactive graphical interface. The system automatically calculates the total cost, applies tax, generates a receipt, and saves booking records for future viewing. This project was created to demonstrate Object-Oriented Programming concepts while building a practical and user-friendly application.

3. OOP Concepts Applied
Encapsulation

Each part of the system is organized into classes that group related data and behavior together. For example, the Movie class stores movie details, while the Booking class handles customer and booking information. This keeps the code clean, organized, and easier to manage.

Abstraction

Processes such as seat selection, checkout, and receipt generation are separated into their own dialog windows. This allows users to focus on completing tasks without needing to understand how everything works behind the scenes.

Inheritance

The application makes use of inheritance by extending Swing classes like JFrame and JDialog. This allows the program to reuse existing GUI functionality while adding its own features.

Polymorphism

Some methods are overridden to customize behavior, such as disabling table editing or customizing how panels are drawn. This lets different components behave differently while still following the same structure.

4. Program Structure
Main Classes and Their Roles

Cinema_FullSystem – The main window of the application. It controls the overall flow of the system, manages movies and bookings, and handles user actions.

Movie – Represents a movie with its title, genre, rating, showtimes, and ticket price.

Booking – Stores all details of a completed booking, including customer information, selected seats, and total cost.

SeatSelectionDialog – Displays the seat layout and ensures that already booked seats cannot be selected again.

CheckoutDialog – Shows the price breakdown, collects user details, and confirms the booking.

ReceiptDialog – Displays a receipt after booking and allows the user to save it as a text file.

PastBookingsDialog – Shows a list of all previous bookings saved in the system.

PopcornPanel – A custom panel used for the sidebar background design.

5. How to Run the Program
Requirements

Java Development Kit (JDK) 8 or higher

A Java IDE (such as IntelliJ IDEA, Eclipse, or NetBeans) or Command Prompt

Steps

Save the main file as Cinema_FullSystem.java.

Compile the program using:

javac Cinema_FullSystem.java


Run the program using:

java Cinema_FullSystem


The Cinema Booking System window will appear and is ready to use.

6. Sample Output
******** Cinema Receipt ********
Movie: Avengers: Endgame
Showtime: 7:00 PM
Seats: A1, A2
Total: 537.60
*******************************


7. Author and Acknowledgement

Author:
James Kirby Ignacio
Joshua Arante

Acknowledgement:
I would like to thank our instructor for guidance throughout this project, as well as online resources such as W3 schools and tutorials that helped in understanding Java and Object-Oriented Programming concepts.

8. Other Sections
Future Enhancements

Add user accounts and login functionality

Connect the system to a database

Improve the seat selection interface

Add support for multiple cinema halls

Integrate real online payment methods

References:
Online programming tutorials and forums
