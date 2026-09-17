# SandStorm - Quadro de Tarefas & Mensuração do MVP

Este documento consolida o andamento das fases de desenvolvimento do mod **SandStorm** para **Minecraft 26.3 (Fabric / Java 25)**, detalhando o que já foi concluído, as métricas de qualidade e o que resta para o fechamento do MVP.

---

## 📊 Métricas Gerais do Projeto

| Métrica | Valor Atual | Meta MVP | Status |
| :--- | :--- | :--- | :--- |
| **Versão Alvo** | Minecraft 26.3 | Minecraft 26.3 | ✅ Atingido |
| **Fabric Loader / API** | 0.19.5 / 0.160.7+26.3 | Compatibilidade Estável | ✅ Atingido |
| **Java SDK** | Java 25 | Java 25 | ✅ Atingido |
| **Testes Automatizados** | **180 testes** (0 falhas) | > 150 testes | ✅ Superado |
| **Rigor de Código (Zero Comentários)** | **0 linhas de comentários** | 0 linhas | ✅ 100% Auditado |
| **Arquitetura Desacoplada** | 100% isolamento de componentes | Zero acoplamento | ✅ Validado |
| **Receitas Data-Driven (JEI/REI)** | 20 receitas oficiais | Cobertura total | ✅ Concluído |
| **Progresso Estimado do MVP** | **~75%** | **100%** | 🟡 Em Andamento |

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

---

## 🎯 O Que Falta para Concluir o MVP (Pendências)

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

### 2. Geração de Mundo (Worldgen)
- [ ] Configuração de colocação de estruturas para Ruínas Tecnológicas Enterradas nos desertos (`sandstorm:buried_tech_ruins`).
- [ ] Configuração de aquíferos subterrâneos salobros nos desertos (`sandstorm:brackish_aquifer`).

### 3. Sons e Áudios Ambientais
- [ ] Definição de `sounds.json` dedicado para canhão sônico, pulso do thumper, alerta de tempestade e zumbido do reator de terraformação.

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
