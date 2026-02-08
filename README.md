# Invoice Maker
A robust desktop application designed to streamline the invoicing process for freelancers. Built with JavaFX and powered by Supabase, this tool allows you to manage clients, track history, and generate professional PDF invoices with cloud synchronization.

I originally developed this project as a solution for my own freelance business to move away from manual spreadsheets and paid subscriptions. It is designed to be lightweight, reliable, and secure, keeping your financial data synchronized between your local machine and your private cloud database.

## 🚀 Key Features

*   **PDF Generation**: Automatically generates clean, professional PDF invoices ready to email to clients.
*   **Supabase Integration**: Seamlessly syncs customers and invoice metadata to a cloud database, ensuring data persistence and multi-device readiness.
*   **Client Management**: Stores customer details for quick access, eliminating repetitive data entry.
*   **Service History & PDF Scraping**: Automatically extracts service history from existing PDF invoices to show what you've done for a client in the past.
*   **Multi-Language Support**: Support for English and Slovak languages, including proper character encoding (Arial font) for Slovak invoices.
*   **Bank & Company Profiles**: Saves your business and bank information to auto-populate invoice headers and payment instructions.
*   **Dynamic File Management**: Automatically monitors your save folders and configuration files using Java's `WatchService` and Virtual Threads to keep the UI in sync.

## 🛠 Technical Stack

*   **Java 21+** (utilizing Project Loom Virtual Threads)
*   **JavaFX 21** (UI Framework)
*   **Supabase** (PostgreSQL + PostgREST for backend storage)
*   **iText 7** (PDF Engine)
*   **Jackson** (JSON Processing)
*   **Maven** (Build Tool)

## 📋 Roadmap Progress

### ✅ Completed (Recently Added)
- [x] **Cloud Synchronization**: Migration to Supabase for customer and invoice tracking.
- [x] **Seller Information**: Complete management of company and bank details.
- [x] **Client Management Tab**: New interface to add, edit, and delete saved clients.
- [x] **Internationalization**: Support for Slovak language and characters.
- [x] **Initial Configuration**: Automated setup popup for first-time users.
- [x] **Service History**: Automated PDF parsing to show past services per client.

### 🚀 Future Roadmap
- [ ] **Pdf Cloud**: Automatically upload PDFs to a cloud storage provider.
- [ ] **In-App PDF Preview**: View and edit PDFs directly within the application.
- [ ] **Multi-Device Sync**: Synchronize data between multiple devices.
- [ ] **Email Processing**: Automatically send invoices to customers via email.
- [ ] **Auto Reminders**: Email reminders for payment via bank APIs.
- [ ] **UI Refresh**: Modernizing the look and feel (beyond the "early 2000s" look).

