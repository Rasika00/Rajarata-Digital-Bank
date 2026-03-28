@echo off
setlocal
cd /d "%~dp0"

set "JAVAC_EXE="
set "JAR_EXE="

if defined JAVA_HOME (
  if exist "%JAVA_HOME%\bin\javac.exe" set "JAVAC_EXE=%JAVA_HOME%\bin\javac.exe"
  if exist "%JAVA_HOME%\bin\jar.exe" set "JAR_EXE=%JAVA_HOME%\bin\jar.exe"
)

if not defined JAVAC_EXE (
  for /f "delims=" %%i in ('where javac 2^>nul') do (
    set "JAVAC_EXE=%%i"
    goto :found_javac
  )
)
:found_javac

if not defined JAR_EXE (
  for /f "delims=" %%i in ('where jar 2^>nul') do (
    set "JAR_EXE=%%i"
    goto :found_jar
  )
)
:found_jar

if not defined JAVAC_EXE (
  echo Java compiler not found. Install JDK 21 and set JAVA_HOME or add javac to PATH.
  exit /b 1
)

if not defined JAR_EXE (
  echo jar tool not found. Install JDK 21 and set JAVA_HOME or add jar to PATH.
  exit /b 1
)

echo Cleaning previous installer build classes...
if exist dist\build-bin rmdir /s /q dist\build-bin
mkdir dist\build-bin

echo Compiling latest sources for installer package...
"%JAVAC_EXE%" --release 21 -d dist\build-bin src\Main.java src\bank\exception\*.java src\bank\model\*.java src\bank\service\*.java src\bank\ui\*.java src\bank\util\*.java
if errorlevel 1 (
  echo Compilation failed. Installer not created.
  exit /b 1
)

if not exist dist\input mkdir dist\input

echo Packaging fresh JAR for installer...
"%JAR_EXE%" --create --file dist\input\RajarataBank.jar -C dist\build-bin .
if errorlevel 1 (
  echo Failed to package JAR file.
  exit /b 1
)

if not exist data\assets\logo.ico (
  echo Missing icon file: data\assets\logo.ico
  echo Add a Windows ICO icon to continue.
  exit /b 1
)

set "APP_VERSION=1.0.1"

echo Building Windows EXE installer with jpackage...
jpackage --type exe ^
  --name RajarataDigitalBank ^
  --app-version %APP_VERSION% ^
  --input dist\input ^
  --main-jar RajarataBank.jar ^
  --main-class Main ^
  --dest dist\output ^
  --icon data\assets\logo.ico ^
  --vendor "Rajarata Digital Bank" ^
  --description "Rajarata Digital Bank Desktop Application" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --install-dir "RajarataDigitalBank"

if errorlevel 1 (
  echo.
  echo Installer build failed.
  echo Make sure WiX Toolset is installed and available in PATH.
  echo For WiX v3: candle.exe + light.exe
  echo For WiX v4/v5: wix.exe
  exit /b 1
)

echo.
echo Installer created in dist\output\ (example: RajarataDigitalBank-%APP_VERSION%.exe)
endlocal
