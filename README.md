# 🔐 Password Manager App

A secure Android application for managing passwords and credit card information with comprehensive local storage, advanced validation, modern UI features, and beautiful card-based design.

## ✨ Features

### 🔑 Password Management
- **Add Passwords**: Store passwords with context, username, expiry dates, and notes
- **Edit Passwords**: Full edit and delete functionality for existing passwords
- **View Passwords**: List all saved passwords with username, expiry date, and notes preview
- **Data Persistence**: Passwords are saved locally using SharedPreferences with JSON serialization
- **Expiry Tracking**: Monitor password expiration dates with smart date filtering
- **Notes Support**: Add detailed notes (up to 1000 characters) for each password entry
- **Username Field**: Store usernames separately from passwords for better organization

### 💳 Credit Card Management
- **Add Credit Cards**: Store comprehensive card details with bank name and notes
- **Edit Credit Cards**: Full edit and delete functionality for existing cards
- **View Credit Cards**: List all saved cards with masked numbers, bank name, and card type
- **Card Type Detection**: Automatic identification of card types (Visa, Mastercard, Amex, etc.)
- **Luhn Algorithm Validation**: Real-time card number validation with visual feedback
- **CVV Masking**: Secure CVV input with show/hide toggle functionality
- **Bank Name & Notes**: Optional fields for additional card information
- **Smart Expiry Date Selection**: Dynamic month filtering based on selected year
- **Beautiful Card Design**: Modern rounded cards with clean borders and proper spacing

### 🛡️ Security Features
- **Local Storage**: Data encrypted in SharedPreferences with Gson serialization
- **Card Number Masking**: Privacy protection for credit cards in lists
- **CVV Protection**: Masked CVV input with visibility toggle
- **Input Validation**: Comprehensive validation for all forms
- **Unique ID Generation**: UUID-based unique identifiers for all entries
- **Data Integrity**: Graceful handling of missing fields in existing data

### 🎨 User Interface
- **Material Design**: Modern Material Design components throughout
- **Navigation Component**: Smooth fragment-based navigation
- **ViewBinding**: Type-safe view access for better development experience
- **Responsive Layouts**: Optimized layouts for different screen sizes
- **Visual Feedback**: Color-coded validation messages and status indicators
- **Smart Spinners**: Dynamic expiry date selection with current date awareness
- **Vibrant Gradients**: Each card in the list is displayed with a unique, vibrant gradient, making the UI more dynamic and visually appealing.
- **3D Text Effect**: The card number has a subtle 3D effect, making it stand out.
- **Modern Card Design**: Rounded credit cards with clean borders and proper spacing.
- **Theme Support**: Light and dark theme compatibility.

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 26+ (Android 8.0+)
- Java 22 or later
- Gradle 8.9+

### Installation

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd PasswordManagerApp
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project folder and select it

