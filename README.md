# 🔐 Password Manager App

A secure Android application for managing passwords and credit card information with **enterprise-grade AES-256-GCM encryption**, zero-knowledge security architecture, advanced validation, modern UI features, and beautiful card-based design.

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
- **Zero-Knowledge Encryption**: Master password serves as encryption key - no password hashes stored
- **AES-256-GCM Encryption**: Military-grade authenticated encryption for all sensitive data
- **PBKDF2 Key Derivation**: 100,000+ iterations with cryptographically secure salt
- **Verification Token System**: Authentication through decryption instead of password hashing
- **Secure Memory Management**: Automatic clearing of encryption keys and sensitive data
- **Session Security**: Auto-logout with key clearing when app goes to background
- **Card Number Masking**: Privacy protection for credit cards in lists
- **CVV Protection**: Masked CVV input with visibility toggle
- **Password Masking**: Dynamic password masking with bullets matching actual length
- **Input Validation**: Comprehensive validation for all forms
- **Unique ID Generation**: UUID-based unique identifiers for all entries
- **Data Integrity**: Authenticated encryption prevents tampering
- **Dark Theme Security**: Proper dropdown backgrounds prevent information leakage

### 🎨 User Interface
- **Material Design**: Modern Material Design components throughout
- **Navigation Component**: Smooth fragment-based navigation
- **ViewBinding**: Type-safe view access for better development experience
- **Responsive Layouts**: Optimized layouts for different screen sizes
- **Visual Feedback**: Color-coded validation messages and status indicators
- **Smart Spinners**: Dynamic expiry date selection with current date awareness
- **Tabbed Navigation**: The main screen now features a tabbed layout for easy navigation between Cards and Passwords.
- **Unified Metallic Theme**: The entire app now uses a consistent metallic black gradient background for a sleek and modern look.
- **Real-time Card Preview**: Live card preview in add/edit screens that matches the exact appearance of cards in the list.
- **Color-synchronized Cards**: Card previews use the same gradient colors as their position in the list for perfect visual consistency.
- **Custom Header**: The main screen has a custom header with the app title and subtitle.
- **Exit Button**: An exit button has been added to the header to close the app.
- **Auto-Logout**: The app will automatically log out after 2 minutes of inactivity for enhanced security.
- **Glassmorphism Design**: Modern glassmorphism card backgrounds with rotating gradient colors.
- **Enhanced Password Cards**: Redesigned password cards with proper vertical spacing, labels, and masked password display.
- **Unified Card Layout**: Password list and preview cards now use identical layouts for consistency.
- **Dark Theme Optimization**: Proper dropdown backgrounds and text contrast for dark theme.
- **Theme Support**: Light and dark theme compatibility with proper text contrast.

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
│   │   ├── crypto/
│   │   │   ├── CryptographyManager.kt      # Cryptographic operations interface
│   │   │   ├── CryptographyManagerImpl.kt  # AES-256-GCM implementation
│   │   │   ├── EncryptedData.kt           # Encrypted data container
│   │   │   ├── AuthResult.kt              # Authentication result types
│   │   │   ├── CryptographyError.kt       # Cryptographic error types
│   │   │   ├── CryptoConstants.kt         # Security parameters
│   │   │   └── SecureMemoryManager.kt     # Secure memory operations
│   │   ├── data/
│   │   │   ├── SecurePasswordRepository.kt     # Encrypted repository interface
│   │   │   ├── SecurePasswordRepositoryImpl.kt # Encrypted data operations
│   │   │   ├── RepositoryManager.kt           # Repository management
│   │   │   └── StorageKeys.kt                 # Storage key constants
│   │   ├── security/
│   │   │   └── SessionManager.kt          # Secure session management
│   │   ├── model/
│   │   │   ├── PasswordEntry.kt           # Password data model with Parcelable
│   │   │   └── CreditCardEntry.kt         # Credit card data model with Parcelable
│   │   ├── ui/
│   │   │   ├── LoginActivity.kt           # Master password authentication
│   │   │   ├── SetupMasterPasswordActivity.kt # Initial encryption setup
│   │   │   ├── MainActivity.kt            # Main activity with session management
│   │   │   ├── MainFragment.kt            # Tabbed main interface
│   │   │   ├── PasswordListFragment.kt    # Password list with encrypted data
│   │   │   ├── AddPasswordFragment.kt     # Add password with encryption
│   │   │   ├── EditPasswordFragment.kt    # Edit password with encryption
│   │   │   ├── CreditCardListFragment.kt  # Credit card list with encrypted data
│   │   │   ├── CreditCardFragment.kt      # Add credit card with encryption
│   │   │   └── EditCreditCardFragment.kt  # Edit credit card with encryption
│   │   └── util/
│   │       ├── CardColorUtil.kt           # Card color utilities
│   │       ├── CardValidator.kt           # Credit card validation logic
│   │       └── StackedCardDecoration.kt   # RecyclerView decoration for cards
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
- **Gson**: JSON serialization for encrypted data persistence
- **Material Design**: Modern UI components
- **Kotlin Parcelize**: Efficient data class serialization
- **Biometric**: Biometric authentication support (disabled for encrypted system)
- **Firebase**: Authentication and cloud services integration

