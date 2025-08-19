# Master Password Encryption System Design

## Overview

This design document outlines the implementation of a secure encryption system that uses the master password as the primary encryption key. The system eliminates password hash storage and implements true zero-knowledge security through encryption-based authentication.

## Architecture

### High-Level Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   User Input    │───▶│  Key Derivation  │───▶│   Encryption    │
│ (Master Pass)   │    │   (PBKDF2)       │    │  (AES-256-GCM)  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
                       ┌──────────────────┐    ┌─────────────────┐
                       │   Salt Storage   │    │ Encrypted Data  │
                       │ (SharedPrefs)    │    │ (SharedPrefs)   │
                       └──────────────────┘    └─────────────────┘
```

### Security Flow

```
Setup Flow:
Master Password → PBKDF2 + Salt → AES Key → Encrypt Verification Token → Store

Login Flow:
Master Password → PBKDF2 + Stored Salt → AES Key → Decrypt Verification Token
                                                           │
                                                    Success? Access Granted
                                                    Failure? Invalid Password
```

## Components and Interfaces

### 1. CryptographyManager

**Purpose**: Central component for all cryptographic operations

```kotlin
interface CryptographyManager {
    // Key derivation
    fun deriveKeyFromPassword(password: String, salt: ByteArray): SecretKey
    fun generateSalt(): ByteArray
    
    // Encryption/Decryption
    fun encrypt(data: String, key: SecretKey): EncryptedData
    fun decrypt(encryptedData: EncryptedData, key: SecretKey): String?
    
    // Verification
    fun createVerificationToken(key: SecretKey): EncryptedData
    fun verifyPassword(password: String, salt: ByteArray, verificationToken: EncryptedData): Boolean
    
    // Memory management
    fun clearKey(key: SecretKey)
    fun clearSensitiveData(data: CharArray)
}
```

### 2. SecurePasswordRepository

**Purpose**: Enhanced repository with encryption capabilities

```kotlin
interface SecurePasswordRepository {
    // Setup and authentication
    fun setupMasterPassword(password: String): Boolean
    fun authenticateUser(password: String): AuthResult
    
    // Encrypted data operations
    fun addPasswordEntry(entry: PasswordEntry, masterKey: SecretKey): PasswordEntry
    fun getAllPasswordEntries(masterKey: SecretKey): List<PasswordEntry>
    fun updatePasswordEntry(entry: PasswordEntry, masterKey: SecretKey): Boolean
    fun deletePasswordEntry(id: String): Boolean
    
    // Credit card operations
    fun addCreditCardEntry(entry: CreditCardEntry, masterKey: SecretKey): CreditCardEntry
    fun getAllCreditCardEntries(masterKey: SecretKey): List<CreditCardEntry>
    
    // Migration
    fun migrateFromLegacyStorage(masterPassword: String): MigrationResult
    