3. **Build and Run**

   **Option 1: Using provided build scripts (Recommended)**
   ```bash
   # Windows - Full build and install
   build_and_install.bat
   
   # Windows - Build only
   build_only.bat
   
   # Windows - Quick build and install
   quick_build.bat
   ```

   **Option 2: Manual commands**
   ```bash
   # Build debug APK
   ./gradlew assembleDebug
   
   # Install on connected device/emulator
   ./gradlew installDebug
   
   # Or install manually
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

## 📱 Usage

### Password Management

#### Adding a Password
1. Open the app
2. Click "Add New Password"
3. Fill in the details:
   - **Context**: What the password is for (e.g., "Gmail", "Bank Account")
   - **Username**: Your username/email for the service
   - **Password**: Your actual password
   - **Expiry Date**: Select month and year (optional)
   - **Notes**: Additional information (optional, up to 1000 characters)
4. Click "Save Password"

#### Editing a Password
1. From the password list, tap on any password entry
2. Modify any fields as needed
3. Click "Update Password" to save changes
4. Click "Delete Password" to remove the entry

#### Password List Features
- **Username Display**: Shows username or "not stored" if empty
- **Expiry Date**: Displays formatted expiry date or "Never" if not set
- **Notes Preview**: Shows truncated notes with ellipsis if too long
- **Tap to Edit**: Tap any password to edit its details

### Credit Card Management

#### Adding a Credit Card
1. Navigate to "Credit Cards" section
2. Click "Add New Credit Card"
3. Fill in card details:
   - **Card Number**: Automatically detects card type and validates
   - **Card Holder Name**: Name on the card
   - **Bank Name**: Issuing bank (optional)
   - **Expiry Date**: Select month and year
   - **CVV**: Security code (masked with show/hide toggle)
   - **Notes**: Additional information (optional)
4. Real-time validation feedback is shown
5. Click "Save Credit Card"

#### Editing a Credit Card
1. From the credit card list, tap on any card entry
2. Modify any fields as needed
3. Real-time validation updates as you type
4. Click "Update Credit Card" to save changes
5. Click "Delete Credit Card" to remove the entry

#### Credit Card Features
- **Card Type Detection**: Automatically identifies Visa, Mastercard, Amex, Discover, JCB, Diners Club
- **Luhn Validation**: Real-time card number validation with visual feedback
- **Masked Display**: Card numbers shown as **** **** **** 1234 in lists
- **Bank Information**: Displays bank name if provided
- **Notes Support**: Optional notes for additional card information
- **Modern Design**: Beautiful rounded cards with clean borders and proper spacing

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/passwordmanager/
│   │   ├── data/
│   │   │   └── PasswordRepository.kt    # Data persistence with Gson
│   │   ├── model/
│   │   │   ├── PasswordEntry.kt         # Password data model with Parcelable
│   │   │   └── CreditCardEntry.kt       # Credit card data model with Parcelable
│   │   ├── ui/
│   │   │   ├── MainActivity.kt          # Main activity with navigation
│   │   │   ├── PasswordListFragment.kt  # Password list with edit functionality
│   │   │   ├── AddPasswordFragment.kt   # Add password form with enhanced fields
│   │   │   ├── EditPasswordFragment.kt  # Edit password form with smart date handling
│   │   │   ├── CreditCardListFragment.kt # Credit card list with edit functionality
│   │   │   ├── CreditCardFragment.kt    # Add credit card form with validation
│   │   │   └── EditCreditCardFragment.kt # Edit credit card form with validation
│   │   └── util/
│   │       ├── CardType.kt              # Card type detection and validation
│   │       ├── CardColorUtil.kt         # Card color utilities
│   │       ├── CardValidator.kt         # Credit card validation logic
│   │       └── StackedCardDecoration.kt # RecyclerView decoration for cards
│   └── res/
│       ├── layout/                      # UI layouts with Material Design
│       ├── navigation/                  # Navigation graphs
│       ├── drawable/                    # Custom drawables for UI elements
│       │   ├── card_rounded_border.xml  # Rounded card borders
│       │   ├── card_border_background.xml # Card border backgrounds
│       │   └── glassy_card_background.xml # Glassy card effects
│       └── values/                      # Resources and themes
├── build_and_install.bat               # Full build and install script
├── build_only.bat                      # Build only script
└── quick_build.bat                     # Quick build and install script
```

## 🔧 Technical Details

### Dependencies
- **AndroidX**: Modern Android development libraries
- **Navigation Component**: Fragment navigation with safe args
- **RecyclerView**: Efficient list display with custom adapters
- **ViewBinding**: Type-safe view access
- **Gson**: JSON serialization for data persistence
- **Material Design**: Modern UI components
- **Kotlin Parcelize**: Efficient data class serialization

### Data Storage
- **SharedPreferences**: Local data persistence with JSON serialization
- **Gson**: Advanced JSON handling with custom deserializers for data migration
- **UUID Generation**: Unique identifiers for all data entries

