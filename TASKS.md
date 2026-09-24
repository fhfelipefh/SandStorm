# SandStorm - Quadro de Tarefas & Mensuração do MVP

Este documento consolida o andamento das fases de desenvolvimento do mod **SandStorm** para **Minecraft 26.3 (Fabric / Java 25)**, detalhando o que já foi concluído, as métricas de qualidade e o que resta para o fechamento do MVP.

---

## 📊 Métricas Gerais do Projeto

| Métrica | Valor Atual | Meta MVP | Status |
| :--- | :--- | :--- | :--- |
| **Versão Alvo** | Minecraft 26.3 | Minecraft 26.3 | ✅ Atingido |
| **Fabric Loader / API** | 0.19.5 / 0.160.7+26.3 | Compatibilidade Estável | ✅ Atingido |
| **Java SDK** | Java 25 | Java 25 | ✅ Atingido |
| **Testes Automatizados** | **627 testes** (0 falhas) | > 150 testes | ✅ Superado |
| **Rigor de Código (Zero Comentários)** | **0 linhas de comentários** | 0 linhas | ✅ 100% Auditado |
| **Arquitetura Desacoplada** | 100% isolamento de componentes | Zero acoplamento | ✅ Validado |
| **Receitas Data-Driven (JEI/REI)** | 70 receitas oficiais + Catálogo Interno de Projetos | Cobertura total | ✅ Concluído |
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

### 💉 Fase 21: Bio-Farmacologia Tecnológica, Hipo-Injetores & Ampolas Estimulantes (Substituição de Poções Vanilla)
- [ ] **Hipo-Injetor Pneumático Portátil (`hypo_injector`)**:
  - [ ] Dispositivo médico ergonômico em liga de titânio escuro com cartucho pneumático pressurizado reutilizável.
  - [ ] Mecânica de acionamento instantâneo (0.2s): carrega ampolas/stims no inventário e injeta diretamente no sistema circulatório do operador sem necessidade de desequipar o capacete do traje espacial.
  - [ ] Suporte a atalho tático de emergência (uso direto com a mão secundária ou tecla rápida configurável).
  - [ ] Sistema de barramento sonoro com sibilo pneumático característico de ejeção a gás estéril (`item.hypo_injector.use`).
  - [ ] Integração com o `SurvivalHudOverlay`: visor LED com indicador de doses restantes e telemetria de saturação metabólica.
- [ ] **Ampolas Bioquímicas & Stims de Alta Eficiência (Substitutos de Poções)**:
  - [ ] **Ampola Neuro-Adrenérgica (`adrenal_stim`)** *(Substitui Speed / Velocidade)*:
    - Estimula os impulsos nervosos e sobrecarrega temporariamente os servomotores dos membros inferiores por 3 minutos (+30% velocidade de movimento e +15% aceleração de sprint).
    - Fabricação: Refinaria Química (`chemical_refinery`) a partir de seiva pesada concentrada (`heavy_sap_bottle`), sal mineral e silício refinado.
  - [ ] **Bio-Espuma Coagulante Molecular (`biofoam_cartridge`)** *(Substitui Instant Health & Regeneration)*:
    - Espuma hemostática enriquecida com micro-nanorobôs médicos que sela perfurações e traumas graves em combate.
    - Aplica cura instantânea de 4 corações e regeneração celular acelerada por 15 segundos.
    - Fabricação: Fabricador de Nanitas (`nanite_fabricator`) a partir de quitina micronizada (`sandworm_chitin`) e biopolímeros flexíveis (`flexible_biopolymer`).
  - [ ] **Estimulador Miomecânico de Torque (`myomer_stim`)** *(Substitui Strength / Força)*:
    - Injeta nanofibras de polímero contrátil nos membros superiores, multiplicando o torque e a tração mecânica do exoesqueleto em +40% por 3 minutos.
    - Amplifica severamente o impacto cinético da Vibro-Crysknife, armas tecnológicas e golpes corpo a corpo.
  - [ ] **Sérum Endotérmico Refratário (`endothermic_serum`)** *(Substitui Fire Resistance / Resistência ao Fogo)*:
    - Nanogel de altíssima capacidade térmica que preenche as micro-câmaras do traje espacial, conferindo 5 minutos de imunidade total a chamas, radiação de plasma e insolação extrema no deserto.
  - [ ] **Sérum Anti-Inercial Gravitacional (`grav_dampener_stim`)** *(Substitui Slow Falling & Jump Boost)*:
    - Solução eletrolítica que sintoniza as solas das botas magnéticas com micro-campos repulsores: permite saltos verticais de 2.5 blocos e desaceleração terminal suave em quedas de penhascos e crateras rochosas.
  - [ ] **Ampola de Desintoxicação Celular (`detox_ampoule`)** *(Substitui Leite / Cura de Efeitos Negativos)*:
    - Agente quelante sintético de ação instantânea que neutraliza veneno ácido de verme de areia, radiação estática de fulgurito e estados de náusea em 1 segundo.
  - [ ] **Emulsão de Refração Óptica Furtiva (`stealth_nano_drape`)** *(Substitui Invisibility / Invisibilidade)*:
    - Revestimento fotônico que dobra feixes de luz visível ao redor do chassi do traje por 90 segundos, ocultando o jogador da linha de visão de sentinelas automáticas e criaturas mutantes da superfície.

