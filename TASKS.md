# SandStorm - Quadro de Tarefas & Mensuração do MVP

Este documento consolida o andamento das fases de desenvolvimento do mod **SandStorm** para **Minecraft 26.3 (Fabric / Java 25)**, detalhando o que já foi concluído, as métricas de qualidade e o que resta para o fechamento do MVP.

---

## 📊 Métricas Gerais do Projeto

| Métrica | Valor Atual | Meta MVP | Status |
| :--- | :--- | :--- | :--- |
| **Versão Alvo** | Minecraft 26.3 | Minecraft 26.3 | ✅ Atingido |
| **Fabric Loader / API** | 0.19.5 / 0.160.7+26.3 | Compatibilidade Estável | ✅ Atingido |
| **Java SDK** | Java 25 | Java 25 | ✅ Atingido |
| **Testes Automatizados** | **571 testes** (0 falhas) | > 150 testes | ✅ Superado |
| **Rigor de Código (Zero Comentários)** | **0 linhas de comentários** | 0 linhas | ✅ 100% Auditado |
| **Arquitetura Desacoplada** | 100% isolamento de componentes | Zero acoplamento | ✅ Validado |
| **Receitas Data-Driven (JEI/REI)** | 62 receitas oficiais | Cobertura total | ✅ Concluído |
| **Geração de Mundo (Worldgen)** | Aquíferos, Ruínas e Núcleos em desertos | Totalmente Integrado | ✅ Concluído |
| **Progresso Estimado do MVP** | **100%** | **100%** | ✅ Concluído |

---

## 🗺️ Roadmap de Fases e Status

### Fase 1: Arquitetura Base, Sobrevivência e Mecânicas Centrais (Concluída - 100%)
- [x] Criação do projeto Fabric Gradle configurado para Minecraft 26.3 e Java 25.
- [x] Arquitetura desacoplada de componentes ECS (`EnergyStorageComponent`, `ThermalComponent`, `SuitPowerComponent`, `SeismicTrackerComponent`, etc.).
- [x] Física de temperatura desértica: calor extremo diurno (48°C) e frio noturno/subterrâneo (22°C).
- [x] Sistema de traje espacial com proteção contra tempestades de areia, perda de oxigênio/energia e recarga solar dinâmica.
- [x] Ciclo de água: Água salobra (`BrackishWaterBlock`, `BrackishWaterBottleItem`), dessalinização (`DesalinationFilterBlock`), água potável (`PotableWaterBottleItem`) e sal mineral.
- [x] Supressão de monstros vanilla (`VanillaMonsterSuppressionHandler`) para preservar a imersão de planeta alienígena hostil.
- [x] Restrição tecnológica de ferramentas (`TechnologyToolRestrictionHandler`):
  - [x] Proibição de picaretas, machados, espadas e enxadas vanilla manuais.
  - [x] Mineração de pedra/minérios restrita a robôs (`ExcavatorVehicleEntity`, `MegazordEntity`) ou à ferramenta tecnológica básica (`SiliconPickaxeItem` / `silicon_pickaxe`).
  - [x] Progressão do Capítulo 1: Fornalha de arenito -> Base estrutural na bancada -> Matriz elétrica na Impressora 3D da cabine -> Picareta de Silício -> Mineração de Carvão fóssil.
  - [x] Permissão explícita de pás vanilla para movimentação e coleta de areia para a nave.
  - [x] Desativação das 24 receitas de ferramentas vanilla no `RecipeManager`.

### Fase 2: Automação, Energia e Robótica (Concluída - 100%)
- [x] Processamento de silício: Minério bruto (`raw_silicon`), pastilha de silício (`silicon_wafer`), placa de circuito (`circuit_board`) e nanoatuadores (`nano_actuator`).
- [x] Impressora 3D de bancada (`Printer3DBlock`) com processamento via energia.
- [x] Fabricador de nanitas (`NaniteFabricatorBlock`) para criação de componentes avançados.
- [x] Receptor de Energia Solar Sem Fio (`WirelessSolarReceiverBlock` Tier 1 e Tier 2) para transmissão esférica WPT e recarga de traje na nave e postos avançados.
- [x] Hangar de montagem (`AssemblyBayBlock`) e estação de drones (`DroneDockBlock`).
- [x] Drone logístico de carga (`CargoDroneEntity`).
- [x] Veículo escavador tripulável (`ExcavatorVehicleEntity`) com bateria de 50.000 FE/kJ e capacidade de escavação pesada.
- [x] Mecha de combate titan (`MegazordEntity`) com sistema de blindagem e absorção de impacto.

### Fase 3: Ameaça Sísmica, Thumper e Canhão Sônico (Concluída - 100%)
- [x] Entidade colossal do Verme de Areia (`SandwormEntity`) com detecção sísmica.
- [x] Dispositivo batedor (`ThumperBlock` / `VibrationEmitterComponent`) para atrair vermes ou desviar atenção de bases.
- [x] Canhão sônico portátil (`SonicCannonItem`) com disparo de onda de choque sônica e recuo cinético.
- [x] Radar de anomalias (`AnomalyRadarItem`) e analisador atmosférico (`AtmosphericAnalyzerItem`).
- [x] Drops biotecnológicos: Quitina de verme (`sandworm_chitin`) e dente de verme (`sandworm_tooth`).

### Fase 4: Terraformação Setorial em Cúpula (Concluída - 100%)
- [x] Terraformador atmosférico setorial (`AtmosphericTerraformerBlock`).
- [x] Índice de terraformação local (`TerraformingIndexComponent`).
- [x] Lógica de mundo infinito: cúpula com raio dinâmico `getDomeRadius()`, sem contadores globais arbitrários.
- [x] Conversão de blocos estéreis (areia, arenito) em biomas úmidos e grama dentro do perímetro da máquina.
- [x] Bomba de Nutrientes Arremessável (`NutrientBombItem` / `NutrientBombEntity`) com ciclo temporal de enriquecimento biológico do solo (`NutrientTerraformingManager`: areia -> terra -> grama viva -> vegetação nativa).

### Fase 5: Integração JEI/REI/EMI e Testes Automatizados (Concluída - 100%)
- [x] 45 arquivos JSON de receitas data-driven em `data/sandstorm/recipe/` (smelting, blasting, shaped, shapeless).
- [x] 24 arquivos JSON em `data/minecraft/recipe/` anulando ferramentas vanilla via `fabric:load_conditions`.
- [x] `SandStormItemsTest`: Validação de chaves de itens e namespace.
- [x] `SandStormBlocksTest`: Validação de chaves de blocos e propriedades.
- [x] `SpaceSuitItemTest`: Validação de durabilidade de armadura e raridade.
- [x] `SuitSurvivalHandlerTest`: Validação do ciclo de vida dos componentes de jogadores.
- [x] `TechnologyToolRestrictionHandlerTest`: Validação da exclusividade robótica e permissão de pás.
- [x] `AssetIntegrityTest`: Validação sintática de modelos JSON e cabeçalhos de texturas PNG.
- [x] `JeiCompatibilityTest`: Validação estrutural de compatibilidade do JEI/REI.
- [x] `ZeroCommentsArchitectureTest`: Garantia contínua de zero comentários em todo o código Java.

### Fase 6: Geração de Mundo / Worldgen (Concluída - 100%)
- [x] Configuração de aquíferos subterrâneos salobros nos desertos (`sandstorm:brackish_aquifer`):
  - Worldgen feature: `data/sandstorm/worldgen/feature/brackish_aquifer.json`
  - Placed feature: `data/sandstorm/worldgen/placed_feature/brackish_aquifer.json` (Y: 25 a 60).
- [x] Configuração de ruínas tecnológicas soterradas nos desertos (`sandstorm:buried_tech_ruins`):
  - Worldgen feature: `data/sandstorm/worldgen/feature/buried_tech_ruins.json`
  - Placed feature: `data/sandstorm/worldgen/placed_feature/buried_tech_ruins.json` (Y: 48 a 72).
- [x] Configuração de núcleos de dados ancestrais (`sandstorm:ancient_data_core`):
  - Worldgen feature: `data/sandstorm/worldgen/feature/ancient_data_core.json`
  - Placed feature: `data/sandstorm/worldgen/placed_feature/ancient_data_core.json` (Y: 45 a 68).
- [x] Eliminação completa de oceanos superficiais e fluidos acima do solo:
  - Override de noise settings: `data/minecraft/worldgen/noise_settings/overworld.json` (`sea_level: -64`, `default_fluid: "minecraft:air"`).
  - Override de bioma: `data/minecraft/worldgen/biome/desert.json` (remoção de `desert_well`, `spring_water` e `underwater_magma`).
  - Água restrita unicamente a aquíferos subterrâneos salobros (`sandstorm:brackish_aquifer`), acessíveis apenas por escavação e purificação com filtro de dessalinização.
- [x] Handler de injeção em biomas via Fabric Biome API (`SandStormWorldGen.java`).
- [x] Testes automatizados de Worldgen (`SandStormWorldGenTest.java`).

### Fase 7: Infraestrutura Inicial, Sobrevivência em Arenito e Mobilidade (Concluída - 100%)
- [x] **Bancada de Trabalho de Arenito (`sandstone_workbench`)**:
  - [x] Bloco 3x3 funcional para fabricação manual primária sem madeira.
  - [x] Texturas exclusivas e alinhadas ao arenito clássico do deserto.
  - [x] Drop garantido ao ser quebrada com a mão livre ou ferramentas.