### Architecture
- **Repository Pattern**: Clean data access abstraction
- **Fragment-based Navigation**: Modern Android navigation with safe args
- **MVVM-ready**: Prepared for ViewModel integration
- **Custom Deserializers**: Graceful handling of schema changes

### Key Technical Features
- **Card Type Detection**: Regex-based card type identification
- **Luhn Algorithm**: Credit card number validation
- **Dynamic Spinner Logic**: Smart date selection with current date awareness
- **Data Migration**: Backward compatibility for existing data
- **Parcelable Models**: Efficient data passing between fragments
- **Modern Card Design**: Rounded corners with clean borders and proper spacing

## 🛠️ Development

### Building the Project

**Using Build Scripts (Recommended):**
```bash
# Windows - Full build and install
build_and_install.bat

# Windows - Build only
build_only.bat

# Windows - Quick build and install
quick_build.bat
```

**Manual Commands:**
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Clean build
./gradlew clean assembleDebug
```

### Installing on Emulator/Device
```bash
# Check connected devices
adb devices

# Install APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.example.passwordmanager/.ui.LoginActivity
```

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## 🎨 UI/UX Improvements

### Credit Card Design
- **Rounded Corners**: 20dp corner radius for modern appearance
- **Clean Borders**: 6dp border width with proper color contrast
- **Proper Spacing**: Optimized margins and padding for better visual hierarchy
- **Theme Support**: Light and dark theme compatibility
- **Responsive Layout**: Adapts to different screen sizes

### Visual Enhancements
- **Material Design**: Consistent Material Design components
- **Color Coding**: Visual feedback for validation states
- **Smooth Animations**: Fragment transitions and list animations
- **Accessibility**: Proper content descriptions and focus handling

## 🔒 Security Considerations

- **Local Storage**: Data stored locally with JSON serialization
- **Input Validation**: Comprehensive validation for all user inputs
- **Card Number Masking**: Privacy protection in list views
- **CVV Protection**: Masked input with visibility toggle
- **Data Integrity**: Robust error handling and data migration

## 🚧 Recent Updates & Enhancements

### ✅ Completed Features
- [x] **Edit & Delete Functionality**: Full CRUD operations for both passwords and credit cards
- [x] **Enhanced Data Models**: Added username, notes, bank name, and card type fields
- [x] **Card Type Detection**: Automatic identification of major card types
- [x] **Luhn Algorithm Validation**: Real-time credit card number validation
- [x] **CVV Masking**: Secure CVV input with show/hide toggle
- [x] **Smart Expiry Date Selection**: Dynamic month filtering based on current date
- [x] **Notes Support**: Multi-line notes field with character limits
- [x] **Data Persistence**: Robust local storage with Gson serialization
- [x] **UI Improvements**: Material Design components and better user experience
- [x] **Navigation Enhancement**: Proper fragment navigation with safe args
- [x] **Code Cleanup**: Removed unused code and optimized performance
- [x] **Modern Card Design**: Rounded credit cards with clean borders and proper spacing
- [x] **Build Scripts**: Windows batch scripts for easy building and installation
- [x] **Theme Support**: Light and dark theme compatibility for card backgrounds
- [x] **Visual Polish**: Improved spacing, margins, and overall visual hierarchy

### 🔄 Future Enhancements
- [ ] Biometric authentication
- [ ] Password strength checker
- [ ] Auto-fill integration
- [ ] Password generator
- [ ] Export/Import functionality
- [ ] Enhanced dark theme support
- [ ] Widget support
- [ ] Backup to cloud storage
- [ ] Search functionality
- [ ] Categories/Tags for organization
- [ ] Card color customization
- [ ] Advanced animations and transitions

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

If you encounter any issues or have questions, please:
1. Check the existing issues
2. Create a new issue with detailed information
3. Contact the development team

---

**Note**: This application provides secure local storage for sensitive information. For production use, consider implementing additional encryption and security measures.
