# Diagramas de Arquitetura e Engenharia

<details>
<summary><b>1. Fluxo de Sobrevivência (Energia, Água e Clima)</b></summary>

```mermaid
graph TD;
    A[Luz Solar Direta] -->|Recarrega| B(Traje de Suporte à Vida)
    N[Safe Zone da Nave] -->|Área Imune| B
    C[Exploração Subterrânea] -->|Drena Energia| B
    C -->|Acúmulo de Passos| D[Alerta Sísmico: Verme]
    T[Tempestade de Areia] -->|Abafa Vibrações em 50%| C
    T -->|Atenua Luz Solar em 85%| A
    I[Thumper / Emissor Sísmico] -->|Desvia Criatura| D
    C -->|Mineração Aquífera| E(Água Salobra Subterrânea)
    E -->|Filtro de Dessalinização| F[Água Potável Purificada]
    F --> G[Nutrição e Saciedade Ótima]
```
</details>

<details>
<summary><b>2. Fluxo Industrial e Logística Aérea</b></summary>

```mermaid
graph TD;
    A[Areia do Deserto] -->|Purificação| B[Silício Bruto]
    B -->|Litografia| C[Wafer de Silício]
    C -->|Impressora 3D| D[Placa de Circuito Integrado]
    D -->|Fabricador de Nanites| E[Nano-Atuador Mecânico]
    E -->|Pátio de Montagem| F[Drones de Carga Aérea]
    F -->|Voo Aéreo: Zero Vibração no Solo| G[Logística Segura Entre Bases]
    E -->|Construção Pesada| H[Veículo de Escavação & Megazord]
```
</details>

<details>
<summary><b>3. Fluxo de Combate Sônico & Megazord</b></summary>

```mermaid
graph TD;
    A[Verme de Areia Emerge] --> B{Jogador Protegido?}
    B -- Fora do Mecha --> C[Canhão Sônico Portátil]
    B -- No Cockpit --> D[Megazord Titânico: 500 HP]
    D -->|Bateria 100.000 J| E[Onda de Choque Acústica de 16m]
    C & E -->|Atordoa e Repele Criatura| F[Verme Dispersado]
    E -.->|Garantia Absoluta| G[Zero Dano a Máquinas do Jogador]
```
</details>

<details>
<summary><b>4. Fluxo de Terraformação por Cúpula de Oásis (Endgame Setorial)</b></summary>

```mermaid
graph TD;
    A[Processador Atmosférico] -->|Consumo de 2.000 J/ciclo| B[Cúpula Ecológica Local: 0 a 100%]
    B -->|Estágio 1: 0-25%| C[Área Inicial: Raio de 6 Blocos]
    B -->|Estágio 2: 25-50%| D[Condensação e Queda Térmica: Raio de 10 Blocos]
    B -->|Estágio 3: 50-75%| E[Precipitação e Dispersão de Vermes: Raio de 14 Blocos]
    B -->|Estágio 4: 75-100%| F[Oásis Fértil Pleno e Solo Verdejante: Raio de 18 Blocos]
    E & F -->|Regeneração Local de Solo| G[Areia -> Grama e Solo Fértil no Raio]
    E & F -->|Proteção da Cúpula| H[Área Segura e Habitável Local]
    I[Mundo Infinito Além da Cúpula] -.->|Preservação Ecológica| J[Deserto e Vermes Continuam Ativos nas Regiões Selvagens]
```
</details>

<details>
<summary><b>5. Fluxo de Clonagem Quântica, Ego-Casting & Respawn por Proximidade</b></summary>