### Data Storage
- **Encrypted SharedPreferences**: AES-256-GCM encrypted local data persistence
- **Zero-Knowledge Architecture**: No plaintext sensitive data stored anywhere
- **Secure Key Management**: PBKDF2-derived keys with secure memory handling
- **Verification Token System**: Password verification through decryption
- **Gson**: Advanced JSON handling with encrypted serialization
- **UUID Generation**: Unique identifiers for all data entries
- **Data Caching**: In-memory decrypted data cache during active sessions

### Architecture
- **Repository Pattern**: Clean data access abstraction with encryption layer
- **Cryptography Manager**: Centralized cryptographic operations
- **Secure Repository**: Encrypted data operations with master key management
- **Fragment-based Navigation**: Modern Android navigation with safe args
- **MVVM-ready**: Prepared for ViewModel integration
- **Session Management**: Secure key lifecycle and automatic cleanup
- **Error Handling**: Comprehensive cryptographic error management

### Key Technical Features
- **Enterprise Cryptography**: AES-256-GCM with PBKDF2 key derivation
- **Zero-Knowledge Security**: Master password as encryption key, no hashes stored
- **Secure Memory Management**: Automatic key clearing and sensitive data protection
- **Authentication via Decryption**: Password verification through encrypted token decryption
- **Card Type Detection**: Regex-based card type identification
- **Luhn Algorithm**: Credit card number validation
- **Dynamic Spinner Logic**: Smart date selection with current date awareness
- **Legacy Data Migration**: Secure migration from unencrypted to encrypted storage
- **Parcelable Models**: Efficient data passing between fragments
- **Modern Card Design**: Rounded corners with clean borders and proper spacing
- **Custom Spinner Theming**: Dark-themed dropdowns with proper text visibility
- **Selector-based Backgrounds**: Interactive dropdown items with pressed/selected states
- **Theme Integration**: Comprehensive dark theme support across all UI components

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

### Latest Visual Updates (v2.3)
- **Fixed Dropdown Theme Issue**: Resolved white background problem in all spinners across the app
- **Comprehensive Dark Theme**: All dropdowns now use consistent dark theme with proper text visibility
- **Enhanced Spinner Styling**: Improved dropdown backgrounds with proper pressed/selected states
- **Unified Spinner Layouts**: Created dedicated dark-themed spinner item layouts for consistency
- **Theme Integration**: Spinners now properly inherit app's dark theme through custom styling
- **Enhanced Password Cards**: Complete redesign with proper vertical spacing and content structure
- **Unified Card Layout**: Password list and preview cards now use identical layouts for perfect consistency
- **Real-time Password Preview**: Live preview functionality for password add/edit screens with masked display
- **Improved Typography**: Optimized font sizes and spacing for better readability across all card types
- **Perfect Color Matching**: Card previews use exact gradient colors matching their position in lists
- **Glassmorphism Design**: Modern frosted glass effect with rotating gradient backgrounds
- **Enhanced Accessibility**: Proper contrast ratios and readable text on all backgrounds

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
- **Metallic Gradient**: Consistent metallic black gradient background across all screens
- **High Contrast Text**: White text on dark backgrounds for optimal readability
- **Real-time Preview**: Live card preview that updates as you type
- **Color Consistency**: Preview cards match list card colors exactly

## 🔒 Security Architecture

### **Zero-Knowledge Encryption System**
- **Master Password as Key**: No password hashes stored - master password directly derives encryption key
- **AES-256-GCM**: Military-grade authenticated encryption with 256-bit keys
- **PBKDF2 Key Derivation**: 100,000+ iterations with unique 32-byte salt per user
- **Verification Token**: Authentication through encrypted token decryption
- **Secure Memory**: Automatic clearing of keys and sensitive data from memory

