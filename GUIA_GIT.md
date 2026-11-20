# Guia rápido de Git e GitHub

> Objetivo: manter o projeto organizado sem adicionar burocracia. Use o que fizer sentido para a equipe; campos e referências a Issues são opcionais.

## 1. Combinados simples

- Faça uma branch para cada tarefa ou alteração relacionada.
- Evite desenvolver diretamente na `main`.
- Mantenha a `main` funcionando sempre que possível.
- Faça commits pequenos, com mensagens que expliquem a mudança.
- Abra uma Pull Request (PR) para revisar e integrar alterações.
- Teste o que mudou antes de pedir revisão.
- Não inclua senhas, tokens, chaves de API ou arquivos com dados pessoais no repositório.
- Não misture mudanças sem relação na mesma PR.

## 2. Nomes de branches

Use letras minúsculas, hífens e um nome curto que explique a tarefa.

| Tipo | Quando usar | Exemplo |
|---|---|---|
| `feat/` | Nova funcionalidade | `feat/cadastro-usuario` |
| `fix/` | Correção de erro | `fix/validacao-email` |
| `docs/` | Documentação | `docs/guia-instalacao` |
| `refactor/` | Reorganização interna sem mudar o comportamento esperado | `refactor/servico-usuario` |
| `test/` | Criação ou ajuste de testes | `test/login` |
| `chore/` | Manutenção e configuração | `chore/atualiza-dependencias` |

Se a equipe usa Issues, pode incluir o número: `feat/12-cadastro-usuario`. O número é opcional.

## 3. Mensagens de commit

Uma convenção simples é usar o padrão **Conventional Commits**:

`tipo: descrição curta no presente`

| Tipo | Uso | Exemplo |
|---|---|---|
| `feat` | Nova funcionalidade | `feat: adiciona cadastro de usuário` |
| `fix` | Correção de bug | `fix: corrige validação do e-mail` |
| `docs` | Documentação | `docs: atualiza guia de instalação` |
| `style` | Formatação sem alterar a lógica | `style: ajusta indentação` |
| `refactor` | Melhoria interna sem mudar comportamento | `refactor: simplifica serviço de usuário` |
| `test` | Testes | `test: adiciona testes de login` |
| `chore` | Tarefas de manutenção | `chore: atualiza dependências` |

Boas práticas:
- Descreva uma mudança principal por commit.
- Prefira mensagens específicas a `ajustes`, `teste`, `final` ou `mudanças`.
- Escreva de forma curta e clara.
- O escopo é opcional. Exemplo: `feat(auth): adiciona recuperação de senha`.

## 4. Fluxo recomendado

Neste projeto, considere `develop` como a branch de integração do trabalho da equipe e `main` como a branch principal/estável. As novas funcionalidades e correções devem, em geral, sair de `develop` e abrir PR com destino a `develop`. Se o repositório não usar esse fluxo, adapte os nomes das branches.

### A. Atualizar a branch principal

```bash
git switch develop
git pull origin develop
```

Atualiza sua cópia local de `develop` com as mudanças do repositório remoto. Faça isso antes de criar uma branch para uma nova tarefa.

### B. Criar uma branch para a tarefa

```bash
git switch -c feat/nome-da-tarefa
```

Cria a branch e muda para ela. Escolha um nome relacionado à alteração.

### C. Conferir e selecionar as alterações

```bash
git status
git diff
git add caminho/do/arquivo
```

- `git status`: mostra os arquivos alterados e o estado do Git.
- `git diff`: mostra as mudanças ainda não preparadas para commit.
- `git add caminho/do/arquivo`: prepara um arquivo específico para o próximo commit.

Para preparar todas as alterações atuais, pode usar `git add .`, mas confira antes o `git status` para não incluir arquivos indevidos.

### D. Criar o commit

```bash
git commit -m "feat: descreve a mudança"
```

Registra as alterações preparadas com uma mensagem clara.

### E. Enviar a branch ao GitHub

```bash
git push -u origin feat/nome-da-tarefa
```

Publica a branch remota. O `-u` configura o acompanhamento para facilitar os próximos `git push` e `git pull` nessa branch.

### F. Abrir a Pull Request

No GitHub, abra uma PR da sua branch para `develop` e preencha o modelo disponível em `.github/PULL_REQUEST_TEMPLATE.md`. Revise o resumo, os testes e os arquivos alterados antes de solicitar revisão.

### G. Depois do merge

```bash
git switch develop
git pull origin develop
git branch -d feat/nome-da-tarefa
```

Atualiza a `develop` local e apaga a branch local já integrada. Só apague branches que não tenham trabalho pendente. A exclusão local não apaga a branch remota.

## 5. Outros comandos úteis

