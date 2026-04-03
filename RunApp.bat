@echo off
setlocal
cd /d "%~dp0"
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.7.6-hotspot"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "JAVAC_EXE=%JAVA_HOME%\bin\javac.exe"

if not exist "%JAVA_EXE%" (
	echo Java runtime not found at: %JAVA_EXE%
	goto :end
)

if not exist "%JAVAC_EXE%" (
	echo Java compiler not found at: %JAVAC_EXE%
	goto :end
)

echo Cleaning old class files...
if exist bin rmdir /s /q bin
mkdir bin

echo Compiling sources with Java 21...
"%JAVAC_EXE%" --release 21 -d bin src\Main.java src\bank\exception\*.java src\bank\model\*.java src\bank\service\*.java src\bank\ui\*.java src\bank\util\*.java
if errorlevel 1 (
	echo Compilation failed.
	goto :end
)

echo Launching application...
"%JAVA_EXE%" -cp bin Main
:end
pause
