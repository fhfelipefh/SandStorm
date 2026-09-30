# Changelog - SandStorm

Todas as alterações notáveis no projeto **SandStorm** serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.5.4] - 2026-09-30

### 🔊 Feedback Sonoro e Partículas de Conclusão nas Máquinas
* **Feedback Multissensorial de Produção**:
  * Implementação do ciclo `onProcessCompleted` em `BaseMachineBlockEntity` propagado deterministicamente para todas as máquinas funcionais do SandStorm.
  * **Impressora 3D (3D Printer)**: Efeitos sonoros mecânicos com duplo clique (`PRINTER_3D_CRAFT` e `CRAFTER_CRAFT`), acompanhados de faíscas laser de solda (`ELECTRIC_SPARK`), fumaça térmica de assentamento (`WHITE_SMOKE`) e poof de finalização.
  * **Fabricador de Nanites (Nanite Fabricator)**: Ressonância quântica de ativação (`NANITE_ACTIVATE` e `BEACON_POWER_SELECT`) com matriz geométrica de nanorrobôs (`WAX_OFF`, `GLOW`, `ELECTRIC_SPARK`).
  * **Refinaria Química (Chemical Refinery)**: Alívio despressurizador de vapor industrial (`FIRE_EXTINGUISH`), borbulho ativo (`BREWING_STAND_BREW`) e batida de válvula metálica pesada (`IRON_TRAPDOOR_CLOSE`), liberando plumas densas de vapor na chaminé (`CAMPFIRE_COSY_SMOKE`, `WHITE_SMOKE`, `SMOKE`).
  * **Modificador Molecular (Molecular Modifier)**: Ativação quântica e ressonância atômica (`BEACON_ACTIVATE` e `AMETHYST_BLOCK_RESONATE`) com runas energéticas de encantamento (`ENCHANT`, `ELECTRIC_SPARK`, `GLOW`).
  * **Linha de Montagem Automatizada (Auto Assembly Line)**: Prensagem e estalo pneumático (`ASSEMBLY_CONSTRUCT`, `ANVIL_USE`, `CRAFTER_CRAFT`) com descarga de pressão (`POOF`, `ELECTRIC_SPARK`).
  * **Tanque Biorreator (Bioreactor Vat)**: Borbulho orgânico e gelatinoso (`BREWING_STAND_BREW`, `SLIME_BLOCK_PLACE`) com efervescência de nutrientes (`HAPPY_VILLAGER`, `SPLASH`, `COMPOSTER`).
  * **Filtro Dessalinizador (Desalination Filter)**: Descarga pressurizada de água potável (`DESALINATION_PROCESS`, `BOTTLE_FILL`) com jatos de gotículas filtradas (`SPLASH`, `DRIPPING_WATER`, `CLOUD`).
  * **Tanque Incubador Ciborgue (Cyborg Incubator Vat)**: Despertar cibernético (`BEACON_ACTIVATE`, `IRON_GOLEM_REPAIR`) com centelhas elétricas e fumaça biológica (`ELECTRIC_SPARK`, `CAMPFIRE_COSY_SMOKE`, `GLOW`).
  * **Broca de Núcleo Profundo (Deep Core Drill)**: Impacto de perfuração do manto planetário (`HEAVY_CORE_HIT`, `NETHERITE_BLOCK_BREAK`) com fumaça vulcânica e partículas incandescentes (`LARGE_SMOKE`, `LAVA`, `ELECTRIC_SPARK`).
  * **Câmara Hidropônica (Hydroponic Chamber)**: Efeito de colheita vegetal abundante (`CROP_BREAK`, `EXPERIENCE_ORB_PICKUP`) e fertilização verde (`HAPPY_VILLAGER`, `COMPOSTER`).
  * **Extrator de Plasma Lítico (Litho-Plasma Extractor)**: Descarga centrífuga com despressurização térmica e chamas de plasma (`BEACON_ACTIVATE`, `FIRE_EXTINGUISH`, `ELECTRIC_SPARK`, `SOUL_FIRE_FLAME`).

### ℹ️ Sistema Padronizado de Tooltips "SHIFT para Detalhes"
* **Padronização Universal de Tooltips**:
  * Integrada sinalização clara e elegante de tecla SHIFT em todos os módulos, tecnologias, componentes e equipamentos do mod, evitando poluição visual nas barras rápidas e inventários.

### 🏗️ Pré-visualização e Custo de Blueprints no Construtor de Megastruturas
* **Hologramas e Estimativas de Custo**:
  * Pré-visualização com análise em tempo real dos blocos necessários para as megastruturas em andamento.

