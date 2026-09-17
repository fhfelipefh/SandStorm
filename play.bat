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
    echo [*] Compilando versao atualizada do SandStorm...
    call "%SCRIPT_DIR%gradlew.bat" build -x test
    if errorlevel 1 (
        echo [!] Erro ao compilar o mod. Verifique os logs.
        pause
        exit /b 1
    )
)

echo [*] Atualizando sandstorm-1.0.0.jar na pasta de mods...
copy /Y "%JAR_SOURCE%" "%MODS_DIR%\sandstorm-1.0.0.jar" >nul
if errorlevel 1 (
    echo [!] Falha ao copiar o mod para %MODS_DIR%.
) else (
    echo [OK] Mod copiado com sucesso para:
    echo      %MODS_DIR%\sandstorm-1.0.0.jar
)

set "ARG=%~1"
if /i "%ARG%"=="run" goto opt_run
if /i "%ARG%"=="client" goto opt_run
if /i "%ARG%"=="1" goto opt_run
if /i "%ARG%"=="launcher" goto opt_launcher
if /i "%ARG%"=="2" goto opt_launcher
if /i "%ARG%"=="sync" goto opt_sync
if /i "%ARG%"=="3" goto opt_sync

echo.
echo ===================================================
echo  Escolha como deseja iniciar o jogo:
echo ===================================================
echo  [1] Iniciar jogo direto com SandStorm (Recomendado)
echo  [2] Abrir o Launcher do Minecraft
echo  [3] Apenas atualizar mod e fechar
echo ===================================================
echo.

set /p CHOICE="Selecione uma opcao [1-3] (Padrao: 1): "
if "%CHOICE%"=="" set CHOICE=1

if "%CHOICE%"=="1" goto opt_run
if "%CHOICE%"=="2" goto opt_launcher
if "%CHOICE%"=="3" goto opt_sync

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
echo [OK] Mod sincronizado com sucesso. Bom jogo!
exit /b 0