- [x] **Fornalha de Arenito (`sandstone_furnace`)**:
  - [x] Bloco e entidade funcional com suporte a fundição e queima de combustíveis.
  - [x] Estados horizontal (`facing`) e aceso (`lit`), com emissão de luz 13, partículas e som de brasas.
  - [x] Integração com Fabric Transfer API (`ItemStorage.SIDED`) para automação de inventário.
  - [x] Drop garantido com a mão livre ou ferramentas.
  - [x] Receita oficial com 8 blocos de arenito (`sandstone_furnace.json`).
- [x] **Caminhos de Rocha Sólida (Safe Paths)**:
  - [x] Criação da tag de blocos sísmicos seguros (`seismic_safe_blocks.json`), incluindo rochas naturais, arenito, bancada e fornalha de arenito.
  - [x] Jogadores andando sobre esses blocos não propagam vibrações detectáveis pelos vermes de areia.
- [x] **Condensador Noturno de Orvalho (`dew_condenser`)**:
  - [x] Coleta de umidade atmosférica durante a madrugada congelante do deserto.
  - [x] Produção passiva de água potável limpa pela manhã.
- [x] **Prancha de Areia (Sandboard - `sandboard`)**:
  - [x] Veículo portátil equipado nos pés para surfar descendo dunas de areia.
  - [x] Física de aceleração por inclinação topográfica de dunas.
- [x] **Saneamento de Receitas Básicas**:
  - [x] Remoção da receita inconsistente de arenito para graveto de madeira.
  - [x] Manutenção da reciclagem de gravetos/hastes metálicas a partir de sucata (`stick_from_scrap_metal.json`).
- [x] **Controles e Ergonomia**:
  - [x] Remapeamento do atalho da lanterna do capacete para a tecla **G** (`InputConstants.KEY_G`), evitando conflito com a tecla F (troca de mão secundária).

### Fase 8: Voo a Jato, Propelente e Refino Químico (Concluída - 100%)
- [x] **Módulo de Voo a Jato do Traje (`suit_upgrade_jetpack`)**:
  - [x] Instalação por clique direito no traje fundido.
  - [x] Voo livre de sobrevivência com aceleração vetorial via corrida (`sprint boost`).
  - [x] Efeitos de partículas de propulsão e som dedicado.
  - [x] Protocolo de descida segura com Queda Lenta (`Slow Falling`) preventiva ao esgotar combustível.
- [x] **Cartuchos de Combustível**:
  - [x] Cartucho Vazio (`empty_cartridge`) forjado com sucata metálica e silício.
  - [x] Cartucho de Propelente de Alta Pressão (`propellant_cartridge`) com 60s de autonomia de voo por unidade.
  - [x] Consumo automático em tempo real no inventário com devolução do cartucho vazio.
- [x] **Refinaria Química Industrial (`chemical_refinery`)**:
  - [x] Bloco e entidade multislot com estética industrial dark-tech correspondente às outras máquinas.
  - [x] Reação química entre sal mineral, água, cartuchos vazios e energia elétrica/WPT para síntese de propelente.
  - [x] Tela procedural com interface gráfica holográfica (`ChemicalRefineryScreen.java`).
- [x] **Módulos de Upgrade do Traje Espacial (`suit_upgrade_*`)**:
  - [x] `suit_upgrade_battery`: Expansão da capacidade da bateria do traje.
  - [x] `suit_upgrade_thermal`: Blindagem contra extremos térmicos diurnos e noturnos.
  - [x] `suit_upgrade_seismic`: Redução de pegada de vibração sísmica.
  - [x] `suit_upgrade_visor`: Otimização ótica com redução do consumo da lanterna.

---

## 🚀 Próximas Tarefas & Backlog de Expansão (Novas Tasks)

### 🌪️ Fase 9: Fenômenos Climáticos Extremos & Exploração Avançada (Concluída - 100%)
- [x] **Tempestades Elétricas de Areia (Ion Sandstorms)**:
  - [x] Descargas elétricas e arcos ionizantes durante o pico das tempestades de areia (`intensity >= 0.75`).
  - [x] Formação de fulgurito (areia vitrificada em blocos de vidro com descargas elétricas).
  - [x] Partículas de faíscas elétricas e ion dust ciano suspensas no ar da tempestade.
  - [x] Interferência eletromagnética em bússolas (giro errático da agulha durante tempestades iônicas).
  - [x] Interferência e corrupção de telemetria no radar de anomalias com ruído estático e sons de burnout.
- [x] **Efeito Térmico de Miragem e Ondas de Calor**:
  - [x] Distorção ótica no horizonte em temperaturas elevadas / extremos diurnos com ondulação de neblina e cintilação de calor no HUD.
  - [x] Falsos reflexos de poças de água que evaporam em poofs de vapor ao aproximar-se (< 11 blocos).
  - [x] Aparições fantasmagóricas de ruínas distantes que se dissolvem em poeira arenosa ao aproximar-se (< 15 blocos).
- [x] **Novas Estruturas Procedurais de Ruínas**:
  - [x] *Outposts de Colonização Abandonados*: Instalações modulares semi-soterradas (11x11) contendo terminais de dados (`ancient_data_core`), maquinários industriais (`buried_tech_ruins`, `printer_3d`), dunas invasoras e baús com peças tecnológicas e upgrades de traje.
  - [x] *Silos de Combustível Clandestinos*: Tanques e bunkers subterrâneos (Y: 30-44) com chaminé de ventilação na superfície, tubulações de fluidos (`fluid_pipe`), refinaria química (`chemical_refinery`), cartuchos de propelente estocados e reagentes químicos.
  - [x] Detecção expandida no Radar de Anomalias para localizar postos avançados e silos de combustível.

### 🛡️ Fase 10: Biologia Alienígena Hostil, Ciclo de Vida do Verme (Estilo Duna) & Defesas de Base
- [ ] **Ciclo Biológico Completo do Verme da Areia (Metamorfose Ecológica de Duna)**:
  - [ ] **Estágio 1: Truta da Areia (Sandtrout / Little Makers - `sandtrout`)**:
    - Pequena criatura bio-anfíbia coriácea que rasteja sob dunas profundas e ruínas areníticas.
    - **Seqüestro Ativo de Umidade (Desertificação)**: Atrai-se instintivamente por qualquer fonte de água líquida (garrafas quebradas, condensadores de orvalho desprotegidos, poças de aquíferos) e a absorve imediatamente, aprisionando a umidade em tecidos biológicos selados para proteger os vermes adultos da água livre letal.
    - **Captura & Biomembrana Impermeabilizante**: Pode ser coletada manualmente com o traje espacial em frascos herméticos (`sandtrout_capsule`), servindo como membrana biológica impermeabilizante de alta tecnologia na confecção de trajes e eclusas.
    - Morte por saturação: Se exposta a excesso de água sem conseguir escapar, sofre lise celular liberando a massa precursora que enriquece os aquíferos salobros (`brackish_aquifer`).
  - [ ] **Estágio 2: Larva / Ninfa da Areia (Sandworm Larva / Brood - `sandworm_larva`)**:
    - Agrupamento metamórfico de trutas da areia que dá origem a larvas vermiformes de 2 a 4 blocos de comprimento.
    - **Ninhos em Cavernas & Ruínas Subterrâneas (`sandworm_nest`)**: Habitam fendas profundas de arenito e galerias escuras, protegendo depósitos de silício fóssil e núcleos tecnológicos soterrados.
    - **Comportamento de Enxame Predatório (Pack Hunting AI)**: Comunicam-se por estalos sísmicos de alta frequência; se o jogador fizer barulho ao minerar ou quebrar blocos por perto, emergem rapidamente da areia solta em bandos de 3 a 6 indivíduos rápidos e vorazes.
    - Drops: Quitina tenra de larva (`soft_chitin_plate`) e fluido biliar corrosivo (`larval_bile`).
  - [ ] **Estágio 3: Verme Sub-Adulto / Caçador de Dunas (Juvenile Sandworm - `juvenile_sandworm`)**:
    - Espécime em fase intermediária de crescimento (~12 a 18 blocos de comprimento).
    - Patrulha as bordas e dunas superficiais em velocidade moderada, atacando alvos que se desloquem fora de blocos seguros de rocha.
    - Menor resistência que o titã adulto: Pode ser repelido por 1 a 2 disparos certeiros do Canhão Sônico (`sonic_cannon`) ou pela Torreta Sônica (`autonomous_sonic_turret`).
    - **Ritual da Água da Vida (Water of Life Extraction)**:
      - Ao ser afogado intencionalmente pelo jogador com água potável pura em uma câmara de contenção hidropônica ou selada, o verme jovem sofre espasmo metabólico final e secreta a **Água da Vida (`water_of_life_vial`)**.
      - Substância catalisadora de extrema potência biológica e periculosidade: ao ser consumida com traje protegido ou analisada no Datapad, desbloqueia instantaneamente a árvore oculta de telemetria ancestral e receitas tecnológicas arcanas.
  - [x] **Estágio 4: Verme Adulto Colossal / Shai-Hulud (`sandworm`)**:
    - O predador ápice titânico de 64+ blocos já implementado no mod (`SandwormEntity`), com movimentação de deslocamento de dunas, mandíbulas de placas quádruplas e destruição sísmica em massa.
  - [ ] **Motor Ecológico de Metamorfose (`SandwormLifecycleManager`)**:
    - Simulação de progressão ontogenética no mundo: agregação de trutas -> emergência de ninhos de larvas -> maturação para caçadores juvenis -> migração definitiva para as profundezas do grande deserto árido como vermes colossais.
