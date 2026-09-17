@echo off
setlocal enabledelayedexpansion

title SandStorm - Launcher and Mod Sync

echo ===================================================
echo           SANDSTORM MOD - MINECRAFT 26.3
echo ===================================================
echo.

set "SCRIPT_DIR=%~dp0"
set "MINECRAFT_DIR=%APPDATA%\.minecraft"
set "MODS_DIR=%MINECRAFT_DIR%\mods"
set "JAR_SOURCE=%SCRIPT_DIR%build\libs\sandstorm-1.0.0.jar"

echo [*] Diretorio do projeto: %SCRIPT_DIR%
echo [*] Diretorio do Minecraft: %MINECRAFT_DIR%
echo.

if not exist "%MODS_DIR%" (
    echo [+] Criando pasta de mods em %MODS_DIR%...
    mkdir "%MODS_DIR%"
)

if not exist "%JAR_SOURCE%" (
    echo [*] Compilando versao inicial do SandStorm...
    call "%SCRIPT_DIR%gradlew.bat" build -x test
    if errorlevel 1 (
        echo [!] Erro ao compilar o mod. Verifique os logs.
        pause
        exit /b 1
    )
)

if exist "%JAR_SOURCE%" (
    copy /Y "%JAR_SOURCE%" "%MODS_DIR%\sandstorm-1.0.0.jar" >nul
)

set "ARG=%~1"
if /i "%ARG%"=="run" goto opt_run
if /i "%ARG%"=="client" goto opt_run
if /i "%ARG%"=="1" goto opt_run
if /i "%ARG%"=="launcher" goto opt_launcher
if /i "%ARG%"=="2" goto opt_launcher
if /i "%ARG%"=="sync" goto opt_sync
if /i "%ARG%"=="3" goto opt_sync
if /i "%ARG%"=="dev" goto opt_dev
if /i "%ARG%"=="4" goto opt_dev

echo.
echo ===================================================
echo  Escolha como deseja iniciar o jogo:
echo ===================================================
echo  [1] Iniciar jogo direto com SandStorm (Recomendado)
echo  [2] Abrir o Launcher do Minecraft
echo  [3] Apenas atualizar mod e fechar
echo  [4] Modo Dev - Recompilar + Log ao vivo no terminal
echo ===================================================
echo.

set /p CHOICE="Selecione uma opcao [1-4] (Padrao: 1): "
if "%CHOICE%"=="" set CHOICE=1

if "%CHOICE%"=="1" goto opt_run
if "%CHOICE%"=="2" goto opt_launcher
if "%CHOICE%"=="3" goto opt_sync
if "%CHOICE%"=="4" goto opt_dev

echo [!] Opcao invalida. Encerrando.
exit /b 0

:opt_run
echo.
echo [*] Iniciando cliente do Minecraft com Fabric e SandStorm...
echo [*] O jogo abrira em sua propria janela grafica interativa.
start "Minecraft - SandStorm" "%SCRIPT_DIR%gradlew.bat" runClient
exit /b 0

:opt_launcher
echo.
echo [*] Tentando abrir o Minecraft Launcher...
start minecraft:
if errorlevel 1 (
    if exist "%ProgramFiles(x86)%\Minecraft Launcher\MinecraftLauncher.exe" (
        start "" "%ProgramFiles(x86)%\Minecraft Launcher\MinecraftLauncher.exe"
    ) else if exist "%ProgramFiles%\Minecraft Launcher\MinecraftLauncher.exe" (
        start "" "%ProgramFiles%\Minecraft Launcher\MinecraftLauncher.exe"
    ) else (
        echo [!] Nao foi possivel abrir o launcher automaticamente.
        echo [*] Abra o seu launcher manualmente; os arquivos ja estao atualizados em %MODS_DIR%.
        pause
    )
)
exit /b 0

:opt_sync
echo.
echo [*] Recompilando versao atualizada do SandStorm...
call "%SCRIPT_DIR%gradlew.bat" build -x test
if errorlevel 1 (
    echo [!] Erro ao compilar o mod.
    pause
    exit /b 1
)
copy /Y "%JAR_SOURCE%" "%MODS_DIR%\sandstorm-1.0.0.jar" >nul
echo [OK] Mod sincronizado com sucesso. Bom jogo!
exit /b 0

:opt_dev
echo.
echo [*] Modo Dev selecionado.
echo [*] Recompilando o mod para garantir a versao mais atualizada...
call "%SCRIPT_DIR%gradlew.bat" build -x test
if errorlevel 1 (
    echo [!] Erro ao recompilar o mod. Abortando.
    pause
    exit /b 1
)

copy /Y "%JAR_SOURCE%" "%MODS_DIR%\sandstorm-1.0.0.jar" >nul
if errorlevel 1 (
    echo [!] Falha ao copiar o mod compilado para %MODS_DIR%.
) else (
    echo [OK] Mod recompilado e atualizado em:
    echo      %MODS_DIR%\sandstorm-1.0.0.jar
)

echo.
echo [*] Iniciando cliente com logs ao vivo...
echo [*] Pressione Ctrl+C no terminal para encerrar o jogo.
echo.

if not exist "%SCRIPT_DIR%run\logs" mkdir "%SCRIPT_DIR%run\logs"

for /f "tokens=1-6 delims=/:. " %%a in ("%DATE% %TIME%") do (
    set "LOG_DATE=%%c-%%b-%%a"
    set "LOG_TIME=%%d-%%e"
)
set "LOG_FILE=%SCRIPT_DIR%run\logs\dev-session-%LOG_DATE%_%LOG_TIME%.log"

echo [*] Salvando log em: %LOG_FILE%
echo.

powershell -Command "& { $env:JAVA_HOME='%JAVA_HOME%'; & '%SCRIPT_DIR%gradlew.bat' runClient 2>&1 | Tee-Object -FilePath '%LOG_FILE%' }"

echo.
echo ===================================================
echo  Sessao encerrada. Log salvo em:
echo  %LOG_FILE%
echo ===================================================
echo.

set /p OPEN_LOG="Deseja abrir o log no Notepad? [S/N]: "
if /i "%OPEN_LOG%"=="S" start notepad "%LOG_FILE%"

pause
exit /b 0
