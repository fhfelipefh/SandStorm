# SandStorm - Quadro de Tarefas & Mensuração do MVP

Este documento consolida o andamento das fases de desenvolvimento do mod **SandStorm** para **Minecraft 26.3 (Fabric / Java 25)**, detalhando o que já foi concluído, as métricas de qualidade e o que resta para o fechamento do MVP.

---

## 📊 Métricas Gerais do Projeto

| Métrica | Valor Atual | Meta MVP | Status |
| :--- | :--- | :--- | :--- |
| **Versão Alvo** | Minecraft 26.3 | Minecraft 26.3 | ✅ Atingido |
| **Fabric Loader / API** | 0.19.5 / 0.160.7+26.3 | Compatibilidade Estável | ✅ Atingido |
| **Java SDK** | Java 25 | Java 25 | ✅ Atingido |
| **Testes Automatizados** | **877 testes** (0 falhas) | > 150 testes | ✅ Superado |
| **Rigor de Código (Zero Comentários)** | **0 linhas de comentários** | 0 linhas | ✅ 100% Auditado |
| **Arquitetura Desacoplada** | 100% isolamento de componentes | Zero acoplamento | ✅ Validado |
| **Receitas Data-Driven (JEI/REI)** | 120 receitas oficiais + Catálogo Interno de Projetos | Cobertura total | ✅ Concluído |
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
- [x] Impressora 3D de bancada (`Printer3DBlock`) com processamento via energia e catálogo expandido de manufatura aditiva:
  - [x] Ferramentas tecnológicas de campo: Escâner Geológico (`geological_scanner`), Sonda de Campo (`field_probe`) e Ferramenta de Reparo Tecnológico (`repair_tool`).
  - [x] Componentes estruturais e mecânicos: Placa Estrutural (`structural_plate`), Suporte de Circuito (`circuit_mount`) e Vedação de Pressão Hermética (`pressure_seal`).
  - [x] Matriz de componentes elétricos (`electric_component`), placas de circuito (`circuit_board`) e fuzil de plasma (`plasma_rifle`).
- [x] Fabricador de nanitas (`NaniteFabricatorBlock`) para criação de componentes avançados e lâmina vibratória de dente de verme (`vibro_crysknife`) com tempo calibrado de 60s (1200 ticks).
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
- [x] `AssetIntegrityTest`: Validação sintática de modelos JSON, integridade de cabeçalhos PNG e auditoria estrita contra ícones de barreira no JEI/livro de receitas (`everyItemDefinitionMustHaveACorrespondingItemTexture`).
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

### 🌋 Fase 14: Expedições Subterrâneas Profundas & Surto de Plasma Cósmico (Concluída - 100%)
- [x] **Cavernas de Quartzo Piezoelétrico (`piezo_caverns`)**:
  - [x] Geodos subterrâneos raros (Y: -30 a 10) repletos de cristais que vibram acusticamente ao sofrer impacto ou corte por laser (`PiezoQuartzBlock`, `BuddingPiezoQuartzBlock`, `PiezoQuartzClusterBlock`).
  - [x] Fonte de ressonadores piezoelétricos (`piezo_resonator`, `piezo_quartz_shard`) para tecnologia militar e sísmica avançada.
  - [x] Geração procedural subterrânea configurada via recursos de bioma e `SandStormWorldGen`.
- [x] **Perfuratriz Automática de Poço Profundo (`deep_core_drill`)**:
  - [x] Maquinário industrial de grande porte para extração contínua de fluidos fósseis pressurizados e minerais raros do manto planetário (`DeepCoreDrillBlock`, `DeepCoreDrillBlockEntity`).
  - [x] Sistema de armazenamento de energia (50.000 J a 50 J/t), tanque de fluidos embutido de 4.000 mB integrado à Fabric Transfer API (`Storage<FluidVariant>`), buffer de 8 slots de minérios e envase automático de baldes de fluidos fósseis (`pressurized_fossil_fluid_bucket`).
  - [x] Chassis industrial cyberpunk com interface holográfica (`DeepCoreDrillScreen`, `DeepCoreDrillMenu`) e monitoramento em tempo real.

### 🛡️ Fase 15: Tecnologia Militar de Plasma, Blindagens Exóticas & Defesa Orbital (Concluída - 100%)
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

### 🌪️ Fase 16: Biomas Extremos, Ventos Radioativos & Oásis Fósseis (Concluída - 100%)
- [x] **Ermos de Fulgurito Líquido & Dunas Vitrificadas (`fulgurite_wastes`)**:
  - Sub-bioma desértico hiper-radioativo formado por séculos de descargas iônicas contínuas, repleto de monólitos de vidro negro e areia eletrizada condutora.
- [x] **Oásis Fóssil Subterrâneo (`fossilized_oasis`)**:
  - Cavernas ocultas com remanescentes botânicos preservados em âmbar e fontes termais minerais, fonte de sementes ancestrais para terraformação definitiva.

### 🚂 Fase 17: Logística Maglev, Linhas Industriais & Domos Coloniais (Concluída - 100%)
- [x] **Sistema Ferroviário Magnético de Areia (`sand_maglev_rail`)**:
  - Trilhos de levitação magnética e vagões autônomos de alta velocidade para transporte expresso de minérios, fluidos e jogadores entre postos avançados distantes.
- [x] **Domo Residencial de Colonos (`habitat_dome`)**:
  - Módulos habitacionais pressurizados com controle atmosférico integrado, recicladores de oxigênio e camas de criostase para suporte de vida prolongado.
- [x] **Linha de Montagem Industrial Automatizada (`auto_assembly_line`)**:
  - Esteiras industriais e braços manipuladores robóticos para manufatura contínua sem necessidade de intervenção do jogador em receitas em cadeia.

### 🚧 Fase 18: Fortificações Perimétricas, Muralhas de Espinhos & Contra-Medidas Físicas (Concluída - 100%)
- [x] **Paredes com Espinhos de Titânio Balístico (`titanium_spike_wall`)**:
  - Painel modular de fortificação estrutural feito de liga pesada de titânio escuro (`#121820`) e pontas reforçadas de metal temperado.
  - **Dano Físico Contínuo Indiscriminado**: Qualquer entidade que encostar, colidir ou tentar atravessar (monstros invasores, outros jogadores ou o próprio operador da base desatento) sofre 6.0 pontos de dano cinético de perfuração por contato contínuo, além de desaceleração severa e efeito lacerante.
  - Suporte a posicionamento multidirecional: vertical como parede de barreira perimétrica, horizontal rente ao solo ou invertido em tetos de contenção.
  - Barreira física passiva imune a desgaste de baterias, ideal para defesa permanente de perímetros de assentamento.
- [x] **Paredes de Espinhos Retráteis Pneumáticos (`retractable_spike_wall`)**:
  - Bloco de contenção ativo dotado de pistões pneumáticos de alta pressão e estacas afiadas de perfuração.
  - Estado recolhido: As estacas permanecem alinhadas à superfície da parede, funcionando como bloco liso de passagem livre e inofensivo.
  - Ativação por Redstone/Sinal Lógico: Ejeção repentina e violenta das lâminas projetando-se em até 1.5 bloco para a frente.
  - **Dano Crítico de Empalamento & Repulsão**: Causa 14.0 de dano instantâneo de perfuração mecânica somado a alto recuo cinético (knockback), atingindo com letalidade qualquer criatura ou player (aliado ou inimigo) presente na área de projeção.
- [x] **Muralha de Espinhos Eletrizados de Alta Tensão (`electrified_spike_barrier`)**:
  - Barreira metálica condutora conectada à malha de energia sem fio (WPT) ou cabeamento elétrico direto.
  - **Dano Duplo (Perfuração Mecânica + Arco Voltaico Ciano)**: Ao encostar, descarrega energia elétrica de alta densidade (arco voltaico emissivo `#00E5FF`), consumindo 50 J por pulso de descarga.
  - Causa dano físico mais choque elétrico e paralisia temporária (Stun / Lentidão extrema IV).
  - Indiscriminado e de alto risco: Eletrocuta qualquer ser vivo desprotegido (jogadores necessitam de Traje Espacial com módulo de isolamento para não sofrerem eletrocussão letal).
- [x] **Muralha de Espinhos com Revestimento Bio-Corrosivo (`corrosive_chitin_spike_wall`)**:
  - Barreira avançada forjada no Fabricador de Nanitas com quitina afiada de verme (`sandworm_chitin`), biopolímeros flexíveis (`flexible_biopolymer`) e seiva pesada de cacto (`heavy_sap_bottle`).
  - **Dano de Corrosão Ácida e Degradação de Armaduras**: O contato com as pontas embebidas em ácido biológico inflige perfuração e o status "Corrosão Ácida", drenando vida ao longo do tempo e deteriorando aceleradamente a durabilidade da armadura equipada a cada tick.
  - Barreira altamente perigosa para contenção de espécimes hostis biológicos e dissuasão de invasores em ambientes desérticos.
- [x] **Armadilha de Espinhos de Chão Pressurizada (`kinetic_floor_spikes`)**:
  - Grelha embutida de piso camuflável nas texturas de arenito ou placas de titânio de bases.
  - Ativação sísmica por pressão: Ao detectar o peso de passos de qualquer entidade (mobs, outros players ou o próprio construtor), ejeta estacas verticais afiadas do assoalho.
  - Dano perfurante ascendente com foco nos membros inferiores, ignorando proteções convencionais que não sejam botas reforçadas do traje espacial.
- [x] **Portão Fortificado com Grades de Espinhos Esmagadores (`crushing_spike_gate`)**:
  - Portão industrial motorizado de contenção com fileiras de dentes pontiagudos de titânio.
  - Projetado para fechamento hermético de emergência em hangares, garagens de rovers e eclusas de ar.
  - Causa dano crítico esmagador e perfurante se fechar sobre qualquer entidade que esteja sob o vão no momento da descida.

### 🏗️ Fase 19: Construtor Autônomo de Megaestruturas, Drones Operários & Manufatura Holográfica 3D (Layer-by-Layer) (Concluída - 100%)
- [x] **Núcleo de Construção de Megaestruturas (`megastructure_constructor`)**:
  - Bloco tecnológico pesado de ancoragem no solo (geometria 2x2 com base em titânio escuro `#121820` e 4 pistões pneumáticos de fixação estrutural ao substrato rochoso).
  - Cúpula emissora holográfica central esculpida em cristal piezoelétrico lapidado, emitindo um cone vertical piramidal giratório de luz volumétrica ciano (`#00E5FF`) com anéis de giroscópio mecânicos animados em rotação contínua de telemetria.
  - Conexão nativa à malha de energia sem fio (WPT) com buffer de alta densidade de 500.000 J (`EnergyStorageComponent`), consumindo energia progressiva por bloco sintetizado e taxa contínua de upkeep dos campos holográficos e hangares.
  - Hangar de acoplamento automatizado com baias de recarga rápida e ancoragem magnética para esquadrilhas de 2 a 4 drones construtores (`builder_drone`).
  - Compartimento de insumos de 18 slots com compatibilidade bidirecional com a Fabric Transfer API (`ItemStorage.SIDED`), aceitando alimentação via esteiras automatizadas (`auto_assembly_line`), funis ou tubulações de abastecimento.
  - Máquina de estados operacionais sincronizada cliente-servidor:
    - `IDLE`: Sistema em espera aguardando seleção de esquemático ou inserção de materiais.
    - `CALIBRATING`: Varredura inicial e projeção do wireframe 3D completo da obra no mundo.
    - `BUILDING`: Drones operando ativamente em voo com litografia a plasma e assentamento de camadas.
    - `PAUSED`: Interrupção por falta de blocos específicos no inventário ou exaustão energética, projetando a silhueta da peça faltante em tom de alerta âmbar estroboscópico (`#FFB300`).
    - `COMPLETED`: Megaestrutura finalizada, recolhimento dos drones, emissão do pulso sísmico seguro permanente e acionamento do feixe triunfante.
- [x] **Drones Construtores Operários (`builder_drone`) & Dinâmica de Voo Aéreo**:
  - Nova entidade robótica aérea inteligente (`BuilderDroneEntity`) com chassi aerodinâmico angular industrial dark-tech (`#121820`) e linhas de contraste luminescentes ciano (`#00E5FF`).
  - **Sistema de Propulsão Quad-Ion com Efeitos Visuais**:
    - Quatro propulsores vetoriais basculantes com partículas volumétricas de exaustão de íons e iluminação emissiva dinâmica nos bocais.
    - Animação procedural de inclinação (pitch/roll/yaw) proporcional ao vetor de velocidade durante deslocamento entre o hangar e as coordenadas da obra.
    - Voo suave com interpolação inercial tridimensional (amortecimento suave de desaceleração ao se aproximar do ponto de assentamento).
  - **Garras de Contenção Magnética & Cabeça Litográfica**:
    - Sub-modelo articulado inferior dotado de duas pinças mecânicas magnéticas capazes de reter a miniatura 3D do bloco transportado durante a rota de voo.
    - Cabeçote central móvel com emissores laser ópticos duplos convergentes que se orientam automaticamente em direção à coordenada exata do bloco na camada ativa.
  - **I.A. Operária e Navegação em Esquadrilha**:
    - Distribuição paralela e autônoma de tarefas entre 2 a 4 drones ativos simultâneos num raio de até 48 blocos sem sobreposição de rotas de voo.
    - Altitude de cruzeiro de segurança ajustada dinamicamente acima do topo da camada mais alta já construída, prevenindo qualquer risco de colisão com a estrutura física em evolução.
    - Protocolo de retorno de emergência: se uma tempestade elétrica de areia (Ion Sandstorm) atingir o pico (`intensity >= 0.75`), os drones abortam manobras no céu aberto e recolhem-se imediatamente às baias pressurizadas do núcleo para evitar descargas catastróficas.
- [x] **Pipeline Gráfico do Feixe Litográfico de Plasma Contínuo & VFX de Solda (Laser Hazard)**:
  - **Renderização Volumétrica do Feixe Laser (`SubmitNodeCollector`)**:
    - Renderizado no cliente através de geometria cilíndrica de dupla camada (dual-layer) totalmente imune a quebras de pipeline e 100% compatível com Sodium e Iris Shaders:
      - *Núcleo Interno Superaquecido (Inner Core)*: Feixe cilíndrico concentrado ultra-brilhante branco-azulado (`#FFFFFF` a `#E0FFFF`) com emissão plena.
      - *Bainha Externa de Plasma (Plasma Sheath)*: Envoltório cilíndrico translúcido com brilho emissivo ciano intenso (`#00E5FF`) com opacidade oscilante senoidal.
    - Scroll UV vertical contínuo da textura de plasma a laser (`vOffset = -gameTime * 0.35f`), gerando a ilusão ótica de plasma fluindo em alta velocidade da ponteira do drone em direção ao bloco físico.
  - **Efeitos de Partículas & Solda Molecular no Ponto Focal**:
    - Erupção contínua de faíscas elétricas ciano e brasas incandescentes saltando parabolicamente a partir do ponto de impacto da fusão molecular na superfície do bloco.
    - Nuvem volumétrica sutil de fumaça ionizada e distorção ótica de calor (efeito de miragem térmica local) no entorno do ponto de litografia.
    - Efeito sonoro contínuo espacializado em loop de laser de fusão (`block.megastructure_constructor.laser`) com estalos de queima controlada e zumbido eletromagnético harmônico.
  - **Mecânica de Perigo e Dano Térmico Contínuo Indiscriminado (Laser Hazard)**:
    - Raycast tridimensional estrito entre a ponteira do drone e o ponto de fusão com caixa de colisão volumétrica em tempo real.
    - Qualquer entidade que cruzar o feixe laser, passar por baixo da linha de tiro ou colidir com o ponto focal de fusão molecular (monstros invasores, animais, outros jogadores ou o próprio arquiteto/operador desatento) sofre **8.0 pontos de dano térmico contínuo por segundo** (`DamageSource` tipo plasma/fogo), além de repulsão cinética suave.
    - Efeitos imediatos no alvo atingido: partículas de queimadura e faíscas avermelhadas de alta temperatura, fumaça preta saindo do corpo e ignição breve de chamas.
    - Permite o aproveitamento estratégico pelo jogador como armadilha ambiental defensiva de alto dano contra hordas invasoras que tentem se aproximar do canteiro de obras.
- [x] **Projeção Holográfica 3D em Tempo Real no Mundo (Holographic Blueprint Wireframe)**:
  - **Shader e Render Layer Customizada de Alta Performance**:
    - Renderização tridimensional no espaço do mundo utilizando render layers translúcidas aditivas do Minecraft/Fabric (`RenderTypes` com frustum culling acelerado), sem sobrecarga de draw calls.
    - Malha wireframe tridimensional completa da megaestrutura desenhada com linhas de neon ciano luminescente (`#00E5FF`).
  - **Efeitos Visuais de Holograma Sci-Fi de Alta Fidelidade**:
    - *Varredura de Scanline Vertical*: Linha horizontal de feixe luminoso mais intensa que sobe e desce ciclicamente por toda a extensão vertical da estrutura a cada 4 segundos.
    - *Respiração Senoidal de Opacidade*: Modulação harmônica da transparência da malha (entre 30% e 65%) orientada pelo tick do cliente (`Math.sin(gameTime * 0.06f)`), criando pulsação viva de energia.
    - *Destaque Luminescente da Camada Y Ativa*: A fatia horizontal exata em construção no momento recebe contornos reforçados em ciano elétrico brilhante com malha interna tracejada de alta intensidade luminescente.
    - *Interferência Estática Eletrostática*: Em caso de déficit energético ou rajadas ionizantes de tempestade de areia, a projeção holográfica exibe pequenas tremulações geométricas e falhas estáticas estroboscópicas momentâneas.
- [x] **Mecânica Construtiva Aditiva Layer-by-Layer (Fatia a Fatia Y) & Materialização**:
  - **Algoritmo Construtivo Rigoroso de Elevação**:
    - A megaestrutura é erguida estritamente de baixo para cima (`Y_min` até `Y_max`), impedindo que blocos superiores fiquem flutuando no ar sem sustentação ou fora da sequência de impressão física.
    - Dentro de cada camada horizontal Y, a construção percorre um trajeto concêntrico ordenado (do centro para as bordas ou em espiral contínua), assemelhando-se visualmente a uma monumental impressora 3D industrial em tempo real.
  - **Transição de Materialização Molecular em Três Estágios por Bloco**:
    - *Estágio 1 - Alinhamento (0% a 25%)*: O drone chega sobre a coordenada, trava os propulsores em hover e projeta o feixe laser inicial sobre a caixa wireframe ciano.
    - *Estágio 2 - Fusão Molecular (25% a 85%)*: A geometria sólida do bloco sobe progressivamente de baixo para cima com uma máscara translúcida de dissolução molecular e brasas de plasma percorrendo os vértices.
    - *Estágio 3 - Solidificação (100%)*: Encaixe físico instantâneo no mundo com disparo de um clarão de luz branca e micro-dispersão de faíscas azuis acompanhado de som seco de travamento estrutural.
  - **Celebração Harmônica por Nível Y Finalizado**:
    - Ao solidificar o último bloco da fatia Y corrente:
      - Disparo de acorde harmônico espacializado de telemetria (`megastructure_layer_complete`).
      - Anel de choque perimétrico: Uma onda de luz ciano viaja rapidamente por toda a borda externa da camada recém-concluída.
      - Feedback em tempo real na interface e na actionbar do jogador: `[I.A.T.I.] Camada Y={nivel} finalizada ({concluidos}/{total} blocos). Elevando plano de litografia para o próximo nível...`.
