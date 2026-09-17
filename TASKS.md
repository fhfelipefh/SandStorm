# SandStorm - Quadro de Tarefas & Mensuração do MVP

Este documento consolida o andamento das fases de desenvolvimento do mod **SandStorm** para **Minecraft 26.3 (Fabric / Java 25)**, detalhando o que já foi concluído, as métricas de qualidade e o que resta para o fechamento do MVP.

---

## 📊 Métricas Gerais do Projeto

| Métrica | Valor Atual | Meta MVP | Status |
| :--- | :--- | :--- | :--- |
| **Versão Alvo** | Minecraft 26.3 | Minecraft 26.3 | ✅ Atingido |
| **Fabric Loader / API** | 0.19.5 / 0.160.7+26.3 | Compatibilidade Estável | ✅ Atingido |
| **Java SDK** | Java 25 | Java 25 | ✅ Atingido |
| **Testes Automatizados** | **187 testes** (0 falhas) | > 150 testes | ✅ Superado |
| **Rigor de Código (Zero Comentários)** | **0 linhas de comentários** | 0 linhas | ✅ 100% Auditado |
| **Arquitetura Desacoplada** | 100% isolamento de componentes | Zero acoplamento | ✅ Validado |
| **Receitas Data-Driven (JEI/REI)** | 20 receitas oficiais | Cobertura total | ✅ Concluído |
| **Geração de Mundo (Worldgen)** | Aquíferos, Ruínas e Núcleos em desertos | Totalmente Integrado | ✅ Concluído |
| **Progresso Estimado do MVP** | **~82%** | **100%** | 🟡 Em Andamento |

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
  - [x] Mineração de pedra/minérios restrita a robôs e maquinários (`ExcavatorVehicleEntity`, `MegazordEntity`).
  - [x] Permissão explícita de pás vanilla para movimentação e coleta de areia para a nave.
  - [x] Desativação das 24 receitas de ferramentas vanilla no `RecipeManager`.

### Fase 2: Automação, Energia e Robótica (Concluída - 100%)
- [x] Processamento de silício: Minério bruto (`raw_silicon`), pastilha de silício (`silicon_wafer`), placa de circuito (`circuit_board`) e nanoatuadores (`nano_actuator`).
- [x] Impressora 3D de bancada (`Printer3DBlock`) com processamento via energia.
- [x] Fabricador de nanitas (`NaniteFabricatorBlock`) para criação de componentes avançados.
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

### Fase 5: Integração JEI/REI/EMI e Testes Automatizados (Concluída - 100%)
- [x] 20 arquivos JSON de receitas data-driven em `data/sandstorm/recipe/` (smelting, blasting, shaped, shapeless).
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
  - Configured feature: `data/sandstorm/worldgen/configured_feature/brackish_aquifer.json`
  - Placed feature: `data/sandstorm/worldgen/placed_feature/brackish_aquifer.json` (Y: 25 a 60).
- [x] Configuração de ruínas tecnológicas soterradas nos desertos (`sandstorm:buried_tech_ruins`):
  - Configured feature: `data/sandstorm/worldgen/configured_feature/buried_tech_ruins.json`
  - Placed feature: `data/sandstorm/worldgen/placed_feature/buried_tech_ruins.json` (Y: 48 a 72).
- [x] Configuração de núcleos de dados ancestrais (`sandstorm:ancient_data_core`):
  - Configured feature: `data/sandstorm/worldgen/configured_feature/ancient_data_core.json`
  - Placed feature: `data/sandstorm/worldgen/placed_feature/ancient_data_core.json` (Y: 45 a 68).
- [x] Handler de injeção em biomas via Fabric Biome API (`SandStormWorldGen.java`).
- [x] Testes automatizados de Worldgen (`SandStormWorldGenTest.java`).

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
| `weather.sandstorm.wind` | `sandstorm_wind.ogg` | `weather` | 6.0s (loop) | Vento uivante e violento de tempestade de areia. Ruído de milhões de partículas abrasivas colidindo contra o visor do traje. |

---

## 🎯 O Que Falta para o Fechamento do MVP (Pendências)

### 1. Modelagem 3D no Blockbench & Texturas Pixel Art (Foco Visual)
- [x] `raw_silicon`: Modelo Blockbench (.bbmodel) + Modelo Item (.json) + Textura 16x16 (.png).
- [x] `circuit_board`: Modelo Item (.json) + Textura 16x16 (.png).
- [ ] **Itens Tecnológicos Pendentes**:
  - [ ] `silicon_wafer`: Textura e modelo 16x16.
  - [ ] `nano_actuator`: Textura e modelo 16x16.
  - [ ] `tech_disc`: Textura e modelo 16x16.
  - [ ] `scrap_metal`: Textura e modelo 16x16.
  - [ ] `mineral_salt`: Textura e modelo 16x16.
- [ ] **Equipamentos e Ferramentas**:
  - [ ] `sonic_cannon`: Modelo 3D no Blockbench + Textura personalizada.
  - [ ] `anomaly_radar`: Textura e modelo com display animado.
  - [ ] `atmospheric_analyzer`: Textura e display de telemetria.
  - [ ] `space_suit_helmet`, `chestplate`, `leggings`, `boots`: Texturas de item + camadas de armadura 3D (`space_suit_layer_1.png` e `space_suit_layer_2.png`).
- [ ] **Alimentos e Fluidos**:
  - [ ] `space_ration`: Textura de ração militar espacial embalada a vácuo.
  - [ ] `potable_water_bottle`: Frasco tecnológico com líquido azul puro.
  - [ ] `brackish_water_bottle`: Frasco com líquido turvo salobro.
- [ ] **Modelos 3D de Blocos e Máquinas (Blockbench)**:
  - [ ] `thumper.bbmodel`: Modelo com pistão oscilante e pesos rítmicos.
  - [ ] `printer_3d.bbmodel`: Bancada tecnológica com laser/cabeçote de impressão.
  - [ ] `desalination_filter.bbmodel`: Tubulações de cobre, tanque e condensador.
  - [ ] `nanite_fabricator.bbmodel`: Câmara de contenção e emissão de luz de nanitas.
  - [ ] `atmospheric_terraformer.bbmodel`: Reator central emissor de campo de força/cúpula.
  - [ ] `drone_dock.bbmodel` & `assembly_bay.bbmodel`: Pistas e braços de montagem.
  - [ ] `ancient_data_core.bbmodel` & `buried_tech_ruins.bbmodel`: Monólitos tecnológicos enterrados.
- [ ] **Modelos de Entidades**:
  - [ ] `sandworm.bbmodel`: Corpo segmentado cilíndrico com mandíbulas quádruplas.
  - [ ] `cargo_drone.bbmodel`: Drone quadricóptero com garras de carga.
  - [ ] `excavator_vehicle.bbmodel`: Rover industrial de esteiras com pá/furadeira frontal.
  - [ ] `megazord.bbmodel`: Mecha bipedal titan com cockpit duplo.

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
