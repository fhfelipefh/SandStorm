# Changelog - SandStorm

Todas as alterações notáveis no projeto **SandStorm** serão documentadas neste arquivo.

## [1.19.50] - 2026-10-30

### ❄️ Correção de Geometria dos Androides Apex
* Corrigidas as caixas físicas do Guardião Criogênico, Grazer, Fanghorn, Glinthawk e Waterwing para coincidirem com a geometria renderizada.
* O Guardião Criogênico deixou de ocupar uma colisão invisível muito acima do seu corpo, restaurando seleção, combate e navegação coerentes.
* Adicionada regressão automatizada para manter as dimensões físicas sincronizadas com os cinco modelos.

## [1.19.49] - 2026-10-30

### 🧭 Reconstrução do Showcase com Isolamento Espacial
* Realocadas as atrações que compartilhavam blocos e adicionada validação geométrica contra sobreposições.
* Recriado o mundo `SandStorm_Showcase` a partir de chunks limpos.

## [1.19.36] - 2026-10-29

### 🌐 Reestruturação Espacial das 4 Grandes Avenidas do Showcase, Terminal de Trânsito Rápido & Renderização Sólida do Caixote
* **Expansão Espacial & Fim da Superlotação no Proving Ground (`SandStorm_Showcase`)**:
  * **Grandes Avenidas Cardeais Dedicadas**:
    * **Avenida Oeste (Indústria & Mobilidade)**: Complexo de Carbono e Forjas expandido para 25x25 em `(-50, 160, 0)`, totalmente isolado da praça central e do tabuleiro de blocos; Circuito de testes Maglev expandido para 30x30 em `(-90, 160, 0)`, interligado por via expressa de 5 blocos de largura.
    * **Avenida Leste (Computação Quântica, Cofres & Órbita)**: Computador Quântico movido para `(50, 160, 0)` em plataforma 15x15; Câmara de Cofre Blindada isolada em `(50, 160, -45)`; Portal Subespacial em `(80, 160, -45)`; Base Espacial Orbital com canhão de massa em `(80, 160, 25)`.
    * **Avenida Norte (Defesa, Balística, Rapel & Ciborgues)**: Perímetro de Defesa e Balística com railgun em `(-45, 160, -50)`; Estação de Rapel e Abismo de 128 blocos ampliada para plataforma 15x15 em `(45, 160, -50)`; Complexo de Ciborgues e Doca de Drones movidos para o extremo norte em `z = -80..-115`.
    * **Avenida Sul (Galeria de Tecnologias, Domo & Biosferas)**: Tabuleiro de blocos do mod com espaçamento aumentado para 5 blocos (`spacing = 5`), corredores de circulação amplos e desimpedidos que desembocam no Domo de Megaestruturas e nas 7 Biosferas Planetárias Gigantes.
* **Isolamento Total do Sistema de Teleporte ("Afastar Tapetes")**:
  * **Terminal de Trânsito Rápido Noroeste**: todas as 16 placas de teleporte do showcase (8 setores cardeais/instalações de superfície e 6 megadungeons subterrâneas) foram reunidas em um pavilhão elevado de transporte rápido em `x = -15..-33, z = -14..-29`.
  * **Eliminação de Minas de Teleporte**: removidas todas as placas de pressão do piso que interceptavam o fluxo de caminhada e exploração do jogador pela praça central e corredores.
  * **Sensibilidade de Gatilho Aperfeiçoada**: raio horizontal de ativação reduzido para `<= 0.35` blocos (`distX`, `distZ`), exigindo pisada deliberada sobre o pedestal para evitar qualquer ativação acidental durante a caminhada.
* **Fim do Flickering e Limpeza Visual na Galeria de Blocos**:
  * **Renderização Opaque-Solid do Caixote de Arenito (`SandstoneCrateBlockEntityRenderer`)**: migrado o render type de `RenderTypes.entityCutoutCull` para `RenderTypes.entitySolid(TEXTURE)`, garantindo escrita estrita no buffer de profundidade (*depth buffer*) e teste Z opaco correto contra vidros e blocos translúcidos adjacentes.
  * **Remoção de Placas de Carvalho Conflitantes**: eliminadas placas de sinalização de carvalho na parede que causavam *z-fighting* e *clipping* através de modelos de blocos; toda identificação passa a ser realizada exclusivamente por hologramas tridimensionais nítidos.

## [1.19.35] - 2026-10-29

### 🧗 Refinamento do Sistema de Rapel, Modelo 3D da Turbina do Capturador & Correção de Z-Fighting no Caixote
* **Correção de Z-Fighting / Cintilação no Caixote de Arenito (`SandstoneCrateBlock`)**:
  * Substituído o render type em `SandstoneCrateBlockEntityRenderer` de `RenderTypes.entityCutout` para `RenderTypes.entityCutoutCull` com descarte de faces posteriores (*backface culling*).
  * Afastado o fecho/trinco frontal da tampa (`lock`) em `0.01f` em `SandstoneCrateModel`, eliminando sobreposição coplanar exata com a parede frontal do baú e erradicando qualquer *flickering* visual em qualquer ângulo de câmera.
* **Redesign Completo do Duto e Ventilador 3D do Capturador de Carbono Atmosférico**:
  * Textura superior (`atmospheric_carbon_extractor_top.png`) totalmente redesenhada: removida a hélice 2D pintada e substituída por uma abertura cilíndrica de sucção ("buraco") com chanfro metálico de admissão, gradiente radial escurecido e grade/malha metálica protetora no fundo da cavidade.
  * Criado o modelo 3D dedicado `AtmosphericCarbonExtractorFanModel`: rotor industrial com 6 pás aerodinâmicas inclinadas (*pitched blades*), cubo central e ogiva de rotação girando no interior do duto superior.
  * Renderizado em `AtmosphericCarbonExtractorBlockEntityRenderer` com velocidade de rotação dinâmica (suave em repouso e hiper-acelerada quando em processamento ou com tiragem por combustão).
