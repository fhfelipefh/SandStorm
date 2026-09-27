# SandStorm 🏜️

[![CurseForge](https://cf.way2muchnoise.eu/sandstorm.svg)](https://www.curseforge.com/minecraft/mc-mods/sandstorm)

**SandStorm** é um mod de sobrevivência planetária, automação industrial e terraformação para Minecraft 1.21.4 (Fabric). 

Você é um astronauta perdido em um planeta deserto infestado de gigantescos Vermes de Areia. Sua missão é sobreviver, construir maquinários avançados, extrair água subterrânea e, eventualmente, terraformar as dunas em oásis habitáveis.

---

## ⚙️ Principais Mecânicas

* **Traje de Sobrevivência:** Regula a temperatura e recarrega com luz solar. Camas são inúteis aqui; a sobrevivência é 24 horas.
* **Vermes de Areia:** Cuidado com vibrações ao correr ou minerar. Use Thumpers para distraí-los. *Eles não destroem máquinas, apenas atacam você.*
* **Indústria e Energia:** Redes de energia sem fio (WPT), dessalinização de água, impressão 3D de componentes e fabricação de robôs.
* **Defesa de Base:** Escudos de plasma e canhões cinéticos para proteção pesada.
* **Clonagem Quântica:** Substitui o respawn tradicional. Você usa clones para "ego-casting" seguro.
* **Terraformação (Endgame):** Construa Processadores Atmosféricos para criar áreas verdes, água doce e afastar os vermes.

> 📚 **Quer saber mais?** Temos diagramas de engenharia, listas de máquinas e tutorais completos na [Wiki do SandStorm](https://github.com/fhfelipefh/SandStorm/wiki).

---

## 🛠️ Instalação (Jogadores)

> **Dica:** Se você usa o aplicativo do CurseForge, basta acessar [a página oficial do SandStorm](https://www.curseforge.com/minecraft/mc-mods/sandstorm) e clicar em "Install" para que ele baixe as dependências automaticamente.

Caso prefira a instalação manual:
1. Instale o **Java 25**.
2. No Minecraft Launcher, inicie a versão `1.21.4` pelo menos uma vez.
3. Instale o [Fabric Loader 0.19.5+](https://fabricmc.net/use/installer/).
4. Baixe a [Fabric API](https://modrinth.com/mod/fabric-api) e o [SandStorm](https://www.curseforge.com/minecraft/mc-mods/sandstorm).
5. Coloque ambos na sua pasta `mods/` e jogue.

---

## 💻 Desenvolvimento (Compilar e Testar)

O projeto requer **Java 25** e usa Gradle.

```bash
git clone https://github.com/fhfelipefh/SandStorm.git
cd SandStorm

# Para rodar o jogo direto:
.\gradlew runClient

# Para buildar o mod (.jar):
.\gradlew build
```

*Desenvolvido por [@fhfelipefh](https://github.com/fhfelipefh)*
