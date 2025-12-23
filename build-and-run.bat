@echo off
echo =========================================
echo 🔨 BUILDING INTELLICOACH PROJECT...
echo =========================================

if not exist build mkdir build
xcopy /E /I /Y src\resources build 2>nul
dir /s /b src\java\*.java > sources.txt
javac --module-path "C:\Users\hp\Downloads\openjfx-25.0.1_windows-x64_bin-sdk\javafx-sdk-25.0.1\lib" --add-modules javafx.controls -d build -cp "lib/*" -sourcepath src/java @sources.txt
del sources.txt

echo  Build completed successfully!
echo.
echo =========================================
echo  RUNNING INTELLICOACH APPLICATION...
echo =========================================

java --module-path "C:\Users\hp\Downloads\openjfx-25.0.1_windows-x64_bin-sdk\javafx-sdk-25.0.1\lib" --add-modules javafx.controls --enable-native-access=javafx.graphics,ALL-UNNAMED -cp "build;lib/*" com.IntelliCoachApp