# IntelliCoach

**IntelliCoach** is an intelligent, student-centered digital platform designed to help students manage, monitor, and enhance their academic and personal growth. It integrates Java (OOP) for backend logic and data handling with Python Streamlit for analytics and visualization, providing a hybrid, interactive, and data-driven solution to support Self-Regulated Learning (SRL).

---

## Project Structure

```
IntelliCoach/
│
├── java/ # Java backend (OOP logic)
│ ├── src/
│ │ ├── models/ # Java data classes
│ │ │ ├── User.java
│ │ │ ├── Activity.java
│ │ │ └── ProgressLog.java
│ │ ├── services/ # Core logic / operations
│ │ │ ├── ActivityService.java
│ │ │ ├── ProgressService.java
│ │ │ └── RecommendationService.java
│ │ ├── controllers/ # Controllers for GUI or APIs
│ │ │ └── MainController.java
│ │ ├── utils/ # Helper classes
│ │ │ ├── FileHandler.java # Read/write JSON or CSV
│ │ │ └── JsonParser.java
│ │ └── Main.java # Entry point
│ └── data/ # Java-generated data for Streamlit
│   ├── activities.json
│   └── users.json
│
├── streamlit_app/ # Python + Streamlit dashboard
│ ├── app.py # Main dashboard
│ ├── analytics/ # Analytics & visualization
│ │ ├── charts.py
│ │ ├── progress_analysis.py
│ │ └── stats.py
│ ├── recommendations/ # Recommendations module
│ │ └── recommend.py
│ ├── components/ # Optional modular UI components
│ │ └── sidebar.py
│ └── data/ # Processed/merged data for dashboard
│   └── merged_data.csv
│
├── database/ # SQLite or JSON DB or PostgreSQL
│ ├── student_db.sqlite
│ └── logs/
│   └── daily_logs.json
│
├── tests/ # Automated tests
│ ├── java_tests/
│ │ ├── TestActivity.java
│ │ └── TestProgressService.java
│ ├── python_tests/
│ │ ├── test_analytics.py
│ │ └── test_recommend.py
│ └── integration_tests/
│   └── test_java_python_bridge.py
│
├── docs/ # Documentation
│ ├── proposal.pdf
│ ├── system_design.md
│ ├── requirements.md
│ └── reports/
│   └── final_report.docx
│
├── config/ # Config & environment settings
│ ├── settings.json
│ └── environment.env
│
├── lib/ # External dependencies
│ ├── external_jars/ # Java libraries
│ └── python_env/ # Python virtual environment
│
├── scripts/ # Optional utility scripts
│ ├── java_to_python_sync.py # Auto-sync Java JSON to Python
│ └── data_cleaner.py
│
├── README.md
├── requirements.txt # Python dependencies
└── .gitignore
```

---

## Technologies Used

- **Java (OOP)** – Core backend logic, activity logging, and data management  
- **Python + Streamlit** – Interactive dashboards, analytics, and visualizations  
- **PostgreSQL / MySQL** – Optional relational database for persistent storage  
- **JSON / CSV** – Data interchange between Java and Streamlit  
- **Libraries:** Pandas, Matplotlib, Plotly (Python); JDBC (Java)

---

## Features

1. **Activity Logging:** Students can record daily academic and extracurricular activities.  
2. **Progress Tracking:** Visualize daily, weekly, and monthly performance.  
3. **Analytics Dashboard:** Bar charts, line charts, and statistics for progress monitoring.  
4. **Personalized Recommendations:** Suggests areas for improvement based on logged activities.  
5. **Integration:** Java handles structured logic and data storage; Streamlit provides visualization.  

---

## Setup Instructions

### Java

1. Compile Java classes:

```bash
javac -d java/bin java/src/models/*.java java/src/services/*.java java/src/Main.java
```

2. Run Java to generate JSON files:

```bash
java -cp java/bin Main
```

### Streamlit

1. Install dependencies:

```bash
pip install -r requirements.txt
```

2. Run the dashboard:

```bash
cd streamlit_app
streamlit run app.py
```

### PostgreSQL (Optional)

```sql
CREATE DATABASE intellicoach;
CREATE USER ic_user WITH PASSWORD 'password123';
GRANT ALL PRIVILEGES ON DATABASE intellicoach TO ic_user;
```

---

## Contribution

This is a personal academic project. Contributions for improvements, analytics, or visualization are welcome.

---

## License

[MIT Lice]