- [x] **Torreta Sônica Automatizada (Autonomous Sonic Turret - `autonomous_sonic_turret`)**:
  - [x] Bloco industrial com rotação direcional e carcaça pesada de titânio escuro (`#121820`), emissores de onda sônica duplos e cúpula de sensores óticos.
  - [x] Integração total com malha de energia sem fio (WPT) e slot de bateria auxiliar, consumindo 400 J por pulso acústico num raio de 20 blocos.
  - [x] Disparo acústico repelente (`SandStormSoundEvents.SONIC_CANNON_BLAST`, ondas de choque `SonicBlastVisualEffect`) causando 16 de dano e repulsão física em alvos válidos.
  - [x] Matriz de I.A. avançada configurável através de HUD em tela cheia (`AutonomousSonicTurretScreen`), com barra de pesquisa em tempo real e catálogo de cards para cada mob e monstro do jogo.
  - [x] Modos de filtragem selecionáveis: Alvos Ativos (Whitelist) vs Ignorados (Blacklist).
  - [x] Estratégias de mira inteligentes: Foco no Mais Próximo, Menor Vida e Maior Ameaça, com ações rápidas para seleção de todos os monstros e limpeza de alvos.
  - [x] Sincronização cliente-servidor instantânea via pacote de rede `ConfigureTurretPayload`.
- [ ] **Sinalizador Sísmico de Quarentena (Seismic Beacon)**:
  - Bloco de alta tecnologia que emite pulso de frequência nula, criando uma bolha sísmica neutra onde vermes não entram.

### 🎮 Fase 11: Polimento Audiovisual, Qualidade de Vida & Publicação
- [ ] **Áudio Dinâmico para a Prancha de Areia (Sandboard)**:
  - Som contínuo de areia fofa deslizando sob a prancha, variando de tom e volume com a velocidade.
- [x] **Efeitos Visuais de Deslizamento do Sandboard**:
  - [x] Emissão de partículas de areia do terreno sob a prancha durante deslizamentos em velocidade (`SandboardEntity.tick()`).
- [x] **Redesign Visual do Traje Espacial (`SpaceSuitArmorModel`)**:
  - [x] Modelo 3D customizado dark-tech (`#121820`) com ombreiras reforçadas, tanques dorsais duplos de O2 e placas blindadas.
  - [x] Visor holográfico com camada de brilho emissivo ciano (`#00E5FF`) utilizando `RenderTypes.eyes`.
  - [x] Ícones 2D de itens redesenhados em pixel art de alta fidelidade para capacete, peitoral, calças e botas.
  - [x] Renderizador acoplado via `ArmorRenderer` com renderização dinâmica por slot de equipamento.
  - [x] Conformidade de hierarquia `HumanoidModel` (subpartes `head/hat`, `body`, `right_arm`, `left_arm`, `right_leg`, `left_leg`) e proteção contra crashes de inicialização.
- [ ] **Guia Integrado no Datapad com Diagramas de Maquinário**:
  - Aba de esquemáticos e manuais holográficos de montagem no Datapad.
- [x] **Compatibilidade Oficial com Otimizadores Gráficos**:
  - [x] Validação estrita de compatibilidade com Sodium, Iris Shaders e Lithium na versão 26.3.
  - [x] Metadados oficiais de compatibilidade via bloco `"suggests"` no `fabric.mod.json`.
  - [x] Priorização de mixins (`priority = 1050`) em `SandstormFogMixin` e `FlashlightLightMixin` para coexistência harmoniosa com shaders externos e pipelines de iluminação.
  - [x] Modelos de blocos dinâmicos (`Printer3D`, `NaniteFabricator`, `DesalinationFilter`) 100% migrados para `SubmitNodeCollector` sem vazamentos de estado OpenGL.
  - [x] Teste arquitetural formal `GraphicOptimizersCompatibilityArchitectureTest.java` com 100% de sucesso.

### 🌿 Fase 12: Xenobotânica, Agricultura Hidropônica & Ciclos de Carbono
- [x] **Câmara Hidropônica Pressurizada (`hydroponic_chamber`)**:
  - [x] Módulo selado com iluminação bio-UV e injeção de água dessalinizada e sais minerais.
  - [x] Aceleração controlada de culturas vegetais dentro do raio de proteção da cúpula de terraformação (boost 3x de velocidade e rendimento).
  - [x] Renderizador 3D dedicado com mudas animadas e integração total com malha de energia sem fio (WPT).
- [x] **Sementes de Grama Xeno-Adaptada (`xeno_grass_seeds`)**:
  - [x] Espécies xerófilas geneticamente aprimoradas para fixação de nitrogênio e conversão acelerada de areia em solo arável fértil (`Blocks.DIRT`).
  - [x] Propagação vegetal biológica com resistência aos ventos abrasivos e colonização progressiva de dunas.
- [x] **Cactos de Seiva Pesada (`heavy_sap_cactus`)**:
  - [x] Nova flora desértica resistente à radiação solar extrema, acumulando seiva viscosa sob incidência solar direta (`SAP_LEVEL` 0 a 3).
  - [x] Extração de biopolímeros flexíveis e água bruta concentrada através de seringas de coleta (`sampling_syringe`) e frascos de vidro.
- [x] **Biorremediação de Resíduos Salinos**:
  - [x] Substrato saturado de salitre (`salinized_sand`) resultante de processos intensivos de dessalinização.
  - [x] Utilização de plantas halófitas (`halophyte_plant`) em 4 estágios de desenvolvimento para absorver o excesso de salitre acumulado no solo e restaurar solo limpo.

### ⚡ Fase 13: Rede Logística de Dutos & Malha Energética WPT Expandida (Concluída - 100%)
- [x] **Torre Retransmissora WPT de Longo Alcance (`wpt_relay_tower`)**:
  - [x] Pilão de alta voltagem para estender a rede de energia sem fio em até 128 blocos, alimentando escavações e postos avançados remotos.
  - [x] Indicador de sinal com arco voltaico de ressonância eletromagnética, efeitos de partículas e som ambiente.
  - [x] Gerenciamento espacial global com `WptRelayTowerManager` interligado aos receptores solares wireless.
- [x] **Dutos de Fluidos Inteligentes (`smart_fluid_pipe`)**:
  - [x] Sistema modular de transporte pressurizado para água salobra, água potável, salmoura e propelente.
  - [x] Conexão dinâmica de 6 direções com VoxelShapes dedicados e prevenção de refluxo unidirecional.
  - [x] Integração completa com Fabric Transfer API (`FluidStorage.SIDED`).
- [x] **Console Holográfico de Monitoramento de Rede (`grid_monitor_console`)**:
  - [x] Terminal interativo com interface holográfica dark-tech (`GridMonitorConsoleScreen`).
  - [x] Telemetria em tempo real: contagem e geração solar, contagem e geração térmica, número de torres retransmissoras, acumuladores ativos, energia total estocada e capacidade do buffer.
  - [x] Diagnóstico da malha energética com status de estabilidade (BALANCED, SURPLUS, DEFICIT, CRITICAL) e carga local de cobertura WPT.
- [x] **Acumulador de Estado Sólido Industrial (`solid_state_accumulator`)**:
  - [x] Banco de baterias de alta densidade energética (500.000 J) com sustentação para tempestades severas e a noite fria.
  - [x] Modos de operação selecionáveis no bloco e na interface: AUTO (equilibra carga/descarga), CHARGE (apenas armazena) e DISCHARGE (fornece energia WPT ativa em 48 blocos).
  - [x] Interface gráfica industrial com slots de bateria/célula, barra de energia animada e botão de alternância de modo (`SolidStateAccumulatorScreen`).

### 🌋 Fase 14: Expedições Subterrâneas Profundas & Surto de Plasma Cósmico
- [x] **Cavernas de Quartzo Piezoelétrico (`piezo_caverns`)**:
  - [x] Geodos subterrâneos raros (Y: -30 a 10) repletos de cristais que vibram acusticamente ao sofrer impacto ou corte por laser (`PiezoQuartzBlock`, `BuddingPiezoQuartzBlock`, `PiezoQuartzClusterBlock`).
  - [x] Fonte de ressonadores piezoelétricos (`piezo_resonator`, `piezo_quartz_shard`) para tecnologia militar e sísmica avançada.
  - [x] Geração procedural subterrânea configurada via recursos de bioma e `SandStormWorldGen`.
- [x] **Perfuratriz Automática de Poço Profundo (`deep_core_drill`)**:
  - [x] Maquinário industrial de grande porte para extração contínua de fluidos fósseis pressurizados e minerais raros do manto planetário (`DeepCoreDrillBlock`, `DeepCoreDrillBlockEntity`).
  - [x] Sistema de armazenamento de energia (50.000 J a 50 J/t), tanque de fluidos embutido de 4.000 mB integrado à Fabric Transfer API (`Storage<FluidVariant>`), buffer de 8 slots de minérios e envase automático de baldes de fluidos fósseis (`pressurized_fossil_fluid_bucket`).
  - [x] Chassis industrial cyberpunk com interface holográfica (`DeepCoreDrillScreen`, `DeepCoreDrillMenu`) e monitoramento em tempo real.

### 🛡️ Fase 15: Tecnologia Militar de Plasma, Blindagens Exóticas & Defesa Orbital
- [x] **Canhão de Feixe de Plasma Pesado (`heavy_plasma_cannon`)**:
  - [x] Arma industrial/militar pesada que projeta feixes de plasma térmico concentrado a longas distâncias (alcance 72m, dano 32, custo 1.800 J), perfurando alinhamentos de alvos e vitrificando blocos de areia em vidro (`HeavyPlasmaCannonItem`).
  - [x] Integrado ao sistema de energia e baterias do traje espacial, com recuo acústico e feixe de plasma com partículas de alta temperatura.
