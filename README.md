
## Folder Structure
IntelliCoach/
│
├── java/                             # Java backend (OOP logic)
│   ├── src/
│   │   ├── models/                   # Java data classes
│   │   │   ├── User.java
│   │   │   ├── Activity.java
│   │   │   └── ProgressLog.java
│   │   ├── services/                 # Core logic / operations
│   │   │   ├── ActivityService.java
│   │   │   ├── ProgressService.java
│   │   │   └── RecommendationService.java
│   │   ├── controllers/              # Controllers for GUI or APIs
│   │   │   └── MainController.java
│   │   ├── utils/                     # Helper classes
│   │   │   ├── FileHandler.java      # Read/write JSON or CSV
│   │   │   └── JsonParser.java
│   │   └── Main.java                  # Entry point
│   └── data/                          # Java-generated data for Streamlit
│       ├── activities.json
│       └── users.json
│
├── streamlit_app/                     # Python + Streamlit dashboard
│   ├── app.py                         # Main dashboard
│   ├── analytics/                     # Analytics & visualization
│   │   ├── charts.py
│   │   ├── progress_analysis.py
│   │   └── stats.py
│   ├── recommendations/               # Recommendations module
│   │   └── recommend.py
│   ├── components/                    # Optional modular UI components
│   │   └── sidebar.py
│   └── data/                          # Processed/merged data for dashboard
│       └── merged_data.csv
│
├── database/                          #  SQLite or JSON DB or postgress this mill be updated
│   ├── student_db.sqlite
│   └── logs/
│       └── daily_logs.json
│
├── tests/                             # Automated tests
│   ├── java_tests/
│   │   ├── TestActivity.java
│   │   └── TestProgressService.java
│   ├── python_tests/
│   │   ├── test_analytics.py
│   │   └── test_recommend.py
│   └── integration_tests/
│       └── test_java_python_bridge.py
│
├── docs/                              # Documentation
│   ├── proposal.pdf
│   ├── system_design.md
│   ├── requirements.md
│   └── reports/
│       └── final_report.docx
│
├── config/                            # Config & environment settings
│   ├── settings.json
│   └── environment.env
│
├── lib/                               # External dependencies
│   ├── external_jars/                 # Java libraries
│   └── python_env/                     # Python virtual environment
│
├── scripts/                           # Optional utility scripts
│   ├── java_to_python_sync.py         # Auto-sync Java JSON to Python
│   └── data_cleaner.py
│
├── README.md
├── requirements.txt                   # Python dependencies
└── .gitignore
