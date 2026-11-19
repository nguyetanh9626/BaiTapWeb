@echo off
setlocal EnableExtensions

set "PROJECT_DIR=%~dp0"
if "%PROJECT_DIR:~-1%"=="\" set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"

:: 1. Thiet lap moi truong (uu tien bien moi truong he thong, neu chua co thi lay thu muc tools)
if "%JAVA_HOME%"=="" (
    if exist "C:\Users\USER\tools\jdk-21.0.13+9" (
        set "JAVA_HOME=C:\Users\USER\tools\jdk-21.0.13+9"
    )
)
if "%MAVEN_HOME%"=="" (
    if exist "C:\Users\USER\tools\apache-maven-3.9.9" (
        set "MAVEN_HOME=C:\Users\USER\tools\apache-maven-3.9.9"
    )
)
if "%CATALINA_HOME%"=="" (
    if exist "C:\Users\USER\tools\apache-tomcat-11.0.27" (
        set "CATALINA_HOME=C:\Users\USER\tools\apache-tomcat-11.0.27"
    )
)

if not "%JAVA_HOME%"=="" set "PATH=%JAVA_HOME%\bin;%PATH%"
if not "%MAVEN_HOME%"=="" set "PATH=%MAVEN_HOME%\bin;%PATH%"

echo ============================================================
echo   BAITAPWEB - KHOI DONG TU DONG - SQL SERVER + TOMCAT 11
echo ============================================================
echo JAVA_HOME    : %JAVA_HOME%
echo MAVEN_HOME   : %MAVEN_HOME%
echo CATALINA_HOME: %CATALINA_HOME%
echo PROJECT_DIR  : %PROJECT_DIR%
echo ============================================================

:: Kiem tra cac cong cu can thiet
if "%JAVA_HOME%"=="" goto :ERR_JAVA
if not exist "%JAVA_HOME%\bin\java.exe" goto :ERR_JAVA_EXE

if "%MAVEN_HOME%"=="" (
    where mvn >nul 2>nul
    if errorlevel 1 goto :ERR_MVN
    set "MVN_CMD=mvn"
) else (
    set "MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
)

if "%CATALINA_HOME%"=="" goto :ERR_CATALINA
if not exist "%CATALINA_HOME%\bin\startup.bat" goto :ERR_CATALINA_STARTUP

:: [Buoc 1] Kiem tra ket noi SQL Server tren cong 1433
echo.
echo [1/4] Kiem tra ket noi SQL Server tren cong 1433...
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort 1433 -State Listen -ErrorAction SilentlyContinue) { exit 0 } else { exit 1 }"
if not errorlevel 1 goto :PORT_OK

echo     Cong 1433 chua mo. Dang co gang bat TCP/IP...
if exist "%PROJECT_DIR%\database\enable-sqlserver-tcp.bat" (
    powershell -NoProfile -Command "Start-Process -FilePath '%PROJECT_DIR%\database\enable-sqlserver-tcp.bat' -Verb RunAs -Wait"
)
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort 1433 -State Listen -ErrorAction SilentlyContinue) { exit 0 } else { exit 1 }"
if not errorlevel 1 goto :PORT_OK

echo [CANH BAO] Cong 1433 chua mo. Kiem tra SQL Server Configuration Manager.
goto :STEP_DB

:PORT_OK
echo     -^> Cong 1433 dang hoat dong tot.

:: [Buoc 2] Chay cac script database: baitapweb.sql va webst2.sql
:STEP_DB
echo.
echo [2/4] Chay cac script khoi tao Database SQL Server...

set "SQLCMD_AUTH="
if not "%BAITAPWEB_DB_USER%"=="" (
    if not "%BAITAPWEB_DB_PASSWORD%"=="" (
        set "SQLCMD_AUTH=-S localhost -U %BAITAPWEB_DB_USER% -P %BAITAPWEB_DB_PASSWORD% -C"
    )
)
if "%SQLCMD_AUTH%"=="" (
    set "SQLCMD_AUTH=-S localhost -U sa -P 1234567@a$ -C"
)
sqlcmd %SQLCMD_AUTH% -Q "SELECT 1" >nul 2>nul
if not errorlevel 1 goto :DO_BAITAPWEB

set "SQLCMD_AUTH=-S .\SQLEXPRESS -E"
sqlcmd %SQLCMD_AUTH% -Q "SELECT 1" >nul 2>nul
if not errorlevel 1 goto :DO_BAITAPWEB

set "SQLCMD_AUTH=-S localhost -E"

:DO_BAITAPWEB
echo     Dang chay baitapweb.sql...
sqlcmd %SQLCMD_AUTH% -f 65001 -i "%PROJECT_DIR%\database\baitapweb.sql" >nul 2>nul
if errorlevel 1 (
    echo [CANH BAO] Chay baitapweb.sql chua thanh cong.
) else (
    echo     -^> Chay thanh cong baitapweb.sql
)