- [x] **Gerador de Escudo de Força Cinético (`kinetic_shield_generator`)**:
  - [x] Estrutura de contenção defensiva para bases e postos avançados (`KineticShieldGeneratorBlock`, `KineticShieldGeneratorBlockEntity`).
  - [x] Projeta cúpula defensiva com raio de 20 blocos (`KineticShieldTracker`), defletindo e desintegrando projéteis hostis e amortecendo completamente as vibrações de passos no subsolo para suprimir ataques de vermes gigantes (`SeismicSurvivalHandler`, `SandwormSeismicTargetGoal`).
  - [x] Sustentação contínua de energia (upkeep 5 J/t, deflexão 50 J) com buffer interno de 20.000 J alimentável pela rede WPT.
- [x] **Matriz de Blindagem Composta de Titânio e Quitina (`titanium_chitin_composite`)**:
  - [x] Superliga metalobiológica sintetizada na câmara do Fabricador de Nanitas a partir de placas de quitina de verme (`sandworm_chitin`) e liga metálica / atuadores (`scrap_metal`, `nano_actuator`).
  - [x] Utilizada como ingrediente estrutural essencial para armamentos pesados, maquinário de blindagem e componentes orbitais.
- [x] **Satélite de Sensoriamento Remoto Orbital (`orbital_survey_satellite`)**:
  - [x] Dispositivo de lançamento aeroespacial sob céu aberto com propulsão e efeitos pirotécnicos (`OrbitalSurveySatelliteItem`).
  - [x] Ativa o sistema de rede orbital planetária persistente (`SatelliteSavedData`, `SatelliteNetworkManager`).
  - [x] Integração completa de telemetria com o Datapad (`DatapadClientHelper`, `SurvivalDatapadScreen`), fornecendo link orbital ativo e previsão de contagem regressiva para a próxima tempestade de areia iônica.

### 🌪️ Fase 16: Biomas Extremos, Ventos Radioativos & Oásis Fósseis
- [ ] **Ermos de Fulgurito Líquido & Dunas Vitrificadas (`fulgurite_wastes`)**:
  - Sub-bioma desértico hiper-radioativo formado por séculos de descargas iônicas contínuas, repleto de monólitos de vidro negro e areia eletrizada condutora.
- [ ] **Oásis Fóssil Subterrâneo (`fossilized_oasis`)**:
  - Cavernas ocultas com remanescentes botânicos preservados em âmbar e fontes termais minerais, fonte de sementes ancestrais para terraformação definitiva.

### 🚂 Fase 17: Logística Maglev, Linhas Industriais & Domos Coloniais
- [ ] **Sistema Ferroviário Magnético de Areia (`sand_maglev_rail`)**:
  - Trilhos de levitação magnética e vagões autônomos de alta velocidade para transporte expresso de minérios, fluidos e jogadores entre postos avançados distantes.
- [ ] **Domo Residencial de Colonos (`habitat_dome`)**:
  - Módulos habitacionais pressurizados com controle atmosférico integrado, recicladores de oxigênio e camas de criostase para suporte de vida prolongado.
- [ ] **Linha de Montagem Industrial Automatizada (`auto_assembly_line`)**:
  - Esteiras industriais e braços manipuladores robóticos para manufatura contínua sem necessidade de intervenção do jogador em receitas em cadeia.

### 🚧 Fase 18: Fortificações Perimétricas, Muralhas de Espinhos & Contra-Medidas Físicas
- [ ] **Paredes com Espinhos de Titânio Balístico (`titanium_spike_wall`)**:
  - Painel modular de fortificação estrutural feito de liga pesada de titânio escuro (`#121820`) e pontas reforçadas de metal temperado.
  - **Dano Físico Contínuo Indiscriminado**: Qualquer entidade que encostar, colidir ou tentar atravessar (monstros invasores, outros jogadores ou o próprio operador da base desatento) sofre 6.0 pontos de dano cinético de perfuração por contato contínuo, além de desaceleração severa e efeito lacerante.
  - Suporte a posicionamento multidirecional: vertical como parede de barreira perimétrica, horizontal rente ao solo ou invertido em tetos de contenção.
  - Barreira física passiva imune a desgaste de baterias, ideal para defesa permanente de perímetros de assentamento.
- [ ] **Paredes de Espinhos Retráteis Pneumáticos (`retractable_spike_wall`)**:
  - Bloco de contenção ativo dotado de pistões pneumáticos de alta pressão e estacas afiadas de perfuração.
  - Estado recolhido: As estacas permanecem alinhadas à superfície da parede, funcionando como bloco liso de passagem livre e inofensivo.
  - Ativação por Redstone/Sinal Lógico: Ejeção repentina e violenta das lâminas projetando-se em até 1.5 bloco para a frente.
  - **Dano Crítico de Empalamento & Repulsão**: Causa 14.0 de dano instantâneo de perfuração mecânica somado a alto recuo cinético (knockback), atingindo com letalidade qualquer criatura ou player (aliado ou inimigo) presente na área de projeção.
- [ ] **Muralha de Espinhos Eletrizados de Alta Tensão (`electrified_spike_barrier`)**:
  - Barreira metálica condutora conectada à malha de energia sem fio (WPT) ou cabeamento elétrico direto.
  - **Dano Duplo (Perfuração Mecânica + Arco Voltaico Ciano)**: Ao encostar, descarrega energia elétrica de alta densidade (arco voltaico emissivo `#00E5FF`), consumindo 50 J por pulso de descarga.
  - Causa dano físico mais choque elétrico e paralisia temporária (Stun / Lentidão extrema IV).
  - Indiscriminado e de alto risco: Eletrocuta qualquer ser vivo desprotegido (jogadores necessitam de Traje Espacial com módulo de isolamento para não sofrerem eletrocussão letal).
- [ ] **Muralha de Espinhos com Revestimento Bio-Corrosivo (`corrosive_chitin_spike_wall`)**:
  - Barreira avançada forjada no Fabricador de Nanitas com quitina afiada de verme (`sandworm_chitin`), biopolímeros flexíveis (`flexible_biopolymer`) e seiva pesada de cacto (`heavy_sap_bottle`).
  - **Dano de Corrosão Ácida e Degradação de Armaduras**: O contato com as pontas embebidas em ácido biológico inflige perfuração e o status "Corrosão Ácida", drenando vida ao longo do tempo e deteriorando aceleradamente a durabilidade da armadura equipada a cada tick.
  - Barreira altamente perigosa para contenção de espécimes hostis biológicos e dissuasão de invasores em ambientes desérticos.
- [ ] **Armadilha de Espinhos de Chão Pressurizada (`kinetic_floor_spikes`)**:
  - Grelha embutida de piso camuflável nas texturas de arenito ou placas de titânio de bases.
  - Ativação sísmica por pressão: Ao detectar o peso de passos de qualquer entidade (mobs, outros players ou o próprio construtor), ejeta estacas verticais afiadas do assoalho.
  - Dano perfurante ascendente com foco nos membros inferiores, ignorando proteções convencionais que não sejam botas reforçadas do traje espacial.
- [ ] **Portão Fortificado com Grades de Espinhos Esmagadores (`crushing_spike_gate`)**:
  - Portão industrial motorizado de contenção com fileiras de dentes pontiagudos de titânio.
  - Projetado para fechamento hermético de emergência em hangares, garagens de rovers e eclusas de ar.
  - Causa dano crítico esmagador e perfurante se fechar sobre qualquer entidade que esteja sob o vão no momento da descida.

### 🏗️ Fase 19: Construtor Autônomo de Megaestruturas, Drones Operários & Manufatura Holográfica 3D (Layer-by-Layer)
- [ ] **Núcleo de Construção de Megaestruturas (`megastructure_constructor`)**:
  - Bloco tecnológico pesado de ancoragem no solo com cúpula de emissão de sinal, compartimento de peças e hangar de drones embutido.
  - Conexão nativa à malha de energia sem fio (WPT) e buffer interno de alta voltagem (500.000 J), consumindo energia a cada bloco materializado.
  - Interface holográfica interativa para seleção de esquemáticos (Cúpula Geodésica de Biosfera, Cidadela Fortificada, Silo de Lançamento e Pirâmide Tecnológica), controle de velocidade de montagem e matriz de inventário de materiais requeridos com contadores em tempo real.
  - Telemetria de diagnóstico: se faltar algum recurso específico ou energia, o construtor entra em modo de espera e projeta a silhueta da peça faltante em tom de alerta âmbar (`#FFB300`).
- [ ] **Drones Construtores Operários (`builder_drone`)**:
  - Nova entidade de robótica aérea inteligente (`BuilderDroneEntity`) acoplada ao construtor central, operando em esquadrilhas de 2 a 4 unidades autônomas num raio de até 48 blocos.
  - **Ciclo Operário de Montagem**: O drone coleta o bloco necessário no compartimento do núcleo, decola com propulsores iônicos ciano e navega de forma suave até a coordenada exata do bloco na camada ativa.
  - **Feixe Litográfico de Fusão Molecular**: O drone paira sobre a coordenada e projeta um feixe contínuo de laser/plasma ciano (`#00E5FF`) com som característico de solda a laser e partículas de brasas e faíscas elétricas até assentar o bloco no mundo físico.
  - **Dano Térmico Contínuo Indiscriminado (Laser Hazard)**:
    - Qualquer entidade que passar por baixo do feixe, cruzar a linha de tiro do laser ou tocar no ponto focal de fusão molecular (monstros invasores, animais, outros jogadores ou o próprio arquiteto/operador) sofre 8.0 pontos de dano térmico contínuo por segundo (`DamageSource` de calor/plasma), com ignição de fogo e efeito de repulsão leve.
    - Mecânica de alto risco no canteiro de obras que exige atenção durante a operação, permitindo também o uso tático do feixe construtor como armadilha ambiental de defesa caso hordas invasoras se aproximem da área em construção.
    - Partículas de fumaça cinzenta, clarão avermelhado de calor e estalos sonoros de queimadura ao atingir qualquer criatura ou jogador sob o raio.
  - Retorno automático ao núcleo em caso de tempestades elétricas extremas ou término do lote de blocos da camada.
