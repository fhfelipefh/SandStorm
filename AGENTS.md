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

## 🧹 Higiene de Arquivos & Scripts Geradores
1. Scripts temporários (ex: scripts Python de geração procedural de texturas, conversores descartáveis) devem ser colocados no diretório scratch do agente ou excluídos imediatamente após a geração dos arquivos finais.
2. Não deixe pastas ou arquivos temporários não rastreados no workspace (`scratch/`, `.tmp`, etc.). Mantenha o `git status` sempre limpo.

## 🚀 Validação Obrigatória Antes de Envio Remoto
Antes de concluir qualquer tarefa de desenvolvimento ou efetuar `git push` ao repositório remoto:
1. Executar `./gradlew test` e garantir que todos os testes (incluindo testes de arquitetura) passem com 100% de sucesso.
2. Executar `./gradlew build` para validar empacotamento, compilação client/server e geração de recursos.
3. Verificar com `git status` que a árvore de trabalho está limpa antes do envio.
