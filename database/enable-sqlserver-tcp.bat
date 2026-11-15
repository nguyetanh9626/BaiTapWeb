@echo off
setlocal EnableExtensions

echo [1/3] Dang bat TCP/IP cong 1433 va Mixed Mode Authentication cho SQL Server Express...
reg add "HKLM\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQLServer" /v LoginMode /t REG_DWORD /d 2 /f
if errorlevel 1 goto :ERR

reg add "HKLM\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQLServer\SuperSocketNetLib\Tcp" /v Enabled /t REG_DWORD /d 1 /f
if errorlevel 1 goto :ERR

reg add "HKLM\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQLServer\SuperSocketNetLib\Tcp\IPAll" /v TcpDynamicPorts /t REG_SZ /d "" /f
if errorlevel 1 goto :ERR

reg add "HKLM\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQLServer\SuperSocketNetLib\Tcp\IPAll" /v TcpPort /t REG_SZ /d "1433" /f
if errorlevel 1 goto :ERR

echo [2/3] Dang khoi dong lai service MSSQL$SQLEXPRESS...
net stop "MSSQL$SQLEXPRESS" /y
net start "MSSQL$SQLEXPRESS"
if errorlevel 1 goto :ERR

echo [3/3] Da bat TCP/IP cong 1433 thanh cong!
exit /b 0

:ERR
echo.
echo [LOI] Khong the cap nhat cau hinh SQL Server. Hay chuot phai chon Run as administrator.
pause
exit /b 1
