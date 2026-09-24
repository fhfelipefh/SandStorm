# SandStorm - Diretrizes de Desenvolvimento & Regras do Repositório

## 🏛️ Invariantes de Arquitetura de Código Java
Ao criar ou editar qualquer arquivo `.java` em `src/main/java`, `src/client/java` ou `src/test/java`:

1. **Zero Comentários**:
   - É terminantemente proibido inserir qualquer tipo de comentário (`//`, `/*`, `*`) no código-fonte Java. O código deve ser 100% autodocumentado. Violação quebra `ZeroCommentsArchitectureTest`.
2. **Zero Imports Inline**:
   - Nunca use referências de pacote inline no meio do código (ex: `net.minecraft.world.level.block.Blocks.SAND` ou `com.fhfelipefh.sandstorm...`).
   - Toda classe externa deve ser declarada como import explícito no topo do arquivo (`import net.minecraft...;`). Violação quebra `NoInlineImportsArchitectureTest`.
3. **Zero Imports Não Utilizados**:
   - Todo import declarado no topo do arquivo deve ser efetivamente usado no corpo da classe. Violação quebra `NoUnusedImportsArchitectureTest`.
4. **Prevenção de Deadlocks no Servidor**:
   - **Geração de Mundo (Chunks)**: Nunca execute `level.setBlock()` (ou gere estruturas) diretamente dentro de manipuladores do evento `ServerChunkEvents.CHUNK_LOAD` via `level.getServer().execute()`. Isso causa *deadlock* mútuo entre o *Server Thread* e o *Chunk Worker Thread*. A forma correta é adicionar os alvos a uma fila assíncrona (`Queue`) e processá-los via `ServerTickEvents.END_SERVER_TICK`, condicionando a execução ao `level.isLoaded(pos)`.
   - **Salvamento (I/O)**: É proibido chamar `saveAndJoin()` na thread do servidor. As classes devem invocar `setDirty()` e delegar a persistência ao sistema assíncrono vanilla.
   - Violações quebram `ServerDeadlockPreventionArchitectureTest`.

## 🧹 Higiene de Arquivos & Scripts Geradores
1. Scripts temporários (ex: scripts Python de geração procedural de texturas, conversores descartáveis) devem ser colocados no diretório scratch do agente ou excluídos imediatamente após a geração dos arquivos finais.
2. Não deixe pastas ou arquivos temporários não rastreados no workspace (`scratch/`, `.tmp`, etc.). Mantenha o `git status` sempre limpo.

## 🎨 Protocolo de Integridade de Texturas e Assets (Minecraft 1.21.4+)
Ao criar, renomear ou registrar qualquer novo item ou bloco no SandStorm:

1. **Cadeia Completa Obrigatória de Itens**:
   - Todo item registrado em `SandStormItems` exige rigorosamente três arquivos:
     - `assets/sandstorm/items/<item>.json`: definição de modelo no padrão 1.21.4 (`{"model": {"type": "minecraft:model", "model": "sandstorm:item/<item>"}}`). Sem este arquivo, o item fica sem textura no livro de receitas!
     - `assets/sandstorm/models/item/<item>.json`: modelo base apontando para a camada de textura (`layer0`).
     - `assets/sandstorm/textures/item/<item>.png`: textura válida.

2. **Cadeia Completa Obrigatória de Blocos**:
   - Todo bloco registrado em `SandStormBlocks` exige:
     - `assets/sandstorm/blockstates/<block>.json`: mapeamento de variantes.
     - `assets/sandstorm/models/block/<block>.json`: modelo do bloco referenciando texturas existentes.
     - `assets/sandstorm/items/<block>.json`: definição de renderização do item do bloco no inventário.
     - Texturas PNG válidas em `assets/sandstorm/textures/block/`.

3. **Validação de Assinatura e Integridade dos PNGs**:
   - É terminantemente proibido criar arquivos PNG vazios (0 bytes) ou com cabeçalhos corrompidos.
   - Todo arquivo PNG gerado processualmente deve conter a assinatura padrão `89 50 4E 47 0D 0A 1A 0A` e resolução compatível (16x16 ou 32x32).
   - Scripts descartáveis para gerar texturas devem rodar e ser limpos imediatamente, deixando a textura final íntegra.

4. **Auditoria Automatizada via Testes (`AssetIntegrityTest`)**:
   - Qualquer novo item ou bloco deve ser coberto pelo `AssetIntegrityTest` e `JeiCompatibilityTest`.
   - Antes de dar a tarefa como concluída, `./gradlew test` deve validar a existência física de todos os arquivos de textura e JSONs referenciados.

## 🚀 Validação Obrigatória Antes de Envio Remoto
Antes de concluir qualquer tarefa de desenvolvimento ou efetuar `git push` ao repositório remoto:
1. Executar `./gradlew test` e garantir que todos os testes (incluindo testes de arquitetura) passem com 100% de sucesso.
2. Executar `./gradlew build` para validar empacotamento, compilação client/server e geração de recursos.
3. Verificar com `git status` que a árvore de trabalho está limpa antes do envio.