```mermaid
graph TD;
    subgraph "Preparação & Gestação"
        A["Insumos Biológicos: Quitosana + Trealose + Glicerol + Água Potável"] --> B["Cápsula de Estase Quântica (Quantum Sleeper Pod)"]
        E["Rede WPT Sem Fio (100.000 J)"] --> B
        M["Matriz de Consciência Quântica"] --> B
        B -->|Bio-Síntese Celular| C["Corpo Clone Pronto em Estase Criogênica"]
        C --> D["Equipamentos Pré-Armazenados nos 42 Slots da Cápsula"]
    end

    subgraph "Mecânica de Ego-Casting (Transferência de Mente)"
        P["Jogador Ativo na Cápsula Alpha"] -->|Aciona Transferência| TR{"Rede de Clones Sincronizada?"}
        TR -- Sim --> S1["Corpo Alpha Adormece"]
        S1 --> S2["Inventário Físico Completo é Retido com Segurança em Alpha"]
        S2 --> S3["Consciência Quântica Teletransportada pela Rede"]
        S3 --> S4["Corpo Beta Desperta na Cápsula Receptora"]
        S4 --> S5["Descarrega Inventário Pré-Armazenado de Beta no Jogador"]
        S5 --> S6["Zero Duplicação de Itens: Corpos Físicos Isolados"]
    end

    subgraph "Protocolo de Respawn de Emergência"
        DTH["Morte do Jogador em Exploração Distante"] --> LOC["Servidor Intercepta Coordenadas de Óbito"]
        LOC --> DIST["Cálculo Euclidiano de Proximidade (findNearestReadyPod)"]
        DIST --> NEAR["Localiza Cápsula com Clone Mais Próxima"]
        NEAR --> RES["Respawn Imediato na Cápsula Mais Próxima com Inventário do Clone"]
    end
```
</details>

<details>
<summary><b>6. Fluxo de Enxame de Ciborgues, Docas de Recarga & Torre Holo-Tática</b></summary>

```mermaid
graph TD;
    subgraph "Incubação & Especialização"
        MTR["Matriz Neural + Servo-Motores + Liga Titânio-Quitina"] --> INC["Incubadora Criogênica de Ciborgues"]
        INC -->|Síntese Biônica| C1["Ciborgue Minerador (Escavação e Voxel Mutex)"]
        INC -->|Síntese Biônica| C2["Ciborgue Construtor (Edificação e Reparos)"]
        INC -->|Síntese Biônica| C3["Ciborgue Coletor (Colheita de Flora e Biomassa)"]
    end

    subgraph "Ciclo de Trabalho & Autonomia"
        C1 & C2 & C3 --> WORK["Operação Autônoma com Reserva Atômica de Blocos"]
        WORK --> BAT{"Bateria Interna < 20%?"}
        BAT -- Sim --> RET["Retorno Autônomo à Doca de Recarga Mais Próxima"]
        RET --> DOCK["Doca de Recarga por Indução (Cyborg Docking Station)"]
        DOCK -->|Recarga Rápida 100%| WORK
        BAT -- Não --> WORK
    end

    subgraph "Comando Estratégico Holo-Tático"
        SPIRE["Torre Holo-Tática (Holo-Tactical Spire)"] --> RADAR["Radar Holográfico 3D (Raio de 48m)"]
        LINK["Capacete Espacial com Elo Sináptico (neural_synapse_link)"] --> SPIRE
        SPIRE -->|Broadcast de Ordem Global| ORD{"Diretriz Ativa"}
        ORD -->|Ordem 0: Convergência| C1 & C2 & C3
        ORD -->|Ordem 1: Otimização de Tarefas| C1 & C2 & C3
        ORD -->|Ordem 2: Patrulha Perimétrica| C1 & C2 & C3
        ORD -->|Ordem 3: Alerta Sísmico de Evacuação| C1 & C2 & C3
    end
```
</details>

<details>
<summary><b>7. Fluxo de Construtor de Megaestruturas & Manufatura Holográfica 3D</b></summary>