- [x] **Interface Gráfica Holográfica da Estação Construtora (`MegastructureConstructorScreen`)**:
  - **Design Cyberpunk / Dark-Tech Industrial**:
    - Tela em tela cheia com estética visual futurista imersiva: fundo grafite escuro (`#0D1117`), molduras em cinza titânio (`#161B22`) e linhas de dados em neon ciano (`#00E5FF`).
  - **Viewport Holográfico 3D Rotacionável Interativo (Miniatura ao Vivo da Obra)**:
    - Janela central de projeção 3D vetorial em tempo real onde o jogador pode inspecionar o esquemático completo da megaestrutura:
      - Rotação livre em 360° em todos os eixos através de clique e arraste com o botão esquerdo do mouse.
      - Zoom dinâmico com a roda de rolagem do mouse e reposicionamento de pan com o botão direito.
      - Diferenciação visual tridimensional por cores:
        - *Verde Ciano Vivo*: Blocos já assentados e solidificados no mundo.
        - *Amarelo Âmbar Pulsante*: Blocos pertencentes à camada Y ativa sendo trabalhada pelos drones.
        - *Azul Cobalto Translúcido*: Blocos futuros do esquemático ainda aguardando construção.
  - **Painel de Telemetria de Materiais & Gestão Energética**:
    - Gráfico radial circular de progresso geral de montagem (porcentagem de 0.0% a 100.0%).
    - Indicador de estabilidade da malha de energia sem fio (WPT) com leitura contínua: capacidade do buffer (500.000 J), taxa instantânea de consumo (J/t), carga solar/retransmissora recebida e status (`SURPLUS`, `BALANCED`, `DEFICIT`).
    - Matriz de contagem de materiais: Tabela com ícones 3D de cada tipo de bloco exigido pela estrutura, indicando a quantidade estocada no inventário do núcleo versus o total exigido para a obra completa e para a camada atual.
    - Sinalização de alertas: Em caso de falta de qualquer material específico, o ícone correspondente na matriz pulsa em vermelho vivo com tooltip informativo indicando a quantidade faltante para reabastecimento.
    - Controles de operação: Botões táteis com feedback sonoro: `[INICIAR CONSTRUÇÃO]`, `[PAUSAR MANUFATURA]`, `[DESMONTAR REVERSO]` e alternador de perfil operacional (`MODO ECONÔMICO: 1 drone, taxa reduzida` / `MODO INDUSTRIAL: 2 drones, taxa nominal` / `MODO OVERCLOCK: 4 drones, velocidade máxima e consumo 3x de energia`).
- [x] **Catálogo Monumental de Megaestruturas Nativas em Blueprints**:
  - **Cúpula Geodésica de Biosfera (`biosphere_dome`)**:
    - Dimensões: 25x25x13 blocos.
    - Arquitetura: Cúpula hemisférica geodésica hermética com nervuras poligonais de titânio escuro e vitrais em vidro temperado de fulgurito (`fulgurite_glass`) imunes a danos de tempestade.
    - Núcleo Interno: Solo fértil central com canais de irrigação de água potável, canteiros dedicados para culturas de xenobotânica (`xeno_grass_block`, `heavy_sap_cactus`, `halophyte_plant`), módulo de câmara hidropônica central e eclusa dupla perimétrica pressurizada com portas automáticas de contenção.
    - Sistema de Iluminação: Anel geodésico no ápice da cúpula com luminárias bio-UV emitindo luz solar sintética de nível 14 permanente.
  - **Cidadela Planetária de Fortificação (`planetary_citadel`)**:
    - Dimensões: 31x31x18 blocos.
    - Arquitetura: Fortaleza militar planetária monumental com muralhas perimétricas duplas, ameias de combate balístico, passadiços de patrulha e quatro torres de bastião nos cantos.
    - Plataformas de Armamento Pesado: O topo das quatro torres angulares vem pré-configurado com bases de ancoragem para montagem direta de Torretas Sônicas Automatizadas (`autonomous_sonic_turret`) e geradores de escudo cinético (`kinetic_shield_generator`).
    - Perímetro Físico Defensivo: Calçada externa guarnecida com fileiras duplas de paredes de espinhos de titânio (`titanium_spike_wall`), espinhos retráteis pneumáticos (`retractable_spike_wall`) e portões de grades esmagadoras (`crushing_spike_gate`).
    - Pátio Operacional: Hangar térreo amplo projetado para abrigo de rovers escavadores (`excavator_vehicle`), mechas de combate (`megazord`) e armazenamento seguro de lingotes e cartuchos de combustível.
  - **Silo & Plataforma de Lançamento Orbital (`orbital_launch_silo`)**:
    - Dimensões: 19x19x32 blocos (câmara subterrânea profunda e torre vertical de lançamento).
    - Arquitetura: Base de operações aeroespaciais dotada de silo cilíndrico de blindagem reforçada com anéis de titânio e concreto, poço inferior de deflexão e exaustão térmica de chamas e torre de umbilical externa (gantry) com braços mecânicos articulados para manutenção de foguetes e satélites.
    - Infraestrutura de Apoio: Tubulações inteligentes de fluidos pressurizados conectadas a tanques de propelente de alta pressão (`smart_fluid_pipe`), console de monitoramento da rede orbital, luzes estroboscópicas vermelhas de navegação aérea e sinalização industrial em faixas chevron pretas e amarelas.
  - **Pirâmide Tecnológica do Deserto (`desert_tech_pyramid`)**:
    - Dimensões: 29x29x15 blocos.
    - Arquitetura: Megálito escalonado de estética mística ancestral-cyberpunk, talhado em arenito lapidado com relevos geométricos e veios embutidos condutores de ouro e silício cristalino.
    - Reator Subterrâneo & Coletor Atmosférico: O ápice superior da pirâmide abriga um mastro coletor piezoelétrico coroado por uma cúpula de plasma que atrai raios e arcos voltaicos durante tempestades elétricas de areia (Ion Sandstorms), canalizando instantaneamente as descargas estáticas em pulsos massivos de energia elétrica pura para os acumuladores da base.
- [x] **Finalização Épica & Efeitos Audiovisuais Monumentais ("Eye-Candy")**:
  - **Clímax de Conclusão da Obra**:
    - No instante em que o drone assenta o último bloco físico da megaestrutura:
      - Disparo do acorde triunfante cósmico (`megastructure_complete.ogg`) audível em 64 blocos com reverberação atmosférica densa.
      - Dispersão de uma gigantesca onda de choque volumétrica esférica de partículas luminescentes ciano (`#00E5FF`) que se propaga do centro até além das muralhas da estrutura, dissipando instantaneamente neblinas e partículas de tempestade de areia na vizinhança.
      - Um feixe monumental de plasma emissivo ascende do topo da estrutura até o limite das nuvens (`BeaconBeam`), iluminando o céu do deserto com clarão ressonante.
      - Os drones construtores operários executam uma rotação em formação acrobática de celebração sobre a cúpula antes de aterrissarem em descida vertical sincronizada em suas baias de recarga no hangar.
  - **Consagração como Zona Sísmica Segura Permanente**:
    - O perímetro geográfico completo delimitado pela megaestrutura concluída é automaticamente incorporado ao registro do `SeismicSurvivalHandler` e do `KineticShieldTracker` como Zona Segura Definitiva.
    - Jogadores e maquinários operando no solo interior da megaestrutura ficam 100% imunes a detecção de passos sísmicos, ataques e tremores subterrâneos do Verme de Areia Colossal (`SandwormEntity`), consolidando o local como um verdadeiro santuário habitacional e civilizatório no planeta desértico.
