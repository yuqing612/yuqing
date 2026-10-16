@echo off
chcp 936 >nul
title yuqing 项目启动器
cd /d "%~dp0"

echo ==========================================
echo   yuqing 项目一键启动
echo ==========================================
echo.

rem ---------- 1. 检查 MySQL ----------
echo [1/3] 检查 MySQL 3306 端口 ...
netstat -ano | findstr ":3306" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo [错误] 没有检测到 MySQL 在 3306 端口运行！
    echo 请先在 Windows 服务中启动 MySQL80，再运行本脚本。
    pause
    exit /b 1
)
echo       MySQL 正常。

rem ---------- 1.5 初始化数据库（缺失则幂等建库，本地 dev root 密码 123456） ----------
echo [1.5] 检查数据库 yuqing_db ...
set "MYSQL=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set "MYSQL_ROOT_PWD=123456"
set "DBOK=0"
"%MYSQL%" -u yuqing -p123456 -e "USE yuqing_db;" >nul 2>&1 && set DBOK=1
if "%DBOK%"=="0" (
    echo       yuqing_db 不存在，使用 root 自动初始化 ...
    "%MYSQL%" -u root -p%MYSQL_ROOT_PWD% < springBootTest\sql\yuqing_db.sql >nul 2>&1
    if errorlevel 1 (
        echo [警告] 数据库初始化失败，请确认 MySQL root 密码（本脚本内置本地 dev 密码 123456）。
    ) else (
        echo       yuqing_db 初始化完成（用户 yuqing / 库 yuqing_db / 表 sys_user）。
    )
) else (
    echo       yuqing_db 已存在，跳过。
)

rem ---------- 2. 启动后端 ----------
echo [2/3] 启动后端 springBootTest 8081 ...
if not exist "springBootTest\mvnw.cmd" (
    echo [错误] 找不到 springBootTest\mvnw.cmd
    echo 请确认本脚本放在 yuqing-main 项目根目录下！
    pause
    exit /b 1
)
start "yuqing-backend" cmd /k "cd /d springBootTest && mvnw.cmd spring-boot:run"

rem ---------- 3. 启动前端 ----------
echo [3/3] 启动前端 anime-login-vue 5173 ...
if not exist "anime-login-vue\package.json" (
    echo [错误] 找不到 anime-login-vue\package.json
    echo 请确认本脚本放在 yuqing-main 项目根目录下！
    pause
    exit /b 1
)
if not exist "anime-login-vue\node_modules" (
    echo       检测到 node_modules 不存在，开机重置后先安装依赖 ...
    pushd "anime-login-vue"
    call npm install
    if errorlevel 1 (
        echo [错误] npm install 失败，请检查网络后重试。
        popd
        pause
        exit /b 1
    )
    popd
)
start "yuqing-frontend" cmd /k "cd /d anime-login-vue && npm run dev"

echo.
echo ==========================================
echo   启动完成！已弹出后端、前端两个窗口。
echo   后端: http://localhost:8081
echo   前端: http://localhost:5173  浏览器会自动打开
echo   首次启动后端要编译，等 30 秒左右属正常。
echo   关闭项目请运行 停止项目.bat
echo ==========================================
pause
