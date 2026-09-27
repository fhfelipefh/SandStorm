# Demonstração Visual das Máquinas & Construções

> [!NOTE]
> Todos os recursos visuais do SandStorm são obtidos **100% diretamente dos assets nativos e da engine do jogo** (Minecraft 1.21.4 / Fabric). Nenhuma imagem sintética ou externa é utilizada. Para visualizar qualquer uma das construções e máquinas completas no seu próprio mundo, utilize os comandos in-game `/sandstorm debug setup <instalação>`.

### 🔬 Galeria de Maquinários Tecnológicos Nativos

| Máquina / Instalação | Textura Nativa do Jogo | Função e Arquitetura no Jogo | Comando de Spawn In-Game |
|:---:|:---:|---|---|
| **Gerador de Escudo de Plasma**<br>*(Plasma Shield Generator)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/plasma_shield_generator_front_active.png" width="64" height="64" alt="Plasma Shield Generator" /> | Gerador planetário de 1.000.000 J conectado à malha WPT. Projeta domo de contenção com repulsão dinâmica de vermes de areia colossais, desintegração de projéteis e choque cinético em ameaças. | `/sandstorm debug setup plasma_defense_complex` |
| **Canhão Cinético Anti-Titã**<br>*(Kinetic Railgun)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/kinetic_railgun_front_lit.png" width="64" height="64" alt="Kinetic Railgun" /><br><img src="../../src/main/resources/assets/sandstorm/textures/item/kinetic_slug.png" width="48" height="48" alt="Kinetic Slug" /> | Artilharia pesada ferroviária de 250.000 J. Varredura automática em 64m priorizando Sandworms, disparando projéteis hipercinéticos com 50.0 de dano e repulsão vetorial. | `/sandstorm debug setup plasma_defense_complex` |
| **Pilão de Defesa Acústica**<br>*(Acoustic Defense Pylon)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/acoustic_defense_pylon_side_active.png" width="64" height="64" alt="Acoustic Defense Pylon" /> | Torre perimétrica de cancelamento de ressonância sísmica em 32m de raio, suprimindo o acúmulo de passos e mineração que atrai predadores do subsolo. | `/sandstorm debug setup plasma_defense_complex` |
| **Cápsula de Estase Quântica**<br>*(Quantum Sleeper Pod)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/quantum_sleeper_pod_front_active.png" width="64" height="64" alt="Quantum Sleeper Pod Ativo" /><br><img src="../../src/main/resources/assets/sandstorm/textures/block/quantum_sleeper_pod_front_occupied.png" width="64" height="64" alt="Quantum Sleeper Pod Ocupado" /> | Câmara criogênica de 100.000 J conectada à malha WPT sem fio. Realiza a bio-síntese do corpo do clone consumindo nutrientes e biocompostos (quitosana, trealose, glicerol) e permite o **Ego-Casting**. | `/sandstorm debug setup clone_facility` |
| **Torre Holo-Tática & Radar 3D**<br>*(Holo-Tactical Spire)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/holo_tactical_spire_side.png" width="64" height="64" alt="Holo-Tactical Spire" /><br><img src="../../src/main/resources/assets/sandstorm/textures/item/neural_synapse_link.png" width="48" height="48" alt="Neural Synapse Link" /> | Pilar de comando militar com cúpula holográfica. Conecta-se à mente de ciborgues autônomos via `neural_synapse_link`, transmitindo ordens macro-estratégicas. | `/sandstorm debug setup cyborg_outpost` |
| **Incubadora Criogênica de Ciborgues**<br>*(Cyborg Incubator Vat)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/cyborg_incubator_vat_front_active.png" width="64" height="64" alt="Cyborg Incubator" /> | Tanque biônico com fluido eletrolítico de titânio para montagem e impressão celular de ciborgues especialistas. | `/sandstorm debug setup cyborg_outpost` |
| **Doca de Recarga Rápida**<br>*(Cyborg Docking Station)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/cyborg_docking_station_top_active.png" width="64" height="64" alt="Cyborg Docking Station" /> | Plataforma de indução eletromagnética para carregamento rápido e sincronização de telemetria dos robôs em operação autônoma contínua. | `/sandstorm debug setup cyborg_outpost` |
| **Pod de Bio-Regeneração**<br>*(Bio-Regeneration Pod)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/bio_regeneration_pod_front_active.png" width="64" height="64" alt="Bio-Regeneration Pod" /> | Câmara estéril médica para purificação celular e regeneração rápida de tecidos biológicos do explorador espacial. | `/sandstorm debug setup medbay_clinic` |
| **Biorreator de Batelada Quimiostato**<br>*(Bioreactor Vat)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/bioreactor_vat_front.png" width="64" height="64" alt="Bioreactor Vat" /> | Quimiostato industrial de fermentação celular contínua para cultivo de extremófilos botânicos (quitosana, trehalose, glicerol e neuro-alcaloides). | `/sandstorm debug setup bioreactor_lab` |
| **Processador Atmosférico**<br>*(Atmospheric Terraformer)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/atmospheric_terraformer.png" width="64" height="64" alt="Atmospheric Terraformer" /> | Usina planetária de terraformação setorial. Projeta uma cúpula de microclima dinâmico que transforma areia em solo fértil e lagos de água doce. | `/sandstorm debug setup terraformer_dome` |
| **Filtro de Dessalinização**<br>*(Desalination Filter)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/desalination_filter.png" width="64" height="64" alt="Desalination Filter" /> | Unidade de filtração osmótica que purifica aquíferos salobres subterrâneos em água potável estéril e subprodutos de sal mineral. | `/sandstorm debug setup starter_base` |
| **Impressora 3D Industrial**<br>*(Printer 3D)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/printer_3d.png" width="64" height="64" alt="3D Printer" /> | Manufatura aditiva de precisão para wafers de silício, placas de circuito integrado e ferramentas científicas. | `/sandstorm debug setup starter_base` |
| **Fabricador de Nanorobôs**<br>*(Nanite Fabricator)* | <img src="../../src/main/resources/assets/sandstorm/textures/block/nanite_fabricator.png" width="64" height="64" alt="Nanite Fabricator" /> | Síntese de nano-atuadores e circuitos avançados para maquinário pesado e montagem de mechas. | `/sandstorm debug setup starter_base` |