echo     Dang chay webst2.sql...
sqlcmd %SQLCMD_AUTH% -f 65001 -i "%PROJECT_DIR%\database\webst2.sql" >nul 2>nul
if errorlevel 1 (
    echo [CANH BAO] Chay webst2.sql chua thanh cong.
) else (
    echo     -^> Chay thanh cong webst2.sql
)

echo     Dang chay update_baitapweb_v2.sql...
if exist "%PROJECT_DIR%\database\update_baitapweb_v2.sql" (
    sqlcmd %SQLCMD_AUTH% -f 65001 -i "%PROJECT_DIR%\database\update_baitapweb_v2.sql" >nul 2>nul
    if errorlevel 1 (
        echo [CANH BAO] Chay update_baitapweb_v2.sql chua thanh cong.
    ) else (
        echo     -^> Chay thanh cong update_baitapweb_v2.sql
    )
)

echo     Dang chay update_baitapweb_v3_profile.sql...
if exist "%PROJECT_DIR%\database\update_baitapweb_v3_profile.sql" (
    sqlcmd %SQLCMD_AUTH% -f 65001 -i "%PROJECT_DIR%\database\update_baitapweb_v3_profile.sql" >nul 2>nul
    if errorlevel 1 (
        echo [CANH BAO] Chay update_baitapweb_v3_profile.sql chua thanh cong.
    ) else (
        echo     -^> Chay thanh cong update_baitapweb_v3_profile.sql
    )
)

if exist "%PROJECT_DIR%\database\fix_vietnamese_videos.sql" (
    sqlcmd %SQLCMD_AUTH% -f 65001 -i "%PROJECT_DIR%\database\fix_vietnamese_videos.sql" >nul 2>nul
)

:: [Buoc 3] Build WAR bang Maven
echo.
echo [3/4] Build WAR va deploy len Tomcat...
cd /d "%PROJECT_DIR%"
call "%MVN_CMD%" clean package -DskipTests=true
if errorlevel 1 goto :ERR_BUILD

:: Tat Tomcat cu neu dang chay cong 8080
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) { & '%CATALINA_HOME%\bin\shutdown.bat' | Out-Null; for ($i=0; $i -lt 20; $i++) { Start-Sleep -Milliseconds 500; if (-not (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue)) { break } } }"
if exist "%CATALINA_HOME%\webapps\baitapweb" rmdir /s /q "%CATALINA_HOME%\webapps\baitapweb"
copy /y "%PROJECT_DIR%\target\baitapweb.war" "%CATALINA_HOME%\webapps\baitapweb.war" >nul
if errorlevel 1 goto :ERR_COPY

echo     -^> Deploy WAR thanh cong vao %CATALINA_HOME%\webapps\baitapweb.war

:: [Buoc 4] Khoi dong Apache Tomcat
echo.
echo [4/4] Khoi dong Apache Tomcat...
start "" /D "%CATALINA_HOME%\bin" "%CATALINA_HOME%\bin\startup.bat"

echo     Dang doi Tomcat san sang tai http://localhost:8080/baitapweb/home ...
powershell -NoProfile -Command "for ($i=0; $i -lt 30; $i++) { Start-Sleep -Milliseconds 500; try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/baitapweb/home' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { break } } catch {} }"

start "" "http://localhost:8080/baitapweb/home"
echo.
echo ============================================================
echo   HOAN TAT! He thong da san sang:
echo   - Trang chu       : http://localhost:8080/baitapweb/home
echo   - Dang nhap       : http://localhost:8080/baitapweb/login
echo   - Ho so ca nhan   : http://localhost:8080/baitapweb/profile
echo   - Danh muc JPA    : http://localhost:8080/baitapweb/admin/categories
echo   - Danh muc JDBC   : http://localhost:8080/baitapweb/legacy/category/list
echo ============================================================
echo De tat Tomcat sau khi su dung xong, chay:
echo %CATALINA_HOME%\bin\shutdown.bat
echo.
pause
exit /b 0

:: Xu ly loi ro rang
:ERR_JAVA
echo [LOI] Khong tim thay JAVA_HOME! Vui long cai dat JDK 21 va set bien moi truong.
pause
exit /b 1

:ERR_JAVA_EXE
echo [LOI] java.exe khong ton tai tai: %JAVA_HOME%\bin\java.exe
pause
exit /b 1

:ERR_MVN
echo [LOI] Khong tim thay Maven! Vui long set MAVEN_HOME hoac them mvn vao PATH.
pause
exit /b 1

:ERR_CATALINA
echo [LOI] Khong tim thay CATALINA_HOME! Vui long cai dat Apache Tomcat va set bien moi truong.
pause
exit /b 1

:ERR_CATALINA_STARTUP
echo [LOI] startup.bat khong ton tai tai: %CATALINA_HOME%\bin\startup.bat
pause
exit /b 1

:ERR_BUILD
echo.
echo [LOI] Build Maven that bai tai buoc [3/4]!
pause
exit /b 1

:ERR_COPY
echo.
echo [LOI] Khong the copy baitapweb.war vao %CATALINA_HOME%\webapps!
pause
exit /b 1