### **Data Protection**
- **Encrypted Storage**: All sensitive data encrypted before storage in SharedPreferences
- **Authentication Tags**: Built-in tamper detection with GCM mode
- **Unique IVs**: Fresh initialization vector for every encryption operation
- **Session Security**: Keys cleared when app backgrounds, auto-logout after inactivity

### **Privacy Features**
- **Card Number Masking**: Privacy protection in list views
- **CVV Protection**: Masked input with visibility toggle
- **Password Masking**: Dynamic masking matching actual password length
- **No Plaintext Storage**: Zero plaintext sensitive data on device

### **Compliance & Standards**
- **OWASP Mobile Security**: Follows mobile application security guidelines
- **NIST Cryptography**: Uses NIST-approved algorithms and parameters
- **Industry Best Practices**: Implements current security recommendations

## 🚧 Recent Updates & Enhancements

### ✅ Completed Features

#### **🔐 Security & Encryption**
- [x] **Zero-Knowledge Architecture**: Master password encryption system with no stored hashes
- [x] **AES-256-GCM Encryption**: Enterprise-grade authenticated encryption for all sensitive data
- [x] **PBKDF2 Key Derivation**: 100,000+ iterations with cryptographically secure salt
- [x] **Verification Token System**: Authentication through encrypted token decryption
- [x] **Secure Memory Management**: Automatic key clearing and sensitive data protection
- [x] **Session Security**: Auto-logout with key clearing when app backgrounds
- [x] **Legacy Data Migration**: Secure migration from unencrypted to encrypted storage

#### **📱 Core Functionality**
- [x] **Edit & Delete Functionality**: Full CRUD operations for both passwords and credit cards
- [x] **Enhanced Data Models**: Added username, notes, bank name, and card type fields
- [x] **Card Type Detection**: Automatic identification of major card types
- [x] **Luhn Algorithm Validation**: Real-time credit card number validation
- [x] **CVV Masking**: Secure CVV input with show/hide toggle
- [x] **Smart Expiry Date Selection**: Dynamic month filtering based on current date
- [x] **Notes Support**: Multi-line notes field with character limits
- [x] **Encrypted Data Persistence**: Robust encrypted storage with Gson serialization

#### **🎨 User Interface**
- [x] **Modern Card Design**: Rounded credit cards with clean borders and proper spacing
- [x] **Real-time Card Preview**: Live preview of credit cards in add/edit screens with instant updates
- [x] **Color Synchronization**: Card previews match exact gradient colors from list position
- [x] **Consistent Background**: Metallic gradient background across all screens for visual unity
- [x] **Enhanced Password Cards**: Redesigned with proper spacing, labels, and masked password display
- [x] **Unified Card System**: Single layout used for both password list and preview screens
- [x] **Dark Theme Integration**: Complete dark theme support with proper contrast
- [x] **Typography Optimization**: Improved font sizes and spacing across all card elements
- [x] **Navigation Enhancement**: Proper fragment navigation with safe args

#### **🛠️ Development & Build**
- [x] **Build Scripts**: Windows batch scripts for easy building and installation
- [x] **Comprehensive Testing**: Unit tests for cryptographic operations and data integrity
- [x] **Error Handling**: Robust error handling for encryption/decryption operations
- [x] **Code Architecture**: Clean separation of concerns with repository pattern

### 🔄 Future Enhancements

#### **🔐 Advanced Security**
- [ ] Hardware Security Module (HSM) integration
- [ ] Multi-factor authentication options
- [ ] Secure backup and recovery system
- [ ] Advanced threat detection
- [ ] Security audit logging

#### **🚀 Enhanced Features**
- [ ] Password strength checker and generator
- [ ] Auto-fill integration with Android Autofill Framework
- [ ] Secure sharing of passwords between trusted devices
- [ ] Advanced search and filtering capabilities
- [ ] Categories and tags for better organization
- [ ] Secure notes and document storage

#### **☁️ Cloud & Sync**
- [ ] End-to-end encrypted cloud backup
- [ ] Multi-device synchronization
- [ ] Secure export/import functionality
- [ ] Cross-platform compatibility

#### **📱 User Experience**
- [ ] Widget support for quick access
- [ ] Advanced animations and transitions
- [ ] Customizable card colors and themes
- [ ] Voice commands integration
- [ ] Accessibility improvements
- [ ] Tablet and landscape optimization

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

**Note**: This application implements enterprise-grade AES-256-GCM encryption with zero-knowledge architecture. All sensitive data is encrypted with your master password as the key, ensuring that even if your device is compromised, your data remains secure. The app follows OWASP mobile security guidelines and uses NIST-approved cryptographic algorithms.
