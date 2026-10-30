# História e Mecânicas Principais

## 📖 História e Premissa

Você é um astronauta explorador que aterrissou em um planeta completamente desertificado, coberto por dunas seculares. O que as sondagens orbitais não revelaram é que o planeta não está desabitado: sob as areias espreitam colossais e vorazes **Vermes de Areia**.

Por sorte, sua aterrissagem inicial foi em uma área onde você desligou os motores a tempo. Os *chunks* iniciais ao redor de sua nave formam uma **Safe Zone** protegida da percepção sísmica.

No entanto, há um revés: os sistemas da nave acusam falha crítica nos motores de propulsão. Esta foi uma viagem só de ida. Agora, o planeta é o seu novo lar, e a sua única opção é conquistá-lo ou perecer tentando.

---

## ⚙️ Pilares de Jogabilidade & Mecânicas Centrais

### 🧑‍🚀 Traje Espacial & Suporte à Vida
Esqueça monstros vanilla convencionais (zumbis e esqueletos são suprimidos para preservar a imersão planetária). A verdadeira ameaça é o ambiente térmico. O traje espacial é energizado por luz solar direta e regula continuamente a temperatura corporal do astronauta. No subsolo, a energia solar não penetra e a bateria interna é consumida progressivamente.

### ☀️ Ciclo Circadiano Ininterrupto (Sem Pulo de Noite)
Camas vanilla são desativadas por lore e equilíbrio de jogo. O traje de suporte à vida mantém os parâmetros fisiológicos ativos 24 horas por dia, eliminando a necessidade biológica de repouso. Não há como pular o frio da noite ou tempestades de areia: o planejamento técnico e o isolamento de base são as únicas defesas.

### 🐛 A Ameaça Sísmica dos Vermes de Areia & Defesa Ativa
Corridas bruscas, mineração e saltos acumulam vibrações na areia solta. Ao ultrapassar o limiar sísmico do setor, um Verme de Areia colossal emerge das profundezas.
* **Regra da Indústria:** Os vermes caçam alvos biológicos e vibrações cinéticas. **Eles nunca destroem ou danificam suas máquinas e estruturas industriais.**
* **Iscas Acústicas (Thumpers):** Pilares sísmicos posicionáveis no solo que emitem pulsações rítmicas para desviar os vermes de areia e criar corredores de passagem seguros.
* **Torretas de Defesa Sônica:** Unidades com sensor de alvos que emitem anéis de pulso sônico hipersônico, causando dano contínuo e repulsão dinâmica sem emitir frequências que atraiam mais vermes.

![Torreta Sônica Autônoma em Combate](../screenshots/sonic-turret-combat.png)
*Torreta Sônica Autônoma repelindo ameaças hostis e protegendo instalações industriais no deserto.*

---

### 💧 Aquíferos Subterrâneos & Dessalinização Osmótica
O subsolo arenoso abriga aquíferos de água salobra. A água bruta é tóxica se consumida sem tratamento, exigindo purificação em **Filtros de Dessalinização** para gerar **Água Potável** pura e subprodutos de sal mineral.

### 🌪️ Tempestades de Areia & Clima Extremo
Eventos atmosféricos severos que reduzem a radiação solar (85%), mas amortecem os ruídos do solo pela metade, gerando janelas estratégicas de escavação e movimentação.

### ⚡ Malha de Energia Sem Fio (WPT) & Armazenamento
Receptores solares e torres retransmissoras transmitem energia por ressonância eletromagnética (WPT) diretamente para máquinas e baterias sem a necessidade de cabeamento manual complexo.

### 📡 Arqueologia Planetária & Radar de Anomalias
Scanners direcionais com feedback de áudio detectam **Ruínas Tecnológicas Soterradas** e **Núcleos de Dados Ancestrais** sob as dunas, recuperando esquemas ópticos (`TECH_DISC`) e ligas metálicas (`SCRAP_METAL`).

---