- [ ] **Mecânica de Manufatura Aditiva 3D Layer-by-Layer (Fatia a Fatia Y)**:
  - Algoritmo construtivo estrito por fatias horizontais de elevação: a estrutura é erguida de baixo para cima (`Y_min` até `Y_max`), nunca deixando blocos flutuando sem suporte físico ou fora da ordem de impressão.
  - Dentro de cada nível Y, a construção avança de forma concêntrica ou radial contínua, criando um processo visualmente fascinante e rítmico, idêntico ao de uma impressora 3D industrial em escala monumental.
  - Disparo de aviso sonoro harmônico (`megastructure_layer_complete`) e notificação na tela ao concluir cada andar ("*Camada 8/36 concluída. Avançando para o nível superior...*").
- [ ] **Projeção Holográfica Prévia (Holographic Blueprint Wireframe)**:
  - Sistema de renderização cliente em tempo real que projeta o modelo completo da megaestrutura no espaço tridimensional com malha translúcida azul/ciano antes e durante a construção.
  - A camada atual em execução recebe realce luminescente pulsante, facilitando o acompanhamento visual do progresso da obra de qualquer ângulo.
  - Os blocos físicos assentados pelos drones substituem o holograma instantaneamente com transição fluida de materialização.
- [ ] **Catálogo de Megaestruturas Nativas em Blueprints**:
  - *Cúpula Geodésica de Biosfera (`biosphere_dome`)*: Enorme cúpula hemisférica hermética de vidro temperado e nervuras metálicas de titânio, com eclusa de despressurização e canteiros centrais de terraformação.
  - *Castelo / Cidadela de Fortificação Planetária (`planetary_citadel`)*: Fortaleza monumental de múltiplos andares com muralhas perimétricas com ameias, torres nos quatro cantos preparadas para acoplar Torretas Sônicas (`autonomous_sonic_turret`) e salão central.
  - *Silo & Plataforma de Lançamento Orbital (`orbital_launch_silo`)*: Estrutura vertical monumental com anéis de sustentação de fuselagem, torre de gantry e tubulações de combustível para satélites e naves.
  - *Pirâmide Tecnológica do Deserto (`desert_tech_pyramid`)*: Megálito de arenito lapidado e veios condutores de silício, com câmara interna de reatores e vértice superior captador de tempestades iônicas.
- [ ] **Finalização Épica & Efeitos Audiovisuais Satisfatórios ("Eye-Candy")**:
  - Pulso sonoro triunfante em 64 blocos ao concluir o último bloco da megaestrutura, acompanhado de dispersão de onda de choque de partículas de luz ciano e recolhimento em formação dos drones.
  - A estrutura concluída é automaticamente registrada como Zona Sísmica Segura imune a ataques de vermes da areia no solo interno.

### 🌍 Fase 20: Preset de Mundo Dedicado & Desacoplamento do Vanilla ("Mundo" -> "Tipo de mundo: SandStorm") (Concluída - 100%)
- [x] **Desacoplamento Total da Geração de Mundo Vanilla**:
  - [x] Interromper a substituição invasiva e destrutiva dos arquivos nativos do Minecraft (`data/minecraft/dimension/overworld.json` e `data/minecraft/worldgen/world_preset/normal.json`).
  - [x] Garantir que o Overworld vanilla permaneça 100% íntegro e jogável quando o jogador selecionar ou jogar em perfis/mundos convencionais.
- [x] **Criação do World Preset Oficial do SandStorm (`sandstorm:desert_planet`)**:
  - [x] Registro do preset de mundo dedicado em `data/sandstorm/worldgen/world_preset/desert_planet.json`.
  - [x] Configuração isolada da dimensão sob namespace próprio do mod:
    - Gerador de ruído do deserto infinito sem oceanos nem corpos d'água superficiais (`sea_level: -64`, `default_fluid: "minecraft:air"` em `data/sandstorm/worldgen/noise_settings/desert_planet.json`).
    - Injeção das features geológicas e arqueológicas exclusivas: aquíferos salobros subterrâneos (`sandstorm:brackish_aquifer`), ruínas tecnológicas soterradas (`sandstorm:buried_tech_ruins`) e núcleos de dados ancestrais (`sandstorm:ancient_data_core`).
    - Supressão de estruturas e mobs convencionais no ecossistema do preset árido.
- [x] **Integração no Botão "Tipo de mundo" da Aba "Mundo"**:
  - [x] Registro do identificador `sandstorm:desert_planet` na tag de presets do Minecraft: `data/minecraft/tags/worldgen/world_preset/normal.json` com `"replace": false`.
  - [x] Disponibilização direta no botão ciclável **"Tipo de mundo"** na aba **"Mundo"** da tela de criação (`CreateWorldScreen` / `WorldCreationUiState`), permitindo ao jogador alternar livremente entre:
    - `Tipo de mundo: Sandstorm`
    - `Tipo de mundo: Padrão` (Minecraft Vanilla)
    - `Tipo de mundo: Superplano`
    - `Tipo de mundo: Grandes Biomas`
    - `Tipo de mundo: Amplificado`
    - `Tipo de mundo: Mundo Único`
- [x] **Pré-seleção Automática por Padrão ao Abrir a Tela de Criação**:
  - [x] Implementação de Mixin client em `WorldCreationUiState` (`SandStormWorldPresetSelectionMixin`).
  - [x] Ao abrir a tela "Criar novo mundo", o seletor `Tipo de mundo` na aba "Mundo" é automaticamente inicializado com o preset do SandStorm (`sandstorm:desert_planet`) como opção padrão ativa (interceptando a seleção vanilla inicial de `WorldPresets.NORMAL` em novos mundos e aplicando `setWorldType`).
  - [x] Proporciona inicialização instantânea e imersiva para o jogador sem requerer configuração manual de menus, mantendo total liberdade para alternar para "Padrão" caso deseje um mundo vanilla.
- [x] **Localização & Identidade (i18n)**:
  - [x] Chaves de internacionalização registradas em `pt_br.json`, `en_us.json` e `es_es.json`:
    - `generator.sandstorm.desert_planet`: "Sandstorm" (título enxuto oficial em pt_br, en_us e es_es).
    - `generator.sandstorm.desert_planet.description`: "Planeta desértico árido e hostil, assolado por tempestades de areia, vermes gigantes e segredos tecnológicos soterrados."
- [x] **Suíte de Testes Automatizados & Validação Arquitetural**:
  - [x] Teste de integridade estrutural do preset `desert_planet.json` (`SandStormWorldPresetTest.java`).
  - [x] Teste de inclusão na tag `data/minecraft/tags/worldgen/world_preset/normal.json`.
  - [x] Validação de não-poluição do namespace vanilla quando presets padrão forem selecionados.
  - [x] Cobertura 100% verde com rigor absoluto: zero comentários, zero imports inline e zero imports não utilizados (**522 testes automatizados** aprovados).

---

## 🎧 Catálogo Completo de Efeitos Sonoros Necessários (SFX)

A tabela abaixo detalha todos os arquivos de áudio necessários para a imersão completa do mod. Os arquivos devem ser fornecidos no formato **Ogg Vorbis (`.ogg`)**, taxa de amostragem **44.1 kHz**, e colocados no diretório `src/main/resources/assets/sandstorm/sounds/`.