    // Security
    fun isEncryptionSetup(): Boolean
    fun clearAllData(): Boolean
}
```

### 3. EncryptedData Model

**Purpose**: Container for encrypted data with metadata

```kotlin
data class EncryptedData(
    val encryptedBytes: ByteArray,
    val iv: ByteArray,
    val authTag: ByteArray,
    val algorithm: String = "AES/GCM/NoPadding"
) {
    fun toStorageString(): String
    companion object {
        fun fromStorageString(data: String): EncryptedData?
    }
}
```

### 4. AuthResult Sealed Class

**Purpose**: Type-safe authentication results

```kotlin
sealed class AuthResult {
    object Success : AuthResult()
    object InvalidPassword : AuthResult()
    object DataCorrupted : AuthResult()
    object SetupRequired : AuthResult()
    data class Error(val message: String) : AuthResult()
}
```

## Data Models

### Enhanced Password Entry

```kotlin
data class PasswordEntry(
    val id: String,
    val context: String,        // Encrypted
    val username: String,       // Encrypted
    val password: String,       // Encrypted
    val expiryDate: String,     // Plain text (not sensitive)
    val notes: String,          // Encrypted
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Enhanced Credit Card Entry

```kotlin
data class CreditCardEntry(
    val id: String,
    val cardNumber: String,     // Encrypted
    val cardHolder: String,     // Encrypted
    val bankName: String,       // Encrypted
    val cardType: String,       // Plain text (derived from number)
    val expiryDate: String,     // Plain text (not sensitive)
    val cvv: String,           // Encrypted
    val notes: String,         // Encrypted
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Storage Schema

```kotlin
// SharedPreferences keys
object StorageKeys {
    const val ENCRYPTION_SALT = "encryption_salt"
    const val VERIFICATION_TOKEN = "verification_token"
    const val ENCRYPTED_PASSWORDS = "encrypted_passwords"
    const val ENCRYPTED_CREDIT_CARDS = "encrypted_credit_cards"
    const val ENCRYPTION_VERSION = "encryption_version"
    const val MIGRATION_STATUS = "migration_status"
}
```

## Error Handling

### Error Types

```kotlin
sealed class CryptographyError : Exception() {
    object InvalidPassword : CryptographyError()
    object DataCorrupted : CryptographyError()
    object EncryptionFailed : CryptographyError()
    object DecryptionFailed : CryptographyError()
    object KeyDerivationFailed : CryptographyError()
    data class UnknownError(override val message: String) : CryptographyError()
}
```

### Error Handling Strategy

1. **Graceful Degradation**: Never lose user data due to encryption errors
2. **Clear Messaging**: Provide specific error messages for different failure types
3. **Recovery Options**: Offer data export/backup before risky operations
4. **Logging**: Secure logging without exposing sensitive information

## Testing Strategy

### Unit Tests

1. **CryptographyManager Tests**
   - Key derivation with various inputs
   - Encryption/decryption round trips
   - Salt generation uniqueness
   - Memory clearing verification

2. **SecurePasswordRepository Tests**
   - Data encryption/decryption
   - Authentication flows
   - Migration scenarios
   - Error handling

3. **Security Tests**
   - Verify no plaintext storage
   - Test key derivation parameters
   - Validate encryption algorithms
   - Memory leak detection

### Integration Tests

1. **End-to-End Encryption**
   - Complete setup → store → retrieve → decrypt flow
   - Multiple data types (passwords, credit cards)
   - Large dataset performance

2. **Migration Testing**
   - Legacy data migration
   - Rollback scenarios
   - Data integrity verification

### Security Validation

1. **Cryptographic Validation**
   - NIST compliance verification
   - Algorithm parameter validation
   - Random number generator testing

2. **Attack Simulation**
   - Brute force resistance
   - Side-channel attack prevention
   - Memory dump analysis

## Implementation Details

### Key Derivation Parameters

```kotlin
object CryptoConstants {
    const val PBKDF2_ITERATIONS = 100_000
    const val SALT_LENGTH = 32 // bytes
    const val KEY_LENGTH = 256 // bits
    const val IV_LENGTH = 12 // bytes for GCM
    const val AUTH_TAG_LENGTH = 16 // bytes
    const val ALGORITHM = "AES/GCM/NoPadding"
    const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256"
}
```

### Memory Security

```kotlin
class SecureMemoryManager {
    fun clearByteArray(array: ByteArray) {
        Arrays.fill(array, 0.toByte())
    }
    
    fun clearCharArray(array: CharArray) {
        Arrays.fill(array, '\u0000')
    }
    
    fun secureRandom(size: Int): ByteArray {
        val random = SecureRandom()
        val bytes = ByteArray(size)
        random.nextBytes(bytes)
        return bytes
    }
}
```

### Performance Considerations

1. **Async Operations**: All encryption/decryption on background threads
2. **Caching Strategy**: Keep decrypted data in memory during active session
3. **Batch Operations**: Encrypt/decrypt multiple items efficiently
4. **Progress Indicators**: Show progress for long-running operations

### Migration Strategy

#### Migration Flow
```
App Launch → Detect Legacy Data → Prompt User → Verify Master Password → 
Create Backup → Migrate Data → Verify Migration → Cleanup Legacy Data
```

#### Migration Implementation
```kotlin
class LegacyDataMigrator {
    fun detectLegacyData(): LegacyDataStatus
    fun createDataBackup(): BackupResult
    fun migrateLegacyData(masterPassword: String): MigrationResult
    fun verifyMigration(masterKey: SecretKey): VerificationResult
    fun cleanupLegacyData(): CleanupResult
    fun rollbackMigration(backupPath: String): RollbackResult
}

data class LegacyDataStatus(
    val hasLegacyPasswords: Boolean,
    val hasLegacyCreditCards: Boolean,
    val hasLegacyMasterPasswordHash: Boolean,
    val passwordCount: Int,
    val creditCardCount: Int
)

sealed class MigrationResult {
    data class Success(val migratedPasswords: Int, val migratedCreditCards: Int) : MigrationResult()
    data class PartialSuccess(val migratedPasswords: Int, val migratedCreditCards: Int, val errors: List<String>) : MigrationResult()
    data class Failure(val error: String, val backupPath: String?) : MigrationResult()
}
```

#### Migration User Experience
1. **Detection**: Automatic detection on app launch
2. **User Prompt**: Clear explanation of migration benefits and risks
3. **Backup Creation**: Automatic backup before migration starts
4. **Progress Tracking**: Real-time progress updates during migration
5. **Verification**: Post-migration verification that all data is accessible
6. **Rollback Option**: Ability to rollback if migration fails

#### Migration Safety Measures
```kotlin
class MigrationSafetyManager {
    fun createPreMigrationBackup(): String // Returns backup file path
    fun validateLegacyData(): ValidationResult
    fun testEncryptionDecryption(masterPassword: String): Boolean
    fun verifyDataIntegrity(originalData: List<Any>, migratedData: List<Any>): Boolean
    fun createMigrationLog(): String
}
```

## Security Considerations

### Threat Model

1. **Device Compromise**: Encrypted data remains secure
2. **Memory Dumps**: Keys cleared after use
3. **Brute Force**: PBKDF2 with high iteration count
4. **Side Channels**: Constant-time operations where possible

### Security Controls

1. **Encryption**: AES-256-GCM (authenticated encryption)
2. **Key Derivation**: PBKDF2 with 100,000+ iterations
3. **Salt**: Unique 32-byte cryptographically secure salt
4. **IV**: Unique initialization vector per encryption
5. **Authentication**: Built-in authentication tags

### Compliance

- **OWASP Mobile Security**: Follows mobile app security guidelines
- **NIST Standards**: Uses NIST-approved cryptographic algorithms
- **Industry Best Practices**: Implements current security recommendations

This design provides a robust, secure foundation for implementing true zero-knowledge encryption in the password manager application.