* **Aprimoramento da Mecânica de Rapel & Correção Estrutural no Showcase**:
  * **Correção do Abismo e Passarela no Showcase**: corrigida a geração da passarela em `ShowcaseAutomation.java` que pavimentava pedras sobre o poço de descida; alinhados os guinchos e o poço de 128 blocos em queda livre para que as cordas desçam sem obstrução até o piso de amortecimento sísmico.
  * **Auto-Captura Inteligente e Não-Invasiva**: restrita a captura automática exclusivamente a jogadores em queda livre (`!player.onGround() && player.fallDistance > 0.5f`), impedindo que o arnês capture jogadores caminhando normalmente no chão da plataforma.
  * **Propulsão Motorizada com Sprint**: segurar `Ctrl` (corrida) durante a subida ou descida aciona o impulso de alta rotação (+75% de velocidade).
  * **Desengate Suave no Solo**: ao atingir o fundo do abismo ou tocar o chão com `S`, o jogador desce suavemente do arnês.
  * **Recolhimento à Distância na Base**: Shift + Botão Direito em qualquer bloco da corda inicia o recolhimento automático do guincho até a base.
  * **Hologramas de Orientação Atualizados**: comandos de operação exibidos com precisão na estação de testes.

## [1.19.34] - 2026-10-29

### ⚡ Complexo de Carbono & Indução Térmica no Showcase & Comandos Dedicados
* **Estação Interativa Completa no Proving Ground (`SandStorm_Showcase`)**:
  * Adicionado o **Complexo de Carbono & Indução Térmica** em `center.offset(-18, 0, 0)` com 5 setups funcionais demonstrando todas as novas tecnologias:
    * **Setup 1 (Fornalha a Arco Elétrico)**: energizada via Acumulador de Estado Sólido e WPT, fundindo silício bruto em wafers de silício com efeito de indução térmica.
    * **Setup 2 (Capturador de Carbono em Modo Carvão)**: operando sob céu aberto, demonstrando a conversão de $CO_2$ atmosférico em carbono sólido/carvão.
    * **Setup 3 (Síntese Epitaxial CVD de Diamante)**: com semente natural de diamante no substrato e feixe de plasma ativo gerando novos diamantes.
    * **Setup 4 (Tiragem Térmica por Combustão)**: Capturador de Carbono adjacente a chamas de Netherrack com boost de velocidade (+200%) demonstrado na prática.
    * **Setup 5 (Logística & Deserto Primário)**: Fornalha Solar de Arenito e Caixote de Arenito com tampa procedural 3D articulada e 54 slots de inventário.
  * **Passarela e Acesso**: passarela envidraçada ligando a Praça Central ao Complexo Oeste, com placas de teleporte bidirecionais e holográficas.
  * **Baús de Suprimento Atualizados**:
    * Novo Baú na Praça Central (`chestPos3`) abastecido com todos os itens, máquinas e materiais recentes (`ATMOSPHERIC_CARBON_EXTRACTOR`, `ELECTRIC_ARC_FURNACE`, `SANDSTONE_CRATE`, `SANDSTONE_FURNACE`, `SYNAPTIC_CARTRIDGE`, `SIGNAL_TRACKER`, `MOLECULAR_CARGO_CAPSULE`, silício, carvão e diamantes).
    * Baú de Suprimento dedicado instalado no próprio Complexo.
* **Comandos Dedicados de Demonstração e Debug**:
  * Comandos de rota: `/sandstorm carbon`, `/sandstorm extractor`, `/sandstorm furnace`, `/sandstorm induction`.
  * Comandos de debug: `/sandstorm_debug carbon`, `/sandstorm_debug extractor`, `/sandstorm_debug furnace`, `/sandstorm_debug induction`.
  * Comandos diretos na raiz: `/sandstorm_carbon`, `/sandstorm_extractor`, `/sandstorm_furnace`.
  * Integração de autocompletação em `SandstormDebugCommand.FACILITIES` com `carbon_foundry`, `carbon`, `extractor` e `furnace`.
* **Auditoria de Arquitetura & Testes de Unidade**:
  * `ShowcaseComputerAndBlocksTest`: validação de nós de comando, explicações de blocos no showcase e cobertura total dos novos blocos no tabuleiro de exibição.

## [1.19.33] - 2026-10-28

### 🔥 Efeito Termodinâmico de Tiragem por Combustão no Capturador de Carbono
* **Aceleração por Combustão Térmica Externa**:
  * Implementação da mecânica de termodinâmica e concentração de $CO_2$: a presença de chamas vivas e fontes de combustão ao redor do Capturador de Carbono Atmosférico (`Blocks.FIRE`, netherrack em chamas, fogueiras acesas `Blocks.CAMPFIRE`, `Blocks.SOUL_FIRE`, `Blocks.LAVA`, `Blocks.MAGMA_BLOCK`) injeta calor e gás carbônico concentrado nas entradas da máquina.
  * **Escala de Eficiência e Velocidade**:
    * 0 chamas: velocidade base (1x).
    * 1 chama adjacente (ex: netherrack aceso): **+100% de velocidade (2x mais rápido)** — carvão em 30s, diamante em 2 min.
    * 2 chamas adjacentes: **+200% de velocidade (3x mais rápido)** — carvão em 20s, diamante em 80s.
    * 3 ou mais chamas adjacentes: **+300% de velocidade (4x mais rápido)** — carvão em 15s, diamante em 60s.
  * **Feedback Visual e Sensorial**:
    * Renderizador 3D: rotação da turbina acelerada dinamicamente proporcional à tiragem térmica com linhas de indução térmica radiante no piso.
    * Partículas: sucção ativa de pequenas chamas (`SMALL_FLAME`) e fumaça quente (`SMOKE`) para a câmara de eletrólise.
    * GUI & Tooltip: micro-ícone de chama pulsante na câmara e tooltip multinível com porcentagem de tiragem térmica ativa (`+100% Velocidade de Reação Térmica`).

## [1.19.32] - 2026-10-28

### 💎 Síntese Epitaxial CVD de Diamante & Upgrade do Capturador Atmosférico
* **Síntese de Diamantes por Deposição Química de Vapor (CVD)**:
  * Expansão da câmara de reação do Capturador de Carbono Atmosférico com slot dedicado para semente de cristalização (`minecraft:diamond`).
  * Requisito de substrato epitaxial natural fiel à vida real: o processo de deposição de carbono puro do plasma exige um diamante como semente/matriz de nucleação cristalina. A semente em si é preservada na matriz.
  * Curva de esforço termodinâmico e balanceamento industrial:
    * Modo Carvão (sem semente): 1.200 ticks (60s), 20 E/tick, totalizando 24.000 E por carvão.
    * Modo Diamante (com semente): 4.800 ticks (240s = 4 minutos), 35 E/tick, totalizando 168.000 E por diamante gerado.
  * Automação sided aprimorada com 3 slots dedicados: topo (semente / matriz de diamante), base (saída automatizada de carvão ou diamante), laterais (baterias e redstone).
  * Renderizador procedural 3D com feixe laser/plasma de deposição CVD ciano de alta voltagem e partículas de faíscas elétricas e brilho de diamante.
  * Integração ao catálogo visual de receitas in-game com preview animado da nucleação de cristais na GUI.