| Identificador do Som | Arquivo `.ogg` | Categoria | Duração | Descrição Tímbrica & Referência Sonora |
| :--- | :--- | :--- | :--- | :--- |
| `item.sonic_cannon.blast` | `sonic_cannon_blast.ogg` | `players` | 1.5s | Disparo de pulso acústico de alta energia. Transiente rápido com decay ressonante metálico e sub-bass comprimido (estilo railgun/EMP). |
| `entity.megazord.shockwave` | `megazord_shockwave.ogg` | `hostile` | 2.5s | Pulso colossal defensivo emitido pelo mecha. Estrondo sísmico grave que dispersa e dissipa com eco atmosférico distante. |
| `entity.sandworm.rumble` | `sandworm_rumble.ogg` | `hostile` | 3.5s (loop) | Tremor subterrâneo contínuo e abafado. Sub-bass rítmico simulando movimentação massiva de areia sob os pés do jogador. |
| `entity.sandworm.emerge` | `sandworm_emerge.ogg` | `hostile` | 2.5s | O verme irrompendo violentamente pelas dunas. Erupção de areia cascalhenta misturada a um rugido estridente insectóide/titânico. |
| `entity.sandworm.attack` | `sandworm_attack.ogg` | `hostile` | 1.0s | Mordida voraz. Fechamento de mandíbulas de quitina pesada com estalo seco e impacto de ar de alta pressão. |
| `block.thumper.thump` | `thumper_thump.ogg` | `blocks` | 1.0s | Batimento sísmico do pistão do Thumper contra a rocha/areia. Impacto mecânico denso e sordo, gerando onda sísmica audível em 64 blocos. |
| `item.anomaly_radar.ping` | `anomaly_radar_ping.ogg` | `players` | 0.4s | Beep sintetizado analógico de sonar portátil. Som limpo e agudo de detecção direcional (pitch adaptativo por distância). |
| `item.atmospheric_analyzer.scan` | `atmospheric_analyzer_scan.ogg` | `players` | 0.8s | Chirp sequencial de varredura eletrônica. Sons de processamento de microprocessador e telemetria de sensores de gás. |
| `block.printer_3d.craft` | `printer_3d_craft.ogg` | `blocks` | 1.5s | Laser litográfico e micro-motores de passo. Zumbido harmônico com passos mecânicos precisos de montagem de wafer. |
| `block.desalination_filter.process` | `desalination_process.ogg` | `blocks` | 1.8s | Filtragem osmótica. Sucção de líquido em tubos pressurizados, condensação e sibilo de liberação de vapor térmico. |
| `block.nanite_fabricator.activate` | `nanite_activate.ogg` | `blocks` | 2.0s | Síntese de nanorobôs. Ressonância eletromagnética ascendente com descarga estática suave e clique de contenção de plasma. |
| `block.atmospheric_terraformer.hum` | `terraformer_hum.ogg` | `blocks` | 4.0s (loop) | Reator central de cúpula ativa. Zumbido eletrostático contínuo, circulação suave de ar e dispersão de partículas ionizadas. |
| `block.assembly_bay.construct` | `assembly_construct.ogg` | `blocks` | 2.2s | Manufatura pesada de veículos. Ruído de solda a ponto por arco elétrico, atuação hidráulica e travamento mecânico de chassis. |
| `entity.cargo_drone.flight` | `cargo_drone_flight.ogg` | `neutral` | 3.0s (loop) | Voo suave de rotores elétricos de drone quadricóptero. Som limpo e contínuo de rotação sem vibração ou contato com o solo. |
| `entity.excavator.engine` | `excavator_engine.ogg` | `neutral` | 3.0s (loop) | Motor elétrico de alta tração e esteiras mecânicas triturando cascalho e areia durante locomoção e escavação. |
| `entity.megazord.step` | `megazord_step.ogg` | `players` | 1.2s | Passada pesada de 5 metros de altura. Impacto de placa metálica com amortecedores hidráulicos comprimindo com força bruta. |
| `suit.battery.low` | `suit_battery_low.ogg` | `ambient` | 0.6s | Alarme de advertência de bateria fraca no capacete do traje espacial. Dois tons curtos eletrônicos de prioridade médica. |
| `suit.solar.charge` | `suit_solar_charge.ogg` | `ambient` | 0.8s | Sinal suave de ativação dos painéis fotovoltaicos ao ser exposto à luz solar direta. Acorde ascendente harmônico sutil. |
| `weather.sandstorm.wind` | `sandstorm_wind.ogg` | `weather` | 6.0s (loop) | Variação 1: Vento uivante e rajadas repentinas de areia abrasiva. |
| `weather.sandstorm.wind.light` | `sandstorm_wind_2.ogg` | `weather` | 6.0s (loop) | Variação 2: Brisa arenosa leve para início e fim de tempestade (< 35% de intensidade). |
| `weather.sandstorm.wind.medium` | `sandstorm_wind_3.ogg` | `weather` | 6.0s (loop) | Variação 3: Ventania intermediária em aceleração contínua (35% a 70% de intensidade). |
| `weather.sandstorm.wind.heavy` | `sandstorm_wind_4.ogg` | `weather` | 6.0s (loop) | Variação 4: Tempestade violenta e ensurdecedora no pico sísmico/climático (> 70% de intensidade). |
| `block.spike_wall.extend` | `spike_wall_extend.ogg` | `blocks` | 0.7s | Ejeção pneumática rápida de estacas de titânio. Som sibilante de ar comprimido seguido de estalo metálico. |
| `block.spike_wall.retract` | `spike_wall_retract.ogg` | `blocks` | 0.8s | Recolhimento mecânico de pistões de espinhos. Som de engrenagens voltando à carcaça do bloco. |
| `block.spike_wall.impale` | `spike_wall_impale.ogg` | `players` | 0.6s | Impacto perfurante e dilacerante. Som seco de aço perfurando blindagem e tecido orgânico com estalo de pressão. |
| `block.electrified_spikes.shock` | `electrified_spikes_shock.ogg` | `blocks` | 1.0s | Descarga de alta tensão contínua em arco voltaico ciano ao contato com corpos orgânicos ou invasores. |
| `block.megastructure_constructor.laser` | `megastructure_laser.ogg` | `blocks` | 1.8s (loop) | Feixe de laser de fusão molecular contínuo emitido por drones construtores ao assentar blocos. |
| `block.megastructure_constructor.layer_complete` | `megastructure_layer_complete.ogg` | `blocks` | 1.2s | Sinal harmônico de telemetria anunciando o término da fatia/camada Y atual e avanço para o andar superior. |
| `block.megastructure_constructor.complete` | `megastructure_complete.ogg` | `players` | 3.5s | Acorde orquestral triunfante em ressonância cósmica com dispersão de onda de choque celebrando a conclusão da megaestrutura. |
| `entity.sandtrout.slither` | `sandtrout_slither.ogg` | `neutral` | 1.5s (loop) | Som viscoso e rápido de rastejamento da truta da areia sob os grãos e dunas. |
| `entity.sandworm_larva.chitter` | `sandworm_larva_chitter.ogg` | `hostile` | 0.8s | Cliques rápidos e estalos sísmicos insectoides de comunicação em bando das larvas de verme. |
| `entity.sandworm_larva.bite` | `sandworm_larva_bite.ogg` | `hostile` | 0.6s | Mordida rápida de mandíbula quitinosa tenra com sibilância de secreção ácida. |
| `entity.juvenile_sandworm.submerge` | `juvenile_sandworm_submerge.ogg` | `hostile` | 2.0s | Deslocamento de dunas e mergulho rápido de espécime juvenil nas camadas superficiais de areia. |

> [!NOTE]
> **Status dos Efeitos Sonoros**: Todos os 20 arquivos `.mp3` foram convertidos com sucesso para Vorbis `.ogg` (mantendo os `.mp3` originais preservados).
> As 4 variações de vento foram integradas em um pool randômico de reprodução contínua e em despachos dinâmicos graduais baseados na intensidade climática em `SandstormWeatherHandler`. Todos os blocos, armas, trajes, veículos e o Verme de Areia agora contam com áudio proprietário imersivo.

### 🧪 Suíte de Testes e Garantia de Áudio (`SandStormSoundEventsTest.java`)
- [x] **Integridade do Registro Java**: Validação estrita de todos os 23 `SoundEvent` registrados em `SandStormSoundEvents.java`.
- [x] **Validação Binária de Cabeçalhos OGG Vorbis**: Inspeção dos 4 bytes mágicos (`OggS` / `0x4F 0x67 0x67 0x53`) garantindo que nenhum arquivo de áudio esteja corrompido ou vazio (tamanho > 100 bytes).
- [x] **Saneamento de Distribuição**: Garantia automatizada de zero arquivos provisórios `.mp3` na pasta de assets de produção.
- [x] **Paridade Referencial `sounds.json`**: Cada evento de som possui categoria válida (`players`, `hostile`, `blocks`, `neutral`, `ambient`, `weather`) e todos os caminhos referenciados mapeiam 1:1 para arquivos `.ogg` existentes no disco.
- [x] **Legendas e Acessibilidade (i18n)**: Paridade de chaves `subtitles.*` em `pt_br.json`, `en_us.json` e `es_es.json`.
- [x] **Atenuação Dinâmica em Tempestades**: Verificação de abafamento de áudio externo e propagação de ruído sísmico em tempo real.

---

## 🎯 Status de Fechamento do MVP e Modelagens

### 1. Modelagem 3D no Blockbench & Texturas Pixel Art (Foco Visual)
- [x] `raw_silicon`: Modelo Blockbench (.bbmodel) + Modelo Item (.json) + Textura 16x16 (.png).
- [x] `circuit_board`: Modelo Item (.json) + Textura 16x16 (.png).
- [x] **Itens Tecnológicos Concluídos**:
  - [x] `silicon_wafer`: Textura e modelo 16x16.
  - [x] `nano_actuator`: Textura e modelo 16x16.
  - [x] `tech_disc`: Textura e modelo 16x16.
  - [x] `scrap_metal`: Textura e modelo 16x16.
  - [x] `mineral_salt`: Textura e modelo 16x16.
  - [x] `empty_cartridge`: Recipiente de combustível reforçado.
  - [x] `propellant_cartridge`: Cartucho de propelente de alta pressão.
  - [x] `suit_upgrade_*`: 5 módulos de melhoria de armadura do traje.
  - [x] `sandboard`: Prancha de areia portátil para locomoção.
  - [x] `xeno_grass_seeds`: Sementes xerófilas geneticamente aprimoradas.
  - [x] `sampling_syringe`: Seringa descartável de coleta de seiva e amostras biológicas.
  - [x] `heavy_sap_bottle`: Frasco tecnológico com seiva viscosa concentrada.
  - [x] `flexible_biopolymer`: Biopolímero flexível extraído de seiva e processado.
  - [x] `halophyte_plant`: Planta halófita suculenta (textura de item e modelos de crescimento).
  - [ ] `sandtrout_capsule`: Frasco criogênico de contenção hermética de truta da areia viva.
  - [ ] `soft_chitin_plate`: Placa de quitina tenra de larva do verme.
  - [ ] `larval_bile`: Frasco de secreção ácida digestiva larval.
  - [ ] `water_of_life_vial`: Frasco tecnológico reforçado com a mística Água da Vida luminescente.
- [x] **Equipamentos e Ferramentas Concluídos**:
  - [x] `sonic_cannon`: Modelo Item (.json) + Textura personalizada 16x16.
  - [x] `anomaly_radar`: Textura e modelo 16x16.
  - [x] `atmospheric_analyzer`: Textura e modelo 16x16.
  - [x] `space_suit_helmet`, `chestplate`, `leggings`, `boots`: Redesign visual dark-tech completo com modelo 3D customizado (`SpaceSuitArmorModel`), ombreiras industriais, tanques duplos dorsais de O2, visor holográfico emissivo ciano (`#00E5FF`), texturas 2D de itens redesenhadas e camadas de armadura 3D (`space_suit.png` e `space_suit_glow.png`).