```mermaid
graph TD;
    subgraph "Ancoragem & Holografia"
        MC["Construtor de Megaestruturas (Megastructure Constructor)"] --> WPT["Conexão à Malha de Energia WPT (500.000 J)"]
        BP["Seleção de Blueprint: Cidadela, Pirâmide, Silo ou Biosfera"] --> MC
        MC --> HOLO["Projeção Holográfica 3D em Tempo Real (Wireframe Ciano #00E5FF)"]
        HOLO --> SCAN["Varredura Layer-by-Layer de Baixo para Cima"]
    end

    subgraph "Manufatura Aditiva Litográfica"
        MC --> DRONES["Ativação de Drones Construtores Operários (BuilderDroneEntity)"]
        INV["18 Slots de Insumos Estruturais (ItemStorage.SIDED)"] --> DRONES
        DRONES -->|Voo Livre sem Ruído Sísmico| POS["Posicionamento no Bloco Holográfico Ativo"]
        DRONES -->|Feixe Litográfico Laser Contínuo| FUS["Fusão Molecular e Materialização do Bloco"]
        FUS --> CHECK{"Camada Concluída?"}
        CHECK -- Não --> POS
        CHECK -- Sim --> SOUND["Sinal Sonoro e Elevação em Y para Próxima Camada"]
        SOUND --> POS
    end

    subgraph "Consagração do Monumento"
        DONE["Todas as Camadas Concluídas a 100%"] --> SHOCK["Onda de Choque Sônica Planetária"]
        SHOCK --> PERIM["Consagração de Perímetro Seguro de 48m (KineticShieldTracker)"]
        PERIM --> SAFE["Zona Imune Permanente a Ataques de Vermes de Areia"]
    end
```
</details>

<details>
<summary><b>8. Fluxo de Fitoquímica de Extremófilos, Biorreatores & Farmacologia Médica</b></summary>

```mermaid
graph TD;
    subgraph "Botânica e Fitoquímica de Dunas"
        SAND["Dunas Hostis & Areias Desérticas"] --> F1["Micélio Radiotrófico"]
        SAND --> F2["Fungo Quitinolítico de Dunas"]
        SAND --> F3["Líquen Crio-Xerofílico"]
        SAND --> F4["Suculenta Halófita"]
        SAND --> F5["Éfedra das Dunas"]
    end

    subgraph "Fermentação e Biorreator"
        F1 & F2 & F3 & F4 & F5 --> BIO["Biorreator de Batelada e Quimiostato (bioreactor_vat)"]
        WAT["Água Potável Purificada + Energia WPT"] --> BIO
        BIO -->|Fermentação Submersa Catalítica| M1["Melanina Radioprotetora"]
        BIO -->|Fermentação Submersa Catalítica| M2["Extrato de Quitosana"]
        BIO -->|Fermentação Submersa Catalítica| M3["Açúcar Trealose"]
        BIO -->|Fermentação Submersa Catalítica| M4["Osmólito de Glicerol"]
        BIO -->|Fermentação Submersa Catalítica| M5["Alcaloides Neuroativos"]
    end

    subgraph "Aplicações de Alta Tecnologia"
        M2 & M3 & M4 --> CLONE["Gestação de Clones Quânticos"]
        M2 --> COMP["Compósito Titânio-Quitina (Ligas e Blindagens)"]
        M1 & M3 & M5 --> PHARM["Formulação Farmacológica de Ampolas Médicas"]
        PHARM --> HYPO["Hipo-Injetor Pneumático de Ação Rápida (Substitui Poções Vanilla)"]
        HYPO --> BOOST["Regulação Térmica, Resistência a Radiação e Vigor Instantâneo"]
    end
```
</details>

<details>
<summary><b>9. Fluxo de Grade de Defesa Planetária de Plasma, Canhão Cinético Anti-Titã & Grade Acústica</b></summary>