## [1.19.31] - 2026-10-28

### ⚡ Fornalha Elétrica de Indução & Captura Atmosférica de Carbono
* **Fornalha a Arco Elétrico (`electric_arc_furnace`)**:
  * Fornalha industrial de alta temperatura sem queima de carvão, alimentada pela rede elétrica sem fio de energia solar (WPT), geradores térmicos, acumuladores de estado sólido e baterias.
  * Consumo inteligente sob demanda: zero gasto de energia em modo de espera (`LIT = false`), acionando os enrolamentos de indução apenas na presença de itens para fundição.
  * Alta velocidade (80 ticks / 4s por item) com suporte completo a receitas de fundição vanilla (`SMELTING`) e alto-forno (`BLASTING`), viabilizando a obtenção de lâminas de silício a partir de silício bruto sem gastar combustível fóssil.
  * Renderizador 3D procedural com câmara de quartzo, item suspenso em levitação indutiva e arcos elétricos dinâmicos de alta voltagem.
* **Capturador de Carbono Atmosférico (`atmospheric_carbon_extractor`)**:
  * Sistema de Captura Direta de Ar (DAC) e eletrólise de carbonatos fundidos a quente ($CO_2 + \text{energia} \rightarrow C + O_2$).
  * Desbloqueio definitivo de progressão para seeds desérticas sem carvão ou biomassa, sintetizando carvão sólido (`minecraft:coal`) do ar.
  * Balanceamento industrial estrito anti-OP: taxa lenta e alta demanda termodinâmica de energia (1.200 ticks / 60s por unidade, 20 E/tick, totalizando 24.000 unidades de energia por carvão).
  * Requisito de desobstrução atmosférica superior com exaustão de plumas de oxigênio puro (`CLOUD`).
  * Renderizador 3D com rotor de turbina centrífuga animado em rotação contínua ($15^\circ/\text{tick}$) e núcleo de eletrólito fundido.
* **Cadeia de Insumos para Early-Game**:
  * Receitas acessíveis sem dependência circular de carvão ou silício (combinando cobre, arenito, sucata metálica, redstone e grades de ferro).

## [1.19.30] - 2026-10-28

### 🧪 Ecossistema Sináptico de XP Verde Vanilla & Renderizadores Procedurais
* Paleta líquida de XP padronizada com verde luminoso esmeralda vanilla (`#55FF20` / `#7CFC00`).
* Condensador sináptico, aspirador de XP, reparador molecular e módulo de sobremarcha neural.
* Renderizadores 3D procedurais e polimento de animações de caminhada (gait) para drones e cães cibernéticos.

## [1.19.29] - 2026-10-28

### 📡 Sistema de Telemetria e Rastreamento Tático & Caixote 3D Animado
* **Microtransponder Bio-Mecânico (`transponder_tag`)**:
  * Equivalente tecnológico da etiqueta vanilla, confeccionado com biopolímeros flexíveis e circuitos integrados.
  * Clique com botão direito em criatura viva (`LivingEntity`) para nomeá-la, travar persistência (`Mob.setPersistenceRequired()`) e registrar frequência na base persistente.
  * Shift + clique com botão direito em qualquer bloco para fixar uma baliza de telemetria estática persistente (ex: bases, caixotes, minas).
  * Operação 100% silenciosa sem sons ou ruídos emitidos.
* **Rastreador Tático de Sinais (`signal_tracker`)**:
  * Localizador omnidirecional de frequências de telemetria portátil.
  * Clique direito para alternar sequencialmente entre os transponders ativos sintonizados.
  * Telemetria na barra de ação (HUD Actionbar) com vetor direcional de mira relativo à visão do jogador (`⬆`, `↗`, `➡`, `↘`, `⬇`, `↙`, `⬅`, `↖`), distância em metros com gradação de cores (Verde, Ciano, Amarelo, Laranja e Vermelho), indicador de altitude (`▲`/`▼`) e detecção interdimensional.
  * Alcance dimensional global sem sobrecarga de processamento ou carregamento forçado de chunks, preservando TPS e memória.
* **Caixote de Arenito 3D Animado (`sandstone_crate`)**:
  * Modelo e renderizador 3D dedicado (`SandstoneCrateBlockEntityRenderer`, `SandstoneCrateModel`) com textura mapeada de 64x64.
  * Animação mecânica suave de rotação da tampa com controle de abertura/fechamento e sincronização com áudio de baú vanilla.

## [1.19.28] - 2026-10-28

### 🚀 Cápsula de Carga Molecular & Logística Portátil Anti-Nesting
* **Cápsula de Carga Molecular (`molecular_cargo_capsule`)**:
  * Alternativa avançada ao Shulker Box, permitindo carregar um inventário portátil de 27 slots no deserto sem depender de acesso ao End.
  * Abertura direta pela mão com clique direito integrando menu persistente via `DataComponents.CONTAINER`.
  * Regra inviolável de segurança anti-nesting: impede a inserção da cápsula dentro de Shulkers, Bundles ou de outras Cápsulas de Carga Molecular (`canFitInsideContainerItems() == false`), além de travar o slot ativo durante o uso.
  * Receita desafiadora combinando ligas de titânio-quitina, biopolímero flexível, caixote de arenito, carcaça de armazenamento, placa de circuito e vedação de pressão.

## [1.19.27] - 2026-10-28

### 📦 Armazenamento Early-Game, Tiers de Fornalha & Refinamento de Sobrevivência
* **Caixote de Arenito (`sandstone_crate`)**:
  * Recipiente de armazenamento acessível para early-game no ermo desértico (sem necessidade de madeira), com capacidade de 54 slots (3.456 itens) e drop seguro de conteúdo.
  * Texturas redesenhadas com cantoneiras de sucata metálica, tampa articulada e alças reforçadas de transporte.