---

## 🏗️ Catálogo de Instalações & Comandos de Demonstração Rápida (Debug)

Para criadores de conteúdo, desenvolvedores e administradores, o mod inclui comandos para geração instantânea de instalações tecnológicas completas e kits de desenvolvimento:

| Instalação / Complexo | Comando In-Game | Estrutura Gerada |
|---|---|---|
| **Complexo Geotérmico e Lito-Plasma** | `/sandstorm debug setup geothermal_well` | Perfuratriz de poço do manto, extrator de lito-plasma, trocador térmico supercrítico e circuito de fluidos |
| **Complexo de Defesa de Plasma** | `/sandstorm debug setup plasma_defense_complex` | Cúpula de escudo de plasma, canhão cinético municiado com slugs e pilões acústicos anti-verme |
| **Complexo de Clonagem Quântica** | `/sandstorm debug setup clone_facility` | Duas Cápsulas de Estase (`quantum_sleeper_pod`) conectadas, telemetria e insumos para teste imediato de Ego-Casting |
| **Canteiro de Megaestrutura** | `/sandstorm debug setup megastructure_site` | Construtor de Megaestruturas ancorado com blueprints e baús de insumos |
| **Posto Avançado de Ciborgues** | `/sandstorm debug setup cyborg_outpost` | Docas de ancoragem, incubadora criogênica, telemetria e ciborgues operantes |
| **Clínica Médica e MedBay** | `/sandstorm debug setup medbay_clinic` | Câmara de bio-regeneração estéril e ampolas farmacológicas avançadas |
| **Laboratório de Biorreatores** | `/sandstorm debug setup bioreactor_lab` | Quimiostato industrial, cepas extremófilas e insumos de fermentação |
| **Oficina Molecular** | `/sandstorm debug setup molecular_workshop` | Modificador molecular com catalisadores de fissão e transmutação |
| **Usina de Energia Planetária** | `/sandstorm debug setup power_station` | Matriz solar de alta eficiência e acumuladores de estado sólido |
| **Complexo de Refinaria** | `/sandstorm debug setup refinery_complex` | Torres de craqueamento térmico e tanques de hidrocarbonetos pesados |
| **Domo de Oásis Terraformado** | `/sandstorm debug setup terraformer_dome` | Processador Atmosférico com cúpula de microclima e oásis verdejante |
| **Fortaleza de Espinhos** | `/sandstorm debug setup spike_fortress` | Perímetro com paredes de titânio, espinhos eletrizados e portões motorizados |
| **Base Inicial de Sobrevivência** | `/sandstorm debug setup starter_base` | Posto balanceado com filtro de dessalinização, água e energia solar |

> **Kits de Demonstração e Equipamentos:** Obtenha conjuntos completos de equipamentos de desenvolvimento com o comando `/sandstorm debug phase all` ou gere instalações prontas para testes com `/sandstorm debug setup <instalação>`.