- [x] **Alimentos e Fluidos Concluídos**:
  - [x] `space_ration`: Textura de ração militar espacial embalada a vácuo.
  - [x] `potable_water_bottle`: Frasco tecnológico com líquido azul puro.
  - [x] `brackish_water_bottle`: Frasco com líquido turvo salobro.
- [x] **Modelos 3D de Blocos e Máquinas (Blockbench & Blockstates)**:
  - [x] `thumper.bbmodel`: Modelo com pistão oscilante, patas de ancoragem e blockstates para `powered=true` e `powered=false`.
  - [x] `printer_3d.bbmodel`: Bancada tecnológica com pórtico e cabeçote litográfico laser.
  - [x] `desalination_filter.bbmodel`: Tubulações de cobre, tanque e condensador de osmose.
  - [x] `nanite_fabricator.bbmodel`: Câmara de contenção e emissão de luz de nanitas.
  - [x] `chemical_refinery.bbmodel`: Máquina industrial química com tubos iluminados e leds de status.
  - [x] `atmospheric_terraformer.bbmodel`: Reator central esférico ionizado com cúpula de terraformação.
  - [x] `drone_dock.bbmodel`: Pista de aterrissagem, faixas de perigo e pilão de recarga.
  - [x] `assembly_bay.bbmodel`: Pátio de montagem com piso reforçado e colunas de guindaste.
  - [x] `ancient_data_core.bbmodel`: Monólito arenítico com núcleo óptico ancestral.
  - [x] `buried_tech_ruins.bbmodel`: Blindagem aeroespacial soterrada com rebites e desgaste térmico.
  - [x] `brackish_aquifer`: Bloco mineral sedimentar com veios salinos e aquíferos.
  - [x] `sandstone_workbench`: Bancada de emergência talhada em arenito com tampo quadriculado.
  - [x] `sandstone_furnace`: Fornalha de arenito com câmara de queima e brasas ativas.
  - [x] `dew_condenser`: Condensador noturno de orvalho em estrutura de arenito e malha de coleta.
  - [x] `hydroponic_chamber`: Módulo hidropônico selado com câmara de iluminação bio-UV e renderizador 3D animado.
  - [x] `xeno_grass_block`: Bloco de grama xeno-adaptada com textura de topo xerófila e transição com areia.
  - [x] `heavy_sap_cactus`: Cacto xerófilo de seiva pesada com 4 níveis visuais de seiva (`SAP_LEVEL` 0 a 3).
  - [x] `salinized_sand`: Substrato de areia saturado de salitre residual pós-dessalinização.
  - [x] `halophyte_plant`: Modelos 3D de planta suculenta em 4 estágios de crescimento para biorremediação.
  - [x] `wpt_relay_tower`: Torre pilar de alta voltagem com emissor WPT e estados ativo/inativo.
  - [x] `smart_fluid_pipe`: Dutos modulares de fluidos inteligentes com 6 conexões direcionais.
  - [x] `grid_monitor_console`: Console com tela holográfica inclinada de telemetria energética da grade.
  - [x] `solid_state_accumulator`: Banco de baterias de estado sólido industrial com indicador frontal de carga.
- [x] **Modelos de Entidades (Blockbench)**:
  - [x] `sandworm.bbmodel`: Corpo cilíndrico segmentado com mandíbulas quádruplas abertas e anel bucal.
  - [ ] `sandtrout.bbmodel`: Modelo pequeno de criatura ameboide coriácea rastejante de areia (Truta da Areia / Little Maker).
  - [ ] `sandworm_larva.bbmodel`: Modelo segmentado ágil de larva/ninfa com anéis tenros de quitina e mandíbula trirradiada.
  - [ ] `juvenile_sandworm.bbmodel`: Modelo intermediário de verme caçador de dunas (~15 blocos) com crista dorsal e sulcos de escavação.
  - [ ] `builder_drone.bbmodel`: Drone operário de construção com pórtico emissor de laser de fusão e garras mecânicas.
  - [x] `cargo_drone.bbmodel`: Drone quadricóptero com rotores e garras de carga.
  - [x] `excavator_vehicle.bbmodel`: Rover industrial de esteiras duplas com broca giratória frontal.
  - [x] `megazord.bbmodel`: Mecha bípede titânico com cockpit e emissores de choque sônico.
  - [x] `sandboard`: Prancha de surfe nas dunas com fixadores de botas.

---

### 2. Geração de Mundo & Planeta Deserto Permanente
- [x] Override inicial de dimensão do Overworld (`data/minecraft/dimension/overworld.json`) para planeta deserto fixo (`minecraft:fixed` com `minecraft:desert`) *(a ser desacoplado na Fase 20 para preservar mundos vanilla intactos)*.
- [x] Override inicial do preset de mundo padrão (`data/minecraft/worldgen/world_preset/normal.json`) *(a ser migrado para o preset dedicado `sandstorm:desert_planet` na Fase 20)*.
- [x] Eliminação da pasta inválida `data/sandstorm/worldgen/feature/` (resolvendo o crash no launcher oficial).
- [x] Eliminação completa dos avisos `Missing model for variant` para todos os 10 blocos e máquinas.
- [x] **Migração Arquitetural para Preset Dedicado**: Implementação da Fase 20 com botão na aba "Mundo" (`Tipo de mundo: SandStorm`), pré-seleção padrão e desacoplamento de arquivos nativos do Minecraft.

---

### 3. Progressão Livre de Softlocks, Restrição Dimensional e Supressão de Phantoms
- [x] **Desbloqueio de Fundição Primária**: Receita de Fornalha a partir de 8 blocos de arenito (`sandstone_furnace.json`), permitindo processar silício e cozinhar sem minerar pedras antes de montar maquinário robótico.
- [x] **Metalurgia de Reciclagem**:
  - [x] Fundição e alto-forno de `scrap_metal` para `iron_ingot`.
  - [x] Extração e manufatura de `copper_ingot` a partir de `scrap_metal`.
- [x] **Resgate Avançado em Ruínas Soterradas (`BuriedTechRuinsBlock.java`)**: Drop balanceado de sucata metálica, discos tecnológicos, lingotes de cobre, redstone, pepitas de ouro e obsidiana.
- [x] **Montagem Industrial de Robôs (`AssemblyBayBlock.java`)**:
  - [x] `SandStormItems.SCRAP_METAL` (10.000 J) monta e despacha o `ExcavatorVehicleEntity` para destravar a mineração de rochas consolidadas.
  - [x] `ANCIENT_DATA_CORE` / `TECH_DISC` (25.000 J) monta o titânico `MegazordEntity`.
  - [x] `SandStormItems.NANO_ACTUATOR` (5.000 J) monta o `CargoDroneEntity`.
- [x] **Rebalanceamento de Fim de Jogo Planetário**:
  - [x] `nanite_fabricator.json` usa quitina de verme da areia (`sandworm_chitin`) e liga metálica (`scrap_metal`), conectando o combate do verme à alta tecnologia.
  - [x] `atmospheric_terraformer.json` utiliza o `ancient_data_core` e quitina de verme, eliminando a dependência do Wither/Netherite do Nether.
  - [x] Receitas de dispensador e pistão adaptadas com arenito para montagem do 3D Printer e Thumper.
- [x] **Supressão Total de Phantoms**:
  - [x] Bloqueio imediato no despachador biológico de entidades (`VanillaMonsterSuppressionHandler.java` descarta `minecraft:phantom`).
  - [x] Gamerule `doPatrolSpawning` e `spawnPhantoms=false` forçado nas regras de carregamento de nível e pouso da nave.
- [x] **Bloqueio de Viagens Dimensionais Convencionais (`DimensionPortalRestrictionHandler.java`)**:
  - [x] Interceptação de ativação de portal do Nether (pederneira/carga de fogo em obsidiana) com telemetria explicativa.
  - [x] Interceptação de inserção de Olho do Fim em molduras de portal com telemetria.
  - [x] Bloqueio forçado de gamerule `allowEnteringNetherUsingPortals=false`.
  - [x] Redirecionamento instantâneo de jogadores que cheguem a Nether/End de volta ao cockpit da nave no Overworld.
- [x] **Suíte de Testes de Integração de Combate & Dimensões**:
  - [x] `EnemyDamageIntegrationTest.java`: Dano do verme (18.0), atenuação da armadura do traje espacial (15 defesa + redução para sobrevivência viável), combate do Megazord (30 dano base + 25 sônico) e imunidade da Safe Zone.
  - [x] `DimensionTravelAndPhantomSuppressionTest.java`: Validação de detecção de tentativas de ignição, ativação de portal e identificação de dimensões proibidas.

---

## 📈 Histórico de Commits e Marcos