### 🤖 Automação Robótica & Ciborgues Especialistas
Pátios de clonagem e incubadoras criogênicas permitem imprimir e programar **Ciborgues Autônomos**. Conectados a uma **Doca WPT** e orientados pela **Torre Holo-Tática**, eles assumem rotinas complexas de agricultura estéril, patrulha perimétrica e manutenção industrial sem intervenção humana contínua.

![Ciborgues Autônomos em Operação](../screenshots/autonomous-cyborgs.png)
*Ciborgues Especialistas executando colheita agrícola e patrulhando a base junto à doca de indução.*

---

### 🚄 Logística Ferroviária Maglev & Drones Aéreos
Para transporte rápido de astronautas e cargas sem disparar os sensores sísmicos dos predadores, a malha de **Trilhos Magnéticos (Maglev)** propulsiona carrinhos em levitação eletromagnética. Em paralelo, **Drones de Carga Aérea** realizam transporte automatizado entre docas com ruído sísmico nulo (`0.0f`).

![Rede de Trilhos Maglev](../screenshots/maglev-transport.png)
*Sistema ferroviário Maglev ligando postos avançados e estações de processamento no planeta.*

---

### 🌿 Terraformação Setorial & Cúpulas de Oásis (Endgame)
Em consonância com a geração de **mundo infinito** do Minecraft, não existe um contador arbitrário global. A terraformação é setorial: cada **Processador Atmosférico** gera uma cúpula de microclima ecológico dinâmico (raio de até 18 blocos), convertendo areia estéril em grama verdejante e lagos de água doce, reduzindo o calor e repelindo vermes.

Com o auxílio do **Gerador de Escudo de Plasma**, a cúpula se torna um perímetro intransponível, blindando o oásis verde contra projéteis e incursões biológicas, consolidando o seu novo lar definitivo.

![Domo de Escudo de Plasma Protegendo o Oásis](../screenshots/plasma-shield-dome.png)
*Biosfera protegida por domo de plasma: oásis verdejante, corpos de água doce e fauna habitável.*


---

### 🔮 Estruturas Multibloco & Projeção Holográfica (Estilo Botania)
Para a montagem de maquinários colossais de engenharia avançada, o SandStorm adota o padrão de **estruturas multibloco modulares** com assistência de **holografia visual no mundo**.

Esqueça a necessidade de adivinhar posições ou consultar guias externos às cegas:
* **Projeção pelo Survival Datapad:** Na aba **VI. Multiblocos** (ou diretamente pelo botão `[HOLO]` nas missões associadas), selecione a estrutura desejada. Ao clicar em **[PROJETAR]**, o holograma 3D é ancorado imediatamente no solo à frente do jogador, rotacionado com base na direção em que você está olhando (Norte, Sul, Leste ou Oeste).
* **Feedback de Validação em Tempo Real:** Cada bloco da estrutura é representado por um wireframe volumétrico com cantoneiras cibernéticas:
  * **Verde Vibrante (`#00E676`):** O bloco correto já está posicionado no local e com o estado válido.
  * **Ciano Pulsante / Cor Temática:** O bloco está ausente ou foi colocado um bloco incorreto, exibindo linhas cruzadas `[X]` para alertar o astronauta.
* **Telemetria de Progresso:** Um texto holográfico tridimensional flutua acima da construção, girando na direção do observador e informando o título da estrutura e a porcentagem exata de montagem em tempo real (ex.: `Montagem: 14 / 18 (77%)`).
* **Desativação Rápida:** Pressione a tecla de atalho **H** no Datapad ou clique em **[DESATIVAR]** a qualquer momento para liberar o campo de visão.

As 6 estruturas canônicas registradas são:
1. **Torre Solar & Relé WPT (`wpt_relay_tower`):** Matriz fotovoltaica e acumulador quântico para transmissão de energia sem fio em larga escala.
2. **Câmara de Cofre Blindada 3x3 (`vault_door_chamber`):** Sala de segurança reforçada com painéis de titânio, chapa escovada e porta de cofre automatizada.
3. **Portal Subespacial (`subspace_gateway`):** Pátio de teletransporte quântico com bobinas de condensação piezoelétricas e lentes gravitacionais.
4. **Cluster de Computação Quântica (`quantum_computer_cluster`):** Banco de dados distribuído de alta densidade para decodificação de blueprints e inteligência de enxame.
5. **Complexo de Lançamento Orbital (`orbital_launch_complex`):** Plataforma com canhão de massa hipersônico, antena parabólica de terra e acumulador eletrostático para satélites e sondas atmosféricas.
6. **Matriz de Super Pistões (`super_piston_array`):** Bateria de pistões cinéticos e tectônicos para britagem e automação industrial de ciclo contínuo.

