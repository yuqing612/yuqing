@echo off
chcp 936 >nul
title yuqing 项目停止
cd /d "%~dp0"

echo ==========================================
echo   停止 yuqing 项目（不动 MySQL）
echo ==========================================
echo.

set "stopped=0"

rem ---------- 停止后端 8081 ----------
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8081" ^| findstr "LISTENING"') do (
    echo 结束后端进程 PID=%%a
    taskkill /PID %%a /T /F >nul 2>&1
    set "stopped=1"
)

rem ---------- 停止前端 5173 ----------
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do (
    echo 结束前端进程 PID=%%a
    taskkill /PID %%a /T /F >nul 2>&1
    set "stopped=1"
)

echo.
if "%stopped%"=="1" (
    echo 已停止。后端、前端的黑窗口可以顺手关掉。
) else (
    echo 8081 和 5173 都没有进程在跑，项目本来就是停着的。
)
pause