1. `4a1fd0c`: Setup inicial da arquitetura SandStorm, componentes ECS e métricas.
2. `37e72fe`: Implementação de mecânicas de sobrevivência, tempestades, thumper, vermes e mechas.
3. `6612c6a`: Atualização de compatibilidade para Minecraft 26.3 (Fabric API 0.160.7, Fabric Loader 0.19.5).
4. `9922e96`: Instalação do perfil Fabric 26.3 e cópia dos jars para o diretório `.minecraft`.
5. `10d396c`: Primeiros modelos Blockbench e texturas pixel art (`raw_silicon`, `circuit_board`).
6. `655fb84`: Adição da suíte de testes de integridade de assets, chaves, armaduras e arquitetura zero comentários.
7. `537c4a8`: Adição de 20 receitas oficiais data-driven e suíte de testes de compatibilidade JEI/REI/EMI.
8. `0e630cb`: Restrição tecnológica de ferramentas (exclusividade de robôs para mineração de pedra, liberação de pás para areia).
9. `72e3dd1`: Criação do TASKS.md inicial com métricas e roadmap.
10. `e0ebe24`: Correção de aterrissagem segura na superfície Y>=64, porta 4x4 sci-fi e armadura acoplada.
11. `6d03143`: Modelos 3D Blockbench completos, Overworld 100% deserto fixo e testes de áudio OGG.
12. `43a1ae0`: Correção de gargalos de progressão, testes de dano de inimigos, supressão de phantoms e restrições de portais Nether/End.
13. `v1.2.0`: Incremento de versão para 1.2.0, build do jar final sandstorm-1.2.0.jar e sincronização com Minecraft.
14. `855ccb3`: Correção da geração de mundo para o formato do Minecraft 26.3 (`worldgen/feature/`), eliminando crash do registry loader.
15. `MVP-100%`: Paridade total de legendas de acessibilidade (i18n) em 3 idiomas e atenuação sísmica dinâmica em tempestades de areia.
16. `v1.3.0`: Incremento de versão para 1.3.0, eliminação total de oceanos e corpos d'água superficiais, desativação de vilas/golens/aldeões e remoção de animais e vegetação vanilla para imersão em mundo árido, silencioso e estéril.
17. `v1.3.1`: Receptor Solar Sem Fio (WPT) no casco da nave com desobstrução solar total, montagem completa da bancada da cabine (impressora 3D, fabricador de nanorobôs, dessalinizador, fornalha e bancada de trabalho) e receitas oficiais de manufatura dos receptores.
18. `v1.3.2`: Implementação da Bomba de Nutrientes Arremessável (Nutrient Bomb), projétil com splash biológico e motor de enriquecimento e terraformação temporal do solo (areia -> terra -> grama viva -> vegetação espontânea).
19. `v1.3.3`: Sistema de Progressão e Resgate de Recompensas do Survival Datapad: persistência permanente de progresso por UUID com `PlayerQuestSavedData`, sincronização bidirecional via CustomPacketPayload, botão interativo `[RESGATAR]` com feedback de áudio, inclusão de 2 novas quests (`wireless_solar_receiver` e `nutrient_bomb`), balanceamento de recompensas para todas as 22 missões, e notificações não-intrusivas na actionbar com telemetria da I.A.T.I.
20. `v1.3.4`: Bancada de Trabalho de Arenito (`sandstone_workbench`), Fornalha de Arenito (`sandstone_furnace`), Condensador Noturno de Orvalho (`dew_condenser`), Prancha de Areia (`sandboard`), Módulos de Upgrade do Traje Espacial (`suit_upgrade_*`), Mochila a Jato com voo livre de sobrevivência e Refinaria Química Industrial (`chemical_refinery`).
21. `v1.3.5`: Saneamento de receitas básicas (remoção de arenito para gravetos de madeira), remapeamento ergonômico da lanterna para a tecla **G** (liberando a tecla F para troca de mãos), blindagem dupla da detecção de itens de fornalha no Datapad e expansão da suíte para **471 testes automatizados** com 100% de aprovação.
22. `v1.3.6`: Resolução de deadlock no thread do servidor em chunk loading (`ProceduralRuinsManager`), eliminação das faixas de calor no HUD (`SurvivalHudOverlay`), normalização de neblina em tempo limpo (`SandstormFogMixin`), e validação estrita de compatibilidade oficial com os otimizadores gráficos **Sodium**, **Iris Shaders** e **Lithium** na versão 26.3 com teste arquitetural dedicado (`GraphicOptimizersCompatibilityArchitectureTest`), expandindo a suíte para **482 testes automatizados** 100% aprovados.
23. `v1.3.7`: Implementação completa da **Fase 12: Xenobotânica, Agricultura Hidropônica & Ciclos de Carbono** (`44ff438`): Câmara Hidropônica Pressurizada (`hydroponic_chamber`) com boost de cúpula de terraformação e energia sem fio WPT, Sementes e Bloco de Grama Xeno-Adaptada (`xeno_grass_seeds` / `xeno_grass_block`) com fixação biológica de nitrogênio e conversão em solo fértil, Cactos de Seiva Pesada (`heavy_sap_cactus`), Seringa de Coleta (`sampling_syringe`), Frasco de Seiva Pesada (`heavy_sap_bottle`), Biopolímeros Flexíveis (`flexible_biopolymer`), Biorremediação de Solos com Areia Salinizada (`salinized_sand`) e Plantas Halófitas (`halophyte_plant`) em 4 estágios de desenvolvimento, além da expansão da suíte para **500 testes automatizados** com 100% de aprovação.
24. `7138892`: Redesign completo do Traje Espacial (`space_suit`) com geometria 3D imponente (ombreiras angulares, tanques duplos de oxigênio traseiros e placas de reforço) na paleta industrial dark-tech (`#121820`), com renderização em duas camadas e visor holográfico com brilho emissivo ciano (`#00E5FF`).
25. `2d273b4`: Implementação da Torreta Sônica Automatizada (`autonomous_sonic_turret`) com renderização 3D, alcance de 20 blocos, dano sônico e repulsão física, interface HUD em tela cheia com barra de pesquisa em tempo real, grid de cards de seleção de alvos de mobs/monstros, modos Whitelist/Blacklist, estratégias de mira por I.A., e expansão para **509 testes automatizados** com 100% de sucesso.
26. `6e02721`: Documentação e planejamento da Fase 18 (Fortificações Perimétricas, Muralhas de Espinhos e Contra-Medidas Físicas), atualização das métricas e log de commits.
27. `d341188`: Resolução de crash de inicialização do cliente Fabric (`NoSuchElementException: Can't find part hat`), corrigindo o aninhamento da parte `hat` como filha direta de `head` no `SpaceSuitArmorModel` conforme a hierarquia do `HumanoidModel` do Minecraft, adição de testes de regressão dedicados (`SpaceSuitArmorModelTest`), validação das regras arquiteturais de zero comentários e zero imports inline, e expansão da suíte para **512 testes automatizados** com 100% de sucesso.
28. `478e79a`: Implementação e validação da **Fase 20: Preset de Mundo Dedicado & Desacoplamento do Vanilla ("Mundo" -> "Tipo de mundo: SandStorm")**:
    - Criação do preset oficial `sandstorm:desert_planet` (`data/sandstorm/worldgen/world_preset/desert_planet.json`) e noise settings (`data/sandstorm/worldgen/noise_settings/desert_planet.json`).
    - Registro na tag `minecraft:worldgen/world_preset/normal` para inclusão no seletor da aba "Mundo" da tela de criação (`CreateWorldScreen` / `WorldCreationUiState`).
    - Mixin client `SandStormWorldPresetSelectionMixin` para pré-seleção automática por padrão ao abrir a tela de criação.
    - Eliminação completa dos overrides forçados nos arquivos vanilla (`dimension/overworld.json`, `world_preset/normal.json`, `worldgen/noise_settings/overworld.json`), preservando 100% da integridade de mundos vanilla normais.
    - Suíte de testes dedicada `SandStormWorldPresetTest` e atualização de `SandStormWorldGenTest`, expandindo a suíte para **522 testes automatizados** com 100% de sucesso.
29. `0a0cc5a`: Implementação e validação da **Fase 13: Rede Logística de Dutos & Malha Energética WPT Expandida**:
    - Torre Retransmissora WPT de Longo Alcance (`wpt_relay_tower`) estendendo cobertura sem fio em até 128 blocos com arco voltaico e som ambiente eletromagnético via `WptRelayTowerManager`.
    - Dutos de Fluidos Inteligentes (`smart_fluid_pipe`) com 6 conexões direcionais dinâmicas, prevenção de refluxo e compatibilidade direta com a Fabric Transfer API (`FluidStorage.SIDED`).
    - Console Holográfico de Monitoramento (`grid_monitor_console` / `GridMonitorConsoleScreen`) com telemetria ao vivo de geração solar, térmica, torres, acumuladores, energia estocada e diagnóstico de estabilidade da grade energética.
    - Acumulador de Estado Sólido Industrial (`solid_state_accumulator` / `SolidStateAccumulatorScreen`) com capacidade de 500.000 J, modos AUTO, CHARGE e DISCHARGE (raio WPT de 48 blocos).
    - 4 novas receitas shaped data-driven, loot tables, modelos 3D com rotação isométrica para GUI e expansão da suíte para **533 testes automatizados** com 100% de aprovação.
30. `970b6f8`: Adição de assets de biomas, sons e texturas (vidro de fulgurito, âmbar fossilizado, juncos ancestrais, areia eletrizada).
31. `a0aa5f7`: Progressão de ferramentas de silício e manufatura aditiva no Capítulo 1: `tool_base`, `electric_component`, `silicon_pickaxe`, autorização no `TechnologyToolRestrictionHandler`, balanceamento do livro de receitas e expansão da suíte para **571 testes automatizados** (0 falhas).
32. `af25991`: Adição do Protocolo de Integridade de Texturas e Assets para Minecraft 1.21.4+ no `AGENTS.md`.
33. `490302e`: Resolução do crash crítico de renderização de linhas (`RenderTypes.LINES`) com `.setLineWidth(1.0f)` em `Printer3DBlockEntityRenderer`, `NaniteFabricatorBlockEntityRenderer` e `DesalinationFilterBlockEntityRenderer`.
34. `b2164a6`: Melhorias de ergonomia da interface (tooltip de slot de bateria, título de container `container.sandstorm.printer_3d` e notas detalhadas de uso da impressora 3D em português, inglês e espanhol).