* **Tiers Avançados de Fornalha**:
  * `insulated_furnace` (**Fornalha Isolada**): estrutura refratária envolta em manta acolchoada de lã de vidro (*fiberglass*) com cintas de bronze, queimando 2x mais itens por unidade de combustível; frente equipada com termômetro analógico e visor de inspeção.
  * `steel_furnace` (**Fornalha de Aço**): forno industrial blindado com parafusos estruturais, escotilha de detonação, trava mecânica de latão e manômetro; velocidade de fundição 2x mais rápida (100 ticks), 2x mais eficiência de combustível, resistência a explosões (1200) e partículas de chamas azuladas de plasma (`SOUL_FIRE_FLAME`) com estalos de alto-forno (`BLASTFURNACE_FIRE_CRACKLE`).
* **Qualidade de Vida no Early Game**:
  * Filtro dessalinizador (`desalination_filter`): receita flexibilizada para aceitar balde comum (`minecraft:bucket`) e qualquer arenito via tag `#c:sandstones`.
  * Aqüífero salobro (`brackish_aquifer`): mineração passa a dropar sal mineral (`mineral_salt`) com suporte a Fortuna.
  * Frascos de água: texturas de `brackish_water_bottle` e `potable_water_bottle` alinhadas à silhueta icônica vanilla com rolha e reflexos de vidro.
* **Rede Quântica de Armazenamento**:
  * Receitas data-driven integradas para cartuchos quânticos (1k a 64k), cabos, controlador e terminal de acesso.

## [1.19.26] - 2026-10-26

### 📡 Utilitários Táticos, Armadilhas de Teto & Emboscadas Holográficas
* **Novas Ferramentas e Dispositivos Táticos**:
  * `tactical_detonator` (**Detonador Remoto Tático**): dispositivo portátil que sintoniza e detona remotamente até 64 blocos de distância qualquer mina ou alçapão instalado.
  * `trap_disarmer_kit` (**Kit de Desarme Tático**): ferramenta essencial de campo para sobreviventes desativarem minas, tripwires e armadilhas inimigas sem detoná-las.
  * `mirage_hologram_trap` (**Mina de Camuflagem Holográfica**): gera uma projeção holográfica de androide em movimento que atrai a inteligência artificial dos mobs para longe.
  * `nanite_web_snare` (**Rede Aérea de Contenção Nanítica**): armadilha de teto que detecta alvos por baixo e dispara microcabos naníticos que suspendem e imobilizam a criatura no ar.
  * `neurotoxic_spikes` (**Esteira de Espinhos com Neurotoxina**): esteira com espinhos embebidos em toxinas desérticas que infligem dano perfurante, veneno contínuo e fraqueza.

## [1.19.25] - 2026-10-26

### 🛡️ Arsenal Tático de Defesa & Família de Armadilhas Mecânicas de Chão
* **Novas Armadilhas de Chão e Dispositivos Táticos**:
  * `collapsible_trapdoor` (**Alçapão Basculante Falso**): alçapão basculante nivelado com o solo que sobrescreve `isPathfindable(LAND) = true`, enganando a inteligência artificial dos mobs para que tratem a armadilha como piso sólido e caminhem sobre ela. Ao ser pisado, colapsa instantaneamente abrindo o vão sob a entidade, retornando à posição fechada após 2.5 segundos (50 ticks).
  * `kinetic_ejector_trap` (**Ejetor Cinético de Chão**): placa mecânica pneumática acionada por pistão sob pressão que projeta entidades a 14 blocos de altura com impulso vetorial, causando dano de impacto em tetos/paredes e recarregando em 3 segundos.
  * `thermal_vent_trap` (**Grelha Térmica de Vapor**): respiradouro geotérmico industrial com textura passiva e ativa em ponto de fusão; inflige dano por chão escaldante, incendeia alvos e aplica cegueira e lentidão a androides e jogadores.
  * `serrated_jaw_trap` (**Armadilha de Mandíbula Serrada**): clássica armadilha de dentes de aço móveis; tranca a vítima no local com lentidão severa até ser desarmada com Shift + botão direito ou picareta.
  * `piezo_shock_trap` (**Mina de Choque Piezoelétrica**): mina subterrânea de cristais piezoelétricos que descarrega arcos elétricos e raios em área ao detectar pressão.
* **Novos Projéteis Táticos Arremessáveis**:
  * `stun_grenade` (**Granada de Luz Fotônica**): clarão concussivo que cega jogadores e reseta o alvo de inteligência artificial de androides.
  * `smoke_grenade` (**Granada de Fumaça Aerossol**): dispersão de névoa volumétrica densa que quebra rastreamento visual.
* **Demonstração Interativa no Mapa de Showcase**:
  * Linha dedicada de testes de armadilhas táticas no Perímetro de Defesa (`ShowcaseAutomation`), com fosso de estacas sob o alçapão basculante, baú de suprimentos táticos e manequim especialista na Praça Central.

## [1.19.20] - 2026-10-15

### ❄️ Cofre Criogênico Subglacial (*Subglacial Cryo-Vault*) & Blocos Mecânicos
* **Nova Megadungeon Procedural Subterrânea Multi-Andar**:
  * Gerada nas profundezas congeladas do subsolo desértico (`Y = -48`), apresentando arquitetura hermética com isolamento térmico avançado (`CRYO_INSULATION`), câmaras de estase, compressores de nitrogênio e o cobiçado **Cofre Sub-Zero de Sementes** (*Sub-Zero Seed Vault*).
  * Iluminação 100% atmosférica orgânica por líquen luminoso (`GLOW_LICHEN`), sem uso de tochas ou lanternas convencionais.
* **Novos Blocos com Mecânicas Ativas**:
  * `cryo_sprayer` (**Pulverizador Criogênico**): mecanismo direcional acionado por redstone que projeta jatos de nitrogênio subzero; congela água em gelo compactado, resfria lava em obsidiana, apaga fogo, inflige dano por congelamento, aplica lentidão e acumula hipotermia extrema em entidades vivas.
  * `brittle_permafrost` (**Permafrost Quebradiço**): sedimento de gelo instável que suporta caminhada cuidadosa/agachada; corridas ou impactos pesados fraturam o bloco, quebrando-o em estilhaços sonoros e derrubando invasores em abismos de estacas pontiagudas.
  * `subglacial_ice_spike` (**Espinho de Gelo Subglacial**): estalagmites/estalactites congeladas que causam perfuração letal e hipotermia em qualquer criatura que cair ou pisar sobre elas.
  * `cryostasis_pod` (**Câmara de Estase Criogênica**): dispositivo de estase médica com BlockEntity integrada. Cura 1 HP a cada 10 ticks e expurga veneno/wither, acumulando risco de hipotermia se ocupada por mais de 100 ticks, além de ejeção emergencial via redstone.
  * `cryo_insulation` e `chiseled_cryo_insulation` (**Isolamento Criogênico**): blocos de contenção térmica de alta tecnologia para salas limpas e laboratórios criogênicos.