```mermaid
graph TD;
    subgraph "Capacitação Energética & Detecção"
        WPT["Malha de Energia WPT (1.000.000 J)"] --> GEN["Gerador de Escudo de Plasma (plasma_shield_generator)"]
        GEN --> TOROID["Bobina Toroidal Supercondutora (superconductor_toroid)"]
        GEN --> CRYSTAL["Cristal Piezoelétrico de Foco de Plasma (plasma_focus_crystal)"]
        TRACKER["Rastreador de Defesa Planetária (KineticShieldTracker)"] --> GEN
    end

    subgraph "Escudo Dinâmico de Plasma (Raio 48m)"
        GEN -->|Cúpula de Plasma Ionizado| DOME["Domo Protetor Ciano"]
        PROJ["Flechas, Projéteis e Fogo Hostil"] -->|Impacto na Borda| DIS["Desintegração Instantânea de Projéteis"]
        MOBS["Monstros & Invasores Hostis"] -->|Tentativa de Travessia| REP["Repulsão por Choque de Plasma (4.0 Dano)"]
        WORMS["Vermes de Areia Colossais"] -->|Colisão com o Domo| DEFLECT["Deflexão Sísmica & Ricochete"]
    end

    subgraph "Canhão Cinético Anti-Titã (Railgun)"
        WPT --> RAIL["Canhão Ferroviário Hipersônico (kinetic_railgun)"]
        SLUG["Munição Pesada de Liga Titânio-Quitina (kinetic_slug)"] --> RAIL
        SCAN["Varredura Automática de Ameaças em 64m"] --> RAIL
        RAIL -->|Disparo de Alta Velocidade| SHOT["Projétil Hipercinético (50.0 Dano + Impulso Vetorial)"]
        SHOT --> TARGET["Alvos Prioritários: Sandworms > Monstros Hostis"]
    end

    subgraph "Grade Acústica Perimétrica"
        WPT --> PYLON["Pilão de Defesa Acústica (acoustic_defense_pylon)"]
        PYLON --> DAMP["Amortecimento de Ressonância Sísmica em 32m"]
        DAMP --> NOISE["Cancelamento de Vibrações de Passos, Mineração e Motores"]
        NOISE --> WORM_SAFE["Invisibilidade Acústica Total contra Predadores do Subsolo"]
    end
```
</details>

<details>
<summary><b>10. Fluxo de Mineração Geotérmica Profunda, Poço do Manto & Sifão Lito-Plasmático</b></summary>

```mermaid
graph TD;
    subgraph "Perfuração do Manto Planetário"
        BORE["Broca de Poço do Manto (deep_core_borehole)"] --> BIT["Broca de Diamante Policristalino (geothermal_core_drill_bit)"]
        BORE --> COOL["Injeção de Água/Refrigerante (8.000 mB)"]
        BORE --> WPT_IN["Alimentação WPT (500.000 J / 250 J/t)"]
        BORE -->|Penetração em Y: 0 a -64| MANTLE["Extração Contínua do Manto Planetário"]
        MANTLE --> SALTS["Sais Brutos de Lítio (raw_lithium_salts)"]
        MANTLE --> STEAM["Fluidos Térmicos Supercríticos"]
    end

    subgraph "Fracionamento & Condensação de Lito-Plasma"
        SALTS --> EXTRACTOR["Extrator Magmático de Lito-Plasma (litho_plasma_extractor)"]
        EXTRACTOR --> WPT_EXT["Alimentação WPT (250.000 J / 150 J/t)"]
        EXTRACTOR -->|Centrifugação Térmica| CAPSULE["Cápsula Criogênica de Lítio Superaquecido (superheated_lithium_capsule)"]
        EXTRACTOR -->|Condensação de Alta Pressão| ALLOY["Lingote de Superliga do Manto (mantle_alloy_ingot)"]
        EXTRACTOR --> BYPROD["Subprodutos Minerais (Quartzo Piezoelétrico, Fósforo e Minérios)"]
    end

    subgraph "Geração Térmica Supercrítica de Energia"
        STEAM & COOL --> EXCHANGER["Trocador de Calor Supercrítico (supercritical_heat_exchanger)"]
        FIN["Aletas Cerâmicas de Dissipação (thermal_radiator_fin)"] --> EXCHANGER
        CAPSULE -->|Catalisador de Potência Máxima| EXCHANGER
        EXCHANGER -->|Geração Massiva de Energia| POWER["Geração de 2.500 a 10.000 J/tick"]
        POWER --> WPT_OUT["Injeção Direta na Rede Sem Fio WPT da Base"]
    end
```
</details>
