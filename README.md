# 🔐 Password Manager App

A secure Android application for managing passwords and credit card information with local storage and cloud sync capabilities.

## ✨ Features

### 🔑 Password Management
- **Add Passwords**: Store passwords with context and expiry dates
- **View Passwords**: List all saved passwords in a clean interface
- **Data Persistence**: Passwords are saved locally using SharedPreferences
- **Expiry Tracking**: Monitor password expiration dates

### 💳 Credit Card Management
- **Add Credit Cards**: Store card details securely
- **View Credit Cards**: List all saved cards with masked numbers for privacy
- **Card Masking**: Automatically masks card numbers for security

### ☁️ Cloud Sync (Firebase)
- **Firebase Integration**: Sync data across devices
- **Authentication**: Secure user authentication
- **Firestore Database**: Cloud-based data storage

### 🛡️ Security Features
- **Local Storage**: Data encrypted in SharedPreferences
- **Card Number Masking**: Privacy protection for credit cards
- **Input Validation**: Proper validation for all forms

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

3. **Configure Firebase (Optional)**
   - Create a Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication and Firestore in Firebase Console

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

## 📱 Usage

### Adding a Password
1. Open the app
2. Click "Add New Password"
3. Enter the context (e.g., "Gmail", "Bank Account")
4. Enter your password
5. Optionally add an expiry date
6. Click "Save"

### Adding a Credit Card
1. Navigate to "Credit Cards" section
2. Click "Add New Credit Card"
3. Fill in card details:
   - Card Number
   - Card Holder Name
   - Expiry Date
   - CVV
4. Click "Save"

### Viewing Saved Data
- **Passwords**: All saved passwords are displayed on the main screen
- **Credit Cards**: Navigate to the Credit Cards section to view saved cards

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/passwordmanager/
│   │   ├── data/
│   │   │   ├── CloudSyncManager.kt      # Firebase sync operations
│   │   │   └── PasswordRepository.kt    # Data persistence layer
│   │   ├── model/
│   │   │   ├── PasswordEntry.kt         # Password data model
│   │   │   └── CreditCardEntry.kt       # Credit card data model
│   │   ├── ui/
│   │   │   ├── MainActivity.kt          # Main activity
│   │   │   ├── PasswordListFragment.kt  # Password list view
│   │   │   ├── AddPasswordFragment.kt   # Add password form
│   │   │   ├── CreditCardListFragment.kt # Credit card list view
│   │   │   └── CreditCardFragment.kt    # Add credit card form
│   │   └── utils/
│   │       ├── PasswordGenerator.kt     # Password generation utilities
│   │       └── ExpiryNotifier.kt        # Expiry notification system
│   └── res/
│       ├── layout/                      # UI layouts
│       ├── navigation/                  # Navigation graphs
│       └── values/                      # Resources
```

## 🔧 Technical Details

### Dependencies
- **AndroidX**: Modern Android development libraries
- **Navigation Component**: Fragment navigation
- **RecyclerView**: Efficient list display
- **ViewBinding**: Type-safe view access
- **Gson**: JSON serialization for data persistence
- **Firebase**: Authentication and cloud storage
- **Retrofit**: HTTP client for API calls

### Data Storage
- **SharedPreferences**: Local data persistence
- **Gson**: JSON serialization
- **Firebase Firestore**: Cloud data storage

### Architecture
- **Repository Pattern**: Data access abstraction
- **MVVM**: Model-View-ViewModel architecture
- **Fragment-based Navigation**: Modern Android navigation

## 🛠️ Development

### Building the Project
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## 🔒 Security Considerations

- **Local Encryption**: Consider implementing encryption for local storage
- **Biometric Authentication**: Add fingerprint/face unlock
- **Master Password**: Implement a master password for app access
- **Auto-lock**: Add automatic locking after inactivity

## 🚧 Future Enhancements

- [ ] Biometric authentication
- [ ] Password strength checker
- [ ] Auto-fill integration
- [ ] Password generator
- [ ] Export/Import functionality
- [ ] Dark theme support
- [ ] Widget support
- [ ] Backup to cloud storage

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

**Note**: This is a development project. For production use, additional security measures should be implemented.