* **Comandos e Showcase Automation**:
  * Adicionados `/sandstorm build cryo_vault` e `/sandstorm_build_cryo_vault` para construção procedural instantânea.
  * Integração total na vitrine do Showcase (`ShowcaseAutomation`), com plataforma de fundação, teto reforçado e terminal com teleporte rápido na Praça Central.

## [1.19.15] - 2026-10-14

### 🔧 Correções Estruturais nas Dungeons Procedurais & Poços Verticais
* **Eliminação de Correntes Flutuantes (*Dangling Chains*)**:
  * Em `CargoHoistShaft`, as correntes do guincho agora são ancoradas diretamente em vigas de suporte e no motor de guincho industrial (`IRON_BLOCK` e `CHISELED_DEEPSLATE`) no teto da sala.
  * O teto no nível `ly = 5` permanece 100% sólido e fechado, prevenindo aberturas involuntárias para o vácuo ou céu. A corrente desce com carga suspensa (`RADIOACTIVE_WASTE_BARREL`) diretamente sobre o poço inferior.
* **Correção da Sobreposição de Grades de Ferro em Escadas (*Ladder / Iron Bars Overlap*)**:
  * O poço vertical do guincho agora possui um pilar estrutural dedicado de deepslate polido em `(2, ly, 1)`.
  * As escadas de mão (`LADDER`) são instaladas na face sul do pilar sem qualquer interferência de grades de ferro. O guarda-corpo perimétrico foi reajustado para manter a entrada da escada totalmente desobstruída e acessível.
* **Vedação e Blindagem de Tetos nas Megadungeons do Showcase**:
  * `StairwellUp` agora verifica se há pavimentos acima antes de abrir o teto; no pavimento superior (Floor 0), o teto permanece hermeticamente vedado e iluminado com luminária de redstone.
  * Adicionada a rotina `buildRoofCap` em `ShowcaseAutomation`, cobrindo as 4 megadungeons com laje espessa de contenção de concreto armado e concreto milenar.

## [1.19.14] - 2026-10-14

### 🛡️ Metalurgia Pesada, Concreto Armado & Blindagem Radiológica de Chumbo
* **Novos Blocos Ultra-Resistentes para Dungeons e Bunkers Subterrâneos**:
  * `reinforced_concrete` (**Concreto Armado**): concreto de alta densidade estrutural com armação interna de vergalhões de aço. Possui dureza 55.0 e resistência a explosões 1500.0 (superando a obsidiana vanilla!), exigindo picareta de diamante/netherite para extração e sendo imune a explosões convencionais de TNT e mísseis.
  * `weathered_reinforced_concrete` (**Concreto Armado Milenar**): variante envelhecida de concreto armado exposta a milênios de pressão tectônica e corrosão geológica, revelando fissuras e malha de aço oxidada. Dureza 50.0 e resistência 1200.0.
  * `lead_block` (**Bloco de Chumbo**): bloco maciço metálico de chumbo denso com acabamento bisotado e tom cinza-azulado característico.
  * `lead_shielding_block` (**Bloco de Blindagem de Chumbo**): placa reforçada de blindagem radiológica com rebites perimetrais e carcaça de contenção de radiação pesada, utilizada para isolar piscinas de combustível e depósitos de resíduos.
* **Novos Itens Metalúrgicos**:
  * `steel_rebar` (**Vergalhão de Aço**): barras caneladas de aço industrial para armação de estruturas e manufatura de concreto armado.
  * `lead_ingot` (**Lingote de Chumbo**): lingote de metal pesado denso para proteção radiológica e equipamentos de contenção.
  * `raw_lead` (**Chumbo Bruto**): fragmento de galena/minério bruto denso de chumbo encontrado em cofres de depósitos e estratos profundos.
* **Integração Arquitetônica nas Dungeons Procedurais**:
  * `BaseDungeonComponent`: o exoesqueleto estrutural dos complexos agora intercala Concreto Armado e Concreto Armado Milenar em tetos e paredes de contenção.
  * `MillennialRepositoryCatalog`: as piscinas de combustível irradiado (`SpentFuelPoolVault`) e áreas de descarte (`CorrodedWasteDepot`) possuem perímetros blindados com Bloco de Chumbo e Blindagem de Chumbo. Baús de espólio agora contêm Vergalhões de Aço, Lingotes de Chumbo e Chumbo Bruto.
  * `AncientBunkerCatalog`: câmaras de armazenamento de resíduos ganham contenção de blindagem de chumbo e espólio metalúrgico reforçado.
* **Rede Subterrânea do Showcase (`ShowcaseAutomation`)**:
  * O subsolo sob a exibição de blocos foi preenchido com 4 megadungeons colossais (693 salas no total), passarelas de vidro fúlgurita com vista para o abismo, tubos de descida com escadas e teleportadores rápidos.

### ☢️ Depósito Geológico Profundo Multi-Andar & Barris Radioativos Milenares
* **Dungeon Procedural Multi-Andar (`MillennialRepositoryDungeon` / `MillennialRepositoryCatalog`)**:
  * Implementação de gerador procedural de megadungeons verticais com $N$ andares (2 a 6 andares, padrão de 3 andares e salas $7 \times 7$).
  * Inspirado em depósitos geológicos de lixo nuclear milenares (Onkalo/WIPP) abandonados nas profundezas do deserto sob o deepslate.
  * Conexão física garantida entre pavimentos vizinhos via alinhamento exato de coordenadas $(X, Z)$ entre âncoras verticais:
    * `StairwellDown`: escadaria helicoidal em espiral ao redor de pilar central que desce 8 blocos até o piso inferior.
    * `StairwellUp`: poço receptor de escada helicoidal conectando ao nível superior.
    * `CargoHoistShaft`: poço vertical de montacargas de carga ($3 \times 3$) com correntes de ferro (`IRON_CHAIN`), escadas de serviço e grades de contenção perimetrais.
    * `SpentFuelPoolVault`: câmara de contenção primária no andar mais profundo contendo piscina de lodo radioativo, resíduos milenares e cofre com Núcleo de Dados Antigo e compósito de titânio.
    * `SurfaceAccessTerminal`: antecâmara de despressurização e acesso de superfície no topo do complexo.