### 🔬 Fase 22: Bancada de Modificação Molecular, Overclocks de Hardware & Nanocoatings (Substituição de Encantamentos)
- [ ] **Bancada de Modificação Molecular (`molecular_modifier`)**:
  - [ ] Maquinário tecnológico de engenharia de precisão que substitui a Mesa de Encantamentos (`enchanting_table`) e a Bigorna (`anvil`) convencionais.
  - [ ] Conexão à malha de energia sem fio WPT (consumo de 500 J por ciclo de calibração molecular).
  - [ ] Interface gráfica modular cyberpunk com baia de ancoragem do equipamento e soquetes: 3 slots para microchips/overclocks de firmware + 1 slot para revestimento químico (nanocoating).
  - [ ] Cabeçote laser litográfico embutido para gravação nanométrica em silício e titânio, eliminando penalidades cumulativas de custo de reparo ou mecânicas místicas de XP.
- [ ] **Módulos de Hardware & Overclocks para Armas (Substitutos de Encantamentos de Combate)**:
  - [ ] **Bobina Ressonadora de Alta Frequência (`vibro_resonator_module`)** *(Substitui Sharpness / Afiação)*:
    - Induz vibração molecular a 80.000 Hz na lâmina ou no bocal de disparo, fragmentando ligações atômicas de blindagens (+1.5 de dano por nível de módulo, Tiers I a V).
  - [ ] **Emissor Térmico de Plasma (`thermal_plasma_emitter`)** *(Substitui Fire Aspect & Flame)*:
    - Superaquece o gume ou os projéteis disparados, incinerando matéria orgânica e derretendo armaduras biológicas no impacto.
  - [ ] **Acelerador de Pulso Concussivo (`kinetic_focus_module`)** *(Substitui Knockback & Punch)*:
    - Dispara uma onda de choque pneumática no ponto de contato que repele e desestabiliza monstros e mechas a até 6 blocos de distância.
- [ ] **Microchips de Otimização para Ferramentas de Mineração (Substitutos de Encantamentos de Mineração)**:
  - [ ] **Núcleo de Cavitação Ultrassônica (`cavitation_frequency_core`)** *(Substitui Efficiency / Eficiência)*:
    - Sintoniza a frequência de impacto da Picareta de Silício com a densidade molecular da rocha, acelerando a taxa de extração em até 200%.
  - [ ] **Desintegrador de Fase Atômica (`atomic_phase_disrupter`)** *(Substitui Silk Touch / Toque Suave)*:
    - Dissocia os limites atômicos do bloco de forma controlada, permitindo a extração perfeitamente íntegra de vidros de fulgurito, colmeias fósseis e clusters de quartzo piezoelétrico.
  - [ ] **Espectrômetro de Ressonância Densimétrica (`spectrometric_sifter`)** *(Substitui Fortune / Fortuna)*:
    - Sensor espectrométrico microscópico que mapeia veios raros no ponto de impacto, maximizando o rendimento de silício, gemas piezoelétricas e carvão fóssil.
- [ ] **Nanotecnologia Estrutural & Auto-Regeneração (Substitutos de Mending & Unbreaking)**:
  - [ ] **Matriz de Nanorobôs Auto-Reparadores (`self_healing_nanite_matrix`)** *(Substitui Mending / Remendo)*:
    - Micro-nanites integrados à estrutura do equipamento que reconstroem microfissuras consumindo pacotes de energia da rede sem fio WPT ou fragmentos de sucata metálica.
  - [ ] **Revestimento Diamantado de Titânio (`titanium_lattice_coating`)** *(Substitui Unbreaking / Inquebrabilidade)*:
    - Nanocamada de diamante sintético e titânio que triplica a rigidez estrutural, reduzindo a taxa de desgaste abrasivo das ferramentas em 75%.