---

### 🦾 Fauna Robótica & Androides Autônomos (Ecossistema Horizon)
O planeta desértico abriga uma biosfera de máquinas ancestrais autônomas, construídas a partir de ligas de titânio e cérebros neurais:
* **Grazer (`grazer`):** Unidades quadrúpedes herbívoras que processam vegetação e minerais das dunas em biocombustível. Rápidas e pacíficas, mas disparam se provocadas.
* **Fanghorn (`fanghorn`):** Máquinas com chifres ramificados que desenterram nutrientes e reagem agressivamente ao perigo, defendendo o rebanho com investidas de alta velocidade.
* **Frostclaw (`frostclaw`):** Androides bípedes massivos com garras criogênicas colossais. Capazes de disparar rajadas congelantes e esmagar o solo, criando terreno gelado que dissipa o calor extremo do deserto.
* **Glinthawk (`glinthawk`):** Drones voadores carniceiros que sobrevoam os céus à procura de sucatas e ligas metálicas, lançando ataques aéreos coordenados.
* **Waterwing (`waterwing`):** Criaturas aladas anfíbias capazes de mergulhar em aquíferos profundos e navegar correntes subterrâneas, coletando água e depósitos minerais intactos.
* **Vulnerabilidade Universal a PEM (`emp_rifle`):** Todas as entidades robóticas e androides (`Grazer`, `Fanghorn`, `Frostclaw`, `Glinthawk`, `Waterwing`, `CyberSteed` e ciborgues) são suscetíveis à interferência eletromagnética. Ao serem atingidas por pulsos do Rifle PEM, têm seus subsistemas paralisados por 10 segundos, desativando inteligência artificial, rotinas e módulos de propulsão.


---

### Sobrescrita Neural (Override Lance) & Montarias Horizon
Com a **Lanca de Sobrescrita Neural** (`neural_override_lance`), o astronauta pode invadir os subsistemas de inteligencia das maquinas ancestrais:
* **Hackeamento Tatico:** Mire a lanca em qualquer androide Horizon (`Grazer`, `Fanghorn`, `Frostclaw`, `Glinthawk` ou `Waterwing`) e segure o clique direito. Um feixe de pulso eletrico e ressonancia sonica ligara o operador a maquina por 35 ticks (1.75s).
* **Sobrescrita Concluida:** Ao finalizar a sobreposicao, a maquina torna-se aliada permanente do jogador, defendendo-o de hostis, cessando agressividade e liberando comandos de montaria.
* **Montarias Terrestres, Aereas e Anfibias:**
  * **Grazer & Fanghorn:** Montarias terrestres velozes com bonus de tracao em areia e arenito.
  * **Glinthawk & Waterwing:** Montarias aereas de alta dirigibilidade. O Waterwing e tambem 100% anfibio, mergulhando em corpos hidricos sem desalojar o condutor.
  * **Frostclaw:** Tanque de combate leal que congela inimigos ao redor com sua aura criogenica.

---