* **Texturas de Barris Radioativos Envelhecidos e Corroídos**:
  * Renovação completa das texturas de `radioactive_waste_barrel` (`side`, `top`, `bottom`) para refletir milênios de exposição.
  * Adição de oxidação ferrosa realista, manchas de ferrugem, escorrimento químico e desgaste superficial com estêncil de perigo radioativo rasgado e desbotado pelo tempo.
* **Novos Comandos de Construção Rápida**:
  * `/sandstorm_build_multidungeon [andares] [largura] [profundidade] [pos]`: geração instantânea da megadungeon multi-andar com teleporte para a antecâmara do primeiro piso.
  * `/sandstorm_build_multi_floor_dungeon`: atalho direto configurável.
  * Integração de `multi_floor_dungeon` e `millennial_repository` no comando `/sandstorm build` e `/sandstorm structure`.
* **Geração Natural em Camadas Profundas (`ProceduralRuinsManager`)**:
  * Nova raridade `MULTIFLOOR_DUNGEON_RARITY = 220`, gerando complexos entre $Y = -28$ e $Y = -43$ na camada de deepslate.
  * Preservação integral da dungeon térrea pré-existente (`AncientBunkerComplexDungeon`, raridade 160).
  * Execução assíncrona por fila em `ServerTickEvents.END_SERVER_TICK` evitando deadlocks e travamento de threads.

## [1.19.13] - 2026-10-14

### 🔭 Observatório Astronômico & Varredura Procedural de Galáxias em Tempo Real
* **Interface Gráfica do Telescópio de Varredura Espectral (`spectral_survey_telescope`)**:
  * Substituição do envio de texto no chat por uma GUI imersiva com chassi de observatório em ciano (`230x208`) e visor astronômico de espaço profundo (`120x90`).
  * Varredura procedural simulada em tempo real com rotação contínua de campo estelar, cintilação e renderização vetorial de 5 corpos celestes:
    * **Galáxia de Andrômeda**: núcleo elíptico denso com braços espirais em rotação matemática contínua.
    * **Nebulosa do Caranguejo**: filamentos difusos, lóbulos de gás ionizado e pulso radiativo.
    * **Exoplaneta Kepler**: esfera planetária com sombreamento volumétrico e anéis orbitais elípticos inclinados.
    * **Pulsar de Cygnus**: estrela de nêutrons compacta com jatos relativísticos opostos e pulsação periódica.
    * **Corona Solar**: núcleo incandescente com proeminências magnéticas dinâmicas e flare solar.
  * **HUD de Observação Astronômica**:
    * Coordenadas equatoriais celestes dinâmicas (Ascensão Reta $\alpha$ e Declinação $\delta$) calculadas proceduralmente em função do alvo e tempo orbital.
    * Retículo de mira com cantoneiras, anel central de foco e escala métrica espacial.
  * **Sintonia de Filtros Espectrais**:
    * **Banda Óptica / Visível (`OPT`)**: mapeia estrelas visíveis e poeira reflexiva em tons de ciano e dourado.
    * **Banda Infravermelha Térmica (`INF`)**: revela calor latente de exoplanetas e protoestrelas em tons quentes e âmbar.
    * **Banda de Rádio & Raios-X (`RAD`)**: destaca radiações de alta energia de pulsares e remanescentes de supernova em magenta e verde néon.
  * **Sistemas do Bloco & Integração de Energia**:
    * Acumulador interno de 50.000 EU com recarga solar contínua sob o céu aberto (50 EU/tick) e medidor visual segmentado com percentual de carga.
    * Disparo manual de varredura profunda com consumo de 500 EU, avanço percentual e efeitos sonoros e visuais de partículas no mundo.
    * Radar orbital meteorológico acoplado detectando frentes de tempestade de areia e contagem regressiva para impactos no setor.
    * Slot de inserção para dados e documentos ópticos integrado ao inventário do jogador.

## [1.19.12] - 2026-10-14

### ☣️ Salas Radioativas, Perigos Químicos & Sentinelas Defeituosos
* **Líquido de Lixo Radioativo (`radioactive_sludge`)**:
  * Implementação completa de fluido customizado (`FlowingFluid` / `Source` / `Flowing`) e bloco de fluido (`RadioactiveSludgeBlock`).
  * Efeitos imediatos de dano mágico ao tocar ou submergir, aplicando Enjoo (`NAUSEA`), Lentidão (`SLOWNESS`) e Veneno (`POISON`).
  * Item de Balde de Lodo Radioativo (`radioactive_sludge_bucket`) integrado à aba criativa SandStorm e cadeias de renderização completas.
* **Barris de Resíduos Radioativos (`radioactive_waste_barrel`)**:
  * Bloco tecnológico de contenção industrial com BlockEntity ativa (`RadioactiveWasteBarrelBlockEntity`).
  * Emissão de radiação contínua em raio de 4 blocos com partículas de fumaça verde e efeito sonoro de vazamento, aplicando Decomposição (`WITHER`) e Enjoo (`NAUSEA`) a jogadores que permaneçam expostos na vizinhança.
  * Tag de mineração com picareta (`mineable/pickaxe`) e tabela de loot associada.
* **Sentinela Defeituoso (`defective_sentinel`)**:
  * Nova entidade hostil exclusiva de dungeons profundas e oficinas subterrâneas abandonadas.
  * Comportamento errático com espasmos visuais em alta frequência, faíscas elétricas constantes e descarga de choque em área (5 blocos) com desaceleração.
  * Ataque corpo a corpo eletrificado e autodestruição violenta por curto-circuito (explosão com faíscas ao morrer).
  * Renderizador customizado (`DefectiveSentinelRenderer`), modelo tridimensional (`DefectiveSentinelModel`) e ovo de spawn (`defective_sentinel_spawn_egg`).
* **Novas Variações Criativas de Salas WFC (`AncientBunkerCatalog`)**:
  * `SludgeCorridor` (`bunker_sludge_corridor`): canal central inundado de lodo radioativo com pedras de salto para parkour e barreiras de ferro laterais.
  * `WasteStorageDepot` (`bunker_waste_storage_depot`): depósito de barris de resíduos empilhados, poças de vazamento radioativo e baú de suprimentos com abertura voltada para o corredor.
  * `DefectiveSentinelWorkshop` (`bunker_sentinel_workshop`): oficina abandonada com sentinelas defeituosos patrulhando entre bancadas, peças de tecnologia enterrada e bigornas.