| Comando | O que faz |
|---|---|
| `git branch` | Lista branches locais. |
| `git switch nome-da-branch` | Troca para uma branch existente. |
| `git branch -a` | Lista branches locais e remotas conhecidas. |
| `git log --oneline -10` | Mostra os últimos 10 commits de forma resumida. |
| `git diff --staged` | Mostra o que está preparado para o próximo commit. |
| `git fetch origin` | Busca informações novas do remoto sem integrar mudanças na branch atual. |
| `git switch --track origin/nome-da-branch` | Cria uma branch local acompanhando a branch remota e muda para ela. |
| `git pull origin main` | Busca e integra as atualizações remotas de `main` na branch atual. Use estando na branch que deseja atualizar. |
| `git restore caminho/do/arquivo` | Descarta alterações locais não preparadas naquele arquivo. **Use com cuidado: pode perder trabalho.** |
| `git restore --staged caminho/do/arquivo` | Remove o arquivo da área de stage, mas mantém as alterações no disco. |
| `git stash` | Guarda temporariamente alterações locais para você trocar de contexto. |
| `git stash pop` | Tenta reaplicar as alterações guardadas mais recentemente. |
| `git show --stat` | Mostra o resumo do commit mais recente. |

### Se houver mudanças novas em `develop` enquanto você trabalha

Uma opção simples é atualizar sua branch com merge:

```bash
git fetch origin
git switch feat/nome-da-tarefa
git merge origin/develop
```

Se houver conflitos, resolva os arquivos indicados, teste o projeto e finalize o merge conforme o Git solicitar. Não execute comandos de reset ou limpeza sem entender o efeito.


## 6. Baixar uma branch remota e revisar uma PR

Quando alguém criar uma branch no GitHub (por exemplo, `develop` ou `feat/cadastro-empresa`), ela ainda pode não existir no seu computador. Primeiro, atualize a lista de branches remotas:

```bash
git fetch origin
git branch -r
```

O comando `git branch -r` deve mostrar branches como `origin/develop` e `origin/feat/cadastro-empresa`.

### A. Trazer a `develop` para o projeto local pela primeira vez

Se ainda não existir uma `develop` local:

```bash
git switch --track origin/develop
```

Isso cria a branch local `develop`, configura o acompanhamento de `origin/develop` e já muda para ela. Depois, confira:

```bash
git branch -vv
git status
```

Se a branch `develop` já existir localmente, apenas mude para ela e atualize:

```bash
git switch develop
git pull origin develop
```

Antes de trocar de branch, confira `git status` e faça commit ou guarde (`git stash`) alterações locais que não queira levar para outra branch.

### B. Baixar uma branch remota para fazer review

Para revisar uma PR de outra pessoa, primeiro atualize as referências remotas e crie uma branch local acompanhando a branch da PR. Exemplo:

```bash
git fetch origin
git switch --track origin/feat/cadastro-empresa
```

Substitua `feat/cadastro-empresa` pelo nome exato da branch que aparece na PR do GitHub. Se a branch local já existir, use `git switch feat/cadastro-empresa` e depois `git pull`.

### C. Conferir as alterações da PR

Com a branch da PR selecionada, confira os arquivos e diferenças em relação à `develop`:

```bash
git status
git log --oneline origin/develop..HEAD
git diff origin/develop...HEAD
```

- `git log`: lista os commits da branch que não estão em `develop`.
- `git diff`: mostra as alterações da PR em relação à base `develop`.
- Abra o projeto, execute a aplicação e rode os testes pertinentes para validar o que foi alterado.

A revisão no Git local serve para inspecionar e testar o código. Comentários, aprovação e merge da PR continuam sendo feitos no GitHub, conforme o processo da equipe.

### D. Depois do review

Quando terminar, volte para `develop` e atualize:

```bash
git switch develop
git pull origin develop
```

Se não precisar mais da cópia local da branch revisada, você pode apagá-la:

```bash
git branch -d feat/cadastro-empresa
```

Esse comando apaga somente a branch local. Não apaga a branch da outra pessoa no GitHub. Se o Git impedir a exclusão, não force com `-D` sem confirmar que não há trabalho local que precise ser preservado.

## 7. Antes de abrir uma PR

- [ ] A branch tem um nome claro.
- [ ] A PR trata de uma tarefa principal.
- [ ] Os arquivos alterados foram conferidos.
- [ ] Testei o que foi alterado ou expliquei o que não consegui testar.
- [ ] Não incluí senhas, tokens ou arquivos locais indevidos.
- [ ] A descrição explica o problema e a solução.
- [ ] Relacionei uma Issue, se existir e for relevante.

## 8. Estrutura sugerida

```text
projeto/
├── .github/
│   └── PULL_REQUEST_TEMPLATE.md
├── src/                     # exemplo: código-fonte
├── README.md                # apresentação e instruções do projeto
└── GUIA_GIT.md              # este guia
```

A pasta `src/` é apenas um exemplo: mantenha a estrutura real do seu projeto.

## 9. Regra de ouro

**Uma tarefa por branch, commits claros e uma PR fácil de entender.** Para um projeto pequeno, isso já ajuda bastante. Ajustem estas convenções se a equipe tiver um padrão próprio.