- [ ] **Placas de Chassi e Revestimentos de Blindagem para o Traje Espacial (Substitutos de Encantamentos de Armadura)**:
  - [ ] **Grelha de Amortecimento Balístico (`ballistic_dampener_mesh`)** *(Substitui Proteção Geral / Protection)*:
    - Malha de dispersão de impacto que dissipa energia cinética por toda a área do chassi.
  - [ ] **Blindagem Cerâmica Ablativa (`ablative_thermal_plating`)** *(Substitui Proteção contra Fogo)*:
    - Placas refratárias projetadas para absorver radiação solar extrema e chamas sem transferir calor ao operador.
  - [ ] **Amortecedores Pneumáticos de Vácuo (`pneumatic_fall_dampers`)** *(Substitui Peso Pena / Feather Falling)*:
    - Pistões hidráulicos de desaceleração montados nos calcanhares das Botas Magnéticas, eliminando até 80% do impacto de pousos abruptos.
  - [ ] **Placas de Descarga Reativa de Chassi (`reactive_shock_plating`)** *(Substitui Espinhos / Thorns)*:
    - Eletrodos perimétricos no traje que liberam um arco voltaico defensivo ciano contra agressores corpo a corpo, causando paralisia e choque elétrico.

### 🏥 Fase 23: Estação Médica Bio-Regenerativa (MedBay Pod) & Purificação Tecnológica do Vanilla
- [ ] **Cápsula de Regeneração Celular MedBay (`bio_regeneration_pod`)**:
  - [ ] Cúpula hermética horizontal de criostase e terapia intensiva com visor curvo de vidro de fulgurito temperado.
  - [ ] Funcionalidade de internação: O jogador deita-se na câmara para:
    - Regeneração acelerada de saúde (cura completa em 8 segundos).
    - Descontaminação biológica e purga instantânea de venenos e radiação.
    - Recarga ultrarrápida dos tanques de oxigênio do traje e estabilização térmica corporal.
  - [ ] Integração com a Fabric Transfer API (`FluidStorage.SIDED` e `ItemStorage.SIDED`): consome água potável e biopolímeros flexíveis para sintetizar soluções regenerativas.
  - [ ] Animações de pressurização, névoa criogênica translúcida e telemetria holográfica com ECG no painel superior.
- [ ] **Purificação Tecnológica & Desativação Definitiva de Magia Vanilla**:
  - [ ] Remoção e anulação no `RecipeManager` das receitas do Suporte de Poções (`brewing_stand`) e da Mesa de Encantamentos (`enchanting_table`).
  - [ ] Supressão de livros encantados em tabelas de saque de estruturas (substituídos por Data Cores, Discos de Firmware e Módulos de Overclock).
  - [ ] Prevenção de spawn de bruxas, suportes arcanos e mecânicas mágicas que quebram a coerência hard sci-fi do planeta desértico.
  - [ ] Suíte de testes dedicada: `Fase21BioPharmacologyTest`, `Fase22MolecularModifierTest` e `Fase23MedBayAndMagicSuppressionTest`.

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
| `item.stim.activate` | `stim_activate.ogg` | `players` | 0.8s | Pulso bio-elétrico com batimentos cardíacos sutilmente acelerados e tom harmônico ascendente de sobrecarga motora. |

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
- [x] **Modelos de Entidades (Blockbench)**:
  - [x] `sandworm.bbmodel`: Corpo cilíndrico segmentado com mandíbulas quádruplas abertas e anel bucal.
  - [ ] `sandtrout.bbmodel`: Modelo pequeno de criatura ameboide coriácea rastejante de areia (Truta da Areia / Little Maker).
  - [ ] `sandworm_larva.bbmodel`: Modelo segmentado ágil de larva/ninfa com anéis tenros de quitina e mandíbula trirradiada.
  - [ ] `juvenile_sandworm.bbmodel`: Modelo intermediário de verme caçador de dunas (~15 blocos) com crista dorsal e sulcos de escavação.
  - [x] `builder_drone.bbmodel`: Drone operário de construção aérea dark-tech com propulsores quad-ion basculantes, cabeçote móvel com emissores laser convergentes e garras magnéticas de retenção de blocos (Fase 19).
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