* **Geração Natural de Megadungeons em Grandes Profundidades**:
  * Integração ao `ProceduralRuinsManager` gerando complexos subterrâneos entre `Y = -52` e `Y = -20` na camada de deepslate.
  * Variação procedural de dimensões de grade (3x3 até 6x6, totalizando de 9 a 36 módulos conectados via WFC por dungeon).
  * Execução assíncrona desacoplada em fila `END_SERVER_TICK` prevenindo deadlocks no carregamento de chunks.
* **Teto Rígido de Densidade para Sentinelas por m²**:
  * Implementação de verificador de densidade volumétrica (`DENSITY_RADIUS_TIGHT = 8.0` máx 2, `DENSITY_RADIUS_WIDE = 16.0` máx 3) em `DefectiveSentinelEntity` e `ScrapSentinelEntity`.
  * Verificação preemptiva em `canSpawnInArea` e expurgo de segurança no tick (`enforceDensityCap`) para impedir acúmulo excessivo e quedas de FPS em qualquer hardware.
* **Correção da Porta de Liga Morfoenergética & Hologramas do Showcase**:
  * Registro de portas convertido para `DoubleHighBlockItem`, garantindo posicionamento atômico das metades inferior (`LOWER`) e superior (`UPPER`).
  * Posicionamento programático de ambas as metades da porta nos pedestais de `ShowcaseAutomation` e `ExhibitionGalleryManager`.
  * Elevação do texto dos hologramas de porta para `Y + 2.45` e `Y + 2.15`, posicionando-o limpo acima da porta de 2 blocos.
  * Hologramas convertidos com `setNoBasePlate(true)` e flag `CLIENT_FLAG_MARKER`, removendo contornos de hitbox, placa de chão e interferência na mira do jogador.
  * Testes de arquitetura dedicados `DoorBlockstateArchitectureTest` e `SentinelDensityArchitectureTest` para prevenir regressões.


## [1.19.11] - 2026-10-14

### 🏛️ Espaçamento Dinâmico no Showcase & Bunker Subterrâneo
* **Espaçamento Dinâmico do Complexo de Biosferas**:
  * Implementado cálculo procedural e dinâmico (`getBiosphereFrontZ()`) que afasta as 7 biosferas automaticamente com base na quantidade real de blocos e baús registrados no mod.
  * Resolução definitiva do conflito onde os domos (especialmente o Domo Xeno-Fúngico) cortavam pedestais de blocos e paredes do mostruário.
  * Abertura de corredor central desimpedido no mostruário de blocos (`X = -2..2`), posicionando colunas de pedestais organizadamente em ambos os lados (`X = -18..-6` e `X = 6..18`).
  * Deslocamento das alas de baús para os flancos externos (`X = -24` e `X = 24`), garantindo avenidas amplas de circulação.
  * Otimização da limpeza de área (`wipeBiospheresArea`) para operar estritamente na zona pós-mostruário, preservando todos os blocos expostos.
* **Geração e Comandos do Bunker Antigo**:
  * Adicionado suporte a posicionamento na localização do jogador (`@s`, entidade do jogador ou padrão sem argumentos).
  * Reposicionamento seguro na entrada do bunker `(0, 1, -4)` para prevenir sufocamento dentro do pedestal do Núcleo de Dados Antigo.
  * Registro de novos atalhos `/sandstorm_build_bunker` e `/sandstorm_build_ancient_bunker`.
* **Engine Procedural de Dungeons Modulares (WFC)**:
  * Criação do sistema modular de componentes com conectores (*sockets* direcionais: `WALL`, `CORRIDOR`, `DOOR`, `SOLID`) e álgebra de rotações discretas 2D/3D.
  * Implementação do algoritmo Wave Function Collapse (`WaveFunctionCollapseSolver`) com heurística de menor entropia, propagação de restrições em cascata (AC-3), suporte a âncoras fixas e salas únicas.
  * Catálogo de 10 módulos subterrâneos (`AncientBunkerCatalog`): câmara de entrada, corredores retos, esquinas em L, entroncamentos em T, encruzilhadas 4-way, becos com baús, cofre do Núcleo de Dados, sala de acumuladores de energia, arquivo de servidores técnicos e rocha sólida de preenchimento.
  * Passagem de acabamento estético (`AncientBunkerClutterPass`) com desgaste de tijolos rachados, teias de aranha e cristais de quartzo piezoelétrico.
* **Correção Visual da Porta de Liga Morfogenética (`morphing_alloy_door`)**:
  * Implementação dos 8 modelos canônicos do sistema de portas do Minecraft 1.21.4 (`bottom_left`, `bottom_left_open`, `bottom_right`, `bottom_right_open`, `top_left`, `top_left_open`, `top_right`, `top_right_open`).
  * Reestruturação completa do blockstate com as 32 combinações de rotação Y e modelos correspondentes ao padrão do `iron_door` vanilla, eliminando a discrepância ortogonal de 90° onde o modelo 3D ficava desconectado da hitbox/bounding box do bloco.
  * Criação do teste de arquitetura dedicado `DoorBlockstateArchitectureTest` para prevenir regressões nos estados e modelos de portas.
* **Ativação e Iluminação das Lâmpadas de Redstone nas Dungeons**:
  * Correção do estado de inicialização das lâmpadas de redstone (`Blocks.REDSTONE_LAMP`), que anteriormente permaneciam apagadas (`lit=false`) por serem instanciadas com flag 2 sem notificação de vizinhos (`UPDATE_NEIGHBORS`).
  * Atualização de `AncientBunkerCatalog` e `AncientBunkerGenerator` para definir `BlockStateProperties.LIT = true` e aplicar flag 3 (`Block.UPDATE_ALL`) no bloco de redstone e lâmpada, garantindo propagação imediata de sinal e emissão de luz nível 15 através dos claraboias de vidro fulgurito.
  * Criação do teste de arquitetura `AncientBunkerLightingArchitectureTest` para auditar e garantir a ativação correta de fontes de redstone e iluminação.
