# IntelliCoach - Time Tracking System

## Project Overview

IntelliCoach is a comprehensive time tracking and productivity management system designed to help users efficiently monitor their daily activities. The application provides real-time activity tracking using Start/Stop functionality, detailed analytics, comprehensive reporting, and personalized productivity recommendations.

## Core Features

### Activity Categories

The system supports six predefined activity categories that are mandatory for comprehensive time tracking:

1. **Academic** - Study sessions, homework, classes, and educational activities
2. **Sport** - Physical exercise, fitness activities, and sports participation
3. **Entertainment** - Leisure activities, games, social media, and recreational time
4. **Extra Activity** - Clubs, volunteering, hobbies, and personal projects
5. **Sleep** - Essential rest periods (mandatory for productivity analysis)
6. **Health / Hygiene** - Personal care, meals, and health-related activities

### Recommended Daily Time Allocation

| Activity Category | Recommended Duration |
| ----------------- | -------------------- |
| Academic          | 6 – 8 hours          |
| Sleep             | 7 – 9 hours          |
| Sport             | 1 – 2 hours          |
| Entertainment     | 1 – 2 hours          |
| Extra Activity    | 1 – 2 hours          |
| Health / Hygiene  | 30 – 60 minutes      |

These recommendations serve as benchmarks for analytics comparison, productivity scoring, and recommendation generation.

## Time Tracking Logic

### Activity Management

- **Single Active Session**: Only one activity can be active at any given time
- **Automatic Switching**: Starting a new activity automatically stops the current one
- **No Overlapping**: The system prevents overlapping time entries
- **Real-time Tracking**: Live timer displays current session duration

### Data Storage

- All activity sessions are automatically saved to SQLite database
- Session data includes user ID, activity type, start/end times, and calculated duration
- Data integrity is maintained through proper database constraints

## System Architecture

### Authentication Flow

```
Login Page → Dashboard → Feature Pages → Logout
     ↓
Registration Page (for new users)
```

### Navigation Structure

- **Dashboard**: Central navigation hub
- **Activity Tracking**: Start/Stop functionality with live timer
- **Analytics**: Daily, weekly, and monthly time analysis
- **Reports**: Comprehensive report generation and export
- **Recommendations**: Personalized productivity suggestions

## Analytics and Reporting

### Analytics Features

- Time distribution analysis across all activity categories
- Period-based summaries (daily, weekly, monthly)
- Actual vs. recommended time comparisons
- Productivity pattern identification
- Balance and imbalance detection

### Report Generation

- Detailed activity reports with precise duration calculations
- Multiple export formats (CSV, TXT)
- Customizable date ranges
- Professional formatting for documentation purposes

### Data Export Fields

- Activity name and category
- Session start and end timestamps
- Calculated duration
- Session date
- User identification

## Recommendation Engine

### Analysis Criteria

- Time allocation balance across categories
- Sleep adequacy assessment
- Entertainment time monitoring
- Academic focus evaluation
- Physical activity tracking

### Recommendation Types

- **Critical Alerts**: For severe imbalances (e.g., insufficient sleep)
- **Balance Suggestions**: For time distribution optimization
- **Activity Reminders**: For missing essential activities
- **Productivity Tips**: For performance improvement

### Productivity Scoring

- Numerical score (0-100) based on time allocation efficiency
- Comparison against recommended guidelines
- Color-coded performance indicators
- Trend analysis over time

## Database Schema

### Core Tables

#### users

- user_id (Primary Key, Auto-increment)
- username (Unique, Not Null)
- email (Unique, Not Null)
- password (Encrypted)
- full_name
- created_at (Timestamp)

#### activity_sessions

- session_id (Primary Key, Auto-increment)
- user_id (Foreign Key)
- activity_type (Constrained to 6 categories)
- start_time (Timestamp)
- end_time (Timestamp)
- duration_minutes (Calculated)
- session_date (Date)
- is_active (Boolean)
- created_at (Timestamp)

#### recommended_times

- activity_type (Primary Key)
- min_minutes (Integer)
- max_minutes (Integer)
- description (Text)

#### recommendations

- recommendation_id (Primary Key, Auto-increment)
- user_id (Foreign Key)
- recommendation_text (Text)
- recommendation_type (Enum)
- priority (HIGH/MEDIUM/LOW)
- based_on_date (Date)
- created_at (Timestamp)
- is_read (Boolean)

## Technical Implementation

### JavaFX User Interface

- **Pure JavaFX Implementation**: No FXML files used
- **Layout Managers**: GridPane, VBox, BorderPane, HBox, FlowPane
- **Event Handling**: Lambda expressions and functional interfaces
- **Real-time Updates**: Timeline-based timer functionality

### Design Patterns

- **Singleton Pattern**: Database connections and service instances
- **Data Access Object (DAO)**: Database interaction abstraction
- **Model-View-Controller (MVC)**: Clear separation of concerns
- **Observer Pattern**: Real-time UI updates

### Advanced Java Features

- **Lambda Expressions**: Event handling and stream operations
- **Inner Classes**: Static, non-static, anonymous, and local implementations
- **Functional Interfaces**: Predicate, Consumer, Function, Supplier
- **Stream API**: Data processing and filtering

## Project Structure

