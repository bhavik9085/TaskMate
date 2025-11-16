# Step-by-Step Setup Guide

Follow these steps to set up and run the Java project:

## 🚀 Quick Start: Running in Cursor IDE

**Yes, you can run the Java project directly in Cursor!**

### Quick Steps:
1. **Install Java Extension Pack** in Cursor:
   - Press `Ctrl+Shift+X` → Search "Extension Pack for Java" → Install

2. **Open the project folder** in Cursor

3. **Wait for Java extensions to load** (check bottom right corner)

4. **Open `Main.java`** and click the **▶ Run** button above the `main` method

5. **That's it!** The application will run in the integrated terminal

📖 **For detailed Cursor instructions, see `CURSOR_RUN_GUIDE.md`**

---

## Traditional Setup (Command Line)

## Prerequisites Check

### 1. Check Java Installation
Open Command Prompt (Windows) or Terminal (Mac/Linux) and run:
```bash
java -version
```
You should see something like: `java version "11.0.x"` or higher.

**If Java is not installed:**
- Download JDK 11+ from: https://www.oracle.com/java/technologies/downloads/
- Install it and add Java to your PATH

### 2. Check Maven Installation
Run:
```bash
mvn -version
```
You should see Maven version information.

**If Maven is not installed:**
- Download from: https://maven.apache.org/download.cgi
- Extract and add to PATH
- Or use your IDE's built-in Maven

### 3. Check MySQL Installation
Run:
```bash
mysql --version
```
You should see MySQL version information.

**If MySQL is not installed:**
- Download MySQL from: https://dev.mysql.com/downloads/mysql/
- Or install XAMPP/WAMP (includes MySQL)
- During installation, remember your root password

## Database Setup (Manual Steps Required)

### Step 1: Start MySQL Service

**Windows:**
- Open Services (Win+R → services.msc)
- Find "MySQL80" or "MySQL"
- Right-click → Start (if not running)

**Or use XAMPP/WAMP:**
- Open XAMPP/WAMP Control Panel
- Click "Start" next to MySQL

**Mac/Linux:**
```bash
sudo systemctl start mysql
# or
brew services start mysql
```

### Step 2: Access MySQL

**Option A: MySQL Command Line**
```bash
mysql -u root -p
```
Enter your MySQL root password when prompted.

**Option B: MySQL Workbench**
- Open MySQL Workbench
- Connect to localhost with root credentials

### Step 3: Create Database

In MySQL, run these commands:
```sql
CREATE DATABASE IF NOT EXISTS finalproject_db;
USE finalproject_db;
```

### Step 4: Create Tables

**Option A: Run the SQL script file**
```bash
mysql -u root -p finalproject_db < src/main/resources/database_schema.sql
```

**Option B: Copy and paste SQL commands**
1. Open `src/main/resources/database_schema.sql` in a text editor
2. Copy all the SQL commands
3. Paste into MySQL command line or Workbench
4. Execute

**Option C: Manual creation**
```sql
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    date_of_birth DATE,
    address VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Step 5: Verify Database Setup

Run this query to verify:
```sql
SHOW TABLES;
SELECT * FROM students;
```

You should see the `students` table and any sample data.

## Application Configuration

### Step 1: Update Database Properties

1. Open `src/main/resources/database.properties` in a text editor
2. Update these values:
   ```properties
   db.username=root
   db.password=YOUR_MYSQL_PASSWORD
   ```
   Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL root password

3. If MySQL is on a different port (not 3306), update:
   ```properties
   db.url=jdbc:mysql://localhost:YOUR_PORT/finalproject_db?...
   ```

### Step 2: Build the Project

Open terminal/command prompt in the project directory:

```bash
mvn clean compile
```

This will:
- Download all dependencies (MySQL connector, etc.)
- Compile Java source files
- Take a few minutes on first run (downloading dependencies)

**Expected output:** `BUILD SUCCESS`

## Running the Application

### Option 1: Using Maven (Recommended)
```bash
mvn exec:java -Dexec.mainClass="com.finalproject.Main"
```

### Option 2: Using IDE
1. **IntelliJ IDEA:**
   - File → Open → Select project folder
   - Wait for Maven import
   - Right-click `Main.java` → Run 'Main.main()'

2. **Eclipse:**
   - File → Import → Existing Maven Projects
   - Select project folder
   - Right-click `Main.java` → Run As → Java Application

3. **VS Code:**
   - Open folder
   - Install Java Extension Pack
   - Run `Main.java`

### Option 3: Create JAR and Run
```bash
# Build JAR
mvn clean package

# Run JAR
java -cp target/classes:target/dependency/* com.finalproject.Main
```

## Testing the Application

1. **Start the application** - You should see:
   ```
   ========================================
      Student Management System
   ========================================
   Database connection successful!
   ```

2. **Test adding a student:**
   - Choose option 1
   - Enter student details
   - Verify success message

3. **Test viewing students:**
   - Choose option 2
   - Should display all students

4. **Test other features** (Update, Delete, Search)

## Troubleshooting

### Problem: "Cannot connect to database"

**Checklist:**
- [ ] MySQL service is running
- [ ] Database `finalproject_db` exists
- [ ] Username and password in `database.properties` are correct
- [ ] MySQL port is 3306 (or updated in properties)

**Test connection manually:**
```bash
mysql -u root -p -e "USE finalproject_db; SHOW TABLES;"
```

### Problem: "MySQL driver not found"

**Solution:**
```bash
mvn clean install
```

This forces Maven to download all dependencies.

### Problem: "Access denied for user 'root'@'localhost'"

**Solution 1:** Check password in `database.properties`

**Solution 2:** Create a new MySQL user:
```sql
CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'securepassword';
GRANT ALL PRIVILEGES ON finalproject_db.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
```
Then update `database.properties` with new credentials.

### Problem: "Port 3306 is already in use"

**Solution:** Another MySQL instance is running. Stop it or change port.

### Problem: Maven build fails

**Solution:**
- Check internet connection (Maven downloads dependencies)
- Clear Maven cache: `mvn clean`
- Delete `~/.m2/repository` folder and rebuild

## Next Steps

Once the application is running:

1. **Explore the code structure** - Understand each layer
2. **Add more features** - Extend with courses, enrollments, etc.
3. **Add error handling** - Improve user experience
4. **Add unit tests** - Test individual components
5. **Add logging** - Better debugging capabilities

## Quick Reference

| Task | Command |
|------|---------|
| Build project | `mvn clean compile` |
| Run application | `mvn exec:java -Dexec.mainClass="com.finalproject.Main"` |
| Create JAR | `mvn clean package` |
| Run tests | `mvn test` |
| Clean build | `mvn clean` |

## Need Help?

- Check `README.md` for detailed documentation
- Review error messages carefully
- Verify each step was completed correctly
- Check MySQL logs if database issues persist

