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