```
IntelliCoach/
├── src/java/com/
│   ├── IntelliCoachApp.java
│   ├── TestDatabase.java
│   ├── models/
│   │   ├── User.java
│   │   ├── ActivitySession.java
│   │   ├── ActivityType.java
│   │   └── Recommendation.java
│   ├── database/
│   │   ├── DBConnection.java
│   │   ├── UserDAO.java
│   │   └── ActivitySessionDAO.java
│   ├── services/
│   │   ├── AuthenticationService.java
│   │   ├── TimeTrackingService.java
│   │   ├── AnalyticsService.java
│   │   └── RecommendationService.java
│   ├── views/
│   │   ├── LoginView.java
│   │   ├── RegistrationView.java
│   │   ├── DashboardView.java
│   │   ├── ActivityTrackingView.java
│   │   ├── AnalyticsView.java
│   │   ├── ReportsView.java
│   │   └── RecommendationsView.java
│   └── interfaces/
│       ├── Repository.java
│       ├── Service.java
│       └── Trackable.java
├── database/
│   ├── schema.sql
│   └── intellicoach.db
├── lib/
│   ├── sqlite-jdbc-3.44.1.0.jar
│   ├── slf4j-api-2.0.9.jar
│   └── slf4j-simple-2.0.9.jar
├── build-and-run.bat
├── build-only.bat
└── README.md
```

## Prerequisites

### System Requirements

1. **Java Development Kit (JDK) 11 or higher**
2. **JavaFX SDK** for GUI functionality
3. **SQLite JDBC Driver** (included in lib directory)

### JavaFX Setup

- Download JavaFX SDK from https://openjfx.io/
- Extract to desired location
- Update module path in run commands

## Build and Execution

### Windows Build System

The project includes automated build scripts for Windows:

```cmd
# Build only (compilation)
build-only.bat

# Build and run application
build-and-run.bat
```

### Manual Compilation

```cmd
# Create build directory
mkdir build

# Compile Java sources
javac --module-path "path\to\javafx\lib" --add-modules javafx.controls -d build -cp "lib/*" -sourcepath src/java src/java/com/*.java src/java/com/*/*.java

# Run application
java --module-path "path\to\javafx\lib" --add-modules javafx.controls -cp "build;lib/*" com.IntelliCoachApp
```

### Database Testing

```cmd
# Test database connectivity (console application)
java -cp "build;lib/*" com.TestDatabase
```

## Usage Instructions

### Initial Setup

1. Launch the application
2. Create a new user account via registration
3. Login with your credentials

### Activity Tracking

1. Navigate to Activity Tracking page
2. Select desired activity from dropdown menu
3. Click START to begin tracking
4. Monitor real-time timer display
5. Click STOP when activity is complete
6. Session data is automatically saved

### Analytics Review

1. Access Analytics page from dashboard
2. Select analysis period (daily, weekly, monthly)
3. Choose specific date or date range
4. Review time distribution charts and tables
5. Compare actual vs. recommended time allocations

### Report Generation

1. Navigate to Reports page
2. Configure report parameters
3. Select export format (CSV or TXT)
4. Generate and preview report
5. Export for external use

### Productivity Recommendations

1. Access Recommendations page
2. Select date for analysis
3. Generate personalized recommendations
4. Review productivity score and suggestions
5. Implement recommended improvements

## Quality Assurance

### Validation Checklist

- Start/Stop functionality operates correctly
- Single active session enforcement
- Automatic activity switching
- No overlapping time entries
- Complete activity category coverage
- Accurate duration calculations
- Comprehensive analytics generation
- Reliable data export functionality
- Effective recommendation algorithms
- Secure user authentication
- Robust database operations

## Troubleshooting

### Common Issues

**Database Connection Errors**

- Verify SQLite JDBC driver is present in lib directory
- Check database file permissions
- Ensure no other instances are accessing the database

**JavaFX Runtime Errors**

- Confirm JavaFX SDK is properly installed
- Verify module path configuration
- Check Java version compatibility

**Compilation Errors**

- Validate Java JDK installation
- Confirm all dependencies are available
- Check source file organization

## Technical Support

### Error Resolution

1. Check system requirements compliance
2. Verify all dependencies are installed
3. Review configuration settings
4. Consult troubleshooting section
5. Examine application logs for detailed error information

## License and Usage

This application is developed for educational and productivity enhancement purposes. It demonstrates professional software development practices including object-oriented design, database integration, user interface development, and comprehensive testing methodologies.

## Development Information

**IntelliCoach Time Tracking System** represents a complete solution for personal productivity management, combining modern Java development practices with practical time management functionality.

---

### Installation & Deployment

## Local Setup

Clone the repository:
git clone https://github.com/Maxd646/IntelliCoach.git

---

## Contributors

| Name               | Id         | Github                                   |
| ------------------ | ---------- | ---------------------------------------- |
| **Daniel Gashaw**  | ETS0387/16 | https://github.com/Maxd646 (Team Leader) |
| **Abrham Teramed** | ETS0094/16 | https://github.com/Abrom-code            |
| **Addis Shiferaw** | ETS0099/16 | https://github.com/Adda-19               |
| **Liyuneh Rstey**  | ETS0841/15 | https://github.com/liyuneh               |
| **Amir Yimam**     | ETS0169/16 | https://github.com/miro129               |

**IntelliCoach** - Professional Time Tracking and Productivity Management
