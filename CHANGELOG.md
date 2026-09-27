# Changelog - SandStorm

Todas as alterações notáveis no projeto **SandStorm** serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.4.4] - 2026-09-27

### 🔄 Distribuição & Metadados
* **CurseForge & Pipeline de Lançamento**:
  * Incremento de versão e consolidação dos metadados de execução com tags de compatibilidade explícitas para **Java 25** e **Java 21**.
  * Geração e empacotamento automatizado das notas de release diretamente do changelog do repositório.

---

## [1.4.3] - 2026-09-27

### 🚀 Correções Críticas & Melhorias de Jogabilidade
* **Portão de Espinhos Esmagadores (`crushing_spike_gate`)**:
  * **Empilhamento & Posicionamento Corrigidos**: Implementada delegação de interação para `BlockItem` no método `useItemOn`. Agora você pode empilhar portões perfeitamente um em cima do outro, no chão ou nas laterais sem que o bloco seja colocado "dentro" e sem alternar o portão involuntariamente.
  * **Novo Modelo 3D e Textura Industrial**: Substituído o antigo cubo genérico por modelos 3D customizados para os estados **Aberto** e **Fechado**. A textura agora conta com colunas reforçadas de titânio, rebites industriais, pistões cromados, sinalização mecânica de advertência (*hazard stripes*) e dentes de lâmina afiados.
  * **Colisão & Passagem Dinâmica**: Quando aberto, o vão central é 100% livre para caminhada sem travar o jogador ou entidades. Quando fechado, bloqueia totalmente a passagem e aplica 12 de dano de esmagamento com som pesado de impacto hidráulico (`ANVIL_LAND`).
* **Interface & Tooltips**:
  * Linhas de texto e tooltips otimizadas para não ultrapassarem 50 caracteres, eliminando cortes ou vazamentos fora da tela mesmo em escalas altas de GUI do Minecraft.
* **Documentação & Wiki**:
  * Adicionada galeria fotográfica completa em `docs/screenshots/` e integrada à Wiki do GitHub e CurseForge, ilustrando a Torreta Sônica em combate, Domo de Escudo de Plasma, Ciborgues Autônomos e malha ferroviária Maglev.
* **Plataforma & Pipeline**:
  * Configuração explícita de metadados de execução com **Java 25** e **Java 21** para o CurseForge e geração automatizada de changelogs no CI/CD.

---

## [1.4.2] - 2026-09-27

### 🌟 Lançamento Oficial no CurseForge
* **Sobrevivência Planetária**:
  * Traje espacial com suporte à vida contínuo regulado por energia solar e temperatura ambiente.
  * Ciclo circadiano contínuo (24/7): camas vanilla desativadas para exigir engenharia térmica e isolamento de base.
* **Predadores Sísmicos (Vermes de Areia)**:
  * Mecânica de ressonância sísmica acumulada por passos e mineração na areia solta.
  * Dispositivos de dispersão e distração (*Thumpers*).
* **Indústria & Automação**:
  * Malha de energia sem fio por indução e ressonância magnética (WPT).
  * Filtragem osmótica de aquíferos salobres subterrâneos para obtenção de água potável estéril.
  * Manufatura aditiva de precisão via Impressora 3D e Fabricador de Nanorobôs.
* **Robótica & Defesa**:
  * Clonagem criogênica e comando tático de Ciborgues Autônomos via Torre Holo-Tática.
  * Armamento anti-titã: Canhões Cinéticos (*Railguns*), Pilões Acústicos e Torretas Sônicas.
* **Terraformação Setorial (Endgame)**:
  * Processadores Atmosféricos e Geradores de Escudo de Plasma gerando microclimas verdes protegidos no deserto infinito.
