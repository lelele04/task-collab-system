@echo off
chcp 65001 >nul
echo ========================================
echo   团队任务协作系统 - 快速启动脚本
echo ========================================
echo.

echo [1/5] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java环境，请先安装JDK 11+
    pause
    exit /b 1
)
echo [OK] Java环境检测通过

echo.
echo [2/5] 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Maven环境，请先安装Maven
    pause
    exit /b 1
)
echo [OK] Maven环境检测通过

echo.
echo [3/5] 检查Node.js环境...
node -v >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Node.js环境，请先安装Node.js
    pause
    exit /b 1
)
echo [OK] Node.js环境检测通过

echo.
echo [4/5] 启动MySQL数据库（请确保已安装并配置MySQL）...
echo 请手动执行以下命令初始化数据库：
echo   mysql -u root -p ^< database/init.sql
echo.

echo [5/5] 检查数据库配置...
findstr /C:"jdbc:mysql://localhost:3306/team_task_db" src\main\resources\application.yml >nul
if errorlevel 1 (
    echo [警告] 请确保 application.yml 中的数据库配置正确
    echo   数据库名: team_task_db
    echo   用户名: root
    echo   密码: root123
)

echo.
echo ========================================
echo   环境检查完成！
echo ========================================
echo.
echo 启动步骤:
echo.
echo 1. 确保MySQL已启动并执行了 database/init.sql
echo.
echo 2. 启动后端 (新窗口):
echo    cd backend
echo    mvn spring-boot:run
echo    或
echo    mvn clean package ^&^& java -jar target/team-task-system-1.0.0.jar
echo.
echo 3. 启动前端 (新窗口):
echo    cd frontend
echo    npm install
echo    npm run dev
echo.
echo 4. 访问系统: http://localhost:3000
echo.
echo 测试账号:
echo   admin / admin123
echo   user1 / user123
echo ========================================
echo.
pause