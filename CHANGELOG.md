# Changelog - SandStorm

Todas as alterações notáveis no projeto **SandStorm** serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.9.0] - 2026-10-06

### 🎨 Correção Visual Crítica & Texturização de Alta Fidelidade dos Golens Cibernéticos
* **Correção Estrutural do Modelo (`CyberneticGolemModel`)**:
  * Correção da hierarquia de partes corporais: a cabeça (`head`), braços (`right_arm`, `left_arm`) e tronco (`body`) foram reestruturados como filhos diretos da raiz (`root`), corrigindo o bug visual crítico em que a cabeça flutuava desacoplada no ar e os braços se projetavam acima dos ombros.
  * Alinhamento estrito com a anatomia e poses padrão do Iron Golem do Minecraft 1.21.4, preservando inclinações de arrancada em Overdrive e oscilações táteis.
* **Texturização Profissional Completa de Todos os Metais (128x128)**:
  * Mapeamento UV preciso sem sobreposições de coordenadas para as 5 variantes metálicas e a camada emissiva:
    * **Cobre (Copper)**: Blindagem cobreada industrial com pátina de oxidação verdigris/ciano, tubulações de latão e reator de plasma esmeralda.
    * **Ferro (Iron)**: Aço escovado e titânio balístico com juntas em fibra de carbono escura e reator de arco ciano elétrico.
    * **Ouro (Gold)**: Proteção térmica aeroespacial com placas douradas espelhadas e núcleo de singularidade violeta/magenta.
    * **Netherita (Netherite)**: Chassi blindado de carbeto de tungstênio e netherita fosca com ranhuras térmicas em brasa carmesim.
    * **Compósito (Titanium-Chitin Composite)**: Carapaça biocibernética de quitina de vermes das dunas com iridescência e matriz iônica solar âmbar.
  * **Face de Abóbora Cibernética Integrada**: Todos os modelos agora ostentam a face estilizada da Cabeça de Autômato na fronte, com olhos angulares e boca dentada iluminada.
  * **Textura Emissiva de Sobreaquecimento (`cybernetic_golem_heat_glow.png`)**: Olhos, boca cibernética, reator frontal e propulsores traseiros e articulares brilham em alta intensidade no escuro durante o estado de Overdrive.

---

## [1.8.0] - 2026-10-06

### ⚡ Armamento Tático de PEM & Paralisia em Massa de Androides
* **Emissor de Pulso Eletromagnético (EMP Blaster)**:
  * Novo armamento tático de alto risco e recompensa extrema (`EmpBlasterItem`), disparando uma onda expansiva de sobrecarga eletromagnética com raio de 48 blocos.
* **Sobrecarga e Paralisia Escalar em Massa**:
  * Neutraliza dezenas, centenas ou milhares de unidades cibernéticas e autômatos simultaneamente no raio de ação (`EmpParalysisHandler`).
  * Duração escalar proporcional à distância do epicentro: no limite do alcance (48 blocos), dura no mínimo 1 minuto (1200 ticks); a queima-roupa, atinge até 2 minutos (2400 ticks) de inabilitação completa.
  * Inibe locomoção, reseta alvos de IA, neutraliza pavios de autodestruição em autômatos abandonados e desativa arrancadas de golens, emitindo arcos elétricos contínuos e estalos de sobrecarga térmica.
* **Efeito Adverso Severo no Operador (Backlash)**:
  * Ao detonar o dispositivo, o jogador sofre imediatamente um contra-golpe eletromagnético por 7 segundos:
    * **Visão Reduzida**: Cegueira e escuridão intensas (`MobEffects.BLINDNESS` e `MobEffects.DARKNESS`).
    * **Ensurdecimento e Tinnitus**: Bloqueio total de sons ambientes e do mundo através de supressão seletiva de áudio no cliente (`EmpDeafenClientHandler`) acompanhado de zumbido agudo e estática magnética no visor HUD (`SurvivalHudOverlay`).
    * **Incapacidade de Correr**: Aplicação de Lentidão extrema nível V (`MobEffects.SLOWNESS`) e corte forçado de arrancada/sprint a cada tick.
* **Cadeia Completa de Assets e Receita Tecnológica**:
  * Textura detalhada 16x16 com bobinas condutoras e capacitores emissores ciano, modelos e definições JSON 1.21.4, receita de manufatura balanceada com placas estruturais, circuitos e nano-atuadores.

---

## [1.7.0] - 2026-10-06

### 🧭 Ecossistema Autônomo e Predação Dinâmica nas Dunas
* **Geração Inteligente Fora do Campo de Visão (Out-of-Sight Spawning)**:
  * Sistema de spawn procedural (`DesertMobSpawnManager`) no deserto do SandStorm que elimina o surgimento abrupto de mobs na visão direta do jogador.
  * Validação trigonométrica de cone de visão (`look.dot`) e traçado de raio (`ClipContext.Block.VISUAL`): autômatos surgem exclusivamente atrás de dunas, cristas de relevo ou fora do alcance periférico da câmera.