### Exoesqueleto Modular & Chips de Soquete
O Traje Espacial (`Space Suit`) pode ser customizado com ate 3 chips de aprimoramento cinetico e cibernetico em tempo real. Basta segurar o chip com o botao direito enquanto veste o peitoral:
1. **Chip de Sonar Sismico (`seismic_sonar_chip`):** Ao agachar ou explorar profundidades abaixo de Y=50, emite pulsos sonares que destacam entidades em um raio de 16 blocos com contorno brilhante.
2. **Modulo de Botas a Jato (`jetboot_module`):** Concede impulsao vertical ao saltar, reduzindo o consumo de energia em subidas ingremes.
3. **Chip Magnetico Gravitacional (`gravitational_magnet_chip`):** Atrai automaticamente minerios, itens soltos e orbes de experiencia em um raio esferico de 8 blocos para o inventario.
4. **Modulo Reciclador Osmotico (`osmotic_recycler_module`):** Recicla calor e umidade corporal interna, restaurando fome e saturacao automaticamente com consumo eletrico sustentavel.
5. **Chip Defletor Cinetico (`kinetic_deflector_chip`):** Amortece quedas severas e colisoes em alta velocidade, absorvendo o impacto cinetico antes de atingir o astronauta.

---

### Modo Ponder Holografico 3D no Survival Datapad
O **Survival Datapad** conta com um simulador 3D isometrico interativo integrado diretamente na interface da aba **VI. Multiblocos**:
* **Visualizacao Isometrica Passo a Passo:** Permite rotacionar e inspecionar a maquete holografica da estrutura multibloco camada por camada (Y-Level), identificando com precisao a orientacao e a paleta de blocos.
* **Inspecao de Voxel:** Passar o cursor sobre qualquer bloco do modelo 3D exibe uma tooltip flutuante com o nome tecnico e coordenadas relativas locais (X, Y, Z).
* **Controles Integrados:** Botoes `< Anterior` e `Proximo >` para avancar na sequencia de montagem, botao de projecao direta no mundo e botao de fechamento imediato.

---

### Chefe Monumental: Apex Sandworm Sovereign
O tita supremo do manto planetario, muito mais temivel e colossal que o verme comum das dunas:
* **Invocacao Sismica:** Deve ser invocado posicionando e ativando o **Catalisador de Ressonancia Sismica** (`seismic_resonance_catalyst`) estritamente abaixo de **Y=0** nas camadas profundas do manto de ardosia (Deepslate).
* **Combate em 3 Fases:**
  * **Fase 1 (100% a 66% HP):** Emersoes violentas com terremotos e ondas de choque sonicas.
  * **Fase 2 (66% a 33% HP):** Escudo de carapaca reforcada, velocidade duplicada e disparo de espinhos de quitina acida.
  * **Fase 3 (33% a 0% HP - Furia Tectonica):** Erupcao vulcanica continua, arremesso de blocos de magma e rugidos devastadores.
* **Recompensas Exclusivas:**
  * **Matriz do Coracao Apice (`apex_heart_matrix`):** Nucleo gravitacional para tecnologias ultradensas.
  * **Arpao Railgun Apice (`apex_railgun_harpoon`):** Armamento lendario de 32 pontos de dano instantaneo com feixe cinetico perfurante.

---

### Hidroponia & Culinaria Espacial (Hydroponics Delight)
Sobreviver no deserto toxico requer autonomia alimentar avancada:
* **Bandeja Hidroponica UV (`hydroponic_tray`):** Bloco estufa compacto com emissao de luz ultravioleta e nutricao osmotica por micro-canais. Acelera drasticamente qualquer semente ou cultivo plantado sobre ela.
* **Cultivo de Trigo Xeno (`xeno_wheat_crop` & `xeno_wheat_seeds`):** Variedade geneticamente modificada em 8 estagios fenotipicos resistente a seca severa.
* **Melao das Dunas & Bagas Criogenicas:** Frutas espaciais nutritivas colhidas para consumo in natura ou processamento.
* **Refeicoes Balanceadas com Buffs Termicos:**
  * **Pasta Nutritiva Termica (`thermal_nutrient_paste`):** Previne hipotermia nas noites deserticas.
  * **Suco Eletrolitico Xeno (`electrolyte_juice`):** Hidratacao instantanea que retarda o esgotamento do traje.
  * **Racao Glaciada Criogenica (`cryo_glazed_ration`):** Alimento denso que arrefece a temperatura interna em tempestades de calor.
  * **Banquete Hidroponico (`hydroponic_feast`):** O prato definitivo do explorador interplanetario, concedendo maxima saturacao e regeneracao continua.