### ⚡ Feedback Visual e LEDs no Drive de Disco Quântico
* **Indicadores Luminosos de Capacidade e Atividade**:
  * LEDs dinâmicos de alta visibilidade refletindo o estado de energia, integridade de dados e transferências no Quantum Disk Drive.

---

## [1.5.3] - 2026-09-30

### 🛠️ Bug Fixes & Stability
* **Terminal Shift-Click Freeze**: Resolved an infinite loop on the client render thread when shift-clicking items into the Quantum Access Terminal.
* **Header & Text Layout**: Fixed item count numbers overlapping the container title in the Quantum Access Terminal.
* **Adaptive Title Scaling**: Container titles now dynamically scale down if custom names or large storage counts exceed screen margins.

### ⚡ Performance
* **Optimized Terminal GUI**: Item grid filtering and sorting are now lazy-evaluated (`clientStateDirty`), running only when contents update, queries change, or sort modes toggle.

### 🧪 Architecture & Quality
* Added comprehensive anti-crash battery architecture tests auditing all container menus to permanently prevent shift-click infinite loops.

---

## [1.4.6] - 2026-09-27

### ⚔️ Paredes de Espinhos & Barreiras Defensivas
* **Colisão Perfurante & Caixa de Dano Físico**:
  * Recuo de colisão de 1 pixel em todas as faces laterais (`COLLISION_SHAPE`), permitindo que entidades e jogadores encostando na parede cruzem a borda física e recebam dano de contato imediato (`entityInside`).
* **Varredura Ativa Contínua por Proximidade**:
  * Implementado ciclo de checagem proativa a cada 10 ticks (0.5s) com detecção expandida (`AABB.inflate(0.15)`). Mobs estáticos ou pressionando a parede recebem dano contínuo, repulsão cinética e efeitos sonoros/partículas sem ficarem imunes.
  * **Paredes de Espinhos de Titânio**: 8.0 de dano de contato, repulsão de impacto e partículas críticas.
  * **Barreiras de Quitina Corrosiva**: 6.0 de dano biológico, envenenamento e corrosão contínua da armadura do atacante.
  * **Barreiras de Espinhos Eletrificadas**: 10.0 de dano de choque e lentidão paralisante quando energizadas (reduzido para 2.0 com traje espacial isolante); 4.0 de dano físico quando desenergizadas.
  * **Espinhos Retráteis de Parede**: Dano esmagador de 14.0 ao armar e 10.0 de contato contínuo no modo estendido.
  * **Espinhos Cinéticos de Chão**: Adicionada detecção lateral de penetração além da pisada vertical.
* **Modelagem 3D & Texturas com Pontas Salientes**:
  * Substituição dos antigos cubos lisos por modelos 3D com geometria pontiaguda tridimensional projetada para fora em todas as faces.
  * Texturas atualizadas com detalhes metálicos, quitinosos e centelhas energizadas.
  * Configuração isométrica calibrada para exibição adequada no GUI e inventário.

---

## [1.4.5] - 2026-09-27

### 🚀 Correções Críticas, Lore Isolada & Deploy Oficial no CurseForge
* **Isolamento de Lore & Compatibilidade com Modpacks**:
  * O jogador só nasce com o traje de sobrevivência e sofre penalidades ambientais (ausência de sono, oxigênio, radiação solar e atração de vermes da areia) quando o mundo for do tipo SandStorm (`sandstorm:desert_planet`).
  * Em outros mundos (Vanilla, Default, Flat, modpacks com outros biomas), o SandStorm atua como mod de conteúdo tecnológico sem aplicar restrições forçadas de Lore ao jogador.
* **Portão de Espinhos Esmagadores (`crushing_spike_gate`)**:
  * Correção de empilhamento: os blocos podem ser colocados um sobre o outro ou lateralmente sem sobreposição interna.
  * Modelos 3D dinâmicos industriais para estados Aberto e Fechado com dentes retráteis e pistões reforçados.
  * Colisão dinâmica permitindo passagem desimpedida quando aberto e bloqueio impenetrável com dano hidráulico quando fechado.
* **Interface & Tooltips**:
  * Linhas curtas em todos os tooltips do mod (máximo 50 caracteres) estruturadas via `List<Component>`, sem corte na interface.
* **CurseForge & Pipeline de Lançamento**:
  * Versão do Minecraft sincronizada estritamente com `1.21.4` para o catálogo da CurseForge.
  * Compatibilidade explícita com **Java 25** e **Java 21**.
  * Modo de falha estrito (`fail-mode: 'fail'`) no pipeline, impedindo que falhas de deploy sejam mascaradas como sucesso e prevenindo tags falsas no repositório.

---

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