* **Orientação Automática de Baús em Dungeons e Estruturas**:
  * Adicionado algoritmo de rotação direcional em `DungeonRotation.rotateDirection(Direction)` e detecção de face desimpedida (`determineOpenFacing`) em `BaseDungeonComponent`.
  * Todos os baús em salas de dungeon (`AncientBunkerCatalog.DeadEnd`, `AncientBunkerGenerator`, `AbandonedOutpostGenerator` e comandos de laboratórios/bunkers) agora orientam seu fecho frontal (`ChestBlock.FACING`) em direção ao interior e às entradas das salas, nunca virados de costas para o jogador ou contra paredes sólidas.
  * Criado o teste unitário e de arquitetura `DungeonChestOrientationTest` para auditar a rotação das faces e validar a ausência de baús desorientados.
* **Componente de Estrutura NBT (`NbtDungeonComponent`) e Comandos de Megadungeon**:
  * Integração nativa com templates `.nbt` do Minecraft (`StructureTemplateManager`) mapeando rotações discretas (`Rotation.NONE`, `CLOCKWISE_90`, `CLOCKWISE_180`, `COUNTERCLOCKWISE_90`) com fallback procedural.
  * Expansão dos comandos `/sandstorm_build_dungeon` e `/sandstorm build procedural_dungeon` para suportar dimensões customizadas de grade (ex.: 5x5 = 25 salas, 7x7 = 49 salas).

---

## [1.14.0] - 2026-10-07

### 🌿 Showcase de Biosferas Expandidas
* Adicionada uma segunda ala com biosferas temperada, florestal e tropical.
* Cada domo recebe pisos, água, vegetação e fauna próprios.
* A vegetação do showcase valida o bloco de suporte antes de ser colocada.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/)
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.10.2] - 2026-10-06

### 🔧 Correção de Ancoragem das Articulações & Emissão de Luz dos Golens Cibernéticos
* **Ancoragem Geométrica das Articulações Incandescentes**:
  * Implementação de `CyberneticGolemHeatLayer` como camada dedicada `RenderLayer` renderizada dentro da matriz de transformações ativa dos membros do modelo.
  * Remoção do envio manual posterior a `super.submit()` que causava a renderização flutuante e deslocada do corpo do mob no espaço global.
  * Regeneração com precisão matemática das coordenadas UV de `cybernetic_golem_heat_glow.png` nos limites exatos dos cotovelos, joelhos, peito e olhos da malha 128x128.
* **Origem Confinada de Faíscas & Emissão de Luz Ambiente**:
  * As partículas elétricas de overdrive (`ELECTRIC_SPARK`) agora têm emissão estritamente restrita aos pontos de junta articular no chassi físico dos golens (raio curto de 0.35 a 0.5 blocos e velocidade nula), eliminando faíscas espalhadas no chão ou no ar vazio.
  * Os golens cibernéticos em modo Overdrive ou aquecidos passam a emitir nível de luz em bloco 9 (`getBlockLightLevel`) na renderização e ambiente.
* **Auditoria Contínua com Testes de Arquitetura**:
  * Expandido `CyberneticGolemVisualIntegrityArchitectureTest` para auditar a existência de `CyberneticGolemHeatLayer`, seu registro no renderer, a sobreposição de `getBlockLightLevel` e o confinamento das partículas aos pontos de articulação.

---

## [1.10.1] - 2026-10-06

### 🚀 Flag de Boost Permanente nos Comandos & Testes de Arquitetura de Texturas
* **Flag de Overdrive Permanente nos Comandos de Debug**:
  * Adicionado suporte ao argumento booleano `permanentBoost` nos comandos `/sandstorm golem <tier> [permanentBoost]` e `/sandstorm_golem <tier> [permanentBoost]`.
  * Novos atalhos no auto-completar: `/sandstorm golem all_boost` e `/sandstorm golem all_overdrive`, além de suporte ao comando `/sandstorm golem boost true` para ativar overdrive perpétuo nos golens próximos.
  * Suporte nativo em `CyberneticGolemEntity`: flag e persistência NBT `PermanentOverdrive`, mantendo partículas elétricas, propulsores e brilho emissivo do reator sem resfriamento.
* **Teste de Arquitetura Dedicado para Preservação Visual de Texturas**:
  * Criado `CyberneticGolemVisualIntegrityArchitectureTest` garantindo que nenhuma das 5 texturas metálicas (128x128), a camada de brilho térmico (`heat_glow.png`) e os blocos/itens da cabeça de autômato possam ser deletados, corrompidos ou terem resolução alterada em atualizações futuras.

---

## [1.10.0] - 2026-10-06

### 🤖 Chamado Remoto & Companheirismo Tático dos Golens Cibernéticos
* **Sistema de Chamado Remoto via Tecla (`V` remapeável)**:
  * Adicionada a tecla dedicada `key.sandstorm.call_golem` (padrão `V`, remapeável no menu de controles).
  * Envia pacote de rede C2S `CallGolemPayload` acionando o manipulador `CyberneticGolemRecallHandler`, que comanda todos os golens cibernéticos ativos do jogador carregados no mundo para se deslocarem até sua posição.
* **Navegação Inteligente e Distância de Respeito**:
  * Alcance de percepção e perseguição estendido para 128 blocos (`FOLLOW_RANGE = 128.0`).
  * Deslocamento estritamente terrestre e cinemático (sem teleporte mágico vanilla).
  * Distância de parada de 4.5 blocos e início de perseguição aos 6.5 blocos, garantindo que o golem nunca colida nem fique "em cima" do jogador.
  * Aumento dinâmico de velocidade de perseguição quando o jogador corre em arrancada (`sprint`).
  * Ativação automática de Overdrive com propulsores se estiver a mais de 20 blocos ou sob comando de recall remoto.
* **Defesa Proativa & Priorização de Androides**:
  * Goal `CyberneticDefendOwnerGoal` que detecta instantaneamente agressores ou alvos hostis mirando no jogador.
  * Prioridade absoluta de retaliação e ativação imediata de Overdrive ao combater Androides.
* **Postura Tática e Animação Correta das Armas**:
  * Rifles e canhões (`PlasmaRifleItem`, `SonicCannonItem`, `HeavyPlasmaCannonItem` e `EmpBlasterItem`) agora utilizam `ItemUseAnimation.CROSSBOW`, corrigindo a pose errônea de ferramenta/picareta.
  * Transformações de modelo (`display`) em 3ª pessoa (`thirdperson_righthand`) e 1ª pessoa (`firstperson_righthand`) alinhadas horizontalmente para a frente como armas de fogo reais.

---

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