* **I.A. de Exploração e Varredura de Terreno (Desert Exploration Wander)**:
  * Implementação de `DesertExplorationWanderGoal` substituindo o passeio estático vanilla por navegação de longo alcance (24 a 48 blocos) através das dunas.
  * Rotina de inspeção e busca sensorial: autômatos pausam periodicamente em cumes de areia para escanear o horizonte com rotação de cabeça, inclinação ótica e faíscas cibernéticas, simulando procura ativa por sinais e recursos.
  * Geração de vibrações sísmicas dinâmicas a cada passo mecânico sobre a areia (`SeismicSurvivalHandler`), com intensidade proporcional ao porte da unidade (unidades operárias e drones pesados causam maior perturbação).
* **Predação Cinematográfica de Vermes de Areia (Sandworm Apex Hunting)**:
  * Evento de predação ambiental (`DesertPredationHandler`): vermes gigantes emergem violentamente das profundezas para caçar e devorar autômatos errantes diante dos olhos do jogador.
  * Sequência de choque com tremor prévio, abalo de partículas de areia, ruptura de solo e mordida esmagadora que destrói as máquinas em fragmentos de sucata e sons de mastigação mecânica.
* **Bloqueio de Golens de Ferro Vanilla & Autômatos Guardiões Cibernéticos**:
  * **Supressão do Golem Vanilla**: Impedida a criação de golens de ferro convencionais no deserto do SandStorm (`VanillaMonsterSuppressionHandler`), interceptando abóboras entalhadas sobre blocos de ferro com feedback sonoro e aviso de telemetria.
  * **Cabeça Robótica de Autômatos (`Cybernetic Golem Head`)**: Bloco direcional estilizado como abóbora entalhada cibernética de Halloween com matriz de circuitos e LEDs frontais. Usado como núcleo de senciência para montagem de autômatos em padrão clássico em T.
  * **Forjamento Multimetálico Universal (`GolemMetalTier`)**: O golem cibernético pode ser erguido com qualquer bloco metálico (Ferro, Cobre, Ouro, Netherita e Compósito de Titânio/Quitina), escalando atributos de vida máxima, blindagem, resistência a recuo, dano de impacto e taxa de recarga térmica.
  * **Sistema de Propulsão em Sobrecarga (5X Overdrive Boost)**: Mecânica automática de proteção ao jogador. Ao detectar ameaças distantes, sobrecarrega os atuadores hidráulicos com faíscas elétricas (`ELECTRIC_SPARK`), juntas incandescentes de alta emissividade e velocidade extrema de arrancada.
  * **Ciclo Térmico & Resfriamento Gradual**: Após o combate, dissipa calor progressivamente liberando plumas de vapor (`CAMPFIRE_COSY_SMOKE`) e chiado térmico (`FIRE_EXTINGUISH`), permitindo nova arrancada apenas quando os dissipadores esfriarem por completo.

---

## [1.6.0] - 2026-10-05

### 🤖 Fauna Mecanoide do Apocalipse Tecnológico
* **Substituição de Mobs Orgânicos por Autômatos Antigos**:
  * **Autômato Abandonado (Derelict Automaton)**: Andróide bípede de combate com rotina corrompida de autodestruição. Ao se aproximar de alvos, trava no solo e sobrecarrega o núcleo de fusão, gerando explosão física e **onda de choque EMP** que drena a energia do traje espacial (`SuitPowerComponent`). Dropar de sucata e placas de circuito danificadas.
  * **Cão Cibernético (Cyber Hound)**: Robô quadrúpede batedor da série *K-9 Recon*. Não procria nem consome matéria orgânica; domesticado exclusivamente via reprogramação de firmware e reparado com sucata metálica (`scrap_metal`). Possui visor óptico que alterna entre âmbar (selvagem) e ciano (amigável), modo *Stand-by* e sistema de alerta sensorial para tempestades e ameaças nas dunas.
  * **Ferramenta de Reprogramação (Reprogrammer Tool)**: Novo dispositivo portátil ergonômico com antena e interface holográfica. Consome energia do traje espacial para hackear e sobrescrever diretrizes de firmware em unidades mecânicas do mundo.
  * **Unidade Operária (Laborer Unit)**: Autômato industrial de blindagem pesada e pistões hidráulicos reforçados. Imune a calor/fogo, resistente a recuo e com ataques pesados que causam lentidão momentânea.
  * **Drone Explorador (Scout Drone)**: Andróide esbelto de reconhecimento tático com chassi de titânio e canhão de feixe cinético acoplado ao braço direito, mantendo distância e disparando contra alvos.
  * **Drone Rastreador (Crawler Drone)**: Robô hexápode/octópode de perfuração e escalada ágil de cânions, munido de serras e lâminas de alta rotação.
* **Geração Natural em Ruínas Tecnológicas**:
  * Autômatos, cães cibernéticos e drones sentinelas patrulham e guarnecem estruturas abandonadas geradas proceduralmente (`AbandonedOutpostGenerator`).
* **Novas Quests & Árvore de Progressão**:
  * Adicionadas quests "Reprogrammer Tool", "Titanium Companion" e "Mechanical Menace" nos Capítulos 3 e 4 do Datapad de Sobrevivência.
* **Showcase Monumental & Ferramental de Depuração**:
  * Incorporação de vitrines dedicadas para todas as 5 entidades mecânicas na Galeria de Exibição (`ExhibitionGalleryManager`).
  * Expansão dos comandos de depuração `/sandstorm_debug robot <tipo>` e `/sandstorm_debug spawn <tipo>` suportando todas as novas unidades.

---

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