- [x] **Validação Técnica, Suíte de Testes & Garantia Arquitetural**:
  - `Fase19MegastructureTest`: Validação do registro do bloco, propriedades de bloco, container menu, retenção de energia WPT, atributos de voo do drone, catálogo de blueprints e registro de safe-zone.
  - `SandStormSoundEventsTest`: Validação dos novos efeitos sonoros (`MEGASTRUCTURE_CONSTRUCTOR_LASER`, `MEGASTRUCTURE_LAYER_COMPLETE`, `MEGASTRUCTURE_COMPLETE`).
  - Conformidade inegociável com as diretrizes do [AGENTS.md](file:///c:/Users/fhgam/Documents/GitHub/SandStorm/AGENTS.md):
    - Rigor de Zero Comentários no código Java (`ZeroCommentsArchitectureTest`).
    - Rigor de Zero Imports Inline (`NoInlineImportsArchitectureTest`).
    - Rigor de Zero Imports Não Utilizados (`NoUnusedImportsArchitectureTest`).
    - Prevenção total de deadlocks no servidor (`ServerDeadlockPreventionArchitectureTest`).
    - Auditoria automatizada de integridade de texturas e modelos (`AssetIntegrityTest`).

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

### 🍄 Fase 21: Micologia de Extremófilos, Fitoquímica de Dunas & Biorreatores de Batelada (A Ciência Biológica) (Concluída - 100%)
*Baseada na biologia de organismos extremófilos da Terra (análogos aos do deserto de Atacama, fontes hipersalinas, zonas radioativas de Chernobyl e geleiras antárticas), esta fase introduz o cultivo científico de fungos e plantas para a extração de metabólitos secundários e biocompostos que sustentam a sobrevivência e a farmacologia tecnológica.*

- [x] **Fungo Melanizado Radiotrófico (`radiotrophic_mycelium`)**:
  - [x] *Fundamentação Terrestre*: Inspirado no *Cladosporium sphaerospermum* e *Cryptococcus neoformans*, fungos ricos em melanina que proliferam nas ruínas do reator de Chernobyl, utilizando a radiotrofia para converter radiação gama e ultravioleta em bioenergia química.
  - [x] *Mecânica de Cultivo*: Desenvolve-se na escuridão sob influência de solos condutores e campos eletrostáticos (`electrified_sand`, fulgurito ou durante tempestades elétricas de areia).
  - [x] *Composto Bioativo Extraído*: **Matriz de Melanina Radioprotetora (`radioprotective_melanin`)**, biopolímero pigmentar complexo com propriedades quelantes de metais pesados e alta atenuação de radiação ionizante.
  - [x] *Aplicação Farmacêutica*: Base bioquímica para ampolas de desintoxicação celular (`detox_ampoule`) e aditivo biológico para blindagens cerâmicas termais.
- [x] **Fungo Quitinolítico de Decomposição (`chitinolytic_fungus`)**:
  - [x] *Fundamentação Terrestre*: Inspirado em fungos entomopatogênicos e decompositores de artrópodes (*Beauveria bassiana*, *Metarhizium*), cujas enzimas quitinases quebram a carapaça externa de exoesqueletos para sintetizar quitosana de cadeia longa.
  - [x] *Mecânica de Cultivo*: Desenvolve-se e frutifica sobre arenito e carapaças de quitina de verme (`sandworm_chitin`).
  - [x] *Composto Bioativo Extraído*: **Solução Hemostática de Quitosana (`chitosan_extract`)**.
  - [x] *Aplicação Médica Real*: Na medicina de trauma militar terrestre (como os curativos Celox e HemCon), a quitosana carrega carga eletrostática positiva que atrai eritrócitos e plaquetas negativas, selando artérias e ferimentos graves em segundos. No SandStorm, é a base da **Bio-Espuma Coagulante (`biofoam_cartridge`)**.
- [x] **Líquen Crio-Xerófilo com Trealose (`cryo_xerophilic_lichen`)**:
  - [x] *Fundamentação Terrestre*: Inspirado em líquens antárticos (*Xanthoria elegans*) e no mecanismo de anidrobiose de tardígrados e plantas da ressurreição (*Selaginella lepidophylla*), que sobrevivem ao vácuo e à perda de 99% da água acumulando o dissacarídeo trealose.
  - [x] *Mecânica de Cultivo*: Espécie simbiótica que tolera o choque térmico diário entre 48°C de dia e -10°C de noite sobre rochas expostas e arenito árido.
  - [x] *Composto Bioativo Extraído*: **Concentrado de Trealose Anidrobiótica (`trehalose_sugar`)**, açúcar protetor celular que vitrifica o citoplasma celular sem permitir a formação de cristais líticos ou degradação de membranas.
  - [x] *Aplicação Farmacêutica*: Estabilizador osmótico para o Sérum Anti-Inercial (`grav_dampener_stim`) e preservante biológico de culturas celulares.
- [x] **Suculenta Halófita de Salmoura (`halophyte_succulent`)**:
  - [x] *Fundamentação Terrestre*: Inspirada na planta costeira *Salicornia* e na microalga halotolerante *Dunaliella salina*, que sobrevivem em salmouras hiper-salinas produzindo glicerol intracelular denso e antioxidantes carotenoides/betalaínas.
  - [x] *Mecânica de Cultivo*: Desenvolve-se em substratos estéreis saturados de sal mineral e salitre (`salinized_sand`), alimentada por água salobra bruta subterrânea.
  - [x] *Composto Bioativo Extraído*: **Glicerol Osmoprotetor & Betalaína (`osmolyte_glycerol`)**, fluido biológico endotérmico de alto ponto de ebulição e absorção térmica.
  - [x] *Aplicação Farmacêutica*: Precursor direto do Sérum Endotérmico Refratário (`endothermic_serum`) para regulação do traje espacial.
- [x] **Xerófita Alcaloide de Duna (`dune_ephedra`)**:
  - [x] *Fundamentação Terrestre*: Inspirada no gênero terrestre *Ephedra* (plantas perenes de desertos e estepes que sintetizam os alcaloides efedrina e pseudoefedrina, broncodilatadores e estimulantes do sistema nervoso simpático).
  - [x] *Mecânica de Cultivo*: Arbusto lenhoso rasteiro de dunas profundas com raízes axiais capazes de captar umidade microscópica condensada.
  - [x] *Composto Bioativo Extraído*: **Alcaloides Neuro-Ativos Purificados (`neuroactive_alkaloids`)**, ativadores adrenérgicos que aceleram a propagação sináptica e o tônus neuromuscular.
  - [x] *Aplicação Farmacêutica*: Componente fitoquímico ativo da Ampola Neuro-Adrenérgica (`adrenal_stim`) e do Estimulador Miomecânico (`myomer_stim`).
- [x] **Biorreator de Fermentação & Quimiostato (`bioreactor_vat`)**:
  - [x] Bloco de maquinário com câmara cilíndrica de vidro borossilicato iluminada, sensor de pH, agitador magnético estéril e termostato digital.
  - [x] Conexão bidirecional à Fabric Transfer API (`ItemStorage.SIDED`), aceitando injeção de insumos e extração de metabólitos purificados e devolução de frascos de vidro.
  - [x] Suporte à rede de energia sem fio WPT (10.000 J de capacidade, 10 J/tick) e queima de combustível no slot de bateria (redstone/redstone block).
  - [x] Permite fermentação submersa controlada em 5 receitas de caldo/metabólitos para os extremófilos:
    - *Meio Caldo-Quitosana*: Quitina de verme / Fungo Quitinolítico + seiva pesada / água potável -> colheita de `chitosan_extract`.
    - *Meio Caldo-Radiotrófico*: Micélio Radiotrófico / Areia eletrizada + pó de silício / água potável -> colheita de `radioprotective_melanin`.
    - *Meio Caldo-Osmótico*: Suculenta Halófita / Salitre + água salobra / água potável -> colheita de `osmolyte_glycerol`.
    - *Meio Caldo-Criogênico*: Líquen Crio-Xerófilo / Sal mineral + água potável -> colheita de `trehalose_sugar`.
    - *Meio Caldo-Adrenérgico*: Éfedra das Dunas / Sementes ancestrais + sal mineral / água potável -> colheita de `neuroactive_alkaloids`.
  - [x] Interface gráfica industrial moderna (`BioreactorVatScreen` / `BioreactorVatMenu`) com monitoramento de energia, progresso de fermentação, estado de processamento e conexão WPT.
- [x] **Comando de Plantação Linear da Flora e Fungos (`/sandstorm plants` / `/plantar`)**:
  - [x] Comando operacional para demonstração e cultivo rápido de todas as 9 espécies botânicas e fúngicas extremófilas do mod em estado maduro sobre seus respectivos substratos.
  - [x] Suporte aos modos linear contínuo com irrigação e passarela de arenito (`line` / `strip`) e campo agrícola em fileiras paralelas (`rows` / `field`).
  - [x] Aliases e atalhos completos: `/sandstorm plants`, `/sandstorm_plants`, `/sandstorm plant_field`, `/plantar`, `/plant_field`.
  - [x] Cobertura automatizada por testes unitários (`SandstormPlantCommandTest.java`).

### 💉 Fase 22: Bio-Farmacologia Tecnológica, Hipo-Injetores & Síntese Farmacêutica (Substituição de Poções Vanilla)
*Utilizando os compostos bioativos reais cultivados e purificados na Fase 21, o operador formula medicamentos de alta tecnologia para injeção estéril no traje espacial.*

- [x] **Hipo-Injetor Pneumático Portátil (`hypo_injector`)**:
  - [x] Dispositivo médico ergonômico em liga de titânio escuro com cartucho pneumático pressurizado reutilizável.
  - [x] Mecânica de acionamento instantâneo (0.2s): carrega ampolas/stims no inventário e injeta diretamente no sistema circulatório do operador sem necessidade de desequipar o capacete do traje espacial.
  - [x] Suporte a atalho tático de emergência (uso direto com a mão secundária ou tecla rápida configurável).
  - [x] Sistema de barramento sonoro com sibilo pneumático característico de ejeção a gás estéril (`item.hypo_injector.use`).
  - [x] Integração com o `SurvivalHudOverlay`: visor LED com indicador de doses restantes e telemetria de saturação metabólica.
- [x] **Formulação Científica das Ampolas & Stims (Substitutos de Poções)**:
  - [x] **Ampola Neuro-Adrenérgica (`adrenal_stim`)** *(Substitui Speed / Velocidade)*:
    - *Fórmula*: Alcaloides Neuro-Ativos (`neuroactive_alkaloids`) + Solução Salina Mineralizada (`mineral_salt`).
    - *Efeito Farmacológico*: Vasodilatação periférica e sobrecarga controlada dos servomotores dos membros inferiores por 3 minutos (+30% velocidade de movimento e +15% aceleração de sprint).
  - [x] **Bio-Espuma Coagulante Molecular (`biofoam_cartridge`)** *(Substitui Instant Health & Regeneration)*:
    - *Fórmula*: Solução Hemostática de Quitosana (`chitosan_extract`) + Biopolímeros Flexíveis (`flexible_biopolymer`) + Micro-nanitas médicos.
    - *Efeito Farmacológico*: A quitosana catiônica atrai as células sanguíneas formando um tampão gelatinoso instantâneo, selando hemorragias internas (cura imediata de 4 corações + regeneração acelerada de tecidos por 15s).
  - [x] **Estimulador Miomecânico de Torque (`myomer_stim`)** *(Substitui Strength / Força)*:
    - *Fórmula*: Alcaloides Neuro-Ativos (`neuroactive_alkaloids`) + Nanotubos de Carbono / Quitina de Verme (`sandworm_chitin`).
    - *Efeito Farmacológico*: Aumenta temporariamente o limiar contrátil muscular e o torque do exoesqueleto em +40% por 3 minutos, amplificando o dano de armas brancas e impacto cinético.
  - [x] **Sérum Endotérmico Refratário (`endothermic_serum`)** *(Substitui Fire Resistance / Resistência ao Fogo)*:
    - *Fórmula*: Glicerol Osmoprotetor (`osmolyte_glycerol`) + Seiva Pesada de Cacto (`heavy_sap_bottle`).
    - *Efeito Farmacológico*: Nanogel de altíssima capacidade endotérmica que satura as micro-câmaras do traje espacial, conferindo 5 minutos de imunidade total a chamas, radiação de plasma e insolação extrema no deserto.
  - [x] **Sérum Anti-Inercial Gravitacional (`grav_dampener_stim`)** *(Substitui Slow Falling & Jump Boost)*:
    - *Fórmula*: Concentrado de Trealose Anidrobiótica (`trehalose_sugar`) + Fragmento de Quartzo Piezoelétrico lapidado (`piezo_quartz_shard`).
    - *Efeito Farmacológico*: Solução eletrolítica que sintoniza as solas das botas magnéticas com micro-campos repulsores piezoelétricos: permite saltos de 2.5 blocos e desaceleração terminal suave em quedas de desfiladeiros.
  - [x] **Ampola de Desintoxicação Celular (`detox_ampoule`)** *(Substitui Leite / Antídoto de Venenos)*:
    - *Fórmula*: Matriz de Melanina Radioprotetora (`radioprotective_melanin`) + Água Potável Pura (`potable_water_bottle`).
    - *Efeito Farmacológico*: A melanina quelante sequestra moléculas de ácido e toxinas de verme de areia, restaurando a homeostase celular e purgando venenos, choque estático e náuseas em 1.0s.
  - [x] **Emulsão de Refração Óptica Furtiva (`stealth_nano_drape`)** *(Substitui Invisibility / Invisibilidade)*:
    - *Fórmula*: Biopolímeros Flexíveis + Partículas micronizadas de Vidro de Fulgurito (`fulgurite_glass`).
    - *Efeito Farmacológico*: Película metamaterial translúcida que curva feixes de luz ao redor do chassi do traje por 90 segundos, tornando o operador indetectável para radares e sensores visuais de criaturas da superfície.

### 🔬 Fase 23: Bancada de Modificação Molecular, Overclocks de Hardware & Nanocoatings (Substituição de Encantamentos) (Concluída - 100%)
*Substituição da Mesa de Encantamentos, Bigorna e livros mágicos por engenharia física de semicondutores, overclocks de firmware e nanocamadas estruturais.*

- [x] **Bancada de Modificação Molecular (`molecular_modifier`)**:
  - [x] Maquinário tecnológico de engenharia de precisão que substitui a Mesa de Encantamentos (`enchanting_table`) e a Bigorna (`anvil`) convencionais.
  - [x] Conexão à malha de energia sem fio WPT (consumo de 500 J por ciclo de calibração molecular).
  - [x] Interface gráfica modular cyberpunk com baia de ancoragem do equipamento e soquetes: 3 slots para microchips/overclocks de firmware + 1 slot para revestimento químico (nanocoating).
  - [x] Cabeçote laser litográfico embutido para gravação nanométrica em silício e titânio, eliminando penalidades cumulativas de custo de reparo ou mecânicas místicas de XP.
- [x] **Módulos de Hardware & Overclocks para Armas (Substitutos de Encantamentos de Combate)**:
  - [x] **Bobina Ressonadora de Alta Frequência (`vibro_resonator_module`)** *(Substitui Sharpness / Afiação)*:
    - Induz vibração molecular a 80.000 Hz na lâmina ou no bocal de disparo, fragmentando ligações atômicas de blindagens (+1.5 de dano por nível de módulo, Tiers I a V).
  - [x] **Emissor Térmico de Plasma (`thermal_plasma_emitter`)** *(Substitui Fire Aspect & Flame)*:
    - Superaquece o gume ou os projéteis disparados, incinerando matéria orgânica e derretendo armaduras biológicas no impacto.
  - [x] **Acelerador de Pulso Concussivo (`kinetic_focus_module`)** *(Substitui Knockback & Punch)*:
    - Dispara uma onda de choque pneumática no ponto de contato que repele e desestabiliza monstros e mechas a até 6 blocos de distância.
- [x] **Microchips de Otimização para Ferramentas de Mineração (Substitutos de Encantamentos de Mineração)**:
  - [x] **Núcleo de Cavitação Ultrassônica (`cavitation_frequency_core`)** *(Substitui Efficiency / Eficiência)*:
    - Sintoniza a frequência de impacto da Picareta de Silício com a densidade molecular da rocha, acelerando a taxa de extração em até 200%.
  - [x] **Desintegrador de Fase Atômica (`atomic_phase_disrupter`)** *(Substitui Silk Touch / Toque Suave)*:
    - Dissocia as ligações químicas sem fraturar o bloco, permitindo a extração perfeitamente íntegra de vidros de fulgurito, colmeias fósseis e clusters de quartzo piezoelétrico.
  - [x] **Espectrômetro de Ressonância Densimétrica (`spectrometric_sifter`)** *(Substitui Fortune / Fortuna)*:
    - Sensor espectrométrico microscópico que mapeia veios raros no ponto de impacto, maximizando o rendimento de silício, gemas piezoelétricas e carvão fóssil.
- [x] **Nanotecnologia Estrutural & Auto-Regeneração (Substitutos de Mending & Unbreaking)**:
  - [x] **Matriz de Nanorobôs Auto-Reparadores (`self_healing_nanite_matrix`)** *(Substitui Mending / Remendo)*:
    - Micro-nanites integrados à estrutura do equipamento que reconstroem microfissuras consumindo pacotes de energia da rede sem fio WPT ou fragmentos de sucata metálica.
  - [x] **Revestimento Diamantado de Titânio (`titanium_lattice_coating`)** *(Substitui Unbreaking / Inquebrabilidade)*:
    - Nanocamada de diamante sintético e titânio que triplica a rigidez estrutural, reduzindo a taxa de desgaste abrasivo das ferramentas em 75%.
- [x] **Placas de Chassi e Revestimentos de Blindagem para o Traje Espacial (Substitutos de Encantamentos de Armadura)**:
  - [x] **Grelha de Amortecimento Balístico (`ballistic_dampener_mesh`)** *(Substitui Proteção Geral / Protection)*:
    - Malha de dispersão de impacto que dissipa energia cinética por toda a área do chassi.
  - [x] **Blindagem Cerâmica Ablativa (`ablative_thermal_plating`)** *(Substitui Proteção contra Fogo)*:
    - Placas refratárias enriquecidas com melanina radiotrófica projetadas para absorver radiação solar extrema e chamas sem transferir calor ao operador.
  - [x] **Amortecedores Pneumáticos de Vácuo (`pneumatic_fall_dampers`)** *(Substitui Peso Pena / Feather Falling)*:
    - Pistões hidráulicos de desaceleração montados nos calcanhares das Botas Magnéticas, eliminando até 80% do impacto de pousos abruptos.
  - [x] **Placas de Descarga Reativa de Chassi (`reactive_shock_plating`)** *(Substitui Espinhos / Thorns)*:
    - Eletrodos perimétricos no traje que liberam um arco voltaico defensivo ciano contra agressores corpo a corpo, causando paralisia e choque elétrico.

### 🏥 Fase 24: Estação Médica Bio-Regenerativa (MedBay Pod) & Purificação Tecnológica do Vanilla (Concluída - 100%)
- [x] **Cápsula de Regeneração Celular MedBay (`bio_regeneration_pod`)**:
  - [x] Cúpula hermética horizontal de criostase e terapia intensiva com visor curvo de vidro de fulgurito temperado.
  - [x] Funcionalidade de internação (`MedBaySeatEntity`): O jogador entra na câmara para:
    - [x] Regeneração acelerada de saúde (cura completa e contínua de tecidos biológicos a cada 20 ticks).
    - [x] Descontaminação biológica e purga instantânea de venenos, náuseas, fraqueza, cegueira e lentidão com soluções de quitosana, trealose, melanina e bio-espuma.
    - [x] Recarga ultrarrápida dos tanques de oxigênio do traje espacial (100%), restauração de energia (100%) e estabilização térmica corporal exata em 37.0°C em `PlayerSuitSavedData`.
  - [x] Integração com a Fabric Transfer API (`FluidStorage.SIDED` e `ItemStorage.SIDED`): reservatório interno de 4000 mB de água, aceitando abastecimento via baldes/garrafas ou tubulações, slot de biomateriais estabilizadores e slot de bateria WPT.
  - [x] Animações de pressurização, névoa criogênica translúcida e telemetria holográfica com monitor ECG cardíaco animado em tempo real e medidor de fluidos na interface (`BioRegenerationPodScreen`).
- [x] **Purificação Tecnológica & Desativação Definitiva de Magia Vanilla**:
  - [x] Remoção e anulação no `RecipeManager` das receitas do Suporte de Poções (`brewing_stand`), Mesa de Encantamentos (`enchanting_table`), Bigorna (`anvil`, `chipped_anvil`, `damaged_anvil`) via arquivos em `data/minecraft/recipe/`.
  - [x] Interceptação de colocação e uso de blocos arcanos e desativação em tempo de execução via `MagicSuppressionHandler`.
  - [x] Prevenção de spawn de bruxas e entidades arcanas em desertos alienígenas hostis.
  - [x] Suíte de testes dedicada: `Fase24MedBayAndMagicSuppressionTest` com 14 testes cobrindo todas as mecânicas, elevando a suíte para **745 testes automatizados (100% de sucesso)**.

### 🦾 Fase 25: Bio-Cibernética Fundamental, Incubadora de Chassis & Núcleos Neurais Biônicos (Concluída - 100%)
*Fusão entre a biologia avançada de extremófilos (Fase 21), a farmacologia de estimulantes (Fase 22) e a robótica pesada: criação de tecidos sintéticos eletroativos, órgãos bio-refrigerantes e computação neural orgânica ("wetware") para dar vida a ciborgues industriais autônomos.*

- [x] **Incubadora Bio-Cibernética (`cyborg_incubator_vat`)**:
  - [x] Maquinário monumental 1x1x2 de biogestação e montagem molecular biomecânica em liga de titânio escuro e cúpula de vidro de fulgurito temperado.
  - [x] Conexão direta à malha de energia sem fio WPT (buffer de 25.000 J, consumo de 50 J/tick durante a bio-síntese) e à Fabric Transfer API (`FluidStorage.SIDED`, `ItemStorage.SIDED`).
  - [x] Câmara de perfusão hidrostática estéril contendo meio amniótico enriquecido com glicerol osmoprotetor (`osmolyte_glycerol`) e açúcar trealose anidrobiótico (`trehalose_sugar`) para manter células e miômeros viáveis durante a sinterização de conectores eletrônicos.
  - [x] Processo de montagem biomecânica sequencial (estágios de gestação do chassi):
    - *Estágio 1 - Endoesqueleto*: Chassi Esquelético de Titânio-Quitina (`biomechanical_chassis_frame`).
    - *Estágio 2 - Atuação Muscular*: Injeção de Feixes de Miômeros Artificiais (`synthetic_myomer_bundle`).
    - *Estágio 3 - Sistema Nervoso Autônomo*: Acoplamento do Núcleo Neural Biônico ("Wetware" AI Core - `bio_neural_core`).
    - *Estágio 4 - Homeostase Circulatória*: Conexão de Cânister de Hemolinfa Bio-Refrigerante (`bio_coolant_canister`).
  - [x] Interface gráfica cyberpunk imersiva (`CyborgIncubatorScreen` / `CyborgIncubatorMenu`) com silhueta holográfica do ciborgue sendo sintetizado, monitor de compatibilidade tecidual celular, barra de perfusão líquida e botão de ativação de firmware neural.
  - [x] Efeitos visuais e acústicos volumétricos no mundo: iluminação ciano estroboscópica, bolhas de oxigenação em ascensão no fluido e sibilo de pressurização hidrostática (`block.cyborg_incubator.loop`).
- [x] **Componentes Biológicos & Cibernéticos Fundamentais**:
  - [x] **Feixes de Miômeros Artificiais Eletroativos (`synthetic_myomer_bundle`)**:
    - Fibras musculares sintéticas de polímeros eletroativos dopados com nanotubos de carbono, extrato de quitosana (`chitosan_extract`) e estimulador de torque (`myomer_stim`).
    - Mecânica de contração eletro-induzida em milissegundos sem folga mecânica ou engrenagens vulneráveis à areia, entregando torque brutal para mineração e transporte de materiais.
  - [x] **Núcleo Neural Biônico de Processamento ("Wetware" Core - `bio_neural_core`)**:
    - Processador biocomputacional não-binário: redes vivas de micélio radiotrófico cultivado (`radioprotective_melanin`) integradas a micro-canais de silício e banhadas em alcaloides neuro-ativos (`neuroactive_alkaloids`).
    - Oferece tomada de decisão autônoma instantânea, algoritmo de pathfinding tridimensional heurístico adaptativo (desvio de precipícios, areia movediça e tempestades de areia) e independência operacional sem sobrecarga de CPU do servidor Minecraft.
  - [x] **Cânister de Hemolinfa Bio-Refrigerante (`bio_coolant_canister`)**:
    - Fluido de circulação ciano luminescente derivado de glicerol osmoprotetor, biopolímeros flexíveis e água potável desmineralizada.
    - Dissipa calor dos feixes de miômeros e impede superaquecimento do núcleo neural sob a temperatura ambiente de 48°C do deserto.
  - [x] **Chassi Esquelético de Compósito Titânio-Quitina (`biomechanical_chassis_frame`)**:
    - Endoesqueleto articulado fabricado em compósito de titânio e quitina (`titanium_chitin_composite`), projetado com canaletas internas seladas de cabeamento e fixadores angulares de miômeros.
- [x] **Estética Visual Dark-Tech & Biomecânica de Alto Impacto**:
  - [x] Contraste visual marcante entre blindagem metálica cinza/titânio escuro fosco (`#1E232A`), feixes musculares carmesim/fibrosos expostos nas articulações (joelhos, cotovelos, vértebras) e tubulações translúcidas pulsando com fluido ciano fluorescente (`#00E5FF`).
  - [x] Cabeçote robótico com visor óptico emissivo adaptativo que muda dinamicamente de tom conforme o estado operacional da entidade:
    - *Ciano (`#00E5FF`)*: Operação normal / minerando / construindo / colhendo.
    - *Âmbar (`#FF9100`)*: Alerta / em trânsito logístico / nível baixo de bateria WPT.
    - *Vermelho (`#FF1744`)*: Ameaça detectada / protocolo defensivo engajado.
    - *Roxo (`#D500F9`)*: Sincronização em rede de enxame (Swarm Intelligence).

### 🤖 Fase 26: Ciborgues Especialistas Autônomos & Uplink de Comando Holográfico (Concluída - 100%)
*Criação de três entidades ciborgues especializadas que executam autonomamente diretrizes espaciais complexas sob comando de um transmissor holográfico de mão.*

- [x] **Transmissor Holográfico de Comando (`cybernetic_command_uplink`)**:
  - [x] Dispositivo portátil ergonômico com terminal tático, telêmetro laser de alta precisão e antena de uplink neural.
  - [x] Mecânica de demarcação volumétrica holográfica 3D (Bounding Boxes tridimensionais neon projetadas no mundo):
    - [x] *Modo Mineração (Volume Ciano `#00E5FF`)*: O jogador clica em dois vértices opostos no mundo para delimitar um cubo ou poço de escavação (ex: 8x8x16 blocos ou galeria horizontal).
    - [x] *Modo Construção (Volume Âmbar `#FF9100`)*: O jogador ancora um blueprint virtual do Datapad ou demarca uma zona para reparo estrutural automático pós-tempestade.
    - [x] *Modo Agrícola / Coleta (Volume Esmeralda `#00E676`)*: Demarca o perímetro de canteiros e fazendas de extremófilos para monitoramento agronômico autônomo.
  - [x] Clique com botão direito no ciborgue abre a **Interface de Telemetria Biônica**:
    - [x] Monitoramento em tempo real de carga energética WPT (Joules), integridade muscular dos miômeros (%), nível de bio-refrigerante, inventário interno (18 slots) e seletor de rotina: `Trabalho Autônomo na Zona`, `Seguir Operador`, `Patrulhar Perímetro` e `Retornar à Doca`.
- [x] **Ciborgue Minerador de Subsolo (`cyborg_excavator` / Excavator Cyborg)**:
  - [x] Chassi reforçado bípede de perfil robusto, pernas articuladas de alta tração com botas magnéticas e braço direito fundido a uma broca de vibro-cavitação atômica com ponta de diamante sintético.
  - [x] Sensor de peitoral com escâner geológico miniaturizado que emite pulsos de sonar sísmico no solo.
  - [x] Comportamento Autônomo Inteligente:
    - [x] Desloca-se autonomamente até a Zona de Mineração ciano demarcada pelo jogador.
    - [x] Perfura blocos de cima para baixo ou em galerias seguras com rampas e escadarias, evitando colapsos de teto, fontes de lava e fossos de vermes de areia.
    - [x] Coleta seletiva: filtra minérios de alto valor (silício, quartzo piezoelétrico, carvão fóssil, ferro) e descarta cascalho/arenito estéril se configurado pelo jogador.
    - [x] Retorno de Segurança: Ao encher o compartimento interno ou quando a energia WPT cai abaixo de 15%, interrompe a perfuração, emite sinal sonoro de telemetria e caminha de volta à base para descarregar em baús/dutos e recarregar.
- [x] **Ciborgue Construtor Biônico (`cyborg_builder` / Builder Cyborg)**:
  - [x] Chassi ágil com dois braços multi-articulados: braço esquerdo com manipulador de campo magnético para transporte e sustentação de blocos pesados, e braço direito com tocha de solda molecular e fusão a laser.
  - [x] Mochila dorsal compacta de armazenamento de materiais integrada à Fabric Transfer API.
  - [x] Comportamento Autônomo Inteligente:
    - [x] Conecta-se à Zona de Construção demarcada pelo jogador e analisa a lista de blocos requeridos pelo projeto/blueprint.
    - [x] Localiza insumos nos baús vinculados da base, transporta as cargas e constrói de forma aditiva ordenada de baixo para cima (layer-by-layer), projetando feixes laser e faíscas de solda azuis (`entity.cyborg.weld`).
    - [x] Rotina de Manutenção Pós-Tempestade: Se tempestades severas de areia causarem erosão ou danificarem muralhas de espinhos, cúpulas e baterias, sai autonomamente da doca com placas de titânio para efetuar reparos estruturais.
- [x] **Ciborgue Coletor & Agrônomo Fúngico (`cyborg_harvester` / Harvester Cyborg)**:
  - [x] Chassi leve e veloz com garras cirúrgicas retráteis de micro-precisão e cesto traseiro selado hermeticamente para transporte estéril de biomassa e compostos bioativos.
  - [x] Visor óptico dotado de espectrômetro fitossanitário para leitura de estágios de crescimento, umidade e maturação química.
  - [x] Comportamento Autônomo Inteligente:
    - [x] Patrulha continuamente plantações de extremófilos (Micélio Radiotrófico, Fungo Quitinolítico, Suculenta Halófita, Éfedra das Dunas e cactos de seiva pesada).
    - [x] Colhe apenas plantas e fungos que atingiram o estágio final de maturação, preservando a raiz do solo e replantando esporos/sementes imediatamente no mesmo bloco.
    - [x] Transporta os metabólitos colhidos (`radioprotective_melanin`, `chitosan_extract`, etc.) diretamente aos Biorreatores de Batelada (`bioreactor_vat`) da base, descarregando nos slots de insumo para garantir alimentação ininterrupta do pipeline bio-farmacológico.
    - [x] Varredura de Recursos no Deserto: Coleta automaticamente areia de fulgurito residual pós-raios e cascas de quitina de vermes abatidos nas proximidades da base.

### 🌐 Fase 27: Enxame Cibernético (Swarm Intelligence), Doca de Recarga & Módulos de Upgrade (Concluída - 100%)
*Infraestrutura industrial de suporte contínuo: plataformas automatizadas de ancoragem, coordenação de enxame distribuído sem colisões e módulos de aprimoramento bio-mecânico.*

- [x] **Doca de Manutenção e Recarga WPT (`cyborg_docking_station`)**:
  - [x] Plataforma pesada 1x1x1 de solo em titânio e cerâmica piezoelétrica com conector indutivo de energia sem fio WPT e pinças pneumáticas de retenção.
  - [x] Protocolo Automático de Acoplamento:
    - [x] O ciborgue que adentra a doca é acoplado automaticamente, registrando-se no terminal da doca.
    - [x] Recarga ultrarrápida da bateria interna (500 J/tick) via acoplamento ressonante à malha WPT da base (buffer de 100.000 J).
    - [x] Despejo instantâneo de itens do inventário interno para baús, armários ou tubulações de fluidos inteligentes conectados à doca (`ItemStorage.SIDED`).
    - [x] Injeção de bio-refrigerante de hemolinfa e estabilização de integridade celular muscular dos miômeros.
  - [x] Emissão de sinal redstone comparador proporcional ao nível de energia e carga armazenada no capacitor da doca.
  - [x] Interface Gráfica Cyberpunk Industrial (`CyborgDockingStationScreen` / `CyborgDockingStationMenu`): monitor com barra de capacitor WPT, indicador luminoso de acoplamento com telemetria do ciborgue (Joules, miômeros %) e botão tátil com feedback sonoro para forçar desacoplamento manual imediato.
- [x] **Inteligência de Enxame Distribuída (Swarm Coordination Protocol & Voxel Mutex)**:
  - [x] Algoritmo em `CyborgSwarmManager`: coordenação thread-safe de múltiplos ciborgues especialistas simultâneos.
  - [x] Sistema de reservas espaciais de coordenadas (Voxel Mutex Locking): impede que dois mineradores escavem o mesmo bloco ou que dois construtores tentem assentar o mesmo tijolo simultaneamente (`claimBlock`, `releaseBlock`, `isClaimed`).
  - [x] Registro global e descoberta automatizada de docas (`registerDock`, `unregisterDock`, `findNearestAvailableDock`), permitindo que ciborgues em trânsito localizem o ponto de recarga mais próximo no mundo.
  - [x] Liberação preventiva de travas de coordenadas (`releaseAll`) na remoção ou descarte de entidades ciborgues, eliminando deadlocks de mundo.
- [x] **Módulos de Upgrade Biônico Intercambiáveis (`CyborgUpgradeItem`)**:
  - [x] 4 Componentes de personalização de alta fidelidade visual instaláveis nos ciborgues especialistas:
    - [x] *Blindagem de Quitina Ácida (`acid_chitin_plating`)*: Reveste o chassi com carapaça tratada em secreção de larva de verme (+20 de vida, resistência a ácido e reflexão cinética de dano).
    - [x] *Célula Criogênica de Trealose (`cryo_trehalose_cell`)*: Criocélula anidrobiótica que reduz o consumo e evaporação de bio-refrigerante de hemolinfa em 50%.
    - [x] *Lente Óptica LiDAR de Longo Alcance (`long_range_lidar_lens`)*: Dobra o raio operacional de busca de blocos e navegação de 16 para 32 blocos.
    - [x] *Propulsor de Levitação Piezoelétrica (`piezo_hover_thruster`)*: Micro-propulsores iônicos nos pés magnéticos conferindo passo de 1 bloco completo (`step_height`) e +30% de velocidade de deslocamento suave sobre o relevo das dunas.
  - [x] Dock Lateral de Upgrades no `CyborgTelemetryScreen`: painel de diagnóstico acoplado ao lado direito do chassi, renderizando badges neon em tempo real para cada um dos 4 upgrades instalados, com bordas iluminadas, identificadores e tooltips detalhados ao passar o mouse.
- [x] **Cadeia Completa de Assets 1.21.4 & Receitas Data-Driven**:
  - [x] Blockstate e modelos ativo/inativo para a Doca (`cyborg_docking_station`).
  - [x] Modelos de itens e definições em `assets/sandstorm/items/` para a Doca e os 4 Upgrades.
  - [x] 5 Texturas PNG pixel-art de alta fidelidade (top, top_active, side, bottom e texturas de itens 16x16 com assinaturas válidas).
  - [x] 5 Receitas shaped balanceadas em `data/sandstorm/recipe/` e tabela de saque para a doca.
  - [x] Paridade trilingue de localização (`pt_br.json`, `en_us.json`, `es_es.json`).
- [x] **Validação Automatizada de Testes**:
  - [x] Suíte de testes dedicada `Fase27SwarmAndDockingTest`, elevando o total do repositório para **855 testes automatizados** com 100% de sucesso e zero comentários.

### 🛰️ Fase 28: Torre de Projeção Holo-Tática & Matriz Neural Coletiva (Hivemind Holo-Tactical Spire & Neural Mesh) (Concluída - 100%)
*A apoteose visual e estratégica da automação no SandStorm: torre de telecomunicações de grande escala com projeções holográficas volumétricas em tempo real sobre o deserto, interface tática RTS para o enxame, uplink neural de telemetria com visão em primeira pessoa dos ciborgues e monitoramento orbital de tempestades iônicas.*

- [x] **Torre de Projeção Holo-Tática (`holo_tactical_spire`)**:
  - [x] Estrutura vertical imponente com antena ressonante de titânio escuro e emissor óptico holográfico ciano neon no topo.
  - [x] Capacitância interna WPT de 100.000 J e integração à Fabric Transfer API (`ItemStorage.SIDED`).
  - [x] **Projeção Holográfica Volumétrica no Mundo (In-World Hologram Projection)**:
    - [x] Renderizador client dinâmico projetando feixes de laser azul/ciano translúcido (`#00E5FF`) girando lentamente sobre a torre com anéis pulsantes no ar.
    - [x] Marcadores holográficos verticais no terreno indicando em tempo real as coordenadas dos ciborgues ativos no setor, docas conectadas e perímetro de trabalho das zonas demarcadas.
- [x] **Console de Comando Tático do Enxame (RTS Holo-Tactical Interface)**:
  - [x] Interface visual ultra-estilizada (`HoloTacticalSpireScreen` / `HoloTacticalSpireMenu`):
    - [x] Radar topográfico vetorial em tempo real com grade de coordenadas cartesianas (X, Z), renderizando o centro da torre, os ciborgues representados por glifos geométricos com cores de status e raio de alcance.
    - [x] Painel de Diretrizes Macro-Estratégicas do Enxame (Broadcast de Ordens Coletivas com 1 clique):
      - *Convergência Tática (`CONVERGE_AT_TARGET`)*: Todos os ciborgues livres convergem para uma coordenada específica com visor ciano intenso.
      - *Alerta Vermelho Sísmico (`SEISMIC_ALERT_EVACUATE`)*: Ao detectar aproximação do Verme de Areia Colossal, soa sirene de alarme na base e força evacuação e recolhimento imediato de todo o enxame para as docas seguras.
      - *Otimização Coordenada em Grade (`OPTIMAL_COORDINATED_WORK`)*: Distribuição inteligente de tarefas entre escavadores, construtores e coletores para máxima eficiência produtiva sem cruzamento de rotas.
      - *Standby de Manutenção (`STANDBY_HOLD_POSITION`)*: Pausa imediata de consumo de energia e congelamento de posição para inspeção do operador.
- [x] **Módulo de Interface Neural ("Cerebral Synapse Link" - `neural_synapse_link`)**:
  - [x] Item acoplável ao capacete do traje espacial, estabelecendo conexão neural direta com a matriz do enxame.
  - [x] Feedback tátil e telemetria no HUD: alertas na tela quando qualquer ciborgue sofrer dano, ficar preso ou esgotar sua bateria/bio-refrigerante.
- [x] **Sonda de Reconhecimento Orbital de Baixa Altitude (`orbital_recon_probe`)**:
  - [x] Sonda descartável acionada na Torre Holo-Tática que é ejetada verticalmente rumo à estratosfera.
  - [x] Varredura atmosférica remota: prevê a chegada de tempestades de areia iônicas com 5 minutos de antecedência e detecta bolsões subterrâneos de silício e ruínas soterradas num raio de 128 blocos, projetando as coordenadas no mapa da Torre.
- [x] **Efeitos Visuais e Sonoros de Alta Fidelidade ("Eye-Candy")**:
  - [x] Texturas emissivas detalhadas, partículas de feixes de laser holográficos no mundo, anéis ressonantes no topo da torre e sons futuristas de telemetria, ativação de uplink e alarme de evacuação.
  - [x] Suíte de testes dedicada: `Fase28HoloTacticalSpireTest`.

### 🧬 Fase 29: Clonagem Quântica do Jogador, Cápsulas de Estase ("Quantum Sleeper Pods"), Transferência de Consciência ("Ego-Casting") & Respawn por Proximidade (Concluída - 100%)
*A superação definitiva da fragilidade biológica e da morte no planeta inóspito: o operador transcende o corpo físico através de câmaras de estase biológica conectadas em rede quântica, permitindo o teletransporte instantâneo de sua consciência ("Ego-Casting") entre bases distantes e o despertar automático no clone mais próximo em caso de óbito.*

- [x] **Cápsula de Estase e Gestação Biológica (`quantum_sleeper_pod` / `QuantumSleeperPodBlockEntity` / `QuantumSleeperPodBlock`)**:
  - [x] Maquinário bio-quântico de 100.000 J conectado à malha WPT sem fio, com câmara de estase para corpos clonados e propriedades `FACING`, `ACTIVE`, `OCCUPIED`.
  - [x] Sistema de nutrição biológica e bio-síntese celular consumindo metabólitos da Fase 21 (quitosana, trehalose, glicerol e água potável) para gestação orgânica do clone.
  - [x] 42 slots dedicados de armazenamento físico por cápsula: inventário completo isolado (36 slots), armadura (4 slots), mão secundária (1 slot) e slot de insumos biológicos (1 slot). Suporte total à Fabric Transfer API (`ItemStorage.SIDED`).
- [x] **Matriz de Consciência Quântica (`quantum_mind_matrix`)**:
  - [x] Componente tecnológico de alta densidade manufaturado com matriz de silício, circuitos de liga titânio-quitina e neuro-alcaloides, permitindo o emparelhamento sináptico com a malha quântica.
- [x] **Rede de Consciência Quântica Persistente (`CloneNetworkSavedData`)**:
  - [x] Persistência de dados mundiais conforme o padrão moderno 1.21.4 (`SavedDataType` com `RecordCodecBuilder`), mantendo o mapeamento de cápsulas registradas por UUID de jogador e dimensão.
  - [x] Algoritmo de busca euclidiana de proximidade (`findNearestReadyPod`) e busca de alvo de transferência remota (`findTargetPodForTransfer`).
- [x] **Mecânica de Transferência de Consciência ("Ego-Casting")**:
  - [x] Teletransporte instantâneo da consciência do jogador entre corpos físicos mantidos em cápsulas conectadas à rede.
  - [x] Isolamento estrito de inventário físico: ao transferir a mente, todo o inventário atual (itens, armadura, offhand) permanece armazenado na cápsula de partida com o corpo adormecido, enquanto o corpo receptor descarrega seu inventário específico para o jogador, eliminando qualquer duplicação de itens.
- [x] **Protocolo de Respawn de Emergência por Proximidade (`FusedSpaceSuitHandler`)**:
  - [x] No evento de morte do jogador (`ServerPlayerEvents.AFTER_RESPAWN`), o sistema localiza a cápsula com clone pronto fisicamente mais próxima das coordenadas de óbito. O jogador reanima diretamente na cápsula mais próxima com os equipamentos que estavam nela pré-equipados.
- [x] **Interface Gráfica Cyberpunk (`QuantumSleeperMenu` / `QuantumSleeperScreen`)**:
  - [x] Monitor de telemetria com medidor vertical de energia WPT ciano, medidor de bio-nutrientes âmbar, status de ocupação do clone e botões de ação ("Gestar Clone" e "Transferir Consciência").
- [x] **Comandos de Debug e Teste Automatizado (`SandstormDebugCommand`)**:
  - [x] `/sandstorm debug phase 29` (ou `/sandstorm_debug phase 29`): Entrega de kit completo de clonagem.
  - [x] `/sandstorm debug setup clone_facility`: Geração automatizada de complexo laboratorial com duas cápsulas operacionais interligadas (Pod Alpha e Pod Beta) para teste imediato de ego-casting.
- [x] **Validação Automatizada de Testes**:
  - [x] Suíte de testes dedicada `Fase29QuantumCloningTest` e ampliação de `SandstormDebugTest`, com 100% de aprovação e zero comentários.

### 🛡️ Fase 30: Domo de Escudo de Plasma Planetário, Canhão Cinético Anti-Titã & Grade Acústica Perimétrica (Planetary Plasma Defense Grid & Anti-Titan Kinetic Railgun) (Concluída - 100%)
*A fortificação defensiva terminal contra os perigos cósmicos e cataclismos do planeta deserto: proteção de colônias inteiras contra projéteis e incursões hostis com cúpula de plasma magnético, canhão ferroviário hipersônico de alta energia anti-verme titânico e grade automatizada de cancelamento de ressonância sísmica.*

- [x] **Gerador de Escudo de Plasma Planetário (`plasma_shield_generator` / `PlasmaShieldGeneratorBlock` / `PlasmaShieldGeneratorBlockEntity`)**:
  - [x] Bloco industrial com reator de confinamento magnético em titânio e bobinas toroidais supercondutoras.
  - [x] Capacitância massiva de 1.000.000 J WPT, upkeep contínuo de 100 J/tick e raio de proteção ajustável de até 48 blocos.
  - [x] Deflexão e desintegração instantânea de flechas, bolas de fogo e projéteis balísticos externos.
  - [x] Repulsão cinética por choque de plasma causando 4.0 de dano e forte impulso em monstros invasores.
  - [x] Barreira de deflexão e ricochete contra colisões de Vermes de Areia Colossais (`sandworm`).
- [x] **Canhão Cinético Anti-Titã / Railgun Ferroviário (`kinetic_railgun` / `KineticRailgunBlock` / `KineticRailgunBlockEntity`)**:
  - [x] Artilharia pesada ferroviária com capacitor de 250.000 J WPT e compartimento de munição interna de 9 slots (`WorldlyContainer` / Fabric Transfer API).
  - [x] Sistema de mira e varredura automática em raio de 64 blocos com prioridade absoluta para Sandworms e ameaças hostis.
  - [x] Disparo hipersônico consumindo 5.000 J e 1 projétil `kinetic_slug`, desferindo 50.0 de dano com feixe elétrico e vetor de repulsão física acentuado.
- [x] **Pilão de Defesa Acústica Perimétrica (`acoustic_defense_pylon` / `AcousticDefensePylonBlock` / `AcousticDefensePylonBlockEntity`)**:
  - [x] Torre de amortecimento sísmico com 50.000 J de buffer e consumo operacional de 10 J/tick.
  - [x] Cobertura hemisférica de 32 blocos de raio com cancelamento acústico de ressonância sísmica.
  - [x] Integração concorrente com `AcousticDefenseTracker` e `SeismicSurvivalHandler`, anulando acúmulo de vibrações de passos, corrida e mineração que atraem o despertar de vermes.
- [x] **Componentes e Munições de Ponta**:
  - [x] Projétil Cinético Hiperdenso (`kinetic_slug`): munição penetrante forjada em liga titânio-quitina.
  - [x] Bobina Toroidal Supercondutora (`superconductor_toroid`): anel magnético supercondutor para aceleração de partículas e contenção de plasma.
  - [x] Cristal de Foco de Plasma (`plasma_focus_crystal`): difratador piezoelétrico para colimação do feixe energético.
- [x] **Interfaces Gráficas de Controle Tático**:
  - [x] `PlasmaShieldScreen` / `PlasmaShieldMenu`: telemetria de energia WPT, indicador de estado ativo/inativo, leitura de ameaças repelidas e seletor tátil de raio de cobertura.
  - [x] `KineticRailgunScreen` / `KineticRailgunMenu`: grade de munição 3x3, medidor de energia, status de resfriamento e contador de tiros disparados.
- [x] **Cadeia Completa de Assets 1.21.4 & Data-Driven Recipes**:
  - [x] 3 blockstates, 6 modelos de bloco com estados ativos/lit, 6 modelos de item e 6 definições em `assets/sandstorm/items/`.
  - [x] 17 texturas PNG pixel-art de alta fidelidade com assinaturas binárias válidas (`89 50 4E 47 0D 0A 1A 0A`).
  - [x] 6 receitas de fabricação balanceadas em `data/sandstorm/recipe/` e 3 tabelas de saque de bloco.
  - [x] Paridade de localização em português (`pt_br`), inglês (`en_us`) e espanhol (`es_es`).
- [x] **Comandos de Demonstração e Testes In-Game**:
  - [x] `/sandstorm debug phase 30`: Kit de inventário completo com armas, defesas, munições e toroides.
  - [x] `/sandstorm debug setup plasma_defense_complex`: Geração imediata de complexo militar com gerador de plasma, railgun municiado e pilões acústicos.
- [x] **Validação Automatizada de Testes**:
  - [x] Suíte dedicada `Fase30PlasmaDefenseTest` e expansão de `SandstormDebugTest`, elevando o mod para **916 testes automatizados** com 100% de aprovação e zero comentários.

### 🌋 Fase 31: Mineração Geotérmica Profunda, Poço do Manto Planetário & Extratores Magmáticos de Lítio-Plasma (Deep Core Geothermal Well, Mantle Borehole & Litho-Plasma Siphon) (Concluída - 100%)
*A conquista do subsolo profundo: perfuração em escala abissal através da rocha consolidada até as camadas do manto planetário, extração de fluidos térmicos supercríticos e condensação de lítio-plasma para gerar energia quase infinita e ligas superdensas.*

- [x] **Broca de Perfuração do Manto Planetário (`deep_core_borehole` / `DeepCoreBoreholeBlock` / `DeepCoreBoreholeBlockEntity`)**:
  - [x] Maquinário monumental industrial instalado na rocha basal (Y <= 0) ancorado por pistões de amortecimento sísmico em titânio e cerâmica.
  - [x] Cabeçote de perfuração articulado consumindo broca de diamante policristalino (`geothermal_core_drill_bit`), estendendo colunas de tubulação até o manto profundo (Y = -64).
  - [x] Capacitância interna WPT de 500.000 J (consumo de 250 J/tick durante avanço de perfuração) e conexão de dutos de fluidos inteligentes (`FluidStorage.SIDED` com 8.000 mB de capacidade).
  - [x] Extração periódica de sais brutos de lítio (`raw_lithium_salts`) e fluidos geotérmicos supercríticos com ciclo térmico e dinâmicas sísmicas.
- [x] **Extrator Magmático de Lítio-Plasma (`litho_plasma_extractor` / `LithoPlasmaExtractorBlock` / `LithoPlasmaExtractorBlockEntity`)**:
  - [x] Centrífuga térmica hermética de alta pressão em compósito de tungstênio para enriquecimento de fluidos de manto.
  - [x] Separação centrífuga consumindo 150 J/tick e sais de lítio brutos com recipientes criogênicos para condensar cápsulas de lítio superaquecido (`superheated_lithium_capsule`).
  - [x] Condensação piezoelétrica sob alta pressão gerando lingotes de superliga do manto (`mantle_alloy_ingot`) e subprodutos minerais refinados.
- [x] **Trocador de Calor Supercrítico (`supercritical_heat_exchanger` / `SupercriticalHeatExchangerBlock` / `SupercriticalHeatExchangerBlockEntity`)**:
  - [x] Usina térmica com matriz de dissipação para aletas cerâmicas (`thermal_radiator_fin`) e câmara de água pura.
  - [x] Converte água potável e fluidos de manto em vapor supercrítico de altíssima entalpia, gerando de 2.500 J/tick a **10.000 J/tick** diretamente na malha de energia sem fio WPT da base.
- [x] **Superligas do Manto & Componentes Geotérmicos**:
  - [x] Lingote de Superliga do Manto (`mantle_alloy_ingot`): forjado sob pressões gigapascal e temperaturas extremas.
  - [x] Cápsula Criogênica de Lítio Superaquecido (`superheated_lithium_capsule`): fluido isotópico de alta densidade energética para reatores e geradores supercríticos.
  - [x] Aleta Cerâmica de Dissipação Térmica (`thermal_radiator_fin`): radiador ablativo de alta condutividade térmica.
  - [x] Broca de Diamante Policristalino do Manto (`geothermal_core_drill_bit`): ponta intercambiável de extrema dureza abrasiva.
- [x] **Interfaces Gráficas de Monitoramento Industrial**:
  - [x] `DeepCoreBoreholeScreen` / `DeepCoreBoreholeMenu`: painel com telemetria de profundidade, termômetro em Kelvin, manômetro de pressão (Bar), medidores de energia e fluido refrigerante.
  - [x] `LithoPlasmaExtractorScreen` / `LithoPlasmaExtractorMenu`: interface de enriquecimento isotópico com progresso centrífugo e compartimentos de produtos.
  - [x] `SupercriticalHeatExchangerScreen` / `SupercriticalHeatExchangerMenu`: mostrador de taxa de geração térmica (J/t), pressão de vapor e compartimento para aletas de dissipação e cápsulas.
- [x] **Cadeia Completa de Assets 1.21.4 & Data-Driven Recipes**:
  - [x] 3 blockstates, 6 modelos de bloco com estados lit, 8 modelos de item, 8 definições em `assets/sandstorm/items/` e 20 texturas PNG geradas com integridade binária.
  - [x] 7 receitas balanceadas em `data/sandstorm/recipe/` e 3 tabelas de saque de bloco.
  - [x] Paridade linguística multilíngue (`pt_br`, `en_us`, `es_es`).
- [x] **Comandos de Demonstração e Testes In-Game**:
  - [x] `/sandstorm debug phase 31`: Kit de inventário completo com maquinários geotérmicos, brocas, aletas e superligas.
  - [x] `/sandstorm debug setup geothermal_well`: Geração instantânea de complexo geotérmico com perfuratriz, extrator lito-plasma e trocador térmico com conexões operacionais.
- [x] **Validação Automatizada de Testes**:
  - [x] Suíte dedicada `Fase31GeothermalWellTest` e expansão de `SandstormDebugTest`, elevando o mod para **940 testes automatizados** com 100% de sucesso e zero comentários.

### 🛰️ Fase 32: Rede Orbital de Satélites, Telescópio Espacial de Varredura & Lançador de Cargas Eletromagnético (Orbital Mass Driver, Satellite Constellation & Spectral Survey Telescope)
*Rompendo a barreira atmosférica: catapulta eletromagnética linear para lançamento de satélites em órbita baixa e geoestacionária, estação terrena de telemetria e rede de sensoriamento remoto para controle absoluto do planeta a partir do espaço.*

- [ ] **Catapulta Eletromagnética de Massa ("Orbital Mass Driver" - `orbital_mass_driver`)**:
  - [ ] Rampa vertical monumental de aceleração linear com trilhos magnéticos de bobinas Gauss duplas de 12 blocos de altura.
  - [ ] Capacitor de descarga de pulso de alta capacitância: armazena **500.000 J** para efetuar cada disparo orbital hipersônico.
  - [ ] **Mecânica Cinemática de Lançamento Espacial**:
    - [ ] A carga útil (satélite) é inserida no berço da catapulta. Ao acionar o botão de lançamento, uma contagem regressiva sonora de 5 segundos é disparada.
    - [ ] Disparo: O satélite é acelerado verticalmente a Mach 15 com arco voltaico ofuscante, estampido sônico estrondoso e coluna de condensação iônica que perfura as nuvens do céu do deserto.
- [ ] **Estação Terrena de Comunicação Orbital (`orbital_ground_station`)**:
  - [ ] Console de telecomunicações espaciais com antena parabólica rastreadora motorizada de 3 metros montada no teto.
  - [ ] Sincronização contínua com a malha da Torre Holo-Tática (`HoloTacticalSpire`), transmitindo telemetria orbital em tempo real para o enxame de ciborgues e para o Datapad do jogador.
  - [ ] Registro e controle de constelação de até 16 satélites ativos em órbita simultânea.
- [ ] **Telescópio Espacial de Varredura Espectral (`spectral_survey_telescope`)**:
  - [ ] Cúpula de observatório astronômico pressurizada com espelho primário de berílio polido e matriz CCD infravermelha criogênica.
  - [ ] Varredura do céu profundo: identifica órbitas de detritos espaciais, trajetórias de meteoritos e anomalias gravitacionais exoplanetárias.
- [ ] **Constelação de Satélites Orbitais (Cargas Úteis Modulares)**:
  - [ ] **Satélite de Sensoriamento Meteorológico Global (`weather_recon_satellite`)**:
    - [ ] Mapeia frentes de vento térmico na alta atmosfera, prevendo tempestades de areia iônicas com 10 minutos de antecedência e exibindo o vetor de aproximação no radar holográfico.
  - [ ] **Satélite de Espelho Solar Orbital ("Orbital Solar Reflector" - `orbital_solar_reflector_satellite`)**:
    - [ ] Espelho de filme de mylar aluminizado ultrafino de 50 metros em órbita geoestacionária.
    - [ ] Foca feixes de luz solar contínua sobre a base mesmo durante a noite polar do deserto ou sob nuvens de poeira, mantendo a geração solar WPT a 100% 24 horas por dia.
  - [ ] **Satélite de Radar de Abertura Sintética ("SAR Geological Satellite" - `sar_geological_satellite`)**:
    - [ ] Emite micro-ondas de penetração de solo: revela jazidas profundas de quartzo piezoelétrico, aquíferos subterrâneos e câmaras de ruínas soterradas em raio de 512 blocos.
  - [ ] **Satélite de Bombardeio Cinético ("Orbital Kinetic Lance" - `orbital_kinetic_lance_satellite`)**:
    - [ ] Satélite de armamento pesado armado com projéteis densos de tungstênio.
    - [ ] Permite ao operador marcar um alvo na superfície com o Datapad ou com a Torre Holo-Tática para invocar um ataque orbital hipersônico ("Vara de Deus"), desferindo dano catastrófico contra vermes de areia colossais.
- [ ] **Interface Gráfica Orbital Interativa (`OrbitalGroundStationScreen` / `OrbitalGroundStationMenu`)**:
  - [ ] Globo tridimensional holográfico em tempo real do planeta desértico girando no centro da tela com trajetórias orbitais elípticas coloridas por satélite.
  - [ ] Lista lateral de satélites ativos com telemetria (altitude, período orbital, telemetria de sinal e integridade da bateria solar).
  - [ ] Painel de comandos orbitais: calibração de mira, foco de espelho solar e histórico de tempestades monitoradas.
- [ ] **Cadeia Completa de Assets 1.21.4, Modelos 3D, SFX & Testes**:
  - [ ] Modelos BBModel detalhados para a rampa Gauss do Mass Driver, antena parabólica motorizada e chassi orbital com painéis solares dobráveis.
  - [ ] Efeitos sonoros customizados: pulso eletromagnético hipersônico de lançamento (`mass_driver_launch.ogg`), rastreamento mecânico de antena parabólica (`ground_station_track.ogg`) e disparo cinético orbital vindo do céu (`orbital_lance_strike.ogg`).
  - [ ] Suíte de testes dedicada `Fase32OrbitalSatellitesTest`.

### 🌧️ Fase 33: Atmosfera Artificial, Condensação de Nuvens em Escala Continental & O Primeiro Dilúvio (Planetary Atmospheric Genesis, Cloud Seeding & The Great Rain)
*O clímax ecológico do planeta deserto: alteração em grande escala da composição gasosa troposférica, semeadura de núcleos de condensação de chuva e a chegada histórica da primeira chuva, transformando areia estéril em solo vivo e rios de água doce.*

- [ ] **Reator de Gênese Atmosférica Megalítico (`atmospheric_genesis_reactor`)**:
  - [ ] Complexo industrial de craqueamento catalítico de gases atmosféricos de 4x4x6 blocos de altura com chaminé ionizante de plasma.
  - [ ] Consumo de energia WPT massivo (250.000 J por ciclo de saturação de aerossóis) e conexão à malha de fluidos potáveis e biopolímeros.
  - [ ] Rompimento da inversão térmica desértica: dissocia óxidos estéreis e injeta vapor d'água ionizado e ozônio na média troposfera.
- [ ] **Obuseiro Balístico de Semeadura de Nuvens (`cloud_seeding_howitzer`)**:
  - [ ] Canhão de artilharia atmosférica montado em pedestal giratório reforçado com mira angular vertical (45° a 90°).
  - [ ] Dispara cápsulas balísticas de semeadura higroscópica (`cloud_seeding_shell`) diretamente no coração das frentes térmicas identificadas pelos satélites meteorológicos.
  - [ ] Dispersão em altitude: Cada cápsula detonada na altitude Y=192 a 256 libera uma nuvem de micropartículas que aglutinam a umidade dispersa em núcleos de condensação de chuva.
- [ ] **Condensador Troposférico de Umidade Estática (`tropospheric_condenser`)**:
  - [ ] Torre de condensação estática com redes hidrofílicas tecidas em biopolímeros flexíveis e refrigeradas por hemolinfa criogênica.
  - [ ] Captação massiva de água aérea durante a precipitação, alimentando automaticamente cisternas e reservatórios subterrâneos da base (`rainwater_collection_cistern`).
- [ ] **O Grande Cataclismo Ecológico: O Primeiro Dilúvio ("The First Rain")**:
  - [ ] **Transição Visual e Atmosférica Espetacular**:
    - [ ] Ao atingir o índice crítico de saturação troposférica (100%), o céu do planeta escurece gradualmente, substituindo a poeira alaranjada por nuvens cinzentas volumétricas densas.
    - [ ] Trovões distantes ecoam pelas dunas com estrondo e reverberação ressonante (`first_rain_thunder.ogg`).
    - [ ] As primeiras gotas pesadas de chuva caem sobre a areia incandescente, gerando micro-névoas e colunas de vapor térmico ascendente.
    - [ ] A chuva torrencial cai de forma generalizada sobre o setor por 30 minutos contínuos (`first_rain_downpour.ogg`), extinguindo o efeito de calor extremo de 48°C e estabilizando a temperatura ambiente em amenos 22°C.
  - [ ] **Biorremediação e Transformação Territorial Permanente**:
    - [ ] A areia desértica exposta à chuva torrencial em um raio de 128 blocos do reator é hidratada e enriquecida, convertendo-se em solo fértil e terra vegetal (`xeno_grass_block` e `farmland`).
    - [ ] Depressões topográficas de areia e crateras de antigas ruínas enchem-se com poças e lagos perenes de água doce pura e potável.
    - [ ] Florescimento biológico espontâneo: brotamento de juncos ancestrais, acácias xerófilas e flores extremófilas nativas sem necessidade de plantio manual.
- [ ] **Munições & Compostos Químicos**:
  - [ ] Projétil Balístico de Semeadura de Nuvens (`cloud_seeding_shell`): cartucho propelente estocado com iodeto de prata sintético, glicerol osmoprotetor e pó de quartzo piezoelétrico.
  - [ ] Núcleo Catalisador Atmosférico de Platina-Titânio (`atmospheric_catalyst_core`): elemento filtrante de cerâmica sinterizada para reatores de gases.
  - [ ] Cisterna de Coleta Pluvial de Alta Capacidade (`rainwater_collection_cistern`): tanque selado de 32.000 mB de água limpa com drenagem automática.
- [ ] **Interface Gráfica de Gênese Climática (`AtmosphericGenesisScreen` / `AtmosphericGenesisMenu`)**:
  - [ ] Monitor de composição gasosa em tempo real (% de O2, CO2, umidade relativa do ar e densidade barométrica).
  - [ ] Radar de saturação troposférica com contagem regressiva para formação da frente de chuva e seletor de vetor de disparo do obuseiro.
- [ ] **Cadeia Completa de Assets 1.21.4, Modelos 3D, SFX & Testes**:
  - [ ] Modelos BBModel detalhados para o reator de gênese com chaminé de plasma, obuseiro de semeadura e torre condensadora.
  - [ ] Efeitos sonoros customizados: trovão ressonante do dilúvio (`first_rain_thunder.ogg`), tempestade de chuva torrencial (`first_rain_downpour.ogg`) e disparo sibilante do obuseiro (`cloud_howitzer_fire.ogg`).
  - [ ] Suíte de testes dedicada `Fase33AtmosphericGenesisTest`.

### 🚀 Fase 34: Elevador Espacial Planetário, Farol Quântico Subespacial & Transmissão Interestelar de Resgate (Planetary Space Elevator, Deep Space Beacon & The Final Odyssey)
*A apoteose final da sobrevivência e da engenharia cósmica: construção de uma âncora colossal de elevador espacial ligada por nanotubos de carbono à órbita geoestacionária, ativação do farol quântico de táquions e estabelecimento de contato definitivo com a Federação Interestelar.*

- [ ] **Âncora de Base do Elevador Espacial ("Space Elevator Base Anchor" - `space_elevator_base_anchor`)**:
  - [ ] Estrutura monumental 4x4x4 de ancoragem ultra-pesada cravada no leito de rocha sólida com 8 sapatas hidráulicas de titânio e superligas do manto.
  - [ ] Sistema de tensão ativa magnética mantendo o cabo de nanotubos de carbono esticado sob tensão de múltiplos gigapascals rumo ao zênite cósmico.
  - [ ] Buffer de energia colossal de **2.000.000 J WPT** alimentado pela rede integrada da base (reatores solares, geotérmicos e acumuladores de estado sólido).
- [ ] **Vagão Suborbital Maglev Vertical ("Orbital Climber Car" - `orbital_climber_car`)**:
  - [ ] Cápsula pressurizada de transporte vertical que percorre o cabo de nanotubos em levitação magnética linear.
  - [ ] Permite ao operador embarcar para uma subida contínua e triunfante através de todas as camadas atmosféricas (troposfera -> estratosfera -> mesosfera -> termosfera -> órbita negra estrelada em Y=500+).
  - [ ] Interior com assento ergonômico, visor panorâmico de vidro de fulgurito temperado e display HUD de altitude, velocidade (m/s) e aceleração G.

- [ ] **Farol Quântico de Comunicação Subespacial (`quantum_subspace_beacon`)**:
  - [ ] Dispositivo topo de linha acoplado à plataforma superior do elevador espacial ou ao topo da torre de transmissão.
  - [ ] Emissor de ondas gravitacionais moduladas e feixe helicoidal de táquions superluminais capazes de propagar sinal através de hiperespaço sem defasagem relativística de tempo.
- [ ] **Materiais Exóticos & Engenharia Quântica**:
  - [ ] Bobina Trançada de Nanotubos de Carbono-Grafeno (`graphene_nanotube_tether`): fio de espessura nanométrica e resistência mecânica sem precedentes, capaz de sustentar o peso do elevador orbital.
  - [ ] Matriz de Qubits Entrelaçados ("Entangled Qubit Array" - `quantum_entangled_qbit_array`): processador quântico de criptografia e transmissão subespacial instantânea.
  - [ ] Cronômetro Estelar Sincronizado (`stellar_chronometer`): relógio atômico de navegação cósmica para alinhamento de vetores de salto em dobra.
- [ ] **O Grande Clímax: A Transmissão Interestelar & A Conquista Planetária**:
  - [ ] **Sequência Cinemática de Ativação do Farol**:
    - [ ] Ao energizar o Farol Quântico com 100% de carga WPT e inserir a Matriz Qbit:
    - [ ] Um feixe helicoidal ofuscante duplo de luz ciano luminescente e âmbar dourado irrompe verticalmente em direção ao céu, perfurando nuvens e atmosfera até o infinito sideral (`BeaconBeam` estendido).
    - [ ] Disparo de um acorde sinfônico triunfante espacializado audível em todo o mundo (`quantum_beacon_transmit.ogg`).
    - [ ] O anel de choque perimétrico dissipa qualquer tempestade de areia residual e estabiliza a ionosfera do planeta.
  - [ ] **Mensagem de Resgate da Frota Estelar**:
    - [ ] Transmissão holográfica no HUD do operador e na tela do Datapad:
      ```text
      ================================================================
      [TRANSMISSÃO SUBESPACIAL RECEBIDA - PROTOCOLO SEC-ALPHA]
      ORIGEM: NAU-CAPITÂNIA CIENTÍFICA "AURORA IX" - FEDERAÇÃO ESTELAR
      COORDENADAS CONFIRMADAS: SETOR DESÉRTICO PLANETÁRIO OMEGA-7
      SITUAÇÃO: SINAL DE EMERGÊNCIA IDENTIFICADO A 4.2 ANOS-LUZ.
      VETOR DE SALTO EM DOBRA HIPERESPACIAL CALCULADO COM SUCESSO.
      TEMPO ESTIMADO DE CHEGADA: 48 HORAS PADRÃO.
      MENSAGEM: PARABÉNS, OPERADOR. VOCÊ SOBREVIVEU AO CATACLISMO,
      DOMINOU A ECOLOGIA, DOMOU OS TITÃS E CONQUISTOU ESTE PLANETA.
      STATUS DA MISSÃO: TRIUNFO ABSOLUTO.
      ================================================================
      ```
    - [ ] Concessão da Conquista Máxima do SandStorm: **"Soberano do Deserto" ("Desert Sovereign")**.
- [ ] **Cadeia Completa de Assets 1.21.4, Modelos 3D, SFX & Testes**:
  - [ ] Modelos BBModel detalhados para a âncora monumental do elevador, vagão climber e farol quântico com anéis giroscópicos.
  - [ ] Efeitos sonoros customizados: zumbido maglev de subida vertiginosa (`elevator_ascend.ogg`), acionamento de telemetria quântica (`quantum_beacon_transmit.ogg`) e acorde triunfante de conclusão estelar (`space_rescue_fanfare.ogg`).
  - [ ] Suíte de testes dedicada `Fase33SpaceElevatorTest`.

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
| `item.hypo_injector.use` | `hypo_injector_use.ogg` | `players` | 0.3s | Sibilo pneumático de alta pressão. Disparo rápido de injeção a gás estéril no traje espacial com click mecânico de trava. |
| `block.molecular_modifier.work` | `molecular_modifier_work.ogg` | `blocks` | 1.6s | Zumbido harmônico de laser litográfico calibrando microchips e gravação em silício em nível atômico. |
| `block.medbay_pod.enter` | `medbay_pod_enter.ogg` | `blocks` | 1.0s | Pressurização de cúpula médica. Fechamento hermético pneumático com descompressão a gás estéril. |
| `block.medbay_pod.heal` | `medbay_pod_heal.ogg` | `ambient` | 2.0s (loop) | Circulação suave de fluido criogênico e biopolímeros regenerativos em tubulações estéreis. |
| `block.bioreactor.bubble` | `bioreactor_bubble.ogg` | `ambient` | 2.5s (loop) | Borbulhamento e circulação de caldo nutritivo estéril em tanque de fermentação com zumbido magnético suave. |
| `item.stim.activate` | `stim_activate.ogg` | `players` | 0.8s | Pulso bio-elétrico com batimentos cardíacos sutilmente acelerados e tom harmônico ascendente de sobrecarga motora. |
| `block.cyborg_incubator.loop` | `cyborg_incubator_loop.ogg` | `blocks` | 3.0s (loop) | Borbulhamento hidrostático e zumbido ressonante de fluido amniótico sintético e laser de bio-montagem. |
| `entity.cyborg.step` | `cyborg_step.ogg` | `neutral` | 0.5s | Passada bípede biomecânica. Impacto metálico seco de titânio amortecido pela contração elástica de miômeros. |
| `entity.cyborg.drill` | `cyborg_drill.ogg` | `neutral` | 2.0s (loop) | Vibro-broca de cavitação molecular triturando rocha dura e areia com estalos piezoelétricos de alta rotação. |
| `entity.cyborg.weld` | `cyborg_weld.ogg` | `neutral` | 1.8s (loop) | Tocha de plasma molecular e arco elétrico de solda assentando blocos com crepitação estática e zumbido ciano. |
| `entity.cyborg.voice_ack` | `cyborg_voice_ack.ogg` | `neutral` | 0.6s | Resposta vocal sintética em frequência filtrada. Beep melódico eletrônico confirmando recebimento de diretriz do jogador. |
| `item.command_uplink.ping` | `command_uplink_ping.ogg` | `players` | 0.4s | Sinal sonoro de alta tecnologia emitido pelo uplink holográfico ao traçar vértices de bounding boxes no mundo. |
| `block.cyborg_dock.clamp` | `cyborg_dock_clamp.ogg` | `blocks` | 0.9s | Travamento pneumático de pinças metálicas abraçando as pernas do ciborgue na plataforma com sibilo de despressurização. |
| `block.aegis_shield.hum` | `aegis_shield_hum.ogg` | `blocks` | 3.0s (loop) | Zumbido ressonante harmônico de plasma de confinamento magnético em cúpula protetora de alta tensão. |
| `block.aegis_shield.impact` | `aegis_shield_impact.ogg` | `blocks` | 1.0s | Deflexão energética instantânea no escudo de plasma ao interceptar projéteis ou raios iônicos com estalo de dissipação. |
| `block.railcannon.fire` | `railcannon_fire.ogg` | `hostile` | 2.5s | Disparo devastador do canhão ferroviário cinético hipersônico com estrondo sônico violento e descarga elétrica. |
| `block.deep_borehole.drill` | `deep_borehole_drill.ogg` | `blocks` | 3.5s (loop) | Rotação pesada de broca de carbeto de tungstênio triturando rocha ultra-densa em profundidades abissais do manto. |
| `block.litho_extractor.spin` | `litho_extractor_spin.ogg` | `blocks` | 2.2s (loop) | Centrífuga térmica magnética de alta rotação separando sais de lítio e plasma de silicatos sob alta pressão. |
| `block.steam.purge` | `steam_purge.ogg` | `blocks` | 1.5s | Escape sibilante e violento de vapor superaquecido de trocadores de calor geotérmicos sob alívio de pressão. |
| `block.mass_driver.launch` | `mass_driver_launch.ogg` | `players` | 2.0s | Descarga em arco voltaico e aceleração linear em trilho Gauss ejetando cargas úteis rumo à órbita estelar. |
| `block.ground_station.track` | `ground_station_track.ogg` | `blocks` | 1.8s | Servomotores de precisão girando e calibrando a antena parabólica de rastreamento de satélites orbitais. |
| `weather.first_rain.thunder` | `first_rain_thunder.ogg` | `weather` | 4.0s | Trovão atmosférico distante e grave que reverbera por todo o deserto anunciando a chegada da primeira frente de chuva. |
| `weather.first_rain.downpour` | `first_rain_downpour.ogg` | `weather` | 6.0s (loop) | Chuva torrencial contínua e densa caindo sobre dunas, rochas e tetos de maquinários com chiado de vaporização. |
| `block.elevator.ascend` | `elevator_ascend.ogg` | `players` | 4.0s (loop) | Zumbido aerodinâmico e indução eletromagnética linear do vagão subindo verticalmente pelo cabo de nanotubos. |
| `block.quantum_beacon.transmit` | `quantum_beacon_transmit.ogg` | `players` | 3.8s | Emissão ressonante de ondas gravitacionais e feixes táquions helicoidais com eco harmônico rasgando a atmosfera. |
| `ambient.space_rescue_fanfare` | `space_rescue_fanfare.ogg` | `ambient` | 6.5s | Acorde orquestral triunfante em ressonância cósmica comemorando o resgate estelar e a vitória definitiva do operador. |

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
  - [x] `tool_base`: Base estrutural metálica para ferramentas do Capítulo 1.
  - [x] `electric_component`: Matriz elétrica montada para ferramentas de silício.
  - [x] `silicon_pickaxe`: Picareta de silício para mineração e quebra do softlock de pedra.
  - [x] `geological_scanner`: Escâner portátil com visor de display ciano, laser espectrométrico e telemetria mineral.
  - [x] `field_probe`: Sonda de telemetria portátil com haste sensora e anéis térmicos de cobre.
  - [x] `repair_tool`: Multiferramenta ergonômica com arco voltaico duplo para solda e manutenção de campo.
  - [x] `structural_plate`: Placa reforçada de titânio escuro com rebites chanfrados para blindagens e montagens.
  - [x] `circuit_mount`: Suporte cerâmico com contatos de ouro e clipes de ancoragem para placas lógicas.
  - [x] `pressure_seal`: Anel de vedação hermética em elastômero reforçado para tubulações e filtros.
  - [x] **Organismos Extremófilos & Biologia de Dunas (Fase 21)**:
    - [x] `radiotrophic_mycelium`: Bloco com modelo cube_all e textura pixel art 16x16 de hifas púrpuras e nós bio-radiotróficos.
    - [x] `chitinolytic_fungus`: Fungo de prateleira com modelo cross cutout e textura 16x16 âmbar em camadas.
    - [x] `cryo_xerophilic_lichen`: Líquen crio-xerófilo com modelo cross cutout e textura 16x16 ciano-ártica com cristais de trealose.
    - [x] `halophyte_succulent`: Suculenta halófita com modelo cross cutout e roseta verde com crosta salina esbranquiçada.
    - [x] `dune_ephedra`: Arbusto de duna com modelo cross cutout e râmulos articulados verde-oliva com cones avermelhados de alcaloides.
    - [x] `bioreactor_vat`: Biorreator de batelada com modelo orientable de 4 faces texturizadas (`top`, `bottom`, `side`, `front`) e cúpula de vidro iluminada.
    - [x] `radioprotective_melanin`: Ampola farmacêutica de melanina coloidal negra com iridescência ultravioleta.
    - [x] `chitosan_extract`: Frasco com flocos cristalinos dourados de poliglicosamina hemostática.
    - [x] `trehalose_sugar`: Aglomerado de cristais prismáticos de açúcar crio-protetor vitrificador.
    - [x] `osmolyte_glycerol`: Frasco conta-gotas com glicerol osmoprotetor esmeralda viscoso.
    - [x] `neuroactive_alkaloids`: Tintura botânica carmesim e dourada de alcaloides adrenérgicos.
  - [x] **Bio-Farmacologia Tecnológica & Hipo-Injetor (Fase 22)**:
    - [x] `hypo_injector`: Hipo-injetor pneumático portátil em titânio escuro com acionamento estéril instantâneo.
    - [x] `adrenal_stim`: Ampola neuro-adrenérgica para sobrecarga motora (+30% velocidade).
    - [x] `biofoam_cartridge`: Cartucho de bio-espuma coagulante com quitosana para cura e regeneração celular.
    - [x] `myomer_stim`: Estimulador miomecânico para torque contrátil (+40% força de impacto).
    - [x] `endothermic_serum`: Sérum endotérmico refratário para imunidade térmica absoluta e proteção solar.
    - [x] `grav_dampener_stim`: Sérum anti-inercial piezoelétrico para super-saltos e queda suave.
    - [x] `detox_ampoule`: Ampola quelante de desintoxicação celular para purga de venenos e toxinas.
    - [x] `stealth_nano_drape`: Emulsão de refração óptica furtiva para camuflagem metamaterial.
  - [x] **Modificação Molecular, Overclocks & Nanocoatings (Fase 23)**:
    - [x] 12 Módulos de hardware: `vibro_resonator_module`, `thermal_plasma_emitter`, `kinetic_focus_module`, `cavitation_frequency_core`, `atomic_phase_disrupter`, `spectrometric_sifter`, `self_healing_nanite_matrix`, `titanium_lattice_coating`, `ballistic_dampener_mesh`, `ablative_thermal_plating`, `pneumatic_fall_dampers`, `reactive_shock_plating`.
  - [x] **Bio-Cibernética Fundamental & Componentes Neurais (Fase 25)**:
    - [x] `biomechanical_chassis_frame`: Chassi esquelético articulado em titânio-quitina.
    - [x] `synthetic_myomer_bundle`: Feixes de miômeros artificiais eletroativos de alta contração.
    - [x] `bio_neural_core`: Processador wetware neural biônico com micélio radiotrófico e silício.
    - [x] `bio_coolant_canister`: Cânister de hemolinfa bio-refrigerante ciano luminescente.
    - [x] `assembled_cyborg_frame`: Chassi biomecânico integral gestado na incubadora.
  - [x] **Ciborgues Especialistas & Uplink de Comando (Fase 26)**:
    - [x] `cybernetic_command_uplink`: Transmissor holográfico com demarcação 3D de volumes e telemetria biônica.
    - [x] `cyborg_excavator_spawn_egg`: Módulo de ativação e implantação do Ciborgue Escavador.
    - [x] `cyborg_builder_spawn_egg`: Módulo de ativação e implantação do Ciborgue Construtor.
    - [x] `cyborg_harvester_spawn_egg`: Módulo de ativação e implantação do Ciborgue Colhedor.
  - [x] **Enxame Cibernético & Módulos de Upgrade Biônicos (Fase 27)**:
    - [x] `acid_chitin_plating`: Blindagem de quitina ácida (+20 HP e proteção cáustica).
    - [x] `cryo_trehalose_cell`: Célula criogênica de trealose (-50% consumo de bio-refrigerante).
    - [x] `long_range_lidar_lens`: Lente óptica LiDAR de longo alcance (dobra raio de varredura para 32 blocos).
    - [x] `piezo_hover_thruster`: Propulsor piezoelétrico (+1 bloco de elevação de passo e +30% velocidade).
  - [x] **Torre Holo-Tática & Matriz Neural (Fase 28)**:
    - [x] `neural_synapse_link`: Módulo de interface neural para telemetria direta do enxame no capacete do traje.
    - [x] `orbital_recon_probe`: Sonda de reconhecimento orbital descartável para previsão atmosférica e varredura sísmica.
  - [x] `pressure_seal`: Anel hermético de elastômero fluoropolímero para eclusas e tubulações de alta pressão.
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
  - [x] `fulgurite_glass`, `electrified_sand`, `fossilized_amber`, `ancient_reed_block`, `thermal_spring_stone`: Blocos e recursos geológicos dos Ermos de Fulgurito e Oásis Fóssil (Fase 16).
  - [x] `sand_maglev_rail`, `habitat_dome`, `auto_assembly_line`: Trilhos de levitação magnética, domo residencial e linha de montagem industrial (Fase 17).
  - [x] `titanium_spike_wall`, `retractable_spike_wall`, `electrified_spike_barrier`, `corrosive_chitin_spike_wall`, `kinetic_floor_spikes`, `crushing_spike_gate`: Módulos de espinhos de titânio, espinhos retráteis pneumáticos, barreiras de alta tensão, espinhos bio-corrosivos, armadilhas sísmicas de piso e portões de grades esmagadoras (Fase 18).
  - [x] `megastructure_constructor.bbmodel`: Bloco monumental 2x2 com base em titânio escuro, 4 pistões pneumáticos de fixação estrutural, cúpula holográfica central de cristal piezoelétrico com anéis de giroscópio animados e 4 baias de hangar para esquadrilhas de drones (Fase 19).
  - [x] `molecular_modifier`: Bancada industrial de modificação molecular com cabeçote litográfico laser e soquetes de overclock/nanocoating (Fase 23).
  - [x] `bio_regeneration_pod`: Cápsula médica MedBay hermética de criostase, monitor cardíaco ECG e perfusão de bio-soluções (Fase 24).
  - [x] `cyborg_docking_station`: Plataforma pesada de ancoragem e recarga WPT no solo com conector indutivo, garras mecânicas de fixação pneumática e leds de status (Fase 27).
  - [x] `holo_tactical_spire`: Torre monumental de projeção holo-tática com antena ressonante de titânio escuro, emissor holográfico ciano e console de radar vetorial (Fase 28).
  - [ ] `planetary_aegis_generator`, `titan_kinetic_railcannon`, `point_defense_node`: Gerador de escudo de plasma com bobinas toroidais, canhão ferroviário de titã e nós sônicos de defesa de ponto (Fase 29).
  - [ ] `deep_core_borehole`, `litho_plasma_extractor`, `supercritical_heat_exchanger`: Broca monumental do manto, centrífuga de lítio-plasma e trocador térmico com aletas cerâmicas (Fase 30).
  - [ ] `orbital_mass_driver`, `orbital_ground_station`, `spectral_survey_telescope`: Catapulta eletromagnética Gauss linear de 12 blocos, console de antena parabólica rastreadora e observatório astronômico (Fase 31).
  - [ ] `atmospheric_genesis_reactor`, `cloud_seeding_howitzer`, `tropospheric_condenser`: Reator catalítico de plasma com chaminé, obuseiro de semeadura higroscópica e torre condensadora de chuva (Fase 32).
  - [ ] `space_elevator_base_anchor`, `orbital_climber_car`, `quantum_subspace_beacon`: Âncora 4x4x4 com tensão ativa magnética, vagão suborbital maglev e farol quântico helicoidal de táquions (Fase 33).
- [x] **Modelos de Entidades (Blockbench & Java Models)**:
  - [x] `sandworm.bbmodel`: Corpo cilíndrico segmentado com mandíbulas quádruplas abertas e anel bucal.
  - [ ] `sandtrout.bbmodel`: Modelo pequeno de criatura ameboide coriácea rastejante de areia (Truta da Areia / Little Maker).
  - [ ] `sandworm_larva.bbmodel`: Modelo segmentado ágil de larva/ninfa com anéis tenros de quitina e mandíbula trirradiada.
  - [ ] `juvenile_sandworm.bbmodel`: Modelo intermediário de verme caçador de dunas (~15 blocos) com crista dorsal e sulcos de escavação.
  - [x] `builder_drone.bbmodel`: Drone operário de construção aérea dark-tech com propulsores quad-ion basculantes, cabeçote móvel com emissores laser convergentes e garras magnéticas de retenção de blocos (Fase 19).
  - [x] `cargo_drone.bbmodel`: Drone quadricóptero com rotores e garras de carga.
  - [x] `excavator_vehicle.bbmodel`: Rover industrial de esteiras duplas com broca giratória frontal.
  - [x] `megazord.bbmodel`: Mecha bípede titânico com cockpit e emissores de choque sônico.
  - [x] `sandboard`: Prancha de surfe nas dunas com fixadores de botas.
  - [x] `cyborg_excavator`: Chassi bípede biomecânico reforçado de titânio escuro com feixes musculares carmesim expostos, braço broca de vibro-cavitação atômica e visor adaptativo (`CyborgModel` / `CyborgRenderer`, Fase 26).
  - [x] `cyborg_builder`: Chassi biomecânico ágil com manipulador magnético no braço esquerdo, tocha de solda de plasma molecular no braço direito, compartimento dorsal de carga e visor adaptativo (`CyborgModel` / `CyborgRenderer`, Fase 26).
  - [x] `cyborg_harvester`: Chassi esguio e veloz com garras cirúrgicas articuladas, sensor fitossanitário e cesto traseiro selado de coleta hermética de biomassa (`CyborgModel` / `CyborgRenderer`, Fase 26).
  - [ ] `orbital_satellites`: Modelos de satélites modulares (meteorológico, espelho solar, radar SAR e lança cinética) com painéis fotovoltaicos desdobráveis (Fase 31).
  - [ ] `orbital_climber_car`: Cápsula pressurizada aerodinâmica com visor panorâmico de vidro de fulgurito (Fase 33).

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
35. `d239c6c`: Implementação da **Fase 16: Biomas Extremos, Ventos Radioativos & Oásis Fósseis**:
    - Geração de mundo para os Ermos de Fulgurito (`fulgurite_wastes`) com monólitos de fulgurito (`fulgurite_monolith`), blocos de vidro de fulgurito (`fulgurite_glass`) e areia eletrizada condutora (`electrified_sand`).
    - Cavernas ocultas com oásis fóssil subterrâneo (`fossilized_oasis`), fontes termais minerais (`thermal_spring_stone`), juncos ancestrais (`ancient_reed_block`) e depósitos de âmbar fóssil (`fossilized_amber`).
    - Placed features e loot tables correspondentes integrados em `SandStormWorldGen` e suíte de testes `Fase16BiomesAndFeaturesTest`.
36. `5280419`: Implementação da **Fase 17: Logística Maglev, Linhas Industriais & Domos Coloniais**:
    - Sistema ferroviário magnético de areia (`sand_maglev_rail`) para transporte de alta velocidade.
    - Domos residenciais de colonos (`habitat_dome`) com módulos habitacionais pressurizados.
    - Linha de montagem industrial automatizada (`auto_assembly_line`) para manufatura robótica contínua.
    - Suíte de testes dedicada `Fase17LogisticsAndIndustryTest`.
37. `72f0c10`: Implementação da **Fase 18: Fortificações Perimétricas, Muralhas de Espinhos & Contra-Medidas Físicas**:
    - Paredes com espinhos de titânio balístico (`titanium_spike_wall`) com dano físico contínuo e desaceleração severa.
    - Paredes de espinhos retráteis pneumáticos (`retractable_spike_wall`) com ativação por redstone e dano crítico de empalamento.
    - Muralha de espinhos eletrizados de alta tensão (`electrified_spike_barrier`) com dano duplo, choque voltaico ciano e consumo WPT de 50 J.
    - Muralha com revestimento bio-corrosivo (`corrosive_chitin_spike_wall`) sintetizada com quitina e seiva pesada, infligindo corrosão ácida e degradação de armaduras.
    - Armadilha de espinhos de chão pressurizada (`kinetic_floor_spikes`) com gatilho sísmico por pressão de passos.
    - Portão fortificado com grades de espinhos esmagadores (`crushing_spike_gate`) com abertura e fechamento motorizado.
    - 6 novas receitas shaped data-driven, loot tables, blockstates, modelos e texturas 16x16, localização trilingue (pt_br, en_us, es_es) e suíte de testes `Fase18SpikeFortificationsTest`, expandindo a suíte para **591 testes automatizados** com 100% de sucesso.
38. `fase19-done`: Implementação completa da **Fase 19: Construtor Autônomo de Megaestruturas, Drones Operários & Manufatura Holográfica 3D (Layer-by-Layer)**:
    - Bloco tecnológico pesado de ancoragem no solo (`megastructure_constructor`) com geometria monumental, cúpula piezoelétrica com anéis de giroscópio animados, conexão à malha WPT (500.000 J), e 18 slots de insumos compatíveis com a Fabric Transfer API (`ItemStorage.SIDED`).
    - Drones construtores operários autônomos (`BuilderDroneEntity`) com propulsores quad-ion basculantes, animação de rotor, garras magnéticas de retenção de blocos, protocolo de retorno emergencial em tempestades (`storm >= 0.75`) e cabeçote litográfico laser móvel.
    - Mecânica e pipeline gráfico de feixe de laser de fusão molecular contínuo com dano térmico/laser hazard contínuo (8.0 dano/s + ignição de fogo), renderização de linhas cilíndricas duplas emissivas (`SubmitNodeCollector`) 100% compatível com Sodium/Iris.
    - Projeção holográfica 3D no mundo (`MegastructureConstructorBlockEntityRenderer`) renderizando o wireframe ciano (`#00E5FF`) de toda a megaestrutura em tempo real, com destaque âmbar pulsante e diagonal na camada e bloco ativos, e telemetria flutuante com billboard de status.
    - Algoritmo construtivo aditivo rigoroso layer-by-layer (de baixo para cima em Y e concêntrico radial) e transição de materialização molecular.
    - Interface gráfica cyberpunk completa (`MegastructureConstructorScreen` / `MegastructureConstructorMenu`) com tela escura `#FA0A0E17`, bordas neon ciano `#FF00E5FF`, medidor vertical de energia, velocímetro central, telemetria de drones e seletor de blueprints.
    - Catálogo nativo de 4 blueprints monumentais (`biosphere_dome`, `planetary_citadel`, `orbital_launch_silo`, `desert_tech_pyramid`).
    - Finalização épica com onda de choque sonora (`megastructure_complete.ogg`) e consagração do perímetro de 48m como Zona Segura Permanente imune a vermes de areia (`KineticShieldTracker`).
    - 3 novos eventos de som registrados (`MEGASTRUCTURE_CONSTRUCTOR_LASER`, `MEGASTRUCTURE_LAYER_COMPLETE`, `MEGASTRUCTURE_COMPLETE`), mapeamento de áudio em `sounds.json`, receitas, loot tables, i18n trilingue (pt_br, en_us, es_es), texturas PNG íntegras e suíte de testes `Fase19MegastructureTest`, expandindo a suíte para **598 testes automatizados** com 100% de sucesso.
39. `2d07134` / `a3daec7`: Resolução e blindagem das receitas de fortificações perimétricas (uso de `titanium_chitin_composite` em vez de identificadores inexistentes), expansão do `JeiCompatibilityTest` para auditar a existência física de todos os itens e texturas de receitas data-driven, e formalização do *Protocolo de Integridade de Receitas Data-Driven* no `AGENTS.md`.
40. `ce42c0b`: Implementação do **Catálogo de Projetos (Blueprints Drawer)** para a Impressora 3D e o Fabricador de Nanites (`MachineRecipeRegistry`, `MachineRecipe`, auto-preenchimento de insumos com 1 clique e drawer deslizante lateral), **Modelo Focal da Lanterna do Capacete** (`FlashlightFocalModel`, cone de 25°, 28m de alcance, atenuação angular/radial e renderizador de feixe/vinheta `FlashlightFocalRenderer`), revisão da descrição da missão da Picareta de Silício no Datapad (pt_br, en_us, es_es) e expansão da suíte para **610 testes automatizados** 100% aprovados.
41. `f796d11`: Otimização visual da lanterna tática (`FlashlightLightMixin` com intensidade total de visão noturna `nightVisionEffectIntensity=1.0` no modo HIGH, eliminação da vinheta escura que obstruía a visão periférica e retenção limpa do anel focal ciano em `FlashlightFocalRenderer`).
42. `0ef497c`: Saneamento de receitas e purificação hídrica: remoção da rota de fornalha para água potável, obrigatoriedade do Filtro de Dessalinização (`DesalinationFilterBlock`), adição da receita de confecção do refil `filter_cartridge`, receitas de pastilha de silício (`silicon_wafer`) via fundição/alto-forno e calibração do tempo de forja da Vibro-Crysknife para 60s (1200 ticks).
43. `970f3bd`: Eliminação total de ícones de barreira no JEI e livro de receitas de receitas data-driven: geração e mapeamento de texturas de itens para 41 blocos, criação de texturas para máquinas avançadas e implementação do teste de regressão automatizado `everyItemDefinitionMustHaveACorrespondingItemTexture` no `AssetIntegrityTest`, auditando a cadeia completa de 97+ itens.
44. `fase2-printer3d`: Expansão da manufatura aditiva da Impressora 3D (`sandstorm:printer_3d`):
    - 3 Novas Ferramentas Tecnológicas: Escâner Geológico (`geological_scanner`), Sonda de Campo (`field_probe`) e Ferramenta de Reparo Tecnológico (`repair_tool`).
    - 3 Novos Componentes de Construção: Placa Estrutural (`structural_plate`), Suporte de Circuito (`circuit_mount`) e Vedação de Pressão Hermética (`pressure_seal`).
    - Texturas pixel-art 16x16, modelos JSON de item/renderização, registros em `SandStormItems` e aba criativa.
    - Registro de 6 novas receitas funcionais de manufatura aditiva no `MachineRecipeRegistry`, elevando o catálogo da Impressora 3D para 9 receitas.
    - Localização e nomenclatura trilingue (pt_br, en_us, es_es) com paridade 100% auditada por `I18nParityTest`.
    - Expansão da suíte para **627 testes automatizados** com 100% de aprovação e zero comentários.
45. `fase21-extremophile-mycology`: Implementação completa da **Fase 21: Micologia de Extremófilos, Fitoquímica de Dunas & Biorreatores de Batelada**:
    - 5 Organismos extremófilos botânicos e fúngicos terrestres de alta ciência: Micélio Radiotrófico (`radiotrophic_mycelium`), Fungo Quitinolítico de Dunas (`chitinolytic_fungus`), Líquen Crio-Xerofílico (`cryo_xerophilic_lichen`), Suculenta Halófita (`halophyte_succulent`) e Éfedra das Dunas (`dune_ephedra`), com estágios de crescimento, render type cutout e `noOcclusion()`.
    - 5 Metabólitos bioativos de grau médico/farmacêutico: Melanina Radioprotetora (`radioprotective_melanin`), Extrato de Quitosana (`chitosan_extract`), Açúcar Trealose (`trehalose_sugar`), Osmólito de Glicerol (`osmolyte_glycerol`) e Alcaloides Neuroativos (`neuroactive_alkaloids`).
    - Biorreator de Batelada e Quimiostato (`bioreactor_vat` / `BioreactorVatBlockEntity`): maquinário industrial com fermentação submersa, suporte a WPT (10.000 J), slot de bateria com alimentação por redstone, integração à Fabric Transfer API (`ItemStorage.SIDED`), e devolução automática de frascos de vidro.
    - Interface de usuário (`BioreactorVatMenu` / `BioreactorVatScreen`) com sincronização de telemetria, telemetria de WPT e barra de progresso.
    - Cadeia completa de assets em conformidade estrita com o padrão 1.21.4 (6 blockstates, 6 modelos de bloco, 11 modelos de item, 11 definições em `assets/sandstorm/items/` e 14 texturas pixel art 16x16 com assinaturas PNG válidas).
    - Receita shaped data-driven e loot tables de blocos configuradas.
    - Paridade 100% de i18n nas 3 línguas oficiais (`pt_br.json`, `en_us.json`, `es_es.json`).
    - Suíte de testes dedicada `Fase21ExtremophileMycologyTest`, expandindo a suíte para **634 testes automatizados** com 100% de sucesso e zero comentários.
46. `27b62d0`: Implementação completa da **Fase 22: Bio-Farmacologia Tecnológica, Hipo-Injetores & Síntese Farmacêutica (Substituição de Poções Vanilla)**:
    - Criação do Hipo-Injetor Pneumático Portátil (`hypo_injector` / `HypoInjectorItem`) em liga de titânio escuro com cartucho pneumático pressurizado reutilizável, acionamento estéril instantâneo (0.2s), compatibilidade com mão secundária, som pneumático característico (`item.hypo_injector.use`) e telemetria no HUD de sobrevivência (`SurvivalHudOverlay`).
    - Formulação científica de 7 ampolas e estimulantes farmacológicos fundamentados em biologia real e compostos purificados na Fase 21:
      - Ampola Neuro-Adrenérgica (`adrenal_stim`): Alcaloides adrenérgicos + solução salina -> +30% velocidade de movimento e sprint (+15%) *(substitui Speed)*.
      - Bio-Espuma Coagulante Molecular (`biofoam_cartridge`): Quitosana catiônica + biopolímeros flexíveis -> cura imediata de 4 corações e regeneração tecidual acelerada *(substitui Healing & Regen)*.
      - Estimulador Miomecânico de Torque (`myomer_stim`): Alcaloides neuro-ativos + quitina de verme -> +40% de força muscular e torque cinético *(substitui Strength)*.
      - Sérum Endotérmico Refratário (`endothermic_serum`): Glicerol osmoprotetor + seiva pesada -> imunidade completa a chamas e radiação solar por 5 minutos *(substitui Fire Resistance)*.
      - Sérum Anti-Inercial Gravitacional (`grav_dampener_stim`): Concentrado de trealose + quartzo piezoelétrico -> saltos de 2.5 blocos e desaceleração terminal suave *(substitui Jump Boost & Slow Falling)*.
      - Ampola de Desintoxicação Celular (`detox_ampoule`): Matriz de melanina quelante + água pura -> purga imediata de venenos, choque e náuseas *(substitui Antídotos / Leite)*.
      - Emulsão de Refração Óptica Furtiva (`stealth_nano_drape`): Biopolímeros + vidro de fulgurito micronizado -> invisibilidade de 90s contra radares e sensores térmicos *(substitui Invisibility)*.
    - 7 receitas shaped data-driven balanceadas em `data/sandstorm/recipe/` e integração ao JEI e livro de receitas.
    - 8 novas texturas pixel-art 16x16 com assinaturas PNG válidas, modelos JSON de item e definições em `assets/sandstorm/items/`.
    - Localização trilingue completa e sincronizada (`pt_br.json`, `en_us.json`, `es_es.json`).
    - Suíte de testes automatizados `Fase22BioPharmacologyTest`, elevando o total do repositório para **671 testes automatizados** com 100% de sucesso e zero comentários.
47. `fase23-molecular-modifier`: Implementação completa da **Fase 23: Bancada de Modificação Molecular, Overclocks de Hardware & Nanocoatings (Substituição de Encantamentos Vanilla)**:
    - Bancada de Modificação Molecular (`molecular_modifier` / `MolecularModifierBlock` / `MolecularModifierBlockEntity`): maquinário industrial de engenharia de precisão com suporte a WPT (buffer de 10.000 J, consumo de 500 J / 10 J/t por ciclo), queima de baterias/células de combustível, integração com a Fabric Transfer API (`ItemStorage.SIDED`), e partículas dinâmicas no cliente (arcos elétricos e feixes).
    - 12 Módulos de Hardware, Overclocks e Nanocoatings (`MolecularUpgradeItem` / `UpgradeType`):
      - 3 Overclocks de Armas: Bobina Ressonadora de Alta Frequência (`vibro_resonator_module` -> Afiação III), Emissor Térmico de Plasma (`thermal_plasma_emitter` -> Aspecto Flamejante II), Acelerador de Pulso Concussivo (`kinetic_focus_module` -> Repulsão II).
      - 3 Overclocks de Mineração: Núcleo de Cavitação Ultrassônica (`cavitation_frequency_core` -> Eficiência IV), Desintegrador de Fase Atômica (`atomic_phase_disrupter` -> Toque Suave I), Espectrômetro de Ressonância Densimétrica (`spectrometric_sifter` -> Fortuna III).
      - 2 Nanocoatings Universais: Matriz de Nanorobôs Auto-Reparadores (`self_healing_nanite_matrix` -> Remendo I), Revestimento Diamantado de Titânio (`titanium_lattice_coating` -> Inquebrabilidade III).
      - 4 Revestimentos de Chassi/Armadura: Grelha de Amortecimento Balístico (`ballistic_dampener_mesh` -> Proteção IV), Blindagem Cerâmica Ablativa (`ablative_thermal_plating` -> Proteção contra Fogo IV), Amortecedores Pneumáticos de Vácuo (`pneumatic_fall_dampers` -> Peso Pena IV), Placas de Descarga Reativa (`reactive_shock_plating` -> Espinhos III).
    - Interface Gráfica Cyberpunk Industrial (`MolecularModifierMenu` / `MolecularModifierScreen`): câmara de feixes litográficos animados, soquetes dedicados de overclock e nanocoating, monitor de buffer WPT, proteção de slot de saída e ContainerData sincronizado.
    - 13 Receitas shaped data-driven balanceadas em `data/sandstorm/recipe/` e tabela de saque (`molecular_modifier.json`).
    - 17 Novas texturas pixel-art 16x16 com assinaturas PNG válidas, modelos JSON de bloco e item, e definições em `assets/sandstorm/items/`.
    - Localização trilingue completa e sincronizada (`pt_br.json`, `en_us.json`, `es_es.json`).
    - Suíte de testes automatizados `Fase23MolecularModifierTest`, elevando o total do repositório para **729 testes automatizados** com 100% de sucesso e zero comentários.
48. `fase24-medbay-and-magic-suppression`: Implementação completa da **Fase 24: Estação Médica Bio-Regenerativa (MedBay Pod) & Purificação Tecnológica do Vanilla**:
    - Cápsula de Regeneração Celular MedBay (`bio_regeneration_pod` / `BioRegenerationPodBlock` / `BioRegenerationPodBlockEntity`): maquinário médico avançado de terapia intensiva e criostase com veículo de assento (`MedBaySeatEntity`), recuperação celular acelerada, purga de efeitos negativos, recarga de oxigênio/energia do traje e estabilização térmica para 37.0°C em `PlayerSuitSavedData`.
    - Tanque interno de fluidos (4000 mB) com suporte bidirecional à Fabric Transfer API (`FluidStorage.SIDED`, `ItemStorage.SIDED`), aceitando injeção de água potável por tubulações ou recipientes manuais, e consumo de biomateriais estabilizadores (quitosana, trealose, melanina, biofoam).
    - Interface Gráfica Dark-Tech Cyberpunk (`BioRegenerationPodScreen` / `BioRegenerationPodMenu`): osciloscópio de monitor cardíaco ECG com onda P-QRS-T animada em tempo real sincronizada ao batimento cardíaco, readout dinâmico de BPM (60 a 95 BPM), status biométrico e barra vertical de nível de fluidos.
    - Purificação Tecnológica e Supressão de Magia Vanilla (`MagicSuppressionHandler`): bloqueio em tempo de execução da colocação e uso de mesa de encantamentos, suporte de poções e bigornas; supressão de bruxas; e anulação definitiva das receitas vanilla em `data/minecraft/recipe/`.
    - Cadeia completa de assets 1.21.4 (blockstate, modelos de bloco ativo/inativo, modelo de item, definição de renderização em `assets/sandstorm/items/` e 6 texturas PNG com assinaturas válidas).
    - Localização trilingue completa e sincronizada (`pt_br.json`, `en_us.json`, `es_es.json`).
    - Suíte de testes automatizados `Fase24MedBayAndMagicSuppressionTest`, elevando o total do repositório para **745 testes automatizados** com 100% de sucesso e zero comentários.
49. `fase25-bio-cybernetics`: Implementação completa da **Fase 25: Bio-Cibernética Fundamental, Incubadora de Chassis & Núcleos Neurais Biônicos**:
    - Incubadora Bio-Cibernética (`cyborg_incubator_vat` / `CyborgIncubatorVatBlock` / `CyborgIncubatorVatBlockEntity`): maquinário monumental de biogestação e montagem molecular biomecânica com suporte WPT (25.000 J), reservatório de fluidos (8000 mB) com Fabric Transfer API (`FluidStorage.SIDED`, `ItemStorage.SIDED`), e máquina de 4 estágios de gestação de ciborgue.
    - 5 Novos componentes e itens cibernéticos em `SandStormItems`:
      - Chassi Esquelético de Titânio-Quitina (`biomechanical_chassis_frame`, Rarity.RARE).
      - Feixes de Miômeros Artificiais Eletroativos (`synthetic_myomer_bundle`, Rarity.RARE).
      - Núcleo Neural Biônico "Wetware" (`bio_neural_core`, Rarity.EPIC).
      - Cânister de Hemolinfa Bio-Refrigerante (`bio_coolant_canister`, Rarity.UNCOMMON).
      - Chassi Cibernético Biogestado (`assembled_cyborg_frame`, Rarity.EPIC).
    - Interface Gráfica Cyberpunk Dark-Tech (`CyborgIncubatorScreen` / `CyborgIncubatorMenu`): câmara de gestação holográfica vetorial dinâmica com silhueta do endoesqueleto, miômeros carmesim, núcleo neural pulsante em roxo/ciano, bio-refrigerante com bolhas em ascensão e scanline vertical em tempo real, monitor de sincronização tecidual (%), tanque vertical de 8000 mB e barra de perfusão.
    - Cadeia completa de assets 1.21.4 (blockstate, 2 modelos de bloco, 6 modelos de item, 6 definições em `assets/sandstorm/items/` e 11 texturas PNG pixel-art 16x16 com assinaturas válidas).
    - 5 Receitas data-driven balanceadas em `data/sandstorm/recipe/` e tabela de saque (`cyborg_incubator_vat.json`).
    - Localização trilingue completa e sincronizada (`pt_br.json`, `en_us.json`, `es_es.json`) com 100% de paridade auditada por `I18nParityTest`.
    - Suíte de testes dedicada `Fase25BioCyberneticsTest`, elevando o total do repositório para **790 testes automatizados** com 100% de sucesso e zero comentários.
50. `9bfa6f0` (`fase26-autonomous-cyborgs`): Implementação completa da **Fase 26: Ciborgues Especialistas Autônomos & Uplink de Comando Holográfico**:
    - **Transmissor Holográfico de Comando (`CyberneticCommandUplinkItem` / `cybernetic_command_uplink`)**:
      - Dispositivo portátil em titânio escuro com display tátil holográfico e antena emissora ciano neon.
      - 3 Modos Operacionais com alternância instantânea por Shift + Clique (`MINING`, `BUILDING`, `HARVESTING`) com feedback sonoro e notificações na interface.
      - Demarcação tridimensional de zonas de trabalho: registro de Corner A e Corner B no mundo com cálculo dinâmico do volume delimitado (X x Y x Z) e feedback sônico de sinos e carrilhões.
      - Atribuição de diretrizes operacionais e zoneamento por clique direito direto em qualquer ciborgue especialista, com vinculação automática de operador (`OwnerUUID`) e abertura do menu de comando.
    - **3 Entidades de Ciborgues Especialistas Autônomos (`CyborgEntity` base / `PathfinderMob`)**:
      - **Ciborgue Escavador (`cyborg_excavator` / `CyborgExcavatorEntity`)**: 80 HP, 12 armadura, 8 dano; IA autônoma de perfuração vertical descendente segura de minérios e blocos na zona demarcada, evitando quebras acidentais e colapsos, braço broca de vibro-cavitação atômica e navegação precisa.
      - **Ciborgue Construtor (`cyborg_builder` / `CyborgBuilderEntity`)**: 60 HP, 10 armadura, 6 dano; IA autônoma de assentamento de blocos de baixo para cima consumindo `BlockItem`s de seu inventário interno de 18 slots e emitindo faíscas de solda laser.
      - **Ciborgue Colhedor (`cyborg_harvester` / `CyborgHarvesterEntity`)**: 50 HP, 8 armadura, 5 dano; IA de identificação e colheita seletiva de culturas agrícolas e extremófilos maduros, replantio imediato e sucção de `ItemEntity`s soltos na zona demarcada.
    - **Rotinas Comportamentais & Visor Óptico Adaptativo**:
      - Rotinas em `CyborgRoutine`: Trabalho Autônomo (`AUTONOMOUS_WORK`), Seguir Operador (`FOLLOW_OPERATOR`), Patrulha Perimétrica (`PATROL_PERIMETER`) e Retorno à Doca (`RETURN_TO_DOCK`).
      - Visor óptico adaptativo sincronizado dinamicamente por SynchedEntityData (Ciano para Trabalho Autônomo, Âmbar para Seguir, Vermelho para Patrulha, Roxo para Retorno à Doca).
    - **Interface Gráfica de Telemetria Biônica (`CyborgTelemetryMenu` / `CyborgTelemetryScreen`)**:
      - Chassi futurista de 176x186 com bordas neon `#00E5FF`, 18 slots internos do ciborgue e 36 do operador.
      - Telemetria em tempo real de Joules WPT (0 a 50.000 J), bio-refrigerante de hemolinfa (0 a 4.000 mB) e integridade miomérica tecidual (0 a 100%).
      - Seletor tátil de rotina com 4 botões remotos com feedback de clique e highlight de estado ativo.
    - **Modelos e Renderers Client 1.21.4 (`CyborgModel`, `CyborgRenderState`, `CyborgRenderer`)**:
      - Malha bípede biomecânica detalhada com exo-coluna dorsal, reator de arco peitoral emissivo, braço mecânico especializado por classe e animações dinâmicas de respiração, marcha e trabalho.
    - **Cadeia Completa de Assets 1.21.4 & Receitas Data-Driven**:
      - 4 Definições em `assets/sandstorm/items/` e 4 modelos de itens em `assets/sandstorm/models/item/`.
      - 4 Texturas de itens 16x16 pixel-art com assinaturas PNG válidas e 3 skins de entidades ciborgues 64x64 em `textures/entity/cyborg/`.
      - 4 Receitas shaped balanceadas em `data/sandstorm/recipe/` (uplink e 3 spawn eggs de montagem cibernética).
    - **Localização Trilingue**:
      - Paridade 100% nas 3 línguas oficiais (`pt_br.json`, `en_us.json`, `es_es.json`).
    - **Validação Automatizada de Testes**:
      - Suíte de testes dedicada `Fase26AutonomousCyborgsTest`, elevando o total do repositório para **820 testes automatizados** com 100% de sucesso e zero comentários.
51. `fase27-swarm-and-docking`: Implementação completa da **Fase 27: Enxame Cibernético (Swarm Intelligence), Doca de Recarga & Módulos de Upgrade Biônicos**:
    - **Doca de Manutenção e Recarga WPT (`cyborg_docking_station` / `CyborgDockingStationBlock` / `CyborgDockingStationBlockEntity`)**:
      - Maquinário 1x1x1 em titânio e cerâmica piezoelétrica com capacitor WPT de 100.000 J, reservatório interno de bio-refrigerante de hemolinfa e integração total à Fabric Transfer API (`ItemStorage.SIDED`).
      - Protocolo de auto-acoplamento inteligente para ciborgues retornando de missões, recarga ultrarrápida (500 J/tick), injeção de refrigerante e descarregamento automático de inventários.
      - Interface Gráfica Cyberpunk Industrial (`CyborgDockingStationScreen` / `CyborgDockingStationMenu`) com telemetria ao vivo da doca e do ciborgue ancorado, botão de desacoplamento de emergência com feedback de clique sonoro.
    - **Inteligência de Enxame Coordenada & Protocolo Voxel Mutex (`CyborgSwarmManager`)**:
      - Gestor central de malha de enxame distribuído com Voxel Mutex (`claimBlock`, `releaseBlock`, `isClaimed`) prevenindo colisões de mineração entre escavadores e disputas de assentamento entre construtores.
      - Registro e busca dinâmica da Doca de Recarga mais próxima para recolhimento autônomo de unidades com bateria baixa.
    - **4 Módulos de Upgrade Biônico Intercambiáveis (`CyborgUpgradeItem` / `CyborgUpgradeType`)**:
      - Blindagem de Quitina Ácida (`acid_chitin_plating`): +20 HP e proteção contra ácido e espinhos.
      - Célula Criogênica de Trealose (`cryo_trehalose_cell`): -50% de taxa de consumo de bio-refrigerante.
      - Lente Óptica LiDAR de Longo Alcance (`long_range_lidar_lens`): Raio de varredura dobrado de 16 para 32 blocos.
      - Propulsor Hover Piezoelétrico (`piezo_hover_thruster`): Elevação de passo para 1 bloco e velocidade aumentada em 30%.
    - **Dock Lateral de Upgrades no `CyborgTelemetryScreen`**:
      - Painel lateral dedicado adjacente ao chassi principal com 4 slots visuais hexagonais para upgrades, indicadores neon ativos/inativos e tooltips descritivos em tempo real.
    - **Cadeia Completa de Assets 1.21.4 & Receitas Data-Driven**:
      - Blockstate, modelos ativo/inativo da doca, 5 modelos de itens, 5 definições em `assets/sandstorm/items/` e 5 novas texturas PNG com assinaturas válidas.
      - 5 Receitas shaped balanceadas em `data/sandstorm/recipe/` e tabela de saque da doca.
      - Paridade trilingue de localização (`pt_br`, `en_us`, `es_es`) auditada por `I18nParityTest`.
    - **Validação Automatizada de Testes**:
      - Suíte de testes dedicada `Fase27SwarmAndDockingTest`, elevando o total do repositório para **855 testes automatizados** com 100% de sucesso e zero comentários.
52. `fase28-holo-tactical-spire`: Implementação completa da **Fase 28: Torre de Projeção Holo-Tática & Matriz Neural Coletiva (Hivemind Holo-Tactical Spire & Neural Mesh)**:
    - **Torre de Projeção Holo-Tática (`holo_tactical_spire` / `HoloTacticalSpireBlock` / `HoloTacticalSpireBlockEntity`)**:
      - Estrutura vertical imponente com capacitor WPT de 100.000 J, estados `FACING` e `ACTIVE`, emissor holográfico ciano e slot frontal para sondas de reconhecimento.
      - Suporte integral à Fabric Transfer API (`ItemStorage.SIDED`, `ContainerStorage::of`).
    - **Console de Comando Tático do Enxame (RTS Holo-Tactical Interface)**:
      - Interface visual de alta performance (`HoloTacticalSpireScreen` / `HoloTacticalSpireMenu`):
        - Display de radar cartográfico vetorial com feixe giratório de varredura holográfica em 360°, retícula polar e grade cartesiana (X, Z).
        - Rastreamento dinâmico em tempo real de ciborgues especialistas com glifos geométricos com cores de status de rotina (Ciano para Trabalho Autônomo, Âmbar para Seguir, Vermelho para Patrulha, Roxo para Retorno à Doca).
        - Marcadores luminosos das docas de recarga conectadas.
        - Seletor tátil de 4 Ordens Globais Macro-Estratégicas com broadcast instantâneo ao `CyborgSwarmManager` (Convergência, Otimização, Patrulha Perimétrica, Alerta Sísmico de Evacuação).
    - **Módulo de Interface Neural ("Cerebral Synapse Link" - `neural_synapse_link`)**:
      - Matriz bio-silício para uplink direto entre o capacete do traje espacial e a consciência coletiva do enxame.
    - **Sonda de Reconhecimento Orbital de Baixa Altitude (`orbital_recon_probe`)**:
      - Sonda aeroespacial descartável com telemetria sônica, alarme antecipado de tempestades e rastreamento de ameaças sísmicas.
    - **Cadeia Completa de Assets 1.21.4 & Receitas Data-Driven**:
      - Blockstate, modelos ativo/inativo da torre, modelos de item, 3 definições em `assets/sandstorm/items/` e 6 texturas PNG pixel-art de alta fidelidade com assinaturas válidas.
      - 3 Receitas shaped balanceadas em `data/sandstorm/recipe/` e tabela de saque para a torre.
      - Paridade trilingue de localização (`pt_br`, `en_us`, `es_es`) auditada por `I18nParityTest`.
    - **Validação Automatizada de Testes**:
      - Suíte de testes dedicada `Fase28HoloTacticalSpireTest`, elevando o total do repositório para **872 testes automatizados** com 100% de sucesso e zero comentários.
53. `sandstorm-debug-suite`: Implementação da **Suíte Unificada de Comandos de Debug para Todas as Fases (Fases 1 a 28), Spawns de Robôs/Entidades e Geração de Estruturas Completas**:
    - **Comandos de Kits por Fase (`/sandstorm debug phase <1..28|all>` & `/sandstorm_debug phase <1..28|all>`)**:
      - Suporte a todas as 28 fases do SandStorm com kits de inventário sob medida, suprindo desde trajes espaciais, água, ferramentas primárias e maquinários industriais, até biorreatores, farmacologia, modificações moleculares, robótica avançada, enxames de ciborgues e torres holo-táticas.
    - **Spawns de Robôs e Veículos (`/sandstorm debug spawn|robot <entidade>`)**:
      - 9 Entidades e robôs cobertos: `cyborg_excavator`, `cyborg_builder`, `cyborg_harvester`, `excavator_vehicle`, `megazord`, `cargo_drone`, `builder_drone`, `sandworm` (em modo showcase com escala titânica) e `sandboard`.
    - **Geração Procedural de Estruturas e Postos Tecnológicos (`/sandstorm debug setup|structure <instalação>`)**:
      - 15 Instalações e bases completas: `cyborg_outpost`, `medbay_clinic`, `bioreactor_lab`, `molecular_workshop`, `spike_fortress`, `power_station`, `megastructure_site`, `hydroponics_dome`, `deep_drill_station`, `defense_perimeter`, `refinery_complex`, `terraformer_dome`, `maglev_station`, `starter_base`, `ancient_ruin_site`.
    - **Comandos Operacionais Complementares**:
      - `/sandstorm debug suit refill|drain`: Recarga e teste de estresse térmico/oxigênio do traje espacial.
      - `/sandstorm debug swarm status|reset|order <0..3>`: Monitoramento de Voxel Mutex, liberação de travas espaciais e broadcast de diretrizes estratégicas.
      - `/sandstorm debug weather start [intensidade]|stop`: Controle de tempestades de areia iônicas.
      - `/sandstorm debug seismic`: Convocação imediata de encontro com Verme de Areia Colossal.
      - `/sandstorm debug list`: Catálogo in-game de todos os comandos e parâmetros.
    - **Validação Automatizada de Testes**:
      - Suíte de testes dedicada `SandstormDebugTest`, elevando o total do repositório para **877 testes automatizados** com 100% de sucesso e zero comentários.
54. `fases-29-33-roadmap`: Concepção arquitetural e planejamento monumental das **Fases 29 a 33** no `TASKS.md`:
    - **Fase 29 (Domo de Escudo de Plasma Planetário, Canhão Cinético Anti-Titã & Defesa de Ponto Sônica)**:
      - Gerador de escudo de plasma com bobinas toroidais supercondutoras (1.000.000 J WPT), projeção holográfica de cúpula com hexágonos dinâmicos, repulsão de vermes e projéteis; canhão ferroviário hipersônico duplo de cerco anti-titã e grade de defesa acústica.
    - **Fase 30 (Mineração Geotérmica Profunda, Poço do Manto Planetário & Extratores Magmáticos de Lítio-Plasma)**:
      - Broca monumental de perfuração até a rocha-mãe (Bedrock, Y=-64) com amortecedores sísmicos, centrífuga térmica de enriquecimento de plasma e trocador de calor supercrítico gerando 10.000 J/tick, superligas do manto e cápsulas criogênicas de lítio.
    - **Fase 31 (Rede Orbital de Satélites, Telescópio Espacial de Varredura & Lançador de Cargas Eletromagnético)**:
      - Catapulta eletromagnética Gauss linear (Mass Driver) de 12 blocos de altura com aceleração hipersônica, console de estação terrena com antena parabólica rastreadora, telescópio espacial de varredura profunda e 4 satélites de carga útil (meteorológico, espelho solar 24/7, radar SAR geológico e lança cinética orbital "Vara de Deus").
    - **Fase 32 (Atmosfera Artificial, Condensação de Nuvens em Escala Continental & O Primeiro Dilúvio)**:
      - Reator de craqueamento catalítico de plasma com chaminé ionizante, obuseiro balístico de semeadura higroscópica de nuvens com cápsulas de iodeto de prata e glicerol, torre condensadora e o clímax ecológico: "O Primeiro Dilúvio" (trovões distantes, chuva torrencial, extinção do calor de 48°C, conversão em solo fértil e formação de lagos de água doce).
    - **Fase 33 (Elevador Espacial Planetário, Farol Quântico Subespacial & Transmissão Interestelar de Resgate)**:
      - Âncora megalítica 4x4x4 com tensão ativa magnética, cabos trançados de nanotubos de carbono-grafeno, vagão suborbital maglev vertical com subida triunfante pela atmosfera até a órbita estrelada (Y=500+), farol quântico de comunicação superluminal em feixe helicoidal de táquions e clímax da missão com mensagem de resgate da Federação Interestelar e conquista "Soberano do Deserto".
    - Expansão do catálogo oficial de efeitos sonoros com 13 novos áudios customizados e planejamento de kits e comandos `/sandstorm debug phase <29..33>`.
55. `fase29-done`: Implementação completa da **Fase 29: Clonagem Quântica do Jogador, Cápsulas de Estase ("Quantum Sleeper Pods"), Transferência de Consciência ("Ego-Casting") & Respawn por Proximidade**:
    - **Cápsula de Estase e Gestação Biológica (`quantum_sleeper_pod` / `QuantumSleeperPodBlockEntity` / `QuantumSleeperPodBlock`)**:
      - Maquinário bio-quântico de 100.000 J conectado à malha WPT sem fio, com câmara de estase para corpos clonados e propriedades `FACING`, `ACTIVE`, `OCCUPIED`.
      - Sistema de nutrição biológica e bio-síntese celular consumindo metabólitos da Fase 21 (quitosana, trehalose, glicerol e água potável) para gestação orgânica do clone.
      - 42 slots dedicados de armazenamento físico por cápsula: inventário completo isolado (36 slots), armadura (4 slots), mão secundária (1 slot) e slot de insumos biológicos (1 slot). Suporte total à Fabric Transfer API (`ItemStorage.SIDED`).
    - **Matriz de Consciência Quântica (`quantum_mind_matrix`)**:
      - Componente tecnológico de alta densidade manufaturado com matriz de silício, circuitos de liga titânio-quitina e neuro-alcaloides, permitindo o emparelhamento sináptico com a malha quântica.
    - **Rede de Consciência Quântica Persistente (`CloneNetworkSavedData`)**:
      - Persistência de dados mundiais conforme o padrão moderno 1.21.4 (`SavedDataType` com `RecordCodecBuilder`), mantendo o mapeamento de cápsulas registradas por UUID de jogador e dimensão.
      - Algoritmo de busca euclidiana de proximidade (`findNearestReadyPod`) e busca de alvo de transferência remota (`findTargetPodForTransfer`).
    - **Mecânica de Transferência de Consciência ("Ego-Casting")**:
      - Teletransporte instantâneo da consciência do jogador entre corpos físicos mantidos em cápsulas conectadas à rede.
      - Isolamento estrito de inventário físico: ao transferir a mente, todo o inventário atual (itens, armadura, offhand) permanece armazenado na cápsula de partida com o corpo adormecido, enquanto o corpo receptor descarrega seu inventário específico para o jogador, eliminando qualquer duplicação de itens.
    - **Protocolo de Respawn de Emergência por Proximidade (`FusedSpaceSuitHandler`)**:
      - No evento de morte do jogador (`ServerPlayerEvents.AFTER_RESPAWN`), o sistema localiza a cápsula com clone pronto fisicamente mais próxima das coordenadas de óbito. O jogador reanima diretamente na cápsula mais próxima com os equipamentos que estavam nela pré-equipados.
    - **Interface Gráfica Cyberpunk (`QuantumSleeperMenu` / `QuantumSleeperScreen`)**:
      - Monitor de telemetria com medidor vertical de energia WPT ciano, medidor de bio-nutrientes âmbar, status de ocupação do clone e botões de ação ("Gestar Clone" e "Transferir Consciência").
    - **Comandos de Debug e Teste Automatizado (`SandstormDebugCommand`)**:
      - `/sandstorm debug phase 29` (ou `/sandstorm_debug phase 29`): Entrega de kit completo de clonagem.
      - `/sandstorm debug setup clone_facility`: Geração automatizada de complexo laboratorial com duas cápsulas operacionais interligadas (Pod Alpha e Pod Beta) para teste imediato de ego-casting.
    - **Validação Automatizada de Testes**:
      - Suíte de testes dedicada `Fase29QuantumCloningTest` e ampliação de `SandstormDebugTest`, elevando o total do repositório para **892 testes automatizados** com 100% de aprovação e zero comentários.
56. `fase30-done`: Implementação completa da **Fase 30: Domo de Escudo de Plasma Planetário, Canhão Cinético Anti-Titã & Grade Acústica Perimétrica (Planetary Plasma Defense Grid & Anti-Titan Kinetic Railgun)**:
    - **Gerador de Escudo de Plasma Planetário (`plasma_shield_generator` / `PlasmaShieldGeneratorBlockEntity` / `PlasmaShieldGeneratorBlock`)**:
      - Buffer de 1.000.000 J WPT, upkeep de 100 J/tick e deflexão de projéteis e repulsão dinâmica de monstros e vermes de areia colossais num raio configurável de até 48 blocos.
      - Sincronização em tempo real com `KineticShieldTracker` e interface `PlasmaShieldScreen` com telemetria de ameaças repelidas e medidor de energia.
    - **Canhão Cinético Anti-Titã / Railgun Ferroviário (`kinetic_railgun` / `KineticRailgunBlockEntity` / `KineticRailgunBlock`)**:
      - Buffer de 250.000 J WPT, compartimento de munição interna de 9 slots (`WorldlyContainer` / Transfer API) para projéteis `kinetic_slug`.
      - Sistema de mira automática em 64 blocos com prioridade absoluta para Sandworms e ameaças hostis, causando 50.0 de dano com hipervelocidade cinética e repulsão vetorial.
    - **Pilão de Defesa Acústica Perimétrica (`acoustic_defense_pylon` / `AcousticDefensePylonBlockEntity` / `AcousticDefensePylonBlock`)**:
      - 50.000 J buffer, 10 J/tick de consumo e raio de 32 blocos de amortecimento sísmico total.
      - Integração com `AcousticDefenseTracker` e `SeismicSurvivalHandler`, anulando acúmulo de vibrações que atraem vermes colossais.
    - **Itens e Componentes**:
      - `kinetic_slug` (Projétil hiperdenso de liga titânio-quitina), `superconductor_toroid` (Bobina toroidal supercondutora de plasma), `plasma_focus_crystal` (Cristal piezelétrico de foco de plasma).
    - **Cadeia Completa de Assets 1.21.4 & Data-Driven Recipes**:
      - 3 blockstates, 6 modelos de bloco (ativos e inativos), 6 modelos de item, 6 definições em `assets/sandstorm/items/` e 17 texturas PNG geradas com headers válidos.
      - 6 receitas em `data/sandstorm/recipe/` e 3 tabelas de saque de bloco.
      - Paridade linguística (`en_us`, `pt_br`, `es_es`).
    - **Suíte de Debug & Teste**:
      - `/sandstorm debug phase 30`: Kit completo com todas as defesas de plasma, munições, toroides e cristais.
      - `/sandstorm debug setup plasma_defense_complex`: Criação imediata do complexo fortificado de defesa com gerador de plasma, railgun carregado e pilões perimétricos.
    - **Validação de Testes**:
      - Suíte `Fase30PlasmaDefenseTest` e `SandstormDebugTest`, elevando o mod para **916 testes automatizados** com 100% de sucesso, conformidade de arquitetura e zero comentários.
57. `fase31-done`: Implementação completa da **Fase 31: Mineração Geotérmica Profunda, Poço do Manto Planetário & Extratores Magmáticos de Lítio-Plasma (Deep Core Geothermal Well, Mantle Borehole & Litho-Plasma Siphon)**:
    - **Broca de Perfuração do Manto Planetário (`deep_core_borehole` / `DeepCoreBoreholeBlockEntity` / `DeepCoreBoreholeBlock`)**:
      - Buffer de 500.000 J WPT, 250 J/tick de consumo operacional, tanque interno de fluido de 8.000 mB compatível com Fabric Transfer API (`FluidStorage.SIDED`).
      - Cabeçote com broca de diamante policristalino (`geothermal_core_drill_bit`) estendendo poço até a rocha-mãe (Y <= -64), operando com refrigeração por água e gerando sais brutos de lítio (`raw_lithium_salts`).
    - **Extrator Magmático de Lítio-Plasma (`litho_plasma_extractor` / `LithoPlasmaExtractorBlockEntity` / `LithoPlasmaExtractorBlock`)**:
      - Buffer de 250.000 J WPT, 150 J/tick de consumo de centrifugação térmica, refinando sais brutos com recipientes criogênicos para obter cápsulas criogênicas de lítio superaquecido (`superheated_lithium_capsule`), lingotes de superliga do manto (`mantle_alloy_ingot`) e subprodutos minerais raros.
    - **Trocador de Calor Supercrítico (`supercritical_heat_exchanger` / `SupercriticalHeatExchangerBlockEntity` / `SupercriticalHeatExchangerBlock`)**:
      - Buffer de 1.000.000 J WPT, câmara de 8.000 mB de água e matriz dissipadora para aletas térmicas (`thermal_radiator_fin`) e cápsulas de lítio, gerando entre 2.500 J/tick e **10.000 J/tick** diretamente na malha sem fio WPT.
    - **Cadeia Completa de Assets 1.21.4 & Data-Driven Recipes**:
      - 3 blockstates, 6 modelos de bloco com estados lit, 8 modelos de item, 8 definições em `assets/sandstorm/items/` e 20 texturas PNG com cabeçalhos binários válidos.
      - 7 receitas balanceadas em `data/sandstorm/recipe/` e 3 tabelas de saque de bloco.
      - Paridade linguística nos 3 idiomas suportados (`pt_br`, `en_us`, `es_es`).
    - **Suíte de Debug & Teste**:
      - `/sandstorm debug phase 31`: Kit completo com perfuratriz, extrator, trocador, brocas, aletas e superligas.
      - `/sandstorm debug setup geothermal_well`: Complexo geotérmico pronto com perfuratriz carregada, extrator centrifugador, trocador e tubulações inteligentes conectadas à rede WPT.
    - **Validação de Testes**:
      - Suíte dedicada `Fase31GeothermalWellTest` e atualização de `SandstormDebugTest`, elevando o mod para **940 testes automatizados** com 100% de sucesso, conformidade estrita de arquitetura e zero comentários.
58. `fase32-done`: Implementação completa da **Fase 32: Aprimoramento e Variantes Titânicas de Megazords (Megazord Specialized Upgrade Modules: Aerospace Flight & Deep Submersible Aquatic Operation)**:
    - **Aprimoramentos Modulares de Mecha (`MegazordEntity` & `MegazordVariant`)**:
      - Suporte modular a upgrades instaláveis em tempo de execução via interação direta com a mão principal do jogador ou montagem guiada no Pátio de Montagem (`AssemblyBayBlock`).
      - Variantes sincronizadas em tempo real via `SynchedEntityData` (`DATA_VARIANT`, `DATA_FLIGHT_MODULE`, `DATA_SUBMERSIBLE_MODULE`, `DATA_OVERDRIVE_MODULE`):
        - `STANDARD`: Bípede titânico de assalto terrestre.
        - `AERO_STRIKER`: Mecha voador aeroespacial equipado com propulsão a jato e asas de empuxo vetorial.
        - `ABYSSAL_SUB`: Submersível abissal com selagem hidrostática hiperbárica e bombas de lastro.
        - `APEX_DOMINATOR`: Chassi anfíbio e aeroespacial com todos os módulos instalados e reator de sobrecarga tática.
    - **Capacidade de Voo Tridimensional (Megazords Voadores)**:
      - Ao equipar o Módulo de Voo Aeroespacial (`megazord_flight_module`), a gravidade é anulada em voo sob controle do piloto (`setNoGravity(true)`).
      - Navegação tridimensional livre seguindo o vetor de visão do jogador (`player.getLookAngle()`), subida vertical com tecla de pulo (`jumping`), flutuação estável (hovering) e ejeção de partículas de plasma e faíscas elétricas.
      - Consumo balanceado de energia (6 J/tick em aceleração aerodinâmica).
    - **Capacidade Subaquática Infinita (Megazords Subaquáticos Abissais)**:
      - Ao equipar o Casco Subaquático Abissal (`megazord_submersible_hull`), o Megazord opera infinitamente debaixo d'água sem qualquer risco de asfixia ou perda de ar para o piloto.
      - O suprimento de oxigênio do jogador é travado no valor máximo a cada tick (`player.setAirSupply(player.getMaxAirSupply())`) e recebe os efeitos contínuos de `CONDUIT_POWER` e `WATER_BREATHING`.
      - Flutuabilidade neutra e propulsão hidrostática 3D que permite mergulhar e emergir suavemente em aquíferos, trincheiras oceânicas e reservatórios subterrâneos.
    - **Módulo de Sobrecarga Tática (`megazord_tactical_overdrive`)**:
      - Expansão colossal da bateria do Megazord de 100.000 J para **250.000 J** (`OVERDRIVE_BATTERY_CAPACITY`).
      - Amplificação de velocidade de locomoção em 35% e potencialização do Canhão de Choque Sônico (`triggerSonicShockwave`), elevando o dano de 25.0 para **45.0** e o raio de dispersão de 16.0 para **24.0 blocos** com repulsão vetorial aumentada.
    - **Pátio de Montagem Avançado (`AssemblyBayBlock`)**:
      - Montagem direta de Megazords pré-equipados: `megazord_flight_module` monta Aero Striker (35.000 J), `megazord_submersible_hull` monta Submersível Abissal (35.000 J) e `megazord_tactical_overdrive` monta o Apex Dominator completo (50.000 J).
    - **Itens e Componentes da Fase 32**:
      - `megazord_flight_module`, `megazord_submersible_hull`, `megazord_tactical_overdrive`, `vectored_thruster`, `hydro_ballast_pump`.
    - **Cadeia Completa de Assets 1.21.4 & Data-Driven Recipes**:
      - 5 definições em `assets/sandstorm/items/`, 5 modelos de item e 5 texturas PNG válidas com cabeçalho binário padrão.
      - 5 receitas data-driven em `data/sandstorm/recipe/` utilizando exclusivamente identificadores válidos dos registros do mod.
      - Paridade linguística absoluta entre `pt_br.json`, `en_us.json` e `es_es.json`.
    - **Sistema de Quests & Datapad**:
      - Quest `megazord_upgrades` adicionada ao Capítulo 4 ("Era dos Mechas e Fortificações").
    - **Comandos de Debug e Suíte de Testes**:
      - `/sandstorm debug phase 32`: Kit completo de módulos, propulsores e ligas.
      - `/sandstorm debug spawn megazord_flight`, `/sandstorm debug spawn megazord_sub`, `/sandstorm debug spawn megazord_apex`.
      - Suíte dedicada `Fase32MegazordUpgradesTest` e atualização de `SandstormDebugTest`, elevando o projeto para mais de **970 testes automatizados** com 100% de sucesso, integridade física de assets e conformidade arquitetural com zero comentários.
