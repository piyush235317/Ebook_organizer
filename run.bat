@echo off
echo [*] Cleaning old builds...
if exist bin rd /s /q bin
mkdir bin

echo [*] Compiling eBook Organizer (V2 Explainable)...
javac -d bin model/*.java decorator/*.java service/*.java ui/*.java

if %errorlevel% neq 0 (
    echo [!] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo [*] Launching Application...
java -cp bin ui.MainFrame
pause
